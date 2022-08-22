package me.mrgazdag.hibiscus.library.users;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import jakarta.websocket.CloseReason;
import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.Util;
import me.mrgazdag.hibiscus.library.event.DefaultEventNode;
import me.mrgazdag.hibiscus.library.event.DelegateEventNode;
import me.mrgazdag.hibiscus.library.event.EventNode;
import me.mrgazdag.hibiscus.library.event.device.DeviceEvent;
import me.mrgazdag.hibiscus.library.event.device.DeviceLoginEvent;
import me.mrgazdag.hibiscus.library.event.device.UserLoginEvent;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientHelloPacket;
import me.mrgazdag.hibiscus.library.users.networking.handler.DefaultPacketHandler;
import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerChangePagePacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerIdentityRequestPacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerKickPacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerWelcomePacket;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ConnectedDevice implements DelegateEventNode<DeviceEvent> {
    private final LibraryServer library;
    private final WebSocketChannel socket;
    private final UUID deviceId;

    private String loginProfileId;
    private ConnectedUser user;
    private DeviceState state;
    private PacketHandler packetHandler;
    private byte userFlags;
    private PageContext currentPageContext;

    private transient String identityServerKey;

    private final EventNode<DeviceEvent> eventNode;

    public ConnectedDevice(LibraryServer library, WebSocketChannel socket, UUID deviceId) {
        this.library = library;
        this.socket = socket;
        this.deviceId = deviceId;

        this.packetHandler = new DefaultPacketHandler(this);
        this.state = DeviceState.NEW;
        this.currentPageContext = null;
        this.eventNode = new DefaultEventNode<>(library);
    }

    public LibraryServer getLibraryServer() {
        return library;
    }

    public WebSocketChannel getWebSocket() {
        return socket;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public DeviceState getState() {
        return state;
    }

    /**
     * Internal method.
     *
     * Used to set the initial data on this client,
     * from the specified Hello packet.
     * @param packet the hello packet to use data from
     */
    public void _preLogin(ClientHelloPacket packet) {
        // Disallow calling this after it has already been set
        if (state != DeviceState.NEW) return;

        this.userFlags = packet.getDeviceFlags();
        this.loginProfileId = packet.getProfileId();

        if (isAttemptingToLogin()) {
            state = DeviceState.LOGGING_IN;
        } else {
            state = DeviceState.POLLING;
        }
    }

    public void _loginSuccessful(ConnectedUser user, boolean newUser) {
        // Disallow calling this after user has already logged in
        if (state != DeviceState.LOGGING_IN && state != DeviceState.DISCONNECTED) return;

        this.user = user;
        user._addDevice(this);

        if (newUser) {
            UserLoginEvent loginEvent = new UserLoginEvent(this);
            library.getEventManager().callEvent(loginEvent);
            if (loginEvent.getResult() == UserLoginEvent.Result.KICKED) {
                kick(loginEvent.getKickMessage());
                return;
            }
        }
        DeviceLoginEvent loginEvent = new DeviceLoginEvent(this);
        library.getEventManager().callEvent(loginEvent);
        if (loginEvent.getResult() == DeviceLoginEvent.Result.KICKED) {
            kick(loginEvent.getKickMessage());
            return;
        }

        System.out.println("[LibaryServer] " + user.getDisplayName() + " (" + user.getUserId() + ") has logged in from " + socket.getSourceAddress().getHostString() + ":" + socket.getSourceAddress().getPort());
        sendPacket(new ServerWelcomePacket(user.getUserId(), user.getNickname()));

        // Send UI data
        sendPacket(library.getRegistry().getInitialUIPacket(this));

        // Login completed
        state = DeviceState.LOGGED_IN;
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public ConnectedUser getUser() {
        return user;
    }

    public String getLoginProfileId() {
        return loginProfileId;
    }

    public boolean isAttemptingToLogin() {
        return (userFlags & ClientHelloPacket.IS_LOGIN_BIT) > 0;
    }

    public boolean isMobileDevice() {
        return (userFlags & ClientHelloPacket.IS_MOBILE_BIT) > 0;
    }

    public boolean isWebApp() {
        return (userFlags & ClientHelloPacket.IS_WEB_VERSION_BIT) > 0;
    }

    public boolean isGuestAccount() {
        return (userFlags & ClientHelloPacket.IS_GUEST_BIT) > 0;
    }

    public void sendPacket(ServerPacket packet) {
        packet = packetHandler.onPacketSend(packet);
        if (packet != null) {
            WebSockets.sendBinary(packet.compress(), socket, null);
        }
    }

    public void kick(String reason) {
        // Disallow calling this after the user has disconnected
        if (state == DeviceState.DISCONNECTED) return;
        sendPacket(new ServerKickPacket(reason));
        socket.setCloseCode(CloseReason.CloseCodes.NORMAL_CLOSURE.getCode());
        socket.setCloseReason(reason);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user != null) {
            System.out.println("[LibaryServer] " + user.getDisplayName() + " (" + user.getUserId() + ") was kicked: " + reason);
            user._removeDevice(this);
        }
        state = DeviceState.DISCONNECTED;
        if (currentPageContext != null) {
            currentPageContext.page().removeDevice(this);
            currentPageContext = null;
        }
    }

    public void _disconnect(boolean abrupt) {
        if (user != null) {
            System.out.println("[LibaryServer] " + user.getDisplayName() + " (" + user.getUserId() + ") has disconnected" + (abrupt ? " abruptly" : ""));
            user._removeDevice(this);
        }
        state = DeviceState.DISCONNECTED;
        if (currentPageContext != null) {
            currentPageContext.page().removeDevice(this);
            currentPageContext = null;
        }
    }

    public String getIdentityServerKey() {
        return identityServerKey;
    }

    public void requestIdentityRefresh() {
        // Generate Server Key (used for authentication)
        this.identityServerKey = Util.generateRandomString(64, Util.azAZ09);
        sendPacket(new ServerIdentityRequestPacket(identityServerKey));
    }

    public PageContext getCurrentPage() {
        return currentPageContext;
    }

    public void setCurrentPage(PageContext page) {
        if (Objects.equals(currentPageContext, page)) {
            // Page change request should be cancelled
            cancelPageRequest();
            return;
        }

        if (currentPageContext != null) {
            currentPageContext.page().removeDevice(this);
        }

        if (page == null) {
            page = library.getUIManager().getDefaultPage(this).getContext();
        }
        currentPageContext = page;
        sendPacket(new ServerChangePagePacket(page));
        currentPageContext.page().addDevice(this);
    }

    public void goToDefaultPage() {
        setCurrentPage(null);
    }

    public void cancelPageRequest() {
        sendPacket(new ServerChangePagePacket(currentPageContext));
    }

    @Override
    public EventNode<DeviceEvent> getEventNode() {
        return eventNode;
    }

    @Override
    public String toString() {
        return "ConnectedDevice{" +
                "deviceId=" + deviceId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                '}';
    }
}

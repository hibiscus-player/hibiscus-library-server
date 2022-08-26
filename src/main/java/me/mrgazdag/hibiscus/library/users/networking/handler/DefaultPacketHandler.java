package me.mrgazdag.hibiscus.library.users.networking.handler;

import jakarta.websocket.CloseReason;
import me.mrgazdag.hibiscus.library.coreapi.ProfileData;
import me.mrgazdag.hibiscus.library.event.device.DeviceConnectEvent;
import me.mrgazdag.hibiscus.library.event.device.DeviceSwitchPageEvent;
import me.mrgazdag.hibiscus.library.ui.action.client.ClientPageAction;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.ConnectedUser;
import me.mrgazdag.hibiscus.library.users.DeviceState;
import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.client.*;
import me.mrgazdag.hibiscus.library.users.networking.protocol.DefaultProtocol;
import me.mrgazdag.hibiscus.library.users.networking.protocol.Protocol;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerHelloPacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPongPacket;

import java.io.IOException;

public class DefaultPacketHandler extends PacketHandler {
    private int packetErrorCounter;
    private int unknownPacketIdCounter;
    public DefaultPacketHandler(ConnectedDevice device) {
        super(device);
    }

    @Override
    protected Protocol getProtocol() {
        return DefaultProtocol.INSTANCE;
    }

    @Override
    public void onJoin() {
        // Wait for Client Hello packet
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onDisconnect(int code, String reason, boolean remote) {
        if (remote) {
            device._disconnect(code == CloseReason.CloseCodes.CLOSED_ABNORMALLY.getCode());
        }
    }

    @Override
    protected void unexpectedPacket(ClientPacket packet) {
        //TODO make this configurable

        // Default to strict impl
        device.kick("Unexpected packet");
    }

    @Override
    protected void unknownPacket(int packetId) {
        System.err.println("WARNING! Unknown packet received from device " + device.getDeviceId() + ": " + packetId);
        unknownPacketIdCounter++;
        if (unknownPacketIdCounter > 50) {
            device.kick("Too many packet errors! Please update your client.");
        }
    }

    @Override
    protected void invalidPacket(String message) {
        System.err.println("WARNING! Packet error on device " + device.getDeviceId() + ": " + message);
        packetErrorCounter++;
        if (packetErrorCounter > 50) {
            device.kick("Too many packet errors! Please update your client.");
        }
    }

    @Override
    public void onClientHello(ClientHelloPacket packet) {
        device._preLogin(packet);

        //TODO Make these strings configurable
        String serverName = "Example Server";
        String serverMotd = "Example MOTD";
        DeviceConnectEvent connectEvent = new DeviceConnectEvent(device, serverName, serverMotd);
        device.getLibraryServer().getEventManager().callEvent(connectEvent);
        device.sendPacket(new ServerHelloPacket(connectEvent.getServerName(), connectEvent.getServerMotd()));

        if (device.isAttemptingToLogin()) {
            if (device.isGuestAccount()) {
                if (!true /*TODO are guest accounts allowed to log in*/) {
                    device.kick("Guest accounts are not allowed.");
                } else {
                    // Guest accounts have no possibly already connected user objects
                    // We create a new one for them here:
                    ConnectedUser user = device.getLibraryServer().getWebsocketServer().createGuestUser();
                    device._loginSuccessful(user, true);
                }
            } else {
                // Non-guest accounts need an identity check
                device.requestIdentityRefresh();
            }
        }
    }

    @Override
    public void onClientIdentityComplete(ClientIdentityCompletePacket packet) {
        String serverKey = device.getIdentityServerKey();
        if (serverKey == null) {
            // Identity check was not requested
            unexpectedPacket(packet);
            return;
        }
        String profileId = device.getLoginProfileId();

        // Authenticate with the core server
        ProfileData info = null;
        try {
            info = device.getLibraryServer().getCoreApi().getProfile(profileId, serverKey);
        } catch (IOException ignored) {}

        if (info == null) {
            device.kick("Failed to authenticate, bad profile ID");
            return;
        }

        ConnectedUser user = device.getLibraryServer().getWebsocketServer().getUserFromUserID(profileId);
        boolean isNewUser = user == null;
        if (isNewUser) {
            user = device.getLibraryServer().getWebsocketServer().loadUser(profileId);

        }

        // Update values
        user.setDisplayName(info.displayName());
        user.setPhotoUrl(info.photoUrl());

        // If this is the first identification (= player is
        // not yet logged in) then let the user join freely
        if (device.getState() == DeviceState.LOGGING_IN) {
            device._loginSuccessful(user, isNewUser);
        }
    }

    @Override
    public void onClientPing(ClientPingPacket packet) {
        device.sendPacket(new ServerPongPacket(packet.getData()));
    }

    @Override
    public void onClientChangePage(ClientChangePagePacket packet) {
        String pageId = packet.getPageId();
        PageContext page;
        if (pageId == null) {
            page = device.getLibraryServer().getUIManager().getDefaultPage(device).getContext();
        } else {
            page = device.getLibraryServer().getUIManager().getPageContext(pageId);
            if (page == null || !page.page().isVisible(device)) {
                // No page is found for this device
                if (device.getCurrentPage() == null) {
                    // If the device has no selected page yet,
                    // then use the default page
                    page = device.getLibraryServer().getUIManager().getDefaultPage(device).getContext();
                } else {
                    // Otherwise, the request should get canceled
                    device.cancelPageRequest();
                    return;
                }
            }
        }


        DeviceSwitchPageEvent event = new DeviceSwitchPageEvent(device, device.getCurrentPage(), page);
        device.getLibraryServer().getEventManager().callEvent(event);

        device.setCurrentPage(event.getNewPage());
    }

    @Override
    public void onClientPageAction(ClientPageActionPacket packet) {
        if (device.getCurrentPage() == null) {
            unexpectedPacket(packet);
            return;
        }

        Page page = device.getCurrentPage().page();
        UIComponent component = page.getComponent(packet.getComponentId());
        if (component == null) {
            // Handle wrong component id
            invalidPacket("invalid component id " + packet.getComponentId() + " on page " + device.getCurrentPage().sourcePageId());
            return;
        }
        ClientPageAction<?> action = component.getClientPageAction(packet.getActionId());
        if (action == null) {
            // Handle wrong action id
            invalidPacket("invalid action id " + packet.getActionId() + " on component " + component.getComponentId());
            return;
        }

        action.handle(device, packet.getData());
    }
}

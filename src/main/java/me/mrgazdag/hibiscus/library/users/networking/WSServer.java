package me.mrgazdag.hibiscus.library.users.networking;

import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.ConnectedUser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class WSServer extends AbstractReceiveListener {
    private static final String DEVICE_KEY = "device";
    private final LibraryServer library;
    private final Map<String,ConnectedUser> userIDMap;
    private final Map<String,ConnectedUser> profileMap;
    private final Map<UUID,ConnectedDevice> deviceMap;

    public WSServer(LibraryServer library) {
        this.library = library;
        this.userIDMap = new LinkedHashMap<>();
        this.profileMap = new HashMap<>();
        this.deviceMap = new HashMap<>();
    }

    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        ConnectedDevice device = createDevice(channel);
        channel.setAttribute(DEVICE_KEY, device);
        device.getPacketHandler().onJoin();
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        ConnectedDevice device = (ConnectedDevice) channel.getAttribute(DEVICE_KEY);
        device.getPacketHandler().onDisconnect(channel.getCloseCode(), channel.getCloseReason(), channel.isCloseInitiatedByRemotePeer());
        deviceMap.remove(device.getDeviceId());
        library.getEventManager().unregisterNode(device);

        // If this device was the last active device of this user, then remove the user
        if (device.getUser() != null && device.getUser().getDevices().size() == 0) {
            userIDMap.remove(device.getUser().getUserId());
            library.getEventManager().unregisterNode(device.getUser());
            if (!device.isGuestAccount()) {
                profileMap.remove(device.getUser().getUserId());
            }
        }
    }

    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
        ConnectedDevice device = (ConnectedDevice) channel.getAttribute(DEVICE_KEY);
        device.getPacketHandler().handle(WebSockets.mergeBuffers(message.getData().getResource()));
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        if (channel != null) {
            ConnectedDevice device = (ConnectedDevice) channel.getAttribute(DEVICE_KEY);
            device.getPacketHandler().onError(error);
        } else {
            error.printStackTrace();
        }
    }

    public ConnectedDevice createDevice(WebSocketChannel socket) {
        UUID deviceUUID;
        do {
            deviceUUID = UUID.randomUUID();
        } while (deviceMap.containsKey(deviceUUID));
        ConnectedDevice device = new ConnectedDevice(library, socket, deviceUUID);
        deviceMap.put(deviceUUID, device);
        return device;
    }

    public ConnectedUser createGuestUser() {
        String userId;
        do {
            userId = "guest_" + UUID.randomUUID();
        } while (userIDMap.containsKey(userId));
        ConnectedUser user = new ConnectedUser(library, userId, null, library.getPermissionManager().getUser(null));
        user.addGroup(library.getPermissionManager().getGuestGroup());
        user.setDisplayName("Guest");
        userIDMap.put(userId, user);
        return user;
    }

    public ConnectedUser loadUser(String profileId) {
        String userId = "profile_" + profileId;
        ConnectedUser user = new ConnectedUser(library, userId, profileId, library.getPermissionManager().getUser(profileId));
        userIDMap.put(userId, user);
        profileMap.put(profileId, user);
        return user;
    }

    public ConnectedUser getUserFromProfileID(String profileId) {
        return profileMap.get(profileId);
    }

    public ConnectedUser getUserFromUserID(String userId) {
        return userIDMap.get(userId);
    }

    public Collection<ConnectedUser> getConnectedUsers() {
        return userIDMap.values();
    }

    public Collection<ConnectedDevice> getConnectedDevices() {
        return deviceMap.values();
    }

    public void broadcast(ServerPacket packet) {
        broadcast(packet, getConnectedDevices());
    }
    public void broadcastSockets(ServerPacket packet, Collection<WebSocketChannel> sockets) {
        broadcast(packet, sockets.stream().map(channel->(ConnectedDevice) channel.getAttribute(DEVICE_KEY)).toList());
    }

    public void broadcast(ServerPacket packet, Collection<ConnectedDevice> sockets) {
        // Try to compress as few times as possible
        ByteBuffer buf = packet.compress();
        for (ConnectedDevice device : sockets) {
            ServerPacket result = device.getPacketHandler().onPacketSend(packet);
            // If the handler does not change the packet, then send the originally compressed buffer
            if (result == packet) WebSockets.sendBinary(buf, device.getWebSocket(), null);
            else if (result != null) WebSockets.sendBinary(result.compress(), device.getWebSocket(), null); //do not call the packet send method twice
        }
    }
}

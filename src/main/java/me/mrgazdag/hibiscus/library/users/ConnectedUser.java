package me.mrgazdag.hibiscus.library.users;

import io.undertow.websockets.core.WebSockets;
import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.event.DefaultEventNode;
import me.mrgazdag.hibiscus.library.event.DelegateEventNode;
import me.mrgazdag.hibiscus.library.event.EventNode;
import me.mrgazdag.hibiscus.library.event.user.UserEvent;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ConnectedUser implements DelegateEventNode<UserEvent> {
    //TODO permissions

    private final LibraryServer library;
    private final List<ConnectedDevice> devices;
    private final String userId;
    private final EventNode<UserEvent> eventNode;
    private String profileId;
    private String displayName;
    private String photoUrl;
    private String nickname;

    public ConnectedUser(LibraryServer library, String userId, String profileId) {
        this.library = library;
        this.devices = new ArrayList<>();
        this.userId = userId;
        this.eventNode = new DefaultEventNode<>(library);
        this.profileId = profileId;
        this.nickname = "";
    }

    public void _addDevice(ConnectedDevice device) {
        this.devices.add(device);
    }

    public void _removeDevice(ConnectedDevice device) {
        this.devices.remove(device);
    }

    public List<ConnectedDevice> getDevices() {
        return devices;
    }

    public LibraryServer getLibraryServer() {
        return library;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEffectiveName() {
        return nickname == null || nickname.isEmpty() ? displayName : nickname;
    }

    public boolean isGuest() {
        return profileId == null;
    }

    public boolean isConnected() {
        return devices.size() > 0;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void broadcastPacket(ServerPacket packet) {
        // Try to compress as few times as possible
        ByteBuffer buf = packet.compress();
        for (ConnectedDevice device : devices) {
            ServerPacket result = device.getPacketHandler().onPacketSend(packet);
            // If the handler does not change the packet, then send the originally compressed buffer
            if (result == packet) WebSockets.sendBinary(buf, device.getWebSocket(), null);
            else if (result != null) WebSockets.sendBinary(result.compress(), device.getWebSocket(), null); //do not call the packet send method twice
        }
    }

    public void kickAll(String reason) {
        for (ConnectedDevice device : new ArrayList<>(devices)) {
            device.kick(reason);
        }
    }

    @Override
    public EventNode<UserEvent> getEventNode() {
        return eventNode;
    }
}

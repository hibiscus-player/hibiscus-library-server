package me.mrgazdag.hibiscus.library.users.networking.handler;

import jakarta.websocket.CloseReason;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientChangePagePacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientHelloPacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientIdentityCompletePacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientPingPacket;
import me.mrgazdag.hibiscus.library.users.networking.protocol.Protocol;

import java.nio.ByteBuffer;

public abstract class PacketHandler {
    protected final ConnectedDevice device;

    public PacketHandler(ConnectedDevice device) {
        this.device = device;
    }

    public ConnectedDevice getDevice() {
        return device;
    }

    public void handle(ByteBuffer buffer) {
        byte id = buffer.get();
        ClientPacket packet = getProtocol().constructClientPacket(id, buffer);
        packet.handle(this);
    }

    protected void log(Object obj) {
        String id = device.getUser() == null ? null : device.getUser().getUserId();
        if (id == null) id = device.getWebSocket().getSourceAddress().getHostString() + ":" + device.getWebSocket().getSourceAddress().getPort();

        if (obj instanceof Throwable t) {
            System.err.print(id);
            System.err.print(" ");
            t.printStackTrace(System.err);
        } else if (obj instanceof ServerPacket sp) {
            System.out.println(id + " ← " + sp);
        } else if (obj instanceof ClientPacket sp) {
            System.out.println(id + " → " + sp);
        } else {
            System.out.println(id + " " + obj);
        }
    }

    protected abstract Protocol getProtocol();

    // State Updates
    public abstract void onJoin();
    public abstract void onError(Throwable e);
    public void onDisconnect(int code, String reason, boolean remote) {
        if (remote) {
            device._disconnect(code == CloseReason.CloseCodes.CLOSED_ABNORMALLY.getCode());
        }
    }

    // Packets
    public ServerPacket onPacketSend(ServerPacket packet) {
        return packet;
    }
    protected abstract void unexpectedPacket(ClientPacket packet);
    public void onClientHello(ClientHelloPacket packet) {
        unexpectedPacket(packet);
    }
    public void onClientIdentityComplete(ClientIdentityCompletePacket packet) {
        unexpectedPacket(packet);
    }
    public void onClientPing(ClientPingPacket packet) {
        unexpectedPacket(packet);
    }
    public void onClientChangePage(ClientChangePagePacket packet) {
        unexpectedPacket(packet);
    }
}

package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ServerIdentityRequestPacket extends ServerPacket {
    private final String serverKey;

    public ServerIdentityRequestPacket(String serverKey) {
        this.serverKey = serverKey;
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        buffer.put((byte) serverKey.length());
        buffer.put(serverKey.getBytes(StandardCharsets.UTF_16BE));
    }

    @Override
    protected int calculateLength() {
        return 1 // Server Key Length (byte)
                        + serverKey.getBytes(StandardCharsets.UTF_16BE).length; // Server Key Contents (utf 16 BE bytes)
    }

    @Override
    public String toString() {
        return "ServerIdentityRequestPacket{" +
                "serverKeyBytes=" + serverKey +
                '}';
    }
}

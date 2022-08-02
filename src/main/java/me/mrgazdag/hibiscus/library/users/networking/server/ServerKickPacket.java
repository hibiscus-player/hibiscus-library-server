package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ServerKickPacket extends ServerPacket {
    private final String kickReason;

    public ServerKickPacket(String kickReason) {
        this.kickReason = kickReason;
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        buffer.putShort((short) kickReason.length());
        buffer.put(kickReason.getBytes(StandardCharsets.UTF_16BE));
    }

    @Override
    protected int calculateLength() {
        return 2 // Kick Reason Length (short)
                + kickReason.getBytes(StandardCharsets.UTF_16BE).length; // Kick Reason Contents (utf 16 BE bytes)
    }

    @Override
    public String toString() {
        return "ServerKickPacket{" +
                "kickReasonBytes=" + kickReason +
                '}';
    }
}

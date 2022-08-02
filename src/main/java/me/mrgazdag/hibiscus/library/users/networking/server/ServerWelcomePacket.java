package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ServerWelcomePacket extends ServerPacket {
    private final String userId;
    private final String nickname;

    public ServerWelcomePacket(String userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        buffer.put((byte) userId.length());
        buffer.put(userId.getBytes(StandardCharsets.UTF_16BE));

        buffer.put((byte) nickname.length());
        buffer.put(nickname.getBytes(StandardCharsets.UTF_16BE));
    }

    @Override
    protected int calculateLength() {
        return 1 // User ID Length (byte)
                + userId.getBytes(StandardCharsets.UTF_16BE).length + // User ID Contents (utf 16 BE bytes)
                1 + // Nickname Length (byte)
                + nickname.getBytes(StandardCharsets.UTF_16BE).length; // Nickname Contents (utf 16 BE bytes)
    }

    @Override
    public String toString() {
        return "ServerWelcomePacket{" +
                "userIdData=" + userId +
                ", nicknameData=" + nickname +
                '}';
    }
}

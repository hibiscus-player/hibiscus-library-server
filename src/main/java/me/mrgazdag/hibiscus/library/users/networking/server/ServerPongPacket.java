package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ServerPongPacket extends ServerPacket {
    private final byte[] data;

    public ServerPongPacket(byte[] data) {
        this.data = data;
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        buffer.put(data);
    }

    @Override
    protected int calculateLength() {
        return 8; // ID (8 bytes)
    }

    @Override
    public String toString() {
        return "ServerPongPacket{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}

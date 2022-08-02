package me.mrgazdag.hibiscus.library.users.networking.client;

import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ClientPingPacket extends ClientPacket {
    private final byte[] data;
    public ClientPingPacket(ByteBuffer buffer) {
        super(buffer);
        data = new byte[8];
        buffer.get(data);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.onClientPing(this);
    }

    @Override
    public String toString() {
        return "ClientPingPacket{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}

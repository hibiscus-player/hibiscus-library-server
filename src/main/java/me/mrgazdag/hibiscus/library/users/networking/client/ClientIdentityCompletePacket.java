package me.mrgazdag.hibiscus.library.users.networking.client;

import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;

import java.nio.ByteBuffer;

public class ClientIdentityCompletePacket extends ClientPacket {
    public ClientIdentityCompletePacket(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.onClientIdentityComplete(this);
    }

    @Override
    public String toString() {
        return "ClientIdentityCompletePacket{}";
    }
}

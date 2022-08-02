package me.mrgazdag.hibiscus.library.users.networking;

import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;

import java.nio.ByteBuffer;

public abstract class ClientPacket {
    public ClientPacket(ByteBuffer buffer) {
    }
    public abstract void handle(PacketHandler handler);
}

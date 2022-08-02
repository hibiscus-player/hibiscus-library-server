package me.mrgazdag.hibiscus.library.users.networking.client;

import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientChangePagePacket extends ClientPacket {
    private final String pageId;
    public ClientChangePagePacket(ByteBuffer buffer) {
        super(buffer);
        int length = buffer.getShort() * 2;
        if (length > 0) {
            byte[] data = new byte[length];
            buffer.get(data, 0, length);
            pageId = new String(data, StandardCharsets.UTF_16BE); // Maybe eventually change this into some other, cheaper charset
        } else {
            pageId = null;
        }
    }

    public String getPageId() {
        return pageId;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.onClientChangePage(this);
    }

    @Override
    public String toString() {
        return "ClientChangePagePacket{" +
                "pageId='" + pageId + '\'' +
                '}';
    }
}

package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ServerChangePagePacket extends ServerPacket {
    private final String pageId;

    public ServerChangePagePacket(PageContext page) {
        this.pageId = page == null ? null : page.sourcePageId();
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        if (pageId == null) {
            buffer.putShort((short) 0);
        } else {
            buffer.putShort((short) pageId.length());
            buffer.put(pageId.getBytes(StandardCharsets.UTF_16BE));
        }
    }

    @Override
    protected int calculateLength() {
        return 2 // Page ID Length (short)
                + pageId.getBytes(StandardCharsets.UTF_16BE).length; // Page ID Contents (utf 16 BE bytes)
    }

    @Override
    public String toString() {
        return "ServerChangePagePacket{" +
                "pageId='" + pageId + '\'' +
                '}';
    }
}

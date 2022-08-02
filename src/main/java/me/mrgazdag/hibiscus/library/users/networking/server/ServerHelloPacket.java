package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ServerHelloPacket extends ServerPacket {
    private final String serverName;
    private final String serverMotd;

    public ServerHelloPacket(String serverName, String serverMotd) {
        this.serverName = serverName;
        this.serverMotd = serverMotd;
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        buffer.put((byte) serverName.length());
        buffer.put(serverName.getBytes(StandardCharsets.UTF_16BE));

        buffer.put((byte) serverMotd.length());
        buffer.put(serverMotd.getBytes(StandardCharsets.UTF_16BE));
    }

    @Override
    protected int calculateLength() {
        return 1 // Server Name Length (byte)
                + serverName.getBytes(StandardCharsets.UTF_16BE).length // Server Name Contents (utf 16 BE bytes)
                + 1 // Server MOTD Length (byte)
                + serverMotd.getBytes(StandardCharsets.UTF_16BE).length; // Server MOTD Contents (utf 16 BE bytes)
    }

    @Override
    public String toString() {
        return "ServerHelloPacket{" +
                "serverNameBytes=" + serverName +
                ", serverMotdBytes=" + serverMotd +
                '}';
    }
}

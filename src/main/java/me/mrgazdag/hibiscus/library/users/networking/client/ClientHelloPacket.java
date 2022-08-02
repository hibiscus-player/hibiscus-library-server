package me.mrgazdag.hibiscus.library.users.networking.client;

import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientHelloPacket extends ClientPacket {
    public static final byte IS_LOGIN_BIT = 0x01;
    public static final byte IS_MOBILE_BIT = 0x02;
    public static final byte IS_WEB_VERSION_BIT = 0x04;
    public static final byte IS_GUEST_BIT = 0x08;
    private final byte deviceFlags;
    private final String profileId;
    public ClientHelloPacket(ByteBuffer buffer) {
        super(buffer);

        deviceFlags = buffer.get();
        if ((deviceFlags & IS_GUEST_BIT) == 0) {
            // Actual account, read Profile ID
            int length = buffer.get()*2;
            byte[] data = new byte[length];
            buffer.get(data, 0, length);
            profileId = new String(data, StandardCharsets.UTF_16BE); // Maybe eventually change this into some other, cheaper charset
        } else {
            // Guest Account
            profileId = null;
        }
    }

    public String getProfileId() {
        return profileId;
    }

    public byte getDeviceFlags() {
        return deviceFlags;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.onClientHello(this);
    }

    @Override
    public String toString() {
        return "ClientHelloPacket{" +
                "deviceFlags=" + deviceFlags +
                ", profileId='" + profileId + '\'' +
                '}';
    }
}

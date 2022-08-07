package me.mrgazdag.hibiscus.library.users.networking.protocol;

import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Protocol {
    protected final Map<Byte, Function<ByteBuffer, ClientPacket>> clientMap;
    protected final Map<Class<? extends ServerPacket>, Byte> serverMap;

    public Protocol() {
        clientMap = new HashMap<>();
        serverMap = new HashMap<>();
    }
    protected final void register(Function<ByteBuffer, ClientPacket> create) {
        clientMap.put((byte) clientMap.size(), create);
    }
    protected final void register(byte id, Function<ByteBuffer, ClientPacket> create) {
        clientMap.put(id, create);
    }
    protected final void register(Class<? extends ServerPacket> clazz) {
        serverMap.put(clazz, (byte) serverMap.size());
    }
    protected final void register(Class<? extends ServerPacket> clazz, byte id) {
        serverMap.put(clazz, id);
    }

    public ClientPacket constructClientPacket(byte id, ByteBuffer buffer) {
        if (!clientMap.containsKey(id)) return null;
        return clientMap.get(id).apply(buffer);
    }
    public byte getServerPacketID(Class<? extends ServerPacket> packetClass) {
        return serverMap.get(packetClass);
    }
}

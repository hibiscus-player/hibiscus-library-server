package me.mrgazdag.hibiscus.library.users.networking;

import me.mrgazdag.hibiscus.library.users.networking.protocol.DefaultProtocol;

import java.nio.ByteBuffer;

public abstract class ServerPacket {
    public byte getID() {
        return DefaultProtocol.INSTANCE.getServerPacketID(getClass());
    }
    public ByteBuffer compress() {
        ByteBuffer bb = ByteBuffer.allocateDirect(getLength());
        bb.put(getID());
        compress(bb);
        bb.position(0); //fix not sending bytes
        return bb;
    }
    protected abstract void compress(ByteBuffer buffer);
    public int getLength() {
        return calculateLength() + 1; // Packet ID byte
    }
    protected abstract int calculateLength();
}

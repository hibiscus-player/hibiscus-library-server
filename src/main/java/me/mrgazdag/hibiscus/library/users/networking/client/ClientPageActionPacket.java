package me.mrgazdag.hibiscus.library.users.networking.client;

import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.handler.PacketHandler;

import java.nio.ByteBuffer;

public class ClientPageActionPacket extends ClientPacket {
    private final int componentId;
    private final short actionId;
    private final ByteBuffer data;

    public ClientPageActionPacket(ByteBuffer buffer) {
        super(buffer);

        this.componentId = buffer.getInt();
        this.actionId = buffer.getShort();
        int size = buffer.getInt();
        this.data = ByteBuffer.allocateDirect(size);
        this.data.put(0, buffer, buffer.position(), size);
    }

    public int getComponentId() {
        return componentId;
    }

    public short getActionId() {
        return actionId;
    }

    public ByteBuffer getData() {
        return data;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.onClientPageAction(this);
    }

    @Override
    public String toString() {
        return "ClientPageActionPacket{" +
                "componentId=" + componentId +
                ", actionId=" + actionId +
                '}';
    }
}

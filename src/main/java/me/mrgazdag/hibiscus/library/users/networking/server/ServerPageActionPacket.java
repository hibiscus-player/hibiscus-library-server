package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.ui.action.server.ServerPageAction;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;

public class ServerPageActionPacket<T> extends ServerPacket {
    private final ServerPageAction<T> action;
    private final T value;
    private final int size;

    public ServerPageActionPacket(ServerPageAction<T> action, T value) {
        this.action = action;
        this.value = value;
        this.size = action.size(value);
    }

    @Override
    protected void compress(ByteBuffer buffer) {
        buffer.putInt(action.getComponentId());
        buffer.putShort(action.getServerActionId());
        buffer.putInt(size);
        action.serialize(value, buffer);
    }

    @Override
    protected int calculateLength() {
        return 4 +  // Component ID
                2 + // Action ID
                4 + // Data Length
                size;
    }


}

package me.mrgazdag.hibiscus.library.ui.action.client;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

public abstract class ClientPageAction<T> {
    private final short clientActionId;
    private BiConsumer<ConnectedDevice, T> handler;

    public ClientPageAction(short clientActionId) {
        this.clientActionId = clientActionId;
    }

    public short getClientActionId() {
        return clientActionId;
    }

    protected abstract T deserialize(ByteBuffer buffer);
    public void handle(ConnectedDevice device, ByteBuffer buffer) {
        if (this.handler != null) this.handler.accept(device, deserialize(buffer));
    }
    public void handle(ConnectedDevice device, T value) {
        if (this.handler != null) this.handler.accept(device, value);
    }

    public void setHandler(BiConsumer<ConnectedDevice, T> handler) {
        this.handler = handler;
    }
}

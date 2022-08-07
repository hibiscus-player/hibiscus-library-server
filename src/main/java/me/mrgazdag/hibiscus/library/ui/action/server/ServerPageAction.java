package me.mrgazdag.hibiscus.library.ui.action.server;

import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageActionPacket;

import java.nio.ByteBuffer;

public abstract class ServerPageAction<T> {
    private final int componentId;
    private final short serverActionId;

    public ServerPageAction(UIComponent component, short serverActionId) {
        this.componentId = component.getComponentId();
        this.serverActionId = serverActionId;
    }

    public int getComponentId() {
        return componentId;
    }

    public short getServerActionId() {
        return serverActionId;
    }

    public abstract void serialize(T object, ByteBuffer buffer);
    public abstract int size(T object);
    public void send(ConnectedDevice device, T object) {
        device.sendPacket(new ServerPageActionPacket<>(this, object));
    }
}

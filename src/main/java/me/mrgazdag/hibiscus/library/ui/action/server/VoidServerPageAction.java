package me.mrgazdag.hibiscus.library.ui.action.server;

import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.nio.ByteBuffer;

public class VoidServerPageAction extends ServerPageAction<Void> {
    public VoidServerPageAction(UIComponent component, short serverActionId) {
        super(component, serverActionId);
    }

    @Override
    public void serialize(Void object, ByteBuffer buffer) {}

    @Override
    public int size(Void object) {
        return 0;
    }

    public void send(ConnectedDevice device) {
        send(device, null);
    }
}

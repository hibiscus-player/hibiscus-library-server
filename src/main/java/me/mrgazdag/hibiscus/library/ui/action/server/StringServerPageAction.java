package me.mrgazdag.hibiscus.library.ui.action.server;

import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringServerPageAction extends ServerPageAction<String> {
    public StringServerPageAction(UIComponent component, short serverActionId) {
        super(component, serverActionId);
    }

    @Override
    public void serialize(String object, ByteBuffer buffer) {
        buffer.putInt(object.length());
        buffer.put(object.getBytes(StandardCharsets.UTF_16BE));
    }

    @Override
    public int size(String object) {
        return 4 + object.length()*2;
    }
}

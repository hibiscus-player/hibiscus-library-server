package me.mrgazdag.hibiscus.library.ui.action.client;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringClientPageAction extends ClientPageAction<String> {
    public StringClientPageAction(short actionId) {
        super(actionId);
    }

    @Override
    protected String deserialize(ByteBuffer buffer) {
        byte[] string = new byte[buffer.getInt()*2];
        buffer.get(string, 0, string.length);
        return new String(string, StandardCharsets.UTF_16BE);
    }
}

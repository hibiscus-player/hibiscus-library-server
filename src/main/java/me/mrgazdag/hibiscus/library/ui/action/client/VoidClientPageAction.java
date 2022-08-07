package me.mrgazdag.hibiscus.library.ui.action.client;

import java.nio.ByteBuffer;

public class VoidClientPageAction extends ClientPageAction<Void> {
    public VoidClientPageAction(short actionId) {
        super(actionId);
    }

    @Override
    protected Void deserialize(ByteBuffer buffer) {
        return null;
    }
}

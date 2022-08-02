package me.mrgazdag.hibiscus.library.ui.change;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.util.function.Consumer;

public abstract class SimpleChangeHandler implements ChangeHandler {
    protected final Object sync;

    protected boolean autoUpdate;
    protected boolean updateQueued;

    public SimpleChangeHandler() {
        this.sync = new Object();
        this.autoUpdate = true;
        this.updateQueued = false;
    }

    protected abstract void iterateDevices(Consumer<ConnectedDevice> action);

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        synchronized (sync) {
            this.autoUpdate = autoUpdate;
            if (autoUpdate) {
                // Send all queued packets
                sendQueuedUpdates();
            }
        }
    }

    @Override
    public boolean isAutoUpdate() {
        return autoUpdate;
    }
}

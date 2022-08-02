package me.mrgazdag.hibiscus.library.ui.change;

import me.mrgazdag.hibiscus.library.ui.property.UIProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public interface ChangeHandler {
    void setAutoUpdate(boolean autoUpdate);
    boolean isAutoUpdate();
    void sendQueuedUpdates();
    void propertyUpdated(UIProperty<?> property, ConnectedDevice targetDevice);
}

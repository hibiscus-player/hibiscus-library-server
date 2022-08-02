package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public class AbstractDeviceEvent implements DeviceEvent {
    protected final ConnectedDevice device;

    public AbstractDeviceEvent(ConnectedDevice device) {
        this.device = device;
    }

    public ConnectedDevice getDevice() {
        return device;
    }
}

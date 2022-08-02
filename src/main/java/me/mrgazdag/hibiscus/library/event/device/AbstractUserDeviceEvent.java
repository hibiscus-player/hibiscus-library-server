package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.event.EventManager;
import me.mrgazdag.hibiscus.library.event.user.AbstractUserEvent;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public abstract class AbstractUserDeviceEvent extends AbstractUserEvent implements DeviceEvent {
    protected final ConnectedDevice device;

    public AbstractUserDeviceEvent(ConnectedDevice device) {
        super(device.getUser());
        this.device = device;
    }

    public ConnectedDevice getDevice() {
        return device;
    }

    @Override
    public void executeCustomNodes(Class<?> clazz, EventManager manager) {
        device.handleEvent(clazz, this);
        device.getUser().handleEvent(clazz, this);
    }
}

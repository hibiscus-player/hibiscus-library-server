package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.event.CallableEvent;
import me.mrgazdag.hibiscus.library.event.Event;
import me.mrgazdag.hibiscus.library.event.EventManager;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

@CallableEvent
public interface DeviceEvent extends Event {
    ConnectedDevice getDevice();

    @Override
    default void executeCustomNodes(Class<?> clazz, EventManager manager) {
        getDevice().handleEvent(clazz, this);
    }
}

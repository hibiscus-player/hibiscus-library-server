package me.mrgazdag.hibiscus.library.event.user;

import me.mrgazdag.hibiscus.library.event.CallableEvent;
import me.mrgazdag.hibiscus.library.event.Event;
import me.mrgazdag.hibiscus.library.event.EventManager;
import me.mrgazdag.hibiscus.library.users.ConnectedUser;

@CallableEvent
public interface UserEvent extends Event {
    ConnectedUser getUser();

    @Override
    default void executeCustomNodes(Class<?> clazz, EventManager manager) {
        getUser().handleEvent(clazz, this);
    }
}

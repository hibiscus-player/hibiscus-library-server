package me.mrgazdag.hibiscus.library.event.user;

import me.mrgazdag.hibiscus.library.users.ConnectedUser;

public abstract class AbstractUserEvent implements UserEvent {
    protected final ConnectedUser user;

    public AbstractUserEvent(ConnectedUser user) {
        this.user = user;
    }

    public ConnectedUser getUser() {
        return user;
    }
}

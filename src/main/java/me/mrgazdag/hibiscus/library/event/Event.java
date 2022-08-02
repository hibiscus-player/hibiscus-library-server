package me.mrgazdag.hibiscus.library.event;

/**
 * Represents a generic event.
 */
@CallableEvent
public interface Event {
    default void executeCustomNodes(Class<?> clazz, EventManager manager) {}
}

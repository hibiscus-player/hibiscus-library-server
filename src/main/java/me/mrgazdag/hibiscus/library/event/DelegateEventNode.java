package me.mrgazdag.hibiscus.library.event;

import me.mrgazdag.hibiscus.library.plugin.Plugin;

import java.util.function.Consumer;

public interface DelegateEventNode<E extends Event> extends EventNode<E> {
    EventNode<E> getEventNode();

    @Override
    default <T extends E> void handleEvent(Class<?> clazz, T event) {
        getEventNode().handleEvent(clazz, event);
    }

    @Override
    default <T extends E> void registerEventHandler(Class<T> type, Plugin plugin, Consumer<T> handler) {
        getEventNode().registerEventHandler(type, plugin, handler);
    }

    @Override
    default <T extends E> void unregisterEventHandler(Class<T> type, Plugin plugin, Consumer<T> handler) {
        getEventNode().unregisterEventHandler(type, plugin, handler);
    }

    @Override
    default void unregisterAll(Plugin plugin) {
        getEventNode().unregisterAll(plugin);
    }
}

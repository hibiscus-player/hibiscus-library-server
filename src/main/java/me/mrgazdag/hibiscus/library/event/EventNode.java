package me.mrgazdag.hibiscus.library.event;

import me.mrgazdag.hibiscus.library.plugin.Plugin;

import java.util.function.Consumer;

public interface EventNode<E extends Event> {
    <T extends E> void handleEvent(Class<?> clazz, T event);
    <T extends E> void registerEventHandler(Class<T> type, Plugin plugin, Consumer<T> handler);
    <T extends E> void unregisterEventHandler(Class<T> type, Plugin plugin, Consumer<T> handler);
    void unregisterAll(Plugin plugin);
}

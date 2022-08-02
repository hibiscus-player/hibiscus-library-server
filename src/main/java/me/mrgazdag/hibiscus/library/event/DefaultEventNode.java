package me.mrgazdag.hibiscus.library.event;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DefaultEventNode<E extends Event> implements EventNode<E> {
    private final Table<Class<?>, Plugin, Set<Consumer<? extends E>>> map;
    public DefaultEventNode(LibraryServer library) {
        this(library.getEventManager());
    }
    public DefaultEventNode(EventManager manager) {
        this.map = HashBasedTable.create();
        manager.registerNode(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends E> void handleEvent(Class<?> clazz, T event) {
        for (Set<Consumer<? extends E>> handlers : map.row(clazz).values()) {
            for (Consumer<? extends E> handler : handlers) {
                ((Consumer<E>)handler).accept(event);
            }
        }
    }

    @Override
    public <T extends E> void registerEventHandler(Class<T> type, Plugin plugin, Consumer<T> handler) {
        Set<Consumer<? extends E>> set;
        if (map.contains(type, plugin)) {
            set = map.get(type, plugin);
        } else {
            set = new HashSet<>();
            map.put(type, plugin, set);
        }
        set.add(handler);
    }

    @Override
    public <T extends E> void unregisterEventHandler(Class<T> type, Plugin plugin, Consumer<T> handler) {
        if (map.contains(type, plugin)) {
            map.get(type, plugin).remove(handler);
        }
    }

    @Override
    public void unregisterAll(Plugin plugin) {
        map.column(plugin).clear();
    }
}

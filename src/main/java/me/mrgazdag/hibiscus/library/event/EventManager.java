package me.mrgazdag.hibiscus.library.event;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.plugin.Plugin;

import java.util.*;

public class EventManager implements DelegateEventNode<Event> {
    private final LibraryServer library;
    private final Map<Class<?>, List<Class<?>>> eventClassMap;
    private final Set<EventNode<?>> registeredNodes;
    private final DefaultEventNode<Event> rootNode;

    public EventManager(LibraryServer library) {
        this.library = library;
        this.eventClassMap = new WeakHashMap<>();
        this.registeredNodes = new HashSet<>();
        this.rootNode = new DefaultEventNode<>(this);
    }

    public LibraryServer getLibrary() {
        return library;
    }

    public void registerNode(EventNode<?> node) {
        synchronized (this.registeredNodes) {
            this.registeredNodes.add(node);
        }
    }
    public void unregisterNode(EventNode<?> node) {
        synchronized (this.registeredNodes) {
            this.registeredNodes.remove(node);
        }
    }
    @Override
    public void unregisterAll(Plugin plugin) {
        synchronized (this.registeredNodes) {
            for (EventNode<?> node : this.registeredNodes) {
                node.unregisterAll(plugin);
            }
        }
    }

    @Override
    public EventNode<Event> getEventNode() {
        return rootNode;
    }

    public void callEvent(Event event) {
        Class<?> clazz = event.getClass();
        List<Class<?>> types;
        if (eventClassMap.containsKey(clazz)) {
            types = eventClassMap.get(clazz);
        } else {
            types = new ArrayList<>();
            Class<?> current = clazz;
            while (Event.class.isAssignableFrom(current)) {
                if (current.isAnnotationPresent(CallableEvent.class)) {
                    types.add(current);
                }
                collectInterfaces(current, types);
                current = current.getSuperclass();
            }
            eventClassMap.put(clazz, types);
        }
        for (Class<?> type : types) {
            event.executeCustomNodes(type, this);
            handleEvent(type, event);
        }
    }
    private void collectInterfaces(Class<?> clazz, List<Class<?>> list) {
        for (Class<?> iface : clazz.getInterfaces()) {
            if (Event.class.isAssignableFrom(iface) && iface.isAnnotationPresent(CallableEvent.class) && !list.contains(iface)) {
                list.add(iface);
                collectInterfaces(iface, list);
            }
        }
    }
}

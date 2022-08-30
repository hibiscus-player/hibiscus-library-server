package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public interface UIContainer {
    void setChildComponent(int id, UIComponent component);
    UIComponent getChildComponent(int id);
    int getMaxChildrenCount();
    int getComponentId();
    boolean isVisible(ConnectedDevice device);

    boolean areParentsVisible(ConnectedDevice device);

    void clearChild(int childIndex);
}

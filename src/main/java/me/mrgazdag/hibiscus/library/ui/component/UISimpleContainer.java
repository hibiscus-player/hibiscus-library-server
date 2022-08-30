package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.page.Page;

import java.util.ArrayList;
import java.util.List;

public abstract class UISimpleContainer extends UIComponent implements UIContainer {
    private final int maxChildren;
    private final List<UIComponent> children;
    public UISimpleContainer(Page page, int componentId, int maxChildren) {
        super(page, componentId);
        this.maxChildren = maxChildren;
        this.children = new ArrayList<>(Math.min(16, maxChildren));
    }

    public List<UIComponent> getChildren() {
        return children;
    }

    @Override
    public int getMaxChildrenCount() {
        return maxChildren;
    }

    @Override
    public UIComponent getChildComponent(int id) {
        return children.get(id);
    }

    private <T extends UIComponent> T create(T source) {
        children.add(source);
        source.updateParent(this, children.size()-1);
        return source;
    }

    @Override
    public void setChildComponent(int id, UIComponent component) {
        UIComponent old = children.size() > id ? children.get(id) : null;
        if (old != null) old.updateParent(null, -1);
        children.add(id, component);
        component.updateParent(this, id);
    }

    @Override
    public void clearChild(int childIndex) {
        children.remove(childIndex);
    }

    public void addChild(UIComponent child) {
        setChildComponent(children.size(), child);
    }

    public void removeChild(UIComponent child) {
        child.updateParent(null, -1);
    }
}

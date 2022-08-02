package me.mrgazdag.hibiscus.library.ui.property.visibility;

import me.mrgazdag.hibiscus.library.ui.change.ComponentChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.property.BooleanProperty;

public class ComponentVisibilityProperty extends BooleanProperty {
    private final ComponentChangeHandler changeHandler;
    private final UIComponent component;
    public ComponentVisibilityProperty(UIComponent component, short propertyId, ComponentChangeHandler changeHandler, boolean defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
        this.changeHandler = changeHandler;
        this.component = component;
    }

    @Override
    public void sendUpdate() {
        changeHandler.visibilityUpdated(component, this);
    }
}

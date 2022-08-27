package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.action.client.StringClientPageAction;
import me.mrgazdag.hibiscus.library.ui.action.server.StringServerPageAction;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.EnumProperty;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public class TextInputComponent extends UIComponent {
    private final StringProperty defaultValue;
    private final EnumProperty<UpdateEventType> updateEvents;
    private final StringClientPageAction onInputChange;
    private final StringServerPageAction setValue;

    public TextInputComponent(Page page, int componentId) {
        super(page, componentId);
        this.defaultValue = stringProperty("");
        this.updateEvents = enumProperty(UpdateEventType.class, UpdateEventType.AFTER_CHANGE);
        this.onInputChange = stringClientAction();
        this.setValue = stringServerAction();
    }

    public StringProperty getDefaultValue() {
        return defaultValue;
    }
    public String getDefaultValue(ConnectedDevice device) {
        return defaultValue.get(device);
    }

    public StringClientPageAction onInputChange() {
        return this.onInputChange;
    }

    public void setValue(ConnectedDevice device, String string) {
        this.setValue.send(device, string);
    }

    @Override
    protected String getComponentTypeName() {
        return "text_input";
    }

    public enum UpdateEventType {
        EVERY_KEY,
        AFTER_CHANGE,
        AFTER_CHANGE_TIMEOUT
    }
}

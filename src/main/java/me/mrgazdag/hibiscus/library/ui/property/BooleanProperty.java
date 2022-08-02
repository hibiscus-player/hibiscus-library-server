package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class BooleanProperty extends UIProperty<Boolean> {

    public BooleanProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, boolean defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(Boolean value) {
        return 1;
    }

    @Override
    public void serializeValue(Boolean value, ByteBuffer buffer) {
        buffer.put((byte) (value ? 1 : 0));
    }
}

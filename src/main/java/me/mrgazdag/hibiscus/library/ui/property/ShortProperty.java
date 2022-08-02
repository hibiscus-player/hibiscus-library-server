package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class ShortProperty extends UIProperty<Short> {

    public ShortProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, short defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(Short value) {
        return Short.BYTES; // Value
    }

    @Override
    public void serializeValue(Short value, ByteBuffer buffer) {
        buffer.putShort(value);
    }
}

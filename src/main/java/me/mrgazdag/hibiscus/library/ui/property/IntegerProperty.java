package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class IntegerProperty extends UIProperty<Integer> {

    public IntegerProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, int defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(Integer value) {
        return Integer.BYTES; // Value
    }

    @Override
    public void serializeValue(Integer value, ByteBuffer buffer) {
        buffer.putInt(value);
    }
}

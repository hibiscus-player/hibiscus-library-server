package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class FloatProperty extends UIProperty<Float> {

    public FloatProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, float defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(Float value) {
        return Float.BYTES; // Value
    }

    @Override
    public void serializeValue(Float value, ByteBuffer buffer) {
        buffer.putFloat(value);
    }
}

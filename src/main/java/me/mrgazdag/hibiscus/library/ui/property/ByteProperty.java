package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class ByteProperty extends UIProperty<Byte> {

    public ByteProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, byte defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(Byte value) {
        return Byte.BYTES;
    }

    @Override
    public void serializeValue(Byte value, ByteBuffer buffer) {
        buffer.put(value);
    }
}

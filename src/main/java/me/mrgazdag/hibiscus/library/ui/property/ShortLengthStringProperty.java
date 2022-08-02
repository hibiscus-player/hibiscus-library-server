package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ShortLengthStringProperty extends UIProperty<String> {

    public ShortLengthStringProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, String defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(String value) {
        return 2 // Length in chars
                + value.length() * 2; //Main data
    }

    @Override
    public void serializeValue(String value, ByteBuffer buffer) {
        buffer.putShort((short) value.length());
        buffer.put(value.getBytes(StandardCharsets.UTF_16BE));
    }
}

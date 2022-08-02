package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringProperty extends UIProperty<String> {

    public StringProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, String defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(String value) {
        return 4 // Length in chars
                + value.length() * 2; //Main data
    }

    @Override
    public void serializeValue(String value, ByteBuffer buffer) {
        buffer.putInt(value.length());
        buffer.put(value.getBytes(StandardCharsets.UTF_16BE));
    }
}

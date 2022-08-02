package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.color.Color;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class ColorProperty extends UIProperty<Color> {

    public ColorProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, Color defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(Color value) {
        return 4; // Value
    }

    @Override
    public void serializeValue(Color value, ByteBuffer buffer) {
        buffer.putInt(value == null ? 0 : value.getColorValue());
    }
}

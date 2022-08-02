package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.color.ThemeColor;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class ThemeColorProperty extends UIProperty<ThemeColor> {

    public ThemeColorProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, ThemeColor defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
    }

    @Override
    public int serializedValueSizeInBytes(ThemeColor value) {
        return 2; // Value
    }

    @Override
    public void serializeValue(ThemeColor value, ByteBuffer buffer) {
        buffer.putShort((short) (value == null ? 0 : value.getColorID()));
    }
}

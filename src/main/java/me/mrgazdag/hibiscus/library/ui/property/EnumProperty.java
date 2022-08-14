package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;

import java.nio.ByteBuffer;

public class EnumProperty<E extends Enum<E>> extends UIProperty<E> {
    private final int enumConstantCount;
    public EnumProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, Class<E> clazz, E defaultValue) {
        super(component, propertyId, changeHandler, defaultValue);
        this.enumConstantCount = clazz.getEnumConstants().length;
    }

    @Override
    public int serializedValueSizeInBytes(E value) {
        // Special case of -1 representing null
        if (enumConstantCount < 256) {
            return Byte.BYTES;
        } else if (enumConstantCount < 65536) {
            return Short.BYTES;
        } else {
            return Integer.BYTES;
        }
    }

    @Override
    public void serializeValue(E value, ByteBuffer buffer) {
        int num = value == null ? -1 : value.ordinal();
        if (enumConstantCount < 256) {
            buffer.put((byte) num);
        } else if (enumConstantCount < 65536) {
            buffer.putShort((short) num);
        } else {
            buffer.putInt(num);
        }
    }
}

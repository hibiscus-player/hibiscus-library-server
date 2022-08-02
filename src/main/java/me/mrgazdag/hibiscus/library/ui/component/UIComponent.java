package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.change.ComponentChangeHandler;
import me.mrgazdag.hibiscus.library.ui.color.Color;
import me.mrgazdag.hibiscus.library.ui.color.ThemeColor;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.*;
import me.mrgazdag.hibiscus.library.ui.property.visibility.ComponentVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class UIComponent {
    private final Page page;
    private final int componentId;
    private final ComponentChangeHandler changeHandler;
    private final List<UIProperty<?>> properties;
    private final ComponentVisibilityProperty visibilityProperty;

    public UIComponent(Page page, int componentId) {
        this.page = page;
        this.componentId = componentId;
        this.changeHandler = new ComponentChangeHandler(page);
        this.properties = new ArrayList<>();
        this.visibilityProperty = visibilityProperty(true);
    }
    protected ComponentVisibilityProperty visibilityProperty(boolean defaultValue) {
        // Special case, no property ID needed, as this does not get sent to the client
        return new ComponentVisibilityProperty(this, (short) -1, changeHandler, defaultValue);
    }
    protected BooleanProperty booleanProperty(boolean defaultValue) {
        short propertyId = (short) properties.size();
        BooleanProperty prop = new BooleanProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected ByteLengthStringProperty byteLengthStringProperty(String defaultValue) {
        short propertyId = (short) properties.size();
        ByteLengthStringProperty prop = new ByteLengthStringProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected ByteProperty byteProperty(byte defaultValue) {
        short propertyId = (short) properties.size();
        ByteProperty prop = new ByteProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected ColorProperty colorProperty(Color defaultValue) {
        short propertyId = (short) properties.size();
        ColorProperty prop = new ColorProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected FloatProperty floatProperty(float defaultValue) {
        short propertyId = (short) properties.size();
        FloatProperty prop = new FloatProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected IntegerProperty integerProperty(int defaultValue) {
        short propertyId = (short) properties.size();
        IntegerProperty prop = new IntegerProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected ShortLengthStringProperty shortLengthStringProperty(String defaultValue) {
        short propertyId = (short) properties.size();
        ShortLengthStringProperty prop = new ShortLengthStringProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected ShortProperty shortProperty(short defaultValue) {
        short propertyId = (short) properties.size();
        ShortProperty prop = new ShortProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected StringProperty stringProperty(String defaultValue) {
        short propertyId = (short) properties.size();
        StringProperty prop = new StringProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }
    protected ThemeColorProperty themeColorProperty(ThemeColor defaultValue) {
        short propertyId = (short) properties.size();
        ThemeColorProperty prop = new ThemeColorProperty(this, propertyId, changeHandler, defaultValue);
        properties.add(prop);
        return prop;
    }

    public ComponentVisibilityProperty getVisibility() {
        return visibilityProperty;
    }

    public boolean isVisible(ConnectedDevice device) {
        return getVisibility().get(device);
    }

    public int getComponentId() {
        return componentId;
    }

    @SuppressWarnings("unchecked")
    public ByteBuffer serializeAdd(ConnectedDevice device) {
        String typeName = getComponentTypeName();
        int originalLength = 4 + 2 + typeName.length()*2 + 2; // Component ID + Type Name length (short) + Type Name (UTF16LE) + Data Length (short)
        int dataLength = 0;
        List<Object> valueList = new ArrayList<>();
        for (UIProperty<?> property : properties) {
            Object value = property.get(device);
            dataLength += ((UIProperty<Object>)property).serializedValueSizeInBytes(value);
            valueList.add(value);
        }

        ByteBuffer buf = ByteBuffer.allocateDirect(originalLength + dataLength);
        // Component ID
        buf.putInt(componentId);
        // Type Name
        buf.putShort((short) typeName.length());
        buf.put(typeName.getBytes(StandardCharsets.UTF_16BE));
        // Data
        buf.putShort((short) dataLength);
        for (int i = 0; i < properties.size(); i++) {
            UIProperty<Object> property = (UIProperty<Object>) properties.get(i);
            Object value = valueList.get(i);
            property.serializeValue(value, buf);
        }
        return buf;
    }
    protected abstract String getComponentTypeName();
}
package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.action.client.ClientPageAction;
import me.mrgazdag.hibiscus.library.ui.action.client.StringClientPageAction;
import me.mrgazdag.hibiscus.library.ui.action.client.VoidClientPageAction;
import me.mrgazdag.hibiscus.library.ui.action.server.ServerPageAction;
import me.mrgazdag.hibiscus.library.ui.action.server.StringServerPageAction;
import me.mrgazdag.hibiscus.library.ui.action.server.VoidServerPageAction;
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
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class UIComponent {
    private final Page page;
    private final int componentId;
    private final ComponentChangeHandler changeHandler;
    private final List<UIProperty<?>> properties;
    private final List<ClientPageAction<?>> clientActions;
    private final List<ServerPageAction<?>> serverActions;
    private final ComponentVisibilityProperty visibilityProperty;
    private UIContainer parent;
    private int childIndex;

    public UIComponent(Page page, int componentId) {
        this.page = page;
        this.componentId = componentId;
        this.changeHandler = new ComponentChangeHandler(page);
        this.properties = new ArrayList<>();
        this.clientActions = new ArrayList<>();
        this.serverActions = new ArrayList<>();
        this.visibilityProperty = visibilityProperty(true);
    }

    public void updateParent(UIContainer parent, int childIndex) {
        UIContainer oldParent = this.parent;
        if (this.parent != parent && this.parent != null) {
            this.parent.clearChild(childIndex);
        }
        this.parent = parent;
        this.childIndex = childIndex;
        changeHandler.parentUpdated(this, oldParent);
    }

    public UIContainer getParent() {
        return parent;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public Page getPage() {
        return page;
    }


    public int getComponentId() {
        return componentId;
    }

    // Properties
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
    protected <E extends Enum<E>> EnumProperty<E> enumProperty(Class<E> clazz, E defaultValue) {
        short propertyId = (short) properties.size();
        EnumProperty<E> prop = new EnumProperty<>(this, propertyId, changeHandler, clazz, defaultValue);
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

    // Client actions

    public ClientPageAction<?> getClientPageAction(int actionId) {
        return clientActions.get(actionId);
    }
    protected VoidClientPageAction voidClientAction() {
        short actionId = (short) clientActions.size();
        VoidClientPageAction action = new VoidClientPageAction(actionId);
        clientActions.add(action);
        return action;
    }
    protected StringClientPageAction stringClientAction() {
        short actionId = (short) clientActions.size();
        StringClientPageAction action = new StringClientPageAction(actionId);
        clientActions.add(action);
        return action;
    }
    protected <T extends ClientPageAction<?>> T customClientAction(Function<Short, T> constructor) {
        short actionId = (short) clientActions.size();
        T action = constructor.apply(actionId);
        clientActions.add(action);
        return action;
    }

    // Server actions
    protected VoidServerPageAction voidServerAction() {
        short actionId = (short) serverActions.size();
        VoidServerPageAction action = new VoidServerPageAction(this, actionId);
        serverActions.add(action);
        return action;
    }
    protected StringServerPageAction stringServerAction() {
        short actionId = (short) serverActions.size();
        StringServerPageAction action = new StringServerPageAction(this, actionId);
        serverActions.add(action);
        return action;
    }

    protected <T extends ServerPageAction<?>> T customServerAction(BiFunction<UIComponent, Short, T> constructor) {
        short actionId = (short) serverActions.size();
        T action = constructor.apply(this, actionId);
        serverActions.add(action);
        return action;
    }

    public ComponentVisibilityProperty getVisibility() {
        return visibilityProperty;
    }

    public boolean isVisible(ConnectedDevice device) {
        return getVisibility().get(device);
    }

    public boolean areParentsVisible(ConnectedDevice device) {
        UIContainer parent = getParent();
        while (parent instanceof UIComponent component) {
            if (!component.isVisible(device)) return false;
            parent = component.getParent();
        }
        return parent != null;
    }

    @SuppressWarnings("unchecked")
    public ByteBuffer serializeAdd(ConnectedDevice device) {
        String typeName = getComponentTypeName();
        int originalLength = 4 + // Component ID (int)
                2 + // Type Name Length (short)
                4 + // Parent ID (int)
                4 + // Child Index (int)
                typeName.length()*2 // Type Name (string UTF16BE)
                + 2; // Data Length (short)
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
        // Parent ID
        buf.putInt(parent.getComponentId());
        // Child Index
        buf.putInt(childIndex);
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

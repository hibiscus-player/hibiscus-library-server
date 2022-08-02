package me.mrgazdag.hibiscus.library.ui.property;

import me.mrgazdag.hibiscus.library.ui.change.ChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.property.filters.ContextFilter;
import me.mrgazdag.hibiscus.library.ui.property.filters.DevicePropertyFilter;
import me.mrgazdag.hibiscus.library.ui.property.filters.UserPropertyFilter;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.ConnectedUser;

import java.nio.ByteBuffer;
import java.util.*;

public abstract class UIProperty<T> {
    private final UIComponent component;
    private final short propertyId;
    private final ChangeHandler changeHandler;

    private T defaultValue;
    private final Map<ConnectedDevice, T> deviceOverrides;
    private final Map<ConnectedUser, T> userOverrides;
    private final List<DevicePropertyFilter<T>> deviceFilters;
    private final List<UserPropertyFilter<T>> userFilters;
    private final List<ContextFilter<T>> contextFilters;

    public UIProperty(UIComponent component, short propertyId, ChangeHandler changeHandler, T defaultValue) {
        this.component = component;
        this.propertyId = propertyId;
        this.changeHandler = changeHandler;

        this.defaultValue = defaultValue;
        this.deviceOverrides = new WeakHashMap<>();
        this.userOverrides = new WeakHashMap<>();
        this.deviceFilters = new ArrayList<>();
        this.userFilters = new ArrayList<>();
        this.contextFilters = new ArrayList<>();
    }

    public UIComponent getComponent() {
        return component;
    }

    public short getPropertyId() {
        return propertyId;
    }

    public int serializedUpdateSizeInBytes(T value) {
        return 4 + //Component ID
                2 + //Property ID
                2 + //Content Length
                serializedValueSizeInBytes(value); //Content
    }
    public abstract int serializedValueSizeInBytes(T value);

    public void serializeUpdate(T value, ByteBuffer buffer) {
        buffer.putInt(component.getComponentId());
        buffer.putShort(propertyId);
        buffer.putShort((short) serializedValueSizeInBytes(value));
        serializeValue(value, buffer);
    }
    public abstract void serializeValue(T value, ByteBuffer buffer);

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        sendUpdate();
    }
    public void setOverride(ConnectedDevice device, T value) {
        T oldValue = get(device);
        deviceOverrides.put(device, value);
        if (!Objects.deepEquals(oldValue, value)) {
            sendUpdate(device);
        }
    }
    public void setOverride(ConnectedUser user, T value) {
        userOverrides.put(user, value);
        for (ConnectedDevice device : user.getDevices()) {
            sendUpdate(device);
        }
    }
    public void clearOverride(ConnectedDevice device) {
        T oldValue = get(device);
        deviceOverrides.remove(device);
        if (!Objects.deepEquals(oldValue, get(device))) {
            sendUpdate(device);
        }
        sendUpdate();
    }
    public void clearOverride(ConnectedUser user) {
        userOverrides.remove(user);
        for (ConnectedDevice device : user.getDevices()) {
            sendUpdate(device);
        }
    }
    public void clearOverrides() {
        deviceOverrides.clear();
        userOverrides.clear();
        sendUpdate();
    }
    public void addDeviceFilter(DevicePropertyFilter<T> filter) {
        deviceFilters.add(filter);
        sendUpdate();
    }
    public void removeDeviceFilter(DevicePropertyFilter<T> filter) {
        deviceFilters.remove(filter);
        sendUpdate();
    }
    public void addUserFilter(UserPropertyFilter<T> filter) {
        userFilters.add(filter);
        sendUpdate();
    }
    public void removeUserFilter(UserPropertyFilter<T> filter) {
        userFilters.remove(filter);
        sendUpdate();
    }
    public void addContextFilter(ContextFilter<T> filter) {
        contextFilters.add(filter);
        sendUpdate();
    }
    public void removeContextFilter(ContextFilter<T> filter) {
        contextFilters.remove(filter);
        sendUpdate();
    }
    public void clearFilters() {
        deviceFilters.clear();
        userFilters.clear();
        contextFilters.clear();
        sendUpdate();
    }

    public void sendUpdate() {
        changeHandler.propertyUpdated(this, null);
    }
    public void sendUpdate(ConnectedDevice device) {
        changeHandler.propertyUpdated(this, device);
    }
    public T get(ConnectedDevice device) {
        if (deviceOverrides.containsKey(device)) return deviceOverrides.get(device);
        else if (userOverrides.containsKey(device.getUser())) return userOverrides.get(device.getUser());
        else {
            for (ContextFilter<T> contextFilter : contextFilters) {
                if (contextFilter.test(device.getCurrentPage())) return contextFilter.apply(device.getCurrentPage());
            }
            for (DevicePropertyFilter<T> deviceFilter : deviceFilters) {
                if (deviceFilter.test(device)) return deviceFilter.apply(device);
            }
            for (UserPropertyFilter<T> userFilter : userFilters) {
                if (userFilter.test(device.getUser())) return userFilter.apply(device.getUser());
            }
            return defaultValue;
        }
    }
}
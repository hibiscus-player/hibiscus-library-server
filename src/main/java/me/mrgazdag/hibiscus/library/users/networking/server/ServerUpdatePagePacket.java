package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.property.UIProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerUpdatePagePacket extends ServerPacket {
    private static final byte COMPONENTS_ADDED_BIT = 0x01;
    private static final byte COMPONENTS_REMOVED_BIT = 0x02;
    private static final byte PROPERTIES_UPDATED_BIT = 0x04;
    private final ByteBuffer buffer;
    public ServerUpdatePagePacket(ConnectedDevice device, UIComponent componentAdded, UIComponent componentRemoved, UIProperty<?> propertyUpdated) {
        byte mask = 0;
        if (componentAdded != null) mask |= COMPONENTS_ADDED_BIT;
        if (componentRemoved != null) mask |= COMPONENTS_REMOVED_BIT;
        if (propertyUpdated != null) mask |= PROPERTIES_UPDATED_BIT;

        int length = 1 + 1; // Packet ID + Mask

        ByteBuffer added;
        if (componentAdded != null) {
            length += 4; // List size (will be 1)
            added = componentAdded.serializeAdd(device);
            length += added.position();
            added.position(0);
        } else added = null;

        int removed;
        if (componentRemoved != null) {
            length += 4; // List size (will be 1)
            removed = componentRemoved.getComponentId();
            length += 4;
        } else removed = -1;

        UpdatedProperty<?> updatedValue;
        if (propertyUpdated != null && propertyUpdated.getComponent().isVisible(device)) {
            length += 4; // List size (will be 1)
            updatedValue = new UpdatedProperty<>(propertyUpdated, device);
            length += updatedValue.size();
        } else updatedValue = null;

        buffer = ByteBuffer.allocateDirect(length);
        buffer.put(getID());
        buffer.put(mask);
        if (componentAdded != null) {
            buffer.putInt(1); // List size
            buffer.put(added);
        }
        if (componentRemoved != null) {
            buffer.putInt(1); // List size
            buffer.putInt(removed);
        }
        if (updatedValue != null) {
            buffer.putInt(1); // List size
            updatedValue.serialize(buffer);
        }

        buffer.position(0);
    }
    public ServerUpdatePagePacket(ConnectedDevice device, Collection<UIComponent> componentsAdded, Collection<UIComponent> componentsRemoved, Collection<UIProperty<?>> propertiesUpdated) {
        byte mask = 0;
        if (componentsAdded != null && componentsAdded.size() > 0) mask |= COMPONENTS_ADDED_BIT;
        if (componentsRemoved != null && componentsRemoved.size() > 0) mask |= COMPONENTS_REMOVED_BIT;
        if (propertiesUpdated != null && propertiesUpdated.size() > 0) mask |= PROPERTIES_UPDATED_BIT;

        int length = 1 + 1; // Packet ID + Mask

        List<ByteBuffer> toAdd;
        if ((mask & COMPONENTS_ADDED_BIT) > 0) {
            length += 4; // List size

            toAdd = new ArrayList<>();
            for (UIComponent component : componentsAdded) {
                ByteBuffer serialized = component.serializeAdd(device);
                length += serialized.position();
                serialized.position(0);
                toAdd.add(serialized);
            }
        } else toAdd = null;

        List<Integer> toRemove;
        if ((mask & COMPONENTS_REMOVED_BIT) > 0) {
            length += 4; // List size

            toRemove = new ArrayList<>();
            length += 4 * componentsRemoved.size();
            for (UIComponent component : componentsRemoved) {
                toRemove.add(component.getComponentId());
            }
        } else toRemove = null;

        List<UpdatedProperty<?>> toUpdate;
        if ((mask & PROPERTIES_UPDATED_BIT) > 0) {
            length += 4; // List size

            toUpdate = new ArrayList<>();
            for (UIProperty<?> property : propertiesUpdated) {
                if (!property.getComponent().isVisible(device)) continue;
                UpdatedProperty<?> updated = new UpdatedProperty<>(property, device);
                length += updated.size();
                toUpdate.add(updated);
            }
        } else toUpdate = null;

        buffer = ByteBuffer.allocateDirect(length);
        buffer.put(getID());
        buffer.put(mask);

        if ((mask & COMPONENTS_ADDED_BIT) > 0) {
            buffer.putInt(toAdd.size()); // List size

            for (ByteBuffer addBuffer : toAdd) {
                buffer.put(addBuffer);
            }
        }
        if ((mask & COMPONENTS_REMOVED_BIT) > 0) {
            buffer.putInt(toRemove.size()); // List size

            for (Integer integer : toRemove) {
                buffer.putInt(integer);
            }
        }
        if ((mask & PROPERTIES_UPDATED_BIT) > 0) {
            buffer.putInt(toUpdate.size()); // List size
            for (UpdatedProperty<?> prop : toUpdate) {
                prop.serialize(buffer);
            }
        }

        buffer.position(0);
    }

    @Override
    public ByteBuffer compress() {
        return buffer;
    }

    @Override
    protected void compress(ByteBuffer buffer) {}

    @Override
    protected int calculateLength() {
        return -1;
    }

    @Override
    public int getLength() {
        return buffer.capacity();
    }

    @Override
    public String toString() {
        return "ServerUpdatePageContentsPacket{" +
                "buffer=" + buffer +
                '}';
    }
    private static class UpdatedProperty<T> {
        private final UIProperty<T> property;
        private final T value;
        public UpdatedProperty(UIProperty<T> property, ConnectedDevice device) {
            this.property = property;
            this.value = property.get(device);
        }
        public int size() {
            return property.serializedUpdateSizeInBytes(value);
        }
        public void serialize(ByteBuffer buffer) {
            property.serializeUpdate(value, buffer);
        }
    }
}

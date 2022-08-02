package me.mrgazdag.hibiscus.library.ui.change;

import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.UIProperty;
import me.mrgazdag.hibiscus.library.ui.property.visibility.ComponentVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerUpdatePagePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ComponentChangeHandler extends SimpleChangeHandler {
    private final Page parentPage;
    private final List<UIComponent> visibilityList;
    private final List<UIComponent> addedList;
    private final List<UIComponent> removedList;
    private final List<UIProperty<?>> propertyUpdateList;

    public ComponentChangeHandler(Page parentPage) {
        this.parentPage = parentPage;
        this.visibilityList = new ArrayList<>();
        this.addedList = new ArrayList<>();
        this.removedList = new ArrayList<>();
        this.propertyUpdateList = new ArrayList<>();
    }

    @Override
    protected void iterateDevices(Consumer<ConnectedDevice> action) {
        parentPage.getActiveDevices().forEach(action);
    }

    public void visibilityUpdated(UIComponent component, ComponentVisibilityProperty property) {
        if (autoUpdate) {
            iterateDevices(device->{
                if (property.get(device)) {
                    // The client will handle add packets as update packets
                    // if the component with this id already exists
                    device.sendPacket(new ServerUpdatePagePacket(device, component, null, null));
                } else {
                    // The client will ignore remove packets
                    // if the component with this id does not exist
                    device.sendPacket(new ServerUpdatePagePacket(device, null, component, null));
                }
            });
        } else {
            synchronized (sync) {
                if (autoUpdate) {
                    //Value has been changed while waiting for the synchronized block
                    return;
                }
                visibilityList.add(component);
                updateQueued = true;
            }
        }
    }

    @Override
    public void propertyUpdated(UIProperty<?> property, ConnectedDevice targetDevice) {
        if (autoUpdate) {
            if (targetDevice == null) {
                iterateDevices(device->{
                    device.sendPacket(new ServerUpdatePagePacket(device, null, null, property));
                });
            } else {
                targetDevice.sendPacket(new ServerUpdatePagePacket(targetDevice, null, null, property));
            }
        } else {
            synchronized (sync) {
                if (autoUpdate) {
                    //Value has been changed while waiting for the synchronized block
                    return;
                }
                propertyUpdateList.add(property);
                updateQueued = true;
            }
        }
    }

    @Override
    public void sendQueuedUpdates() {
        synchronized (sync) {
            if (!updateQueued) return;
            if (visibilityList.size() > 0) {
                List<UIComponent> addedList = new ArrayList<>();
                List<UIComponent> removedList = new ArrayList<>();
                List<UIProperty<?>> propertyUpdateList = new ArrayList<>();
                iterateDevices(device->{
                    addedList.addAll(this.addedList);
                    removedList.addAll(this.addedList);
                    propertyUpdateList.addAll(this.propertyUpdateList);
                    for (UIComponent uiComponent : visibilityList) {
                        if (uiComponent.isVisible(device)) {
                            // The client will handle add packets as update packets
                            // if the component with this id already exists
                            if (!addedList.contains(uiComponent)) addedList.add(uiComponent);
                        } else {
                            // The client will ignore remove packets
                            // if the component with this id does not exist
                            if (!removedList.contains(uiComponent)) removedList.add(uiComponent);
                        }
                    }
                    device.sendPacket(new ServerUpdatePagePacket(device, addedList, removedList, propertyUpdateList));
                });
            } else {
                iterateDevices(device->{
                    device.sendPacket(new ServerUpdatePagePacket(device, addedList, removedList, propertyUpdateList));
                });
            }
            this.visibilityList.clear();
            this.addedList.clear();
            this.removedList.clear();
            this.propertyUpdateList.clear();
            this.updateQueued = false;
        }
    }
}

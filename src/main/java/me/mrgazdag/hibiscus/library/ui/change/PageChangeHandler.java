package me.mrgazdag.hibiscus.library.ui.change;

import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.UIProperty;
import me.mrgazdag.hibiscus.library.ui.property.visibility.PageVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerUpdatePagePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PageChangeHandler extends SimpleChangeHandler {
    private final UIManager manager;
    private final Page page;
    private boolean visibilityChanged;

    private final List<UIComponent> addedList;
    private final List<UIComponent> removedList;

    public PageChangeHandler(UIManager manager, Page page) {
        this.manager = manager;
        this.page = page;
        this.addedList = new ArrayList<>();
        this.removedList = new ArrayList<>();
        this.visibilityChanged = false;
    }

    @Override
    protected void iterateDevices(Consumer<ConnectedDevice> action) {
        manager.iterateDevices(action);
    }

    @Override
    public void sendQueuedUpdates() {
        synchronized (sync) {
            if (!updateQueued) return;
            if (!page.isRegistered()) return;

            if (visibilityChanged) {
                iterateDevices(device->{
                    if (page.isVisible(device)) {
                        // The client will handle add packets as update packets
                        // if the component with this id already exists
                        device.sendPacket(new ServerPageListChangePacket(device, null, null, null, page, null, null));
                    } else {
                        // The client will ignore remove packets
                        // if the component with this id does not exist
                        device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, page, null));
                    }
                });
                visibilityChanged = false;
            } else {
                iterateDevices(device->{
                    device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, null, page));
                });
            }

            if (addedList.size() > 0 || removedList.size() > 0) {

            }
            updateQueued = false;
        }
    }

    public void componentAdded(UIComponent component) {
        if (autoUpdate) {
            iterateDevices(device->{
                device.sendPacket(new ServerUpdatePagePacket(device, component, null, null, null));
            });
        } else {
            synchronized (sync) {
                if (autoUpdate) {
                    //Value has been changed while waiting for the synchronized block
                    return;
                }
                addedList.add(component);
                updateQueued = true;
            }
        }
    }

    public void componentRemoved(UIComponent component) {
        if (autoUpdate) {
            iterateDevices(device->{
                device.sendPacket(new ServerUpdatePagePacket(device, null, component, null, null));
            });
        } else {
            synchronized (sync) {
                if (autoUpdate) {
                    //Value has been changed while waiting for the synchronized block
                    return;
                }
                removedList.add(component);
                updateQueued = true;
            }
        }
    }

    public void visibilityUpdated(PageVisibilityProperty property) {
        if (autoUpdate) {
            iterateDevices(device->{
                if (property.get(device)) {
                    // The client will handle add packets as update packets
                    // if the component with this id already exists
                    device.sendPacket(new ServerPageListChangePacket(device, null, null, null, page, null, null));
                } else {
                    // The client will ignore remove packets
                    // if the component with this id does not exist
                    device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, page, null));
                }
            });
        } else {
            synchronized (sync) {
                if (autoUpdate) {
                    //Value has been changed while waiting for the synchronized block
                    return;
                }
                visibilityChanged = true;
                updateQueued = true;
            }
        }
    }

    @Override
    public void propertyUpdated(UIProperty<?> property, ConnectedDevice targetDevice) {
        if (autoUpdate) {
            if (targetDevice == null) {
                iterateDevices(device->{
                    device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, null, page));
                });
            } else {
                targetDevice.sendPacket(new ServerPageListChangePacket(targetDevice, null, null, null, null, null, page));
            }
        } else {
            synchronized (sync) {
                if (autoUpdate) {
                    //Value has been changed while waiting for the synchronized block
                    return;
                }
                updateQueued = true;
            }
        }
    }
}

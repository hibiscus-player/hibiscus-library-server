package me.mrgazdag.hibiscus.library.ui.change;

import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.ui.property.UIProperty;
import me.mrgazdag.hibiscus.library.ui.property.visibility.PageGroupVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;

import java.util.function.Consumer;

public class PageGroupChangeHandler extends SimpleChangeHandler {
    private final UIManager manager;
    private final PageGroup pageGroup;
    private boolean visibilityChanged;

    public PageGroupChangeHandler(UIManager manager, PageGroup pageGroup) {
        this.manager = manager;
        this.pageGroup = pageGroup;
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
            if (!pageGroup.isRegistered()) return;

            if (visibilityChanged) {
                iterateDevices(device->{
                    if (pageGroup.isVisible(device)) {
                        // The client will handle add packets as update packets
                        // if the component with this id already exists
                        device.sendPacket(new ServerPageListChangePacket(device, pageGroup, null, null, null, null, null));
                    } else {
                        // The client will ignore remove packets
                        // if the component with this id does not exist
                        device.sendPacket(new ServerPageListChangePacket(device, null, pageGroup, null, null, null, null));
                    }
                });
                visibilityChanged = false;
            } else {
                iterateDevices(device->{
                    device.sendPacket(new ServerPageListChangePacket(device, null, null, pageGroup, null, null, null));
                });
            }
            updateQueued = false;
        }
    }

    public void visibilityUpdated(PageGroupVisibilityProperty property) {
        if (autoUpdate) {
            iterateDevices(device->{
                if (property.get(device)) {
                    // The client will handle add packets as update packets
                    // if the component with this id already exists
                    device.sendPacket(new ServerPageListChangePacket(device, pageGroup, null, null, null, null, null));
                } else {
                    // The client will ignore remove packets
                    // if the component with this id does not exist
                    device.sendPacket(new ServerPageListChangePacket(device, null, pageGroup, null, null, null, null));
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
                    device.sendPacket(new ServerPageListChangePacket(device, null, null, pageGroup, null, null, null));
                });
            } else {
                targetDevice.sendPacket(new ServerPageListChangePacket(targetDevice, null, null, pageGroup, null, null, null));
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

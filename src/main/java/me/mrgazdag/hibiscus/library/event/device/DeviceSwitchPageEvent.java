package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.event.CallableEvent;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

@CallableEvent
public class DeviceSwitchPageEvent extends AbstractDeviceEvent {
    private final PageContext oldPage;
    private final PageContext requestedPage;
    private PageContext newPage;

    public DeviceSwitchPageEvent(ConnectedDevice device, PageContext oldPage, PageContext newPage) {
        super(device);
        this.oldPage = oldPage;
        this.requestedPage = newPage;
        this.newPage = newPage;
    }

    public PageContext getOldPage() {
        return oldPage;
    }

    public PageContext getRequestedPage() {
        return requestedPage;
    }

    public PageContext getNewPage() {
        return newPage;
    }

    public void setNewPage(PageContext newPage) {
        this.newPage = newPage;
    }

    @Override
    public String toString() {
        return "DeviceSwitchPageEvent{" +
                "oldPage=" + oldPage +
                ", requestedPage=" + requestedPage +
                ", newPage=" + newPage +
                ", device=" + device +
                '}';
    }
}

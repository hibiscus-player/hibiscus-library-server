package me.mrgazdag.hibiscus.library.ui;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.DeviceState;

import java.util.function.Consumer;
import java.util.function.Function;

public class UIManager {
    private final LibraryServer library;

    private Function<ConnectedDevice, Page> defaultPage;

    public UIManager(LibraryServer library) {
        this.library = library;
        this.defaultPage = null;
    }

    public void iterateDevices(Consumer<ConnectedDevice> action) {
        for (ConnectedDevice device : library.getWebsocketServer().getConnectedDevices()) {
            if (device.getState() == DeviceState.LOGGED_IN) {
                action.accept(device);
            }
        }
    }

    public Page getPageById(String key) {
        return library.getRegistry().getPageById(key);
    }

    public PageContext getPageContext(String sourceId) {
        return library.getRegistry().getPageContext(sourceId);
    }
    public PageGroup getPageGroup(String key) {
        return library.getRegistry().getPageGroup(key);
    }

    public Page getDefaultPage(ConnectedDevice device) {
        if (this.defaultPage == null) return null;
        return this.defaultPage.apply(device);
    }
    public void setDefaultPage(Page page) {
        this.defaultPage = device->page;
    }

    public void setDefaultPage(Function<ConnectedDevice, Page> defaultPage) {
        this.defaultPage = defaultPage;
    }

    public LibraryServer getLibrary() {
        return library;
    }
}

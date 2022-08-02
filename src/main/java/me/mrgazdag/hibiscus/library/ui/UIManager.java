package me.mrgazdag.hibiscus.library.ui;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.DeviceState;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class UIManager {
    private final LibraryServer library;
    private final Map<String, PageGroup> pageGroups;
    private final Map<String, Page> pages;

    private Function<ConnectedDevice, Page> defaultPage;

    public UIManager(LibraryServer library) {
        this.library = library;
        this.pageGroups = new LinkedHashMap<>();
        this.pages = new LinkedHashMap<>();
        this.defaultPage = null;
    }

    public void iterateDevices(Consumer<ConnectedDevice> action) {
        for (ConnectedDevice device : library.getWebsocketServer().getConnectedDevices()) {
            if (device.getState() == DeviceState.LOGGED_IN) {
                action.accept(device);
            }
        }
    }

    public void registerPageGroup(PageGroup group) {
        if (pageGroups.containsKey(group.getId())) throw new IllegalStateException("A page group with id \"" + group.getId() + "\" is already registered!");
        this.pageGroups.put(group.getId(), group);
        iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, group, null, null, null, null, null));
        });
    }
    public void unregisterPageGroup(PageGroup group) {
        this.pageGroups.remove(group.getId(), group);
        iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, null, group, null, null, null, null));
        });
    }

    public void registerPage(Page page) {
        if (page.isRegistered()) return;

        if (page.getGroup() == null) throw new IllegalStateException("Cannot register a page without PageGroup set");
        if (!page.getGroup().isRegistered()) throw new IllegalStateException("Cannot register a page without a registered PageGroup!");
        if (pages.containsKey(page.getPageId())) throw new IllegalStateException("A page with id \"" + page.getPageId() + "\" is already registered!");
        this.pages.put(page.getPageId(), page);
        iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, null, null, null, page, null, null));
        });
    }
    public void unregisterPage(Page page) {
        this.pages.remove(page.getPageId(), page);
        iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, page, null));
        });
    }

    public Page getPageById(String key) {
        return pages.get(key);
    }

    public PageContext getPageContext(String sourceId) {
        int index = sourceId.indexOf('/');
        if (index == -1) return pages.get(sourceId).getContext();
        return pages.get(sourceId.substring(0, index)).getContext(sourceId);
    }
    public PageGroup getPageGroup(String key) {
        return this.pageGroups.get(key);
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

    public ServerPageListChangePacket getInitialUIPacket(ConnectedDevice device) {
        return new ServerPageListChangePacket(device, pageGroups.values(), null, null, pages.values(), null, null);
    }

    public LibraryServer getLibrary() {
        return library;
    }
}

package me.mrgazdag.hibiscus.library.registry;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.plugin.Plugin;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrivateGlobalRegistry implements Registry {
    private final LibraryServer libraryServer;
    private final Map<String, PageGroup> pageGroups;
    private final Map<String, Page> pages;

    public PrivateGlobalRegistry(LibraryServer libraryServer) {
        this.libraryServer = libraryServer;
        this.pageGroups = new LinkedHashMap<>();
        this.pages = new LinkedHashMap<>();
    }

    @Override
    public LibraryServer getLibraryServer() {
        return libraryServer;
    }

    @Override
    public Page createPage(String id, String... idParameters) {
        return null;
    }

    @Override
    public PageGroup createPageGroup(String id) {
        return null;
    }

    @Override
    public void registerPage(Page page) {
        if (page.isRegistered()) return;

        if (page.getGroup() == null) throw new IllegalStateException("Cannot register a page without PageGroup set");
        if (!page.getGroup().isRegistered()) throw new IllegalStateException("Cannot register a page without a registered PageGroup!");
        if (pages.containsKey(page.getPageId())) throw new IllegalStateException("A page with id \"" + page.getPageId() + "\" is already registered!");
        this.pages.put(page.getPageId(), page);
        this.libraryServer.getUIManager().iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, null, null, null, page, null, null));
        });
    }

    @Override
    public void unregisterPage(Page page) {
        this.pages.remove(page.getPageId(), page);
        this.libraryServer.getUIManager().iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, page, null));
        });
    }

    @Override
    public void registerPageGroup(PageGroup group) {
        if (pageGroups.containsKey(group.getId())) throw new IllegalStateException("A page group with id \"" + group.getId() + "\" is already registered!");
        this.pageGroups.put(group.getId(), group);
        this.libraryServer.getUIManager().iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, group, null, null, null, null, null));
        });
    }

    @Override
    public void unregisterPageGroup(PageGroup group) {
        this.pageGroups.remove(group.getId(), group);
        this.libraryServer.getUIManager().iterateDevices(device->{
            device.sendPacket(new ServerPageListChangePacket(device, null, group, null, null, null, null));
        });
    }

    @Override
    public Page getPageById(String pageId) {
        return pages.get(pageId);
    }

    @Override
    public PageContext getPageContext(String sourceId) {
        int index = sourceId.indexOf('/');
        if (index == -1) return pages.get(sourceId).getContext();
        return pages.get(sourceId.substring(0, index)).getContext(sourceId);
    }

    public PageGroup getPageGroup(String key) {
        return this.pageGroups.get(key);
    }

    public PluginRegistry createPluginRegistry(Plugin plugin) {
        return new PluginRegistry(this, plugin);
    }

    public ServerPacket getInitialUIPacket(ConnectedDevice device) {
        return new ServerPageListChangePacket(device, pageGroups.values(), null, null, pages.values(), null, null);
    }
}

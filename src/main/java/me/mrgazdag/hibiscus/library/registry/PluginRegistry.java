package me.mrgazdag.hibiscus.library.registry;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.plugin.Plugin;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.util.HashMap;
import java.util.Map;

public class PluginRegistry implements Registry {
    private final PrivateGlobalRegistry global;
    private final Plugin plugin;
    private final Map<String, PageGroup> pageGroups;
    private final Map<String, Page> pages;
    private boolean cleanupActive;
    PluginRegistry(PrivateGlobalRegistry global, Plugin plugin) {
        this.global = global;
        this.plugin = plugin;
        this.pageGroups = new HashMap<>();
        this.pages = new HashMap<>();
        this.cleanupActive = false;
    }

    @Override
    public LibraryServer getLibraryServer() {
        return global.getLibraryServer();
    }

    @Override
    public Page createPage(String id, String... idParameters) {
        return new Page(this, id, idParameters);
    }

    @Override
    public PageGroup createPageGroup(String id) {
        return new PageGroup(this, id);
    }

    @Override
    public void registerPage(Page page) {
        global.registerPage(page);
        if (!cleanupActive) this.pages.put(page.getPageId(), page);
    }

    @Override
    public void unregisterPage(Page page) {
        global.unregisterPage(page);
        if (!cleanupActive) this.pages.remove(page.getPageId(), page);
    }

    @Override
    public void registerPageGroup(PageGroup group) {
        global.registerPageGroup(group);
        if (!cleanupActive) this.pageGroups.put(group.getId(), group);
    }

    @Override
    public void unregisterPageGroup(PageGroup group) {
        global.unregisterPageGroup(group);
        if (!cleanupActive) this.pageGroups.remove(group.getId(), group);
    }

    @Override
    public Page getPageById(String pageId) {
        return global.getPageById(pageId);
    }

    @Override
    public PageGroup getPageGroup(String key) {
        return global.getPageGroup(key);
    }

    @Override
    public PageContext getPageContext(String sourceId) {
        return global.getPageContext(sourceId);
    }

    @Override
    public ServerPacket getInitialUIPacket(ConnectedDevice device) {
        return global.getInitialUIPacket(device);
    }

    public void cleanup() {
        if (!plugin.isDisabled()) return;
        cleanupActive = true;
        for (Page page : pages.values()) {
            page.unregister();
        }
        for (PageGroup pageGroup : pageGroups.values()) {
            pageGroup.unregister();
        }
        cleanupActive = false;
    }
}

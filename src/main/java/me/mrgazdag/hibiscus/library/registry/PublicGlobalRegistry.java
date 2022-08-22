package me.mrgazdag.hibiscus.library.registry;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

public class PublicGlobalRegistry implements Registry {
    private final LibraryServer libraryServer;
    private final PrivateGlobalRegistry registry;

    public PublicGlobalRegistry(LibraryServer libraryServer, PrivateGlobalRegistry registry) {
        this.libraryServer = libraryServer;
        this.registry = registry;
    }

    @Override
    public LibraryServer getLibraryServer() {
        return libraryServer;
    }

    @Override
    public Page createPage(String id, String... idParameters) {
        return error();
    }

    @Override
    public PageGroup createPageGroup(String id) {
        return error();
    }

    public <T> T error() {
        throw new UnsupportedOperationException("Use Plugin#getRegistry()!");
    }

    @Override
    public void registerPage(Page page) {
        error();
    }

    @Override
    public void unregisterPage(Page page) {
        error();
    }

    @Override
    public void registerPageGroup(PageGroup group) {
        error();
    }

    @Override
    public void unregisterPageGroup(PageGroup group) {
        error();
    }

    @Override
    public Page getPageById(String pageId) {
        return registry.getPageById(pageId);
    }

    @Override
    public PageGroup getPageGroup(String key) {
        return registry.getPageGroup(key);
    }

    @Override
    public PageContext getPageContext(String sourceId) {
        return registry.getPageContext(sourceId);
    }

    @Override
    public ServerPacket getInitialUIPacket(ConnectedDevice device) {
        return registry.getInitialUIPacket(device);
    }
}

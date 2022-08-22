package me.mrgazdag.hibiscus.library.registry;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageContext;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

public interface Registry {
    LibraryServer getLibraryServer();
    Page createPage(String id, String...idParameters);
    PageGroup createPageGroup(String id);
    void registerPage(Page page);
    void unregisterPage(Page page);
    void registerPageGroup(PageGroup group);
    void unregisterPageGroup(PageGroup group);
    Page getPageById(String pageId);
    PageGroup getPageGroup(String key);
    PageContext getPageContext(String sourceId);
    ServerPacket getInitialUIPacket(ConnectedDevice device);
}
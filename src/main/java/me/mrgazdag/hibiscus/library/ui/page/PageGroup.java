package me.mrgazdag.hibiscus.library.ui.page;

import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.ui.change.PageGroupChangeHandler;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.visibility.PageGroupVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PageGroup {
    private static final String DEFAULT_NAME = "Unnamed Group";
    private static final boolean DEFAULT_VISIBILITY = true;
    private final UIManager uiManager;
    private final String id;
    private boolean registered;
    private final List<Page> pages;

    private final PageGroupChangeHandler changeHandler;
    private final StringProperty groupName;
    private final PageGroupVisibilityProperty visibility;

    public PageGroup(UIManager uiManager, String id) {
        this.uiManager = uiManager;
        this.id = id;
        this.registered = false;
        this.pages = new ArrayList<>();

        this.changeHandler = new PageGroupChangeHandler(uiManager, this);
        this.groupName = new StringProperty(null, (short) -1, changeHandler, DEFAULT_NAME);
        this.visibility = new PageGroupVisibilityProperty(this, changeHandler, DEFAULT_VISIBILITY);
    }

    public void register() {
        uiManager.registerPageGroup(this);
        registered = true;
    }
    public void unregister() {
        uiManager.unregisterPageGroup(this);
        registered = false;
    }
    public boolean isRegistered() {
        return registered;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public String getId() {
        return id;
    }

    protected void updatePage() {
        if (registered) uiManager.iterateDevices(device->{
            if (isVisible(device)) {
                device.sendPacket(new ServerPageListChangePacket(device, null, null, this, null, null, null));
            }
        });
    }

    public String getGroupName(ConnectedDevice device) {
        return groupName.get(device);
    }

    public StringProperty getGroupName() {
        return groupName;
    }

    public boolean isVisible(ConnectedDevice device) {
        return visibility.get(device);
    }

    public PageGroupVisibilityProperty getVisibility() {
        return visibility;
    }

    public void addPage(Page page) {
        if (!pages.contains(page)) {
            pages.add(page);
        }
    }
    public void removePage(Page page) {
        pages.remove(page);
    }

    public ByteBuffer serialize(ConnectedDevice device) {
        String groupName = getGroupName(device);

        int length = 4 + id.length()*2 + 4 + groupName.length()*2;
        ByteBuffer buffer = ByteBuffer.allocateDirect(length);
        buffer.putInt(id.length());
        buffer.put(id.getBytes(StandardCharsets.UTF_16BE));
        buffer.putInt(groupName.length());
        buffer.put(groupName.getBytes(StandardCharsets.UTF_16BE));

        return buffer;
    }
}

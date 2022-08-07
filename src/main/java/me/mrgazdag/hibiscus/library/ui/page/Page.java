package me.mrgazdag.hibiscus.library.ui.page;

import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.ui.change.PageChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.ButtonComponent;
import me.mrgazdag.hibiscus.library.ui.component.TextBoxComponent;
import me.mrgazdag.hibiscus.library.ui.component.TitleBoxComponent;
import me.mrgazdag.hibiscus.library.ui.component.UIComponent;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.visibility.PageVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerUpdatePagePacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Page {
    private static final String DEFAULT_NAME = "Unnamed Page";
    private static final String DEFAULT_ICON = PageIcons.MATERIAL_ARTICLE;
    private static final boolean DEFAULT_VISIBILITY = true;

    private final UIManager uiManager;
    private final String id;
    private final String[] idParameters;
    private final PageContext emptyContext;
    private boolean registered;
    private PageGroup group;
    private final Set<ConnectedDevice> activeDevices;

    // Components
    private final Map<Integer, UIComponent> components;
    private final AtomicInteger componentIdCounter;

    // Properties
    private final PageChangeHandler changeHandler;
    private StringProperty pageName;
    private StringProperty pageIcon;
    private PageVisibilityProperty visible;

    public Page(UIManager uiManager, String id, String...idParameters) {
        this.uiManager = uiManager;
        this.id = id;
        if (id.contains("/")) throw new IllegalStateException("id cannot contain '/'");
        this.idParameters = idParameters;
        this.emptyContext = new PageContext(this, id);
        this.registered = false;
        this.group = null;
        this.activeDevices = new HashSet<>();

        this.components = new LinkedHashMap<>();
        this.componentIdCounter = new AtomicInteger(0);

        this.changeHandler = new PageChangeHandler(uiManager, this);
        this.pageName = new StringProperty(null, (short) -1, changeHandler, DEFAULT_NAME);
        this.pageIcon = new StringProperty(null, (short) -1, changeHandler, DEFAULT_ICON);
        this.visible = new PageVisibilityProperty(this, changeHandler, DEFAULT_VISIBILITY);
    }
    public PageContext getContext() {
        return emptyContext;
    }
    public PageContext getContext(String sourceId) {
        String[] parts = sourceId.split("/");
        if (!parts[0].equals(this.id)) return null;

        Map<String, String> parameters = new HashMap<>(idParameters.length);
        for (int i = 0; i < idParameters.length; i++) {
            String key = idParameters[i];
            String value = parts.length > i+1 ? parts[i+1] : null;
            parameters.put(key, value);
        }
        return new PageContext(this, sourceId, parameters);
    }

    public void register() {
        uiManager.registerPage(this);
        registered = true;
    }
    public void unregister() {
        uiManager.unregisterPage(this);
        registered = false;
    }
    public boolean isRegistered() {
        return registered;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public String getPageId() {
        return id;
    }

    public Set<ConnectedDevice> getActiveDevices() {
        return activeDevices;
    }

    public void addDevice(ConnectedDevice device) {
        activeDevices.add(device);
        device.sendPacket(new ServerUpdatePagePacket(device, components.values(), null, null));
    }
    public void removeDevice(ConnectedDevice device) {
        activeDevices.remove(device);
    }


    protected void updatePage() {
        if (registered) uiManager.iterateDevices(device->{
            if (isVisible(device)) {
                device.sendPacket(new ServerPageListChangePacket(device, null, null, null, null, null, this));
            }
        });
    }
    public PageGroup getGroup() {
        return group;
    }
    public void setGroup(PageGroup group) {
        if (this.group != null) this.group.removePage(this);
        this.group = group;
        this.group.addPage(this);
        updatePage();
    }

    public String getPageName(ConnectedDevice device) {
        return pageName.get(device);
    }

    public StringProperty getPageName() {
        return pageName;
    }

    public String getPageIcon(ConnectedDevice device) {
        return pageIcon.get(device);
    }

    public StringProperty getPageIcon() {
        return pageIcon;
    }

    public boolean isVisible(ConnectedDevice device) {
        return visible.get(device);
    }

    public PageVisibilityProperty getVisibility() {
        return visible;
    }

    public void setAutoUpdate(boolean value) {
        changeHandler.setAutoUpdate(value);
    }
    public boolean isAutoUpdate() {
        return changeHandler.isAutoUpdate();
    }

    public ByteBuffer serialize(ConnectedDevice device) {
        boolean visible = isVisible(device);
        if (!visible) return null;
        String pageId = getPageId();
        String groupId = getGroup().getId();

        String pageName = getPageName(device);
        String pageIcon = getPageIcon(device);

        int length = 4 //Page ID Length (int)
                + pageId.length()*2 //Page ID
                + 4 //Group ID Length (int)
                + groupId.length()*2 //Group ID
                + 4 //Page Name Length (int)
                + pageName.length()*2 //Page Name
                + 4 //Page Icon Length (short)
                + pageIcon.length()*2; //Page Icon

        ByteBuffer buf = ByteBuffer.allocateDirect(length);
        buf.putInt(pageId.length());
        buf.put(pageId.getBytes(StandardCharsets.UTF_16BE));
        buf.putInt(groupId.length());
        buf.put(groupId.getBytes(StandardCharsets.UTF_16BE));
        buf.putInt(pageName.length());
        buf.put(pageName.getBytes(StandardCharsets.UTF_16BE));
        buf.putInt(pageIcon.length());
        buf.put(pageIcon.getBytes(StandardCharsets.UTF_16BE));

        return buf;
    }

    public Collection<UIComponent> getComponents() {
        return components.values();
    }
    public UIComponent getComponent(int id) {
        return components.get(id);
    }

    public void removeComponent(UIComponent component) {
        components.remove(component);
    }

    public TitleBoxComponent createTitleBox() {
        int id =  componentIdCounter.getAndIncrement();
        TitleBoxComponent component = new TitleBoxComponent(this, id);
        components.put(id, component);
        changeHandler.componentAdded(component);
        return component;
    }

    public TextBoxComponent createTextBox() {
        int id =  componentIdCounter.getAndIncrement();
        TextBoxComponent component = new TextBoxComponent(this, id);
        components.put(id, component);
        changeHandler.componentAdded(component);
        return component;
    }

    public ButtonComponent createButton() {
        int id =  componentIdCounter.getAndIncrement();
        ButtonComponent component = new ButtonComponent(this, id);
        components.put(id, component);
        changeHandler.componentAdded(component);
        return component;
    }
}

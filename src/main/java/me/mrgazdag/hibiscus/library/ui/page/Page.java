package me.mrgazdag.hibiscus.library.ui.page;

import me.mrgazdag.hibiscus.library.registry.PluginRegistry;
import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.ui.change.PageChangeHandler;
import me.mrgazdag.hibiscus.library.ui.component.*;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.visibility.PageVisibilityProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerPageListChangePacket;
import me.mrgazdag.hibiscus.library.users.networking.server.ServerUpdatePagePacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Page implements UIContainer {
    private static final String DEFAULT_NAME = "Unnamed Page";
    private static final String DEFAULT_ICON = PageIcons.MATERIAL_ARTICLE;
    private static final boolean DEFAULT_VISIBILITY = true;

    private final UIManager uiManager;
    private final PluginRegistry registry;
    private final String id;
    private final String[] idParameters;
    private final PageContext emptyContext;
    private boolean registered;
    private PageGroup group;
    private final Set<ConnectedDevice> activeDevices;

    // Components
    private final Map<Integer, UIComponent> componentIdMap;
    private final AtomicInteger componentIdCounter;
    private UIComponent rootElement;

    // Properties
    private final PageChangeHandler changeHandler;
    private StringProperty pageName;
    private StringProperty pageIcon;
    private PageVisibilityProperty visible;

    public Page(PluginRegistry registry, String id, String...idParameters) {
        this.uiManager = registry.getLibraryServer().getUIManager();
        this.registry = registry;
        this.id = id;
        if (id.contains("/")) throw new IllegalStateException("id cannot contain '/'");
        this.idParameters = idParameters;
        this.emptyContext = new PageContext(this, id);
        this.registered = false;
        this.group = null;
        this.activeDevices = new HashSet<>();

        this.componentIdMap = new LinkedHashMap<>();
        this.componentIdCounter = new AtomicInteger(1);

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
        registry.registerPage(this);
        registered = true;
    }
    public void unregister() {
        registry.unregisterPage(this);
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
        device.sendPacket(new ServerUpdatePagePacket(device, componentIdMap.values(), null, null, null));
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

    @Override
    public boolean areParentsVisible(ConnectedDevice device) {
        return true;
    }

    public void setRootElement(UIComponent rootElement) {
        setChildComponent(0, rootElement);
    }

    @Override
    public void clearChild(int childIndex) {
        if (childIndex != 0) throw new IllegalArgumentException("Cannot clear a child of Page other than the root element!");
        if (this.rootElement != null) changeHandler.componentRemoved(rootElement);
        this.rootElement = null;
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
        return componentIdMap.values();
    }
    public UIComponent getComponent(int id) {
        return componentIdMap.get(id);
    }

    public void removeComponent(UIComponent component) {
        componentIdMap.remove(component);
    }

    public UIComponent getRootElement() {
        return getChildComponent(0);
    }

    @Override
    public void setChildComponent(int id, UIComponent component) {
        if (id != 0) throw new IllegalArgumentException("Cannot set a child of Page other than the root element!");
        if (this.rootElement != null) {
            this.rootElement.updateParent(null, -1);
            changeHandler.componentRemoved(rootElement);
        }
        this.rootElement = component;
        if (component != null) {
            component.updateParent(this, 0);
            changeHandler.componentAdded(component);
        }
    }

    @Override
    public UIComponent getChildComponent(int id) {
        return id == 0 ? rootElement : null;
    }

    @Override
    public int getMaxChildrenCount() {
        return 1;
    }

    @Override
    public int getComponentId() {
        return 0;
    }

    public TitleBoxComponent createTitleBox() {
        int id = componentIdCounter.getAndIncrement();
        TitleBoxComponent component = new TitleBoxComponent(this, id);
        componentIdMap.put(id, component);
        return component;
    }
    public TextBoxComponent createTextBox() {
        int id = componentIdCounter.getAndIncrement();
        TextBoxComponent component = new TextBoxComponent(this, id);
        componentIdMap.put(id, component);
        return component;
    }

    public ButtonComponent createButton() {
        int id = componentIdCounter.getAndIncrement();
        ButtonComponent component = new ButtonComponent(this, id);
        componentIdMap.put(id, component);
        return component;
    }

    public TextInputComponent createTextInput() {
        int id = componentIdCounter.getAndIncrement();
        TextInputComponent component = new TextInputComponent(this, id);
        componentIdMap.put(id, component);
        return component;
    }

    public BlockLayoutComponent createBlockLayout() {
        int id = componentIdCounter.getAndIncrement();
        BlockLayoutComponent component = new BlockLayoutComponent(this, id);
        componentIdMap.put(id, component);
        return component;
    }
}

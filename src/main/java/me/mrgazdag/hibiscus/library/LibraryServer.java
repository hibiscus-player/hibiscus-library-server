package me.mrgazdag.hibiscus.library;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import me.mrgazdag.hibiscus.library.coreapi.CoreApi;
import me.mrgazdag.hibiscus.library.event.EventManager;
import me.mrgazdag.hibiscus.library.playback.PlaybackMixer;
import me.mrgazdag.hibiscus.library.plugin.JavaPluginManager;
import me.mrgazdag.hibiscus.library.plugin.Plugin;
import me.mrgazdag.hibiscus.library.registry.PrivateGlobalRegistry;
import me.mrgazdag.hibiscus.library.registry.PublicGlobalRegistry;
import me.mrgazdag.hibiscus.library.registry.Registry;
import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.users.ConnectedUser;
import me.mrgazdag.hibiscus.library.users.networking.WSServer;
import me.mrgazdag.hibiscus.library.users.permissions.PermissionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class LibraryServer {
    private final Map<UUID, PlaybackMixer> mixerMap;
    private final Map<String, ConnectedUser> userMap;

    private final Properties hibiscusProperties;
    private final APIVersion version;

    private final CoreApi coreApi;
    private final Path rootFolder;
    private final WSServer wsServer;
    private final Registry registry;
    private final UIManager uiManager;
    private final EventManager eventManager;
    private final PermissionManager permissionManager;
    private final JavaPluginManager pluginManager;

    public LibraryServer(Path rootFolder, CoreApi coreApi) {
        this.rootFolder = rootFolder;
        this.mixerMap = new HashMap<>();
        this.userMap = new HashMap<>();

        this.hibiscusProperties = loadProperties();
        this.version = APIVersion.fromString(hibiscusProperties.getProperty("hibiscus.version", null));

        this.coreApi = coreApi;
        this.wsServer = new WSServer(this);
        PrivateGlobalRegistry pgr = new PrivateGlobalRegistry(this);
        this.registry = new PublicGlobalRegistry(this, pgr);
        this.uiManager = new UIManager(this);
        this.eventManager = new EventManager(this);
        this.permissionManager = new PermissionManager(this, rootFolder.resolve("perms.json"));
        this.pluginManager = new JavaPluginManager(this, pgr, rootFolder.resolve("plugins"));
        try {
            this.pluginManager.tryCreatePluginsDirectory();
            this.pluginManager.loadAllPlugins();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.pluginManager.getPlugins().size() == 0) {
            // Load the default plugin
            System.out.println("Found no plugins, loading the default plugin...");
            Plugin plugin = loadDefaultPlugin();
            if (plugin != null) {
                pluginManager.enablePlugin(plugin);
                System.out.println("The default plugin has been loaded.");
            }
        }
    }
    private Properties loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream input = LibraryServer.class.getClassLoader().getResourceAsStream("hibiscus.properties");
            if (input != null) {
                prop.load(input);
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
    public Plugin loadDefaultPlugin() {
        String defaultVersion = hibiscusProperties.getProperty("hibiscus.default-plugin.version", null);
        if (defaultVersion == null || defaultVersion.equals("${default-plugin.version}")) {
            System.err.println("Could not load the default plugin: the default plugin version is missing!");
            return null;
        }
        try {
            String fileName = "hibiscus-default-plugin-" + defaultVersion + ".jar";
            InputStream input = LibraryServer.class.getClassLoader().getResourceAsStream(fileName);
            if (input == null) {
                System.err.println("Could not load the default plugin: could not find '" + fileName + "'!");
                return null;
            }
            Path destination = pluginManager.getPluginsFolder().resolve(fileName);
            OutputStream out = Files.newOutputStream(destination, StandardOpenOption.CREATE_NEW);
            input.transferTo(out);
            input.close();
            out.close();
            return pluginManager.loadPlugin(destination);
        } catch (IOException e) {
            System.err.println("Could not load the default plugin:");
            e.printStackTrace();
            return null;
        }
    }

    public CoreApi getCoreApi() {
        return coreApi;
    }

    public PlaybackMixer createMixer() {
        UUID uuid = UUID.randomUUID();
        PlaybackMixer mixer = new PlaybackMixer(uuid);
        mixerMap.put(uuid, mixer);
        return mixer;
    }

    public PlaybackMixer getMixer(UUID uuid) {
        return mixerMap.get(uuid);
    }

    public WSServer getWebsocketServer() {
        return wsServer;
    }

    public Registry getRegistry() {
        return registry;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public APIVersion getAPIVersion() {
        return version;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public JavaPluginManager getPluginManager() {
        return pluginManager;
    }

    public void hookInto(RoutingHandler handler) {
        handler.get("/websocket", Handlers.websocket((exchange1, channel) -> {
            channel.getReceiveSetter().set(wsServer);
            wsServer.onConnect(exchange1, channel);
            channel.resumeReceives();
        }));
    }
}

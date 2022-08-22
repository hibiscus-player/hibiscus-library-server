package me.mrgazdag.hibiscus.library.plugin;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.registry.PluginRegistry;
import me.mrgazdag.hibiscus.library.registry.Registry;

import java.io.PrintStream;
import java.nio.file.Path;

public class Plugin {
    LibraryServer libraryServer;
    Path dataFolder;
    PluginDescriptionFile description;
    PrintStream logger;
    PluginRegistry registry;
    boolean canEnable;
    private boolean disabled;

    public Plugin() {
        canEnable = true;
        disabled = true;
    }

    protected void onEnable() {}
    protected void onDisable() {}

    final void enable() {
        if (!canEnable) return;
        disabled = false;
        onEnable();
    }
    final void disable() {
        if (!canEnable) return;
        disabled = true;
        onDisable();
        registry.cleanup();
    }

    public final boolean isDisabled() {
        return disabled;
    }

    public final PrintStream getLogger() {
        return logger;
    }

    protected final Registry getRegistry() {
        return registry;
    }

    public final PluginDescriptionFile getDescriptionFile() {
        return description;
    }

    public final Path getDataFolder() {
        return dataFolder;
    }

    public final LibraryServer getLibraryServer() {
        return libraryServer;
    }
}

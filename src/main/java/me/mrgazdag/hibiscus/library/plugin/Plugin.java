package me.mrgazdag.hibiscus.library.plugin;

import me.mrgazdag.hibiscus.library.LibraryServer;

import java.io.PrintStream;
import java.nio.file.Path;

public class Plugin {
    LibraryServer libraryServer;
    Path dataFolder;
    PluginDescriptionFile description;
    PrintStream logger;
    boolean canEnable;
    private boolean disabled;

    public Plugin() {
        canEnable = true;
        disabled = true;
    }

    public void onEnable() {}
    public void onDisable() {}

    void enable() {
        if (!canEnable) return;
        disabled = false;
        onEnable();
    }
    void disable() {
        if (!canEnable) return;
        disabled = true;
        onDisable();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public PrintStream getLogger() {
        return logger;
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

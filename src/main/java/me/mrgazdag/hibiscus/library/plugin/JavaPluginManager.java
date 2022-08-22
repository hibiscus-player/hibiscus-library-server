package me.mrgazdag.hibiscus.library.plugin;

import me.mrgazdag.hibiscus.library.APIVersion;
import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.registry.PrivateGlobalRegistry;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaPluginManager {
    public static final Pattern NEWLINE_MATCHER = Pattern.compile("[\\n\\r]+");
    private final LibraryServer libraryServer;
    private final PrivateGlobalRegistry registry;
    private final Path pluginsFolder;
    private final HashMap<String, Plugin> plugins;
    private final List<Plugin> pluginList;

    public JavaPluginManager(LibraryServer libraryServer, PrivateGlobalRegistry pgr, Path pluginsFolder) {
        this.libraryServer = libraryServer;
        this.registry = pgr;
        this.pluginsFolder = pluginsFolder;
        this.plugins = new HashMap<>();
        this.pluginList = new ArrayList<>();
    }
    public void tryCreatePluginsDirectory() throws IOException {
        if (!Files.exists(pluginsFolder)) {
            Files.createDirectories(pluginsFolder);
        }
    }

    public Path getPluginsFolder() {
        return pluginsFolder;
    }
    public Plugin loadPlugin(Path path) {
        if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".jar")) {
            PluginClassLoader classLoader;
            try {
                classLoader = new PluginClassLoader(path);
            } catch (IOException | InvalidPluginDescriptionFileException | NoSuchMethodError e) {
                e.printStackTrace();
                return null;
            }

            PluginDescriptionFile descriptionFile = classLoader.getDescriptionFile();
            String prefix = "[" + descriptionFile.getId() + "] ";
            PrefixedPrintStream pp = new PrefixedPrintStream(System.out, prefix);
            pp.println("Loading plugin from jar " + pluginsFolder.relativize(path));
            try {
                Class<?> mainClass = classLoader.loadClass(descriptionFile.getMainClass());
                if (!Plugin.class.isAssignableFrom(mainClass)) {
                    new InvalidPluginDescriptionFileException(prefix + "Main class points to a class which does not extend Plugin!").fillInStackTrace().printStackTrace();
                    return null;
                }
                APIVersion apiVersion = descriptionFile.getApiVersion();
                if (apiVersion == null) {
                    System.err.println("WARNING: Plugin " + descriptionFile.getId() + " does not declare which version of the API it was compiled against!");
                    System.err.println("WARNING: We offer no backwards compatibility, watch out for major changes.");
                } else if (!apiVersion.isCompatible(libraryServer.getAPIVersion())) {
                    // API might not be compatible
                    System.err.println("WARNING: Plugin " + descriptionFile.getId() + " was made of an older version of the API, namely \"" + apiVersion + "\".");
                    System.err.println("WARNING: We offer no backwards compatibility, watch out for major changes.");
                }
                try {
                    Plugin plugin = mainClass.asSubclass(Plugin.class).getConstructor().newInstance();
                    plugin.libraryServer = libraryServer;
                    plugin.description = descriptionFile;
                    plugin.logger = pp;
                    plugin.registry = registry.createPluginRegistry(plugin);
                    plugins.put(descriptionFile.getId(), plugin);
                    classLoader.setPlugin(plugin);
                    return plugin;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    e.printStackTrace();
                    try {
                        classLoader.release();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                try {
                    classLoader.release();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (UnsupportedClassVersionError e) {
                System.err.println(prefix + "The plugin \"" + descriptionFile.getId() + "\" was compiled with a newer Java version! Unloading...");
                try {
                    classLoader.release();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public void loadAllPlugins() throws IOException {
        // Load all plugin jars
        Stream<Path> stream = Files.walk(pluginsFolder, 1, FileVisitOption.FOLLOW_LINKS);
        stream.forEach(this::loadPlugin);
        stream.close();

        if (plugins.size() != 0) {
            //System.out.println("Resolving dependencies");
            // then resolve dependencies, remove ones who are missing dependencies
            Iterator<Plugin> it = plugins.values().iterator();
            while (it.hasNext()) {
                Plugin m = it.next();
                String prefix = "[" + m.getDescriptionFile().getId() + "] ";
                //System.out.println(prefix + "Resolving dependencies...");
                String[] unknownPlugins = m.getDescriptionFile().resolveDependencies(plugins);
                //System.out.println(prefix + "Dependency resolving done.");
                if (unknownPlugins.length > 0) {
                    new MissingDependencyException(m.getDescriptionFile().getId(), unknownPlugins).fillInStackTrace().printStackTrace();
                    it.remove();
                    m.canEnable = false;
                }
            }
            pluginList.addAll(sortPlugins(new ArrayList<>(plugins.values())));
            System.out.println("Enabling plugins...");
            for (Plugin plugin : pluginList) {
                try {
                    if (plugin.canEnable) plugin.enable();
                } catch (Throwable t) {
                    t.printStackTrace();
                    plugin.disable();
                }
            }
            System.out.println("Successfully enabled all plugins.");
        }
    }

    public List<Plugin> sortPlugins(List<Plugin> plugins) {
        List<Plugin> bad = new ArrayList<>();
        List<Plugin> sortedPlugins = new ArrayList<>();
        for (Plugin plugin : plugins) {
            if (bad.contains(plugin)) continue;
            try {
                pluginSortRecursive(plugin, sortedPlugins, new ArrayList<>());
            } catch (LoopingDependencyException e) {
                e.printStackTrace();
                bad.addAll(e.getBadPlugins());
            }
        }
        return sortedPlugins;
    }
    private void pluginSortRecursive(Plugin plugin, List<Plugin> sortedPlugins, List<Plugin> used) throws LoopingDependencyException {
        if (sortedPlugins.contains(plugin)) return;
        if (used.contains(plugin)) {
            throw new LoopingDependencyException(used);
        }
        List<Plugin> usedCopy = new ArrayList<>(used);
        usedCopy.add(plugin);
        for (Plugin dependency : plugin.getDescriptionFile().getDependencies()) {
            pluginSortRecursive(dependency, sortedPlugins, usedCopy);
        }
        sortedPlugins.add(plugin);
    }

    public void unloadPlugin(Plugin plugin) {
        if (!plugin.isDisabled()) disablePlugin(plugin);
        ClassLoader loader = plugin.getClass().getClassLoader();
        if (loader instanceof PluginClassLoader pcl) {
            pluginList.remove(plugin);
            plugins.remove(plugin.getDescriptionFile().getId());
            plugin.canEnable = false;
            try {
                pcl.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Plugin " + plugin.getDescriptionFile().getId() + "'s classloader (" + loader.getClass().getName() + ") is not an instance of PluginClassLoader??");
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (plugin.isDisabled()) return;
        plugin.disable();
    }

    public void enablePlugin(Plugin plugin) {
        if (!plugin.isDisabled()) return;
        if (!plugin.canEnable) return;
        plugin.enable();
    }

    public Collection<Plugin> getPlugins() {
        return pluginList;
    }

    public Plugin getPlugin(String id) {
        return plugins.get(id);
    }
    /**
     * Disables all plugins.
     */
    public void disableAllPlugins() {
        System.out.println("Disabling all plugins...");
        List<Plugin> pluginsList = new ArrayList<>(getPlugins());
        Collections.reverse(pluginsList);
        for (Plugin plugin : pluginsList) {
            disablePlugin(plugin);
        }
        System.out.println("All plugins have been disabled.");
    }

    /**
     * Enables all plugins.
     */
    public void enableAllPlugins() {
        for (Plugin plugin : getPlugins()) {
            enablePlugin(plugin);
        }
    }

    /**
     * Unloads all {@link Plugin}s.
     */
    public void unloadAllPlugins() {
        System.out.println("Unloading all plugins...");
        List<Plugin> pluginsList = new ArrayList<>(getPlugins());
        Collections.reverse(pluginsList);
        for (Plugin plugin : pluginsList) {
            unloadPlugin(plugin);
        }
        System.out.println("All plugins have been unloaded.");
    }

    /**
     * Performs a full reload of all plugins. This action unloads existing plugins from memory.
     * This method is not supported, please restart instead.
     */
    public void reload() throws IOException {
        disableAllPlugins();
        unloadAllPlugins();
        loadAllPlugins();
        enableAllPlugins();
    }
}
package me.mrgazdag.hibiscus.library.plugin;

import java.util.List;

public class LoopingDependencyException extends Exception {
    private final List<Plugin> bad;

    public LoopingDependencyException(List<Plugin> bad) {
        super("Problematic plugins: " + toString(bad));
        this.bad = bad;
    }

    public List<Plugin> getBadPlugins() {
        return bad;
    }

    private static String toString(List<Plugin> plugins) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < plugins.size(); i++) {
            Plugin plugin = plugins.get(i);
            sb.append(plugin.getDescriptionFile().getId());
            if (i < plugins.size()-1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

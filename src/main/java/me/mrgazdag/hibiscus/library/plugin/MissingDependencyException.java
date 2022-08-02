package me.mrgazdag.hibiscus.library.plugin;

import java.util.Arrays;

public class MissingDependencyException extends Exception {
    public MissingDependencyException(String pluginName, String[] dependencies) {
        super("Plugin \"" + pluginName + "\" is missing the following dependencies: " + Arrays.toString(dependencies));
    }
}

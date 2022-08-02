package me.mrgazdag.hibiscus.library.plugin;

public class InvalidPluginDescriptionFileException extends Exception {
    public InvalidPluginDescriptionFileException(String message) {
        super(message);
    }
    public InvalidPluginDescriptionFileException(String message, Exception exc) {
        super(message, exc);
    }
    public InvalidPluginDescriptionFileException(Exception exc) {
        super(exc);
    }
}

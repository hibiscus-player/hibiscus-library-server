package me.mrgazdag.hibiscus.library.ui.color;

public interface Color {
    int getColorValue();

    default int getColorID() {
        return -1;
    }
    default int getAlpha() {
        return -1;
    }
    default int getRed() {
        return -1;
    }
    default int getGreen() {
        return -1;
    }
    default int getBlue() {
        return -1;
    }
}

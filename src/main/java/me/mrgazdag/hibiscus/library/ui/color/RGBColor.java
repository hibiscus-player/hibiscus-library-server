package me.mrgazdag.hibiscus.library.ui.color;

public class RGBColor implements Color {
    private final int value;

    public RGBColor(int argb) {
        this.value = argb;
    }

    public RGBColor(int red, int green, int blue, int alpha) {
        if (alpha == 0) {
            this.value = 0;
        } else {
            this.value = ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
        }
    }

    @Override
    public int getColorValue() {
        return value;
    }

    @Override
    public int getAlpha() {
        return value & 0xFF000000;
    }
    @Override
    public int getRed() {
        return value & 0x00FF0000;
    }
    @Override
    public int getGreen() {
        return value & 0x0000FF00;
    }
    @Override
    public int getBlue() {
        return value & 0x000000FF;
    }
}

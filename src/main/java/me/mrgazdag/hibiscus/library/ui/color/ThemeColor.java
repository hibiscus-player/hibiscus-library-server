package me.mrgazdag.hibiscus.library.ui.color;

public enum ThemeColor implements Color {
    BRIGHT_RED(1),
    BRIGHT_ORANGE(2),
    BRIGHT_YELLOW(3),
    BRIGHT_GREEN(4),
    BRIGHT_CYAN(5),
    BRIGHT_BLUE(6),
    BRIGHT_PURPLE(7),
    BRIGHT_MAGENTA(8),
    BRIGHT_GRAY(9),

    DARK_RED(10),
    DARK_ORANGE(11),
    DARK_YELLOW(12),
    DARK_GREEN(13),
    DARK_CYAN(14),
    DARK_BLUE(15),
    DARK_PURPLE(16),
    DARK_MAGENTA(17),
    DARK_GRAY(18),

    BACKGROUND(19),
    BACKGROUND_BRIGHTER(20),
    BACKGROUND_DARKER(21),
    BACKGROUND_EVEN_DARKER(22),
    PRIMARY(23),
    SECONDARY(24),
    TEXT(25);

    private final int colorId;
    ThemeColor(int colorId) {
        this.colorId = colorId;
    }

    @Override
    public int getColorID() {
        return colorId;
    }

    @Override
    public int getColorValue() {
        return colorId;
    }
}

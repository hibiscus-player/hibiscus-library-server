package me.mrgazdag.hibiscus.library.ui.page;

public class PageIcons {
    public static final String MATERIAL_HOME = material("home");
    public static final String MATERIAL_SEARCH = material("search");
    public static final String MATERIAL_LIBRARY_MUSIC = material("library_music");
    public static final String MATERIAL_PODCASTS = material("podcasts");
    public static final String MATERIAL_ARTICLE = material("article");

    public static String material(String name) {
        return "material:" + name;
    }
}

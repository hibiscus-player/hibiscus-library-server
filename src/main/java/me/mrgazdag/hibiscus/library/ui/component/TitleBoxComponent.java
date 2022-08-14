package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.color.ThemeColor;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.EnumProperty;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.ThemeColorProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public class TitleBoxComponent extends UIComponent {
    private final StringProperty titleText;
    private final StringProperty subtitleText;
    private final ThemeColorProperty backgroundColor;
    private final EnumProperty<TextAlignment> textAlignment;

    public TitleBoxComponent(Page page, int componentId) {
        super(page, componentId);
        this.titleText = stringProperty("Title");
        this.subtitleText = stringProperty("Subtitle");
        this.backgroundColor = themeColorProperty(ThemeColor.BACKGROUND);
        this.textAlignment = enumProperty(TextAlignment.class, TextAlignment.LEFT);
    }

    public StringProperty getTitleText() {
        return titleText;
    }
    public String getTitleText(ConnectedDevice device) {
        return titleText.get(device);
    }
    public StringProperty getSubtitleText() {
        return subtitleText;
    }
    public String getSubtitleText(ConnectedDevice device) {
        return subtitleText.get(device);
    }

    public ThemeColorProperty getBackgroundColor() {
        return backgroundColor;
    }
    public ThemeColor getBackgroundColor(ConnectedDevice device) {
        return backgroundColor.get(device);
    }

    public EnumProperty<TextAlignment> getTextAlignment() {
        return textAlignment;
    }
    public TextAlignment getTextAlignment(ConnectedDevice device) {
        return textAlignment.get(device);
    }

    @Override
    protected String getComponentTypeName() {
        return "title_box";
    }

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
}

package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.color.ThemeColor;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.ThemeColorProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public class TitleBoxComponent extends UIComponent {
    private final StringProperty titleText;
    private final ThemeColorProperty backgroundColor;

    public TitleBoxComponent(Page page, int componentId) {
        super(page, componentId);
        this.titleText = stringProperty("Title");
        this.backgroundColor = themeColorProperty(ThemeColor.BACKGROUND);
    }

    public StringProperty getTitleText() {
        return titleText;
    }
    public String getTitleText(ConnectedDevice device) {
        return titleText.get(device);
    }

    public ThemeColorProperty getBackgroundColor() {
        return backgroundColor;
    }
    public ThemeColor getBackgroundColor(ConnectedDevice device) {
        return backgroundColor.get(device);
    }

    @Override
    protected String getComponentTypeName() {
        return "title_box";
    }
}

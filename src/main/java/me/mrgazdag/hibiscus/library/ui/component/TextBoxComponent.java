package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.color.ThemeColor;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.ThemeColorProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public class TextBoxComponent extends UIComponent {
    private final StringProperty text;
    private final ThemeColorProperty backgroundColor;

    public TextBoxComponent(Page page, int componentId) {
        super(page, componentId);
        this.text = stringProperty("Text");
        this.backgroundColor = themeColorProperty(ThemeColor.BACKGROUND);
    }

    public StringProperty getText() {
        return text;
    }
    public String getText(ConnectedDevice device) {
        return text.get(device);
    }

    public ThemeColorProperty getBackgroundColor() {
        return backgroundColor;
    }
    public ThemeColor getBackgroundColor(ConnectedDevice device) {
        return backgroundColor.get(device);
    }

    @Override
    protected String getComponentTypeName() {
        return "text_box";
    }
}

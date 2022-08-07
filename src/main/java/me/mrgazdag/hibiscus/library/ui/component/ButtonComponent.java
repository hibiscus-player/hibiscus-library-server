package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.action.client.VoidClientPageAction;
import me.mrgazdag.hibiscus.library.ui.color.ThemeColor;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.StringProperty;
import me.mrgazdag.hibiscus.library.ui.property.ThemeColorProperty;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

public class ButtonComponent extends UIComponent {
    private final StringProperty buttonText;
    private final ThemeColorProperty backgroundColor;
    private final VoidClientPageAction onPress;

    public ButtonComponent(Page page, int componentId) {
        super(page, componentId);
        this.buttonText = stringProperty("Button");
        this.backgroundColor = themeColorProperty(ThemeColor.PRIMARY);

        this.onPress = voidClientAction();
    }

    public StringProperty getButtonText() {
        return buttonText;
    }
    public String getButtonText(ConnectedDevice device) {
        return buttonText.get(device);
    }

    public ThemeColorProperty getBackgroundColor() {
        return backgroundColor;
    }
    public ThemeColor getBackgroundColor(ConnectedDevice device) {
        return backgroundColor.get(device);
    }

    public VoidClientPageAction onPress() {
        return onPress;
    }

    @Override
    protected String getComponentTypeName() {
        return "button";
    }
}

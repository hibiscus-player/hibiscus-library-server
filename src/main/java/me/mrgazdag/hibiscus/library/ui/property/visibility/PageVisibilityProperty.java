package me.mrgazdag.hibiscus.library.ui.property.visibility;

import me.mrgazdag.hibiscus.library.ui.change.PageChangeHandler;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.property.BooleanProperty;

public class PageVisibilityProperty extends BooleanProperty {
    private final PageChangeHandler changeHandler;
    private final Page page;
    public PageVisibilityProperty(Page page, PageChangeHandler changeHandler, boolean defaultValue) {
        super(null, (short)-1, changeHandler, defaultValue);
        this.changeHandler = changeHandler;
        this.page = page;
    }

    @Override
    public void sendUpdate() {
        changeHandler.visibilityUpdated(this);
    }
}

package me.mrgazdag.hibiscus.library.ui.property.visibility;

import me.mrgazdag.hibiscus.library.ui.change.PageGroupChangeHandler;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.ui.property.BooleanProperty;

public class PageGroupVisibilityProperty extends BooleanProperty {
    private final PageGroupChangeHandler changeHandler;
    private final PageGroup pageGroup;
    public PageGroupVisibilityProperty(PageGroup pageGroup, PageGroupChangeHandler changeHandler, boolean defaultValue) {
        super(null, (short)-1, changeHandler, defaultValue);
        this.changeHandler = changeHandler;
        this.pageGroup = pageGroup;
    }

    @Override
    public void sendUpdate() {
        changeHandler.visibilityUpdated(this);
    }
}

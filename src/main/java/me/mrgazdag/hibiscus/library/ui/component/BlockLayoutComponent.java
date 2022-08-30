package me.mrgazdag.hibiscus.library.ui.component;

import me.mrgazdag.hibiscus.library.ui.page.Page;

public class BlockLayoutComponent extends UISimpleContainer {
    public BlockLayoutComponent(Page page, int componentId) {
        super(page, componentId, Integer.MAX_VALUE);
    }

    @Override
    protected String getComponentTypeName() {
        return "block_layout";
    }
}

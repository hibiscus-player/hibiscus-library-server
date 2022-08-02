package me.mrgazdag.hibiscus.library.ui.page;

import java.util.Collections;
import java.util.Map;

public record PageContext(Page page, String sourcePageId, Map<String, String> parameters) {
    private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

    public PageContext(Page page, String sourcePageId) {
        this(page, sourcePageId, EMPTY_MAP);
    }

    public String parameter(String key) {
        return parameters.get(key);
    }
}

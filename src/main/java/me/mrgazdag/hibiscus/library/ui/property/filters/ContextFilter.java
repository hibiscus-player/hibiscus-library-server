package me.mrgazdag.hibiscus.library.ui.property.filters;

import me.mrgazdag.hibiscus.library.ui.page.PageContext;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ContextFilter<T> extends Predicate<PageContext>, Function<PageContext, T> {
    @Override
    default boolean test(PageContext context) {
        return true;
    }

    @Override
    T apply(PageContext context);
}

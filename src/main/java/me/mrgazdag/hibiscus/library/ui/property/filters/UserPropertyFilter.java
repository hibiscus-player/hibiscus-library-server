package me.mrgazdag.hibiscus.library.ui.property.filters;

import me.mrgazdag.hibiscus.library.users.ConnectedUser;

import java.util.function.Function;
import java.util.function.Predicate;

public interface UserPropertyFilter<T> extends Predicate<ConnectedUser>, Function<ConnectedUser, T> {
    @Override
    default boolean test(ConnectedUser user) {
        return true;
    }

    @Override
    T apply(ConnectedUser user);
}

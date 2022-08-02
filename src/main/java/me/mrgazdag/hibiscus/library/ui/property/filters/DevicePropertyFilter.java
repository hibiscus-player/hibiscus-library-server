package me.mrgazdag.hibiscus.library.ui.property.filters;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.util.function.Function;
import java.util.function.Predicate;

public interface DevicePropertyFilter<T> extends Predicate<ConnectedDevice>, Function<ConnectedDevice, T> {
    @Override
    default boolean test(ConnectedDevice device) {
        return true;
    }

    @Override
    T apply(ConnectedDevice device);
}

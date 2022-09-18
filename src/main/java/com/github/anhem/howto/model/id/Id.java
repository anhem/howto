package com.github.anhem.howto.model.id;

public interface Id<T> {

    Integer NEW_INT = 0;

    T value();

    default boolean isNew() {
        T value = value();
        if (value instanceof Integer) {
            return value.equals(NEW_INT);
        }
        throw new IllegalStateException(String.format("missing implementation for %s", value.getClass().getSimpleName()));
    }
}

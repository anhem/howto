package com.github.anhem.howto.model.id;

import java.lang.reflect.Method;

public interface Id<T> {
    String BUILDER_METHOD_NAME = "builder";
    String BUILD_METHOD_NAME = "build";
    String VALUE_METHOD_NAME = "value";
    Integer NEW_INT = 0;

    T getValue();

    default boolean isNew() {
        T value = getValue();
        if (value instanceof Integer) {
            return value.equals(NEW_INT);
        }
        throw new IllegalStateException(String.format("missing implementation for %s", value.getClass().getSimpleName()));
    }

    @SuppressWarnings("unchecked")
    static <C extends Id<V>, V> C of(Class<C> clazz, V value) {
        try {
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD_NAME).invoke(null);
            Method valueMethod = builderObject.getClass().getDeclaredMethod(VALUE_METHOD_NAME, value.getClass());
            valueMethod.invoke(builderObject, value);
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD_NAME);
            return (C) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.anhem.howto.testutil;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;

import java.util.Map;

public class TestPopulator {

    private static final PopulateConfig POPULATE_CONFIG = PopulateConfig.builder()
            .builderStrategy()
            .pattern(BuilderPattern.LOMBOK)
            .and()
            .constructorStrategy()
            .build();

    private static final PopulateConfig TEST = PopulateConfig.builder()
            .clearStrategies()
            .build();

    private static final PopulateFactory POPULATE_FACTORY = new PopulateFactory(POPULATE_CONFIG);

    public static <T> T populate(Class<T> clazz) {
        return POPULATE_FACTORY.populate(clazz);
    }

    public static <T, U> T populate(Class<T> clazz, Class<U> overrideClass, OverridePopulate<U> overridePopulate) {
        return POPULATE_FACTORY.populate(clazz, overrideClass, overridePopulate);
    }

    public static <T> T populate(Class<T> clazz, String overrideName, Class<?> overrideClass, OverridePopulate<?> overridePopulate) {
        return POPULATE_FACTORY.populate(clazz, overrideName, overrideClass, overridePopulate);
    }

    public static <T> T populate(Class<T> clazz, Map<?, OverridePopulate<?>> overrides) {
        return POPULATE_FACTORY.populate(clazz, overrides);
    }
}

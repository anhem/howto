package com.github.anhem.howto.testutil;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;

public class TestPopulator {

    public static <T> T populate(Class<T> clazz) {
        return populateFactory.populate(clazz);
    }

    private static final PopulateConfig populateConfig = PopulateConfig.builder()
            .strategyOrder(Strategy.BUILDER)
            .builderPattern(BuilderPattern.LOMBOK)
            .build();

    private static final PopulateFactory populateFactory = new PopulateFactory(populateConfig);
}

package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Id;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

class IdTest {

    @Test
    void isNewInteger() {
        assertThat(new AccountId(0).isNew()).isTrue();
        assertThat(new AccountId(1).isNew()).isFalse();
    }

    @Test
    void isNewBooleanThrowsException() {
        assertThatThrownBy(() -> new BooleanId(true).isNew()).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("missing implementation for Boolean");
    }

    @Test
    void nonNullIsEnforcedOnAllIdClasses() {
        findIdClasses().forEach(IdTest::assertNullChecksAreEnforced);
    }


    @SuppressWarnings("rawtypes")
    private static void assertNullChecksAreEnforced(Class<? extends Id> idClass) {
        Arrays.stream(idClass.getDeclaredConstructors()).forEach(constructor -> {
            try {
                constructor.newInstance(createNullArguments(constructor));
                fail(String.format("class %s is not enforcing null checks", idClass.getName()));
            } catch (InvocationTargetException ite) {
                assertThat(ite.getTargetException()).isInstanceOf(NullPointerException.class);
            } catch (Exception e) {
                fail(e);
            }
        });
    }

    private static Object[] createNullArguments(Constructor<?> constructor) {
        return IntStream.range(0, constructor.getParameters().length)
                .mapToObj(i -> null)
                .toArray();
    }

    @SuppressWarnings("rawtypes")
    private Set<Class<? extends Id>> findIdClasses() {
        Reflections reflections = new Reflections(this.getClass().getPackageName());
        return reflections.getSubTypesOf(Id.class);
    }

    private static class BooleanId implements Id<Boolean> {

        private final boolean value;

        private BooleanId(Boolean value) {
            if (value == null) {
                throw new NullPointerException("");
            }
            this.value = value;
        }

        @Override
        public Boolean value() {
            return value;
        }
    }
}
package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdTest {

    @Test
    public void isNewInteger() {
        assertThat(AccountId.of(0).isNew()).isTrue();
        assertThat(AccountId.of(1).isNew()).isFalse();
    }
}
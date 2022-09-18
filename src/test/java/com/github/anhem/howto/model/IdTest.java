package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdTest {

    @Test
    public void isNewInteger() {
        assertThat(Id.of(AccountId.class, 0).isNew()).isTrue();
        assertThat(Id.of(AccountId.class, 1).isNew()).isFalse();
    }
}
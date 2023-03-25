package com.github.anhem.howto.model.id;

import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.model.id.RoleName.ADMINISTRATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleNameTest {

    @Test
    public void allRoleNameValuesShouldMatchEnumNameAndPrefix() {
        assertThat(RoleName.values()).allMatch(roleName -> String.format("ROLE_%s", roleName.name()).equals(roleName.getValue()));
    }

    @Test
    public void fromStringReturnsRoleName() {
        assertThat(RoleName.fromString(ADMINISTRATOR.name())).isEqualTo(ADMINISTRATOR);
    }

    @Test
    public void fromStringThrowsExceptionWhenInvalidRoleName() {
        assertThatThrownBy(() -> RoleName.fromString("invalidRoleName")).isInstanceOf(RuntimeException.class);
    }

}
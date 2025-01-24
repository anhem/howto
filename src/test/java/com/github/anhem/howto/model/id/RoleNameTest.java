package com.github.anhem.howto.model.id;

import com.github.anhem.howto.model.RoleName;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.model.RoleName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleNameTest {

    @Test
    void allRoleNameValuesShouldMatchEnumNameAndPrefix() {
        assertThat(RoleName.values()).allMatch(roleName -> String.format("ROLE_%s", roleName.name()).equals(roleName.getRole()));
    }

    @Test
    void fromNameReturnsRoleName() {
        assertThat(RoleName.fromName(ADMINISTRATOR.name())).isEqualTo(ADMINISTRATOR);
    }

    @Test
    void fromNameThrowsExceptionWhenInvalidRoleName() {
        assertThatThrownBy(() -> RoleName.fromName("invalidRoleName")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void fromRoleReturnsRoleName() {
        assertThat(RoleName.fromRole(ADMINISTRATOR.getRole())).isEqualTo(ADMINISTRATOR);
    }

    @Test
    void fromRoleThrowsExceptionWhenInvalidRoleName() {
        assertThatThrownBy(() -> RoleName.fromRole("invalidRole")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void valueReturnsEnumName() {
        assertThat(ADMINISTRATOR.value()).isEqualTo(ADMINISTRATOR.name());
        assertThat(MODERATOR.value()).isEqualTo(MODERATOR.name());
        assertThat(USER.value()).isEqualTo(USER.name());
    }

}
package com.github.anhem.howto.model.id;

import com.github.anhem.howto.model.RoleName;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.model.RoleName.ADMINISTRATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleNameTest {

    @Test
    public void allRoleNameValuesShouldMatchEnumNameAndPrefix() {
        assertThat(RoleName.values()).allMatch(roleName -> String.format("ROLE_%s", roleName.name()).equals(roleName.getRole()));
    }

    @Test
    public void fromStringReturnsRoleName() {
        assertThat(RoleName.fromName(ADMINISTRATOR.name())).isEqualTo(ADMINISTRATOR);
    }

    @Test
    public void fromStringThrowsExceptionWhenInvalidRoleName() {
        assertThatThrownBy(() -> RoleName.fromName("invalidRoleName")).isInstanceOf(RuntimeException.class);
    }

}
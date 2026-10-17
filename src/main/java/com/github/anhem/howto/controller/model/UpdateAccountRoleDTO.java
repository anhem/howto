package com.github.anhem.howto.controller.model;

import com.github.anhem.howto.model.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateAccountRoleDTO {

    @NotNull
    RoleName role;
}

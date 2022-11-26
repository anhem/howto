package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.RoleId;
import com.github.anhem.howto.model.id.RoleName;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Role {

    @NonNull
    RoleId roleId;
    @NonNull
    RoleName roleName;
    @NonNull
    String description;
    @NonNull
    Instant created;
    @NonNull
    Instant lastUpdated;
}

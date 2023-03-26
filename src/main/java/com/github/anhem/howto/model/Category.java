package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.CategoryId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Category {

    @NonNull
    CategoryId categoryId;
    @NonNull
    String name;
    String description;
    @NonNull
    Instant created;
    @NonNull
    Instant lastUpdated;

}

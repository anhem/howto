package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.UpsertCategoryDTO;
import com.github.anhem.howto.model.Category;

import java.time.Instant;

import static com.github.anhem.howto.model.id.CategoryId.NEW_CATEGORY_ID;

public class UpsertCategoryDTOMapper {

    public static Category mapToCategory(UpsertCategoryDTO upsertCategoryDTO) {
        Instant now = Instant.now();
        return Category.builder()
                .categoryId(NEW_CATEGORY_ID)
                .name(upsertCategoryDTO.getName())
                .description(upsertCategoryDTO.getDescription())
                .created(now)
                .lastUpdated(now)
                .build();
    }

    public static Category mapToCategory(UpsertCategoryDTO upsertCategoryDTO, Category category) {
        return category.toBuilder()
                .name(upsertCategoryDTO.getName())
                .description(upsertCategoryDTO.getDescription())
                .lastUpdated(Instant.now())
                .build();
    }
}

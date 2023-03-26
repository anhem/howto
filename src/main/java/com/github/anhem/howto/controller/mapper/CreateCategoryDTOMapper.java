package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreateCategoryDTO;
import com.github.anhem.howto.model.Category;

import java.time.Instant;

import static com.github.anhem.howto.model.id.CategoryId.NEW_CATEGORY_ID;

public class CreateCategoryDTOMapper {

    public static Category mapToCategory(CreateCategoryDTO createCategoryDTO) {
        Instant now = Instant.now();
        return Category.builder()
                .categoryId(NEW_CATEGORY_ID)
                .name(createCategoryDTO.getName())
                .description(createCategoryDTO.getDescription())
                .created(now)
                .lastUpdated(now)
                .build();
    }
}

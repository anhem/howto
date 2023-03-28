package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.UpsertCategoryDTO;
import com.github.anhem.howto.model.Category;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.controller.mapper.UpsertCategoryDTOMapper.mapToCategory;
import static com.github.anhem.howto.model.id.CategoryId.NEW_CATEGORY_ID;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class UpsertCategoryDTOMapperTest {

    @Test
    void mappedToNewModel() {
        UpsertCategoryDTO upsertCategoryDTO = populate(UpsertCategoryDTO.class);

        Category category = mapToCategory(upsertCategoryDTO);

        assertThat(category).hasNoNullFieldsOrProperties();
        assertThat(category.getCategoryId()).isEqualTo(NEW_CATEGORY_ID);
    }

    @Test
    void mappedToExistingModel() {
        UpsertCategoryDTO upsertCategoryDTO = populate(UpsertCategoryDTO.class);
        Category category = populate(Category.class);

        Category updatedCategory = mapToCategory(upsertCategoryDTO, category);

        assertThat(updatedCategory).hasNoNullFieldsOrProperties();
    }

}
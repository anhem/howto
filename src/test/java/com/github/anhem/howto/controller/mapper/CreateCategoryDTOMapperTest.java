package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreateCategoryDTO;
import com.github.anhem.howto.model.Category;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class CreateCategoryDTOMapperTest {

    @Test
    void mappedToModel() {
        CreateCategoryDTO createCategoryDTO = populate(CreateCategoryDTO.class);

        Category category = CreateCategoryDTOMapper.mapToCategory(createCategoryDTO);

        assertThat(category).hasNoNullFieldsOrProperties();
    }

}
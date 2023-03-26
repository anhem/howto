package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CategoryDTO;
import com.github.anhem.howto.model.Category;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.howto.controller.mapper.CategoryDTOMapper.mapToCategoryDTOs;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class CategoryDTOMapperTest {

    @Test
    void mappedToDTO() {
        Category category = populate(Category.class);

        List<CategoryDTO> categoryDTOs = mapToCategoryDTOs(List.of(category));

        assertThat(categoryDTOs).hasSize(1);
        assertThat(categoryDTOs.get(0)).hasNoNullFieldsOrProperties();
    }

}
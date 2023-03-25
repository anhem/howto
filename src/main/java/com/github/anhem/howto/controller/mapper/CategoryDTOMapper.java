package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CategoryDTO;
import com.github.anhem.howto.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryDTOMapper {

    public static List<CategoryDTO> mapToCategoryDTOs(List<Category> categories) {
        return categories.stream()
                .map(CategoryDTOMapper::mapToCategoryDTO)
                .collect(Collectors.toList());
    }

    private static CategoryDTO mapToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId().value())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}

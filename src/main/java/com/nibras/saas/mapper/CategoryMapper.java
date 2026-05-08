package com.nibras.saas.mapper;

import com.nibras.saas.dto.request.CategoryRequest;
import com.nibras.saas.dto.response.CategoryResponse;
import com.nibras.saas.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public Category toEntity(final CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public CategoryResponse toResponse(final Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

}

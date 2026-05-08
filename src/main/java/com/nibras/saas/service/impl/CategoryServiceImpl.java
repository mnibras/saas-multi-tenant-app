package com.nibras.saas.service.impl;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.CategoryRequest;
import com.nibras.saas.dto.response.CategoryResponse;
import com.nibras.saas.entity.Category;
import com.nibras.saas.mapper.CategoryMapper;
import com.nibras.saas.repository.CategoryRepository;
import com.nibras.saas.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void create(CategoryRequest request) {
        checkIfCategoryExists(request.getName());
        final Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
    }

    @Override
    public void update(final String id, final CategoryRequest request) {
        // check if category already exists by ID
        final Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            log.debug("Category does not exist with id '{}'", id);
            throw new EntityNotFoundException("Category does not exist with id '" + id + "'");
        }

        if (!existingCategory.get().getName().equalsIgnoreCase(request.getName())) {
            checkIfCategoryExists(request.getName());
        }

        final Category categoryToUpdate = categoryMapper.toEntity(request);
        categoryToUpdate.setId(id);
        categoryRepository.save(categoryToUpdate);
    }

    @Override
    public CategoryResponse findById(final String id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Category does not exist"));
    }

    @Override
    public PageResponse<CategoryResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Category> categories = this.categoryRepository.findAll(pageRequest);
        final Page<CategoryResponse> categoryResponses = categories.map(this.categoryMapper::toResponse);
        return PageResponse.of(categoryResponses);
    }

    @Override
    public void delete(final String id) {
        final Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category does not exist"));
        categoryRepository.delete(category);
    }

    private void checkIfCategoryExists(final String name) {
        final Optional<Category> optionalCategory = categoryRepository.findByNameIgnoreCase(name);
        if (optionalCategory.isPresent()) {
            log.debug("Category with name '{}' already exists", name);
            throw new EntityNotFoundException("Category with name '" + name + "' already exists");
        }
    }
}

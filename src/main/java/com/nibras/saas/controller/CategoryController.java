package com.nibras.saas.controller;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.CategoryRequest;
import com.nibras.saas.dto.response.CategoryResponse;
import com.nibras.saas.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid final CategoryRequest request) {
        categoryService.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{category-id}")
    public ResponseEntity<Void> updateCategory(@RequestBody @Valid final CategoryRequest request,
                                               @PathVariable("category-id") @NotNull(message = "Category ID cannot be null") final String id) {
        categoryService.update(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> findAllCategories(@RequestParam(name = "page", defaultValue = "0") final int page,
                                                                            @RequestParam(name = "size", defaultValue = "10") final int size
    ) {
        return ResponseEntity.ok(this.categoryService.findAll(page, size));
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable("category-id")
                                                             @NotNull(message = "Category ID cannot be null") final String id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category-id") @NotNull(message = "Category ID cannot be null") final String id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

package com.nibras.saas.service.impl;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.ProductRequest;
import com.nibras.saas.dto.response.ProductResponse;
import com.nibras.saas.entity.Category;
import com.nibras.saas.entity.Product;
import com.nibras.saas.mapper.ProductMapper;
import com.nibras.saas.repository.CategoryRepository;
import com.nibras.saas.repository.ProductRepository;
import com.nibras.saas.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public void create(final ProductRequest request) {
        checkIfProductAlreadyExistsByReference(request.getReference());
        checkIfCategoryExistById(request.getCategoryId());

        final Product entity = this.productMapper.toEntity(request);
        this.productRepository.save(entity);
    }

    @Override
    public void update(final String id, final ProductRequest request) {
        final Optional<Product> productExists = this.productRepository.findById(id);
        if (productExists.isEmpty()) {
            log.debug("Product does not exist with id '{}'", id);
            throw new EntityNotFoundException("Product does not exist with id '" + id + "'");
        }

        if (!productExists.get().getReference().equalsIgnoreCase(request.getReference())) {
            checkIfProductAlreadyExistsByReference(request.getReference());
        }

        checkIfCategoryExistById(request.getCategoryId());

        final Product productToUpdate = this.productMapper.toEntity(request);
        productToUpdate.setId(id);
        this.productRepository.save(productToUpdate);
    }

    @Override
    public PageResponse<ProductResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Product> products = this.productRepository.findAll(pageRequest);
        final Page<ProductResponse> productResponses = products.map(this.productMapper::toResponse);
        return PageResponse.of(productResponses);
    }

    @Override
    public ProductResponse findById(final String id) {
        return this.productRepository.findById(id)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist with id '" + id + "'"));
    }

    @Override
    public void delete(final String id) {
        final Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist with id '" + id + "'"));
        this.productRepository.delete(product);

    }

    private void checkIfProductAlreadyExistsByReference(final String reference) {
        final Optional<Product> product = this.productRepository.findByReferenceIgnoreCase(reference);
        if (product.isPresent()) {
            log.debug("Product already exists with reference '{}'", reference);
            throw new RuntimeException("Product already exists with reference '" + reference + "'");
        }
    }

    private void checkIfCategoryExistById(final String categoryId) {
        final Optional<Category> category = this.categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.debug("Category does not exist with id '{}'", categoryId);
            throw new EntityNotFoundException("Category does not exist with id '" + categoryId + "'");
        }
    }

}
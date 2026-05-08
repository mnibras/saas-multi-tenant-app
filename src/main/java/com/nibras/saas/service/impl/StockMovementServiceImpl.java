package com.nibras.saas.service.impl;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.StockMovementRequest;
import com.nibras.saas.dto.response.StockMovementResponse;
import com.nibras.saas.entity.Product;
import com.nibras.saas.entity.StockMovement;
import com.nibras.saas.mapper.StockMovementMapper;
import com.nibras.saas.repository.ProductRepository;
import com.nibras.saas.repository.StockMovementRepository;
import com.nibras.saas.service.StockMovementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final StockMovementMapper stockMovementMapper;

    @Override
    public void create(final StockMovementRequest request) {
        checkIfProductExistsById(request.getProductId());

        final StockMovement entity = this.stockMovementMapper.toEntity(request);
        entity.setDate(LocalDate.now());
        this.stockMovementRepository.save(entity);
    }

    @Override
    public void update(final String id, final StockMovementRequest request) {
        final Optional<StockMovement> stockMvt = this.stockMovementRepository.findById(id);
        if (stockMvt.isEmpty()) {
            log.debug("StockMovement does not exist with id '{}'", id);
            throw new EntityNotFoundException("StockMovement does not exist with id '" + id + "'");
        }

        checkIfProductExistsById(request.getProductId());

        final StockMovement stockMvtToUpdate = this.stockMovementMapper.toEntity(request);
        stockMvtToUpdate.setDate(LocalDate.now());
        stockMvtToUpdate.setId(id);
        this.stockMovementRepository.save(stockMvtToUpdate);
    }

    @Override
    public PageResponse<StockMovementResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMovement> stockMovements = this.stockMovementRepository.findAll(pageRequest);
        final Page<StockMovementResponse> stockMvtResponses = stockMovements.map(this.stockMovementMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    @Override
    public StockMovementResponse findById(final String id) {
        return this.stockMovementRepository.findById(id)
                .map(this.stockMovementMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("StockMovement with id '" + id + "' does not exist"));
    }

    @Override
    public void delete(final String id) {
        final StockMovement stockMvt = this.stockMovementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockMovement with id '" + id + "' does not exist"));
        this.stockMovementRepository.delete(stockMvt);

    }

    @Override
    public PageResponse<StockMovementResponse> findAllByProductId(final String productId, final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMovement> stockMovements = this.stockMovementRepository.findAllByProductId(productId, pageRequest);
        final Page<StockMovementResponse> stockMvtResponses = stockMovements.map(this.stockMovementMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    private void checkIfProductExistsById(final String productId) {
        final Optional<Product> product = this.productRepository.findById(productId);
        if (product.isEmpty()) {
            log.debug("Product does not exist with id '{}'", productId);
            throw new EntityNotFoundException("Product does not exist with id '" + productId + "'");
        }
    }

}

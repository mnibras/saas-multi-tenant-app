package com.nibras.saas.controller;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.StockMovementRequest;
import com.nibras.saas.dto.response.StockMovementResponse;
import com.nibras.saas.service.StockMovementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    public ResponseEntity<Void> createStockMvt(@RequestBody @Valid final StockMovementRequest request) {
        this.stockMovementService.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{stock-mvt-id}")
    public ResponseEntity<Void> updateStockMvt(@RequestBody
                                               @Valid final StockMovementRequest request,
                                               @PathVariable("stock-mvt-id")
                                               @NotNull(message = "Stock Mvt ID cannot be null") final String id
    ) {
        this.stockMovementService.update(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{stock-mvt-id}")
    public ResponseEntity<StockMovementResponse> findStockMvtById(@PathVariable("stock-mvt-id")
                                                                  @NotNull(message = "Stock Mvt ID cannot be null") final String id) {
        return ResponseEntity.ok(this.stockMovementService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<StockMovementResponse>> findAllStockMovements(
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "10") final int size) {
        return ResponseEntity.ok(this.stockMovementService.findAll(page, size));
    }

    @GetMapping("/product/{product-id}")
    public ResponseEntity<PageResponse<StockMovementResponse>> findAllStockMovementsByProductId(
            @PathVariable("product-id")
            @NotNull(message = "Product ID cannot be null") final String productId,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "10") final int size) {
        return ResponseEntity.ok(this.stockMovementService.findAllByProductId(productId, page, size));
    }

    @DeleteMapping("/{stock-mvt-id}")
    public ResponseEntity<Void> deleteStockMvt(@PathVariable("stock-mvt-id")
                                               @NotNull(message = "Stock Mvt ID cannot be null") final String id) {
        this.stockMovementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

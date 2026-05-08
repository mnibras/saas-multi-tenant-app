package com.nibras.saas.controller;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.ProductRequest;
import com.nibras.saas.dto.response.ProductResponse;
import com.nibras.saas.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody @Valid final ProductRequest request) {
        this.productService.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{product-id}")
    public ResponseEntity<Void> updateProduct(@RequestBody @Valid final ProductRequest request,
                                              @PathVariable("product-id") @NotNull(message = "Product ID cannot be null") String id) {
        this.productService.update(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable("product-id") @NotNull(message = "Product ID cannot be null") String id) {
        return ResponseEntity.ok(this.productService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAllProducts(@RequestParam(name = "page", defaultValue = "0") final int page,
                                                                         @RequestParam(name = "size", defaultValue = "10") final int size) {
        return ResponseEntity.ok(this.productService.findAll(page, size));
    }

    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("product-id")
                                              @NotNull(message = "Product ID cannot be null") final String id) {
        this.productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

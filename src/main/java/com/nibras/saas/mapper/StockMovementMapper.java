package com.nibras.saas.mapper;

import com.nibras.saas.dto.request.StockMovementRequest;
import com.nibras.saas.dto.response.StockMovementResponse;
import com.nibras.saas.entity.Product;
import com.nibras.saas.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovement toEntity(final StockMovementRequest request) {
        return StockMovement.builder()
                .date(request.getDateMvt())
                .comment(request.getComment())
                .stockMovementType(request.getTypeMvt())
                .quantity(request.getQuantity())
                .product(Product.builder()
                        .id(request.getProductId())
                        .build())
                .deleted(false)
                .build();
    }

    public StockMovementResponse toResponse(final StockMovement entity) {
        return StockMovementResponse.builder()
                .id(entity.getId())
                .dateMvt(entity.getDate())
                .comment(entity.getComment())
                .typeMvt(entity.getStockMovementType())
                .quantity(entity.getQuantity())
                .build();
    }

}

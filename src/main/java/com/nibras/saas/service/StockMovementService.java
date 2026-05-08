package com.nibras.saas.service;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.StockMovementRequest;
import com.nibras.saas.dto.response.StockMovementResponse;

public interface StockMovementService extends BasicService<StockMovementRequest, StockMovementResponse> {

    PageResponse<StockMovementResponse> findAllByProductId(final String productId, final int page, final int size);

}

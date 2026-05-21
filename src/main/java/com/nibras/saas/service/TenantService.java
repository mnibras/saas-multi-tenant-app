package com.nibras.saas.service;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.RegisterTenantRequest;
import com.nibras.saas.dto.response.TenantResponse;

public interface TenantService {

    void registerTenant(final RegisterTenantRequest registerTenantRequest);

    void approveTenant(final String tenantId);

    void activateTenant(final String tenantId);

    void deactivateTenant(final String tenantId);

    void suspendTenant(final String tenantId);

    PageResponse<TenantResponse> findAll(final int page, final int size);

}

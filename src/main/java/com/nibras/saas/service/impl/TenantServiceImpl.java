package com.nibras.saas.service.impl;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.RegisterTenantRequest;
import com.nibras.saas.dto.response.TenantResponse;
import com.nibras.saas.entity.Tenant;
import com.nibras.saas.entity.User;
import com.nibras.saas.enums.TenantStatus;
import com.nibras.saas.enums.UserRole;
import com.nibras.saas.exception.DuplicateResourceException;
import com.nibras.saas.exception.InvalidRequestException;
import com.nibras.saas.mapper.TenantMapper;
import com.nibras.saas.repository.TenantRepository;
import com.nibras.saas.repository.UserRepository;
import com.nibras.saas.service.ProvisioningService;
import com.nibras.saas.service.TenantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final ProvisioningService provisioningService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void registerTenant(RegisterTenantRequest registerTenantRequest) {
        // check if the tenant is already exists
        if (this.tenantRepository.existsByCompanyCode(registerTenantRequest.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant with company code already exists");
        }

        // check if email exists
        if (this.tenantRepository.existsByEmail(registerTenantRequest.getEmail())) {
            throw new DuplicateResourceException("Tenant with email already exists");
        }

        final Tenant tenant = this.tenantMapper.toEntity(registerTenantRequest);
        tenant.setAdminPassword(this.passwordEncoder.encode(registerTenantRequest.getAdminPassword()));
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);

        log.info("Tenant registered successfully with company code: {}", registerTenantRequest.getCompanyCode());
    }

    @Override
    public void approveTenant(String tenantId) {
        final Tenant tenant = getTenant(tenantId);

        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);

        try {
            // provision the schema for the tenant
            this.provisioningService.provisionTenant(tenant);

            // create admin user for the tenant
            createAdminUser(tenant);
        } catch (final Exception e) {
            log.error("Error provisioning tenant with id: {}. Rolling back changes. Error: {}", tenantId, e.getMessage());
            rollbackTenantStatus(tenant);
        }
    }

    @Override
    public void activateTenant(String tenantId) {
        final Tenant tenant = getTenant(tenantId);
        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new InvalidRequestException("Only tenants in PENDING status can be activated");
        }

        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);

        log.info("Tenant activated successfully with id: {}", tenantId);
    }

    @Override
    public void deactivateTenant(String tenantId) {
        final Tenant tenant = getTenant(tenantId);
        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Only tenants in ACTIVE status can be deactivated");
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        this.tenantRepository.save(tenant);

        log.info("Tenant deactivated successfully with id: {}", tenantId);
    }

    @Override
    public void suspendTenant(String tenantId) {
        final Tenant tenant = getTenant(tenantId);
        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Only tenants in ACTIVE status can be deactivated");
        }

        tenant.setStatus(TenantStatus.SUSPENDED);
        this.tenantRepository.save(tenant);

        log.info("Tenant suspended successfully with id: {}", tenantId);
    }

    @Override
    public PageResponse<TenantResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenants = this.tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenants.map(this.tenantMapper::toResponse);
        return PageResponse.of(tenantResponses);
    }

    private void createAdminUser(final Tenant tenant) {
        // check if the user already exists
        if (this.userRepository.existsByUsername(tenant.getAdminUsername())) {
            throw new DuplicateResourceException("Admin user with username already exists");
        }

        final User user = User.builder()
                .username(tenant.getAdminUsername())
                .email(tenant.getAdminEmail())
                .firstName(extractFirstName(tenant.getAdminFullName()))
                .lastName(extractLastName(tenant.getAdminFullName()))
                .password(tenant.getAdminPassword()) // already hashed when register tenant
                .role(UserRole.ROLE_COMPANY_ADMIN)
                .tenant(tenant)
                .enabled(true)
                .deleted(false)
                .build();

        this.userRepository.save(user);

        log.info("Admin user created successfully with email: {}", tenant.getAdminEmail());
    }

    private void rollbackTenantStatus(Tenant tenant) {
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

    private Tenant getTenant(String tenantId) {
        return this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with id: " + tenantId));
    }

    private String extractFirstName(final String fullName) {
        return fullName.split(" ")[0];
    }

    private String extractLastName(final String fullName) {
        return fullName.split(" ").length > 1 ? fullName.split(" ")[1] : fullName;
    }
}

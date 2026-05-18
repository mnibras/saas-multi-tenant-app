package com.nibras.saas.service.impl;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.config.TenantContext;
import com.nibras.saas.dto.request.UserRequest;
import com.nibras.saas.dto.response.UserResponse;
import com.nibras.saas.entity.Tenant;
import com.nibras.saas.entity.User;
import com.nibras.saas.enums.UserRole;
import com.nibras.saas.exception.DuplicateResourceException;
import com.nibras.saas.exception.InvalidRequestException;
import com.nibras.saas.mapper.UserMapper;
import com.nibras.saas.repository.UserRepository;
import com.nibras.saas.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public void createUser(UserRequest userRequest) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Creating user for tenant: {}", tenantId);

        // validate username
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + userRequest.getUsername());
        }

        // validate email
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + userRequest.getEmail());
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (userRequest.getRole().equals(UserRole.ROLE_PLATFORM_ADMIN)) {
            throw new InvalidRequestException("Cannot assign PLATFORM_ADMIN role to a user");
        }

        final User user = userMapper.toEntity(userRequest);
        user.setTenant(Tenant.builder().id(tenantId).build());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        userRepository.save(user);

        log.info("User created successfully with username: {} for tenant: {}", userRequest.getUsername(), tenantId);
    }

    @Override
    public void updateUser(String userId, UserRequest userRequest) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Updating user for tenant: {}", tenantId);

        final User user = this.userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenantId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // check if username is being changed and if it is already taken
        if (!user.getUsername().equals(userRequest.getUsername()) && this.userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email is being changed and if it is already taken
        if (!user.getEmail().equals(userRequest.getEmail()) && this.userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (userRequest.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("User role cannot be PLATFORM_ADMIN");
        }

        // update user details
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setRole(userRequest.getRole());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());

        this.userRepository.save(user);

        log.info("User updated successfully");
    }

    @Override
    public void deleteUser(String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenantId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // soft delete user
        user.setDeleted(true);
        this.userRepository.save(user);

        log.info("User deleted successfully");
    }

    @Override
    public UserResponse getUserById(String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenantId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        return userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int pageSize) {
        final String tenantId = TenantContext.getCurrentTenant();
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        final Page<User> userPage = this.userRepository.findAllByTenantId(tenantId, pageRequest);
        final Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);
        return PageResponse.of(responsePage);
    }

    @Override
    public void enableUser(String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenantId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(true);
        this.userRepository.save(user);
        log.info("User enabled successfully");
    }

    @Override
    public void disableUser(String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenantId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(false);
        this.userRepository.save(user);
        log.info("User disabled successfully");
    }

}

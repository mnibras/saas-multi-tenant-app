package com.nibras.saas.repository;

import com.nibras.saas.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String adminUsername);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdAndNotDeleted(String id);

    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.deleted = false")
    Page<User> findAllByTenantId(String tenantId, Pageable pageable);

}

package com.nibras.saas.repository;

import com.nibras.saas.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, String> {

    Page<StockMovement> findAllByProductId(String productId, Pageable pageable);

}

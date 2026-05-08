package com.nibras.saas.entity;

import com.nibras.saas.enums.StockMovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stock_movement")
public class StockMovement extends AbstractEntity {

    @Column(name = "type", nullable = false)
    @Enumerated(STRING)
    private StockMovementType stockMovementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}

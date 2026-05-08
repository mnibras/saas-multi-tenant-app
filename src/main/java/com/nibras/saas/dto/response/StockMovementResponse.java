package com.nibras.saas.dto.response;

import com.nibras.saas.enums.StockMovementType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMovementResponse {

    private String id;
    private StockMovementType typeMvt;
    private Integer quantity;
    private LocalDate dateMvt;
    private String comment;

}

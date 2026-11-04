package com.serviceflow.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ServiceCatalogItemRequest(
        @NotBlank @Size(max = 140) String name,
        @Size(max = 100) String category,
        String description,
        @PositiveOrZero BigDecimal defaultPrice,
        Boolean active
) {
}

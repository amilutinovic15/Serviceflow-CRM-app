package com.serviceflow.invoice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record InvoiceItemRequest(
        Long serviceJobId,
        @NotBlank @Size(max = 255) String description,
        @Positive BigDecimal quantity,
        @PositiveOrZero BigDecimal unitPrice
) {
}

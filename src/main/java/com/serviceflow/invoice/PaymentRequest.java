package com.serviceflow.invoice;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(
        @NotNull @Positive BigDecimal amount,
        LocalDate paidAt,
        PaymentMethod method,
        @Size(max = 120) String reference,
        String notes
) {
}

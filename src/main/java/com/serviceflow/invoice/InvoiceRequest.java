package com.serviceflow.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
        @NotNull Long customerId,
        @Size(max = 40) String invoiceNumber,
        InvoiceStatus status,
        LocalDate issuedAt,
        LocalDate dueAt,
        @PositiveOrZero BigDecimal taxRate,
        String notes,
        @NotEmpty List<@Valid InvoiceItemRequest> items
) {
}

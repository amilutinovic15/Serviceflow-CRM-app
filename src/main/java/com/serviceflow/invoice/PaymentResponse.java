package com.serviceflow.invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PaymentResponse(
        Long id,
        Long invoiceId,
        BigDecimal amount,
        LocalDate paidAt,
        PaymentMethod method,
        String reference,
        String notes,
        Instant createdAt
) {
    static PaymentResponse from(InvoicePayment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvoice().getId(),
                payment.getAmount(),
                payment.getPaidAt(),
                payment.getMethod(),
                payment.getReference(),
                payment.getNotes(),
                payment.getCreatedAt()
        );
    }
}

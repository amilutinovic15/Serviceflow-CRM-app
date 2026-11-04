package com.serviceflow.invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        Long id,
        Long customerId,
        String customerName,
        String invoiceNumber,
        InvoiceStatus status,
        LocalDate issuedAt,
        LocalDate dueAt,
        BigDecimal subtotal,
        BigDecimal taxRate,
        BigDecimal taxAmount,
        BigDecimal total,
        BigDecimal paidAmount,
        BigDecimal outstandingAmount,
        String notes,
        List<InvoiceItemResponse> items,
        List<PaymentResponse> payments,
        Instant createdAt,
        Instant updatedAt
) {
    static InvoiceResponse from(Invoice invoice) {
        BigDecimal paidAmount = invoice.getPayments()
                .stream()
                .map(InvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getCustomer().getId(),
                invoice.getCustomer().getFullName(),
                invoice.getInvoiceNumber(),
                invoice.getStatus(),
                invoice.getIssuedAt(),
                invoice.getDueAt(),
                invoice.getSubtotal(),
                invoice.getTaxRate(),
                invoice.getTaxAmount(),
                invoice.getTotal(),
                paidAmount,
                invoice.getTotal().subtract(paidAmount),
                invoice.getNotes(),
                invoice.getItems().stream().map(InvoiceItemResponse::from).toList(),
                invoice.getPayments().stream().map(PaymentResponse::from).toList(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}

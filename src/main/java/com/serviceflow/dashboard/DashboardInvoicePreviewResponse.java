package com.serviceflow.dashboard;

import com.serviceflow.invoice.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardInvoicePreviewResponse(
        Long id,
        String invoiceNumber,
        Long customerId,
        String customerName,
        InvoiceStatus status,
        LocalDate issuedAt,
        LocalDate dueAt,
        BigDecimal total,
        BigDecimal paidAmount,
        BigDecimal outstandingAmount
) {
}

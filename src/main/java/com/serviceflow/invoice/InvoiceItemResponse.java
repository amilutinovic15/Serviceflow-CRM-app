package com.serviceflow.invoice;

import java.math.BigDecimal;

public record InvoiceItemResponse(
        Long id,
        Long serviceJobId,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
    static InvoiceItemResponse from(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.getId(),
                item.getServiceJob() == null ? null : item.getServiceJob().getId(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}

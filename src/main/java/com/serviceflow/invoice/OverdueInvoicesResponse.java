package com.serviceflow.invoice;

import java.time.LocalDate;
import java.util.List;

public record OverdueInvoicesResponse(
        LocalDate checkedAt,
        int updatedCount,
        List<Long> invoiceIds
) {
}

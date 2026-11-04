package com.serviceflow.invoice;

public record EmailInvoiceResponse(
        Long invoiceId,
        String to,
        String subject,
        boolean sent,
        boolean dryRun,
        String message
) {
}

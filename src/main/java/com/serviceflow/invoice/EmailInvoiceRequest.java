package com.serviceflow.invoice;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EmailInvoiceRequest(
        @Email @Size(max = 160) String to,
        @Size(max = 160) String subject,
        String message
) {
}

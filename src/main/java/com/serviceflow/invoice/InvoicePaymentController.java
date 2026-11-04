package com.serviceflow.invoice;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices/{invoiceId}/payments")
public class InvoicePaymentController {

    private final InvoicePaymentService invoicePaymentService;

    public InvoicePaymentController(InvoicePaymentService invoicePaymentService) {
        this.invoicePaymentService = invoicePaymentService;
    }

    @GetMapping
    public List<PaymentResponse> findByInvoiceId(@PathVariable Long invoiceId) {
        return invoicePaymentService.findByInvoiceId(invoiceId);
    }

    @PostMapping
    public InvoiceResponse addPayment(@PathVariable Long invoiceId, @Valid @RequestBody PaymentRequest request) {
        return invoicePaymentService.addPayment(invoiceId, request);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<InvoiceResponse> deletePayment(@PathVariable Long invoiceId, @PathVariable Long paymentId) {
        return ResponseEntity.ok(invoicePaymentService.deletePayment(invoiceId, paymentId));
    }
}

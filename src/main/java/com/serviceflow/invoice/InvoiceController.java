package com.serviceflow.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;
    private final InvoiceEmailService invoiceEmailService;

    public InvoiceController(
            InvoiceService invoiceService,
            InvoicePdfService invoicePdfService,
            InvoiceEmailService invoiceEmailService
    ) {
        this.invoiceService = invoiceService;
        this.invoicePdfService = invoicePdfService;
        this.invoiceEmailService = invoiceEmailService;
    }

    @GetMapping
    public Page<InvoiceResponse> findAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) InvoiceStatus status,
            @ParameterObject @PageableDefault(size = 20, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return invoiceService.findAll(customerId, status, pageable);
    }

    @GetMapping("/{id}")
    public InvoiceResponse findById(@PathVariable Long id) {
        return invoiceService.findById(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = invoicePdfService.generateInvoicePdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + invoicePdfService.filename(id) + "\"")
                .body(pdf);
    }

    @PostMapping("/{id}/send-email")
    public EmailInvoiceResponse sendEmail(@PathVariable Long id, @Valid @RequestBody EmailInvoiceRequest request) {
        return invoiceEmailService.sendInvoice(id, request);
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.create(request);
        return ResponseEntity
                .created(URI.create("/api/invoices/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public InvoiceResponse update(@PathVariable Long id, @Valid @RequestBody InvoiceRequest request) {
        return invoiceService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public InvoiceResponse changeStatus(
            @PathVariable Long id,
            @RequestParam @NotNull InvoiceStatus status
    ) {
        return invoiceService.changeStatus(id, status);
    }

    @PatchMapping("/actions/mark-overdue")
    public OverdueInvoicesResponse markOverdue() {
        return invoiceService.markOverdue();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

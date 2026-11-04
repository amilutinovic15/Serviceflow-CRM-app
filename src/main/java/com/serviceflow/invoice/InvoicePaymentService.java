package com.serviceflow.invoice;

import com.serviceflow.common.NotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoicePaymentService {

    private final InvoiceRepository invoiceRepository;
    private final InvoicePaymentRepository invoicePaymentRepository;

    public InvoicePaymentService(InvoiceRepository invoiceRepository, InvoicePaymentRepository invoicePaymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoicePaymentRepository = invoicePaymentRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByInvoiceId(Long invoiceId) {
        return invoicePaymentRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    public InvoiceResponse addPayment(Long invoiceId, PaymentRequest request) {
        Invoice invoice = getInvoice(invoiceId);

        InvoicePayment payment = new InvoicePayment();
        payment.setAmount(scaleMoney(request.amount()));
        payment.setPaidAt(request.paidAt() == null ? LocalDate.now() : request.paidAt());
        payment.setMethod(request.method() == null ? PaymentMethod.BANK_TRANSFER : request.method());
        payment.setReference(blankToNull(request.reference()));
        payment.setNotes(blankToNull(request.notes()));

        invoice.addPayment(payment);
        invoicePaymentRepository.save(payment);
        updateInvoiceStatus(invoice);

        return InvoiceResponse.from(invoice);
    }

    public InvoiceResponse deletePayment(Long invoiceId, Long paymentId) {
        Invoice invoice = getInvoice(invoiceId);
        InvoicePayment payment = invoicePaymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment with id %d was not found".formatted(paymentId)));

        if (!payment.getInvoice().getId().equals(invoiceId)) {
            throw new NotFoundException("Payment with id %d was not found for invoice %d".formatted(paymentId, invoiceId));
        }

        invoice.removePayment(payment);
        updateInvoiceStatus(invoice);

        return InvoiceResponse.from(invoice);
    }

    private Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice with id %d was not found".formatted(invoiceId)));
    }

    private void updateInvoiceStatus(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            return;
        }

        BigDecimal paidAmount = invoice.getPayments()
                .stream()
                .map(InvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (paidAmount.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (invoice.getStatus() == InvoiceStatus.PAID) {
            invoice.setStatus(InvoiceStatus.SENT);
        }
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

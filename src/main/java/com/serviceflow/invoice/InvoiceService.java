package com.serviceflow.invoice;

import com.serviceflow.common.NotFoundException;
import com.serviceflow.customer.Customer;
import com.serviceflow.customer.CustomerRepository;
import com.serviceflow.job.ServiceJob;
import com.serviceflow.job.ServiceJobRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceService {

    private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_QUANTITY = BigDecimal.ONE;

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final InvoiceNumberService invoiceNumberService;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository,
            ServiceJobRepository serviceJobRepository,
            InvoiceNumberService invoiceNumberService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.invoiceNumberService = invoiceNumberService;
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> findAll(Long customerId, InvoiceStatus status, Pageable pageable) {
        Page<Invoice> invoices;

        if (customerId != null && status != null) {
            invoices = invoiceRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else if (customerId != null) {
            invoices = invoiceRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            invoices = invoiceRepository.findByStatus(status, pageable);
        } else {
            invoices = invoiceRepository.findAll(pageable);
        }

        return invoices.map(InvoiceResponse::from);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long id) {
        return InvoiceResponse.from(getInvoice(id));
    }

    public InvoiceResponse create(InvoiceRequest request) {
        Invoice invoice = new Invoice();
        applyRequest(invoice, request);
        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    public InvoiceResponse update(Long id, InvoiceRequest request) {
        Invoice invoice = getInvoice(id);
        invoice.clearItems();
        applyRequest(invoice, request);
        return InvoiceResponse.from(invoice);
    }

    public InvoiceResponse changeStatus(Long id, InvoiceStatus status) {
        Invoice invoice = getInvoice(id);
        invoice.setStatus(status);
        return InvoiceResponse.from(invoice);
    }

    public OverdueInvoicesResponse markOverdue() {
        LocalDate today = LocalDate.now();
        List<Invoice> overdueInvoices = invoiceRepository.findByDueAtBeforeAndStatusIn(
                today,
                List.of(InvoiceStatus.DRAFT, InvoiceStatus.SENT)
        );

        overdueInvoices.forEach(invoice -> invoice.setStatus(InvoiceStatus.OVERDUE));

        return new OverdueInvoicesResponse(
                today,
                overdueInvoices.size(),
                overdueInvoices.stream().map(Invoice::getId).toList()
        );
    }

    public void delete(Long id) {
        invoiceRepository.delete(getInvoice(id));
    }

    private Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice with id %d was not found".formatted(id)));
    }

    private void applyRequest(Invoice invoice, InvoiceRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer with id %d was not found".formatted(request.customerId())));
        LocalDate issuedAt = request.issuedAt() == null ? LocalDate.now() : request.issuedAt();

        invoice.setCustomer(customer);
        invoice.setInvoiceNumber(resolveInvoiceNumber(invoice, request.invoiceNumber(), issuedAt));
        invoice.setStatus(request.status() == null ? InvoiceStatus.DRAFT : request.status());
        invoice.setIssuedAt(issuedAt);
        invoice.setDueAt(request.dueAt());
        invoice.setTaxRate(scaleMoney(request.taxRate() == null ? DEFAULT_TAX_RATE : request.taxRate()));
        invoice.setNotes(blankToNull(request.notes()));

        request.items().forEach(itemRequest -> invoice.addItem(createItem(itemRequest)));
        recalculateTotals(invoice);
    }

    private InvoiceItem createItem(InvoiceItemRequest request) {
        InvoiceItem item = new InvoiceItem();
        item.setServiceJob(resolveServiceJob(request.serviceJobId()));
        item.setDescription(request.description().trim());
        item.setQuantity(scaleMoney(request.quantity() == null ? DEFAULT_QUANTITY : request.quantity()));
        item.setUnitPrice(scaleMoney(request.unitPrice() == null ? BigDecimal.ZERO : request.unitPrice()));
        item.setLineTotal(scaleMoney(item.getQuantity().multiply(item.getUnitPrice())));
        return item;
    }

    private ServiceJob resolveServiceJob(Long serviceJobId) {
        if (serviceJobId == null) {
            return null;
        }

        return serviceJobRepository.findById(serviceJobId)
                .orElseThrow(() -> new NotFoundException("Service job with id %d was not found".formatted(serviceJobId)));
    }

    private void recalculateTotals(Invoice invoice) {
        BigDecimal subtotal = invoice.getItems()
                .stream()
                .map(InvoiceItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = subtotal.multiply(invoice.getTaxRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        invoice.setSubtotal(scaleMoney(subtotal));
        invoice.setTaxAmount(scaleMoney(taxAmount));
        invoice.setTotal(scaleMoney(subtotal.add(taxAmount)));
    }

    private String resolveInvoiceNumber(Invoice invoice, String requestedNumber, LocalDate issuedAt) {
        if (requestedNumber != null && !requestedNumber.isBlank()) {
            return requestedNumber.trim();
        }

        if (invoice.getInvoiceNumber() != null && !invoice.getInvoiceNumber().isBlank()) {
            return invoice.getInvoiceNumber();
        }

        return invoiceNumberService.nextInvoiceNumber(issuedAt);
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

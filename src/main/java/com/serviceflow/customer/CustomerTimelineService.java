package com.serviceflow.customer;

import com.serviceflow.asset.CustomerAsset;
import com.serviceflow.asset.CustomerAssetRepository;
import com.serviceflow.common.NotFoundException;
import com.serviceflow.invoice.Invoice;
import com.serviceflow.invoice.InvoicePayment;
import com.serviceflow.invoice.InvoicePaymentRepository;
import com.serviceflow.invoice.InvoiceRepository;
import com.serviceflow.job.ServiceJob;
import com.serviceflow.job.ServiceJobComment;
import com.serviceflow.job.ServiceJobCommentRepository;
import com.serviceflow.job.ServiceJobRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerTimelineService {

    private final CustomerRepository customerRepository;
    private final CustomerAssetRepository customerAssetRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final ServiceJobCommentRepository serviceJobCommentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoicePaymentRepository invoicePaymentRepository;

    public CustomerTimelineService(
            CustomerRepository customerRepository,
            CustomerAssetRepository customerAssetRepository,
            ServiceJobRepository serviceJobRepository,
            ServiceJobCommentRepository serviceJobCommentRepository,
            InvoiceRepository invoiceRepository,
            InvoicePaymentRepository invoicePaymentRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerAssetRepository = customerAssetRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.serviceJobCommentRepository = serviceJobCommentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoicePaymentRepository = invoicePaymentRepository;
    }

    public CustomerTimelineResponse getTimeline(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer with id %d was not found".formatted(customerId)));
        List<CustomerTimelineItemResponse> items = new ArrayList<>();

        customerAssetRepository.findByCustomerId(customerId)
                .forEach(asset -> items.add(assetItem(asset)));
        serviceJobRepository.findByCustomerId(customerId)
                .forEach(job -> items.add(jobItem(job)));
        serviceJobCommentRepository.findByServiceJobCustomerId(customerId)
                .forEach(comment -> items.add(commentItem(comment)));
        invoiceRepository.findByCustomerId(customerId)
                .forEach(invoice -> items.add(invoiceItem(invoice)));
        invoicePaymentRepository.findByInvoiceCustomerId(customerId)
                .forEach(payment -> items.add(paymentItem(payment)));

        List<CustomerTimelineItemResponse> sortedItems = items.stream()
                .sorted(Comparator.comparing(CustomerTimelineItemResponse::occurredAt).reversed())
                .toList();

        return new CustomerTimelineResponse(customer.getId(), customer.getFullName(), sortedItems);
    }

    private CustomerTimelineItemResponse assetItem(CustomerAsset asset) {
        return new CustomerTimelineItemResponse(
                asset.getCreatedAt(),
                "ASSET_CREATED",
                "Asset added: " + asset.getName(),
                asset.getCategory(),
                asset.getId(),
                null,
                Map.of(
                        "category", nullToBlank(asset.getCategory()),
                        "nextServiceDueAt", asset.getNextServiceDueAt() == null ? "" : asset.getNextServiceDueAt().toString()
                )
        );
    }

    private CustomerTimelineItemResponse jobItem(ServiceJob job) {
        return new CustomerTimelineItemResponse(
                job.getCreatedAt(),
                "JOB_CREATED",
                "Job created: " + job.getTitle(),
                job.getDescription(),
                job.getId(),
                job.getPrice(),
                Map.of(
                        "status", job.getStatus().name(),
                        "priority", job.getPriority().name()
                )
        );
    }

    private CustomerTimelineItemResponse commentItem(ServiceJobComment comment) {
        return new CustomerTimelineItemResponse(
                comment.getCreatedAt(),
                "JOB_COMMENT",
                "Comment on job: " + comment.getServiceJob().getTitle(),
                comment.getBody(),
                comment.getId(),
                null,
                Map.of(
                        "jobId", comment.getServiceJob().getId(),
                        "authorEmail", nullToBlank(comment.getAuthorEmail())
                )
        );
    }

    private CustomerTimelineItemResponse invoiceItem(Invoice invoice) {
        return new CustomerTimelineItemResponse(
                invoice.getCreatedAt(),
                "INVOICE_CREATED",
                "Invoice created: " + invoice.getInvoiceNumber(),
                invoice.getStatus().name(),
                invoice.getId(),
                invoice.getTotal(),
                Map.of(
                        "invoiceNumber", invoice.getInvoiceNumber(),
                        "status", invoice.getStatus().name()
                )
        );
    }

    private CustomerTimelineItemResponse paymentItem(InvoicePayment payment) {
        return new CustomerTimelineItemResponse(
                payment.getCreatedAt() == null ? payment.getPaidAt().atStartOfDay().toInstant(ZoneOffset.UTC) : payment.getCreatedAt(),
                "PAYMENT_RECEIVED",
                "Payment received",
                payment.getMethod().name(),
                payment.getId(),
                payment.getAmount(),
                Map.of(
                        "invoiceId", payment.getInvoice().getId(),
                        "paidAt", payment.getPaidAt().toString()
                )
        );
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}

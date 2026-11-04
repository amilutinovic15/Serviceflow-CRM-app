package com.serviceflow.dashboard;

import com.serviceflow.customer.CustomerRepository;
import com.serviceflow.asset.CustomerAssetRepository;
import com.serviceflow.invoice.InvoicePaymentRepository;
import com.serviceflow.invoice.InvoiceRepository;
import com.serviceflow.invoice.InvoiceStatus;
import com.serviceflow.invoice.InvoicePayment;
import com.serviceflow.invoice.Invoice;
import com.serviceflow.job.ServiceJob;
import com.serviceflow.job.ServiceJobPriority;
import com.serviceflow.job.ServiceJobRepository;
import com.serviceflow.job.ServiceJobStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final CustomerAssetRepository customerAssetRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoicePaymentRepository invoicePaymentRepository;

    public DashboardService(
            CustomerRepository customerRepository,
            CustomerAssetRepository customerAssetRepository,
            ServiceJobRepository serviceJobRepository,
            InvoiceRepository invoiceRepository,
            InvoicePaymentRepository invoicePaymentRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerAssetRepository = customerAssetRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoicePaymentRepository = invoicePaymentRepository;
    }

    public DashboardSummaryResponse getSummary() {
        BigDecimal invoicedTotal = zeroIfNull(invoiceRepository.sumTotal());
        BigDecimal paidTotal = zeroIfNull(invoicePaymentRepository.sumAmount());
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        return new DashboardSummaryResponse(
                customerRepository.count(),
                customerAssetRepository.count(),
                serviceJobRepository.count(),
                countJobsByStatus(),
                countJobsByPriority(),
                serviceJobRepository.countByPriority(ServiceJobPriority.URGENT),
                invoiceRepository.count(),
                countInvoicesByStatus(),
                invoiceRepository.countByDueAtBeforeAndStatusIn(today, List.of(InvoiceStatus.DRAFT, InvoiceStatus.SENT)),
                invoicedTotal,
                paidTotal,
                zeroIfNegative(invoicedTotal.subtract(paidTotal)),
                zeroIfNull(invoicePaymentRepository.sumAmountPaidBetween(monthStart, today)),
                upcomingJobs(),
                unpaidInvoices()
        );
    }

    private Map<String, Long> countJobsByStatus() {
        return Arrays.stream(ServiceJobStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        serviceJobRepository::countByStatus
                ));
    }

    private Map<String, Long> countInvoicesByStatus() {
        return Arrays.stream(InvoiceStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        invoiceRepository::countByStatus
                ));
    }

    private Map<String, Long> countJobsByPriority() {
        return Arrays.stream(ServiceJobPriority.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        serviceJobRepository::countByPriority
                ));
    }

    private List<DashboardUpcomingJobResponse> upcomingJobs() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = LocalDateTime.of(LocalDate.now().plusDays(7), LocalTime.MAX);

        return serviceJobRepository.findByScheduledAtBetweenAndStatusIn(
                        now,
                        nextWeek,
                        List.of(ServiceJobStatus.NEW, ServiceJobStatus.SCHEDULED, ServiceJobStatus.IN_PROGRESS),
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "scheduledAt"))
                )
                .stream()
                .map(this::toUpcomingJob)
                .toList();
    }

    private List<DashboardInvoicePreviewResponse> unpaidInvoices() {
        return invoiceRepository.findByStatusIn(
                        List.of(InvoiceStatus.DRAFT, InvoiceStatus.SENT, InvoiceStatus.OVERDUE),
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "dueAt"))
                )
                .stream()
                .map(this::toInvoicePreview)
                .toList();
    }

    private DashboardUpcomingJobResponse toUpcomingJob(ServiceJob job) {
        return new DashboardUpcomingJobResponse(
                job.getId(),
                job.getTitle(),
                job.getCustomer().getId(),
                job.getCustomer().getFullName(),
                job.getAssignee() == null ? null : job.getAssignee().getId(),
                job.getAssignee() == null ? null : job.getAssignee().getFullName(),
                job.getStatus(),
                job.getPriority(),
                job.getScheduledAt()
        );
    }

    private DashboardInvoicePreviewResponse toInvoicePreview(Invoice invoice) {
        BigDecimal paidAmount = invoice.getPayments()
                .stream()
                .map(InvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardInvoicePreviewResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getCustomer().getId(),
                invoice.getCustomer().getFullName(),
                invoice.getStatus(),
                invoice.getIssuedAt(),
                invoice.getDueAt(),
                invoice.getTotal(),
                paidAmount,
                invoice.getTotal().subtract(paidAmount)
        );
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal zeroIfNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : value;
    }
}

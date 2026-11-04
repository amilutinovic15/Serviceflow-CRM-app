package com.serviceflow.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatus(InvoiceStatus status);

    long countByStatus(InvoiceStatus status);

    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByStatusIn(List<InvoiceStatus> statuses, Pageable pageable);

    List<Invoice> findByDueAtBeforeAndStatusIn(LocalDate date, List<InvoiceStatus> statuses);

    long countByDueAtBeforeAndStatusIn(LocalDate date, List<InvoiceStatus> statuses);

    @Query("select coalesce(sum(i.total), 0) from Invoice i")
    BigDecimal sumTotal();

    @Query("select coalesce(sum(i.total), 0) from Invoice i where i.status = com.serviceflow.invoice.InvoiceStatus.PAID")
    BigDecimal sumPaidTotal();

    @Query("""
            select coalesce(sum(i.total), 0)
            from Invoice i
            where i.status in (
                com.serviceflow.invoice.InvoiceStatus.DRAFT,
                com.serviceflow.invoice.InvoiceStatus.SENT,
                com.serviceflow.invoice.InvoiceStatus.OVERDUE
            )
            """)
    BigDecimal sumOutstandingTotal();
}

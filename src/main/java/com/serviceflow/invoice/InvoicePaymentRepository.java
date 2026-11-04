package com.serviceflow.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, Long> {

    List<InvoicePayment> findByInvoiceId(Long invoiceId);

    List<InvoicePayment> findByInvoiceCustomerId(Long customerId);

    @Query("select coalesce(sum(p.amount), 0) from InvoicePayment p where p.invoice.id = :invoiceId")
    BigDecimal sumAmountByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Query("select coalesce(sum(p.amount), 0) from InvoicePayment p")
    BigDecimal sumAmount();

    @Query("select coalesce(sum(p.amount), 0) from InvoicePayment p where p.paidAt between :from and :to")
    BigDecimal sumAmountPaidBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}

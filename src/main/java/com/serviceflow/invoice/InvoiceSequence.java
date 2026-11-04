package com.serviceflow.invoice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "invoice_sequences")
public class InvoiceSequence {

    @Id
    @Column(name = "invoice_year")
    private Integer invoiceYear;

    @Column(name = "last_number", nullable = false)
    private Integer lastNumber;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InvoiceSequence() {
    }

    public InvoiceSequence(Integer invoiceYear) {
        this.invoiceYear = invoiceYear;
        this.lastNumber = 0;
        this.updatedAt = Instant.now();
    }

    public Integer getInvoiceYear() {
        return invoiceYear;
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public void increment() {
        lastNumber++;
        updatedAt = Instant.now();
    }
}

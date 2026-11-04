package com.serviceflow.invoice;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceNumberService {

    private final InvoiceSequenceRepository invoiceSequenceRepository;

    public InvoiceNumberService(InvoiceSequenceRepository invoiceSequenceRepository) {
        this.invoiceSequenceRepository = invoiceSequenceRepository;
    }

    @Transactional
    public String nextInvoiceNumber(LocalDate issuedAt) {
        int year = issuedAt.getYear();
        InvoiceSequence sequence = invoiceSequenceRepository.findByInvoiceYearForUpdate(year)
                .orElseGet(() -> invoiceSequenceRepository.saveAndFlush(new InvoiceSequence(year)));

        sequence.increment();
        return "INV-%d-%04d".formatted(year, sequence.getLastNumber());
    }
}

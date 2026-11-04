package com.serviceflow.invoice;

import com.serviceflow.common.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InvoiceEmailService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEmailService.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoicePdfService invoicePdfService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean mailEnabled;
    private final String fromAddress;

    public InvoiceEmailService(
            InvoiceRepository invoiceRepository,
            InvoicePdfService invoicePdfService,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${serviceflow.mail.enabled:false}") boolean mailEnabled,
            @Value("${serviceflow.mail.from:no-reply@serviceflow.local}") String fromAddress
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoicePdfService = invoicePdfService;
        this.mailSenderProvider = mailSenderProvider;
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;
    }

    public EmailInvoiceResponse sendInvoice(Long invoiceId, EmailInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice with id %d was not found".formatted(invoiceId)));

        String recipient = resolveRecipient(invoice, request);
        String subject = resolveSubject(invoice, request);
        String message = resolveMessage(invoice, request);

        if (!mailEnabled) {
            log.info("Dry-run invoice email. invoiceId={}, to={}, subject={}", invoiceId, recipient, subject);
            return new EmailInvoiceResponse(
                    invoiceId,
                    recipient,
                    subject,
                    false,
                    true,
                    "Email integration is in dry-run mode. Set SERVICEFLOW_MAIL_ENABLED=true and SMTP settings to send real emails."
            );
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Mail sender is not configured");
        }

        sendEmail(mailSender, invoice, recipient, subject, message);

        return new EmailInvoiceResponse(
                invoiceId,
                recipient,
                subject,
                true,
                false,
                "Invoice email sent."
        );
    }

    private void sendEmail(JavaMailSender mailSender, Invoice invoice, String recipient, String subject, String message) {
        try {
            byte[] pdf = invoicePdfService.generateInvoicePdf(invoice.getId());
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromAddress);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(message, false);
            helper.addAttachment(invoicePdfService.filename(invoice.getId()), new ByteArrayResource(pdf));

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Invoice email could not be created", exception);
        }
    }

    private String resolveRecipient(Invoice invoice, EmailInvoiceRequest request) {
        if (request.to() != null && !request.to().isBlank()) {
            return request.to().trim();
        }

        String customerEmail = invoice.getCustomer().getEmail();
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer does not have an email address");
        }

        return customerEmail.trim();
    }

    private String resolveSubject(Invoice invoice, EmailInvoiceRequest request) {
        if (request.subject() != null && !request.subject().isBlank()) {
            return request.subject().trim();
        }

        return "Invoice " + invoice.getInvoiceNumber();
    }

    private String resolveMessage(Invoice invoice, EmailInvoiceRequest request) {
        if (request.message() != null && !request.message().isBlank()) {
            return request.message().trim();
        }

        return "Hello,\n\nPlease find invoice %s attached.\n\nBest regards,\nServiceFlow".formatted(invoice.getInvoiceNumber());
    }
}

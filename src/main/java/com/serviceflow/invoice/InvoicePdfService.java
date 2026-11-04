package com.serviceflow.invoice;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.serviceflow.common.NotFoundException;
import com.serviceflow.customer.Customer;
import com.serviceflow.settings.BusinessProfile;
import com.serviceflow.settings.BusinessProfileService;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InvoicePdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    private static final Locale LOCALE = Locale.forLanguageTag("sr-RS");

    private final InvoiceRepository invoiceRepository;
    private final BusinessProfileService businessProfileService;

    public InvoicePdfService(InvoiceRepository invoiceRepository, BusinessProfileService businessProfileService) {
        this.invoiceRepository = invoiceRepository;
        this.businessProfileService = businessProfileService;
    }

    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice with id %d was not found".formatted(invoiceId)));

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);

            document.open();
            BusinessProfile businessProfile = businessProfileService.getProfileEntity();
            addHeader(document, invoice, businessProfile);
            addBusinessProfile(document, businessProfile);
            addCustomer(document, invoice.getCustomer());
            addItems(document, invoice);
            addTotals(document, invoice);
            addNotes(document, invoice);
            addFooter(document, businessProfile);
            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new IllegalStateException("Invoice PDF could not be generated", exception);
        }
    }

    public String filename(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice with id %d was not found".formatted(invoiceId)));
        return invoice.getInvoiceNumber() + ".pdf";
    }

    private void addHeader(Document document, Invoice invoice, BusinessProfile businessProfile) {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph title = new Paragraph(businessProfile.getBusinessName(), titleFont);
        title.setSpacingAfter(10);
        document.add(title);

        document.add(new Paragraph("Invoice: " + invoice.getInvoiceNumber(), normalFont));
        document.add(new Paragraph("Status: " + invoice.getStatus(), normalFont));
        document.add(new Paragraph("Issued at: " + formatDate(invoice.getIssuedAt()), normalFont));
        document.add(new Paragraph("Due at: " + formatDate(invoice.getDueAt()), normalFont));
        addEmptyLine(document);
    }

    private void addBusinessProfile(Document document, BusinessProfile businessProfile) {
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("From", headingFont));
        addIfPresent(document, businessProfile.getLegalName(), normalFont);
        addLabeledIfPresent(document, "Tax number", businessProfile.getTaxNumber(), normalFont);
        addLabeledIfPresent(document, "Registration number", businessProfile.getRegistrationNumber(), normalFont);
        addIfPresent(document, businessProfile.getEmail(), normalFont);
        addIfPresent(document, businessProfile.getPhone(), normalFont);
        addIfPresent(document, businessProfile.getAddress(), normalFont);
        addIfPresent(document, businessProfile.getCity(), normalFont);
        addIfPresent(document, businessProfile.getCountry(), normalFont);
        addEmptyLine(document);
    }

    private void addCustomer(Document document, Customer customer) {
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("Customer", headingFont));
        document.add(new Paragraph(customer.getFullName(), normalFont));
        addIfPresent(document, customer.getCompanyName(), normalFont);
        addIfPresent(document, customer.getEmail(), normalFont);
        addIfPresent(document, customer.getPhone(), normalFont);
        addIfPresent(document, customer.getAddress(), normalFont);
        addEmptyLine(document);
    }

    private void addItems(Document document, Invoice invoice) {
        PdfPTable table = new PdfPTable(new float[] { 5, 1, 2, 2 });
        table.setWidthPercentage(100);
        table.setSpacingBefore(8);
        table.setSpacingAfter(12);

        addHeaderCell(table, "Description");
        addHeaderCell(table, "Qty");
        addHeaderCell(table, "Unit price");
        addHeaderCell(table, "Total");

        invoice.getItems().forEach(item -> {
            table.addCell(cell(item.getDescription()));
            table.addCell(cell(item.getQuantity().toPlainString()));
            table.addCell(cell(formatMoney(item.getUnitPrice())));
            table.addCell(cell(formatMoney(item.getLineTotal())));
        });

        document.add(table);
    }

    private void addTotals(Document document, Invoice invoice) {
        BigDecimal paidAmount = invoice.getPayments()
                .stream()
                .map(InvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstandingAmount = invoice.getTotal().subtract(paidAmount);

        PdfPTable totals = new PdfPTable(new float[] { 3, 2 });
        totals.setWidthPercentage(45);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

        addTotalRow(totals, "Subtotal", invoice.getSubtotal());
        addTotalRow(totals, "Tax", invoice.getTaxAmount());
        addTotalRow(totals, "Total", invoice.getTotal());
        addTotalRow(totals, "Paid", paidAmount);
        addTotalRow(totals, "Outstanding", outstandingAmount);

        document.add(totals);
        addEmptyLine(document);
    }

    private void addNotes(Document document, Invoice invoice) {
        if (invoice.getNotes() == null || invoice.getNotes().isBlank()) {
            return;
        }

        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("Notes", headingFont));
        document.add(new Paragraph(invoice.getNotes(), normalFont));
    }

    private void addFooter(Document document, BusinessProfile businessProfile) {
        if (businessProfile.getInvoiceFooter() == null || businessProfile.getInvoiceFooter().isBlank()) {
            return;
        }

        addEmptyLine(document);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Paragraph footer = new Paragraph(businessProfile.getInvoiceFooter(), normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = cell(text);
        cell.setBackgroundColor(new Color(235, 235, 235));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell cell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setPadding(6);
        return cell;
    }

    private void addTotalRow(PdfPTable table, String label, BigDecimal amount) {
        PdfPCell labelCell = cell(label);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell amountCell = cell(formatMoney(amount));
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amountCell);
    }

    private void addIfPresent(Document document, String value, Font font) {
        if (value != null && !value.isBlank()) {
            document.add(new Paragraph(value, font));
        }
    }

    private void addLabeledIfPresent(Document document, String label, String value, Font font) {
        if (value != null && !value.isBlank()) {
            document.add(new Paragraph(label + ": " + value, font));
        }
    }

    private void addEmptyLine(Document document) {
        document.add(new Paragraph(" "));
    }

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "-" : DATE_FORMATTER.format(date);
    }

    private String formatMoney(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(LOCALE);
        return formatter.format(amount == null ? BigDecimal.ZERO : amount);
    }

}

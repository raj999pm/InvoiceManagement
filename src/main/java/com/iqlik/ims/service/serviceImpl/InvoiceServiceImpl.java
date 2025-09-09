package com.iqlik.ims.service.serviceImpl;

import com.iqlik.ims.entity.Invoice;
import com.iqlik.ims.entity.LineItem;
import com.iqlik.ims.repository.InvoiceRepository;
import com.iqlik.ims.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.UnitValue;

@Service
public class InvoiceServiceImpl implements InvoiceService {


    @Autowired
    private InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public String uploadInvoices(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Invoice> invoicesToSave = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                Invoice invoice = new Invoice();
                invoice.setInvoiceId(String.valueOf(UUID.randomUUID()));
                // Map all fields from the Excel row to the Invoice object using the new helper methods
                invoice.setInvoiceNumber(getStringValueFromCell(row.getCell(0)));
                invoice.setInvoiceDate(getLocalDateValueFromCell(row.getCell(1)));
                invoice.setSellerName(getStringValueFromCell(row.getCell(3)));
                invoice.setSellerAddress(getStringValueFromCell(row.getCell(4)));
                invoice.setSellerContact(getStringValueFromCell(row.getCell(5)));
                invoice.setCompanyRegistrationNumber(getStringValueFromCell(row.getCell(6)));
                invoice.setSellerVatNumber(getStringValueFromCell(row.getCell(7)));
                invoice.setBuyerName(getStringValueFromCell(row.getCell(8)));
                invoice.setBuyerAddress(getStringValueFromCell(row.getCell(9)));

                // Assuming a single line item for simplicity based on the spreadsheet
                LineItem lineItem = new LineItem();
                lineItem.setItemDescription(getStringValueFromCell(row.getCell(10)));
                lineItem.setQuantity((int) getNumericValueFromCell(row.getCell(11)));
                lineItem.setUnitPriceExclVat(getBigDecimalValueFromCell(row.getCell(12)));
                lineItem.setDiscountPerLine(getBigDecimalValueFromCell(row.getCell(13)));
                lineItem.setNetLineTotal(getBigDecimalValueFromCell(row.getCell(14)));
                lineItem.setVatRate(getBigDecimalValueFromCell(row.getCell(15)));
                lineItem.setVatAmountPerLine(getBigDecimalValueFromCell(row.getCell(16)));

                List<LineItem> lineItems = new ArrayList<>();
                lineItems.add(lineItem);
                invoice.setLineItems(lineItems);

                invoice.setTotalExclVat(getBigDecimalValueFromCell(row.getCell(17)));
                invoice.setTotalVat(getBigDecimalValueFromCell(row.getCell(18)));
                invoice.setTotalDueInclVat(getBigDecimalValueFromCell(row.getCell(19)));
                invoice.setPaymentTerms(getStringValueFromCell(row.getCell(20)));
                invoice.setBankPaymentDetails(getStringValueFromCell(row.getCell(21)));
                invoice.setReverseChargeNote(getStringValueFromCell(row.getCell(22)));
                invoice.setCurrency(getStringValueFromCell(row.getCell(23)));

                // Set the company/folder details
                // This logic should be updated based on how you get the company name
                invoice.setCompany("IQLIK");
                invoice.setYear(LocalDate.now().getYear());
                invoice.setMonth(LocalDate.now().getMonthValue());

                invoicesToSave.add(invoice);
            }
            invoiceRepository.saveAll(invoicesToSave);
            return "Invoices uploaded and saved successfully!";
        }
    }

    @Override
    public byte[] downloadPdf(String invoiceId) throws IOException {
        Invoice invoice = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add Invoice Header
        document.add(new Paragraph("Invoice").setBold().setFontSize(18));
        document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber()));
        document.add(new Paragraph("Invoice Date: " + invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("\n"));

        // Add Seller and Buyer Details Table
//        Table infoTable = new Table(new float[]{50, 50});        infoTable.setWidth(UnitValue.createPercentValue(100));
//
//        infoTable.addCell(new Paragraph("Seller Details").setBold());
//        infoTable.addCell(new Paragraph("Buyer Details").setBold());
//
//        infoTable.addCell(new Paragraph("Name: " + invoice.getSellerName()));
//        infoTable.addCell(new Paragraph("Name: " + invoice.getBuyerName()));
//
//        infoTable.addCell(new Paragraph("Address: " + invoice.getSellerAddress()));
//        infoTable.addCell(new Paragraph("Address: " + invoice.getBuyerAddress()));
//
//        document.add(infoTable);
//        document.add(new Paragraph("\n"));
//
//        // Add Line Items Table
//        Table lineItemTable = new Table(UnitValue.createPercentArray(new float[]{40, 10, 15, 15, 20}));
//        lineItemTable.setWidth(UnitValue.createPercentValue(100));
//
//        lineItemTable.addHeaderCell(new Paragraph("Item Description").setBold());
//        lineItemTable.addHeaderCell(new Paragraph("Qty").setBold());
//        lineItemTable.addHeaderCell(new Paragraph("Unit Price").setBold());
//        lineItemTable.addHeaderCell(new Paragraph("Net Total").setBold());
//        lineItemTable.addHeaderCell(new Paragraph("VAT Amount").setBold());
//
//        for (LineItem item : invoice.getLineItems()) {
//            lineItemTable.addCell(new Paragraph(item.getItemDescription()));
//            lineItemTable.addCell(new Paragraph(String.valueOf(item.getQuantity())));
//            lineItemTable.addCell(new Paragraph(String.valueOf(item.getUnitPriceExclVat())));
//            lineItemTable.addCell(new Paragraph(String.valueOf(item.getNetLineTotal())));
//            lineItemTable.addCell(new Paragraph(String.valueOf(item.getVatAmountPerLine())));
//        }
//
//        document.add(lineItemTable);
        document.add(new Paragraph("\n"));

        // Add Totals
        document.add(new Paragraph("Total Excl. VAT: " + invoice.getTotalExclVat()));
        document.add(new Paragraph("Total VAT: " + invoice.getTotalVat()));
        document.add(new Paragraph("Total Due Incl. VAT: " + invoice.getTotalDueInclVat()).setBold());

        document.close();

        return baos.toByteArray();
    }

    @Override
    public List<Invoice> getInvoicesByFolder(String company, int year, int month) {
        return invoiceRepository.findByCompanyAndYearAndMonth(company, year, month);
    }

    // Helper methods for safe cell value parsing
    private String getStringValueFromCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Handle numeric values that should be treated as strings, like an invoice number
            return String.valueOf((long) cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            // Handle formulas that result in a string
            return cell.getStringCellValue();
        }
        return null;
    }

    private double getNumericValueFromCell(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        // Add more cases if needed, e.g., if a cell contains a string like "123.45"
        return 0.0;
    }

    private BigDecimal getBigDecimalValueFromCell(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        return BigDecimal.ZERO;
    }

    private LocalDate getLocalDateValueFromCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }
}

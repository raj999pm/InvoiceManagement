package com.iqlik.ims.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;
    private String invoiceId;
    private String company; // Corresponds to the folder structure
    private int year;
    private int month;

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String sellerName;
    private String sellerAddress;
    private String sellerContact;
    private String companyRegistrationNumber;
    private String sellerVatNumber;

    private String buyerName;
    private String buyerAddress;

    private List<LineItem> lineItems;

    private BigDecimal totalExclVat;
    private BigDecimal totalVat;
    private BigDecimal totalDueInclVat;

    private String paymentTerms;
    private String bankPaymentDetails;
    private String reverseChargeNote;
    private String currency;
}

package com.iqlik.ims.service;

import com.iqlik.ims.entity.Invoice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface InvoiceService {

    String uploadInvoices(MultipartFile file) throws IOException;
    byte[] downloadPdf(String invoiceId) throws IOException;
    List<Invoice> getInvoicesByFolder(String company, int year, int month);
}

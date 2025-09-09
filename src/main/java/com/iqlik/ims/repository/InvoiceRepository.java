package com.iqlik.ims.repository;

import com.iqlik.ims.entity.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByCompanyAndYearAndMonth(String company, int year, int month);

    Optional<Invoice> findByInvoiceId(String invoiceId);
}
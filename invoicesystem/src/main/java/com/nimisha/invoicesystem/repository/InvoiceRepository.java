package com.nimisha.invoicesystem.repository;

import com.nimisha.invoicesystem.domain.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID id);

    List<Invoice> findAll();
}

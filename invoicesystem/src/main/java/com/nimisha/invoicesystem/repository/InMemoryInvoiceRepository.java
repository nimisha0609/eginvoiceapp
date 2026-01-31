package com.nimisha.invoicesystem.repository;

import com.nimisha.invoicesystem.domain.Invoice;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final Map<UUID, Invoice> store = new HashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        store.put(invoice.getId(), invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Invoice> findAll() {
        return new ArrayList<>(store.values());
    }
}

package com.nimisha.invoicesystem.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nimisha.invoicesystem.domain.Invoice;

class InMemoryInvoiceRepositoryTests {

    @Test
    void saveAndFindById() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        Invoice invoice = new Invoice(new BigDecimal("75.00"), LocalDate.of(2026, 2, 1));

        repository.save(invoice);

        assertTrue(repository.findById(invoice.getId()).isPresent());
        assertEquals(invoice, repository.findById(invoice.getId()).orElseThrow());
    }

    @Test
    void findAllReturnsAllInvoices() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        Invoice first = new Invoice(new BigDecimal("25.00"), LocalDate.of(2026, 2, 1));
        Invoice second = new Invoice(new BigDecimal("50.00"), LocalDate.of(2026, 2, 2));

        repository.save(first);
        repository.save(second);

        List<Invoice> invoices = repository.findAll();
        assertEquals(2, invoices.size());
        assertTrue(invoices.contains(first));
        assertTrue(invoices.contains(second));
    }
}

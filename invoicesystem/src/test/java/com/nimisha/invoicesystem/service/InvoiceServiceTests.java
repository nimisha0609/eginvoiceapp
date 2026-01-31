package com.nimisha.invoicesystem.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.nimisha.invoicesystem.domain.Invoice;
import com.nimisha.invoicesystem.domain.InvoiceStatus;
import com.nimisha.invoicesystem.repository.InMemoryInvoiceRepository;

class InvoiceServiceTests {

    @Test
    void createInvoiceValidatesAmountAndStoresInvoice() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        InvoiceService service = new InvoiceService(repository);

        UUID id = service.createInvoice(new BigDecimal("100.00"), LocalDate.of(2026, 2, 1));

        assertTrue(repository.findById(id).isPresent());
        assertEquals(new BigDecimal("100.00"), repository.findById(id).orElseThrow().getAmount());
    }

    @Test
    void createInvoiceRejectsNonPositiveAmount() {
        InvoiceService service = new InvoiceService(new InMemoryInvoiceRepository());

        assertThrows(IllegalArgumentException.class,
                () -> service.createInvoice(new BigDecimal("0"), LocalDate.of(2026, 2, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> service.createInvoice(new BigDecimal("-1"), LocalDate.of(2026, 2, 1)));
    }

    @Test
    void payInvoiceUpdatesInvoice() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        InvoiceService service = new InvoiceService(repository);
        Invoice invoice = new Invoice(new BigDecimal("80.00"), LocalDate.of(2026, 2, 1));
        repository.save(invoice);

        service.payInvoice(invoice.getId(), new BigDecimal("30.00"));

        Invoice updated = repository.findById(invoice.getId()).orElseThrow();
        assertEquals(new BigDecimal("30.00"), updated.getPaidAmount());
        assertEquals(InvoiceStatus.PENDING, updated.getStatus());
    }

    @Test
    void payInvoiceThrowsWhenMissing() {
        InvoiceService service = new InvoiceService(new InMemoryInvoiceRepository());

        assertThrows(IllegalArgumentException.class,
                () -> service.payInvoice(UUID.randomUUID(), new BigDecimal("10.00")));
    }

    @Test
    void processOverdueVoidsUnpaidAndRollsToNewInvoice() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        InvoiceService service = new InvoiceService(repository);
        LocalDate today = LocalDate.now();
        Invoice unpaid = new Invoice(new BigDecimal("100.00"), today.minusDays(15));
        repository.save(unpaid);

        service.processOverdue(new BigDecimal("5.00"), 10);

        Invoice updated = repository.findById(unpaid.getId()).orElseThrow();
        assertEquals(InvoiceStatus.VOID, updated.getStatus());

        List<Invoice> invoices = repository.findAll();
        assertEquals(2, invoices.size());
        Invoice newInvoice = invoices.stream()
                .filter(inv -> !inv.getId().equals(unpaid.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("105.00"), newInvoice.getAmount());
        assertEquals(today.plusDays(30), newInvoice.getDueDate());
        assertEquals(InvoiceStatus.PENDING, newInvoice.getStatus());
    }

    @Test
    void processOverdueClosesPartialAndCreatesReplacement() {
        InMemoryInvoiceRepository repository = new InMemoryInvoiceRepository();
        InvoiceService service = new InvoiceService(repository);
        LocalDate today = LocalDate.now();
        Invoice partiallyPaid = new Invoice(new BigDecimal("120.00"), today.minusDays(15));
        partiallyPaid.applyPayment(new BigDecimal("50.00"));
        repository.save(partiallyPaid);

        service.processOverdue(new BigDecimal("10.00"), 10);

        Invoice updated = repository.findById(partiallyPaid.getId()).orElseThrow();
        assertEquals(InvoiceStatus.PAID, updated.getStatus());

        List<Invoice> invoices = repository.findAll();
        assertEquals(2, invoices.size());
        Invoice newInvoice = invoices.stream()
                .filter(inv -> !inv.getId().equals(partiallyPaid.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("80.00"), newInvoice.getAmount());
        assertEquals(today.plusDays(30), newInvoice.getDueDate());
    }
}

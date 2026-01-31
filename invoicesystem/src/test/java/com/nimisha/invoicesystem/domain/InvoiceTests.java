package com.nimisha.invoicesystem.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class InvoiceTests {

    @Test
    void newInvoiceDefaults() {
        LocalDate dueDate = LocalDate.of(2026, 2, 15);
        Invoice invoice = new Invoice(new BigDecimal("120.50"), dueDate);

        assertNotNull(invoice.getId());
        assertEquals(new BigDecimal("120.50"), invoice.getAmount());
        assertEquals(BigDecimal.ZERO, invoice.getPaidAmount());
        assertEquals(dueDate, invoice.getDueDate());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
        assertEquals(new BigDecimal("120.50"), invoice.remainingAmount());
        assertFalse(invoice.isPartiallyPaid());
    }

    @Test
    void applyPaymentRejectsNonPositive() {
        Invoice invoice = new Invoice(new BigDecimal("100"), LocalDate.of(2026, 2, 1));

        assertThrows(IllegalArgumentException.class,
                () -> invoice.applyPayment(new BigDecimal("0")));
        assertThrows(IllegalArgumentException.class,
                () -> invoice.applyPayment(new BigDecimal("-10")));
    }

    @Test
    void applyPaymentRejectsWhenNotPending() {
        Invoice invoice = new Invoice(new BigDecimal("100"), LocalDate.of(2026, 2, 1));
        invoice.setStatus(InvoiceStatus.PAID);

        assertThrows(IllegalStateException.class,
                () -> invoice.applyPayment(new BigDecimal("10")));
    }

    @Test
    void partialAndFullPaymentsUpdateStatus() {
        Invoice invoice = new Invoice(new BigDecimal("100"), LocalDate.of(2026, 2, 1));

        invoice.applyPayment(new BigDecimal("40"));
        assertEquals(new BigDecimal("60"), invoice.remainingAmount());
        assertEquals(new BigDecimal("40"), invoice.getPaidAmount());
        assertTrue(invoice.isPartiallyPaid());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());

        invoice.applyPayment(new BigDecimal("80"));
        assertEquals(BigDecimal.ZERO, invoice.remainingAmount());
        assertEquals(new BigDecimal("100"), invoice.getPaidAmount());
        assertFalse(invoice.isPartiallyPaid());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    void isOverdueBehavior() {
        LocalDate dueDate = LocalDate.of(2026, 1, 1);
        Invoice invoice = new Invoice(new BigDecimal("100"), dueDate);

        assertFalse(invoice.isOverdue(LocalDate.of(2025, 12, 31), 10));
        assertFalse(invoice.isOverdue(LocalDate.of(2026, 1, 1), 10));
        assertFalse(invoice.isOverdue(LocalDate.of(2026, 1, 10), 10));
        assertTrue(invoice.isOverdue(LocalDate.of(2026, 1, 11), 10));

        invoice.setStatus(InvoiceStatus.VOID);
        assertFalse(invoice.isOverdue(LocalDate.of(2026, 1, 20), 10));
    }

    @Test
    void invoiceStatusEnumIsStable() {
        assertEquals(3, InvoiceStatus.values().length);
        assertEquals(InvoiceStatus.PENDING, InvoiceStatus.valueOf("PENDING"));
        assertEquals(InvoiceStatus.PAID, InvoiceStatus.valueOf("PAID"));
        assertEquals(InvoiceStatus.VOID, InvoiceStatus.valueOf("VOID"));
    }
}

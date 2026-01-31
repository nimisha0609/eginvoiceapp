package com.nimisha.invoicesystem.service;

import com.nimisha.invoicesystem.domain.Invoice;
import com.nimisha.invoicesystem.domain.InvoiceStatus;
import com.nimisha.invoicesystem.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository repository;
    private static final int NEW_DUE_DAYS = 30;

    public InvoiceService(InvoiceRepository repository) {
        this.repository = repository;
    }

    public UUID createInvoice(BigDecimal amount, LocalDate dueDate) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Invoice invoice = new Invoice(amount, dueDate);
        repository.save(invoice);
        return invoice.getId();
    }

    public List<Invoice> listInvoices() {
        return repository.findAll();
    }

    public List<Invoice> listInvoices(InvoiceStatus status, Boolean overdue, int overdueDays, int page, int size) {
        LocalDate today = LocalDate.now();
        return repository.findAll().stream()
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> overdue == null || (overdue == i.isOverdue(today, overdueDays)))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    public void payInvoice(UUID id, BigDecimal amount) {
        Invoice invoice = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        invoice.applyPayment(amount);
        repository.save(invoice);
    }

    public void processOverdue(BigDecimal lateFee, int overdueDays) {
        LocalDate today = LocalDate.now();

        for (Invoice invoice : repository.findAll()) {
            if (!invoice.isOverdue(today, overdueDays)) {
                continue;
            }

            BigDecimal paidAmount = invoice.getPaidAmount();
            // CASE 1: Partially paid
            if (paidAmount.compareTo(BigDecimal.ZERO) > 0 && paidAmount.compareTo(invoice.getAmount()) < 0) {
                BigDecimal remaining = invoice.remainingAmount();
                invoice.applyPayment(remaining);
                repository.save(invoice);

                Invoice newInvoice = new Invoice(remaining.add(lateFee), today.plusDays(NEW_DUE_DAYS));
                repository.save(newInvoice);
            }
            // CASE 2: Not paid
            else if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
                invoice.setStatus(InvoiceStatus.VOID);
                repository.save(invoice);
                Invoice newInvoice = new Invoice(invoice.getAmount().add(lateFee), today.plusDays(NEW_DUE_DAYS));
                repository.save(newInvoice);
            }
        }
    }

}

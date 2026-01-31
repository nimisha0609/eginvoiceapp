package com.nimisha.invoicesystem.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Invoice {

    private final UUID id;
    private final BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private InvoiceStatus status;

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public Invoice(BigDecimal amount, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.paidAmount = BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.status = InvoiceStatus.PENDING;
    }

    public void applyPayment(BigDecimal payment) {
        if (status != InvoiceStatus.PENDING) {
            throw new IllegalStateException("Cannot pay non-pending invoice");
        }
        if (payment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment must be positive");
        }
        BigDecimal remaining = remainingAmount();
        BigDecimal applied = payment.min(remaining);
        paidAmount = paidAmount.add(applied);
        if (paidAmount.compareTo(amount) == 0) {
            status = InvoiceStatus.PAID;
        }
    }

    public boolean isOverdue(LocalDate today, int overdueDays) {
        return status == InvoiceStatus.PENDING &&
                today.isAfter(dueDate) &&
                dueDate.plusDays(overdueDays).isBefore(today.plusDays(1));
    }

    public BigDecimal remainingAmount() {
        return amount.subtract(paidAmount);
    }

    public boolean isPartiallyPaid() {
        return paidAmount.compareTo(BigDecimal.ZERO) > 0 && paidAmount.compareTo(amount) < 0;
    }
}

package com.nimisha.invoicesystem.controller;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

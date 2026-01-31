package com.nimisha.invoicesystem.controller;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ProcessOverdueRequest {

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @JsonProperty("late_fee")
    private BigDecimal lateFee;

    @Min(1)
    @JsonProperty("overdue_days")
    private int overdueDays;

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }
}

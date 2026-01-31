package com.nimisha.invoicesystem.controller;

import com.nimisha.invoicesystem.domain.Invoice;
import com.nimisha.invoicesystem.domain.InvoiceStatus;
import com.nimisha.invoicesystem.service.InvoiceService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> create(@Valid @RequestBody CreateInvoiceRequest req) {
        UUID id = service.createInvoice(req.getAmount(), req.getDueDate());
        return Map.of("id", id.toString());
    }

    @GetMapping
    public List<Invoice> list(@RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(defaultValue = "10") int overdueDays,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.listInvoices(status, overdue, overdueDays, page, size);
    }

    @PostMapping("/{id}/payments")
    public void pay(@PathVariable UUID id, @Valid @RequestBody PaymentRequest req) {
        service.payInvoice(id, req.getAmount());
    }

    @PostMapping("/process-overdue")
    public void process(@Valid @RequestBody ProcessOverdueRequest req) {
        service.processOverdue(req.getLateFee(), req.getOverdueDays());
    }
}

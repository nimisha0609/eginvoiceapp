package com.nimisha.invoicesystem.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.nimisha.invoicesystem.domain.Invoice;
import com.nimisha.invoicesystem.domain.InvoiceStatus;
import com.nimisha.invoicesystem.service.InvoiceService;

class InvoiceControllerTests {

    @Test
    void createDelegatesToServiceAndReturnsId() {
        UUID id = UUID.randomUUID();
        RecordingInvoiceService service = new RecordingInvoiceService(id);
        InvoiceController controller = new InvoiceController(service);

        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDueDate(LocalDate.of(2026, 3, 1));

        Map<String, String> response = controller.create(request);

        assertEquals(id.toString(), response.get("id"));
        assertEquals(new BigDecimal("150.00"), service.lastCreateAmount);
        assertEquals(LocalDate.of(2026, 3, 1), service.lastCreateDueDate);
    }

    @Test
    void listReturnsServiceInvoices() {
        Invoice invoice = new Invoice(new BigDecimal("10.00"), LocalDate.of(2026, 1, 10));
        RecordingInvoiceService service = new RecordingInvoiceService(UUID.randomUUID());
        service.invoices = List.of(invoice);
        InvoiceController controller = new InvoiceController(service);

        List<Invoice> response = controller.list(InvoiceStatus.PENDING, true, 7, 1, 5);

        assertEquals(1, response.size());
        assertEquals(invoice, response.get(0));
        assertEquals(InvoiceStatus.PENDING, service.lastListStatus);
        assertEquals(Boolean.TRUE, service.lastListOverdue);
        assertEquals(7, service.lastListOverdueDays);
        assertEquals(1, service.lastListPage);
        assertEquals(5, service.lastListSize);
    }

    @Test
    void payDelegatesToService() {
        RecordingInvoiceService service = new RecordingInvoiceService(UUID.randomUUID());
        InvoiceController controller = new InvoiceController(service);
        UUID invoiceId = UUID.randomUUID();

        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("42.50"));
        controller.pay(invoiceId, request);

        assertEquals(invoiceId, service.lastPayId);
        assertEquals(new BigDecimal("42.50"), service.lastPayAmount);
    }

    @Test
    void processDelegatesToService() {
        RecordingInvoiceService service = new RecordingInvoiceService(UUID.randomUUID());
        InvoiceController controller = new InvoiceController(service);

        ProcessOverdueRequest request = new ProcessOverdueRequest();
        request.setLateFee(new BigDecimal("12.00"));
        request.setOverdueDays(8);
        controller.process(request);

        assertEquals(new BigDecimal("12.00"), service.lastLateFee);
        assertEquals(8, service.lastOverdueDays);
    }

    private static class RecordingInvoiceService extends InvoiceService {
        private BigDecimal lastCreateAmount;
        private LocalDate lastCreateDueDate;
        private UUID lastPayId;
        private BigDecimal lastPayAmount;
        private BigDecimal lastLateFee;
        private int lastOverdueDays;
        private List<Invoice> invoices = List.of();
        private InvoiceStatus lastListStatus;
        private Boolean lastListOverdue;
        private Integer lastListOverdueDays;
        private Integer lastListPage;
        private Integer lastListSize;
        private final UUID createId;

        RecordingInvoiceService(UUID createId) {
            super(null);
            this.createId = createId;
        }

        @Override
        public UUID createInvoice(BigDecimal amount, LocalDate dueDate) {
            this.lastCreateAmount = amount;
            this.lastCreateDueDate = dueDate;
            return createId;
        }

        @Override
        public List<Invoice> listInvoices() {
            return invoices;
        }

        @Override
        public List<Invoice> listInvoices(InvoiceStatus status, Boolean overdue, int overdueDays, int page, int size) {
            this.lastListStatus = status;
            this.lastListOverdue = overdue;
            this.lastListOverdueDays = overdueDays;
            this.lastListPage = page;
            this.lastListSize = size;
            return invoices;
        }

        @Override
        public void payInvoice(UUID id, BigDecimal amount) {
            this.lastPayId = id;
            this.lastPayAmount = amount;
        }

        @Override
        public void processOverdue(BigDecimal lateFee, int overdueDays) {
            this.lastLateFee = lateFee;
            this.lastOverdueDays = overdueDays;
        }
    }
}

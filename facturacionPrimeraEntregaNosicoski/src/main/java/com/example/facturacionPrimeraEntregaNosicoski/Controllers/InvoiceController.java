package com.example.facturacionPrimeraEntregaNosicoski.Controllers;


import com.example.facturacionPrimeraEntregaNosicoski.Model.Invoice;
import com.example.facturacionPrimeraEntregaNosicoski.Model.InvoiceDetail;
import com.example.facturacionPrimeraEntregaNosicoski.Service.InvoiceDetailService;
import com.example.facturacionPrimeraEntregaNosicoski.Service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {private final InvoiceService invoiceService;
    private final InvoiceDetailService invoiceDetailService;

    public InvoiceController(InvoiceService invoiceService, InvoiceDetailService invoiceDetailService) {
        this.invoiceService = invoiceService;
        this.invoiceDetailService = invoiceDetailService;
    }

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public Invoice getInvoiceById(@PathVariable Integer id) {
        return invoiceService.getInvoiceById(id);
    }

    @PostMapping
    public Invoice createInvoice(@Valid @RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }

    @PutMapping("/{id}")
    public Invoice updateInvoice(@PathVariable Integer id, @Valid @RequestBody Invoice invoiceDetails) {
        return invoiceService.updateInvoice(id, invoiceDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok().build();
    }

    // Endpoint para agregar un detalle a una factura existente
    @PostMapping("/{invoiceId}/details")
    public InvoiceDetail addInvoiceDetail(@PathVariable Integer invoiceId, @Valid @RequestBody InvoiceDetail detail) {
        return invoiceDetailService.addInvoiceDetail(invoiceId, detail);
    }
}

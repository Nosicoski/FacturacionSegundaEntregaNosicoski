package com.example.facturacionPrimeraEntregaNosicoski.Service;

import com.example.facturacionPrimeraEntregaNosicoski.Exception.ResourceNotFoundException;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Invoice;
import com.example.facturacionPrimeraEntregaNosicoski.Model.InvoiceDetail;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Product;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.InvoiceRepository;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final ClientService clientService;

    public InvoiceService(InvoiceRepository invoiceRepository, ClientService clientService, ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.clientService = clientService;
        this.productRepository = productRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id " + id));
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        // Se asume que el objeto invoice ya tiene asignado el cliente (por id)
        invoice.setCreatedAt(LocalDateTime.now());

        double total = 0.0;
        if (invoice.getInvoiceDetails() != null) {
            for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
                Product product = productRepository.findById(detail.getProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + detail.getProduct().getId()));

                if(product.getStock() < detail.getAmount()){
                    throw new IllegalArgumentException("Stock insuficiente para el producto id: " + product.getId());
                }

                // Actualizar stock
                product.setStock(product.getStock() - detail.getAmount());
                productRepository.save(product);

                // Se asigna el precio actual del producto al detalle
                detail.setPrice(product.getPrice());
                detail.setInvoice(invoice);

                total += detail.getAmount() * product.getPrice();
            }
        }
        invoice.setTotal(total);
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Integer id, Invoice invoiceDetails) {
        Invoice invoice = getInvoiceById(id);
        // Actualización simplificada (actualizar el total o el cliente)
        invoice.setTotal(invoiceDetails.getTotal());
        invoice.setClient(invoiceDetails.getClient());
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Integer id) {
        Invoice invoice = getInvoiceById(id);
        invoiceRepository.delete(invoice);
    }

    public List<Invoice> getInvoicesByClientId(Integer clientId) {
        return invoiceRepository.findByClientId(clientId);
    }
}


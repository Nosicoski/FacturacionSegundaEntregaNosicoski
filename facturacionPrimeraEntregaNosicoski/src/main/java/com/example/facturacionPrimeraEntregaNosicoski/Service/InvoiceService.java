package com.example.facturacionPrimeraEntregaNosicoski.Service;

import com.example.facturacionPrimeraEntregaNosicoski.Exception.ResourceNotFoundException;
import com.example.facturacionPrimeraEntregaNosicoski.Model.*;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.InvoiceRepository;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientService clientService;
    private final ProductRepository productRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            ClientService clientService,
            ProductRepository productRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.clientService = clientService;
        this.productRepository = productRepository;
    }

    // Obtener todas las facturas
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Obtener una factura por ID
    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        try {
            // Validar cliente
            if (invoice.getClient() == null || invoice.getClient().getId() == null) {
                throw new IllegalArgumentException("El cliente es obligatorio");
            }
            Client client = clientService.getClientById(invoice.getClient().getId());
            invoice.setClient(client);
            invoice.setCreatedAt(LocalDateTime.now());

            // Validar detalles
            if (invoice.getInvoiceDetails() == null || invoice.getInvoiceDetails().isEmpty()) {
                throw new IllegalArgumentException("La factura debe tener al menos un detalle");
            }

            double total = 0.0;

            for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
                if (detail.getProduct() == null || detail.getProduct().getId() == null) {
                    throw new IllegalArgumentException("Cada detalle debe contener un producto con ID");
                }
                Product product = productRepository.findById(detail.getProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + detail.getProduct().getId()));

                if (product.getStock() < detail.getAmount()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto id: " + product.getId());
                }

                product.setStock(product.getStock() - detail.getAmount());
                productRepository.save(product);

                detail.setPrice(product.getPrice());
                detail.setInvoice(invoice);

                total += detail.getAmount() * product.getPrice();
            }

            invoice.setTotal(total);
            return invoiceRepository.save(invoice);
        } catch (Exception e) {
            // Log del error
            System.err.println("Error al crear la factura: " + e.getMessage());
            throw e; // Relanza la excepción para que Spring la maneje
        }
    }

    // Actualizar una factura (solo campos básicos)
    @Transactional
    public Invoice updateInvoice(Integer id, Invoice invoiceDetails) {
        Invoice invoice = getInvoiceById(id);

        // Actualizar cliente si se proporciona
        if (invoiceDetails.getClient() != null && invoiceDetails.getClient().getId() != null) {
            Client client = clientService.getClientById(invoiceDetails.getClient().getId());
            invoice.setClient(client);
        }

        // Actualizar otros campos (ejemplo: total manual)
        if (invoiceDetails.getTotal() != null) {
            invoice.setTotal(invoiceDetails.getTotal());
        }

        return invoiceRepository.save(invoice);
    }

    // Eliminar una factura
    @Transactional
    public void deleteInvoice(Integer id) {
        Invoice invoice = getInvoiceById(id);
        invoiceRepository.delete(invoice);
    }
}
package com.example.facturacionPrimeraEntregaNosicoski.Service;

import com.example.facturacionPrimeraEntregaNosicoski.Exception.ResourceNotFoundException;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Client;
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

    // Retorna todas las facturas
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Retorna una factura por ID
    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id " + id));
    }

    // Crea una factura a partir de la estructura:
    // {
    //   "client": { "id": 1 },
    //   "invoiceDetails": [
    //       { "amount": 2, "product": { "id": 3 } },
    //       { "amount": 1, "product": { "id": 5 } }
    //   ]
    // }
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        // Verificar que se haya proporcionado el cliente y obtenerlo
        if (invoice.getClient() == null || invoice.getClient().getId() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        Client client = clientService.getClientById(invoice.getClient().getId());
        invoice.setClient(client);
        invoice.setCreatedAt(LocalDateTime.now());

        double total = 0.0;

        // Verificar que existan líneas en la factura
        if (invoice.getInvoiceDetails() == null || invoice.getInvoiceDetails().isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una línea en la factura");
        }

        // Procesar cada línea (detalle) de la factura
        for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
            if (detail.getProduct() == null || detail.getProduct().getId() == null) {
                throw new IllegalArgumentException("Cada línea debe contener un producto con ID");
            }
            Product product = productRepository.findById(detail.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + detail.getProduct().getId()));

            if (product.getStock() < detail.getAmount()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto id: " + product.getId());
            }

            // Actualizar stock del producto
            product.setStock(product.getStock() - detail.getAmount());
            productRepository.save(product);

            // Asignar el precio actual del producto al detalle
            detail.setPrice(product.getPrice());
            detail.setInvoice(invoice);

            total += detail.getAmount() * product.getPrice();
        }
        invoice.setTotal(total);
        return invoiceRepository.save(invoice);
    }

    // Actualiza la factura (por ejemplo, se puede actualizar el cliente)
    public Invoice updateInvoice(Integer id, Invoice invoiceDetails) {
        Invoice invoice = getInvoiceById(id);

        // Actualizar el cliente si se proporciona
        if (invoiceDetails.getClient() != null && invoiceDetails.getClient().getId() != null) {
            Client client = clientService.getClientById(invoiceDetails.getClient().getId());
            invoice.setClient(client);
        }

        // Se pueden actualizar otros campos si fuese necesario.
        // En este ejemplo, si se proporciona un total, se actualiza directamente.
        if (invoiceDetails.getTotal() != null) {
            invoice.setTotal(invoiceDetails.getTotal());
        }

        // Nota: No se actualizan los detalles en este endpoint.
        return invoiceRepository.save(invoice);
    }

    // Elimina una factura
    public void deleteInvoice(Integer id) {
        Invoice invoice = getInvoiceById(id);
        invoiceRepository.delete(invoice);
    }
}

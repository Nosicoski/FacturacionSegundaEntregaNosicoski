package com.example.facturacionPrimeraEntregaNosicoski.Service;

import com.example.facturacionPrimeraEntregaNosicoski.Exception.ResourceNotFoundException;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Invoice;
import com.example.facturacionPrimeraEntregaNosicoski.Model.InvoiceDetail;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Product;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.InvoiceDetailRepository;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.InvoiceRepository;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class InvoiceDetailService {private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    public InvoiceDetailService(InvoiceDetailRepository invoiceDetailRepository, InvoiceRepository invoiceRepository, ProductRepository productRepository) {
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }

    public InvoiceDetail getInvoiceDetailById(Integer id) {
        return invoiceDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de factura no encontrado con id " + id));
    }

    public InvoiceDetail addInvoiceDetail(Integer invoiceId, InvoiceDetail detail) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id " + invoiceId));
        Product product = productRepository.findById(detail.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + detail.getProduct().getId()));

        if (product.getStock() < detail.getAmount()) {
            throw new IllegalArgumentException("Stock insuficiente para el producto id: " + product.getId());
        }

        // Actualizar stock
        product.setStock(product.getStock() - detail.getAmount());
        productRepository.save(product);

        detail.setPrice(product.getPrice());
        detail.setInvoice(invoice);
        InvoiceDetail savedDetail = invoiceDetailRepository.save(detail);

        // Actualizar total de la factura
        double additionalTotal = detail.getAmount() * product.getPrice();
        invoice.setTotal(invoice.getTotal() + additionalTotal);
        invoiceRepository.save(invoice);

        return savedDetail;
    }
}
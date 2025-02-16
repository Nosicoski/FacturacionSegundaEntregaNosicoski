package com.example.facturacionPrimeraEntregaNosicoski.Repository;

import com.example.facturacionPrimeraEntregaNosicoski.Model.InvoiceDetail;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}

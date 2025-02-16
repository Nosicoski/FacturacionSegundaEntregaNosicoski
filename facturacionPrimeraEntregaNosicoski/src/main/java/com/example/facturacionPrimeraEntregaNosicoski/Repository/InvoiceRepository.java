package com.example.facturacionPrimeraEntregaNosicoski.Repository;

import com.example.facturacionPrimeraEntregaNosicoski.Model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByClientId(Integer clientId);
}

package com.example.facturacionPrimeraEntregaNosicoski.Repository;

import com.example.facturacionPrimeraEntregaNosicoski.Model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}

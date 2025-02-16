package com.example.facturacionPrimeraEntregaNosicoski.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

  @Entity
    @Table(name = "clients")
    public class Client {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 75)
        private String name;

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 75)
        private String lastname;

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(max = 11)
        @Column(name = "docnumber", unique = true)
        private String docnumber;

        @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Invoice> invoices;

        // Getters y Setters

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getDocnumber() {
            return docnumber;
        }

        public void setDocnumber(String docnumber) {
            this.docnumber = docnumber;
        }

        public List<Invoice> getInvoices() {
            return invoices;
        }

        public void setInvoices(List<Invoice> invoices) {
            this.invoices = invoices;
        }
    }


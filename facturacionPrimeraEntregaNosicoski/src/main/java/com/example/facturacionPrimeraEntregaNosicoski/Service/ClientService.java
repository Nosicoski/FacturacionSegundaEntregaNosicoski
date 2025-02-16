package com.example.facturacionPrimeraEntregaNosicoski.Service;

import com.example.facturacionPrimeraEntregaNosicoski.Exception.ResourceNotFoundException;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Client;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Integer id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Integer id, Client clientDetails) {
        Client client = getClientById(id);
        client.setName(clientDetails.getName());
        client.setLastname(clientDetails.getLastname());
        client.setDocnumber(clientDetails.getDocnumber());
        return clientRepository.save(client);
    }

    public void deleteClient(Integer id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }
}
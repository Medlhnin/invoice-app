package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.RequestClientDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.REPOSITORIES.ClientRepository;
import com.example.demo.SERVICES.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return ResponseEntity.ok(clients);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody RequestClientDTO request) {
        Client client = new Client(request.nameEnterprise(),
                request.nameContact(),
                request.address(),
                request.ville(),
                request.codePostal(),
                request.phoneNumber(),
                request.mail_address(),
                request.ICE());
        client.setPublicId(UUID.randomUUID());
        clientRepository.save(client);
        return ResponseEntity.ok(client);
    }


}

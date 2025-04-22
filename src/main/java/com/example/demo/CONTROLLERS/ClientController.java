package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.RequestClientDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.MAPPERS.ClientMapper;
import com.example.demo.REPOSITORIES.ClientRepository;
import com.example.demo.SERVICES.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return ResponseEntity.ok(clients);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody RequestClientDTO request) {
        Client client = new Client();
        clientMapper.updateFromDto(request, client);
        client.setPublicId(UUID.randomUUID());
        clientRepository.save(client);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id){
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody RequestClientDTO updated) {

        Client client = clientRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

        clientMapper.updateFromDto(updated, client);
        clientRepository.save(client);
        return ResponseEntity.noContent().build();
    }


}

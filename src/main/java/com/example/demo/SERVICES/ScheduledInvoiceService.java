package com.example.demo.SERVICES;

import com.example.demo.DTOs.ScheduledInvoiceRequest;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.ScheduledInvoice;
import com.example.demo.MAPPERS.ScheduledInvoiceMapper;
import com.example.demo.REPOSITORIES.ClientRepository;
import com.example.demo.REPOSITORIES.ScheduledInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledInvoiceService {
    private final ScheduledInvoiceRepository scheduledInvoiceRepository;
    private final ClientRepository clientRepository;
    private final ScheduledInvoiceMapper scheduledInvoiceMapper;

    public ScheduledInvoice handleScheduledInvoice(ScheduledInvoiceRequest scheduledInvoiceRequest){
        Client client = clientRepository.findByPublicId(scheduledInvoiceRequest.publicId()).
                orElseThrow(() -> new RuntimeException("Client not found"));
        ScheduledInvoice scheduledInvoice = new ScheduledInvoice();
        scheduledInvoiceMapper.updateFromDto(scheduledInvoiceRequest, scheduledInvoice);
        scheduledInvoice.setClient(client);
        scheduledInvoice.setDestination(client.getMail_address());
        scheduledInvoice.setLastGenerated(LocalDateTime.now());
        return scheduledInvoiceRepository.save(scheduledInvoice);
    }
}

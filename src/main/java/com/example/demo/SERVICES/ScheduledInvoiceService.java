package com.example.demo.SERVICES;

import com.example.demo.DTOs.ScheduledInvoiceRequest;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.ScheduledInvoice;
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

    public ScheduledInvoice handleScheduledInvoice(ScheduledInvoiceRequest scheduledInvoiceRequest){
        Client client = clientRepository.findByPublicId(scheduledInvoiceRequest.publicId()).
                orElseThrow(() -> new RuntimeException("Client not found"));
        ScheduledInvoice scheduledInvoice = new ScheduledInvoice();
        scheduledInvoice.setClient(client);
        scheduledInvoice.setDestination(client.getMail_address());
        scheduledInvoice.setProjectDescription(scheduledInvoiceRequest.projectDescription());
        scheduledInvoice.setTva(scheduledInvoiceRequest.tva());
        scheduledInvoice.setFees_disbursements(scheduledInvoiceRequest.fees_disbursements());
        scheduledInvoice.setDeposit(scheduledInvoiceRequest.deposit());
        scheduledInvoice.setAmount(scheduledInvoiceRequest.amount());
        scheduledInvoice.setFrequency(scheduledInvoiceRequest.frequency());
        scheduledInvoice.setLastGenerated(LocalDateTime.now());
        scheduledInvoice.setDelaiEnJours(scheduledInvoiceRequest.delaiEnJours());
        return scheduledInvoiceRepository.save(scheduledInvoice);
    }
}

package com.example.demo.PROCESSORS;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.INTERFACES.InvoiceTriggerStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvoiceProcessor {
    private InvoiceTriggerStrategy triggerStrategy;

    public void processInvoice(Client client, InvoiceRequestDTO request) {
        triggerStrategy.triggerInvoice(client, request);
    }
}

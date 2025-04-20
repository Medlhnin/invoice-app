package com.example.demo.INTERFACES;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.Invoice;

public interface InvoiceTriggerStrategy {
    Invoice triggerInvoice(Client client, InvoiceRequestDTO request);
}

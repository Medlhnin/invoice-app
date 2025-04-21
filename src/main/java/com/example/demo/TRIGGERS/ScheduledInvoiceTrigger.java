package com.example.demo.TRIGGERS;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.INTERFACES.InvoiceTriggerStrategy;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("scheduled")
@RequiredArgsConstructor
public class ScheduledInvoiceTrigger implements InvoiceTriggerStrategy {
    private final InvoiceRepository invoiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledInvoiceTrigger.class);

    @Override
    public Invoice triggerInvoice(Client client, InvoiceRequestDTO request) {

        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setDestination(client.getMail_address());
        invoice.setProjectDescription(request.projectDescription());
        invoice.setAmount(request.amount());
        invoice.setDateFacture(LocalDateTime.now());
        invoice.setMode(Mode.SCHEDULED);
        invoice.setTva(request.tva());
        invoice.setDueDate(request.dueDate());
        invoice.setFees_disbursements(request.fees_disbursements());
        invoice.setDeposit(request.deposit());
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        invoice.setExpectedDate(request.expectedDateTime());
        invoiceRepository.save(invoice);
        logger.info("Scheduled invoice saved");
        return invoice;
    }

}

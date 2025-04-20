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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service("manual")
@RequiredArgsConstructor
public class ManualInvoiceTrigger implements InvoiceTriggerStrategy {
    private final InvoiceRepository invoiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(ManualInvoiceTrigger.class);
    @Override
    public Invoice triggerInvoice(Client client, InvoiceRequestDTO request) {
        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setDestination(client.getMail_address());
        invoice.setProjectDescription(request.projectDescription());
        invoice.setAmount(request.amount());
        invoice.setDateFacture(LocalDateTime.now());
        invoice.setDueDate(request.dueDate());
        invoice.setMode(Mode.MANUEL);
        invoice.setTva(request.tva());
        invoice.setFees_disbursements(request.fees_disbursements());
        invoice.setDeposit(request.deposit());
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        invoice.setExpectedDate(request.expectedDateTime());
        logger.info("Invoice created");
        invoiceRepository.save(invoice);
        logger.info("Invoice saved");
        return invoice;
    }
}

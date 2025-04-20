package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.EmailDTO;
import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.REPOSITORIES.ClientRepository;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import com.example.demo.SERVICES.EmailService;
import com.example.demo.SERVICES.InvoiceService;
import com.example.demo.SERVICES.PostmarkEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private final PostmarkEmailService postmarkEmailService;

    @PostMapping
    public ResponseEntity<Void> triggerInvoice(@RequestBody InvoiceRequestDTO invoiceRequestDTO
                                              , UriComponentsBuilder ucb) throws MessagingException, IOException {
        logger.info("triggerInvoice has been enabled");
        Invoice invoice = invoiceService.createInvoice(invoiceRequestDTO);
        URI locationOfInvoice = ucb
                .path("api/v1/invoice")
                .buildAndExpand(invoice.getId())
                .toUri();

        return ResponseEntity.created(locationOfInvoice).build();
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(){
        List<Invoice> invoices = invoiceRepository.findAll();
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sentEmail(@RequestBody EmailDTO emailDTO){
        postmarkEmailService.sendEmail(emailDTO.to(), emailDTO.subject(), emailDTO.text());
        logger.info("Controller");
        return ResponseEntity.noContent().build();
    }


}

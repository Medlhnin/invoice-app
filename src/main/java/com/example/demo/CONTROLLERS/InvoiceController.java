package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.PaymentMethod;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import com.example.demo.SERVICES.InvoiceService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);


    @PostMapping
    public ResponseEntity<Void> triggerInvoice(@RequestBody InvoiceRequestDTO invoiceRequestDTO
                                              , UriComponentsBuilder ucb) throws MessagingException, IOException {
        logger.info("triggerInvoice has been enabled");
        Invoice invoice = invoiceService.createInvoice(invoiceRequestDTO);
        URI locationOfInvoice = ucb
                .path("api/v1/invoice/{id}")
                .buildAndExpand(invoice.getId())
                .toUri();

        return ResponseEntity.created(locationOfInvoice).build();
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(){
        List<Invoice> invoices = invoiceRepository.findAll();
        return ResponseEntity.ok(invoices);
    }


    @PutMapping("/{id}/payment")
    public ResponseEntity<Void> updatePaymentInfo(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        invoice.setPaymentMethod(PaymentMethod.valueOf(payload.get("paymentMethod").toString()));
        invoice.setInvoiceStatus(InvoiceStatus.Paid);
        invoice.setDatePayment(LocalDateTime.now());
        if (invoice.getPaymentMethod() == PaymentMethod.CHEQUE) {
            invoice.setCheque_number(Long.parseLong(payload.get("cheque_number").toString()));
            invoice.setRemise_number(Long.parseLong(payload.get("remise_number").toString()));
        }

        invoiceRepository.save(invoice);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmReception(@RequestParam Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        invoice.setInvoiceStatus(InvoiceStatus.Pending);
        invoiceRepository.save(invoice);

        return ResponseEntity.ok("Merci pour votre confirmation !");
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<String> validInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        invoice.setInvoiceStatus(InvoiceStatus.Valid);
        invoiceRepository.save(invoice);

        return ResponseEntity.ok("La facture est bien validée");
    }




}

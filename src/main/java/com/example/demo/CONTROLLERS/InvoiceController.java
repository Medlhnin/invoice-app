package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.DTOs.RequestClientDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.PaymentMethod;
import com.example.demo.MAPPERS.InvoiceMapper;
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
    private final InvoiceMapper invoiceMapper;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);


    @PostMapping
    public ResponseEntity<Void> triggerInvoice(@RequestBody InvoiceRequestDTO invoiceRequestDTO
                                              , UriComponentsBuilder ucb) throws MessagingException, IOException {
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

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id){
        return invoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}/payment")
    public ResponseEntity<Void> updatePaymentInfo(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        invoice.setPaymentMethod(PaymentMethod.valueOf(payload.get("paymentMethod").toString()));
        invoice.setInvoiceStatus(InvoiceStatus.Paid);
        if (invoice.getPaymentMethod() == PaymentMethod.CHEQUE) {
            invoice.setCheque_number(Long.parseLong(payload.get("cheque_number").toString()));
            invoice.setRemise_number(Long.parseLong(payload.get("remise_number").toString()));
            invoice.setDatePayment(LocalDateTime.parse(payload.get("datePayment").toString()));
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

    @PutMapping("/{id}/validate")
    public ResponseEntity<Void> validInvoice(@PathVariable Long id) throws MessagingException, IOException {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        invoice.setInvoiceStatus(InvoiceStatus.Valid);
        logger.info("Invoice validated");
        invoiceRepository.save(invoice);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/revert")
    public ResponseEntity<Void> revertInvoice(@PathVariable Long id){
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        /* invoiceService.cancelScheduledEmail(id); */
        invoiceRepository.save(invoice);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody InvoiceRequestDTO updatedRequest) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "INVOICE NOT FOUND"));

        if (invoice.getInvoiceStatus() == InvoiceStatus.Draft) {
            invoiceMapper.requestToInvoice(updatedRequest, invoice);
            invoiceRepository.save(invoice);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only invoices with status 'Draft' can be updated"
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        Invoice invoice = invoiceRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "REMOVED Client NOT FOUND "));
        invoiceRepository.delete(invoice);
        return ResponseEntity.noContent().build();

    }


}

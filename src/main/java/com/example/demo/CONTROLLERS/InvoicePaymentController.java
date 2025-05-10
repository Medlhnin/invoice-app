package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.InvoicePaymentRequestDTO;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENTITIES.InvoicePayment;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.PaymentMethod;
import com.example.demo.REPOSITORIES.InvoicePaymentRepository;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invoice-payment")
@RequiredArgsConstructor
public class InvoicePaymentController {

    private final InvoicePaymentRepository invoicePaymentRepository;
    private final InvoiceRepository invoiceRepository;

    @PostMapping("/{id}")
    public ResponseEntity<Void> updatePaymentInfo(@PathVariable Long id,
                                                  @RequestBody Map<String, Object> payload) {

        InvoicePayment invoicePayment = new InvoicePayment();
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        invoicePayment.setPaymentMethod(PaymentMethod.valueOf(payload.get("paymentMethod").toString()));
        double amountPaid = Double.parseDouble(payload.get("amount").toString());
        invoicePayment.setAmount(amountPaid);
        invoice.increaseAmountPaid(amountPaid);
        invoicePayment.setPaymentDate(LocalDateTime.parse(payload.get("datePayment").toString()));
        invoicePayment.setNotes(payload.get("notes").toString());
        invoicePayment.setInvoice(invoice);
        invoice.setInvoiceStatus(InvoiceStatus.Paid);
        if (invoicePayment.getPaymentMethod() == PaymentMethod.CHEQUE) {
            invoicePayment.setCheque_number(Long.parseLong(payload.get("cheque_number").toString()));
            invoicePayment.setRemise_number(Long.parseLong(payload.get("remise_number").toString()));
        }

        invoicePaymentRepository.save(invoicePayment);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<InvoicePayment>> getAllInvoices(){
        List<InvoicePayment> invoices = invoicePaymentRepository.findAll();
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody InvoicePaymentRequestDTO updatedRequest) {

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        InvoicePayment invoicePayment = invoicePaymentRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "REMOVED Client NOT FOUND "));
        invoicePaymentRepository.delete(invoicePayment);
        return ResponseEntity.noContent().build();
    }


}

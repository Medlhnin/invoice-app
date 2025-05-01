package com.example.demo.CONTROLLERS;

import com.example.demo.DTOs.ScheduledInvoiceRequest;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENTITIES.ScheduledInvoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.MAPPERS.InvoiceMapper;
import com.example.demo.MAPPERS.ScheduledInvoiceMapper;
import com.example.demo.REPOSITORIES.ScheduledInvoiceRepository;
import com.example.demo.SERVICES.ScheduledInvoiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/scheduled-invoices")
@RequiredArgsConstructor
public class ScheduledInvoiceController {
    private final ScheduledInvoiceRepository repository;
    private final ScheduledInvoiceService scheduledInvoiceService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledInvoiceController.class);
    private final ScheduledInvoiceMapper scheduledInvoiceMapper;
    private final InvoiceMapper invoiceMapper;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ScheduledInvoiceRequest scheduledInvoiceRequest,
                                       UriComponentsBuilder ucb) {
        ScheduledInvoice scheduledInvoice = scheduledInvoiceService.handleScheduledInvoice(scheduledInvoiceRequest);
        URI locationOfTemplate = ucb
                .path("api/v1/scheduled-invoices")
                .buildAndExpand(scheduledInvoice.getId())
                .toUri();
        return ResponseEntity.created(locationOfTemplate).build();
    }

    @GetMapping
    public List<ScheduledInvoice> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledInvoice> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody ScheduledInvoiceRequest updated) {
        ScheduledInvoice scheduledInvoice = repository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

        scheduledInvoiceMapper.updateFromDto(updated, scheduledInvoice);
        repository.save(scheduledInvoice);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Invoice> previewNextInvoice(@PathVariable Long id) {
        Optional<ScheduledInvoice> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ScheduledInvoice template = optional.get();
        Invoice invoice = new Invoice();

        invoiceMapper.scheduledInvoiceToInvoice(template, invoice);
        invoice.setDateFacture(LocalDateTime.now());
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        invoice.setMode(Mode.SCHEDULED);

        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateScheduledInvoice(@PathVariable Long id) {
        ScheduledInvoice scheduledInvoice = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modèle non trouvé"));

        logger.info("Invoice deactivated");
        scheduledInvoice.setActive(false);
        repository.save(scheduledInvoice);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateScheduledInvoice(@PathVariable Long id) {
        ScheduledInvoice scheduledInvoice = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modèle non trouvé"));

        scheduledInvoice.setActive(true);
        repository.save(scheduledInvoice);
        return ResponseEntity.noContent().build();
    }

}

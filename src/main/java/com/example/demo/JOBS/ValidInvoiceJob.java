package com.example.demo.JOBS;

import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import com.example.demo.SERVICES.InvoiceService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidInvoiceJob {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private static final Logger logger = LoggerFactory.getLogger(ValidInvoiceJob.class);

    @Scheduled(cron = "0 */5 * * * *")
    public void markInvoicesAsSent() throws MessagingException, IOException {
        List<Invoice> validInvoices = invoiceRepository.findAllByInvoiceStatus(InvoiceStatus.Valid);
        LocalDateTime now = LocalDateTime.now();

        for (Invoice invoice : validInvoices) {
            if (invoice.getExpectedDateTime() != null && invoice.getExpectedDateTime().isBefore(now)) {
                try {
                    invoiceService.SendEmailTreatment(invoice);
                } catch (Exception e) {
                    logger.error("❌ Erreur lors de l'envoi de l'email pour la facture ID {}", invoice.getId(), e);
                }
                invoice.setInvoiceStatus(InvoiceStatus.Sent);
                logger.info("Facture ID {} est envoyée (date: {}).", invoice.getId(), invoice.getExpectedDateTime());
            }
        }

        invoiceRepository.saveAll(validInvoices);
    }
}

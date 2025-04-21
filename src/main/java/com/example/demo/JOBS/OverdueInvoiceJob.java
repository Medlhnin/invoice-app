package com.example.demo.JOBS;

import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OverdueInvoiceJob {
    private final InvoiceRepository invoiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(OverdueInvoiceJob.class);

    @Scheduled(cron = "0 0 0 * * *")
    public void markInvoicesAsOverdue() {
        List<Invoice> pendingInvoices = invoiceRepository.findAllByInvoiceStatus(InvoiceStatus.Pending);
        LocalDateTime now = LocalDateTime.now();

        for (Invoice invoice : pendingInvoices) {
            if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(now)) {
                invoice.setInvoiceStatus(InvoiceStatus.Overdue);
                logger.info("Facture ID {} passée en Overdue (échéance: {}).", invoice.getId(), invoice.getDueDate());
            }
        }

        invoiceRepository.saveAll(pendingInvoices);
    }
}

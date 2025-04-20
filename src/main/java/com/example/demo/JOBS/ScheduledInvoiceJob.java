package com.example.demo.JOBS;


import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.Frequency;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import com.example.demo.SERVICES.InvoiceService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ScheduledInvoiceJob {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ScheduledInvoiceJob.class);

    @Scheduled(cron = "0 0 0 * * *")
    public void generatePeriodicInvoices() throws IOException, MessagingException {
        logger.info("generatePeriodicInvoices has executed");
        List<Invoice> recurringInvoices = invoiceRepository.findAllByFrequencyNot(Frequency.NONE);

        for (Invoice invoice : recurringInvoices) {
            // On considère uniquement les factures créées automatiquement
            if (invoice.getMode() == Mode.SCHEDULED) {
                LocalDateTime lastDateTime = invoice.getDateFacture();

                LocalDateTime nextDueDateTime = switch (invoice.getFrequency()) {
                    case MINUTELY -> lastDateTime.plusMinutes(1);
                    case WEEKLY -> lastDateTime.plusWeeks(1);
                    case MONTHLY -> lastDateTime.plusMonths(1);
                    case QUARTERLY -> lastDateTime.plusMonths(3);
                    case ANNUALLY -> lastDateTime.plusYears(1);
                    default -> null;
                };

                if (nextDueDateTime != null && !nextDueDateTime.isAfter(LocalDateTime.now())) {
                    // Générer une nouvelle facture
                    Invoice newInvoice = cloneInvoiceForNextPeriod(invoice);
                    invoiceRepository.save(newInvoice);
                    logger.info("Invoice saved.");
                    invoiceService.SendEmailAndChangeInvoiceStatus(invoice);
                    logger.info("Invoice has been sent to the client: {}", invoice.getClient().getNameEnterprise());

                }
            }
        }
    }

    private Invoice cloneInvoiceForNextPeriod(Invoice original) {
        Invoice invoice = new Invoice();
        invoice.setClient(original.getClient());
        invoice.setDestination(original.getDestination());
        invoice.setProjectDescription(original.getProjectDescription());
        invoice.setAmount(original.getAmount());
        invoice.setDateFacture(LocalDateTime.now());
        invoice.setMode(Mode.SCHEDULED);
        invoice.setTva(original.getTva());
        invoice.setFees_disbursements(original.getFees_disbursements());
        invoice.setDeposit(original.getDeposit());
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        invoice.setFrequency(original.getFrequency());
        return invoice;
    }


}


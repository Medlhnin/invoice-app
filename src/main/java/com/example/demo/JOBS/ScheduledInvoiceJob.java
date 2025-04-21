package com.example.demo.JOBS;


import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENTITIES.ScheduledInvoice;
import com.example.demo.ENUMS.Frequency;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import com.example.demo.REPOSITORIES.ScheduledInvoiceRepository;
import com.example.demo.SERVICES.InvoiceService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ScheduledInvoiceJob {

    private final ScheduledInvoiceRepository scheduledInvoiceRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    @Scheduled(cron = "0 * * * * *")
    public void generateInvoicesFromTemplates() throws MessagingException, IOException {
        List<ScheduledInvoice> templates = scheduledInvoiceRepository.findAll();

        for (ScheduledInvoice template : templates) {
            LocalDateTime last = template.getLastGenerated();
            LocalDateTime nextDate = calculateNextDueDate(last, template.getFrequency());

            if (nextDate != null && !nextDate.isAfter(LocalDateTime.now())) {
                Invoice invoice = generateInvoiceFromTemplate(template);
                invoiceRepository.save(invoice);

                template.setLastGenerated(LocalDateTime.now());
                scheduledInvoiceRepository.save(template);

                invoiceService.SendEmailAndChangeInvoiceStatus(invoice);
            }
        }
    }

    private Invoice generateInvoiceFromTemplate(ScheduledInvoice template) {
        Invoice invoice = new Invoice();
        invoice.setScheduledSource(template);
        invoice.setClient(template.getClient());
        invoice.setProjectDescription(template.getProjectDescription());
        invoice.setAmount(template.getAmount());
        invoice.setTva(template.getTva());
        invoice.setFees_disbursements(template.getFees_disbursements());
        invoice.setDeposit(template.getDeposit());
        invoice.setDestination(template.getDestination());
        invoice.setDateFacture(LocalDateTime.now());
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        invoice.setMode(Mode.SCHEDULED);
        invoice.setDueDate(LocalDateTime.now().plusDays(template.getDelaiEnJours()));

        return invoice;
    }

    private LocalDateTime calculateNextDueDate(LocalDateTime last, Frequency frequency) {
        if (last == null) return LocalDateTime.now();

        return switch (frequency) {
            case MINUTELY -> last.plusMinutes(1);
            case WEEKLY -> last.plusWeeks(1);
            case MONTHLY -> last.plusMonths(1);
            case QUARTERLY -> last.plusMonths(3);
            case ANNUALLY -> last.plusYears(1);
            default -> null;
        };
    }


}


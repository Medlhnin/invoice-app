package com.example.demo.SERVICES;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.FACTORIES.InvoiceTriggerFactory;
import com.example.demo.INTERFACES.InvoiceTriggerStrategy;
import com.example.demo.REPOSITORIES.ClientRepository;
import com.example.demo.REPOSITORIES.InvoiceRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceTriggerFactory triggerFactory;
    private final TaskScheduler taskScheduler;
    private final AbstractEmailService abstractEmailService;
    private final PostmarkEmailService postmarkEmailService;
    private final EmailService emailService;
    private final PdfGeneratorService pdfGeneratorService;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    public Invoice createInvoice(InvoiceRequestDTO request) throws MessagingException, IOException {
        Client client = clientRepository.findByPublicId(request.publicId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        InvoiceTriggerStrategy strategy = triggerFactory.getStrategy(request.mode());
        logger.info("create has been enabled");
        Invoice invoice = strategy.triggerInvoice(client, request);
        SendEmailTreatment(invoice);
        return invoice;
    }

    public void SendEmailTreatment(Invoice invoice) throws MessagingException, IOException {
        if (invoice.getExpectedDate() == null || invoice.getExpectedDate().isBefore(LocalDateTime.now())) {
            logger.info("Expected date is {}", invoice.getExpectedDate());
            logger.info("email already sent");
            SendEmailAndChangeInvoiceStatus(invoice);
        } else {
            logger.info("email is waiting to be sent");
            taskScheduler.schedule(() -> {
                        try {
                            SendEmailAndChangeInvoiceStatus(invoice);
                        } catch (MessagingException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    Date.from(invoice.getExpectedDate().atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    public void SendEmailAndChangeInvoiceStatus(Invoice invoice) throws MessagingException, IOException {
        if (!invoice.isEmailSent()) {

            // Invoice PDF generation
            byte[] pdf = pdfGeneratorService.generatePdfFromInvoice(invoice);
            // Build URL to confirm the reception
            String confirmationUrl = "http://localhost:9001/api/v1/invoice/confirm?id=" + invoice.getId();

            // Send email
            emailService.sendEmail(
                    invoice.getDestination(),
                    "📄 Votre facture est prête",
                    "Veuillez trouver ci-joint votre facture.",
                    pdf,
                    "facture-" + invoice.getId() + ".pdf",
                    confirmationUrl
            );

            // Update the status
            invoice.setInvoiceStatus(InvoiceStatus.Sent);
            logger.info("Invoice status has changed and email sent to {}", invoice.getDestination());

            invoiceRepository.save(invoice);
        }
    }

}

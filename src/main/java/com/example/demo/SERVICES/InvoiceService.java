package com.example.demo.SERVICES;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.MAPPERS.InvoiceMapper;
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
    private final TaskScheduler taskScheduler;
    private final EmailService emailService;
    private final AzureEmailService azureEmailService;
    private final PdfGeneratorService pdfGeneratorService;
    private final InvoiceMapper invoiceMapper;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    public Invoice createInvoice(InvoiceRequestDTO request) throws MessagingException, IOException {
        Client client = clientRepository.findByPublicId(request.publicId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Invoice invoice = new Invoice();
        invoiceMapper.requestToInvoice(request, invoice);

        invoice.setClient(client);
        invoice.setDestination(client.getMail_address());
        invoice.setDateFacture(LocalDateTime.now());
        invoice.setMode(Mode.MANUEL);
        invoice.setInvoiceStatus(InvoiceStatus.Draft);
        logger.info("Invoice created");
        invoiceRepository.save(invoice);
        logger.info("Invoice saved");
        return invoice;
    }

    public void SendEmailTreatment(Invoice invoice) throws MessagingException, IOException {
        if (invoice.getExpectedDateTime() == null || invoice.getExpectedDateTime().isBefore(LocalDateTime.now())) {
            logger.info("Expected date is {}", invoice.getExpectedDateTime());
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
                    Date.from(invoice.getExpectedDateTime().atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    public void SendEmailAndChangeInvoiceStatus(Invoice invoice) throws MessagingException, IOException {
        logger.info("isEmailSent: {}", invoice.isEmailSent());
        if (!invoice.isEmailSent()) {

            byte[] pdf = pdfGeneratorService.generatePdfFromInvoice(invoice);
            logger.info("PDF generated.");

            String confirmationUrl = "http://localhost:9001/api/v1/invoice/confirm?id=" + invoice.getId();

            emailService.sendEmail(
                    invoice.getDestination(),
                    "📄 Votre facture est prête",
                    "Veuillez trouver ci-joint votre facture.",
                    pdf,
                    "facture-" + invoice.getId() + ".pdf",
                    confirmationUrl
            );
            logger.info("email already sent");
            // Update the status
            invoice.setInvoiceStatus(InvoiceStatus.Sent);
            logger.info("Invoice status has changed and email sent to {}", invoice.getDestination());

            invoiceRepository.save(invoice);
        }
    }

}

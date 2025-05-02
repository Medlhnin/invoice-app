package com.example.demo.SERVICES;

import com.azure.communication.email.*;
import com.azure.communication.email.implementation.models.EmailContent;
import com.azure.communication.email.implementation.models.EmailRecipients;
import com.azure.communication.email.models.*;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

@Service
public class AzureEmailService {
    @Value("${azure.communication.email.connection-string}")
    private String connectionString;

    private static final Logger logger = LoggerFactory.getLogger(AzureEmailService.class);

    public void sendEmail(String to,
                          String subject,
                          String messageBody,
                          byte[] pdfBytes,
                          String fileName,
                          String confirmationUrl) throws IOException {

        EmailClient emailClient = new EmailClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        logger.info("From: DoNotReply@muner.me");
        logger.info("To: {}", to);

        // HTML avec bouton
        String htmlContent = """
            <p>%s</p>
            <p>
                <a href="%s" style="padding: 10px 20px; background-color: #28a745; color: white; text-decoration: none; border-radius: 4px;">
                    ✅ Confirmer la réception
                </a>
            </p>
        """.formatted(messageBody, confirmationUrl);

        EmailContent content = new EmailContent(subject)
                .setHtml(htmlContent);

        EmailRecipients recipients = new EmailRecipients()
                .setTo(Collections.singletonList(new EmailAddress(to)));

        // Ajouter une pièce jointe PDF
        EmailAttachment attachment = new EmailAttachment(
                fileName,
                "application/pdf",
                BinaryData.fromString(Base64.getEncoder().encodeToString(pdfBytes))

        );

        EmailMessage message = new EmailMessage()
                .setSenderAddress("DoNotReply@muner.me")
                .setSubject(subject)
                .setBodyHtml(htmlContent)
                .setToRecipients(new EmailAddress(to))
                .setAttachments(attachment);

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message);
        EmailSendResult result = poller.getFinalResult();


        logger.info("✅ Email envoyé. Message ID: {}", result.getStatus());
    }
}

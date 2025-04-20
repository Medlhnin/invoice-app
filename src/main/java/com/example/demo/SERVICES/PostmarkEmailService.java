package com.example.demo.SERVICES;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostmarkEmailService {

    @Value("${postmark.api.token}")
    private String postmarkApiToken;
    @Value("${postmark.sender.email}")
    private String senderEmail;

    private static final Logger logger = LoggerFactory.getLogger(PostmarkEmailService.class);
    private final PdfGeneratorService pdfGeneratorService;



    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, String subject, String body) {
        logger.info("Sending email starts here.");
        String postmarkUrl = "https://api.postmarkapp.com/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Postmark-Server-Token", postmarkApiToken);

        Map<String, Object> request = new HashMap<>();
        request.put("From", senderEmail);
        request.put("To", to);
        request.put("Subject", subject);
        request.put("TextBody", body);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(postmarkUrl, entity, String.class);
        logger.info("Voici la réponse: {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send email via Postmark: " + response.getBody());
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentBytes, String fileName) {
        logger.info("Sending email with PDF attachment...");

        String postmarkUrl = "https://api.postmarkapp.com/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Postmark-Server-Token", postmarkApiToken);

        // Créer la pièce jointe
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("Name", fileName);
        attachment.put("Content", Base64.getEncoder().encodeToString(attachmentBytes));
        attachment.put("ContentType", "application/pdf");

        // Corps de la requête
        Map<String, Object> request = new HashMap<>();
        request.put("From", senderEmail);
        request.put("To", to);
        request.put("Subject", subject);
        request.put("TextBody", body);
        request.put("Attachments", List.of(attachment));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // Appel HTTP
        ResponseEntity<String> response = restTemplate.postForEntity(postmarkUrl, entity, String.class);

        logger.info("Réponse Postmark : {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de l'envoi de l'email via Postmark : " + response.getBody());
        }
    }
}


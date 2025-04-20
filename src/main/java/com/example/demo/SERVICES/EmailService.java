package com.example.demo.SERVICES;

import com.example.demo.CONTROLLERS.InvoiceController;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.activation.DataSource;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String email;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentBytes, String fileName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        logger.info("Email : {}", email);
        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);

        // Partie HTML
        helper.setText(body, true);

        // Pièce jointe (optionnelle)
        if (attachmentBytes != null && fileName != null) {
            DataSource dataSource = new ByteArrayDataSource(attachmentBytes, "application/pdf");
            helper.addAttachment(fileName, dataSource);
        }

        mailSender.send(mimeMessage);
    }

}



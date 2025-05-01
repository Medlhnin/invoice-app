package com.example.demo.SERVICES;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
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
    private String email = "lahnin.010@gmail.com";
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to,
                          String subject,
                          String messageBody,
                          byte[] pdfBytes,
                          String fileName,
                          String confirmationUrl) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        logger.info("From is: {}", email);
        logger.info("To is: {}", to);
        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);

        // HTML avec bouton
        String htmlContent = """
        <p>%s</p>
        <p><a href="%s" style="padding: 10px 20px; background-color: #28a745; color: white; text-decoration: none; border-radius: 4px;">
            ✅ Confirmer la réception
        </a></p>
    """.formatted(messageBody, confirmationUrl);

        helper.setText(htmlContent, true);

        // Attachement PDF
        DataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
        helper.addAttachment(fileName, dataSource);

        mailSender.send(mimeMessage);
    }


}



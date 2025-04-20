package com.example.demo.SERVICES;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Invoice;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final HtmlGeneratorService htmlGeneratorService;
    private static final Logger logger = LoggerFactory.getLogger(PostmarkEmailService.class);

    public byte[] generatePdfFromInvoice(Invoice invoice) throws IOException {
        try {
            // generate le HTML with Thymeleaf engine
            String htmlContent = htmlGeneratorService.generateInvoiceHtml(invoice);

            // transform to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (RuntimeException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF de la facture", e);
        }
    }

}


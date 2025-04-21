package com.example.demo.SERVICES;

import com.example.demo.ENTITIES.Client;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.REPOSITORIES.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HtmlGeneratorService {

    private final TemplateEngine templateEngine;
    private final ClientRepository clientRepository;

    public String generateInvoiceHtml(Invoice invoice) throws IOException {
        Context context = new Context();
        Map<String, Object> data = new HashMap<>();

        // Client
        Client client = clientRepository.findByPublicId(invoice.getClient().getPublicId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        data.put("clientName", client.getNameEnterprise());
        data.put("clientAddress", client.getAddress());
        data.put("clientCity", client.getVille());
        data.put("clientPhone", client.getPhoneNumber());
        data.put("clientICE", client.getIce());
        data.put("clientEmail", client.getMail_address());
        data.put("dueDate", invoice.getDueDate());


        data.put("invoiceNumber", UUID.randomUUID());
        data.put("invoiceDate", LocalDateTime.now());
        data.put("missionDescription", invoice.getProjectDescription());


        data.put("fees", invoice.getAmount());
        data.put("disbursements", invoice.getFees_disbursements());
        data.put("diposit", invoice.getDeposit());
        data.put("tva", invoice.getTva());
        data.put("total", invoice.getAmount() + invoice.getFees_disbursements() + invoice.getTva());

        String css = new String(Files.readAllBytes(Paths.get("src/main/resources/static/css/template_style.css")), StandardCharsets.UTF_8);
        data.put("css", css);

        context.setVariables(data);

        return templateEngine.process("invoice-template", context);
    }
}
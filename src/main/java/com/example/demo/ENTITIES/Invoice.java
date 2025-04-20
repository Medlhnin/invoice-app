package com.example.demo.ENTITIES;

import com.example.demo.ENUMS.Frequency;
import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.ENUMS.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.apache.el.parser.AstFalse;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "INVOICE_TABLE")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;
    private String destination;
    private LocalDateTime dateFacture ;
    private String projectDescription;
    private double tva;
    private double fees_disbursements;
    private double deposit;
    private double amount;
    @Enumerated(EnumType.STRING)
    private Mode mode;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private Long cheque_number;
    private Long remise_number;
    private LocalDate datePayment;
    private LocalDateTime expectedDate;
    private LocalDateTime dueDate;
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    public boolean isEmailSent(){
        return this.invoiceStatus != InvoiceStatus.Draft;
    }

}

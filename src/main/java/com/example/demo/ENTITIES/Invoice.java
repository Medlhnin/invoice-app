package com.example.demo.ENTITIES;

import com.example.demo.ENUMS.InvoiceStatus;
import com.example.demo.ENUMS.Mode;
import com.example.demo.ENUMS.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "INVOICE_TABLE")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "invoice_seq")
    @SequenceGenerator(name = "invoice_seq", sequenceName = "invoice_table_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @Column(name = "numero_chronologique", unique = true)
    private String numeroChronologique;
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
    private LocalDateTime expectedDateTime;
    private LocalDateTime dueDate;
    @ManyToOne
    @JoinColumn(name = "scheduled_invoice_id")
    private ScheduledInvoice scheduledSource;
    private double amountPaid;


    public boolean isEmailSent(){
        return this.invoiceStatus != InvoiceStatus.Valid;
    }

    public void increaseAmountPaid(double amount){
        this.amountPaid += amount;
    }

    public void decreaseAmountPaid(double amount){
        this.amountPaid -= amount;
    }

    public void invoiceStaus(){
        if(this.amountPaid >= this.amount) {
            this.invoiceStatus = InvoiceStatus.Paid;
        }
        else {
            this.invoiceStatus = InvoiceStatus.partiallyPaid;
        }
    }



}

package com.example.demo.ENTITIES;

import com.example.demo.ENUMS.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private double amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    private Long cheque_number;
    private Long remise_number;

    @Column(name = "notes", length = 500)
    private String notes;

    public void increaseAmountPaid(double amountPayed) {
        this.amount += amountPayed;
    }

}

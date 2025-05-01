package com.example.demo.ENTITIES;

import com.example.demo.ENUMS.Frequency;
import com.example.demo.ENUMS.Mode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class ScheduledInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Client client;

    private String destination;
    private String projectDescription;
    private double tva;
    private double fees_disbursements;
    private double deposit;
    private double amount;
    private int delaiEnJours;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Enumerated(EnumType.STRING)
    private Mode mode = Mode.SCHEDULED;
    private boolean active = true;


    private LocalDateTime lastGenerated;
}

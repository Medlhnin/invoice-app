package com.example.demo.DTOs;

import com.example.demo.ENUMS.PaymentMethod;

import java.time.LocalDateTime;

public record InvoicePaymentRequestDTO(double amount,
                                       LocalDateTime paymentDate,
                                       PaymentMethod paymentMethod,
                                       Long cheque_number,
                                       Long remise_number,
                                       String notes) {
}

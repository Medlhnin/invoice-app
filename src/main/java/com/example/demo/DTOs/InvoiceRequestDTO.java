package com.example.demo.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvoiceRequestDTO(UUID publicId,
                                String projectDescription,
                                double amount,
                                double tva,
                                double fees_disbursements,
                                double deposit,
                                String mode,
                                LocalDateTime expectedDateTime,
                                LocalDateTime dueDate
                         ) {
}

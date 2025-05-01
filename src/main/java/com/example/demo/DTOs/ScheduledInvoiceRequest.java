package com.example.demo.DTOs;

import com.example.demo.ENUMS.Frequency;

import java.util.UUID;

public record ScheduledInvoiceRequest(UUID publicId,
                                      String projectDescription,
                                      double tva,
                                      double fees_disbursements,
                                      double deposit,
                                      double amount,
                                      Frequency frequency,
                                      int delaiEnJours) {
}

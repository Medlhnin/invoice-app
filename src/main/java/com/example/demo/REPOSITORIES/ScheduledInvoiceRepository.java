package com.example.demo.REPOSITORIES;

import com.example.demo.ENTITIES.ScheduledInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledInvoiceRepository extends JpaRepository<ScheduledInvoice, Long> {
}

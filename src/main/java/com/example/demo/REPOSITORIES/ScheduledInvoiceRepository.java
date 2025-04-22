package com.example.demo.REPOSITORIES;

import com.example.demo.ENTITIES.ScheduledInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledInvoiceRepository extends JpaRepository<ScheduledInvoice, Long> {
    List<ScheduledInvoice> findAllByActiveTrue();
}

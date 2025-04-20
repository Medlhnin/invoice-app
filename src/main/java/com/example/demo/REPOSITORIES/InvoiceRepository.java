package com.example.demo.REPOSITORIES;

import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByFrequencyNot(Frequency frequency);
}

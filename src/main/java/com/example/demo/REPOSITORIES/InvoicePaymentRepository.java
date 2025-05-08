package com.example.demo.REPOSITORIES;

import com.example.demo.ENTITIES.InvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, Long> {

}

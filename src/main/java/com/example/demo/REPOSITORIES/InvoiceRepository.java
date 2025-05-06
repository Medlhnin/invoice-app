package com.example.demo.REPOSITORIES;

import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENUMS.InvoiceStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByInvoiceStatus(InvoiceStatus invoiceStatus);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE YEAR(i.dateFacture) = :annee AND MONTH(i.dateFacture) = :mois")
    long countByMonth(@Param("annee") int annee, @Param("mois") int mois);

}

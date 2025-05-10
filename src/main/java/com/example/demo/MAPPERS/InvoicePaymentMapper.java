package com.example.demo.MAPPERS;

import com.example.demo.DTOs.InvoicePaymentRequestDTO;
import com.example.demo.ENTITIES.InvoicePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoicePaymentMapper {
    @Mapping(target = "id", ignore = true)
    void requestToInvoicePayment(InvoicePaymentRequestDTO dto, @MappingTarget InvoicePayment invoicePayment);
}

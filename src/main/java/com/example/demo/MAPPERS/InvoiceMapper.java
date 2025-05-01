package com.example.demo.MAPPERS;

import com.example.demo.DTOs.InvoiceRequestDTO;
import com.example.demo.ENTITIES.Invoice;
import com.example.demo.ENTITIES.ScheduledInvoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(target = "id", ignore = true)
    void requestToInvoice(InvoiceRequestDTO dto, @MappingTarget Invoice invoice);

    @Mapping(target = "id", ignore = true)
    void scheduledInvoiceToInvoice(ScheduledInvoice scheduledInvoice, @MappingTarget Invoice invoice);
}

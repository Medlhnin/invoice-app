package com.example.demo.MAPPERS;

import com.example.demo.DTOs.ScheduledInvoiceRequest;
import com.example.demo.ENTITIES.ScheduledInvoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScheduledInvoiceMapper {
    @Mapping(target = "id", ignore = true)
    void updateFromDto(ScheduledInvoiceRequest dto, @MappingTarget ScheduledInvoice entity);
}

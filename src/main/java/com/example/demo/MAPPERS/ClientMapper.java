package com.example.demo.MAPPERS;

import com.example.demo.DTOs.RequestClientDTO;
import com.example.demo.ENTITIES.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "id", ignore = true)
    void updateFromDto(RequestClientDTO dto, @MappingTarget Client client);
}

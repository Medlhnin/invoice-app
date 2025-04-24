package com.example.demo.MAPPERS;

import com.example.demo.DTOs.SignUpDto;
import com.example.demo.DTOs.UserDto;
import com.example.demo.ENTITIES.Utilisateur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(Utilisateur user);
    Utilisateur signUpToUser(SignUpDto signUpDto);
}

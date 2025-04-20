package com.example.demo.DTOs;

public record RequestClientDTO(String nameEnterprise,
                               String nameContact,
                               String address,
                               String ville,
                               int codePostal,
                               String phoneNumber,
                               String mail_address,
                               int ICE) {
}

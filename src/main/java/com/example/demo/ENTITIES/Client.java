package com.example.demo.ENTITIES;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "CLIENT_TABLE")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "enterprise_id")
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId = UUID.randomUUID();

    private String nameEnterprise;
    private String nameContact;
    private String address;
    private String ville;
    private int codePostal;
    private String phoneNumber;
    private String mail_address;
    private int ICE;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Invoice> invoices;

    public Client(String nameEnterprise, String nameContact, String address, String ville, int codePostal,
                  String phoneNumber, String mail_address, int ICE)
    {
        this.publicId = UUID.randomUUID();
        this.address = address;
        this.nameEnterprise = nameEnterprise;
        this.nameContact = nameContact;
        this.ville = ville;
        this.codePostal = codePostal;
        this.phoneNumber = phoneNumber;
        this.mail_address = mail_address;
        this.ICE = ICE;
    }


}

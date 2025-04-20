package com.example.demo.REPOSITORIES;

import com.example.demo.CONTROLLERS.ClientController;
import com.example.demo.ENTITIES.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByPublicId(UUID publicID);
}

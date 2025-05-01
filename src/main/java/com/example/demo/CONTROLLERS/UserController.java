package com.example.demo.CONTROLLERS;

import com.example.demo.CONFIGURATIONS.UserAuthenticationProvider;
import com.example.demo.DTOs.CredentialsDto;
import com.example.demo.DTOs.SignUpDto;
import com.example.demo.DTOs.UserDto;
import com.example.demo.ENTITIES.Utilisateur;
import com.example.demo.Exceptions.AppException;
import com.example.demo.REPOSITORIES.UserRepository;
import com.example.demo.SERVICES.EmailService;
import com.example.demo.SERVICES.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody SignUpDto user) {
        logger.info("📥 Tentative d'enregistrement d'un nouvel utilisateur");
        UserDto createdUser = userService.register(user);
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
        logger.info("created User: {}", createdUser);
        return ResponseEntity.created(URI.create("/api/v1/user/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> resetPassword(@RequestBody String email){

        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<Utilisateur> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new AppException("USER NOT FOUND", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(user);
    }

}

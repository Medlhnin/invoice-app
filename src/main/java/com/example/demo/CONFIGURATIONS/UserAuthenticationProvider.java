package com.example.demo.CONFIGURATIONS;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.DTOs.UserDto;
import com.example.demo.SERVICES.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDto user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 86400000); // 1 day

        Algorithm algorithm = Algorithm.HMAC256(secretKey); // Use of Hashing256 for encryption
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("firstname", user.getFirstname())
                .withClaim("lastname", user.getLastname())
                .withClaim("role", user.getRole())
                .withClaim("email", user.getEmail())
                .sign(algorithm);
    }

    public Authentication validateToken(String token) {
        logger.info("🔍 Début de validation du token...");
        Algorithm algorithm = Algorithm.HMAC256(secretKey); // Use of Hashing256 for decryption

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        logger.info("✅ Token validé avec succès !");
        logger.info("👤 Utilisateur : {}", decoded.getSubject());
        logger.info("🔖 Rôle : {}", decoded.getClaim("role").asString());

        String role = decoded.getClaim("role").asString();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UserDto user = UserDto.builder()
                .username(decoded.getSubject())
                .firstname(decoded.getClaim("firstname").asString())
                .lastname(decoded.getClaim("lastname").asString())
                .role(role)
                .email(decoded.getClaim("email").asString())
                .build();

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    public Authentication validateTokenStrongly(String token) {
        logger.info("🔍 Début de validation du token...");
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        logger.info("✅ Token validé avec succès !");
        logger.info("👤 Utilisateur : {}", decoded.getSubject());
        logger.info("🔖 Rôle : {}", decoded.getClaim("role").asString());

        String role = decoded.getClaim("role").asString();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UserDto user = userService.findByLogin(decoded.getSubject());

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

}

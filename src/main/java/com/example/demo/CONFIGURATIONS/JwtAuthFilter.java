package com.example.demo.CONFIGURATIONS;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserAuthenticationProvider userAuthenticationProvider;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String path = request.getServletPath();

        logger.info("📌 Requête reçue sur le chemin : {}", path);

        if (path.equals("/api/v1/user/register") || path.equals("/api/v1/user/login")) {
            logger.info("🔓 Pas besoin de JWT pour {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        if (header != null) {
            logger.info("✅ Header Authorization reçu : {}", header);

            String[] authElements = header.split(" ");
            if (authElements.length == 2 && "Bearer".equals(authElements[0])) {
                try {
                    if ("GET".equals(request.getMethod())) {
                        logger.info("🔍 Validation simple du token pour GET");
                        SecurityContextHolder.getContext().setAuthentication(
                                userAuthenticationProvider.validateToken(authElements[1]));


                    } else {
                        logger.info("🔒 Validation renforcée du token pour {}", request.getMethod());
                        SecurityContextHolder.getContext().setAuthentication(
                                userAuthenticationProvider.validateTokenStrongly(authElements[1]));


                    }
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null) {
                        logger.info("\uD83D\uDD0D Rôles attribués après authentification : {}", auth.getAuthorities());
                    }
                } catch (RuntimeException e) {
                    logger.error("❌ Erreur lors de la validation du token : {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired JWT token");
                    return;
                }
            } else {
                logger.warn("⚠️ Format du token invalide !");
            }
        } else {
            logger.warn("⚠️ Aucun header Authorization trouvé !");
        }
        logger.info("🔍 Utilisateur après authentification : " + SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }
}

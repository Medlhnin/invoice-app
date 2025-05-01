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

        logger.info("📌 Request received on the path : {}", path);

        if (path.equals("/api/v1/user/register") || path.equals("/api/v1/user/login")) {
            logger.info("🔓 No JWT required for {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        if (header != null) {
            logger.info("✅ Authorization header received : {}", header);

            String[] authElements = header.split(" ");
            if (authElements.length == 2 && "Bearer".equals(authElements[0])) {
                try {
                    if ("GET".equals(request.getMethod())) {
                        logger.info("🔍 Basic token verification for GET");
                        SecurityContextHolder.getContext().setAuthentication(
                                userAuthenticationProvider.validateToken(authElements[1]));


                    } else {
                        logger.info("🔒 Advanced token validation for {}", request.getMethod());
                        SecurityContextHolder.getContext().setAuthentication(
                                userAuthenticationProvider.validateTokenStrongly(authElements[1]));


                    }
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null) {
                        logger.info("\uD83D\uDD0D Permissions assigned upon successful auth : {}", auth.getAuthorities());
                    }
                } catch (RuntimeException e) {
                    logger.error("❌ Error during token validation : {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired JWT token");
                    return;
                }
            } else {
                logger.warn("⚠️ Malformed token !");
            }
        } else {
            logger.warn("⚠️ No Authorization header found !");
        }
        logger.info("\uD83D\uDD0D User post-authentication : {}", SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }
}

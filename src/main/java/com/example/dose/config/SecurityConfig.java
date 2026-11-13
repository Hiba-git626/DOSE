package com.example.dose.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;

/**
 * Sécurité de l'API : sans session, jetons JWT signés HS256, routes publiques limitées à la config,
 * l'authentification et la confirmation du tuteur. Les routes /me sont réservées au rôle TEEN.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecretKey jwtKey;

    public SecurityConfig(@Value("${dose.security.jwt-secret}") String base64Secret) {
        byte[] bytes = Base64.getDecoder().decode(base64Secret.trim()); //transforme le texte de la clé en octets
        if (bytes.length < 32)  { //refuse une clé trop courte (HS256 exige au moins 256 bits)
            throw new IllegalArgumentException("dose.security.jwt-secret must contain at least 32 bytes (Base64)");
        }
        this.jwtKey = new SecretKeySpec(bytes, "HmacSHA256"); //nom de l’algorithme de signature, pas un secret
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/config", "/api/v1/auth/**", "/api/v1/guardian/**",
                                "/error", "/actuator/health").permitAll()
                        .requestMatchers("/api/v1/me", "/api/v1/me/**").hasRole("TEEN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(response, 401, "INVALID_CREDENTIALS",
                                        "Authentication required or token expired"))
                        .accessDeniedHandler((request, response, e) ->
                                writeError(response, 403, "FORBIDDEN", "Access denied")));
        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /** Lit le rôle dans la revendication « role » du jeton et en fait l'autorité ROLE_xxx. */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    private static void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"" + message + "\",\"fields\":{}}");
    }
}

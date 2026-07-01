package de.htw_belin.Bookblock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Zentrale Sicherheitskonfiguration.
 *
 * Das Backend arbeitet als OAuth2 Resource Server: Jeder Request muss ein
 * gueltiges JWT-Access-Token von Okta im Header "Authorization: Bearer ..."
 * mitbringen. Spring prueft das Token automatisch gegen den Okta-Issuer.
 *
 * Hinweis: In Spring Security 6 / Spring Boot 4 gibt es den im Auth-PDF
 * gezeigten WebSecurityConfigurerAdapter nicht mehr. Stattdessen definiert man
 * eine SecurityFilterChain als Bean - inhaltlich dasselbe.
 */
@Configuration
public class SecurityConfig {

    private final List<String> allowedOrigins;

    public SecurityConfig(@Value("${app.cors.allowed-origins}") String origins) {
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS erlauben (Frontend liegt auf anderer Domain)
                .cors(Customizer.withDefaults())
                // Keine CSRF-Token noetig: wir nutzen Bearer-Tokens, keine Cookies
                .csrf(csrf -> csrf.disable())
                // Kein Server-seitiger Login-Zustand: jedes Token steht fuer sich
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight-Anfragen des Browsers immer durchlassen
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Health-Check ohne Login (fuer Render)
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        // Alles andere nur mit gueltigem Token
                        .anyRequest().authenticated())
                // Tokens als JWT vom Okta-Issuer validieren
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /** Erlaubt dem Render-Frontend (und lokal localhost) den Zugriff auf die API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

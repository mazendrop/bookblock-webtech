package de.htw_belin.Bookblock.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Zentrale Sicherheitskonfiguration mit eigener Nutzerverwaltung.
 *
 * - Passwoerter werden mit BCrypt gehasht.
 * - Nach dem Login stellt das Backend ein JWT aus (HMAC-signiert mit einem
 *   geheimen Schluessel) und prueft es bei jeder weiteren Anfrage selbst.
 * - /auth/register und /auth/login sind ohne Token erreichbar, alles andere
 *   nur mit gueltigem Token.
 */
@Configuration
public class SecurityConfig {

    private final List<String> allowedOrigins;
    private final SecretKey jwtKey;

    public SecurityConfig(@Value("${app.cors.allowed-origins}") String origins,
                          @Value("${app.jwt.secret}") String secret) {
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        this.jwtKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // Keine CSRF-Token noetig: wir nutzen Bearer-Tokens, keine Cookies
                .csrf(csrf -> csrf.disable())
                // Kein Server-seitiger Login-Zustand: jedes Token steht fuer sich
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight-Anfragen des Browsers immer durchlassen
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Registrieren + Login ohne Token erreichbar
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        // Alles andere nur mit gueltigem Token
                        .anyRequest().authenticated())
                // Unsere eigenen JWTs pruefen (mit dem jwtDecoder-Bean unten)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /** BCrypt zum Hashen und Pruefen der Passwoerter. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Signiert neue JWTs mit unserem geheimen Schluessel. */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    /** Prueft eingehende JWTs mit demselben geheimen Schluessel. */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
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

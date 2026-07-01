package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.User;
import de.htw_belin.Bookblock.repository.UserRepository;
import de.htw_belin.Bookblock.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Registrierung und Login mit eigener Nutzerverwaltung.
 *
 * - Passwoerter werden mit BCrypt gehasht gespeichert (nie im Klartext).
 * - Nach erfolgreichem Login/Registrieren gibt es ein JWT zurueck, das das
 *   Frontend bei allen weiteren Anfragen als "Bearer"-Token mitschickt.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwt) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody AuthRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-Mail ist bereits registriert");
        }
        User user = new User(email, passwordEncoder.encode(req.password()));
        users.save(user);
        return tokenResponse(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody AuthRequest req) {
        String email = req.email().trim().toLowerCase();
        User user = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-Mail oder Passwort falsch"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-Mail oder Passwort falsch");
        }
        return tokenResponse(user);
    }

    private Map<String, String> tokenResponse(User user) {
        return Map.of(
                "token", jwt.createToken(user.getEmail()),
                "email", user.getEmail());
    }
}

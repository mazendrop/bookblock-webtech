package de.htw_belin.Bookblock.service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Erzeugt signierte JWT-Access-Tokens fuer angemeldete Nutzer.
 * Das "subject" des Tokens ist die E-Mail - daran erkennt das Backend spaeter,
 * wem eine Anfrage gehoert.
 */
@Service
public class JwtService {

    private static final long GUELTIG_TAGE = 7;

    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String createToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bookblock")
                .issuedAt(now)
                .expiresAt(now.plus(GUELTIG_TAGE, ChronoUnit.DAYS))
                .subject(email)
                .build();
        // Wichtig: explizit HS256 (symmetrischer Schluessel). Ohne diesen Header
        // wuerde Spring RS256 (RSA) annehmen und keinen passenden Schluessel finden.
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}

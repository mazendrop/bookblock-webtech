package de.htw_belin.Bookblock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Rauchtest: Startet die komplette Anwendung einmal hoch.
 *
 * Der JwtDecoder wird gemockt, damit beim Start KEIN echter Okta-Server
 * kontaktiert werden muss - der Test laeuft so auch offline in der CI.
 */
@SpringBootTest
class BookblockApplicationTests {

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@Test
	void die_anwendung_startet_ohne_fehler() {
		// Wenn der Spring-Kontext ohne Exception hochfaehrt, ist der Test gruen.
	}

}

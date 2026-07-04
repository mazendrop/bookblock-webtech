package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Prueft die Autorisierung im Controller: Der Besitzer wird IMMER aus dem
 * Token gelesen (subject = E-Mail). Egal was der Client sonst mitschickt -
 * jede:r arbeitet nur mit den eigenen Buechern.
 *
 * Bewusst als schlanker Unit-Test (JUnit + Mockito), damit er schnell laeuft
 * und in der Praesentation leicht zu erklaeren ist.
 */
class BookControllerSecurityTest {

    private final BookService service = mock(BookService.class);
    private final BookController controller = new BookController(service);

    /** Baut ein echtes JWT mit der gewuenschten E-Mail als subject. */
    private Jwt tokenFuer(String email) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .subject(email)
                .build();
    }

    @Test
    void der_controller_fragt_nur_die_buecher_des_angemeldeten_nutzers_an() {
        BookEntry buch = new BookEntry("1984", "George Orwell", "READING");
        given(service.getBooksOf("lina@example.com")).willReturn(List.of(buch));

        List<BookEntry> ergebnis = controller.getMyBooks(tokenFuer("lina@example.com"));

        assertThat(ergebnis).containsExactly(buch);
        verify(service).getBooksOf("lina@example.com");
    }

    @Test
    void beim_speichern_wird_der_besitzer_aus_dem_token_gesetzt() {
        BookEntry neu = new BookEntry("Faust", "Goethe", "WANT_TO_READ");

        controller.addBook(neu, tokenFuer("ali@example.com"));

        // Der Controller reicht die E-Mail aus dem Token als Besitzer weiter
        verify(service).save(neu, "ali@example.com");
    }
}

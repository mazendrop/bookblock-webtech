package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.config.SecurityConfig;
import de.htw_belin.Bookblock.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueft die Autorisierung der Leseliste.
 *
 * Kernidee: Ohne gueltiges Token gibt es keinen Zugriff, und mit Token sieht
 * man nur die eigenen Buecher (Besitzer = E-Mail aus dem Token).
 */
@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class BookControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void ohne_token_wird_die_leseliste_abgelehnt() throws Exception {
        // Kein Authorization-Header -> 401 Unauthorized
        mvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mit_gueltigem_token_bekommt_der_nutzer_seine_leseliste() throws Exception {
        // Der Service liefert die Buecher genau dieses Nutzers (Besitzer = E-Mail)
        given(bookService.getBooksOf("lina@example.com")).willReturn(List.of());

        mvc.perform(get("/books").with(jwt().jwt(token -> token.subject("lina@example.com"))))
                .andExpect(status().isOk());
    }
}

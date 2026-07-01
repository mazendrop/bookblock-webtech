package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.service.BookService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-Endpunkte fuer die persoenliche Leseliste.
 *
 * Der Besitzer wird immer aus dem gepruefeten JWT gelesen ("sub" = eindeutige
 * Okta-Nutzer-ID). So sieht und aendert jede:r nur die eigenen Buecher -
 * unabhaengig davon, was der Client mitschickt (Autorisierung im Backend).
 *
 * CORS wird zentral in SecurityConfig geregelt, deshalb kein @CrossOrigin mehr.
 */
@RestController
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/books")
    public List<BookEntry> getMyBooks(@AuthenticationPrincipal Jwt jwt) {
        return service.getBooksOf(ownerOf(jwt));
    }

    @PostMapping("/books")
    public BookEntry addBook(@Valid @RequestBody BookEntry book, @AuthenticationPrincipal Jwt jwt) {
        return service.save(book, ownerOf(jwt));
    }

    @PutMapping("/books/{id}")
    public BookEntry updateBook(@PathVariable Long id,
                                @RequestBody BookEntry book,
                                @AuthenticationPrincipal Jwt jwt) {
        return service.updateStatus(id, book.getReadingStatus(), ownerOf(jwt));
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, ownerOf(jwt));
    }

    /** Eindeutige Nutzer-ID aus dem Token. */
    private String ownerOf(Jwt jwt) {
        return jwt.getSubject();
    }
}

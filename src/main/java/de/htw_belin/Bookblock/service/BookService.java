package de.htw_belin.Bookblock.service;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    /** Nur die Buecher des angemeldeten Nutzers. */
    public List<BookEntry> getBooksOf(String owner) {
        return repository.findByOwner(owner);
    }

    /** Speichert ein neues Buch und setzt den Besitzer serverseitig. */
    public BookEntry save(BookEntry book, String owner) {
        book.setOwner(owner);
        return repository.save(book);
    }

    /** Aendert den Lesestatus - aber nur, wenn das Buch dem Nutzer gehoert. */
    public BookEntry updateStatus(Long id, String readingStatus, String owner) {
        BookEntry book = requireOwnedBook(id, owner);
        book.setReadingStatus(readingStatus);
        return repository.save(book);
    }

    /** Loescht ein Buch - aber nur, wenn das Buch dem Nutzer gehoert. */
    public void delete(Long id, String owner) {
        BookEntry book = requireOwnedBook(id, owner);
        repository.delete(book);
    }

    /**
     * Laedt ein Buch und stellt sicher, dass es dem Nutzer gehoert.
     * Fremde oder unbekannte Buecher -> 404, damit niemand erraten kann,
     * welche IDs anderen Nutzern gehoeren.
     */
    private BookEntry requireOwnedBook(Long id, String owner) {
        return repository.findById(id)
                .filter(book -> owner.equals(book.getOwner()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buch nicht gefunden"));
    }
}

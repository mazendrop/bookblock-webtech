package de.htw_belin.Bookblock.service;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueft die Multi-User-Trennung im Service.
 *
 * Kernidee: Jede:r sieht und aendert nur die eigenen Buecher. Fremde Buecher
 * sind unsichtbar (404) und koennen nicht geloescht werden.
 */
class BookServiceTest {

    private final BookRepository repository = mock(BookRepository.class);
    private final BookService service = new BookService(repository);

    @Test
    void ein_nutzer_bekommt_nur_seine_eigenen_buecher() {
        BookEntry linasBuch = new BookEntry("1984", "George Orwell", "READING");
        when(repository.findByOwner("lina@example.com")).thenReturn(List.of(linasBuch));

        List<BookEntry> ergebnis = service.getBooksOf("lina@example.com");

        assertThat(ergebnis).containsExactly(linasBuch);
    }

    @Test
    void fremde_buecher_koennen_nicht_geloescht_werden() {
        BookEntry alisBuch = new BookEntry("Faust", "Goethe", "READING");
        alisBuch.setOwner("ali@example.com");
        when(repository.findById(42L)).thenReturn(Optional.of(alisBuch));

        // Lina versucht, Alis Buch zu loeschen -> Fehler, und nichts wird geloescht
        assertThatThrownBy(() -> service.delete(42L, "lina@example.com"))
                .isInstanceOf(ResponseStatusException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void das_eigene_buch_wird_geloescht() {
        BookEntry linasBuch = new BookEntry("Faust", "Goethe", "READING");
        linasBuch.setOwner("lina@example.com");
        when(repository.findById(7L)).thenReturn(Optional.of(linasBuch));

        service.delete(7L, "lina@example.com");

        verify(repository).delete(linasBuch);
    }
}

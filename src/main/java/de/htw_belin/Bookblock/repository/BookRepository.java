package de.htw_belin.Bookblock.repository;

import de.htw_belin.Bookblock.model.BookEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<BookEntry, Long> {

    /** Liefert nur die Buecher, die dem angemeldeten Nutzer gehoeren. */
    List<BookEntry> findByOwner(String owner);
}

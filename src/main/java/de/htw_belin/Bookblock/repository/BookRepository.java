package de.htw_belin.Bookblock.repository;

import de.htw_belin.Bookblock.model.BookEntry;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<BookEntry, Long> {
}

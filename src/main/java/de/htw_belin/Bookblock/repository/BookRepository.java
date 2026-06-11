package de.htw_belin.Bookblock.repository;

import de.htw_belin.Bookblock.model.BookEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<BookEntry, Long> {
}

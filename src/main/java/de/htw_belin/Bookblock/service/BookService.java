package de.htw_belin.Bookblock.service;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Iterable<BookEntry> getAllBooks() {
        return repository.findAll();
    }

    public BookEntry save(BookEntry book) {
        return repository.save(book);
    }
}

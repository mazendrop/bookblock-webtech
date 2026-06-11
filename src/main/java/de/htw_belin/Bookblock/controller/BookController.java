package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.repository.BookRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class BookController {

    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/books")
    public Iterable<BookEntry> getAllBooks() {
        return repository.findAll();
    }

    @PostMapping("/books")
    public BookEntry addBook(@RequestBody BookEntry book) {
        return repository.save(book);
    }
}

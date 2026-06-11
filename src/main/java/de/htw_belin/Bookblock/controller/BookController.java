package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.service.BookService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/books")
    public Iterable<BookEntry> getAllBooks() {
        return service.getAllBooks();
    }

    @PostMapping("/books")
    public BookEntry addBook(@RequestBody BookEntry book) {
        return service.save(book);
    }
}

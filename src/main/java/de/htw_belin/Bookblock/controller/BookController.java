package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.BookEntry;
import de.htw_belin.Bookblock.service.BookService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/books/{id}")
    public BookEntry updateBook(@PathVariable Long id, @RequestBody BookEntry book) {
        return service.updateStatus(id, book.getReadingStatus());
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        service.delete(id);
    }
}

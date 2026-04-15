package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.model.BookEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {

    @GetMapping("/books")
    public List<BookEntry> getAllBooks() {
        return List.of(
                new BookEntry("Atomic Habits", "James Clear", "WANT_TO_READ"),
                new BookEntry("Clean Code", "Robert C. Martin", "READING"),
                new BookEntry("The Pragmatic Programmer", "Andrew Hunt", "FINISHED")
        );
    }
}
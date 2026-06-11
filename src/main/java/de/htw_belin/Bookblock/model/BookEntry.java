package de.htw_belin.Bookblock.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BookEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String readingStatus;

    public BookEntry() {
    }

    public BookEntry(String title, String author, String readingStatus) {
        this.title = title;
        this.author = author;
        this.readingStatus = readingStatus;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }
}

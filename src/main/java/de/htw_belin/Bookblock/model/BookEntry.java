package de.htw_belin.Bookblock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class BookEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel darf nicht leer sein")
    private String title;

    @NotBlank(message = "Autor darf nicht leer sein")
    private String author;

    @NotBlank(message = "Lesestatus darf nicht leer sein")
    private String readingStatus;

    private String thumbnail;

    @Column(length = 10000)
    private String description;

    // Besitzer des Eintrags = eindeutige Okta-Nutzer-ID (JWT "sub").
    // Wird serverseitig aus dem Token gesetzt, nie vom Client geschickt.
    private String owner;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

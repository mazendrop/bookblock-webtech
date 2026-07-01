package de.htw_belin.Bookblock.controller;

import de.htw_belin.Bookblock.service.GoogleBooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

// CORS wird zentral in SecurityConfig geregelt.
@RestController
public class GoogleBooksController {

    private final GoogleBooksService service;

    public GoogleBooksController(GoogleBooksService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(Map.of("results", java.util.List.of(), "totalItems", 0));
        }
        try {
            return ResponseEntity.ok(service.search(q, page));
        } catch (RestClientResponseException e) {
            // Fehlerstatus von Google (z. B. 429/403) transparent durchreichen
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", "Google Books: " + e.getStatusCode()));
        }
    }
}

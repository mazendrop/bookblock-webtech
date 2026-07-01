package de.htw_belin.Bookblock.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ruft die Google Books API server-seitig auf. Der API-Key bleibt im Backend
 * und taucht nie im Frontend-Bundle auf.
 *
 * Bewusst ohne Jackson-Import (JsonNode): Die JSON-Antwort wird in eine
 * {@code Map} deserialisiert. Das kompiliert unabhaengig davon, ob
 * jackson-databind auf dem Compile-Classpath liegt (bei Spring Boot 4 nicht),
 * und nutzt zur Laufzeit denselben Converter von spring-boot-starter-web.
 */
@Service
public class GoogleBooksService {

    private static final int PAGE_SIZE = 12;
    private static final String GOOGLE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient client = RestClient.create();
    private final String apiKey;

    public GoogleBooksService(@Value("${google.books.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    public Map<String, Object> search(String query, int page) {
        int startIndex = Math.max(0, (page - 1) * PAGE_SIZE);

        StringBuilder url = new StringBuilder(GOOGLE_URL)
                .append("?q=intitle:").append(URLEncoder.encode(query, StandardCharsets.UTF_8))
                .append("&maxResults=").append(PAGE_SIZE)
                .append("&startIndex=").append(startIndex)
                .append("&country=US");
        if (apiKey != null && !apiKey.isBlank()) {
            url.append("&key=").append(apiKey);
        }

        Map<String, Object> data = client.get().uri(url.toString()).retrieve().body(MAP_TYPE);

        List<Map<String, Object>> results = new ArrayList<>();
        Object itemsObj = data == null ? null : data.get("items");
        if (itemsObj instanceof List<?> items) {
            for (Object itemObj : items) {
                if (!(itemObj instanceof Map<?, ?> item)) continue;

                Object infoObj = item.get("volumeInfo");
                Map<?, ?> info = infoObj instanceof Map<?, ?> m ? m : Map.of();

                String thumbnail = null;
                if (info.get("imageLinks") instanceof Map<?, ?> images) {
                    Object t = images.get("thumbnail");
                    if (t == null) t = images.get("smallThumbnail");
                    if (t != null) thumbnail = t.toString().replace("http://", "https://");
                }

                String authors = "Unknown author";
                if (info.get("authors") instanceof List<?> list && !list.isEmpty()) {
                    List<String> a = new ArrayList<>();
                    for (Object o : list) a.add(String.valueOf(o));
                    authors = String.join(", ", a);
                }

                Map<String, Object> r = new HashMap<>();
                r.put("googleId", str(item.get("id"), ""));
                r.put("title", str(info.get("title"), "Untitled"));
                r.put("authors", authors);
                r.put("description", str(info.get("description"), "No description available."));
                r.put("thumbnail", thumbnail);
                r.put("publishedDate", str(info.get("publishedDate"), "n/a"));
                r.put("averageRating", info.get("averageRating") instanceof Number n ? n.doubleValue() : null);
                results.add(r);
            }
        }

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("results", results);
        Object total = data == null ? null : data.get("totalItems");
        pageResult.put("totalItems", total instanceof Number n ? n.intValue() : results.size());
        return pageResult;
    }

    private static String str(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }
}

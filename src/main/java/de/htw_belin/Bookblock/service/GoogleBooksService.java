package de.htw_belin.Bookblock.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ruft die Google Books API server-seitig auf. Der API-Key bleibt hier im
 * Backend und taucht nie im Frontend-Bundle auf.
 */
@Service
public class GoogleBooksService {

    private static final int PAGE_SIZE = 12;
    private static final String GOOGLE_URL = "https://www.googleapis.com/books/v1/volumes";

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

        JsonNode data = client.get().uri(url.toString()).retrieve().body(JsonNode.class);

        List<Map<String, Object>> results = new ArrayList<>();
        if (data != null && data.has("items")) {
            for (JsonNode item : data.get("items")) {
                JsonNode info = item.path("volumeInfo");

                String thumbnail = null;
                JsonNode images = info.path("imageLinks");
                if (!images.isMissingNode()) {
                    String t = images.path("thumbnail").asText(null);
                    if (t == null) t = images.path("smallThumbnail").asText(null);
                    if (t != null) thumbnail = t.replace("http://", "https://");
                }

                String authors = "Unknown author";
                if (info.has("authors")) {
                    List<String> list = new ArrayList<>();
                    info.get("authors").forEach(n -> list.add(n.asText()));
                    if (!list.isEmpty()) authors = String.join(", ", list);
                }

                Map<String, Object> r = new HashMap<>();
                r.put("googleId", item.path("id").asText(""));
                r.put("title", info.path("title").asText("Untitled"));
                r.put("authors", authors);
                r.put("description", info.path("description").asText("No description available."));
                r.put("thumbnail", thumbnail);
                r.put("publishedDate", info.path("publishedDate").asText("n/a"));
                r.put("averageRating", info.has("averageRating") ? info.get("averageRating").asDouble() : null);
                results.add(r);
            }
        }

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("results", results);
        pageResult.put("totalItems",
                data != null && data.has("totalItems") ? data.get("totalItems").asInt() : results.size());
        return pageResult;
    }
}

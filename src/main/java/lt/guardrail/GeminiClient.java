package lt.guardrail;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.io.IOException;

public class GeminiClient implements LlmClient {

    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public GeminiClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Gemini API key is missing");
        }

        this.apiKey = apiKey;
    }

    @Override
    public String generate(String modelId, String text) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + modelId + ":generateContent";

        String json = gson.toJson(Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of(
                                "text", text
                        ))
                ))
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "Gemini API error: " + response.statusCode()
                );
            }

            JsonObject body = JsonParser.parseString(
                    response.body()
            ).getAsJsonObject();

            StringBuilder answer = new StringBuilder();

            var parts = body.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts");

            for (var part : parts) {
                JsonObject object = part.getAsJsonObject();

                if (object.has("text") &&
                        (!object.has("thought") ||
                                !object.get("thought").getAsBoolean())) {
                    answer.append(object.get("text").getAsString());
                }
            }

            return answer.toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Gemini connection failed", e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Request interrupted", e
            );
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "Gemini request failed: " + e.getMessage(), e
            );
        }
    }
}
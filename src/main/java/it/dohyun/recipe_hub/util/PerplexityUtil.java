package it.dohyun.recipe_hub.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class PerplexityUtil {
  private static final PropertyUtil property =
      new PropertyUtil("api.properties", List.of("perplexity.api.key"));

  /** Low-level HTTP caller to Perplexity. Returns response body on HTTP 2xx. */
  public static String callPerplexityApi(String apiKey, String jsonPayload)
      throws IOException, InterruptedException {
    if (apiKey == null || apiKey.isBlank())
      throw new IllegalArgumentException("API key must not be null or blank");

    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("https://api.perplexity.ai/chat/completions"))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
            .build();

    HttpResponse<String> resp =
        client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    int status = resp.statusCode();
    String body = resp.body();
    if (status < 200 || status >= 300)
      throw new IOException("Perplexity API returned status=" + status + " body=" + body);
    JSONObject json = new JSONObject(body);
    return ((JSONObject) json.getJSONArray("choices").get(0))
        .getJSONObject("message")
        .getString("content");
  }

  public static JSONArray fetchNutritionRaw(String food) {
    String apiKey = property.getProperty("perplexity.api.key");
    if (food == null || food.isBlank()) food = "감자 샌드위치";
    if (apiKey == null || apiKey.isBlank()) return null; // explicit: no key -> treat as API failure

    String payload =
        "{\"model\":\"sonar-pro\",\"messages\":[{\"role\":\"user\",\"content\":\""
            + food.replace("\"", "\\\"")
            + "에 대한 영양성분 정보 5개를 알려줘. serve에는 g이나 ml당 단위를 넣어주고\"}],\"response_format\":{\"type\":\"json_schema\",\"json_schema\":{\"schema\":{\"result\":{\"type\":\"array\",\"properties\":{\"name\":{\"type\":\"string\"},\"serve\":{\"type\":\"integer\"},\"calories\":{\"type\":\"number\"},\"protein\":{\"type\":\"number\"},\"fat\":{\"type\":\"number\"},\"carbohydrates\":{\"type\":\"number\"}},\"required\":[\"name\",\"serve\",\"calories\",\"protein\",\"fat\",\"carbohydrates\"]}}}}}";

    try {
      try {
        return new JSONObject(callPerplexityApi(apiKey, payload)).getJSONArray("result");
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

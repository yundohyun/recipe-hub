package it.dohyun.recipe_hub.util;

import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class URLEncodeParser {

  /** PUT/DELETE 요청의 URL-encoded body 를 Map<String, String>으로 변환한다. */
  public static Map<String, String> parseUrlEncodedBody(HttpServletRequest request)
      throws IOException {

    // 1. Body 읽기
    StringBuilder requestBody = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {

      String line;
      while ((line = reader.readLine()) != null) {
        requestBody.append(line);
      }
    }

    String formData = requestBody.toString();
    System.out.println("RAW BODY = " + formData);

    // 2. 파싱한 데이터를 저장할 Map
    Map<String, String> paramMap = new HashMap<>();

    // 3. Body가 비어있지 않으면 파싱
    if (!formData.isBlank()) {
      String[] pairs = formData.split("&");
      for (String pair : pairs) {
        String[] keyValue = pair.split("=", 2); // value에 '=' 포함될 가능성 대비
        if (keyValue.length == 2) {
          String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
          String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
          paramMap.put(key, value);
        }
      }
    }

    return paramMap;
  }
}

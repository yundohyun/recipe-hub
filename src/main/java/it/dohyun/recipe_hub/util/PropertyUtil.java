package it.dohyun.recipe_hub.util;

import java.io.*;
import java.util.*;

public class PropertyUtil {
  private static final String BASE_PATH = "config/";
  private final Properties properties = new Properties();

  public PropertyUtil(String path, List<String> requireKeys) {
    if (path == null) throw new IllegalArgumentException("Path must not be null");

    try (InputStream is = getClass().getClassLoader().getResourceAsStream(BASE_PATH + path)) {
      if (is == null)
        throw new IllegalStateException("Properties file not found in: " + BASE_PATH + path);
      properties.load(is);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load properties file: " + BASE_PATH + path, e);
    }

    if (requireKeys == null) return;

    for (String key : requireKeys) {
      String val = properties.getProperty(key);
      if (val == null || val.trim().isEmpty())
        throw new IllegalStateException(
            "Require property '" + key + "' is missing or empty in: " + BASE_PATH + path);
    }
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public boolean exists(String key) {
    return properties.getProperty(key) != null;
  }
}

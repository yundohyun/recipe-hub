package it.dohyun.recipe_hub.util.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import it.dohyun.recipe_hub.util.PropertyUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class FirebaseUtil {
  private static final PropertyUtil property =
      new PropertyUtil(
          "firebase.properties",
          List.of(
              "firebase.service-account.path",
              "firebase.api.key",
              "firebase.auth.domain",
              "firebase.project.id",
              "firebase.storage.bukkit",
              "firebase.messaging.sender.id",
              "firebase.app.id"));

  public static void initialize() throws IOException {
    if (!FirebaseApp.getApps().isEmpty()) return;

    URL resource =
        FirebaseUtil.class
            .getClassLoader()
            .getResource(property.getProperty("firebase.service-account.path"));
    if (resource == null) throw new IOException("Cannot find firebase.json in the classpath.");

    File file;
    try {
      file = new File(resource.toURI());
    } catch (URISyntaxException e) {
      throw new IOException("Failed to convert resource URL to URI.", e);
    }

    if (!file.exists())
      throw new IOException("File firebase.json does not exist at path: " + file.getAbsolutePath());

    FileInputStream serviceAccount = new FileInputStream(file);

    FirebaseOptions options =
        FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket(property.getProperty("firebase.storage.bukkit"))
            .build();

    FirebaseApp.initializeApp(options);
  }

  public static StorageClient getStorageClient() {
    if (FirebaseApp.getApps().isEmpty())
      throw new IllegalStateException(
          "FirebaseApp has not been initialized. Call FirebaseUtil.initialize() first.");
    return StorageClient.getInstance(FirebaseApp.getInstance());
  }
}

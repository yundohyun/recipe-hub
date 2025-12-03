package it.dohyun.recipe_hub.util.firebase;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import java.io.*;

public class FirebaseStorageUtil {
  private static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] data = new byte[8192];
    int n;
    while ((n = input.read(data)) != -1) buffer.write(data, 0, n);
    return buffer.toByteArray();
  }

  public static String uploadFile(String blobName, InputStream input, String contentType)
      throws Exception {
    StorageClient storage = FirebaseUtil.getStorageClient();
    BlobId blobId = BlobId.of(storage.bucket().getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
    Blob blob = storage.bucket().getStorage().create(blobInfo, toByteArray(input));
    return getDownloadUrl(blobName);
  }

  public static boolean deleteFile(String blobName) {
    StorageClient storage = FirebaseUtil.getStorageClient();
    BlobId blobId = BlobId.of(storage.bucket().getName(), blobName);
    return storage.bucket().getStorage().delete(blobId);
  }

  public static String getDownloadUrl(String blobName) {
    StorageClient storage = FirebaseUtil.getStorageClient();
    String bucketName = storage.bucket().getName();
    return "https://firebasestorage.googleapis.com/v0/b/"
        + bucketName
        + "/o/"
        + blobName.replace("/", "%2F")
        + "?alt=media";
  }
}

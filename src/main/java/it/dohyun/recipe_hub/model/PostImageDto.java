package it.dohyun.recipe_hub.model;

import org.json.JSONObject;

public class PostImageDto {
  private String postId;
  private String imageUrl;

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("postId", postId);
    json.put("imageUrl", imageUrl);
    return json;
  }
}

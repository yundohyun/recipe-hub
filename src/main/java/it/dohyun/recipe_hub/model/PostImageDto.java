package it.dohyun.recipe_hub.model;

import org.json.JSONObject;

public class PostImageDto {
  private String postId;
  private String imageId;

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("postId", postId);
    json.put("imageId", imageId);
    return json;
  }
}

package it.dohyun.recipe_hub.model;

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
}

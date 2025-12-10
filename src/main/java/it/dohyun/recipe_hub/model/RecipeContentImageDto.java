package it.dohyun.recipe_hub.model;

import org.json.JSONObject;

public class RecipeContentImageDto {
  private String recipeContentId;
  private String imageUrl;

  public String getRecipeContentId() {
    return recipeContentId;
  }

  public void setRecipeContentId(String recipeContentId) {
    this.recipeContentId = recipeContentId;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("recipeContentId", recipeContentId);
    json.put("imageUrl", imageUrl);
    return json;
  }
}

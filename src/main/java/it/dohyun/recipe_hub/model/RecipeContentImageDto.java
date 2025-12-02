package it.dohyun.recipe_hub.model;

import org.json.JSONObject;

public class RecipeContentImageDto {
  private String recipeContentId;
  private String imageId;

  public String getRecipeContentId() {
    return recipeContentId;
  }

  public void setRecipeContentId(String recipeContentId) {
    this.recipeContentId = recipeContentId;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("recipeContentId", recipeContentId);
    json.put("imageId", imageId);
    return json;
  }
}

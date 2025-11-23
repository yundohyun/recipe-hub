package it.dohyun.recipe_hub.model;

public class RecipeCaloriesDto {
  private String recipeId;
  private String caloriesId;

  public String getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(String recipeId) {
    this.recipeId = recipeId;
  }

  public String getCaloriesId() {
    return caloriesId;
  }

  public void setCaloriesId(String caloriesId) {
    this.caloriesId = caloriesId;
  }
}

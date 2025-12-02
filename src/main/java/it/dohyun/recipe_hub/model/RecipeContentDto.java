package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class RecipeContentDto {
  private String id;
  private Integer step;
  private String recipeId;
  private String content;
  private LocalDateTime created;
  private LocalDateTime updated;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getStep() {
    return step;
  }

  public void setStep(Integer step) {
    this.step = step;
  }

  public String getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(String recipeId) {
    this.recipeId = recipeId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public LocalDateTime getUpdated() {
    return updated;
  }

  public void setUpdated(LocalDateTime updated) {
    this.updated = updated;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("id", id);
    json.put("step", step);
    json.put("recipeId", recipeId);
    json.put("content", content);
    json.put("created", created);
    json.put("updated", updated);
    return json;
  }
}

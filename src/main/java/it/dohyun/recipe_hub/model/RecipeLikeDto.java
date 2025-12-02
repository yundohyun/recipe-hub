package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class RecipeLikeDto {
  private String id;
  private String recipeId;
  private String memberId;
  private LocalDateTime created;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(String recipeId) {
    this.recipeId = recipeId;
  }

  public String getMemberId() {
    return memberId;
  }

  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("id", id);
    json.put("recipeId", recipeId);
    json.put("memberId", memberId);
    json.put("created", created);
    return json;
  }
}

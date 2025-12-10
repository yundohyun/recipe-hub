package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class RecipeDto {
  private String id;
  private String memberId;
  private String title;
  private Integer serve;
  private Integer duration;
  private Integer viewCount;
  private String thumbnail;
  private String description;
  private String difficulty;
  private LocalDateTime created;
  private LocalDateTime updated;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMemberId() {
    return memberId;
  }

  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getServe() {
    return serve;
  }

  public void setServe(Integer serve) {
    this.serve = serve;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getViewCount() {
    return viewCount;
  }

  public void setViewCount(Integer viewCount) {
    this.viewCount = viewCount;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
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

  public Integer getLikeCount() {
    // No likeCount in DTO; fall back to viewCount if available, else 0
    return (viewCount != null) ? viewCount : 0;
  }

  public Double getRating() {
    return null; // rating not available
  }

  public Integer getRatingCount() {
    return null; // rating count not available
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("id", id);
    json.put("memberId", memberId);
    json.put("title", title);
    json.put("thumbnail", thumbnail);
    json.put("description", description);
    json.put("difficulty", difficulty);
    json.put("serve", serve);
    json.put("duration", duration);
    json.put("viewCount", viewCount);
    json.put("created", created);
    json.put("updated", updated);
    return json;
  }
}

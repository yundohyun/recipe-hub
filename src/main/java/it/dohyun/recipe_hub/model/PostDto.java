package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class PostDto {
  private String id;
  private String memberId;
  private String title;
  private String content;
  private Integer viewCount;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getViewCount() {
    return viewCount;
  }

  public void setViewCount(Integer viewCount) {
    this.viewCount = viewCount;
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
		json.put("memberId", memberId);
		json.put("title", title);
		json.put("content", content);
		json.put("viewCount", viewCount);
		json.put("created", created);
		json.put("updated", updated);
		return json;
	}
}

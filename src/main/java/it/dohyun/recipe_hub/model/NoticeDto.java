package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class NoticeDto {
  private String id;
  private String title;
  private String content;
  private LocalDateTime created;
  private LocalDateTime updated;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
		json.put("title", title);
		json.put("content", content);
		json.put("created", created);
		json.put("updated", updated);
		return json;
	}
}

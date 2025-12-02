package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class ImageDto {
  private String id;
  private String image;
  private LocalDateTime created;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
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
    json.put("image", image);
    json.put("created", created);
    return json;
  }
}

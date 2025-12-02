package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class CaloriesDto {
  private String id;
  private String name;
  private Integer serve;
  private Integer calories;
  private Double protein;
  private Double fat;
  private Double carbohydrates;
  private LocalDateTime created;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getServe() {
    return serve;
  }

  public void setServe(Integer serve) {
    this.serve = serve;
  }

  public Integer getCalories() {
    return calories;
  }

  public void setCalories(Integer calories) {
    this.calories = calories;
  }

  public Double getProtein() {
    return protein;
  }

  public void setProtein(Double protein) {
    this.protein = protein;
  }

  public Double getFat() {
    return fat;
  }

  public void setFat(Double fat) {
    this.fat = fat;
  }

  public Double getCarbohydrates() {
    return carbohydrates;
  }

  public void setCarbohydrates(Double carbohydrates) {
    this.carbohydrates = carbohydrates;
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
    json.put("name", name);
    json.put("serve", serve);
    json.put("calories", calories);
    json.put("protein", protein);
    json.put("fat", fat);
    json.put("carbohydrates", carbohydrates);
    json.put("created", created);
    return json;
  }
}

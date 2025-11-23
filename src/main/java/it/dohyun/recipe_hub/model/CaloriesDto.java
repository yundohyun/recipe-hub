package it.dohyun.recipe_hub.model;

import java.time.LocalDateTime;

public class CaloriesDto {
  private String id;
  private String name;
  private Integer serve;
  private Integer calories;
  private Integer protein;
  private Integer fat;
  private Integer carbohydrates;
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

  public Integer getProtein() {
    return protein;
  }

  public void setProtein(Integer protein) {
    this.protein = protein;
  }

  public Integer getFat() {
    return fat;
  }

  public void setFat(Integer fat) {
    this.fat = fat;
  }

  public Integer getCarbohydrates() {
    return carbohydrates;
  }

  public void setCarbohydrates(Integer carbohydrates) {
    this.carbohydrates = carbohydrates;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }
}

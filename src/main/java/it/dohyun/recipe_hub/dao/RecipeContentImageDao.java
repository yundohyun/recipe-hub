package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeContentImageDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

public class RecipeContentImageDao {
  private RecipeContentImageDto createDto(ResultSet rs) throws SQLException {
    RecipeContentImageDto dto = new RecipeContentImageDto();
    dto.setRecipeContentId(rs.getString("recipe_content_id"));
    dto.setImageId(rs.getString("image_id"));
    return dto;
  }

  public void createRecipeContentImage(RecipeContentImageDto data)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "INSERT INTO recipe_content_image (recipe_content_id, image_id) VALUES (?, ?)");
    ps.setString(1, data.getRecipeContentId());
    ps.setString(2, data.getImageId());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public ArrayList<RecipeContentImageDto> getRecipeContentImages(String recipeContentId)
      throws SQLException, ClassNotFoundException {
    ArrayList<RecipeContentImageDto> postImages = new ArrayList<>();
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement("SELECT * FROM recipe_content_image WHERE recipe_content_id = ?");
    ps.setString(1, recipeContentId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) postImages.add(this.createDto(rs));
    DatabaseUtil.close(con, ps, rs);
    return postImages;
  }

  public void deleteRecipeContentImages(String recipeContentId)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement("DELETE FROM recipe_content_image WHERE recipe_content_id = ?");
    ps.setString(1, recipeContentId);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

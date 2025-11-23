package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeIngredientDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;

public class RecipeIngredientDao {
  private RecipeIngredientDto createDto(ResultSet rs) throws SQLException {
    RecipeIngredientDto dto = new RecipeIngredientDto();
    dto.setId(rs.getString("id"));
    dto.setRecipeId(rs.getString("recipe_id"));
    dto.setIngredient(rs.getString("ingredient"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    dto.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
    return dto;
  }

  public RecipeIngredientDto getRecipeIngredient(String id)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("select * from recipe_ingredient where id = ?");
    ps.setString(1, id);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
      DatabaseUtil.close(con, ps, rs);
      return null;
    }
    RecipeIngredientDto result = this.createDto(rs);
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  public void createRecipeIngredient(RecipeIngredientDto dto)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "INSERT INTO recipe_ingredient (recipe_id, ingredient, amount) VALUES (?, ?, ?)");
    ps.setString(1, dto.getRecipeId());
    ps.setString(2, dto.getIngredient());
    ps.setString(3, dto.getAmount());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void updateRecipeIngredient(RecipeIngredientDto dto)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "UPDATE recipe_ingredient SET recipe_id = ?, ingredient = ?, updated = CURRENT_TIMESTAMP WHERE id = ?");
    ps.setString(1, dto.getRecipeId());
    ps.setString(2, dto.getIngredient());
    ps.setString(3, dto.getId());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void deleteRecipeIngredient(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("DELETE FROM recipe_ingredient WHERE id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

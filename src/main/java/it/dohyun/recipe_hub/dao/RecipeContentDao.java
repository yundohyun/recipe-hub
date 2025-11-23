package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeContentDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

public class RecipeContentDao {
  private RecipeContentDto createDto(ResultSet rs) throws SQLException {
    RecipeContentDto dto = new RecipeContentDto();
    dto.setId(rs.getString("id"));
    dto.setStep(rs.getInt("step"));
    dto.setRecipeId(rs.getString("recipe_id"));
    dto.setContent(rs.getString("content"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    dto.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
    return dto;
  }

  public void createRecipeContent(RecipeContentDto dto)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st =
        con.prepareStatement(
            "INSERT INTO recipe_content (step, recipe_id, content) VALUES (?, ?, ?)");
    st.setInt(1, dto.getStep());
    st.setString(2, dto.getRecipeId());
    st.setString(3, dto.getContent());
    st.executeUpdate();
  }

  public ArrayList<RecipeContentDto> getRecipeContents(String recipeId)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st =
        con.prepareStatement("SELECT * FROM recipe_content WHERE recipe_id = ? ORDER BY step ");
    st.setString(1, recipeId);
    ResultSet rs = st.executeQuery();
    ArrayList<RecipeContentDto> list = new ArrayList<>();
    while (rs.next()) list.add(this.createDto(rs));
    return list;
  }

  public void deleteRecipeContents(String recipeId) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st = con.prepareStatement("DELETE FROM recipe_content WHERE recipe_id = ?");
    st.setString(1, recipeId);
    st.executeUpdate();
  }
}

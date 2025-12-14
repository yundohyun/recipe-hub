package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeDao {
  public RecipeDto createDto(ResultSet rs) throws SQLException {
    RecipeDto dto = new RecipeDto();
    dto.setId(rs.getString("id"));
    dto.setTitle(rs.getString("title"));
    dto.setMemberId(rs.getString("member_id"));
    dto.setServe(rs.getInt("serve"));
    dto.setDuration(rs.getInt("duration"));
    dto.setViewCount(rs.getInt("view_count"));
    dto.setThumbnail(rs.getString("thumbnail"));
    dto.setDescription(rs.getString("description"));
    dto.setDifficulty(rs.getString("difficulty"));
		dto.setCategory(rs.getString("category"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    dto.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
    return dto;
  }

  public RecipeDto getRecipe(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("select * from recipe where id = ?");
    ps.setString(1, id);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
      DatabaseUtil.close(con, ps, rs);
      return null;
    }
    RecipeDto result = this.createDto(rs);
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  public void createRecipe(RecipeDto data) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "INSERT INTO recipe (id, member_id, title, serve, duration, view_count, thumbnail, description, difficulty, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    ps.setString(1, data.getId());
    ps.setString(2, data.getMemberId());
    ps.setString(3, data.getTitle());
    ps.setInt(4, data.getServe());
    ps.setInt(5, data.getDuration());
    ps.setInt(6, data.getViewCount());
    ps.setString(7, data.getThumbnail());
    ps.setString(8, data.getDescription());
    ps.setString(9, data.getDifficulty());
    ps.setString(10, data.getCategory());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void updateRecipe(RecipeDto data) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "UPDATE recipe SET title = ?, serve = ?, duration = ?, view_count = ?, thumbnail = ?, description = ?, difficulty = ?, category = ?, updated = CURRENT_TIMESTAMP WHERE id = ?");
    ps.setString(1, data.getTitle());
    ps.setInt(2, data.getServe());
    ps.setInt(3, data.getDuration());
    ps.setInt(4, data.getViewCount());
    ps.setString(5, data.getThumbnail());
    ps.setString(6, data.getDescription());
    ps.setString(7, data.getDifficulty());
    ps.setString(8, data.getCategory());
    ps.setString(9, data.getId());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void addViewCount(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement("UPDATE recipe SET view_count = COALESCE(view_count, 0) + 1 WHERE id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void deleteRecipe(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("DELETE FROM recipe WHERE id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public List<RecipeDto> searchRecipes(String keyword, String category, Integer page, Integer limit)
      throws SQLException, ClassNotFoundException {
    List<RecipeDto> list = new ArrayList<>();
    Connection con = DatabaseUtil.getConnection();
    StringBuilder sb = new StringBuilder("SELECT * FROM recipe WHERE title LIKE ?");
    boolean hasCategory = (category != null && !category.isBlank());
    if (hasCategory) sb.append(" AND category = ?");
    sb.append(" ORDER BY created DESC");
    if (page != null && limit != null) sb.append(" LIMIT ? OFFSET ?");

    PreparedStatement ps = con.prepareStatement(sb.toString());
    int idx = 1;
    ps.setString(idx++, "%" + (keyword == null ? "" : keyword) + "%");
    if (hasCategory) ps.setString(idx++, category);
    if (page != null && limit != null) {
      ps.setInt(idx++, limit);
      ps.setInt(idx, (page - 1) * limit);
    }
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      list.add(createDto(rs));
    }
    DatabaseUtil.close(con, ps, rs);
    return list;
  }

  public int countRecipes(String keyword, String category) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM recipe WHERE title LIKE ?");
    boolean hasCategory = (category != null && !category.isBlank());
    if (hasCategory) sb.append(" AND category = ?");
    PreparedStatement ps = con.prepareStatement(sb.toString());
    int idx = 1;
    ps.setString(idx++, "%" + (keyword == null ? "" : keyword) + "%");
    if (hasCategory) ps.setString(idx++, category);
    ResultSet rs = ps.executeQuery();
    int count = 0;
    if (rs.next()) count = rs.getInt(1);
    DatabaseUtil.close(con, ps, rs);
    return count;
  }
}

package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;

public class RecipeDao {
  public RecipeDto createDto(ResultSet rs) throws SQLException {
    RecipeDto dto = new RecipeDto();
    dto.setId(rs.getString("id"));
    dto.setTitle(rs.getString("title"));
    dto.setMemberId(rs.getString("member_id"));
    dto.setServe(rs.getInt("serve"));
    dto.setDuration(rs.getInt("duration"));
    dto.setViewCount(rs.getInt("view_count"));
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
            "INSERT INTO recipe (id, member_id, title, serve, duration, view_count) VALUES (?, ?, ?, ?, ?, ?)");
    ps.setString(1, data.getId());
    ps.setString(2, data.getMemberId());
    ps.setString(3, data.getTitle());
    ps.setInt(4, data.getServe());
    ps.setInt(5, data.getDuration());
    ps.setInt(6, data.getViewCount());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void updateRecipe(RecipeDto data) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "UPDATE recipe SET title = ?, serve = ?, duration = ?, view_count = ?, updated = CURRENT_TIMESTAMP WHERE id = ?");
    ps.setString(1, data.getTitle());
    ps.setInt(2, data.getServe());
    ps.setInt(3, data.getDuration());
    ps.setInt(4, data.getViewCount());
    ps.setString(5, data.getId());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void addViewCount(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement("UPDATE recipe SET view_count = view_count + 1 WHERE id = ?");
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
}

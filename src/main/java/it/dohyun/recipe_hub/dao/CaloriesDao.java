package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.common.types.FindOption;
import it.dohyun.recipe_hub.common.types.SortEnum;
import it.dohyun.recipe_hub.model.CaloriesDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

public class CaloriesDao {
  private CaloriesDto createDto(ResultSet rs) throws SQLException {
    CaloriesDto dto = new CaloriesDto();
    dto.setId(rs.getString("id"));
    dto.setName(rs.getString("name"));
    dto.setServe(rs.getInt("serve"));
    dto.setCalories(rs.getInt("calories"));
    dto.setProtein(rs.getDouble("protein"));
    dto.setFat(rs.getDouble("fat"));
    dto.setCarbohydrates(rs.getDouble("carbohydrates"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    return dto;
  }

  public String buildFindQuery(StringBuilder sql, FindOption option) {
    if (!option.toString().contains("WHERE")) sql.append(" AND 1=1");
    if (option.getFrom() != null) sql.append(" AND created >= ?");
    if (option.getTo() != null) sql.append(" AND created <= ?");

    if (option.getSort() != null) {
      sql.append(" ORDER BY created ");
      sql.append(option.getSort() == SortEnum.ASC ? "ASC" : "DESC");
    }

    if (option.getPage() != null && option.getLimit() != null) sql.append(" LIMIT ? OFFSET ?");

    return sql.toString();
  }

  public void setFindQueryParams(PreparedStatement ps, FindOption option, Integer idx)
      throws SQLException {
    if (option.getFrom() != null) ps.setObject(idx++, option.getFrom());

    if (option.getTo() != null) ps.setObject(idx++, option.getTo());

    if (option.getPage() != null && option.getLimit() != null) {
      ps.setInt(idx++, option.getLimit());
      ps.setInt(idx, (option.getPage() - 1) * option.getLimit());
    }
  }

  public void createCalories(CaloriesDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st =
        con.prepareStatement(
            "INSERT INTO calories (name, serve, calories, protein, fat, carbohydrates) VALUES (?, ?, ?, ?, ?, ?)");
    st.setString(1, dto.getName());
    st.setInt(2, dto.getServe());
    st.setInt(3, dto.getCalories());
    st.setDouble(4, dto.getProtein());
    st.setDouble(5, dto.getFat());
    st.setDouble(6, dto.getCarbohydrates());
    st.executeUpdate();
    DatabaseUtil.close(con, st);
  }

  public boolean existsByNameAndCalories(String name, int calories)
      throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    String sql = "SELECT 1 FROM calories WHERE name=? AND calories=?";
    PreparedStatement st = con.prepareStatement(sql);
    st.setString(1, name);
    st.setInt(2, calories);

    ResultSet rs = st.executeQuery();
    boolean exist = rs.next();

    DatabaseUtil.close(con, st, rs);
    return exist;
  }

  public ArrayList<CaloriesDto> searchCalories(String name, FindOption option)
      throws SQLException, ClassNotFoundException {
    ArrayList<CaloriesDto> list = new ArrayList<>();
    Connection con = DatabaseUtil.getConnection();
    StringBuilder sql = new StringBuilder("SELECT * FROM calories WHERE name LIKE ?");
    String sqlQuery = buildFindQuery(sql, option);
    PreparedStatement st = con.prepareStatement(sqlQuery);

    int idx = 1;
    st.setString(idx++, "%" + name + "%");
    setFindQueryParams(st, option, idx);
    ResultSet rs = st.executeQuery();

    while (rs.next()) list.add(createDto(rs));
    DatabaseUtil.close(con, st, rs);
    return list;
  }

  public CaloriesDto getCalories(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st = con.prepareStatement("SELECT * FROM calories WHERE id = ?");
    st.setString(1, id);
    ResultSet rs = st.executeQuery();
    CaloriesDto dto = null;
    if (rs.next()) dto = createDto(rs);
    DatabaseUtil.close(con, st, rs);
    return dto;
  }

  public void deleteCalories(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st = con.prepareStatement("DELETE FROM calories WHERE id = ?");
    st.setString(1, id);
    st.executeUpdate();
    DatabaseUtil.close(con, st);
  }

  public int countCalories() throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM calories");
    ResultSet rs = ps.executeQuery();
    int c = 0;
    if (rs.next()) c = rs.getInt(1);
    DatabaseUtil.close(con, ps, rs);
    return c;
  }
}

package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.NoticeDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

public class NoticeDao {
  private NoticeDto createDto(ResultSet rs) throws SQLException {
    NoticeDto dto = new NoticeDto();
    dto.setId(rs.getString("id"));
    dto.setTitle(rs.getString("title"));
    dto.setContent(rs.getString("content"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    dto.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
    return dto;
  }

  public void createNotice(NoticeDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st =
        con.prepareStatement("INSERT INTO notice (id, title, content) VALUES (?, ?, ?)");
    st.setString(1, dto.getId());
    st.setString(2, dto.getTitle());
    st.setString(3, dto.getContent());
    st.executeUpdate();
    DatabaseUtil.close(con, st);
  }

  public NoticeDto getNotice(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st = con.prepareStatement("SELECT * FROM notice WHERE id = ?");
    st.setString(1, id);
    ResultSet rs = st.executeQuery();
    NoticeDto dto = null;
    if (rs.next()) {
      dto = createDto(rs);
    }
    DatabaseUtil.close(con, st, rs);
    return dto;
  }

  public ArrayList<NoticeDto> getNotices() throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st = con.prepareStatement("SELECT * FROM notice ORDER BY created DESC");
    ResultSet rs = st.executeQuery();
    ArrayList<NoticeDto> list = new ArrayList<>();
    while (rs.next()) list.add(createDto(rs));
    DatabaseUtil.close(con, st, rs);
    return list;
  }

  public void updateNotice(NoticeDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st =
        con.prepareStatement(
            "UPDATE notice SET title = ?, content = ?, updated = CURRENT_TIMESTAMP WHERE id = ?");
    st.setString(1, dto.getTitle());
    st.setString(2, dto.getContent());
    st.setString(3, dto.getId());
    st.executeUpdate();
    DatabaseUtil.close(con, st);
  }

  public void deleteNotice(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement st = con.prepareStatement("DELETE FROM notice WHERE id = ?");
    st.setString(1, id);
    st.executeUpdate();
    DatabaseUtil.close(con, st);
  }
}

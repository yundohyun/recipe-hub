package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.common.types.*;
import it.dohyun.recipe_hub.model.PostDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

public class PostDao {
  private PostDto createDto(ResultSet rs) throws SQLException {
    PostDto dto = new PostDto();
    dto.setId(rs.getString("id"));
    dto.setMemberId(rs.getString("member_id"));
    dto.setTitle(rs.getString("title"));
    dto.setContent(rs.getString("content"));
    dto.setViewCount(rs.getInt("view_count"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    dto.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
    return dto;
  }

  public String buildFindQuery(StringBuilder sql, FindOption option) {
    if (!option.toString().contains("WHERE")) sql.append(" WHERE 1=1");
    if (option.getFrom() != null) sql.append(" AND created_at >= ?");
    if (option.getTo() != null) sql.append(" AND created_at <= ?");

    if (option.getSort() != null) {
      sql.append(" ORDER BY created_at ");
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

  public PostDto getPost(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("select * from post where id = ?");
    ps.setString(1, id);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
      DatabaseUtil.close(con, ps, rs);
      return null;
    }
    PostDto result = this.createDto(rs);
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  public ArrayList<PostDto> getPostsByMemberId(String memberId, FindOption option)
      throws SQLException, ClassNotFoundException {
    ArrayList<PostDto> result = new ArrayList<>();
    Connection con = DatabaseUtil.getConnection();

    StringBuilder sql = new StringBuilder("SELECT * FROM post WHERE member_id = ?");
    String sqlQuery = this.buildFindQuery(sql, option);
    PreparedStatement ps = con.prepareStatement(sqlQuery);

    int idx = 1;
    ps.setString(idx++, memberId);
    this.setFindQueryParams(ps, option, idx);

    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      PostDto dto = this.createDto(rs);
      result.add(dto);
    }
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  private ArrayList<PostDto> searchPosts(String title, String content, FindOption option)
      throws SQLException, ClassNotFoundException {
    ArrayList<PostDto> result = new ArrayList<>();
    Connection con = DatabaseUtil.getConnection();

    StringBuilder sql = new StringBuilder("SELECT * FROM post");
    if (!title.isBlank()) sql.append(" WHERE title LIKE ?");
    if (!content.isBlank()) {
      if (sql.toString().contains("WHERE")) sql.append(" OR content LIKE ?");
      else sql.append(" WHERE content LIKE ?");
    }
    String sqlQuery = this.buildFindQuery(sql, option);
    PreparedStatement ps = con.prepareStatement(sqlQuery);

    int idx = 1;
    if (!title.isBlank()) ps.setString(idx++, "%" + title + "%");
    if (!content.isBlank()) ps.setString(idx++, "%" + content + "%");
    this.setFindQueryParams(ps, option, idx);

    ResultSet rs = ps.executeQuery();
    while (rs.next()) result.add(this.createDto(rs));
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  public void setPost(PostDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "insert into post(member_id, title, content, view_count) values (?, ?, ?, ?)");
    ps.setString(1, dto.getMemberId());
    ps.setString(2, dto.getTitle());
    ps.setString(3, dto.getContent());
    ps.setInt(4, dto.getViewCount());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  void updatePost(PostDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement(
            "update post set title = ?, content = ?, view_count = ?, updated = CURRENT_TIMESTAMP where id = ?");
    ps.setString(1, dto.getTitle());
    ps.setString(2, dto.getContent());
    ps.setInt(3, dto.getViewCount());
    ps.setString(4, dto.getId());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  void deletePost(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("delete from post where id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

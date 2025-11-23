package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.PostLikeDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;

public class PostLikeDao {
  private PostLikeDto createDto(ResultSet rs) throws SQLException {
    PostLikeDto dto = new PostLikeDto();
    dto.setId(rs.getString("id"));
    dto.setPostId(rs.getString("post_id"));
    dto.setMemberId(rs.getString("member_id"));
    dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
    return dto;
  }

  public PostLikeDto getPostLike(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("select * from post_like where id = ?");
    ps.setString(1, id);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
      DatabaseUtil.close(con, ps, rs);
      return null;
    }
    PostLikeDto result = this.createDto(rs);
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  public void setPostLike(PostLikeDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement("insert into post_like(post_id, member_id) values (?, ?)");
    ps.setString(1, dto.getPostId());
    ps.setString(2, dto.getMemberId());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public void deletePostLike(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("delete from post_like where id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

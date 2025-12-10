package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.PostImageDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

public class PostImageDao {
  public PostImageDto createDto(ResultSet rs) throws SQLException {
    PostImageDto dto = new PostImageDto();
    dto.setPostId(rs.getString("post_id"));
    dto.setImageUrl(rs.getString("image_url"));
    return dto;
  }

  public void createPostImage(PostImageDto data) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps =
        con.prepareStatement("INSERT INTO post_image (post_id, image_url) VALUES (?, ?)");
    ps.setString(1, data.getPostId());
    ps.setString(2, data.getImageUrl());
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }

  public ArrayList<PostImageDto> getPostImages(String postId)
      throws SQLException, ClassNotFoundException {
    ArrayList<PostImageDto> postImages = new ArrayList<>();
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("SELECT * FROM post_image WHERE post_id = ?");
    ps.setString(1, postId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) postImages.add(this.createDto(rs));
    DatabaseUtil.close(con, ps, rs);
    return postImages;
  }

  public void deletePostImages(String postId) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("DELETE FROM post_image WHERE post_id = ?");
    ps.setString(1, postId);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

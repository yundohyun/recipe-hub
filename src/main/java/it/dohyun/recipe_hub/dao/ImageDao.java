package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.ImageDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;

public class ImageDao {
  ImageDto getImage(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("select * from image where id = ?");
    ps.setString(1, id);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
      DatabaseUtil.close(con, ps, rs);
      return null;
    }
    ImageDto result = new ImageDto();
    result.setId(rs.getString("id"));
    result.setImage(rs.getString("image"));
    result.setCreated(rs.getTimestamp("created").toLocalDateTime());
    DatabaseUtil.close(con, ps, rs);
    return result;
  }

  ImageDto setImage(ImageDto dto) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("insert into image(image) values (?)");
    ps.setString(1, dto.getImage());
    ps.executeUpdate();
    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) dto.setId(rs.getString(1));
    DatabaseUtil.close(con, ps, rs);
    return dto;
  }

  void deleteImage(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("delete from image where id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.ImageDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;
import java.sql.*;

public class ImageDao {
  public ImageDto getImage(String id) throws SQLException, ClassNotFoundException {
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

  public ImageDto setImage(ImageDto dto) throws SQLException, ClassNotFoundException {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      con = DatabaseUtil.getConnection();
      ps = con.prepareStatement("insert into image(image) values (?)", Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, dto.getImage());
      ps.executeUpdate();
      rs = ps.getGeneratedKeys();
      if (rs != null && rs.next()) {
        dto.setId(rs.getString(1));
      }

      // Fallback: if generated key wasn't returned, attempt to look up by image URL using the same connection
      if (dto.getId() == null || dto.getId().isBlank()) {
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        try {
          ps2 = con.prepareStatement("SELECT id FROM image WHERE image = ? ORDER BY created DESC LIMIT 1");
          ps2.setString(1, dto.getImage());
          rs2 = ps2.executeQuery();
          if (rs2.next()) {
            dto.setId(rs2.getString(1));
          }
        } finally {
          if (rs2 != null) try { rs2.close(); } catch (SQLException ignore) {}
          if (ps2 != null) try { ps2.close(); } catch (SQLException ignore) {}
        }
      }

      return dto;
    } finally {
      if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
      if (ps != null) try { ps.close(); } catch (SQLException ignore) {}
      if (con != null) try { con.close(); } catch (SQLException ignore) {}
    }
  }

  public void deleteImage(String id) throws SQLException, ClassNotFoundException {
    Connection con = DatabaseUtil.getConnection();
    PreparedStatement ps = con.prepareStatement("delete from image where id = ?");
    ps.setString(1, id);
    ps.executeUpdate();
    DatabaseUtil.close(con, ps);
  }
}

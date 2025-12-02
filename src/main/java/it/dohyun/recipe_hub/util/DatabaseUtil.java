package it.dohyun.recipe_hub.util;

import java.sql.*;
import java.util.List;

public class DatabaseUtil {
  private static final PropertyUtil property =
      new PropertyUtil(
          "database.properties",
          List.of("database.url", "database.schema", "database.username", "database.password"));

  public static Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    String url =
        "jdbc:mysql://"
            + property.getProperty("database.url")
            + "/"
            + property.getProperty("database.schema");
    return DriverManager.getConnection(
        url, property.getProperty("database.username"), property.getProperty("database.password"));
  }

  public static void close(Connection con, PreparedStatement st) throws SQLException {
    st.close();
    con.close();
  }

  public static void close(Connection con, PreparedStatement st, ResultSet rs) throws SQLException {
    rs.close();
    st.close();
    con.close();
  }
}

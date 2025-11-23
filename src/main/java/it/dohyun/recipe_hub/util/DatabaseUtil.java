package it.dohyun.recipe_hub.util;

import java.sql.*;

public class DatabaseUtil {
  public static Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/recipe_hub", "root", "dongyang");
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

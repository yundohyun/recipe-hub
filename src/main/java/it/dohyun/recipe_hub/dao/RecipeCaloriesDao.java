package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeCaloriesDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecipeCaloriesDao {
	private RecipeCaloriesDto createDto(ResultSet rs) throws SQLException {
		RecipeCaloriesDto dto = new RecipeCaloriesDto();
		dto.setRecipeId(rs.getString("recipe_id"));
		dto.setCaloriesId(rs.getString("calories_id"));
		return dto;
	}
	
	public void createRecipeCalories(RecipeCaloriesDto dto)
		throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement st =
			con.prepareStatement("INSERT INTO recipe_calories (recipe_id, calories_id) VALUES (?, ?)");
		st.setString(1, dto.getRecipeId());
		st.setString(2, dto.getCaloriesId());
		st.executeUpdate();
		DatabaseUtil.close(con, st);
	}
	
	public RecipeCaloriesDto getRecipeCalories(String recipeId)
		throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement st =
			con.prepareStatement("SELECT * FROM recipe_calories WHERE recipe_id = ?");
		st.setString(1, recipeId);
		ResultSet rs = st.executeQuery();
		RecipeCaloriesDto dto = null;
		if (rs.next()) dto = createDto(rs);
		DatabaseUtil.close(con, st, rs);
		return dto;
	}
	
	public ArrayList<RecipeCaloriesDto> getRecipeCaloriesList(String recipeId)
		throws SQLException, ClassNotFoundException {
		ArrayList<RecipeCaloriesDto> list = new ArrayList<>();
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement st = con.prepareStatement("SELECT * FROM recipe_calories WHERE recipe_id = ?");
		st.setString(1, recipeId);
		ResultSet rs = st.executeQuery();
		while (rs.next()) list.add(createDto(rs));
		DatabaseUtil.close(con, st, rs);
		return list;
	}
	
	public void deleteRecipeCalories(String recipeId) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement st = con.prepareStatement("DELETE FROM recipe_calories WHERE recipe_id = ?");
		st.setString(1, recipeId);
		st.executeUpdate();
		DatabaseUtil.close(con, st);
	}
}

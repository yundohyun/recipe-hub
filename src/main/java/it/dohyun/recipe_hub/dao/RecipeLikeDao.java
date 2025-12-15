package it.dohyun.recipe_hub.dao;

import it.dohyun.recipe_hub.model.RecipeLikeDto;
import it.dohyun.recipe_hub.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecipeLikeDao {
	private RecipeLikeDto createDto(ResultSet rs) throws SQLException {
		RecipeLikeDto dto = new RecipeLikeDto();
		dto.setId(rs.getString("id"));
		dto.setRecipeId(rs.getString("recipe_id"));
		dto.setMemberId(rs.getString("member_id"));
		dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
		return dto;
	}
	
	public RecipeLikeDto getRecipeLike(String id) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from recipe_like where id = ?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			DatabaseUtil.close(con, ps, rs);
			return null;
		}
		RecipeLikeDto result = this.createDto(rs);
		DatabaseUtil.close(con, ps, rs);
		return result;
	}
	
	public RecipeLikeDto getRecipeLikeByRecipeAndMember(String recipeId, String memberId) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from recipe_like where recipe_id = ? and member_id = ? limit 1");
		ps.setString(1, recipeId);
		ps.setString(2, memberId);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			DatabaseUtil.close(con, ps, rs);
			return null;
		}
		RecipeLikeDto result = this.createDto(rs);
		DatabaseUtil.close(con, ps, rs);
		return result;
	}
	
	public void deleteRecipeLikeByRecipeAndMember(String recipeId, String memberId) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("delete from recipe_like where recipe_id = ? and member_id = ?");
		ps.setString(1, recipeId);
		ps.setString(2, memberId);
		ps.executeUpdate();
		DatabaseUtil.close(con, ps);
	}
	
	public void setRecipeLike(RecipeLikeDto dto) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps =
			con.prepareStatement("insert into recipe_like(recipe_id, member_id) values (?, ?)");
		ps.setString(1, dto.getRecipeId());
		ps.setString(2, dto.getMemberId());
		ps.executeUpdate();
		DatabaseUtil.close(con, ps);
	}
	
	public void deleteRecipeLike(String id) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("delete from recipe_like where id = ?");
		ps.setString(1, id);
		ps.executeUpdate();
		DatabaseUtil.close(con, ps);
	}
	
	public int countLikesByRecipeId(String recipeId) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps =
			con.prepareStatement("select count(*) as cnt from recipe_like where recipe_id = ?");
		ps.setString(1, recipeId);
		ResultSet rs = ps.executeQuery();
		int cnt = 0;
		if (rs.next()) {
			cnt = rs.getInt("cnt");
		}
		DatabaseUtil.close(con, ps, rs);
		return cnt;
	}
	
	public java.util.List<String> getRecipeIdsByMemberId(String memberId) throws SQLException, ClassNotFoundException {
		java.util.List<String> list = new java.util.ArrayList<>();
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select recipe_id from recipe_like where member_id = ? order by created desc");
		ps.setString(1, memberId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			list.add(rs.getString("recipe_id"));
		}
		DatabaseUtil.close(con, ps, rs);
		return list;
	}
}

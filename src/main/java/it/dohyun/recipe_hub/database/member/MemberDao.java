package it.dohyun.recipe_hub.database.member;
import it.dohyun.recipe_hub.database.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

public class MemberDao {
	public MemberDto getMember(String id) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from member where id = ?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			DatabaseUtil.close(con, ps, rs);
			return null;
		}
		MemberDto result = new MemberDto();
		result.setId(rs.getString("id"));
		result.setNickname(rs.getString("nickname"));
		result.setAvatar(rs.getString("avatar"));
		result.setIntroduce(rs.getString("introduce"));
		result.setEmail(rs.getString("email"));
		result.setAdmin(rs.getBoolean("admin"));
		result.setPassword(rs.getString("password"));
		result.setCreated(rs.getTimestamp("created").toLocalDateTime());
		DatabaseUtil.close(con, ps, rs);
		return result;
	}
	
	public ArrayList<MemberDto> getMembers() throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from member");
		ResultSet rs = ps.executeQuery();
		ArrayList<MemberDto> result = new ArrayList<>();
		while (rs.next()) {
			MemberDto dto = new MemberDto();
			dto.setId(rs.getString("id"));
			dto.setNickname(rs.getString("nickname"));
			dto.setAvatar(rs.getString("avatar"));
			dto.setIntroduce(rs.getString("introduce"));
			dto.setEmail(rs.getString("email"));
			dto.setAdmin(rs.getBoolean("admin"));
			dto.setPassword(rs.getString("password"));
			dto.setCreated(rs.getTimestamp("created").toLocalDateTime());
			result.add(dto);
		}
		DatabaseUtil.close(con, ps, rs);
		return result;
	}
	
	public void setMember(MemberDto dto) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("insert into member(nickname, avatar, introduce, email, admin, password) values (?, ?, ?, ?, ?, ?)");
		ps.setString(1, dto.getNickname());
		ps.setString(2, dto.getAvatar());
		ps.setString(3, dto.getIntroduce());
		ps.setString(4, dto.getEmail());
		ps.setBoolean(5, dto.isAdmin());
		ps.setString(6, BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
		ps.executeUpdate();
		DatabaseUtil.close(con, ps);
	}
	
	public void updateMember(MemberDto dto) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("update member set nickname = ?, avatar = ?, introduce = ?, email = ?, password = ?");
		ps.setString(1, dto.getNickname());
		ps.setString(2, dto.getAvatar());
		ps.setString(3, dto.getIntroduce());
		ps.setString(4, dto.getEmail());
		ps.setString(5, BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
		ps.executeUpdate();
		DatabaseUtil.close(con, ps);
	}
	
	public void deleteMember(String id) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("delete from member where id = ?");
		ps.setString(1, id);
		ps.executeUpdate();
		DatabaseUtil.close(con, ps);
	}
	
	public String checkLogin(String email, String password)  throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from member where email = ?");
		ps.setString(1, email);
		ResultSet rs = ps.executeQuery();
		
		if (!rs.next() || !BCrypt.checkpw(password, rs.getString("password"))) {
			DatabaseUtil.close(con, ps);
			return null;
		}
		
		String result = rs.getString("id");
		DatabaseUtil.close(con, ps);
		return result;
	}
	
	public boolean checkEmailExist(String email) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from member where email = ?");
		ps.setString(1, email);
		ResultSet rs = ps.executeQuery();
		boolean result = rs.next();
		DatabaseUtil.close(con, ps);
		return result;
	}

	public boolean checkNicknameExist(String nickname) throws SQLException, ClassNotFoundException {
		Connection con = DatabaseUtil.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from member where nickname = ?");
		ps.setString(1, nickname);
		ResultSet rs = ps.executeQuery();
		boolean result = rs.next();
		DatabaseUtil.close(con, ps);
		return result;
	}

}

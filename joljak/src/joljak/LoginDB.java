package joljak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDB {
	private static LoginDB instance = new LoginDB();
	
	public static LoginDB getInstance() {
		return instance;
	}

	public LoginDB() {
		
	}
	
	String jdbcUrl = "jdbc:mysql://15.164.226.117:8080/Patient"; // MySQL 계정
	String dbId = "root"; // MySQL 계정
	String dbPw = ""; // 비밀번호
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql = "";
	String returns = "a";

	// 데이터베이스와 통신하기 위한 코드가 들어있는 메서드
	public String login(String id, String pwd) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 디비 연결 jdbcUrl, dbId,  dbPw가 들어가면됨
			conn = DriverManager.getConnection("jdbc:mysql://15.164.226.117:8080/Patient", "root", "");
			
			//로그인
			sql = "select p_id, p_pwd from Patient where p_id = ? and p_pwd = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				if(rs.getString("p_id").equals(id) && rs.getString("p_pwd").equals(pwd)) {
					returns="로그인 가능";
				} else {
					returns="로그인 실패";
				}
			} else {
				returns="아이디 혹은 비밀번호가 존재하지 않습니다.";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)try {pstmt.close();} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn != null)try {conn.close();	} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returns;
	}
}

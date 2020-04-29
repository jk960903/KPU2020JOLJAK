package joljak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinDB {
	private static JoinDB instance = new JoinDB();
	
	public static JoinDB getInstance() {
		return instance;
	}
	
	public JoinDB() {
		
	}
	
	String jdbcUrl = "jdbc:mysql://15.164.226.117:8080/Patient"; // MySQL 계정
	String dbId = "root"; // MySQL 계정
	String dbPw = ""; // 비밀번호
	Connection conn = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	ResultSet rs = null;
	String sql = "";
	String sql2 = "";
	String returns = "a";
	
	public String join(String id, String pwd, String name, String birth, String phone) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 디비 연결 jdbcUrl, dbId,  dbPw가 들어가면됨
			conn = DriverManager.getConnection("jdbc:mysql://15.164.226.117:8080/Patient", "root", "");
			
			sql = "select p_id from Patient where p_id=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,id);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				if (rs.getString("p_id").equals(id)) { // 이미 아이디가 있는 경우
					returns = "존재하는 아이디입니다.";
				} else {
					sql2="insert into Patient values(?,?,?,?,?)";
					pstmt2=conn.prepareStatement(sql2);
					pstmt2.setString(1, id);
					pstmt2.setString(2, pwd);
					pstmt2.setString(3, name);
					pstmt2.setString(4, birth);
					pstmt2.setString(5, phone);
					
					pstmt2.executeUpdate();
					
					returns="회원가입에 성공하였습니다.";
				}

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
			if (pstmt2 != null)try {pstmt2.close();} catch (SQLException e) {
				e.printStackTrace();
			}
			if (rs != null)try {rs.close();	} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returns;
	}
}

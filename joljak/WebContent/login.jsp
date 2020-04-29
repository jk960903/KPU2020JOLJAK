<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="joljak.LoginDB" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
</head>
<body>

<%
	request.setCharacterEncoding("UTF-8");
	String p_id = request.getParameter("p_id");
	String p_pwd = request.getParameter("p_pwd");
	
	//싱글톤 방식으로 자바 클래스를 불러옵니다.
	LoginDB connectDB = LoginDB.getInstance();
	String returns = connectDB.login("p_id", "p_pwd");
	System.out.println("로그인한 아이디" + p_id);
%>

</body>
</html>
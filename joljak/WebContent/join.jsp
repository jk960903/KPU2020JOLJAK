<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="joljak.JoinDB"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	request.setCharacterEncoding("UTF-8");
	String p_id = request.getParameter("p_id");
	String p_pwd = request.getParameter("p_pwd");
	String p_name = request.getParameter("p_name");
	String p_birth = request.getParameter("p_birth");
	String p_phone = request.getParameter("p_phone");
	
	//싱글톤 방식으로 자바 클래스를 불러옵니다.
	JoinDB connectDB = JoinDB.getInstance();
	String returns = connectDB.join("p_id", "p_pwd","p_name","p_birth","p_phone");
	System.out.println("회원가입한 아이디" + p_id);
%>
</body>
</html>
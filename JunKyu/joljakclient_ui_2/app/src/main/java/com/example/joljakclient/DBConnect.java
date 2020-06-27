package com.example.joljakclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//이 페이지는 여기서 쓰이는것이 아니라 인스턴스에 이페이지를 올리고 JSP 연결해야함

public class DBConnect {
    private static  DBConnect DB=new DBConnect();

    public static DBConnect getInstance(){
        return DB;
    }
    public DBConnect(){

    }
    private String DBURL="jdbc:mysql:";//MySQL 계정 링크
    private String DBID="ID";//DB 아이디 입력
    private String DBPW="password";//DB PassWord
    private Connection conn=null;
    private PreparedStatement preparedStatement=null;
    private PreparedStatement preparedStatement2=null;
    private ResultSet resultSet=null;
    private String sql="";
    private String sql2="";
    String returnsAccount="";
    String returnsLogin="";

    //데이터베이스 통신 메서드
    //회원가입 메서드
    public String AccountDB(String id,String pw){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn= DriverManager.getConnection(DBURL,DBID,DBPW);
            sql="select id from oc22table where id=?";
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,id);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){//종북된값이 있을때
                if(resultSet.getString("id").equals(id)){
                    returnsAccount="id";
                }
                else {//없을때
                    sql2="insert into tablenametable values(?,?)";//
                    preparedStatement2=conn.prepareStatement(sql2);
                    preparedStatement2.setString(1,id);
                    preparedStatement2.setString(2,pw);

                    returnsAccount="OK";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(preparedStatement!=null)
                try{
                preparedStatement.close();}
                catch (SQLException ex){

                }
            if(preparedStatement2 !=null){
                try{
                    preparedStatement2.close();
                } catch (SQLException ex){

                }
            }
            if(resultSet !=null){
                try{
                    resultSet.close();
                } catch (SQLException eX){

                }
            }
            return returnsAccount;
        }
    }
    public String loginDB(String id,String pw){//로그인 디비
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection(DBURL,DBID,DBPW);
            sql="select id,pw from tablenametable where id=? and pw=?";
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setString(2,pw);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getString("id").equals(id)&&resultSet.getString("pw").equals(pw)){//db에 id pw존재 하고 맞음
                    returnsLogin="true";
                }
                else{//id 나 pw 둘중 하나가 다름
                    returnsLogin="false";
                }
            } else{
                returnsLogin="noId";//아이디 또는 비밀번호 존재 x

            }

        } catch (Exception e){

        } finally {
            if(resultSet!=null) try{
                resultSet.close();
            } catch (SQLException e){

            }
            if(preparedStatement !=null) try{
                preparedStatement.close();
            } catch (SQLException e){

            }if(conn!=null) try{
                conn.close();
            } catch (SQLException e){

            }
        }
        return returnsLogin;
    }
}

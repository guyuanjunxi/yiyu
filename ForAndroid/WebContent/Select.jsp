<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>呓语数据库中的数据</title>
<link href="Style.css" type="text/css" rel="stylesheet" />
</head>
<body>
    <%
    Class.forName("com.mysql.jdbc.Driver");
    String url = "jdbc:mysql://cdb-fmhwq8ww.cd.tencentcdb.com:10074/yiyu?useUnicode"  //地址
            + "=true&characterEncoding=utf-8&useSSL=false";  
    Connection connection  = (Connection) DriverManager.getConnection(url,"root","b980227l");
    Statement statement = (Statement) connection.createStatement();  //连接实例
    %>
    <h1>数据表user信息</h1>
    <table>
        <tr>
            <th>name</th>
            <th>password</th>
            <th>phone</th>
        </tr>
        <% 
        String sql1 = "SELECT * FROM user";
        ResultSet rs1 = statement.executeQuery(sql1);
        while(rs1.next()) {
            String name = rs1.getString("name");
            String password = rs1.getString("password");
            String phone = rs1.getString("phone");
        %>
        <tr>
            <td><%= name%></td>
            <td><%= password%></td>
            <td><%= phone%></td>
        </tr>
        <%
        }
        rs1.close();
        %>
    </table>
</body>
</html>
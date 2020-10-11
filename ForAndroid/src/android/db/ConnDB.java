package android.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConnDB {

	/**
	 * 打开数据库
	 */
	public static Connection openConn() {
		// 1.导驱动包mysql-connector-java-5.1.7-bin.jar
		// 2.创建Connection连接对象
		Connection conn = null;
		try {
			// 3.获取mysql驱动
			Class.forName("com.mysql.jdbc.Driver");
			// 4.获取url路径
			String url = "jdbc:mysql://cdb-fmhwq8ww.cd.tencentcdb.com:10074/yiyu";
			// 5.获取mysql连接账号
			String mysql_name = "root";
			String mysql_password = "b980227l";
			// 6.进行连接
			conn = DriverManager.getConnection(url, mysql_name, mysql_password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		return conn;
	}

	/**
	 * 关闭数据库
	 */
	public static void closeConn(ResultSet rs, PreparedStatement ps, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
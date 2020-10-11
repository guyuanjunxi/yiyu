package android.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import android.bean.Person;
import android.db.ConnDB;



public class PersonDAO {
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	public int loginPerson(Person person) {

		int num = 0;
		try {
			conn = ConnDB.openConn();
			String sql = "select * from user where name=? and password=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, person.getName());
			ps.setString(2, person.getPassword());
			rs = ps.executeQuery();
			if (rs.next()) {
				num = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnDB.closeConn(rs, ps, conn);
		}
		return num;
	}
	
	public int registerPerson(Person person) {
		int num = 0;
		try {
			conn = ConnDB.openConn();
			String sql = "insert into user(name,password,phone) values(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, person.getName());
			ps.setString(2, person.getPassword());
			ps.setString(3, person.getPhone());
			num = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnDB.closeConn(rs, ps, conn);
		}
		return num;
	}

	public int forgetPassword(Person person) {
		int num = 0;
		try {
			conn = ConnDB.openConn();
			String sql1 = "select * from user where name=? and phone=?";
			ps = conn.prepareStatement(sql1);
			ps.setString(1, person.getName());
			ps.setString(2, person.getPhone());
			rs = ps.executeQuery();
			if (rs.next()) {
				num = 1;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ConnDB.closeConn(rs, ps, conn);
		}
		return num;
	}
	
	public int changePassword(Person person) {
		int num = 0;
		try {
			conn = ConnDB.openConn();
			String sql = "update user set password=? where name=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, person.getPassword());
			ps.setString(2, person.getName());
			num = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ConnDB.closeConn(rs, ps, conn);
		}
		return num;
	}
	
	public int deletePerson(Person person) {
		int num = 0;
		try {
			conn = ConnDB.openConn();
			String sql = "delete from user where name=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, person.getName());
			num = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnDB.closeConn(rs, ps, conn);
		}
		return num;
	}
	
}

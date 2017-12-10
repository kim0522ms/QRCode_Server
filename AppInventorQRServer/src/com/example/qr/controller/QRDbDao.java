package com.example.qr.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QRDbDao {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private final String DB_URL = "jdbc:oracle:thin://@localhost:1521/xe";
	private final String DB_USER = "app";
	private final String DB_PW = "oradb";
	
	public void connect() throws ClassNotFoundException, SQLException{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		
		conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
	}
	
	public void disconnect() throws SQLException{
		if (pstmt != null){
			pstmt.close();
			pstmt = null;
		}
		
		if (conn != null){
			conn.close();
			conn = null;
		}
	}
	
	public boolean checkOverdue(int clientid) throws ClassNotFoundException, SQLException {
		boolean isOk = false;
		
		connect();
		
		String sql = "SELECT * FROM rentinfo WHERE clientid = ? AND returned = 'FALSE'";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, clientid);
		
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next())
		{
			isOk = false;
		}
		else
		{
			isOk = true;
		}
		
		return isOk;
	}
}

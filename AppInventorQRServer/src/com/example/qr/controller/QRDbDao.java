package com.example.qr.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import sun.tools.jar.resources.jar;

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
	
	// �����ϴ� �й����� üũ
	public boolean checkStudentId(int clientid) throws SQLException, ClassNotFoundException
	{
		boolean isExist = false;
		
		connect();
		
		String sql = "SELECT * FROM CLIENT WHERE clientid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, clientid);
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next())
			isExist = true;
		else
			isExist = false;
		
		disconnect();
		
		return isExist;
	}
	
	// �й��� �Է¹޾Ƽ� ���� ��ü������ Ȯ��. true�� ��� ��ü��
	public boolean checkOverdue(int clientid) throws ClassNotFoundException, SQLException {
		boolean isOverdue = false;
		
		connect();
		
		String sql = "SELECT * FROM RENTINFO WHERE clientid = ? AND returndate < SYSDATE AND returned = 'FALSE'";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, clientid);
		
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next())
			isOverdue = true;
		else
			isOverdue = false;
		
		disconnect();
		
		return isOverdue;
	}
	
	// DB�� �ݳ����� ���. ������ true ����
	public boolean enrollmentReturn(String itemid) throws SQLException, ClassNotFoundException
	{
		boolean result = false;
		
		connect();
		System.out.println("itemid = " + itemid);
		
		String sql = "UPDATE items SET rented = 'FALSE' WHERE itemid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, itemid);
		int isChanged = pstmt.executeUpdate();
		
		if (isChanged > 0)
		{
			sql = "UPDATE rentinfo SET returned = 'TRUE' WHERE returned = 'FALSE' AND itemid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, itemid);
			
			isChanged = pstmt.executeUpdate();
			
			if (isChanged > 0)
				result = true;
			else
				result = false;
		}
		else
			result = false;
		
		disconnect();
		
		return result;
	}
	
	// DB�� �뿩���� ���
	public boolean enrollmentRent(String clientid, String rentDate, String returnDate, String itemid) throws ClassNotFoundException, SQLException
	{
		boolean result = false;
		
		connect();
		
		String sql = "INSERT INTO rentinfo values (rentInfo_sequence.NEXTVAL, ?, ?, ?, 'FALSE', ?)";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, clientid);
		pstmt.setString(2, rentDate);
		pstmt.setString(3, returnDate);
		pstmt.setString(4, itemid);
		
		int rs = pstmt.executeUpdate();
		
		if (rs > 0)
			result = true;
		else
			result = false;
		
		disconnect();
		
		return result;
	}
	
	public String getItemName(String itemid) throws ClassNotFoundException, SQLException
	{
		String itemName = "";
		
		connect();
		
		String sql = "SELECT * FROM items WHERE itemid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, itemid);
		
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next())
		{
			itemName = rs.getString("itemname");
		}
		
		disconnect();
		
		return itemName;
	}
	
	// �������� �뿩���¸� �����Ѵ�(������ id, �����ϰ���� ����). ������ �Ϸ�Ǹ� true�� ������.
	public boolean changeItemStatus(String itemid, boolean rented) throws SQLException, ClassNotFoundException
	{
		boolean isChanged = false;
		String status = "";
		
		if (rented)
			status = "TRUE";
		else
			status = "FALSE";
		
		connect();
		
		String sql = "UPDATE items SET rented = ? WHERE itemid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, status);
		pstmt.setString(2, itemid);
		
		int result = pstmt.executeUpdate();
		
		if (result > 0)
			isChanged = true;
		else
			isChanged = false;
				
		disconnect();
		
		return isChanged;
	}
	
	public String getList() throws ClassNotFoundException, SQLException
	{
		String list = "";
		JSONObject obj;
		JSONArray jsonArr = new JSONArray();
		
		connect();
		
		String sql = "SELECT * FROM RENTINFO r, ITEMS i, client c WHERE RETURNED = 'FALSE' AND r.clientid = c.clientid AND r.itemid = i.itemid";
		pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next())
		{
			obj = new JSONObject();
			obj.put("itemName", rs.getString("itemname"));
			obj.put("clientid", rs.getString("clientid"));
			obj.put("clientName", rs.getString("clientName"));
			obj.put("rentDate", rs.getString("rentdate"));
			obj.put("returnDate", rs.getString("returnDate"));
			
			jsonArr.put(obj);
		}

		disconnect();
		
		list = jsonArr.toString();
		
		System.out.println("JSON Arr : " + list);
		
		return list;
	}
	
	// items ���̺��� ��ȸ�Ͽ� �뿩���� item�� ��� true, �ƴ� ��� false�� ����
	public boolean checkRent(String itemid) throws ClassNotFoundException, SQLException
	{
		boolean isRented = false;
		
		connect();
		
		String sql = "SELECT * FROM items WHERE itemid = ?";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, itemid);
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next())
		{
			String result = rs.getString("RENTED"); 
			if(result.equals("TRUE"))
				isRented = true;
			else
				isRented = false;
		}

		disconnect();
		
		return isRented;
	}
	
}

package com.example.qr.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.qr.model.RentInfo;

/**
 * Servlet implementation class QRController
 */
//@WebServlet("/QRController")
public class QRController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QRController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String subPath = request.getPathInfo();
		String viewName = null;
		
		request.setCharacterEncoding("UTF-8");
		
		if (subPath.equals("/rent"))
		{
			int clientid = Integer.parseInt(request.getParameter("clientid"));
			int itemid = Integer.parseInt(request.getParameter("itemid"));
			String rentDate = request.getParameter("rentdate");
			String returndate = request.getParameter("returndate");
			boolean isOk = false;
			
			QRDbDao db = new QRDbDao();
			
			try {
				isOk = db.rentItem(clientid, itemid, rentDate, returndate);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			if (isOk)
			{
				out.println("success");
			}
			else
			{
				out.println("fail");
			}
			
			viewName = "/Rent_Result.html";
		}
		
		else if (subPath.equals("/return"))
		{
			int itemid = Integer.parseInt(request.getParameter("itemid"));
			boolean isOk = false;
			
			QRDbDao db = new QRDbDao();
			
			try {
				isOk = db.returnItem(itemid);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			if (isOk)
			{
				out.println("success");
			}
			else
			{
				out.println("fail");
			}
			
			viewName = "/Rent_Result.html";
		}
		
		else if (subPath.equals("/getList"))
		{
			QRDbDao db = new QRDbDao();
			
			ArrayList<RentInfo> rentInfoList = new ArrayList<>();
			
			try {
				rentInfoList = db.getList();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JSONObject jObj = new JSONObject();
			JSONArray jArr = new JSONArray();
			RentInfo rentinfo;
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			for(int i = 0; i < rentInfoList.size(); i++)
			{
				rentinfo = rentInfoList.get(i);
				jObj.put("itemid", rentinfo.getItemid());
				jObj.put("clientid", rentinfo.getClientid());
				jObj.put("rentdate", rentinfo.getRentDate());
				jObj.put("returndate", rentinfo.getReturnDate());
				
				jArr.put(jObj);
			}
			
			out.print(jArr.toString());
			out.close();
			
			viewName = "/Rent_Result.html";
		}
		
		else if (subPath.equals("/checkOverdue"))
		{
			System.out.println("Request Type : Check Overdue");
			String clientid = request.getParameter("clientid");
			QRDbDao db = new QRDbDao();
			boolean isOk = false;
			
			try {
				isOk = db.checkOverdue(Integer.parseInt(clientid));
			} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			
			out.println(isOk);
			
			out.close();
			
			viewName = "/Rent_Result.html";
		}
		
		
		if(viewName != null) {
			RequestDispatcher view = request.getRequestDispatcher(viewName);
			view.forward(request,response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String viewName = null;
		
		if(viewName != null) {
			RequestDispatcher view = request.getRequestDispatcher(viewName);
			view.forward(request,response);
		}
	}

}

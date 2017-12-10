package com.example.qr.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			String clientid = request.getParameter("clientid");
			String itemid = request.getParameter("itemid");
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			
			out.println("a" + "    " + "a");
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

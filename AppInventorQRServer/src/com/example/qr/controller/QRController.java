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
    
    public void forwardToViewName(String viewName, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	if(viewName != null) {
			RequestDispatcher view = request.getRequestDispatcher(viewName);
			view.forward(request,response);
		}
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
			String rentDate = request.getParameter("rentdate");
			String returnDate = request.getParameter("returndate");
			String itemid = request.getParameter("itemid");
			
			System.out.println(clientid + "   " + rentDate + "    " + returnDate + "    " + itemid);
			
			int clientid_int = Integer.parseInt(clientid);
			
			
			viewName = "/Rent_Result.html";
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			QRDbDao db = new QRDbDao();
			try {
				// isExist�� False�̸� �������� �ʴ� �й�
				boolean isExist = db.checkStudentId(clientid_int);
				
				if (isExist == false)
				{
					out.print("�������� �ʴ� �л��Դϴ�!");
					out.close();
					
					forwardToViewName(viewName, request, response);
					return;
				}
				
				// isOverdue�� True�� ��ü��
				boolean isOverdue = db.checkOverdue(clientid_int);
				
				if (isOverdue)
				{
					out.print("�ش� �л��� ��ü���Դϴ�!");
					out.close();
					
					forwardToViewName(viewName, request, response);
					return;
				}
				
				// isRented�� true�� �̹� �뿩���� ǰ��
				boolean isRented = db.checkRent(itemid);
				if (isRented)
				{
					out.print("�̹� �뿩���� ǰ���Դϴ�!");
					out.close();
					forwardToViewName(viewName, request, response);
					return;
				}
				
				// isChanged�� false�̸� ������ ����.
				boolean isChanged = db.changeItemStatus(itemid, true);
				if (isChanged == false)
				{
					out.print("�� �� ���� ���� �߻�...");
					out.close();
					forwardToViewName(viewName, request, response);
					return;
				}
				
				
				// isSuccess�� True�� �뿩����
				boolean isSuccess = db.enrollmentRent(clientid, rentDate, returnDate, itemid);
				
				if (isSuccess)
				{
					out.print("Success");
					out.close();
				}
				else
				{
					out.print("Failure");
					out.close();	
				}
				
			} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (subPath.equals("/checkOverdue"))
		{
			System.out.println("Request Type : Check Overdue");
			String clientid = request.getParameter("clientid");
			QRDbDao db = new QRDbDao();
			boolean isOverdue = false;
			
			try {
				isOverdue = db.checkOverdue(Integer.parseInt(clientid));
			} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			
			System.out.println(isOverdue);
			out.print(isOverdue);
			out.close();
			
			viewName = "/Rent_Result.html";
		}
		else if (subPath.equals("/qrcode"))
		{
			String itemid = request.getParameter("itemid");
			String itemName = "";
			QRDbDao db = new QRDbDao();
			
			try {
				itemName = db.getItemName(itemid);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			
			if (itemName.equals(""))
			{
				System.out.println("Failed to get item name..");
				out.print("false");
				out.close();
			}
			else
			{
				System.out.println("Item Name is " + itemName);
				out.print(itemName);
				out.close();
			}
			viewName = "/Rent_Result.html";
		}
		
		else if (subPath.equals("/return"))
		{
			String itemid = request.getParameter("itemid");
			QRDbDao db = new QRDbDao();
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			
			try {
				boolean isReturned = db.enrollmentReturn(itemid);
				
				if (isReturned)
				{
					System.out.println("Return Success!");
					out.print("TRUE");
					out.close();
				}
				else
				{
					System.out.println("Return Failed...");
					out.print("FALSE");
					out.close();
				}
				
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			viewName = "/Rent_Result.html";
		}
		else if(subPath.equals("/list"))
		{
			QRDbDao db = new QRDbDao();
			
			String list = "";
			try {
				list = db.getList();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			out.print(list);
			out.close();
			
			viewName = "/Rent_Result.html";
		}

		forwardToViewName(viewName, request, response);
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String viewName = "/Rent_Result.html";

		
		forwardToViewName(viewName, request, response);
		return;
	}

}

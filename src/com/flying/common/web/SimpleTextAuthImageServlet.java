package com.flying.common.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.flying.common.image.SimpleTextAuthImage;

public class SimpleTextAuthImageServlet extends HttpServlet {
	private static final long serialVersionUID = 7697461451986810811L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		HttpSession session = request.getSession();
		// 在内存中创建图象

		SimpleTextAuthImage authImage = new SimpleTextAuthImage();
		
		ServletOutputStream responseOutputStream = response.getOutputStream();
		authImage.output(responseOutputStream);
		session.setAttribute("SimpleTextAuthImage", authImage.getText());
		
		// 以下关闭输入流！
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}

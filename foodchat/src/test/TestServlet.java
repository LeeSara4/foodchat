package test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import imgFinder.ImageReturner;

@WebServlet("/map")
public class TestServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String str = req.getParameter("text");
		String url = ImageReturner.imageReturn(str);
		req.setAttribute("url", url);
		req.getRequestDispatcher("/viewtest.jsp").forward(req, resp);
//		resp.setStatus(200);
//		resp.getWriter().println("doGet 방식의 요청이며 200 처리");
//		String url = ImageReturner.imageReturn(str);
//		resp.getWriter().println(url);
//		System.out.println("서블릿 작동");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(500);
		resp.getWriter().println("doPost 방식의 요청이라 500 에러 처리");
	}
}

package util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class Setting {
	public static void resp(HttpServletResponse resp, int status, String text) throws IOException {
		resp.setStatus(status);
		resp.setHeader("Content-Type", "application/json;charset=utf-8");
		resp.getWriter().write(text);
	}
}

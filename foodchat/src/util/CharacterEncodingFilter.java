package util;

import java.io.IOException;
import java.net.HttpRetryException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CharacterEncodingFilter implements Filter {

	private String encoding;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// rq.setCharacterEncoding(encoding);
		HttpServletRequest request = (HttpServletRequest)req;
		String path = request.getRequestURI();
		if (path.startsWith("/view")) {
			
		} else {
			req.setCharacterEncoding("utf-8");
			res.setCharacterEncoding("utf-8");
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		encoding = config.getInitParameter("encoding");
		if (encoding == null) {
			encoding = "UTF-8";
		}
	}

	@Override
	public void destroy() {
	}

}

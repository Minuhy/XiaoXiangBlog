package minuhy.xiaoxiang.blog.filter.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class BaseHttpFilter implements Filter {
	/**
	 * ��������ʱ�������в���
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @return true������
	 * @throws UnsupportedEncodingException Other
	 * @throws IOException 
	 * @throws ServletException 
	 */
	protected abstract boolean doHttpFilterFront(HttpServletRequest req, HttpServletResponse res)
			throws UnsupportedEncodingException, ServletException, IOException;

	/**
	 * ������Ӧ��ʱ�������в���
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	protected void doHttpFilterAfter(HttpServletRequest req, HttpServletResponse res) {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
			if (doHttpFilterFront((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse)) {
				filterChain.doFilter(servletRequest, servletResponse);
				doHttpFilterAfter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
			}
		}
	}
}

package minuhy.xiaoxiang.blog.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum;

public abstract class BaseHttpServlet extends HttpServlet {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -5385258002955650552L;
	protected String currentPath;

	/**
	 * 转到提示页面
	 * 
	 * @param success  提示成功或否
	 * @param msg      提示的消息
	 * @param nextPage 下一个跳转的页面
	 * @param req      请求
	 * @param resp     响应
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 */
	public final void forwardTipPage(MsgTypeEnum type, String msg, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(RequestAttributeNameConfig.FORWARD_MSG_TYPE, type);
		req.setAttribute(RequestAttributeNameConfig.FORWARD_MSG, msg);
		req.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE, nextPage);

		// 这里的"/tips.jsp"与include使用方式一样，不需要currentPath
		req.getRequestDispatcher("/tips.jsp").forward(req, resp);
	}

	/**
	 * 转到提示页面
	 * 
	 * @param success       提示成功或否
	 * @param msg           提示的消息
	 * @param nextPageTitle 下一个页面的标题
	 * @param nextPage      下一个跳转的页面
	 * @param req           请求
	 * @param resp          响应
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 */
	public final void forwardTipPage(MsgTypeEnum type, String msg, String nextPageTitle, String nextPage,
			HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE_TITLE, nextPageTitle);
		forwardTipPage(type, msg, nextPage, req, resp);
	}

	/**
	 * 转到警告提示页面
	 * 
	 * @param msg           提示的消息
	 * @param nextPageTitle 下一个页面的标题
	 * @param nextPage      下一个跳转的页面
	 * @param req           请求
	 * @param resp          响应
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 */
	public final void forwardTipWarnPage(String msg, String nextPageTitle, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		forwardTipPage(MsgTypeEnum.WARNING, msg, nextPageTitle, nextPage, req, resp);
	}

	/**
	 * 转到成功提示页面
	 * 
	 * @param msg           提示的消息
	 * @param nextPageTitle 下一个页面的标题
	 * @param nextPage      下一个跳转的页面
	 * @param req           请求
	 * @param resp          响应
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 */
	public final void forwardTipOkPage(String msg, String nextPageTitle, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		forwardTipPage(MsgTypeEnum.SUCCESS, msg, nextPageTitle, nextPage, req, resp);
	}

	/**
	 * 转到错误提示页面
	 * 
	 * @param msg           提示的消息
	 * @param nextPageTitle 下一个页面的标题
	 * @param nextPage      下一个跳转的页面
	 * @param req           请求
	 * @param resp          响应
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 */
	public final void forwardTipErrorPage(String msg, String nextPageTitle, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		forwardTipPage(MsgTypeEnum.ERROR, msg, nextPageTitle, nextPage, req, resp);
	}

	/**
	 * 获取已经登录的用户的UserBean
	 * 
	 * @param req
	 * @return 如果为空就是没得登录
	 */
	public final UserBean getLoginUserBean(HttpServletRequest req) {
		Object obj;
		UserBean user = null;
		obj = req.getSession().getAttribute(SessionAttributeNameConfig.USER_INFO);
		if (obj instanceof UserBean) {
			user = (UserBean) obj;
		}
		return user;
	}

	/**
	 * 获取已经登录的用户的账号
	 * 
	 * @param req
	 * @return 如果为空就是没得登录
	 */
	public final String getLoginUserAccount(HttpServletRequest req) {
		UserBean user = getLoginUserBean(req);
		if (user != null) {
			return user.getAccount();
		}
		return null;
	}

	/**
	 * 返回文本
	 * 
	 * @param msg
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public final void backText(String msg, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().append(msg);
	}

	/**
	 * 返回JSON格式数据
	 * 
	 * @param msg
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public final void backJson(String json, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json; charset=UTF-8");
		resp.getWriter().append(json);
	}
}

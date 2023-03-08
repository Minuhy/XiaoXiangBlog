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
	 * ת����ʾҳ��
	 * 
	 * @param success  ��ʾ�ɹ����
	 * @param msg      ��ʾ����Ϣ
	 * @param nextPage ��һ����ת��ҳ��
	 * @param req      ����
	 * @param resp     ��Ӧ
	 * @throws ServletException Servlet�쳣
	 * @throws IOException      IO�쳣
	 */
	public final void forwardTipPage(MsgTypeEnum type, String msg, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(RequestAttributeNameConfig.FORWARD_MSG_TYPE, type);
		req.setAttribute(RequestAttributeNameConfig.FORWARD_MSG, msg);
		req.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE, nextPage);

		// �����"/tips.jsp"��includeʹ�÷�ʽһ��������ҪcurrentPath
		req.getRequestDispatcher("/tips.jsp").forward(req, resp);
	}

	/**
	 * ת����ʾҳ��
	 * 
	 * @param success       ��ʾ�ɹ����
	 * @param msg           ��ʾ����Ϣ
	 * @param nextPageTitle ��һ��ҳ��ı���
	 * @param nextPage      ��һ����ת��ҳ��
	 * @param req           ����
	 * @param resp          ��Ӧ
	 * @throws ServletException Servlet�쳣
	 * @throws IOException      IO�쳣
	 */
	public final void forwardTipPage(MsgTypeEnum type, String msg, String nextPageTitle, String nextPage,
			HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE_TITLE, nextPageTitle);
		forwardTipPage(type, msg, nextPage, req, resp);
	}

	/**
	 * ת��������ʾҳ��
	 * 
	 * @param msg           ��ʾ����Ϣ
	 * @param nextPageTitle ��һ��ҳ��ı���
	 * @param nextPage      ��һ����ת��ҳ��
	 * @param req           ����
	 * @param resp          ��Ӧ
	 * @throws ServletException Servlet�쳣
	 * @throws IOException      IO�쳣
	 */
	public final void forwardTipWarnPage(String msg, String nextPageTitle, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		forwardTipPage(MsgTypeEnum.WARNING, msg, nextPageTitle, nextPage, req, resp);
	}

	/**
	 * ת���ɹ���ʾҳ��
	 * 
	 * @param msg           ��ʾ����Ϣ
	 * @param nextPageTitle ��һ��ҳ��ı���
	 * @param nextPage      ��һ����ת��ҳ��
	 * @param req           ����
	 * @param resp          ��Ӧ
	 * @throws ServletException Servlet�쳣
	 * @throws IOException      IO�쳣
	 */
	public final void forwardTipOkPage(String msg, String nextPageTitle, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		forwardTipPage(MsgTypeEnum.SUCCESS, msg, nextPageTitle, nextPage, req, resp);
	}

	/**
	 * ת��������ʾҳ��
	 * 
	 * @param msg           ��ʾ����Ϣ
	 * @param nextPageTitle ��һ��ҳ��ı���
	 * @param nextPage      ��һ����ת��ҳ��
	 * @param req           ����
	 * @param resp          ��Ӧ
	 * @throws ServletException Servlet�쳣
	 * @throws IOException      IO�쳣
	 */
	public final void forwardTipErrorPage(String msg, String nextPageTitle, String nextPage, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		forwardTipPage(MsgTypeEnum.ERROR, msg, nextPageTitle, nextPage, req, resp);
	}

	/**
	 * ��ȡ�Ѿ���¼���û���UserBean
	 * 
	 * @param req
	 * @return ���Ϊ�վ���û�õ�¼
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
	 * ��ȡ�Ѿ���¼���û����˺�
	 * 
	 * @param req
	 * @return ���Ϊ�վ���û�õ�¼
	 */
	public final String getLoginUserAccount(HttpServletRequest req) {
		UserBean user = getLoginUserBean(req);
		if (user != null) {
			return user.getAccount();
		}
		return null;
	}

	/**
	 * �����ı�
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
	 * ����JSON��ʽ����
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

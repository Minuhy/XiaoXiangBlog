package minuhy.xiaoxiang.blog.servlet.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.CookieConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.EncryptionUtil;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.VerifyUtil;

/**
 * ��¼
 * 
 * ʹ�ô�ͳ��ʽ����
 * 
 * @author y17mm
 *
 */
@WebServlet("/user/login")
public class LoginServlet extends BaseHttpServlet {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4174368608567168240L;
	private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/login.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		HttpSession session = req.getSession();
		String loginUrl = TextUtil.isString(session.getAttribute(SessionAttributeNameConfig.LOGIN_PAGE), currentPath +"/login.jsp");

		// 1. ��ȡ����
		String account = RequestUtil.getReqParam(req, "account", "");
		String passwd = RequestUtil.getReqParam(req, "passwd", "");
		String captcha = RequestUtil.getReqParam(req, "captcha", "");
		String rememberMe = req.getParameter("rememberMe");

		

		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{} {} {} {}", account, passwd, captcha, rememberMe);
		}
		// Ԥ����
        account = account.trim();
        captcha = captcha.trim();
        passwd = passwd.trim();
        
		session.setAttribute(SessionAttributeNameConfig.LOGIN_ACC, account);
		session.setAttribute(SessionAttributeNameConfig.LOGIN_PWD, passwd);
		session.setAttribute(SessionAttributeNameConfig.LOGIN_REME, rememberMe);

		if (account == null || account.length() < 4) {

			if (DebugConfig.isDebug) {
				log.debug("�˺Ÿ�ʽ����ȷ��{}", account);
			}

			session.removeAttribute(SessionAttributeNameConfig.LOGIN_ACC);
			forwardTipWarnPage("�˺Ÿ�ʽ����ȷ��������4���ַ����˺ţ�", "��¼", loginUrl, req, resp);
			return;
		}

		if (passwd == null || passwd.length() < 4) {

			if (DebugConfig.isDebug) {
				log.debug("���볤��̫�̣�{}", passwd);
			}

			session.removeAttribute(SessionAttributeNameConfig.LOGIN_PWD);
			forwardTipWarnPage("���벻��̫�̣�����4���ַ���", "��¼", loginUrl, req, resp);
			return;
		}

		if (!VerifyUtil.verifyStringByRegEx(captcha, "^[A-z0-9]{1,8}$")) {

			if (DebugConfig.isDebug) {
				log.debug("��֤���ʽ����ȷ��{}", captcha);
			}

			forwardTipWarnPage("��֤���ʽ����ȷ", "��¼", loginUrl, req, resp);
			return;
		}

		// 3. ҵ���߼�

		Object obj;
		// �����֤��
		obj = session.getAttribute(SessionAttributeNameConfig.CAPTCHA);
		session.removeAttribute(SessionAttributeNameConfig.CAPTCHA); // ɾ����֤��
		if (obj instanceof String) {
			if (!((String) obj).equals(captcha)) {

				if (DebugConfig.isDebug) {
					log.debug("��֤�벻��ȷ��{}", captcha);
				}

				forwardTipWarnPage("��֤�벻��ȷ", "��¼",loginUrl, req, resp);
				return;
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("��֤��û�б���ȡ����Session���Ҳ�����");
			}

			forwardTipWarnPage("���Ȼ�ȡ��֤��", "��¼",loginUrl, req, resp);
			return;
		}
		
		// ��ȡIP
		String ip = req.getRemoteAddr();
		ip = TextUtil.maxLenJustify(ip, 32);
		

		UserDb userDb = new UserDb();

		try {
			// ��ѯ���ݿ��е��˺���Ϣ
			UserEntity userEntity = userDb.getUserByAccount(account);
			if (userEntity != null) {

				// У������
				String inpoutPwd = EncryptionUtil.EncodePasswd(account, passwd);
				if (inpoutPwd.equals(userEntity.getPasswd())) {
					// ��¼�ɹ�
					
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_ACC);
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PWD);
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_REME);
					
					
					int role = userEntity.getRole();
					
					UserBean userBean = new UserBean(userEntity.getId(), userEntity.getAccount(),role, userEntity.getNick(),
							userEntity.getSignature(), userEntity.getAvatar());
					userBean.setPasswd(userEntity.getPasswd());

					// ���û���Ϣ�ŵ�Session��
					session.setAttribute(SessionAttributeNameConfig.USER_INFO, userBean);

					// ��ѡ�˼�ס��
					if (rememberMe != null && "on".equals(rememberMe)) {
						// �� Cookie

						if (DebugConfig.isDebug) {
							log.debug("�ѹ�ѡ��ס�ң������ٷ�Cookie");
						}

						int maxAge = 60 * 60 * 24 * 7;

						// ��ȡ�Ѿ��ַ���cookie�Ĺ���ʱ��
						Cookie cookie = null;
						Cookie[] cookies = null;
						// ��ȡcookies������,��һ������
						cookies = req.getCookies();
						if (cookies != null) {
							for (int i = 0; i < cookies.length; i++) {
								cookie = cookies[i];
								if (cookie.getName().equals(CookieConfig.REMEMBER_ME_KEY_NAME)) {
									maxAge = cookie.getMaxAge();
									break;
								}
							}
						}
						
						if(DebugConfig.isDebug) {
							log.debug("cookieʣ��ʱ�䣺{}" + maxAge);
						}

						// �� Cookie
						String encodeStr = EncryptionUtil.EncodeByXor(
								account.length() + CookieConfig.SPLIT_KEY + passwd + account, CookieConfig.ENCODE_KEY);
						String str = java.net.URLEncoder.encode(encodeStr, "UTF-8"); // ����
						cookie = new Cookie(CookieConfig.REMEMBER_ME_KEY_NAME, str);
						cookie.setPath(currentPath);
						cookie.setMaxAge(maxAge);
						cookie.setComment("���ܺ���˺�����");
						resp.addCookie(cookie);

					} else {
						
						// ɾ��Cookie ��Ҫ����ͬ�ķ�ʽ���·�һ�Σ����ҹ���ʱ��Ϊ0
						Cookie cookie = new Cookie(CookieConfig.REMEMBER_ME_KEY_NAME, "null");
						cookie.setPath(currentPath);
						cookie.setMaxAge(0);
						cookie.setComment("���ܺ���˺�����");
						resp.addCookie(cookie);
						
						if (DebugConfig.isDebug) {
							log.debug("��ȡ����ѡ��ס�ң�Cookie�ѱ�ɾ��");
						}
					}
					
					// ֮ǰ��ҳ��
					String prePage = TextUtil.isString(
							session.getAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE),
							currentPath + "/index.jsp");
					String prePageName = TextUtil.isString(
							session.getAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE_NAME),
							"��ҳ");
					
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE);
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE_NAME);

					// ע��ģ�Ҳ�����ɾ��
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PAGE);
					
					// ��������¼ʱ��
					String welcome = "��ӭ�� " + userBean.getNick();
					if(role==1) {
						welcome+="������Ա��";
					}
					if (userDb.UpdateLoginTimeAndIp(userEntity.getId(), TimeUtil.getTimestampMs(), ip)) {

						if (DebugConfig.isDebug) {
							log.debug("��¼�ɹ���{}", account);
						}
						forwardTipOkPage(welcome, prePageName, prePage, req, resp);
						return;
					} else {

						if (DebugConfig.isDebug) {
							log.debug("��¼�ɹ�����¼ʱ�����ʧ�ܣ�{}", account);
						}

						forwardTipWarnPage(welcome+"��", prePageName, prePage, req, resp);
						return;
					}

				} else {
					if (DebugConfig.isDebug) {
						log.debug("�������{} != {}", inpoutPwd, userEntity.getPasswd());
					}

					forwardTipWarnPage("�˺Ż��������", "��¼", loginUrl, req, resp);
					return;
				}

			} else {
				if (DebugConfig.isDebug) {
					log.debug("�˺Ų����ڣ�{}", account);
				}

				forwardTipWarnPage(account + " �˺Ų�����", "��¼", loginUrl, req, resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("���ݿ����{}", e);
			forwardTipErrorPage("���ݿ����", "��¼", loginUrl, req, resp);
			return;
		}
	}

}

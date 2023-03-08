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
 * 登录
 * 
 * 使用传统方式交互
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

		// 1. 获取参数
		String account = RequestUtil.getReqParam(req, "account", "");
		String passwd = RequestUtil.getReqParam(req, "passwd", "");
		String captcha = RequestUtil.getReqParam(req, "captcha", "");
		String rememberMe = req.getParameter("rememberMe");

		

		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{} {} {} {}", account, passwd, captcha, rememberMe);
		}
		// 预处理
        account = account.trim();
        captcha = captcha.trim();
        passwd = passwd.trim();
        
		session.setAttribute(SessionAttributeNameConfig.LOGIN_ACC, account);
		session.setAttribute(SessionAttributeNameConfig.LOGIN_PWD, passwd);
		session.setAttribute(SessionAttributeNameConfig.LOGIN_REME, rememberMe);

		if (account == null || account.length() < 4) {

			if (DebugConfig.isDebug) {
				log.debug("账号格式不正确：{}", account);
			}

			session.removeAttribute(SessionAttributeNameConfig.LOGIN_ACC);
			forwardTipWarnPage("账号格式不正确（至少是4个字符的账号）", "登录", loginUrl, req, resp);
			return;
		}

		if (passwd == null || passwd.length() < 4) {

			if (DebugConfig.isDebug) {
				log.debug("密码长度太短：{}", passwd);
			}

			session.removeAttribute(SessionAttributeNameConfig.LOGIN_PWD);
			forwardTipWarnPage("密码不能太短（至少4个字符）", "登录", loginUrl, req, resp);
			return;
		}

		if (!VerifyUtil.verifyStringByRegEx(captcha, "^[A-z0-9]{1,8}$")) {

			if (DebugConfig.isDebug) {
				log.debug("验证码格式不正确：{}", captcha);
			}

			forwardTipWarnPage("验证码格式不正确", "登录", loginUrl, req, resp);
			return;
		}

		// 3. 业务逻辑

		Object obj;
		// 检查验证码
		obj = session.getAttribute(SessionAttributeNameConfig.CAPTCHA);
		session.removeAttribute(SessionAttributeNameConfig.CAPTCHA); // 删除验证码
		if (obj instanceof String) {
			if (!((String) obj).equals(captcha)) {

				if (DebugConfig.isDebug) {
					log.debug("验证码不正确：{}", captcha);
				}

				forwardTipWarnPage("验证码不正确", "登录",loginUrl, req, resp);
				return;
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("验证码没有被获取（在Session中找不到）");
			}

			forwardTipWarnPage("请先获取验证码", "登录",loginUrl, req, resp);
			return;
		}
		
		// 获取IP
		String ip = req.getRemoteAddr();
		ip = TextUtil.maxLenJustify(ip, 32);
		

		UserDb userDb = new UserDb();

		try {
			// 查询数据库中的账号信息
			UserEntity userEntity = userDb.getUserByAccount(account);
			if (userEntity != null) {

				// 校验密码
				String inpoutPwd = EncryptionUtil.EncodePasswd(account, passwd);
				if (inpoutPwd.equals(userEntity.getPasswd())) {
					// 登录成功
					
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_ACC);
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PWD);
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_REME);
					
					
					int role = userEntity.getRole();
					
					UserBean userBean = new UserBean(userEntity.getId(), userEntity.getAccount(),role, userEntity.getNick(),
							userEntity.getSignature(), userEntity.getAvatar());
					userBean.setPasswd(userEntity.getPasswd());

					// 把用户信息放到Session中
					session.setAttribute(SessionAttributeNameConfig.USER_INFO, userBean);

					// 勾选了记住我
					if (rememberMe != null && "on".equals(rememberMe)) {
						// 发 Cookie

						if (DebugConfig.isDebug) {
							log.debug("已勾选记住我，不用再发Cookie");
						}

						int maxAge = 60 * 60 * 24 * 7;

						// 获取已经分发的cookie的过期时间
						Cookie cookie = null;
						Cookie[] cookies = null;
						// 获取cookies的数据,是一个数组
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
							log.debug("cookie剩余时间：{}" + maxAge);
						}

						// 发 Cookie
						String encodeStr = EncryptionUtil.EncodeByXor(
								account.length() + CookieConfig.SPLIT_KEY + passwd + account, CookieConfig.ENCODE_KEY);
						String str = java.net.URLEncoder.encode(encodeStr, "UTF-8"); // 编码
						cookie = new Cookie(CookieConfig.REMEMBER_ME_KEY_NAME, str);
						cookie.setPath(currentPath);
						cookie.setMaxAge(maxAge);
						cookie.setComment("加密后的账号密码");
						resp.addCookie(cookie);

					} else {
						
						// 删除Cookie 需要以相同的方式再下发一次，并且过期时间为0
						Cookie cookie = new Cookie(CookieConfig.REMEMBER_ME_KEY_NAME, "null");
						cookie.setPath(currentPath);
						cookie.setMaxAge(0);
						cookie.setComment("加密后的账号密码");
						resp.addCookie(cookie);
						
						if (DebugConfig.isDebug) {
							log.debug("已取消勾选记住我，Cookie已被删除");
						}
					}
					
					// 之前的页面
					String prePage = TextUtil.isString(
							session.getAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE),
							currentPath + "/index.jsp");
					String prePageName = TextUtil.isString(
							session.getAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE_NAME),
							"首页");
					
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE);
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE_NAME);

					// 注册的，也在这边删了
					session.removeAttribute(SessionAttributeNameConfig.LOGIN_PAGE);
					
					// 更新最后登录时间
					String welcome = "欢迎你 " + userBean.getNick();
					if(role==1) {
						welcome+="（管理员）";
					}
					if (userDb.UpdateLoginTimeAndIp(userEntity.getId(), TimeUtil.getTimestampMs(), ip)) {

						if (DebugConfig.isDebug) {
							log.debug("登录成功：{}", account);
						}
						forwardTipOkPage(welcome, prePageName, prePage, req, resp);
						return;
					} else {

						if (DebugConfig.isDebug) {
							log.debug("登录成功但登录时间更新失败：{}", account);
						}

						forwardTipWarnPage(welcome+"！", prePageName, prePage, req, resp);
						return;
					}

				} else {
					if (DebugConfig.isDebug) {
						log.debug("密码错误：{} != {}", inpoutPwd, userEntity.getPasswd());
					}

					forwardTipWarnPage("账号或密码错误", "登录", loginUrl, req, resp);
					return;
				}

			} else {
				if (DebugConfig.isDebug) {
					log.debug("账号不存在：{}", account);
				}

				forwardTipWarnPage(account + " 账号不存在", "登录", loginUrl, req, resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库错误：{}", e);
			forwardTipErrorPage("数据库错误", "登录", loginUrl, req, resp);
			return;
		}
	}

}

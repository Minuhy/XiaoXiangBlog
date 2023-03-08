package minuhy.xiaoxiang.blog.servlet.user;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;

/**
 * 
 * 登出
 * 
 * 使用传统方式交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/user/logout")
public class LogoutServlet extends BaseHttpServlet {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1187436275321680233L;
	private static final Logger log = LoggerFactory.getLogger(LogoutServlet.class);
	
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		if (DebugConfig.isDebug) {
			log.debug("退出登录请求");
		}
		
		String account = getLoginUserAccount(req);
		if(account == null) {
			if (DebugConfig.isDebug) {
				log.debug("还没有登录");
			}

			forwardTipWarnPage("还没有登录", "登录", currentPath + "/login.jsp", req, resp);
			return;
		}else {
			// 删除状态
			req.getSession().removeAttribute(SessionAttributeNameConfig.USER_INFO);
			
			HttpSession session = req.getSession();
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                session.removeAttribute(name);
            }
            
			req.getSession().invalidate();
			
			if (DebugConfig.isDebug) {
				log.debug("{} 已退出登录");
			}

			forwardTipWarnPage("已退出登录", "首页", currentPath + "/index.jsp", req, resp);
			return;
		}
		
	}

}

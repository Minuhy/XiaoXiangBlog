package minuhy.xiaoxiang.blog.servlet.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.EncryptionUtil;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.UrlGeneratorUtil;

/**
 * 修改密码
 * 
 * 使用传统方式交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/user/passwd")
public class PasswdServlet extends BaseHttpServlet{

	/**
	 * UID
	 */
	private static final long serialVersionUID = -2851806618912954486L;
	private static final Logger log = LoggerFactory.getLogger(PasswdServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/passwd.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		UserBean userBean = getLoginUserBean(req);
		if(userBean == null) {
			// 没有登录
			if (DebugConfig.isDebug) {
				log.debug("没登录不能修改密码");
			}

			forwardTipWarnPage("修改密码前请先登录", "登录", 
					currentPath + UrlGeneratorUtil.getLoginUrl(currentPath+"/passwd.jsp","资料密码"),
					req, resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("修改密码");
		}
		
		// 1. 获取参数
		String mima = RequestUtil.getReqParam(req, "mima", "");
		String xinmima = RequestUtil.getReqParam(req, "xinmima", "");
		String cxxinmima = RequestUtil.getReqParam(req, "cxxinmima", "");
		
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}， {}，{}", 
					mima, xinmima,cxxinmima
					);
		}
		// 预处理
		mima = mima.trim();
		xinmima = xinmima.trim();
		cxxinmima = cxxinmima.trim();
		
		if (xinmima == null || xinmima.length() < 6||xinmima.length() > 20) {

			if (DebugConfig.isDebug) {
				log.debug("新密码长度不正确：{}", xinmima);
			}

			forwardTipWarnPage("新密码长度不对（6-20字）", "修改密码", currentPath + "/passwd.jsp", req, resp);
			return;
		}
		
		if (!xinmima.equals(cxxinmima)) {

			if (DebugConfig.isDebug) {
				log.debug("两次密码输入不一致：{}，{}", xinmima,cxxinmima);
			}

			forwardTipWarnPage("两次密码输入不一致", "修改密码", currentPath + "/passwd.jsp", req, resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 加密密码
		String rawPwd = EncryptionUtil.EncodePasswd(userBean.getAccount(), mima);
		String newPwd = EncryptionUtil.EncodePasswd(userBean.getAccount(), xinmima);
		
		UserDb userDb = new UserDb();
		// 存入数据库
		try {
			if(userDb.updatePasswd(userBean.getId(), rawPwd, newPwd)) {
				// 更新缓存
				userBean.setPasswd(newPwd);
				// 更新时间戳
				userDb.updateUpdateTimestamp(userBean.getId(), TimeUtil.getTimestampMs());
				// 写入成功
				forwardTipOkPage("修改成功", "修改密码", currentPath + "/passwd.jsp", req, resp);
				return;
			}else {
				forwardTipWarnPage("原密码错误", "修改密码", currentPath + "/passwd.jsp", req, resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库错误：{}", e);
			
			forwardTipErrorPage("数据库出错", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}

	}
}

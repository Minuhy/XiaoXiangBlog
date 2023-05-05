package minuhy.xiaoxiang.blog.servlet.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.UrlGeneratorUtil;

/**
 * 编辑个人文件
 * 
 * 使用传统方式交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/user/edit")
public class EditProfileServlet extends BaseHttpServlet {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1062450090108938279L;
	private static final Logger log = LoggerFactory.getLogger(EditProfileServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/profile.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*
 * 		 req中的值：1 -> avatarId:h085
nick:玉米子
		signature:432423423
		  sex:1
		  hometown:423432
		 link:4234234324
 * */
		
		currentPath = req.getContextPath();
		
		HttpSession session = req.getSession();
		
		UserBean userBean = getLoginUserBean(req);
		if(userBean == null) {
			// 没有登录
			if (DebugConfig.isDebug) {
				log.debug("没登录不能修改资料");
			}

			forwardTipWarnPage("修改资料前请先登录", "登录", 
					currentPath + UrlGeneratorUtil.getLoginUrl(currentPath+"/profile.jsp","资料修改"),
					req, resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("修改资料");
		}
		
		// 1. 获取参数
		String avatarIdH = RequestUtil.getReqParam(req, "avatarId", "");
		String nick = RequestUtil.getReqParam(req, "nick", "");
		String signature = RequestUtil.getReqParam(req, "signature", "");
		String sex = RequestUtil.getReqParam(req, "sex", "");
		String hometown = RequestUtil.getReqParam(req, "hometown", "");
		String link = RequestUtil.getReqParam(req, "link", "");
		
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}， {}，{}， {}，{}， {}", 
					avatarIdH, nick,
					signature, sex,
					hometown, link
					);
		}
		// 预处理
		if(avatarIdH.contains("h")) {
			avatarIdH = avatarIdH.replace("h", "");
		}
		

		// 把数据存到Session中，等下出错了好回传
		session.setAttribute(SessionAttributeNameConfig.PROFILE_AVATAR, avatarIdH);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_SIGNATURE, signature);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_SEX, sex);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_NICK, nick);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_HOMETOWN, hometown);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_LINK, link);
		
		
		// 校验数据
		int avatarId=-1;
		try {
			avatarId = Integer.parseInt(avatarIdH);
			if(avatarId<0||avatarId>138) {
				throw new NumberFormatException("超出范围");
			}
		}catch (NumberFormatException e) {
			e.printStackTrace();
			session.removeAttribute(SessionAttributeNameConfig.PROFILE_AVATAR);
			forwardTipWarnPage("头像设置错误", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}

		if (nick == null || nick.length() < 1||nick.length() > 20) {

			if (DebugConfig.isDebug) {
				log.debug("昵称长度不正确：{}", nick);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_NICK);
			forwardTipWarnPage("昵称长度不对（1-20字）", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		if(signature == null) {
			signature = "";
		}
		
		if (signature.length() > 60) {

			if (DebugConfig.isDebug) {
				log.debug("签名长度不正确：{}", signature);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_SIGNATURE);
			forwardTipWarnPage("签名长度不对（至多60字）", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		// 校验数据
		int sexNumber = 0;
		try {
			sexNumber = Integer.parseInt(sex);
			if(sexNumber!=1 && sexNumber!=2) {
				sexNumber = 0;
			}
		}catch (NumberFormatException e) {
			e.printStackTrace();
			session.removeAttribute(SessionAttributeNameConfig.PROFILE_SEX);
			forwardTipWarnPage("性别参数错误", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		if(hometown == null) {
			hometown = "";
		}
		
		if (hometown.length() > 60) {

			if (DebugConfig.isDebug) {
				log.debug("家乡长度不正确：{}", hometown);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_HOMETOWN);
			forwardTipWarnPage("家乡长度不对（至多60字）", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		if(link == null) {
			link = "";
		}
		
		if (link.length() > 30) {

			if (DebugConfig.isDebug) {
				log.debug("联系方式长度不正确：{}", link);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_LINK);
			forwardTipWarnPage("联系方式长度不对（至多30字）", "修改资料", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 创建博文对象
		UserEntity entity = new UserEntity();
		entity.setId(userBean.getId());
		entity.setAvatar(avatarId);
		entity.setNick(nick);
		entity.setSignature(signature);
		entity.setSex(sexNumber);
		entity.setHometown(hometown);
		entity.setLink(link);
		entity.setUpdateTimestamp(TimeUtil.getTimestampMs());
		
		
		// 存入数据库
		UserDb userDb = new UserDb();
		try {
			if(userDb.updateProfile(entity)) {
				// 写入成功
				userBean.updateProfile(avatarId, nick, signature);
				
				// 清除一下
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_AVATAR);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_SIGNATURE);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_SEX);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_HOMETOWN);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_LINK);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_NICK);
				
				forwardTipOkPage("修改成功", "修改资料", currentPath + "/profile.jsp", req, resp);
				return;
			}else {
				forwardTipWarnPage("数据写入失败", "修改资料", currentPath + "/profile.jsp", req, resp);
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

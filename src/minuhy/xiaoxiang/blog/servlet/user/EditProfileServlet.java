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
 * �༭�����ļ�
 * 
 * ʹ�ô�ͳ��ʽ����
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
 * 		 req�е�ֵ��1 -> avatarId:h085
nick:������
		signature:432423423
		  sex:1
		  hometown:423432
		 link:4234234324
 * */
		
		currentPath = req.getContextPath();
		
		HttpSession session = req.getSession();
		
		UserBean userBean = getLoginUserBean(req);
		if(userBean == null) {
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼�����޸�����");
			}

			forwardTipWarnPage("�޸�����ǰ���ȵ�¼", "��¼", 
					currentPath + UrlGeneratorUtil.getLoginUrl(currentPath+"/profile.jsp","�����޸�"),
					req, resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("�޸�����");
		}
		
		// 1. ��ȡ����
		String avatarIdH = RequestUtil.getReqParam(req, "avatarId", "");
		String nick = RequestUtil.getReqParam(req, "nick", "");
		String signature = RequestUtil.getReqParam(req, "signature", "");
		String sex = RequestUtil.getReqParam(req, "sex", "");
		String hometown = RequestUtil.getReqParam(req, "hometown", "");
		String link = RequestUtil.getReqParam(req, "link", "");
		
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}�� {}��{}�� {}��{}�� {}", 
					avatarIdH, nick,
					signature, sex,
					hometown, link
					);
		}
		// Ԥ����
		if(avatarIdH.contains("h")) {
			avatarIdH = avatarIdH.replace("h", "");
		}
		

		// �����ݴ浽Session�У����³����˺ûش�
		session.setAttribute(SessionAttributeNameConfig.PROFILE_AVATAR, avatarIdH);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_SIGNATURE, signature);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_SEX, sex);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_NICK, nick);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_HOMETOWN, hometown);
		session.setAttribute(SessionAttributeNameConfig.PROFILE_LINK, link);
		
		
		// У������
		int avatarId=-1;
		try {
			avatarId = Integer.parseInt(avatarIdH);
			if(avatarId<0||avatarId>138) {
				throw new NumberFormatException("������Χ");
			}
		}catch (NumberFormatException e) {
			e.printStackTrace();
			session.removeAttribute(SessionAttributeNameConfig.PROFILE_AVATAR);
			forwardTipWarnPage("ͷ�����ô���", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}

		if (nick == null || nick.length() < 1||nick.length() > 20) {

			if (DebugConfig.isDebug) {
				log.debug("�ǳƳ��Ȳ���ȷ��{}", nick);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_NICK);
			forwardTipWarnPage("�ǳƳ��Ȳ��ԣ�1-20�֣�", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		if(signature == null) {
			signature = "";
		}
		
		if (signature.length() > 60) {

			if (DebugConfig.isDebug) {
				log.debug("ǩ�����Ȳ���ȷ��{}", signature);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_SIGNATURE);
			forwardTipWarnPage("ǩ�����Ȳ��ԣ�����60�֣�", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		// У������
		int sexNumber = 0;
		try {
			sexNumber = Integer.parseInt(sex);
			if(sexNumber!=1 && sexNumber!=2) {
				sexNumber = 0;
			}
		}catch (NumberFormatException e) {
			e.printStackTrace();
			session.removeAttribute(SessionAttributeNameConfig.PROFILE_SEX);
			forwardTipWarnPage("�Ա��������", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		if(hometown == null) {
			hometown = "";
		}
		
		if (hometown.length() > 60) {

			if (DebugConfig.isDebug) {
				log.debug("���糤�Ȳ���ȷ��{}", hometown);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_HOMETOWN);
			forwardTipWarnPage("���糤�Ȳ��ԣ�����60�֣�", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		if(link == null) {
			link = "";
		}
		
		if (link.length() > 30) {

			if (DebugConfig.isDebug) {
				log.debug("��ϵ��ʽ���Ȳ���ȷ��{}", link);
			}

			session.removeAttribute(SessionAttributeNameConfig.PROFILE_LINK);
			forwardTipWarnPage("��ϵ��ʽ���Ȳ��ԣ�����30�֣�", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// �������Ķ���
		UserEntity entity = new UserEntity();
		entity.setId(userBean.getId());
		entity.setAvatar(avatarId);
		entity.setNick(nick);
		entity.setSignature(signature);
		entity.setSex(sexNumber);
		entity.setHometown(hometown);
		entity.setLink(link);
		entity.setUpdateTimestamp(TimeUtil.getTimestampMs());
		
		
		// �������ݿ�
		UserDb userDb = new UserDb();
		try {
			if(userDb.updateProfile(entity)) {
				// д��ɹ�
				userBean.updateProfile(avatarId, nick, signature);
				
				// ���һ��
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_AVATAR);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_SIGNATURE);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_SEX);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_HOMETOWN);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_LINK);
				session.removeAttribute(SessionAttributeNameConfig.PROFILE_NICK);
				
				forwardTipOkPage("�޸ĳɹ�", "�޸�����", currentPath + "/profile.jsp", req, resp);
				return;
			}else {
				forwardTipWarnPage("����д��ʧ��", "�޸�����", currentPath + "/profile.jsp", req, resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("���ݿ����{}", e);
			
			forwardTipErrorPage("���ݿ����", "�޸�����", currentPath + "/profile.jsp", req, resp);
			return;
		}

	}
}

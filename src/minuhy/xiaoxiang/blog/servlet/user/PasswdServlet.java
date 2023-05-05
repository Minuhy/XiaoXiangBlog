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
 * �޸�����
 * 
 * ʹ�ô�ͳ��ʽ����
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
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼�����޸�����");
			}

			forwardTipWarnPage("�޸�����ǰ���ȵ�¼", "��¼", 
					currentPath + UrlGeneratorUtil.getLoginUrl(currentPath+"/passwd.jsp","��������"),
					req, resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("�޸�����");
		}
		
		// 1. ��ȡ����
		String mima = RequestUtil.getReqParam(req, "mima", "");
		String xinmima = RequestUtil.getReqParam(req, "xinmima", "");
		String cxxinmima = RequestUtil.getReqParam(req, "cxxinmima", "");
		
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}�� {}��{}", 
					mima, xinmima,cxxinmima
					);
		}
		// Ԥ����
		mima = mima.trim();
		xinmima = xinmima.trim();
		cxxinmima = cxxinmima.trim();
		
		if (xinmima == null || xinmima.length() < 6||xinmima.length() > 20) {

			if (DebugConfig.isDebug) {
				log.debug("�����볤�Ȳ���ȷ��{}", xinmima);
			}

			forwardTipWarnPage("�����볤�Ȳ��ԣ�6-20�֣�", "�޸�����", currentPath + "/passwd.jsp", req, resp);
			return;
		}
		
		if (!xinmima.equals(cxxinmima)) {

			if (DebugConfig.isDebug) {
				log.debug("�����������벻һ�£�{}��{}", xinmima,cxxinmima);
			}

			forwardTipWarnPage("�����������벻һ��", "�޸�����", currentPath + "/passwd.jsp", req, resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// ��������
		String rawPwd = EncryptionUtil.EncodePasswd(userBean.getAccount(), mima);
		String newPwd = EncryptionUtil.EncodePasswd(userBean.getAccount(), xinmima);
		
		UserDb userDb = new UserDb();
		// �������ݿ�
		try {
			if(userDb.updatePasswd(userBean.getId(), rawPwd, newPwd)) {
				// ���»���
				userBean.setPasswd(newPwd);
				// ����ʱ���
				userDb.updateUpdateTimestamp(userBean.getId(), TimeUtil.getTimestampMs());
				// д��ɹ�
				forwardTipOkPage("�޸ĳɹ�", "�޸�����", currentPath + "/passwd.jsp", req, resp);
				return;
			}else {
				forwardTipWarnPage("ԭ�������", "�޸�����", currentPath + "/passwd.jsp", req, resp);
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

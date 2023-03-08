package minuhy.xiaoxiang.blog.servlet.user;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.database.common.Connector;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.EncryptionUtil;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.VerifyUtil;

/**
 * 
 * ע��
 * 
 * ʹ�ô�ͳ��ʽ����
 * 
 * @author y17mm
 *
 */
@WebServlet("/user/register")
public class RegisterServlet extends BaseHttpServlet{

	/**
	 * UID
	 */
	private static final long serialVersionUID = -5715965530582574776L;
	private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/register.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		
		// 1. ��ȡ����
		String nick = RequestUtil.getReqParam(req, "nick", "");
        String account = RequestUtil.getReqParam(req, "account", "");
        String passwd = RequestUtil.getReqParam(req, "passwd", "");
        String repwd = RequestUtil.getReqParam(req, "repwd", "");
        String captcha = RequestUtil.getReqParam(req, "captcha", "");
        
        // 2. ��������ʽ�Ƿ���ȷ
        if (DebugConfig.isDebug) {
            log.debug("������{} {} {} {} {}", nick,account,passwd,repwd,captcha);
        }
        
        // Ԥ����
        account = account.trim();
        captcha = captcha.trim();
        passwd = passwd.trim();
        repwd = repwd.trim();
        nick = nick.trim();
        
        // ��ɫĬ������ͨ�û�
        int role = 0;
        
        // ������֤�Ƿ��ǹ���Աע��
		try {
			Properties properties = new Properties();
			String propertyFile = Connector.class.getResource("/admin.properties").getPath();
        	propertyFile = URLDecoder.decode(propertyFile,"utf-8");
        	properties.load(new FileInputStream(propertyFile));
	        
	        if (DebugConfig.isDebug) {
	        	log.debug("����Ա���� -> username:{}",
	    			properties.getProperty("username"));
	        }
	        
	        if(account.equals(properties.getProperty("username"))) {
        		log.info("����Աע�᣺{}",account);
        		role = 1;
	        }
		}catch (Exception e) {
			log.error("У�����Աʱ����{}",e);
		}
        
        if(nick.length()<=0 || nick.length()>12) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("�ǳƳ��Ȳ���ȷ��1��12���ַ�����{}", nick);
            }
        	
        	forwardTipWarnPage("�ǳƳ��Ȳ���ȷ��1��12���ַ���","ע��",currentPath+"/register.jsp",req,resp);
        	return;
        }
        
        if(!VerifyUtil.verifyStringByRegEx(account,"^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$")) {
        	if(role != 1) { // �ǹ���Աֻ��ʹ���ֻ���ע��
	        	if (DebugConfig.isDebug) {
	                log.debug("�ֻ��Ÿ�ʽ����ȷ��{}", account);
	            }
	        	
	        	forwardTipWarnPage("�ֻ��Ÿ�ʽ����ȷ","ע��",currentPath+"/register.jsp",req,resp);
				return;
        	}else if(account.length()<4){
        		
        		if (DebugConfig.isDebug) {
	                log.debug("����Ա�˺ų��Ȳ���ȷ��{}", account);
	            }
	        	
	        	forwardTipWarnPage("����Ա�˺ų�������4���ַ�","ע��",currentPath+"/register.jsp",req,resp);
				return;
        	}
        }
        
        if(passwd == null || passwd.length() < 4) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("���볤��̫�̣�{}", passwd);
            }
        	
        	forwardTipWarnPage("���벻��̫�̣�����4���ַ���","ע��",currentPath+"/register.jsp",req,resp);
			return;
        }
        
        if(!passwd.equals(repwd)) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("������������벻һ�£�{} -> {}",passwd, repwd);
            }
        	
        	forwardTipWarnPage("������������벻һ��","ע��",currentPath+"/register.jsp",req,resp);
			return;
        }
        
        if(!VerifyUtil.verifyStringByRegEx(captcha,"^[A-z0-9]{1,8}$")) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("��֤���ʽ����ȷ��{}", captcha);
            }
        	
        	forwardTipWarnPage("��֤���ʽ����ȷ","ע��",currentPath+"/register.jsp",req,resp);
			return;
        }

        // 3. ҵ���߼�
        
        Object obj;
        // �����֤��
		HttpSession session = req.getSession();
		obj = session.getAttribute(SessionAttributeNameConfig.CAPTCHA);
		session.removeAttribute(SessionAttributeNameConfig.CAPTCHA); // ɾ����֤��
		if(obj instanceof String) {
			if(!((String)obj).equals(captcha)) {
	        	
	        	if (DebugConfig.isDebug) {
	                log.debug("��֤�벻��ȷ��{}", captcha);
	            }
	        	
				forwardTipWarnPage("��֤�벻��ȷ","ע��",currentPath+"/register.jsp",req,resp);
				return;
			}
		}else {
			if(DebugConfig.isDebug) {
				log.debug("��֤��û�б���ȡ����Session���Ҳ�����");
			}

			forwardTipWarnPage("���Ȼ�ȡ��֤��","ע��",currentPath+"/register.jsp",req,resp);
			return;
		}

        UserDb userDb = new UserDb();
        

        try {
	        // �z���Ƿ��]���^
	        if(userDb.getUserByAccount(account) != null) {
	        	
	        	if (DebugConfig.isDebug) {
	                log.debug("�ѱ�ע�᣺{}", account);
	            }
	        	
	        	forwardTipWarnPage(account + " �ѱ�ע��","ע��",currentPath+"/register.jsp",req,resp);
				return;
	        }
	        
	        
	        
	        
	        
	        // �洢�˺���Ϣ
	        UserEntity userEntity = new UserEntity();
	        userEntity.setAccount(account);
	        userEntity.setNick(nick);
	        userEntity.setRole(role);
	        userEntity.setPasswd(EncryptionUtil.EncodePasswd(account, passwd));
	        userEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
        
			if(userDb.writeUser(userEntity)) {
				
				if (DebugConfig.isDebug) {
			        log.debug("ע��ɹ���{}", account);
			    }
				
				// ��ȡ��¼��URL
				String loginUrl = TextUtil.isString(session.getAttribute(SessionAttributeNameConfig.LOGIN_PAGE), currentPath +"/login.jsp");
				//session.removeAttribute(SessionAttributeNameConfig.LOGIN_PAGE);
				
				forwardTipOkPage(account + " ע��ɹ�","��¼",loginUrl,req,resp);
				return;
			}else {
				
				if (DebugConfig.isDebug) {
			        log.debug("ע��ʧ�ܣ����������󣩣�{}", nick);
			    }
				
				forwardTipWarnPage("ע��ʧ�ܣ�����������","ע��",currentPath+"/register.jsp",req,resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("���ݿ����{}", e);
			forwardTipErrorPage("���ݿ����","ע��",currentPath+"/register.jsp",req,resp);
			return;
		}
	}
	
}

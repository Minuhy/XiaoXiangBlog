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
 * 注册
 * 
 * 使用传统方式交互
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
		
		
		// 1. 获取参数
		String nick = RequestUtil.getReqParam(req, "nick", "");
        String account = RequestUtil.getReqParam(req, "account", "");
        String passwd = RequestUtil.getReqParam(req, "passwd", "");
        String repwd = RequestUtil.getReqParam(req, "repwd", "");
        String captcha = RequestUtil.getReqParam(req, "captcha", "");
        
        // 2. 检查参数格式是否正确
        if (DebugConfig.isDebug) {
            log.debug("参数：{} {} {} {} {}", nick,account,passwd,repwd,captcha);
        }
        
        // 预处理
        account = account.trim();
        captcha = captcha.trim();
        passwd = passwd.trim();
        repwd = repwd.trim();
        nick = nick.trim();
        
        // 角色默认是普通用户
        int role = 0;
        
        // 尝试验证是否是管理员注册
		try {
			Properties properties = new Properties();
			String propertyFile = Connector.class.getResource("/admin.properties").getPath();
        	propertyFile = URLDecoder.decode(propertyFile,"utf-8");
        	properties.load(new FileInputStream(propertyFile));
	        
	        if (DebugConfig.isDebug) {
	        	log.debug("管理员配置 -> username:{}",
	    			properties.getProperty("username"));
	        }
	        
	        if(account.equals(properties.getProperty("username"))) {
        		log.info("管理员注册：{}",account);
        		role = 1;
	        }
		}catch (Exception e) {
			log.error("校验管理员时出错：{}",e);
		}
        
        if(nick.length()<=0 || nick.length()>12) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("昵称长度不正确（1至12个字符）：{}", nick);
            }
        	
        	forwardTipWarnPage("昵称长度不正确（1至12个字符）","注册",currentPath+"/register.jsp",req,resp);
        	return;
        }
        
        if(!VerifyUtil.verifyStringByRegEx(account,"^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$")) {
        	if(role != 1) { // 非管理员只能使用手机号注册
	        	if (DebugConfig.isDebug) {
	                log.debug("手机号格式不正确：{}", account);
	            }
	        	
	        	forwardTipWarnPage("手机号格式不正确","注册",currentPath+"/register.jsp",req,resp);
				return;
        	}else if(account.length()<4){
        		
        		if (DebugConfig.isDebug) {
	                log.debug("管理员账号长度不正确：{}", account);
	            }
	        	
	        	forwardTipWarnPage("管理员账号长度至少4个字符","注册",currentPath+"/register.jsp",req,resp);
				return;
        	}
        }
        
        if(passwd == null || passwd.length() < 4) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("密码长度太短：{}", passwd);
            }
        	
        	forwardTipWarnPage("密码不能太短（至少4个字符）","注册",currentPath+"/register.jsp",req,resp);
			return;
        }
        
        if(!passwd.equals(repwd)) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("两次输入的密码不一致：{} -> {}",passwd, repwd);
            }
        	
        	forwardTipWarnPage("两次输入的密码不一致","注册",currentPath+"/register.jsp",req,resp);
			return;
        }
        
        if(!VerifyUtil.verifyStringByRegEx(captcha,"^[A-z0-9]{1,8}$")) {
        	
        	if (DebugConfig.isDebug) {
                log.debug("验证码格式不正确：{}", captcha);
            }
        	
        	forwardTipWarnPage("验证码格式不正确","注册",currentPath+"/register.jsp",req,resp);
			return;
        }

        // 3. 业务逻辑
        
        Object obj;
        // 检查验证码
		HttpSession session = req.getSession();
		obj = session.getAttribute(SessionAttributeNameConfig.CAPTCHA);
		session.removeAttribute(SessionAttributeNameConfig.CAPTCHA); // 删除验证码
		if(obj instanceof String) {
			if(!((String)obj).equals(captcha)) {
	        	
	        	if (DebugConfig.isDebug) {
	                log.debug("验证码不正确：{}", captcha);
	            }
	        	
				forwardTipWarnPage("验证码不正确","注册",currentPath+"/register.jsp",req,resp);
				return;
			}
		}else {
			if(DebugConfig.isDebug) {
				log.debug("验证码没有被获取（在Session中找不到）");
			}

			forwardTipWarnPage("请先获取验证码","注册",currentPath+"/register.jsp",req,resp);
			return;
		}

        UserDb userDb = new UserDb();
        

        try {
	        // 檢查是否註冊過
	        if(userDb.getUserByAccount(account) != null) {
	        	
	        	if (DebugConfig.isDebug) {
	                log.debug("已被注册：{}", account);
	            }
	        	
	        	forwardTipWarnPage(account + " 已被注册","注册",currentPath+"/register.jsp",req,resp);
				return;
	        }
	        
	        
	        
	        
	        
	        // 存储账号信息
	        UserEntity userEntity = new UserEntity();
	        userEntity.setAccount(account);
	        userEntity.setNick(nick);
	        userEntity.setRole(role);
	        userEntity.setPasswd(EncryptionUtil.EncodePasswd(account, passwd));
	        userEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
        
			if(userDb.writeUser(userEntity)) {
				
				if (DebugConfig.isDebug) {
			        log.debug("注册成功：{}", account);
			    }
				
				// 获取登录的URL
				String loginUrl = TextUtil.isString(session.getAttribute(SessionAttributeNameConfig.LOGIN_PAGE), currentPath +"/login.jsp");
				//session.removeAttribute(SessionAttributeNameConfig.LOGIN_PAGE);
				
				forwardTipOkPage(account + " 注册成功","登录",loginUrl,req,resp);
				return;
			}else {
				
				if (DebugConfig.isDebug) {
			        log.debug("注册失败（服务器错误）：{}", nick);
			    }
				
				forwardTipWarnPage("注册失败（服务器错误）","注册",currentPath+"/register.jsp",req,resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库错误：{}", e);
			forwardTipErrorPage("数据库错误","注册",currentPath+"/register.jsp",req,resp);
			return;
		}
	}
	
}

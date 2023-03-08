package minuhy.xiaoxiang.blog.filter;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.RequiredLoginPageConfig;
import minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum;
import minuhy.xiaoxiang.blog.filter.common.BaseHttpFilter;
/**
 * ��ֹδ��¼�ķ���
 * 
 * @author y17mm
 *
 */
@WebFilter(filterName = "LoginFilter",urlPatterns = "/*")
public class LoginFilter extends BaseHttpFilter {
	private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);


	@Override
	protected boolean doHttpFilterFront(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if (DebugConfig.isDebug) {
            log.debug("��¼������");
        }
		
        //1.�õ������ַ
        String requestURI = req.getRequestURI();
        
        //2.�ж������ַ�Ƿ�����Ҫ��¼��ҳ��
        final String[] rlp = RequiredLoginPageConfig.RLP;
        	for(int i=0;i<rlp.length;i++) {
        		if(i%2 == 0) {
        			String url = rlp[i];
		        	if(url.startsWith("/")) {
			        	if(url.endsWith("*") && url.length() > 1) {
			        		if(requestURI.startsWith(url.substring(0, url.length()-1))) {
			        			return askLogin(req,resp,requestURI,url,rlp[i+1]); // ����
			        		}
			        	}else if(requestURI.contains(url.substring(1, url.length()))){
			        		return askLogin(req,resp,requestURI,url,rlp[i+1]); // ����
			        	}
		        	}else {
		        		if (DebugConfig.isDebug) {
		                    log.debug("����ҳ�������/��ͷ");
		                }
		        	}
        		}
        }
        
        return true;
	}
	
	/**
	 * ������Ҫ��¼��ҳ��
	 * 
	 * @param req
	 * @param resp
	 * @param reqUrl
	 * @param url
	 * @param urlText
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean askLogin(HttpServletRequest req, HttpServletResponse resp,String reqUrl,String url,String urlText) throws ServletException, IOException {
		// �ж��Ƿ��¼
		if(isLogin(req)) {
			return true;
		}
		
		String skipUrl = req.getContextPath() + "/login.jsp?u="+reqUrl+"&n="+java.net.URLEncoder.encode(urlText, "UTF-8");
		if (DebugConfig.isDebug) {
            log.debug("�����أ�{}->{} {} ��ת�� {}",reqUrl,url, urlText,skipUrl);
        }

		req.setAttribute(RequestAttributeNameConfig.FORWARD_MSG_TYPE, MsgTypeEnum.WARNING);
		req.setAttribute(RequestAttributeNameConfig.FORWARD_MSG, urlText+" ��Ҫ��¼����ܷ���");
		req.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE, skipUrl );
		req.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE_TITLE, "��¼");
		// �����"/tips.jsp"��includeʹ�÷�ʽһ��������ҪcurrentPath
		req.getRequestDispatcher("/tips.jsp").forward(req, resp);
		
		return false;
	}
	
	
	public boolean isLogin(HttpServletRequest req) {
		HttpSession session = req.getSession();
        if(session.getAttribute(SessionAttributeNameConfig.USER_INFO) instanceof UserBean){
        	// �û��Ѿ���¼��
        	return true;
        }
        return false;
	}

	

	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (DebugConfig.isDebug) {
            log.debug("��¼����������~");
        }
    }

	@Override
	public void destroy() {
		if (DebugConfig.isDebug) {
            log.debug("��¼����������~");
        }
	}
}

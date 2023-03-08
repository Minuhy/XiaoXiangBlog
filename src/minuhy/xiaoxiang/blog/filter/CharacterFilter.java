package minuhy.xiaoxiang.blog.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.filter.common.BaseHttpFilter;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

/**
 * 设置全局的请求、响应编码
 */
@WebFilter(filterName = "CharacterFilter",urlPatterns = "/*")
public class CharacterFilter extends BaseHttpFilter {
	private static final Logger log = LoggerFactory.getLogger(CharacterFilter.class);

    @Override
    public boolean doHttpFilterFront(HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        printSession(req);

        if (DebugConfig.isDebug) {
            // log.debug("设置字符编码：{}", "UTF-8");
            log.debug("请求URL地址：{}",req.getRequestURL());
            log.debug("请求的SessionID：{}", req.getSession().getId());
        }

        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        
        
        
        return true;
    }

    @Override
    public void doHttpFilterAfter(HttpServletRequest req, HttpServletResponse res) {
        // printSession(req);
    }

    private void printSession(HttpServletRequest req) {
        if(DebugConfig.isDebug){
            HttpSession session = req.getSession();
            Enumeration<String> names = session.getAttributeNames();
            int index = 0;
            while (names.hasMoreElements()) {
                index++;
                String name = names.nextElement();
                Object value = session.getAttribute(name);
                log.debug("session中的值：{} -> {}:{}", index, name, value);
            }
            if (index == 0){
                log.debug("session中没有值");
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (DebugConfig.isDebug) {
            log.debug("字符过滤器上线~ {}", "UTF-8");
        }
    }

	@Override
	public void destroy() {
		if (DebugConfig.isDebug) {
            log.debug("字符过滤器下线~ ");
        }
	}
}

package minuhy.xiaoxiang.blog.listener;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import minuhy.xiaoxiang.blog.config.StatisticsConfig;
import minuhy.xiaoxiang.blog.endpoint.MonitorWebsocket;
/**
 * 监控在线人数
 * @author y17mm
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener {
	
	static int sessionCount = 0;

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		sessionCount++;
		
		ServletContext application = arg0.getSession().getServletContext();
		application.setAttribute(StatisticsConfig.SESSION_COUNT,sessionCount);
		if(MonitorWebsocket.isSendable()) {
			MonitorWebsocket.broadcastMessage(String.format("session:%d\n", sessionCount));
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		sessionCount--;
		
		if(sessionCount<0) {
			sessionCount = 0;
		}
		
		ServletContext application = arg0.getSession().getServletContext();
		application.setAttribute(StatisticsConfig.SESSION_COUNT,sessionCount);
		if(MonitorWebsocket.isSendable()) {
			MonitorWebsocket.broadcastMessage(String.format("session:%d\n", sessionCount));
		}
	}

}

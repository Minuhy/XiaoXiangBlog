package minuhy.xiaoxiang.blog.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.StatisticsConfig;
import minuhy.xiaoxiang.blog.endpoint.MonitorWebsocket;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * ͳ��ÿ���������
 * 
 * @author Administrator
 *
 */
@WebFilter(filterName = "QpsFilter",urlPatterns = "/*")
public class QpsFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(QpsFilter.class);
	final static int cycle = 30;// 30���
	
	int[] ceche; // ���� 
	int index = 0; // ��ǰ����
	int indexCount; // ��ǰ��������
	
	long startTime = 0;
	long lastTime=0;
	
	
	@Override
	public void destroy() {
		ceche = null;
	}
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		ServletContext application = arg0.getServletContext();
		
		// ����֮ǰ��
		long time = TimeUtil.getTimestampSe(); // ����
		int nextIndex = (int) (time % cycle);
		
		// ����100s��û������
		if(time-lastTime>=cycle) {
			ceche = new int[cycle];
		}
		lastTime=time;
		
		// ������
		if(nextIndex!=index) {
			// ���
			for(int i=index;i<nextIndex;i++) {
				ceche[i] = 0;
			}
			
			// ����
			ceche[index] = indexCount; 
			
			// ����
			index = nextIndex;
			indexCount = 0;
		}
		
		// ����
		indexCount++;
		
		// ͳ��
		float sum = 0;
		for(int i=0;i<ceche.length;i++) {
			sum+=ceche[i];
		}
		
		float timeLoad = TimeUtil.getTimestampSe() - startTime;
		if(timeLoad>cycle) {
			timeLoad = cycle;
		}
		
		// �㲥
		application.setAttribute(StatisticsConfig.REQUEST_COUNT, sum/timeLoad);
		if(MonitorWebsocket.isSendable()) {
			MonitorWebsocket.broadcastMessage(String.format("qps:%.3f\n", sum/timeLoad));
		}
		
		
		// ����
		arg2.doFilter(arg0, arg1);
		
		// ��ӡ���
		if(DebugConfig.isDebug) {
			log.debug("ÿ���������{},{},{}" ,index, indexCount,timeLoad);
		}
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		startTime = TimeUtil.getTimestampSe();
		ceche = new int[cycle];
	}
	
	
}

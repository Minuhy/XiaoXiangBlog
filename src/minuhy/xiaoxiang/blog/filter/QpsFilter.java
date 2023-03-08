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
 * 统计每秒访问量的
 * 
 * @author Administrator
 *
 */
@WebFilter(filterName = "QpsFilter",urlPatterns = "/*")
public class QpsFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(QpsFilter.class);
	final static int cycle = 30;// 30秒的
	
	int[] ceche; // 缓存 
	int index = 0; // 当前索引
	int indexCount; // 当前索引计数
	
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
		
		// 处理之前的
		long time = TimeUtil.getTimestampSe(); // 秒数
		int nextIndex = (int) (time % cycle);
		
		// 过了100s还没有请求
		if(time-lastTime>=cycle) {
			ceche = new int[cycle];
		}
		lastTime=time;
		
		// 跳秒了
		if(nextIndex!=index) {
			// 填充
			for(int i=index;i<nextIndex;i++) {
				ceche[i] = 0;
			}
			
			// 归入
			ceche[index] = indexCount; 
			
			// 重置
			index = nextIndex;
			indexCount = 0;
		}
		
		// 计数
		indexCount++;
		
		// 统计
		float sum = 0;
		for(int i=0;i<ceche.length;i++) {
			sum+=ceche[i];
		}
		
		float timeLoad = TimeUtil.getTimestampSe() - startTime;
		if(timeLoad>cycle) {
			timeLoad = cycle;
		}
		
		// 广播
		application.setAttribute(StatisticsConfig.REQUEST_COUNT, sum/timeLoad);
		if(MonitorWebsocket.isSendable()) {
			MonitorWebsocket.broadcastMessage(String.format("qps:%.3f\n", sum/timeLoad));
		}
		
		
		// 放行
		arg2.doFilter(arg0, arg1);
		
		// 打印结果
		if(DebugConfig.isDebug) {
			log.debug("每秒访问量：{},{},{}" ,index, indexCount,timeLoad);
		}
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		startTime = TimeUtil.getTimestampSe();
		ceche = new int[cycle];
	}
	
	
}

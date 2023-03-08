package minuhy.xiaoxiang.blog.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
/**
 * 分页
 * @author y17mm
 */
public class PaginationBean {
	private static final Logger log = LoggerFactory.getLogger(PaginationBean.class);
    
	int current;
	int total;
	boolean next;
	boolean previous;
	
	String targetPage;
	String paramName;
	
	
	
	public String getTargetPage() {
		return targetPage;
	}
	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	/**
	 * 返回 targetPage?paramName=的形式
	 * @return
	 */
	public String getUrlPre() {
		if(targetPage.contains("?")) {
			return targetPage+"&"+paramName+"=";
		}else {
			return targetPage+"?"+paramName+"=";
		}
	}
	
	public int getCurrent() {
		return current;
	}
	
	/**
	 * 设置当前页数 ，从1开始，不能有0
	 * @param current
	 */
	public void setCurrent(int current) {
		this.current = current;
		next = true;
		previous = true;
		
		
		if(current >= total) {
			next = false;
		}
		
		// }else if(current <= 1){ // 可能上下页都没有的情况，只有一页
		
		if(current <= 1){
			previous = false;
		}
		
        if(DebugConfig.isDebug) {
            log.debug("页数：{} 下：{}-上：{}",current,next,previous);
        }
		
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public boolean isNext() {
		return next;
	}
	public boolean isPrevious() {
		return previous;
	}
}

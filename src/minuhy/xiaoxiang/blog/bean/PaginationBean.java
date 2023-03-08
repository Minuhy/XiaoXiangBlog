package minuhy.xiaoxiang.blog.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
/**
 * ��ҳ
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
	 * ���� targetPage?paramName=����ʽ
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
	 * ���õ�ǰҳ�� ����1��ʼ��������0
	 * @param current
	 */
	public void setCurrent(int current) {
		this.current = current;
		next = true;
		previous = true;
		
		
		if(current >= total) {
			next = false;
		}
		
		// }else if(current <= 1){ // ��������ҳ��û�е������ֻ��һҳ
		
		if(current <= 1){
			previous = false;
		}
		
        if(DebugConfig.isDebug) {
            log.debug("ҳ����{} �£�{}-�ϣ�{}",current,next,previous);
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

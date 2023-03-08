package minuhy.xiaoxiang.blog.util;

import javax.servlet.http.HttpServletRequest;
/**
 * ���������������
 * ����ʱ��:2023-2-24 15:58:43
 */
public class RequestUtil {
	/**
	 * ���˿��ַ�����
	 * @param req
	 * @param key
	 * @param def
	 * @return
	 */
	public static String getReqParamNotEmpty(HttpServletRequest req, String key, String def) {
		Object obj = req.getParameter(key);
		if(obj!=null && obj instanceof String && ((String) obj).length() > 0) {
			return (String) obj;
		}else {
			return def;
		}
	}
	
	public static String getReqParam(HttpServletRequest req, String key, String def) {
		Object obj = req.getParameter(key);
		if(obj!=null && obj instanceof String) {
			return (String) obj;
		}else {
			return def;
		}
	}
	
	public static Boolean getReqParam(HttpServletRequest req, String key, Boolean def) {
		Object obj = req.getParameter(key);
		if(obj!=null && obj instanceof Boolean) {
			return (Boolean) obj;
		}else {
			return def;
		}
	}
	
	public static int getReqParam(HttpServletRequest req, String key, int def) {
		Object obj = req.getParameter(key);
		if(obj!=null && obj instanceof String) {
			String n = (String) obj;
			try {
				return Integer.valueOf(n);
			}catch (NumberFormatException e) {
				return def;
			}
		}else {
			return def;
		}
	}
	
	public static String getReqAttribute(HttpServletRequest req, String key, String def) {
		Object obj = req.getAttribute(key);
		if(obj!=null && obj instanceof String) {
			return (String) obj;
		}else {
			return def;
		}
	}
	
	public static Boolean getReqAttribute(HttpServletRequest req, String key, Boolean def) {
		Object obj = req.getAttribute(key);
		if(obj!=null && obj instanceof Boolean) {
			return (Boolean) obj;
		}else {
			return def;
		}
	}
}

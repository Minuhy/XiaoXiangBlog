package minuhy.xiaoxiang.blog.util;

import javax.servlet.http.HttpSession;
/**
 * Session解析工具
 * @author y17mm
 * 创建时间:2023-2-22 12:00:17
 */
public class SessionUtil {
	public static String getAttrString(HttpSession session,String name,String def) {
		Object obj = session.getAttribute(name);
		if(obj instanceof String) {
			return (String)obj;
		}
		return def;
	}
	
	/**
	 * 获取Session中的字符串并将其从Session中清除
	 * @param session Session
	 * @param name 字符串key
	 * @param def 如果没有，则返回 默认值
	 * @return
	 */
	public static String getAttrStringAndPurge(HttpSession session,String name,String def) {
		Object obj = session.getAttribute(name);
		if(obj instanceof String) {
			session.removeAttribute(name);
			return (String)obj;
		}
		return def;
	}
}

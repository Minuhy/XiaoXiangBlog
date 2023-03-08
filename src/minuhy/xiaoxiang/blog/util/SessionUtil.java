package minuhy.xiaoxiang.blog.util;

import javax.servlet.http.HttpSession;
/**
 * Session��������
 * @author y17mm
 * ����ʱ��:2023-2-22 12:00:17
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
	 * ��ȡSession�е��ַ����������Session�����
	 * @param session Session
	 * @param name �ַ���key
	 * @param def ���û�У��򷵻� Ĭ��ֵ
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

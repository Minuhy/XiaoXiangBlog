package minuhy.xiaoxiang.blog.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum;

/**
 * 消息页面URL生成工具
 * @author y17mm
 * 创建时间:2023-2-17 20:37:11
 */
public class TipsJspUtil {
	public static String generateLink(
			String currentPath,
			MsgTypeEnum type,
			String tips,
			String link,
			String title,
			boolean isAutoGo
			) {
		
		String url = currentPath + 
				"/tips.jsp?t=" +
				String.valueOf(type).toLowerCase()+
				"&m="+
				tips+
				"&n="+
				link+
				"&h="+
				UrlEncode(title)+
				"&a="+
				String.valueOf(isAutoGo);
		
		
		return url;
	}
	
	public  static String UrlEncode(String url) {
		try {
			return URLEncoder.encode(url,"utf-8");
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}
}

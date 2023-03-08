package minuhy.xiaoxiang.blog.util;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * URL生成工具
 * @author y17mm
 * 创建时间:2023-2-24 17:21:11
 */
public class UrlGeneratorUtil {
	private static final Logger log = LoggerFactory.getLogger(UrlGeneratorUtil.class);
	public static String getReadUrl(int blogId) {
		return "/read.jsp?i=" + blogId;
	}
	
	public static String getReadCommentUrl(int blogId,int commentId) {
		return "/read.jsp?i=" + blogId+"#commentItem"+commentId;
	}

	public static String getReadUrl(String blogId) {
		return "/read.jsp?i=" + blogId;
	}

	public static String getLoginUrl(String prePage, String pageName) {
		
		try {
			prePage = java.net.URLEncoder.encode(prePage, "UTF-8");
			pageName = java.net.URLEncoder.encode(pageName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.warn("URL字符编码时出错：{}",e);
		}
		
		return  "/login.jsp?u=" + prePage + "&n=" + pageName;
		
	}
}

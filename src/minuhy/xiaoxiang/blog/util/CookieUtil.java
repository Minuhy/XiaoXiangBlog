package minuhy.xiaoxiang.blog.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.CookieConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
/**
 * Cookie解析工具，用于记住密码
 * 创建时间:2023-2-14 16:19:05
 */
public class CookieUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CookieUtil.class);
	
	public static String[] ParseAccountAndPasswd(String cookieStr) {

		if (DebugConfig.isDebug) {
			log.debug("开始解析Cookie");
		}
		
		if(cookieStr!=null && cookieStr.length()>0 && (!"null".equals(cookieStr))) {
			
			String[] r = new String[2];
			try{
				String str = java.net.URLDecoder.decode(cookieStr,"UTF-8");   // 解码
				String formatStr = EncryptionUtil.DecodeByXor(str,CookieConfig.ENCODE_KEY); // 解密
				if (DebugConfig.isDebug) {
					log.debug("解码后文本：{}",formatStr);
				}
				// 解析
				String[] strings = formatStr.split(CookieConfig.SPLIT_KEY);
				if(strings.length>0){
					int len = Integer.parseInt(strings[0]);
					String pa = formatStr.replaceFirst(len+CookieConfig.SPLIT_KEY, "");
					// 取得账号密码
					r[0] = pa.substring(pa.length()-len, pa.length());
					r[1] = pa.substring(0, pa.length()-len);
					if (DebugConfig.isDebug) {
						log.debug("解码：{} -> {}",r[0],r[1]);
					}
					return r;
				}
			}catch(Exception e){
				log.error("解析Cookie出现问题：{}", e);
			}
		}else {
			if (DebugConfig.isDebug) {
				log.debug("不解析");
			}
		}
		return null;
	}
}

package minuhy.xiaoxiang.blog.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.CookieConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
/**
 * Cookie�������ߣ����ڼ�ס����
 * ����ʱ��:2023-2-14 16:19:05
 */
public class CookieUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CookieUtil.class);
	
	public static String[] ParseAccountAndPasswd(String cookieStr) {

		if (DebugConfig.isDebug) {
			log.debug("��ʼ����Cookie");
		}
		
		if(cookieStr!=null && cookieStr.length()>0 && (!"null".equals(cookieStr))) {
			
			String[] r = new String[2];
			try{
				String str = java.net.URLDecoder.decode(cookieStr,"UTF-8");   // ����
				String formatStr = EncryptionUtil.DecodeByXor(str,CookieConfig.ENCODE_KEY); // ����
				if (DebugConfig.isDebug) {
					log.debug("������ı���{}",formatStr);
				}
				// ����
				String[] strings = formatStr.split(CookieConfig.SPLIT_KEY);
				if(strings.length>0){
					int len = Integer.parseInt(strings[0]);
					String pa = formatStr.replaceFirst(len+CookieConfig.SPLIT_KEY, "");
					// ȡ���˺�����
					r[0] = pa.substring(pa.length()-len, pa.length());
					r[1] = pa.substring(0, pa.length()-len);
					if (DebugConfig.isDebug) {
						log.debug("���룺{} -> {}",r[0],r[1]);
					}
					return r;
				}
			}catch(Exception e){
				log.error("����Cookie�������⣺{}", e);
			}
		}else {
			if (DebugConfig.isDebug) {
				log.debug("������");
			}
		}
		return null;
	}
}

package minuhy.xiaoxiang.blog.util;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 时间工具
 * @author y17mm
 * 创建时间:2023-2-17 12:44:11
 */
public class TimeUtil {
	public static long getTimestampMs() {
		return System.currentTimeMillis();
	}
	

	public static String getTimestampMsStr() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static long getTimestampSe() {
		return getTimestampMs()/1000;
	}
	
	/**
	 * yyyy-MM-dd HH:mm
	 * @param timestamp
	 * @return
	 */
	public static String timestamp2DateTime(long timestamp) {
		//当前时间毫秒的时间戳转换为日期
		Date millisecondDate= new Date(timestamp);
		//格式化时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String millisecondStrings = formatter.format(millisecondDate);
		return millisecondStrings;
	}
}

package minuhy.xiaoxiang.blog.util;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * ʱ�乤��
 * @author y17mm
 * ����ʱ��:2023-2-17 12:44:11
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
		//��ǰʱ������ʱ���ת��Ϊ����
		Date millisecondDate= new Date(timestamp);
		//��ʽ��ʱ��
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String millisecondStrings = formatter.format(millisecondDate);
		return millisecondStrings;
	}
}

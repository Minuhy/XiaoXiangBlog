package minuhy.xiaoxiang.blog.bean;

import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * ���ݻ�����¿���
 * @author y17mm
 * ����ʱ��:2023-02-20 22:23   
 */
public abstract class BeanTimeController {

	long lastGetByDatabaseTime = 0; // ���һ�δ����ݿ��ȡ���ݵ�ʱ��

	boolean canRefresh = true;
	
	/**
	 * ����ʱ��
	 */
	public void setRefresh() {
		canRefresh = true;
	}
	
	/**
	 * �Ѵ����ݿ���ˢ��
	 */
	public void refresh() {
		lastGetByDatabaseTime = TimeUtil.getTimestampMs();
		canRefresh = false;
	}

	
	/**
	 * �Ƿ���Դ����ݿ���ˢ����
	 * @return 
	 */
	public boolean isCanRefresh() {
		if(TimeUtil.getTimestampMs()-lastGetByDatabaseTime>DatabaseConfig.MIN_INTERVAL) {
			canRefresh = true;
		}
		return canRefresh;
	}
	
}

package minuhy.xiaoxiang.blog.bean;

import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * 数据缓存更新控制
 * @author y17mm
 * 创建时间:2023-02-20 22:23   
 */
public abstract class BeanTimeController {

	long lastGetByDatabaseTime = 0; // 最后一次从数据库获取数据的时间

	boolean canRefresh = true;
	
	/**
	 * 重设时间
	 */
	public void setRefresh() {
		canRefresh = true;
	}
	
	/**
	 * 已从数据库中刷新
	 */
	public void refresh() {
		lastGetByDatabaseTime = TimeUtil.getTimestampMs();
		canRefresh = false;
	}

	
	/**
	 * 是否可以从数据库中刷新了
	 * @return 
	 */
	public boolean isCanRefresh() {
		if(TimeUtil.getTimestampMs()-lastGetByDatabaseTime>DatabaseConfig.MIN_INTERVAL) {
			canRefresh = true;
		}
		return canRefresh;
	}
	
}

package minuhy.xiaoxiang.blog.bean.user;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.MessageDb;
import minuhy.xiaoxiang.blog.entity.MessageEntity;

public class MessageListBean extends BeanTimeController{
	private static final Logger log = LoggerFactory.getLogger(MessageListBean.class);

	int userId; // 用户ID
	Map<Integer,MessageEntity[]> ceche; // 数据库缓存，页数，数据组
	int total; // 总页数
	
	public MessageListBean() {
		ceche = new HashMap<Integer,MessageEntity[]>();
		total = 0;
		userId = -1;
	}
	
	public void setUserId(int userId) {
		if(userId!=this.userId) {
			this.userId = userId;
			setRefresh();
			ceche = new HashMap<Integer,MessageEntity[]>();
		}
	}

	public int getTotal() {
		return total;
	}

	public MessageEntity[] getMsgDataByPage(int page) throws SQLException {
		if(userId < 1) {
			if(DebugConfig.isDebug) {
				log.debug("没有设置UserID就开始获取消息数据");
			}
			return null;
		}
		
		boolean refresh = false; 
		MessageDb messageDb = new MessageDb();
		if(total < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = messageDb.getMessageTotalByReceiverId(userId);
			total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);

		}
		
		if(page < 0 || page > total) {
			if(DebugConfig.isDebug) {
				log.debug("页面数据不正确：{}-{}，{}",0,total,page);
			}
			return null;
		}
		
		// 如果没到刷新时候，并且缓存中有数据
		if(!isCanRefresh() && ceche.containsKey(page)) {
			return ceche.get(page);
		}
		
		// 从数据库中拿数据
		MessageEntity[] msgs = messageDb.getMessagesByReceiverIdAndPagination(userId, page);
		if(msgs!=null && msgs.length>0) {
			ceche.put(page, msgs);
		}
		
		// 设置刷新时间
		if(refresh) {
			refresh();
		}
		
		return msgs;
	}
}

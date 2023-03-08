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

	int userId; // �û�ID
	Map<Integer,MessageEntity[]> ceche; // ���ݿ⻺�棬ҳ����������
	int total; // ��ҳ��
	
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
				log.debug("û������UserID�Ϳ�ʼ��ȡ��Ϣ����");
			}
			return null;
		}
		
		boolean refresh = false; 
		MessageDb messageDb = new MessageDb();
		if(total < 1 || isCanRefresh()) {
			// ��ȡ��ҳ��
			refresh = true;
			int n = messageDb.getMessageTotalByReceiverId(userId);
			total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);

		}
		
		if(page < 0 || page > total) {
			if(DebugConfig.isDebug) {
				log.debug("ҳ�����ݲ���ȷ��{}-{}��{}",0,total,page);
			}
			return null;
		}
		
		// ���û��ˢ��ʱ�򣬲��һ�����������
		if(!isCanRefresh() && ceche.containsKey(page)) {
			return ceche.get(page);
		}
		
		// �����ݿ���������
		MessageEntity[] msgs = messageDb.getMessagesByReceiverIdAndPagination(userId, page);
		if(msgs!=null && msgs.length>0) {
			ceche.put(page, msgs);
		}
		
		// ����ˢ��ʱ��
		if(refresh) {
			refresh();
		}
		
		return msgs;
	}
}

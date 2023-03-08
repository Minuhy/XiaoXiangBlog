package minuhy.xiaoxiang.blog.bean.user;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.database.MessageDb;

public class MessageNewCountBean extends BeanTimeController {
	
	int userId; // �û�ID
	int newMsgCount; // ����Ϣ����
	
	public MessageNewCountBean() {}
	
	public int getNewMsgCount() {
		if(isCanRefresh()) {
			newMsgCount = getNewMsgData();
		}
		return newMsgCount;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		if(userId!=this.userId) {
			this.userId = userId;
			setRefresh();
		}
	}

	private int getNewMsgData() {
		MessageDb messageDb = new MessageDb();
		this.newMsgCount = messageDb.getNewMessageCountByUserId(userId);
		return newMsgCount;
	}
}

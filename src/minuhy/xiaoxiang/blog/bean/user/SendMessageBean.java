package minuhy.xiaoxiang.blog.bean.user;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.MessageDb;
import minuhy.xiaoxiang.blog.entity.MessageEntity;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * ʹ�������������Ϣ
 * @author y17mm
 *
 */
public class SendMessageBean {
	private static final Logger log = LoggerFactory.getLogger(SendMessageBean.class);

	MessageEntity messageEntity;
	
	public SendMessageBean() {
		messageEntity = new MessageEntity();
	}
	
	public int getSenderId() {
		return messageEntity.getSenderId();
	}
	public void setSenderId(int senderId) {
		messageEntity.setSenderId(senderId);
	}
	public int getReceiverId() {
		return messageEntity.getReceiverId();
	}
	public void setReceiverId(int receiverId) {
		messageEntity.setReceiverId(receiverId);
	}
	public String getTargetUrl() {
		return messageEntity.getTargetUrl();
	}
	public void setTargetUrl(String targetUrl) {
		messageEntity.setTargetUrl(targetUrl);
	}
	public String getTitle() {
		return messageEntity.getTitle();
	}
	public void setTitle(String title) {
		messageEntity.setTitle(title);
	}
	public String getContent() {
		return messageEntity.getContent();
	}
	public void setContent(String content) {
		messageEntity.setContent(content);
	}
	public int getMsgType() {
		return messageEntity.getMsgType();
	}
	public void setMsgType(int msgType) {
		messageEntity.setMsgType(msgType);
	}
	public long getCreateTimestamp() {
		return messageEntity.getCreateTimestamp();
	}
    
    public boolean send() {
    	if(getReceiverId()>0
    			&& getSenderId()>0
    			&& getTitle()!=null
    			&& !getTitle().equals("")
    			&& getMsgType()>0) {
    		// ����ʱ���
    		messageEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
    		
    		// ������Ϣ
    		MessageDb msgDb = new MessageDb();
    		try {
    			if(msgDb.writeMessage(messageEntity)) {
    				return true;
    			}
    		}catch (SQLException e) {
    			e.printStackTrace();
    			log.error("д�����ݿ�ʱ����{}",e);
			}
    	}else {
    		if(DebugConfig.isDebug) {
    			log.debug("��Ϣ������������ʧ��");
    		}
    	}
    	return false;
    }
}

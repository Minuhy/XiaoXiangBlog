package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;
import minuhy.xiaoxiang.blog.entity.MessageEntity;
/**
 * ��Ϣ������ݿ����
 * @author y17mm
 * ����ʱ��:2023-02-21 12:11
 */
public class MessageDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(MessageDb.class);
	
	/**
	 * ������Ϣ��д�����ݿ�
	 * 
	 * @param messageEntity ��Ϣ��Ϣ
	 * @return �ɹ�/��
	 * @throws SQLException ���ݿ����
	 */
	public boolean writeMessage(MessageEntity messageEntity) throws SQLException {
		String sql = "INSERT INTO `t_message` " + 
				"(`sender_id`, `receiver_id`, `target_url`, `title`, `content`, `msg_type`, `create_timestamp`) " + 
				"VALUES (?, ?, ?, ?, ?, ?, ?)";

		if (DebugConfig.isDebug) {
			log.debug("������Ϣ��{} - {}", sql, messageEntity.toString());
		}

		int result1 = 0;
		int result2 = 0;
		try {
			result1 = insert(sql, 
							String.valueOf(messageEntity.getSenderId()),
							String.valueOf(messageEntity.getReceiverId()),
							messageEntity.getTargetUrl()==null?"":messageEntity.getTargetUrl(),
							messageEntity.getTitle()==null?"":messageEntity.getTitle(),
							messageEntity.getContent()==null?"":messageEntity.getContent(),
							String.valueOf(messageEntity.getMsgType()),
							String.valueOf(messageEntity.getCreateTimestamp())
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result1);
			}
			
			// ���û��Ǳ�����Ϣ��һ -------------------------------------------------------
			sql = "UPDATE `t_user` "
					+ "SET `has_new_msg` = `has_new_msg` + 1 "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(messageEntity.getReceiverId())
				);
			
			if(result2 == 0) {
				log.debug("�û���Ϣ��־����ʧ��");
			}

			if (DebugConfig.isDebug) {
				log.debug("Ϊ�û����һ��δ����Ϣ��{}", messageEntity.getReceiverId());
			}
		} finally {
			close();
		}
		return result1 > 0;
	}

	
	/**
	 * ��ȡ��Ϣ����
	 * @param userId
	 * @return
	 */
	public int getNewMessageCountByUserId(int userId) {
		String sql = "SELECT `has_new_msg` " + 
				"FROM `t_user` " + 
				"WHERE `id` = ?" 
				+ "LIMIT 0,1;";

		if (DebugConfig.isDebug) {
			log.debug("��������Ϣ�������ݣ�{} - {}", sql, userId);
		}

		int newMsgCount = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId)
				);
			if (resultSet.next()) {
				newMsgCount = resultSet.getInt("has_new_msg");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("��������Ϣ�����������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return newMsgCount;
	}
	
	/**
	 * ��ȡĳ���û�����Ϣ����
	 * @param receiverId �û�ID
	 * @return ��Ϣ����
	 */
	public int getMessageTotalByReceiverId(int receiverId) {
		String sql = "SELECT COUNT(`id`) as `count` " + 
				"FROM `t_message` " + 
				"WHERE `receiver_id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("������Ϣ������{} - {}", sql, receiverId);
		}

		int msgTotal = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(receiverId)
				);
			if (resultSet.next()) {
				msgTotal = resultSet.getInt("count");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("��������Ϣ�����������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return msgTotal;
	}
	
	/**
	 * ��ȡ��Ϣ����
	 * @param receiverId
	 * @param pagination
	 * @return
	 * @throws SQLException
	 */
	public MessageEntity[] getMessagesByReceiverIdAndPagination(int receiverId,int pagination) throws SQLException {
		MessageEntity[] messageEntitys = new MessageEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			//// ������� ------------------------------
			String sql = "SELECT * " + 
					"FROM `t_message` " + 
					"WHERE `receiver_id` = ? " + 
					"ORDER BY `create_timestamp` DESC " + 
					"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT;
			
			if (DebugConfig.isDebug) {
				log.debug("������Ϣ���ݣ�{} - {},{}", sql, receiverId,pagination);
			}
			
			ResultSet resultSet = query(sql,
					String.valueOf(receiverId)
					);
			for(int i=0;i<messageEntitys.length;i++) {
				MessageEntity messageEntity = createMessageEntity(resultSet);
				if(messageEntity == null) {
					// �����ģ����½��������滻һ��
					MessageEntity[] MessageEntitysTemp = new MessageEntity[i];
					for(int j=0;j<i;j++) {
						MessageEntitysTemp[j] = messageEntitys[j];
					}
					messageEntitys = MessageEntitysTemp;
					// ����
					break;
				}else {
					messageEntitys[i] = messageEntity;
				}
			}
			//// ����������Ϊ�Ѷ� ------------------------------
			sql = "UPDATE `t_message` "
					+ "SET `state` = 1 "
					+ "WHERE `id` = ?";
			
			int resultSum = 0;
			for(int i=0;i<messageEntitys.length;i++) {
				if(messageEntitys[i].getState() == 0) { // ��Ϣ��δ��״̬
					int result = update(sql, 
							String.valueOf(messageEntitys[i].getId())
						);
					resultSum+=result;
					if(DebugConfig.isDebug) {
						if(result == 0) {
							log.warn("������ϢΪ�Ѷ�ʱʧ�ܣ�{} - {},{}", sql, result,messageEntitys[i].getId());
						}
					}
				}
			}
			// �����û���Ϣ�Ǳߵ�δ������===============================
			sql = "UPDATE `t_user` " + 
					"SET `has_new_msg` = if(`has_new_msg`- ?<0,0,`has_new_msg`- ?) " + 
					"WHERE `id` = ?";
			if(resultSum != 0) { // ����Ϣ������Ϊ�Ѷ���
				int result = update(sql, 
						String.valueOf(resultSum),
						String.valueOf(resultSum),
						String.valueOf(receiverId)
					);
				if(DebugConfig.isDebug) {
					if(result == 0) {
						log.warn("�����û�δ������ʱʧ�ܣ�{} - {},{}", sql, receiverId,resultSum);
					}
				}
			}
		} finally {
			close();
		}
		
		return messageEntitys;
	}

	private MessageEntity createMessageEntity(ResultSet resultSet) throws SQLException {
		MessageEntity messageEntity = null;
		if (resultSet.next()) {

			int id = resultSet.getInt("id"); // ������Ψһ��ʶ
		    int senderId= resultSet.getInt("sender_id"); // ������ID
		    int receiverId= resultSet.getInt("receiver_id"); // ������ID
		    int state= resultSet.getInt("state"); // �鿴״̬��1�Ѳ鿴��0δ�鿴
		    String targetUrl= resultSet.getString("target_url"); // Ŀ������
		    String title= resultSet.getString("title"); // ��Ϣ����
		    String content= resultSet.getString("content"); // ��Ϣ����
		    int msgType= resultSet.getInt("msg_type"); // ��Ϣ���ͣ�1���ۣ�2���ޣ�3�ظ����ۣ�4ϵͳ��Ϣ
		    long createTimestamp= resultSet.getLong("create_timestamp"); // ����ʱ��
			
			
		    messageEntity = new MessageEntity( 
		    		id,  
		    		senderId,  
		    		receiverId, 
		    		state,  
		    		targetUrl,  
		    		title,  
		    		content,  
		    		msgType,  
		    		createTimestamp
				);

			if (DebugConfig.isDebug) {
				log.debug("�鵽���ݣ�{}", messageEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("û�в鵽����");
			}
		}
		return messageEntity;
	}
	
}

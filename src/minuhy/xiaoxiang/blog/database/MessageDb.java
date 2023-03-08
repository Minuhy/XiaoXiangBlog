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
 * 消息相关数据库操作
 * @author y17mm
 * 创建时间:2023-02-21 12:11
 */
public class MessageDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(MessageDb.class);
	
	/**
	 * 发送消息，写入数据库
	 * 
	 * @param messageEntity 消息信息
	 * @return 成功/否
	 * @throws SQLException 数据库错误
	 */
	public boolean writeMessage(MessageEntity messageEntity) throws SQLException {
		String sql = "INSERT INTO `t_message` " + 
				"(`sender_id`, `receiver_id`, `target_url`, `title`, `content`, `msg_type`, `create_timestamp`) " + 
				"VALUES (?, ?, ?, ?, ?, ?, ?)";

		if (DebugConfig.isDebug) {
			log.debug("发送消息：{} - {}", sql, messageEntity.toString());
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
				log.debug("插入数据：{}", result1);
			}
			
			// 给用户那边新消息加一 -------------------------------------------------------
			sql = "UPDATE `t_user` "
					+ "SET `has_new_msg` = `has_new_msg` + 1 "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(messageEntity.getReceiverId())
				);
			
			if(result2 == 0) {
				log.debug("用户消息标志新增失败");
			}

			if (DebugConfig.isDebug) {
				log.debug("为用户添加一条未读消息：{}", messageEntity.getReceiverId());
			}
		} finally {
			close();
		}
		return result1 > 0;
	}

	
	/**
	 * 获取消息计数
	 * @param userId
	 * @return
	 */
	public int getNewMessageCountByUserId(int userId) {
		String sql = "SELECT `has_new_msg` " + 
				"FROM `t_user` " + 
				"WHERE `id` = ?" 
				+ "LIMIT 0,1;";

		if (DebugConfig.isDebug) {
			log.debug("查找新消息计数数据：{} - {}", sql, userId);
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
				log.error("查找新消息计数数据数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return newMsgCount;
	}
	
	/**
	 * 获取某个用户的消息总数
	 * @param receiverId 用户ID
	 * @return 消息总数
	 */
	public int getMessageTotalByReceiverId(int receiverId) {
		String sql = "SELECT COUNT(`id`) as `count` " + 
				"FROM `t_message` " + 
				"WHERE `receiver_id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查找消息总数：{} - {}", sql, receiverId);
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
				log.error("查找新消息计数数据数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return msgTotal;
	}
	
	/**
	 * 获取消息数据
	 * @param receiverId
	 * @param pagination
	 * @return
	 * @throws SQLException
	 */
	public MessageEntity[] getMessagesByReceiverIdAndPagination(int receiverId,int pagination) throws SQLException {
		MessageEntity[] messageEntitys = new MessageEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			//// 查出数据 ------------------------------
			String sql = "SELECT * " + 
					"FROM `t_message` " + 
					"WHERE `receiver_id` = ? " + 
					"ORDER BY `create_timestamp` DESC " + 
					"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT;
			
			if (DebugConfig.isDebug) {
				log.debug("查找消息数据：{} - {},{}", sql, receiverId,pagination);
			}
			
			ResultSet resultSet = query(sql,
					String.valueOf(receiverId)
					);
			for(int i=0;i<messageEntitys.length;i++) {
				MessageEntity messageEntity = createMessageEntity(resultSet);
				if(messageEntity == null) {
					// 不够的，重新建个数组替换一下
					MessageEntity[] MessageEntitysTemp = new MessageEntity[i];
					for(int j=0;j<i;j++) {
						MessageEntitysTemp[j] = messageEntitys[j];
					}
					messageEntitys = MessageEntitysTemp;
					// 跳出
					break;
				}else {
					messageEntitys[i] = messageEntity;
				}
			}
			//// 把数据设置为已读 ------------------------------
			sql = "UPDATE `t_message` "
					+ "SET `state` = 1 "
					+ "WHERE `id` = ?";
			
			int resultSum = 0;
			for(int i=0;i<messageEntitys.length;i++) {
				if(messageEntitys[i].getState() == 0) { // 消息是未读状态
					int result = update(sql, 
							String.valueOf(messageEntitys[i].getId())
						);
					resultSum+=result;
					if(DebugConfig.isDebug) {
						if(result == 0) {
							log.warn("设置消息为已读时失败：{} - {},{}", sql, result,messageEntitys[i].getId());
						}
					}
				}
			}
			// 减少用户信息那边的未读数量===============================
			sql = "UPDATE `t_user` " + 
					"SET `has_new_msg` = if(`has_new_msg`- ?<0,0,`has_new_msg`- ?) " + 
					"WHERE `id` = ?";
			if(resultSum != 0) { // 有消息被设置为已读了
				int result = update(sql, 
						String.valueOf(resultSum),
						String.valueOf(resultSum),
						String.valueOf(receiverId)
					);
				if(DebugConfig.isDebug) {
					if(result == 0) {
						log.warn("减少用户未读数量时失败：{} - {},{}", sql, receiverId,resultSum);
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

			int id = resultSet.getInt("id"); // 主键，唯一标识
		    int senderId= resultSet.getInt("sender_id"); // 发送者ID
		    int receiverId= resultSet.getInt("receiver_id"); // 接收者ID
		    int state= resultSet.getInt("state"); // 查看状态：1已查看，0未查看
		    String targetUrl= resultSet.getString("target_url"); // 目标链接
		    String title= resultSet.getString("title"); // 消息标题
		    String content= resultSet.getString("content"); // 消息内容
		    int msgType= resultSet.getInt("msg_type"); // 消息类型：1评论，2点赞，3回复评论，4系统消息
		    long createTimestamp= resultSet.getLong("create_timestamp"); // 创建时间
			
			
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
				log.debug("查到数据：{}", messageEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("没有查到数据");
			}
		}
		return messageEntity;
	}
	
}

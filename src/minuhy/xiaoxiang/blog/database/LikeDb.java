package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;
import minuhy.xiaoxiang.blog.entity.LikeEntity;

/**
 * ����������ݿ����
 * @author y17mm
 * ����ʱ��:2023-02-17 19:19 
 */
public class LikeDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(LikeDb.class);

	/**
	 * �û�ID�Ͳ���ID�����û�����ƪ���ĵĵ���״̬
	 * 
	 * @param userId �û����
	 * @param blogId ���ı��
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException
	 */
	public LikeEntity getLikeByUserIdAndBlogId(int userId, int blogId) throws SQLException {
		String sql = "SELECT * " 
				+ "FROM `t_like` " 
				+ "WHERE `user_id`=? AND `blog_id`=?" 
				+ "LIMIT 0,1;";

		if (DebugConfig.isDebug) {
			log.debug("���ҵ������ݣ�{} - {}��{}", sql, userId, blogId);
		}

		LikeEntity likeEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId), 
					String.valueOf(blogId)
				);
			likeEntity = createLikeEntity(resultSet);
		} finally {
			close();
		}
		return likeEntity;
	}

	/**
	 * ͨ�����������һ�� LikeEntity
	 * 
	 * @param resultSet �����
	 * @return LikeEntity
	 * @throws SQLException
	 */
	public LikeEntity createLikeEntity(ResultSet resultSet) throws SQLException {
		LikeEntity likeEntity = null;
		if (resultSet.next()) {
			int id = resultSet.getInt("id"); // ������Ψһ
			int state = resultSet.getInt("state"); // ״̬��1���ޣ�-1���ԣ�0ȡ��
			int blogId = resultSet.getInt("blog_id"); // �����޵Ĳ���
			int userId = resultSet.getInt("user_id"); // ���޵��û�
			long createTimestamp = resultSet.getLong("create_timestamp"); // ����ʱ��
			long updateTimestamp = resultSet.getLong("update_timestamp"); // �޸�ʱ��

			likeEntity = new LikeEntity(
					id, 
					state, 
					blogId, 
					userId, 
					createTimestamp, 
					updateTimestamp
				);

			if (DebugConfig.isDebug) {
				log.debug("�鵽���ݣ�{}", likeEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("û�в鵽����");
			}
		}
		return likeEntity;
	}

	/**
	 * ���µ���
	 * 
	 * @param state     ����״̬ ״̬��1���ޣ�-1���ԣ�0ȡ��
	 * @param userId    �û�ID
	 * @param blogId    ����ID
	 * @param timestamp ��ǰʱ��
	 * @return �ɹ�/��
	 * @throws SQLException
	 */
	public boolean UpdateLike(int state, int userId, String blogId, long timestamp) throws SQLException {
		String sql = "UPDATE `t_like` " 
				+ "SET `state`=?,`update_timestamp`=? " 
				+ "WHERE `user_id`=? AND `blog_id`=?";

		if (DebugConfig.isDebug) {
			log.debug("���µ��ޣ�{} - {},{},{},{}", sql, state, userId, blogId, timestamp);
		}

		int result = 0;
		try {
			result = update(sql, 
					String.valueOf(state), 
					String.valueOf(timestamp), 
					String.valueOf(userId), 
					blogId
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * ��ӵ�����Ϣ�����ݿ��в���������ӣ������޸�
	 * 
	 * @param state     ����״̬ ״̬��1���ޣ�-1���ԣ�0ȡ��
	 * @param userId    �û�ID
	 * @param blogId    ����ID
	 * @param timestamp ��ǰʱ��
	 * @return �ɹ�/��
	 * @throws SQLException
	 */
	public boolean setLike(int state, int userId, String blogId, long timestamp) throws SQLException {
		String sql = "SELECT * " 
				+ "FROM `t_like` " 
				+ "WHERE `user_id`=? AND `blog_id`=?" 
				+ "LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("���ҵ������ݣ�{} - {}��{}", sql, userId, blogId);
		}
		
		int likeCount = 0;

		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId), 
					blogId
				);
			
			int preState = 0;
			int result = 0;
			
			if (resultSet.next()) {
				preState = resultSet.getInt("state"); // ֮ǰ��̬��
				
				// �޸�
				String updateSql = "UPDATE `t_like` " 
						+ "SET `state`=?,`update_timestamp`=? "
						+ "WHERE `user_id`=? AND `blog_id`=?";

				if (DebugConfig.isDebug) {
					log.debug("���µ��ޣ�{} - {},{},{},{}", sql, state, userId, blogId, timestamp);
				}

				result = update(updateSql, 
						String.valueOf(state), 
						String.valueOf(timestamp), 
						String.valueOf(userId), 
						blogId
					);

				if (DebugConfig.isDebug) {
					log.debug("�������ݣ�{}", result);
				}
			} else {
				preState = 0; // ֮ǰ��̬��
				// д��
				String insertSql = "INSERT INTO `t_like` " 
						+ "(`state`, `user_id`, `blog_id`, `create_timestamp`) "
						+ "VALUES (?,?,?,?)";

				if (DebugConfig.isDebug) {
					log.debug("��¼���ޣ�{} - {},{},{},{}", sql, state, userId, blogId, timestamp);
				}

				result = insert(insertSql, 
						String.valueOf(state), 
						String.valueOf(userId), 
						blogId, 
						String.valueOf(timestamp)
					);

				if (DebugConfig.isDebug) {
					log.debug("�������ݣ�{}", result);
				}
			}
			
			
			if(preState == 0 && state == 1) { // ֮ǰ�ޱ�̬�����ڵ���
				likeCount = 1;
			}else if(preState == -1 && state == 1) { // ֮ǰ���ԣ����ڵ���
				likeCount = 1;
			}else if(preState == 1 && state == 1) { // ֮ǰ���ޣ����ڵ���
				likeCount = 0;
			}else
				
			
			if(preState == 0 && state == 0) { // ֮ǰ�ޱ�̬�������ޱ�̬
				likeCount = 0;
			}else if(preState == -1 && state == 0) { // ֮ǰ���ԣ������ޱ�̬
				likeCount = 0;
			}else if(preState == 1 && state == 0) { // ֮ǰ���ޣ������ޱ�̬
				likeCount = -1;
			}else
			
				
			if(preState == 0 && state == -1) { // ֮ǰ�ޱ�̬�����ڷ���
				likeCount = 0;
			}else if(preState == -1 && state == -1) { // ֮ǰ���ԣ����ڷ���
				likeCount = 0;
			}else if(preState == 1 && state == -1) { // ֮ǰ���ޣ����ڷ���
				likeCount = -1;
			}
			
			int result1 = 0;
			int result2 = 0;
			if(likeCount != 0) {
				// ------------------------------------------
				String updateBlogLikeCountSql = "UPDATE `t_blog` "
						+ "SET `like_count` = `like_count` + ? "
						+ "WHERE `id` = ?";
				
				if (DebugConfig.isDebug) {
					log.debug("���Ӳ���ϲ������{} - {}", likeCount, blogId);
				}
				
				result1 = update(updateBlogLikeCountSql, 
						String.valueOf(likeCount), 
						blogId
					);

				if (DebugConfig.isDebug) {
					log.debug("���Ӳ���ϲ���������{}",result1);
				}
				
				// ------------------------------------------
				String selectAuthorIdByBlogIdSql = "SELECT `author_id` " + 
						"FROM `t_blog` " + 
						"WHERE `id` = ?";

				
				if (DebugConfig.isDebug) {
					log.debug("��ȡ����ID��{}", blogId);
				}
				
				resultSet = query(selectAuthorIdByBlogIdSql, 
						blogId
					);
				
				int authorId = 0;
				if (resultSet.next()) {
					authorId = resultSet.getInt("author_id"); // ��ȡ����ID
				}
				
				if (DebugConfig.isDebug) {
					log.debug("����ID��{} ", authorId);
				}
				// ------------------------------------------
				if(authorId!=0) {
					// ��������ӵ�����
					String updateUserBlogLikeCountSql = "UPDATE `t_user` "
							+ "SET `blog_like_count` = `blog_like_count` + ? "
							+ "WHERE `id` = ?";
					
					if (DebugConfig.isDebug) {
						log.debug("��������ϲ������{} - {}", likeCount, authorId);
					}
					
					result2 = update(updateUserBlogLikeCountSql, 
							String.valueOf(likeCount), 
							String.valueOf(authorId)
						);
					
					if (DebugConfig.isDebug) {
						log.debug("��������ϲ���������{}",result2);
					}
				}
			}
			
			return result > 0;
		} finally {
			close();
		}
	}

	/**
	 * ���ޣ�д�����ݿ�
	 * 
	 * @param likeEntity ������Ϣ
	 * @return Ӱ������
	 * @throws SQLException ���ݿ����
	 */
	public boolean writeLike(LikeEntity likeEntity) throws SQLException {
		String sql = "INSERT INTO `t_like` " 
				+ "(`state`, `user_id`, `blog_id`, `create_timestamp`) "
				+ "VALUES (?,?,?,?)";

		if (DebugConfig.isDebug) {
			log.debug("��¼���ޣ�{} - {}", sql, likeEntity.toString());
		}

		int result = 0;
		try {
			result = insert(sql, 
					String.valueOf(likeEntity.getState()), 
					String.valueOf(likeEntity.getUserId()),
					String.valueOf(likeEntity.getBlogId()), 
					String.valueOf(likeEntity.getCreateTimestamp())
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

}

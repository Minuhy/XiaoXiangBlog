package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;
import minuhy.xiaoxiang.blog.entity.CommentEntity;

public class CommentDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(CommentDb.class);


	/**
	 * ��ʱ��˳���ȡ���ĵ�����
	 * @param blogId ����ID
	 * @param pageNumber ҳ������0��ʼ
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException
	 */
	public CommentEntity[] getBlogCommentsByPageOrderByTime(int blogId,int pagination) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_comment` " + 
				"WHERE `blog_id` = ? AND `active` = ? " + 
				"ORDER BY `create_timestamp` DESC " + 
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("��ȡ���ĵ����ۣ�{} - {}", sql, blogId);
		}

		CommentEntity[] commentEntitys = new CommentEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql,
					String.valueOf(blogId),
					"1"
					);
			for(int i=0;i<commentEntitys.length;i++) {
				CommentEntity commentEntity = createCommentEntity(resultSet);
				if(commentEntity == null) {
					// ���� commentEntitys.length �ĳ��ȣ����½��������滻һ��
					CommentEntity[] commentEntitysTemp = new CommentEntity[i];
					for(int j=0;j<i;j++) {
						commentEntitysTemp[j] = commentEntitys[j];
					}
					commentEntitys =commentEntitysTemp;
					// ����
					break;
				}else {
					commentEntitys[i] = commentEntity;
				}
			}
		} finally {
			close();
		}
		return commentEntitys;
	}
	

	/**
	 * ͨ��ID����������
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public String getCommentContentById(int id) throws SQLException {
		String sql = "SELECT `content`,`active` " + 
				"FROM `t_comment` " + 
				"WHERE `id`= ? " + 
				"LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("��ȡ���ĵ��������ݣ�{} - {}", sql, id);
		}

		String content = null;
		try {
			ResultSet resultSet = query(sql,
					String.valueOf(id)
					);
			if (resultSet.next()) {
				int active = resultSet.getInt("active"); // �Ƿ���Ч��1��Ч��0��Ч
				content = resultSet.getString("content"); // �ظ�������
				if(active == 0) {
					content = "�ѱ�ɾ��";
				}
			}
		} finally {
			close();
		}
		return content;
	}
	
	
	
	/**
	 * ��ȡ��������
	 * @param blogId ����ID
	 * @return
	 * @throws SQLException 
	 */
	public int getCommentTotalByBlogId(int blogId) throws SQLException {
		String sql = "SELECT COUNT(*) AS `count` " + 
				"FROM `t_comment` " + 
				"WHERE `blog_id` = ? AND `active` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��������������{} - {}", sql, blogId);
		}

		int commentTotal = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(blogId),
					"1"
				);
			if (resultSet.next()) {
				commentTotal = resultSet.getInt("count");
			}
		} finally {
			close();
		}
		return commentTotal;
	}
	
	/**
	 * д������
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public boolean writePostComment(CommentEntity entity) throws SQLException {
		String sql = "INSERT INTO "
				+ "`t_comment` ( `blog_id`, `user_id`,`reply_id`, `content`, `create_timestamp`) "
				+ "VALUES ( ?, ?, ?,?, ?)";

		if (DebugConfig.isDebug) {
			log.debug("�������ۣ�{} - {}", sql, entity);
		}

		int result1 = 0;
		int result2 = 0;
		try {
			result1 = insert(sql, 
					String.valueOf(entity.getBlogId()), 
					String.valueOf(entity.getUserId()), 
					String.valueOf(entity.getReplyId()), 
					entity.getContent(), 
					String.valueOf(entity.getCreateTimestamp())
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result1);
			}
			
			// �������߲��ļ���
			sql = "UPDATE `t_blog` "
					+ "SET `comment_count` = `comment_count` + 1 "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(entity.getBlogId())
					);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result2);
			}
		} finally {
			close();
		}
		return result1 > 0;
	}

	/**
	 * ͨ�����������һ�� CommentEntity
	 * 
	 * @param resultSet �����
	 * @return CommentEntity
	 * @throws SQLException
	 */
	public static CommentEntity createCommentEntity(ResultSet resultSet) throws SQLException {
		CommentEntity commentEntity = null;
		if (resultSet.next()) {

			int id = resultSet.getInt("id"); // ������Ψһ��ʶ
			int active = resultSet.getInt("active"); // �Ƿ���Ч��1��Ч��0��Ч
			int blogId = resultSet.getInt("blog_id"); // ����ID������ƪ�����µ�����
			int userId= resultSet.getInt("user_id"); // ���۷�����ID
			int replyId= resultSet.getInt("reply_id"); // ���ظ�������ID
			String content = resultSet.getString("content"); // �ظ�������
			long createTimestamp = resultSet.getLong("create_timestamp"); // ����ʱ��
			long updateTimestamp = resultSet.getLong("update_timestamp"); // �޸�ʱ��
			

			commentEntity = new CommentEntity(id, 
					active, 
					blogId, 
					userId, 
					replyId, 
					content, 
					createTimestamp, 
					updateTimestamp
				);

			if (DebugConfig.isDebug) {
				log.debug("�鵽���ݣ�{}", commentEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("û�в鵽����");
			}
		}
		return commentEntity;
	}


	public int getBlogIdByReplyId(int replyId) throws SQLException {
		String sql = "SELECT `blog_id` " + 
				"FROM `t_comment` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��������ID�鲩��ID��{} - {}", sql, replyId);
		}

		int blogId = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(replyId)//,
					// "1" ���Իظ���ɾ��������
				);
			if (resultSet.next()) {
				blogId = resultSet.getInt("blog_id");
			}
		} finally {
			close();
		}
		return blogId;
	}


	/**
	 * ���û�����������ID
	 * 
	 * @param userId
	 * @param blogId
	 * @return
	 * @throws SQLException
	 */
	public int getNewCommentIdByUserIdAndBlogId(int userId, int blogId) throws SQLException {
		String sql = "SELECT `id` " + 
				"FROM `t_comment` " + 
				"WHERE `user_id`=? AND `blog_id`=? " + 
				"ORDER BY `create_timestamp` DESC " + 
				"LIMIT 0,1";
		
		if (DebugConfig.isDebug) {
			log.debug("���û�����������ID��{} - {}��{}", sql, userId,blogId);
		}

		int commentId = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId), 
					String.valueOf(blogId)//,
					// "1" ���Իظ���ɾ��������
				);
			if (resultSet.next()) {
				commentId = resultSet.getInt("id");
			}
		} finally {
			close();
		}
		return commentId;
	}

	/**
	 * ��ѯһ�����۵����ߺͶ�Ӧ�Ĳ���
	 * @param commetId
	 * @return 
	 * @throws SQLException 
	 */
	public int[] getUserIdAndBlogIdByCommentId(int commetId) throws SQLException {
		String sql = "SELECT `user_id`,`blog_id` " + 
				"FROM `t_comment` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��ѯ���۵����ߣ�{} - {}", sql, commetId);
		}

		int id[] = new int[2];
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(commetId)
				);
			if (resultSet.next()) {
				id[0] = resultSet.getInt("user_id");
				id[1] = resultSet.getInt("blog_id");
			}
		} finally {
			close();
		}
		return id;
	}


	/**
	 * ��������״̬Ϊ�����ã�ͬʱ���ٲ������ۼ���
	 * @param blogId
	 * @param commetId
	 * @param updateTime 
	 * @return
	 * @throws SQLException 
	 */
	public boolean deleteComment(int blogId, int commetId, long updateTime) throws SQLException {
		String sql = "UPDATE `t_comment` "
				+ "SET `active` = ? , `update_timestamp` = ? "
				+ "WHERE `id` = ?  and `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("ɾ�����ģ�{} - {}��{}", sql, blogId,commetId);
		}

		int result1 = 0;
		int result2 = 0;
		try {
			result1 = update(sql, 
					String.valueOf(0), 
					String.valueOf(updateTime), 
					String.valueOf(commetId),
					"1"
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result1);
			}
			
			// �������߲��ļ���
			sql = "UPDATE `t_blog` " + 
					"SET `comment_count` = IF(`comment_count` - '1'>'0',`comment_count` - '1','0') " + 
					"WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(blogId)
					);
			
			if(result2<1) {
				log.warn("�������ۼ�������{}",blogId);
			}

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result2);
			}
		} finally {
			close();
		}
		return result1 > 0;
	}

	/**
	 * ͨ��ID��ȡ����
	 * @param replyId
	 * @return
	 * @throws SQLException 
	 */
	public CommentEntity getCommentById(int replyId) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_comment` " + 
				"WHERE `id` = ? " + 
				"LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("�������ۣ�{} - {}", sql, replyId);
		}

		CommentEntity commentEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(replyId)
				);
			commentEntity = createCommentEntity(resultSet);
		} finally {
			close();
		}
		return commentEntity;
	}
	
	
}

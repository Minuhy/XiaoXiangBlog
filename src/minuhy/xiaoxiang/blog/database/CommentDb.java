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
	 * 按时间顺序获取博文的评论
	 * @param blogId 博客ID
	 * @param pageNumber 页数，从0开始
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException
	 */
	public CommentEntity[] getBlogCommentsByPageOrderByTime(int blogId,int pagination) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_comment` " + 
				"WHERE `blog_id` = ? AND `active` = ? " + 
				"ORDER BY `create_timestamp` DESC " + 
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("获取博文的评论：{} - {}", sql, blogId);
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
					// 不够 commentEntitys.length 的长度，重新建个数组替换一下
					CommentEntity[] commentEntitysTemp = new CommentEntity[i];
					for(int j=0;j<i;j++) {
						commentEntitysTemp[j] = commentEntitys[j];
					}
					commentEntitys =commentEntitysTemp;
					// 跳出
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
	 * 通过ID查评论内容
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
			log.debug("获取博文的评论内容：{} - {}", sql, id);
		}

		String content = null;
		try {
			ResultSet resultSet = query(sql,
					String.valueOf(id)
					);
			if (resultSet.next()) {
				int active = resultSet.getInt("active"); // 是否有效，1有效，0无效
				content = resultSet.getString("content"); // 回复的内容
				if(active == 0) {
					content = "已被删除";
				}
			}
		} finally {
			close();
		}
		return content;
	}
	
	
	
	/**
	 * 获取评论总数
	 * @param blogId 博客ID
	 * @return
	 * @throws SQLException 
	 */
	public int getCommentTotalByBlogId(int blogId) throws SQLException {
		String sql = "SELECT COUNT(*) AS `count` " + 
				"FROM `t_comment` " + 
				"WHERE `blog_id` = ? AND `active` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查找评论总数：{} - {}", sql, blogId);
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
	 * 写入评论
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public boolean writePostComment(CommentEntity entity) throws SQLException {
		String sql = "INSERT INTO "
				+ "`t_comment` ( `blog_id`, `user_id`,`reply_id`, `content`, `create_timestamp`) "
				+ "VALUES ( ?, ?, ?,?, ?)";

		if (DebugConfig.isDebug) {
			log.debug("发布评论：{} - {}", sql, entity);
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
				log.debug("插入数据：{}", result1);
			}
			
			// 增加作者博文计数
			sql = "UPDATE `t_blog` "
					+ "SET `comment_count` = `comment_count` + 1 "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(entity.getBlogId())
					);

			if (DebugConfig.isDebug) {
				log.debug("更新数据：{}", result2);
			}
		} finally {
			close();
		}
		return result1 > 0;
	}

	/**
	 * 通过结果集创建一个 CommentEntity
	 * 
	 * @param resultSet 结果集
	 * @return CommentEntity
	 * @throws SQLException
	 */
	public static CommentEntity createCommentEntity(ResultSet resultSet) throws SQLException {
		CommentEntity commentEntity = null;
		if (resultSet.next()) {

			int id = resultSet.getInt("id"); // 主键，唯一标识
			int active = resultSet.getInt("active"); // 是否有效，1有效，0无效
			int blogId = resultSet.getInt("blog_id"); // 博文ID，在哪篇博文下的评论
			int userId= resultSet.getInt("user_id"); // 评论发送者ID
			int replyId= resultSet.getInt("reply_id"); // 被回复的评论ID
			String content = resultSet.getString("content"); // 回复的内容
			long createTimestamp = resultSet.getLong("create_timestamp"); // 发布时间
			long updateTimestamp = resultSet.getLong("update_timestamp"); // 修改时间
			

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
				log.debug("查到数据：{}", commentEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("没有查到数据");
			}
		}
		return commentEntity;
	}


	public int getBlogIdByReplyId(int replyId) throws SQLException {
		String sql = "SELECT `blog_id` " + 
				"FROM `t_comment` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("根据评论ID查博客ID：{} - {}", sql, replyId);
		}

		int blogId = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(replyId)//,
					// "1" 可以回复已删除的评论
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
	 * 查用户的最新评论ID
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
			log.debug("查用户的最新评论ID：{} - {}，{}", sql, userId,blogId);
		}

		int commentId = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId), 
					String.valueOf(blogId)//,
					// "1" 可以回复已删除的评论
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
	 * 查询一个评论的作者和对应的博客
	 * @param commetId
	 * @return 
	 * @throws SQLException 
	 */
	public int[] getUserIdAndBlogIdByCommentId(int commetId) throws SQLException {
		String sql = "SELECT `user_id`,`blog_id` " + 
				"FROM `t_comment` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查询评论的作者：{} - {}", sql, commetId);
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
	 * 设置评论状态为不可用，同时减少博客评论计数
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
			log.debug("删除博文：{} - {}，{}", sql, blogId,commetId);
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
				log.debug("更新数据：{}", result1);
			}
			
			// 减少作者博文计数
			sql = "UPDATE `t_blog` " + 
					"SET `comment_count` = IF(`comment_count` - '1'>'0',`comment_count` - '1','0') " + 
					"WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(blogId)
					);
			
			if(result2<1) {
				log.warn("博客评论计数错误，{}",blogId);
			}

			if (DebugConfig.isDebug) {
				log.debug("更新数据：{}", result2);
			}
		} finally {
			close();
		}
		return result1 > 0;
	}

	/**
	 * 通过ID获取评论
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
			log.debug("查找评论：{} - {}", sql, replyId);
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

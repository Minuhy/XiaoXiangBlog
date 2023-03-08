package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;
import minuhy.xiaoxiang.blog.entity.LikeEntity;

/**
 * 点赞相关数据库操作
 * @author y17mm
 * 创建时间:2023-02-17 19:19 
 */
public class LikeDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(LikeDb.class);

	/**
	 * 用户ID和博文ID查找用户对这篇博文的点赞状态
	 * 
	 * @param userId 用户编号
	 * @param blogId 博文编号
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException
	 */
	public LikeEntity getLikeByUserIdAndBlogId(int userId, int blogId) throws SQLException {
		String sql = "SELECT * " 
				+ "FROM `t_like` " 
				+ "WHERE `user_id`=? AND `blog_id`=?" 
				+ "LIMIT 0,1;";

		if (DebugConfig.isDebug) {
			log.debug("查找点赞数据：{} - {}，{}", sql, userId, blogId);
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
	 * 通过结果集创建一个 LikeEntity
	 * 
	 * @param resultSet 结果集
	 * @return LikeEntity
	 * @throws SQLException
	 */
	public LikeEntity createLikeEntity(ResultSet resultSet) throws SQLException {
		LikeEntity likeEntity = null;
		if (resultSet.next()) {
			int id = resultSet.getInt("id"); // 主键，唯一
			int state = resultSet.getInt("state"); // 状态：1点赞，-1反对，0取消
			int blogId = resultSet.getInt("blog_id"); // 被点赞的博文
			int userId = resultSet.getInt("user_id"); // 点赞的用户
			long createTimestamp = resultSet.getLong("create_timestamp"); // 点赞时间
			long updateTimestamp = resultSet.getLong("update_timestamp"); // 修改时间

			likeEntity = new LikeEntity(
					id, 
					state, 
					blogId, 
					userId, 
					createTimestamp, 
					updateTimestamp
				);

			if (DebugConfig.isDebug) {
				log.debug("查到数据：{}", likeEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("没有查到数据");
			}
		}
		return likeEntity;
	}

	/**
	 * 更新点赞
	 * 
	 * @param state     点赞状态 状态：1点赞，-1反对，0取消
	 * @param userId    用户ID
	 * @param blogId    博文ID
	 * @param timestamp 当前时间
	 * @return 成功/否
	 * @throws SQLException
	 */
	public boolean UpdateLike(int state, int userId, String blogId, long timestamp) throws SQLException {
		String sql = "UPDATE `t_like` " 
				+ "SET `state`=?,`update_timestamp`=? " 
				+ "WHERE `user_id`=? AND `blog_id`=?";

		if (DebugConfig.isDebug) {
			log.debug("更新点赞：{} - {},{},{},{}", sql, state, userId, blogId, timestamp);
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
				log.debug("更新数据：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * 添加点赞信息，数据库中不存在则添加，否则修改
	 * 
	 * @param state     点赞状态 状态：1点赞，-1反对，0取消
	 * @param userId    用户ID
	 * @param blogId    博文ID
	 * @param timestamp 当前时间
	 * @return 成功/否
	 * @throws SQLException
	 */
	public boolean setLike(int state, int userId, String blogId, long timestamp) throws SQLException {
		String sql = "SELECT * " 
				+ "FROM `t_like` " 
				+ "WHERE `user_id`=? AND `blog_id`=?" 
				+ "LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找点赞数据：{} - {}，{}", sql, userId, blogId);
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
				preState = resultSet.getInt("state"); // 之前的态度
				
				// 修改
				String updateSql = "UPDATE `t_like` " 
						+ "SET `state`=?,`update_timestamp`=? "
						+ "WHERE `user_id`=? AND `blog_id`=?";

				if (DebugConfig.isDebug) {
					log.debug("更新点赞：{} - {},{},{},{}", sql, state, userId, blogId, timestamp);
				}

				result = update(updateSql, 
						String.valueOf(state), 
						String.valueOf(timestamp), 
						String.valueOf(userId), 
						blogId
					);

				if (DebugConfig.isDebug) {
					log.debug("更新数据：{}", result);
				}
			} else {
				preState = 0; // 之前的态度
				// 写入
				String insertSql = "INSERT INTO `t_like` " 
						+ "(`state`, `user_id`, `blog_id`, `create_timestamp`) "
						+ "VALUES (?,?,?,?)";

				if (DebugConfig.isDebug) {
					log.debug("记录点赞：{} - {},{},{},{}", sql, state, userId, blogId, timestamp);
				}

				result = insert(insertSql, 
						String.valueOf(state), 
						String.valueOf(userId), 
						blogId, 
						String.valueOf(timestamp)
					);

				if (DebugConfig.isDebug) {
					log.debug("插入数据：{}", result);
				}
			}
			
			
			if(preState == 0 && state == 1) { // 之前无表态，现在点赞
				likeCount = 1;
			}else if(preState == -1 && state == 1) { // 之前反对，现在点赞
				likeCount = 1;
			}else if(preState == 1 && state == 1) { // 之前点赞，现在点赞
				likeCount = 0;
			}else
				
			
			if(preState == 0 && state == 0) { // 之前无表态，现在无表态
				likeCount = 0;
			}else if(preState == -1 && state == 0) { // 之前反对，现在无表态
				likeCount = 0;
			}else if(preState == 1 && state == 0) { // 之前点赞，现在无表态
				likeCount = -1;
			}else
			
				
			if(preState == 0 && state == -1) { // 之前无表态，现在反对
				likeCount = 0;
			}else if(preState == -1 && state == -1) { // 之前反对，现在反对
				likeCount = 0;
			}else if(preState == 1 && state == -1) { // 之前点赞，现在反对
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
					log.debug("增加博文喜欢量：{} - {}", likeCount, blogId);
				}
				
				result1 = update(updateBlogLikeCountSql, 
						String.valueOf(likeCount), 
						blogId
					);

				if (DebugConfig.isDebug) {
					log.debug("增加博文喜欢量结果：{}",result1);
				}
				
				// ------------------------------------------
				String selectAuthorIdByBlogIdSql = "SELECT `author_id` " + 
						"FROM `t_blog` " + 
						"WHERE `id` = ?";

				
				if (DebugConfig.isDebug) {
					log.debug("获取作者ID：{}", blogId);
				}
				
				resultSet = query(selectAuthorIdByBlogIdSql, 
						blogId
					);
				
				int authorId = 0;
				if (resultSet.next()) {
					authorId = resultSet.getInt("author_id"); // 获取作者ID
				}
				
				if (DebugConfig.isDebug) {
					log.debug("作者ID：{} ", authorId);
				}
				// ------------------------------------------
				if(authorId!=0) {
					// 给作者添加点赞量
					String updateUserBlogLikeCountSql = "UPDATE `t_user` "
							+ "SET `blog_like_count` = `blog_like_count` + ? "
							+ "WHERE `id` = ?";
					
					if (DebugConfig.isDebug) {
						log.debug("增加作者喜欢量：{} - {}", likeCount, authorId);
					}
					
					result2 = update(updateUserBlogLikeCountSql, 
							String.valueOf(likeCount), 
							String.valueOf(authorId)
						);
					
					if (DebugConfig.isDebug) {
						log.debug("增加作者喜欢量结果：{}",result2);
					}
				}
			}
			
			return result > 0;
		} finally {
			close();
		}
	}

	/**
	 * 点赞，写入数据库
	 * 
	 * @param likeEntity 点赞信息
	 * @return 影响行数
	 * @throws SQLException 数据库错误
	 */
	public boolean writeLike(LikeEntity likeEntity) throws SQLException {
		String sql = "INSERT INTO `t_like` " 
				+ "(`state`, `user_id`, `blog_id`, `create_timestamp`) "
				+ "VALUES (?,?,?,?)";

		if (DebugConfig.isDebug) {
			log.debug("记录点赞：{} - {}", sql, likeEntity.toString());
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
				log.debug("插入数据：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

}

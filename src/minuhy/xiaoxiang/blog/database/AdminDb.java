package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.entity.BlogEntity;
import minuhy.xiaoxiang.blog.entity.CommentEntity;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;

/**
 * 管理员操作数据库的相关动作
 * 
 * @author y17mm 创建时间:2023-02-28 23:57
 */
public class AdminDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(AdminDb.class);

	public UserEntity[] getUserInfoByPageOrderByTime(int pagination) throws SQLException {
		String sql = "SELECT * " + "FROM `t_user` " + "ORDER BY `create_timestamp` DESC " + "LIMIT "
				+ pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("获取用户列表：{} - {}", sql, pagination);
		}

		UserEntity[] userEntities = new UserEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql);
			for (int i = 0; i < userEntities.length; i++) {
				UserEntity userEntity = createUserEntity(resultSet);
				if (userEntity == null) {
					// 不够 blogEntitys.length 的长度，重新建个数组替换一下
					UserEntity[] userEntitiesTemp = new UserEntity[i];
					for (int j = 0; j < i; j++) {
						userEntitiesTemp[j] = userEntities[j];
					}
					userEntities = userEntitiesTemp;
					// 跳出
					break;
				} else {
					userEntities[i] = userEntity;
				}
			}
		} finally {
			close();
		}
		return userEntities;
	}

	private UserEntity createUserEntity(ResultSet resultSet) throws SQLException {
		return UserDb.createUserEntity(resultSet);
	}

	/**
	 * 查找本月用户新增数，出错返回-1
	 * 
	 * @return 本月用户新增数
	 */
	public int getNewUserMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_user` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("查找本月用户新增数：{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("查找本月用户新增数：{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * 查找本月博客新增数，出错返回-1
	 * 
	 * @return 本月博客新增数
	 */
	public int getNewBlogMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_blog` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("查找本月博客新增数：{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("查找本月博客新增数：{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * 查找本月评论新增数，出错返回-1
	 * 
	 * @return 本月评论新增数
	 */
	public int getNewCommentMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_comment` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("查找本月评论新增数：{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("查找本月评论新增数：{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * 查找本月点赞新增数，出错返回-1
	 * 
	 * @return 本月点赞新增数
	 */
	public int getNewLikeMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_like` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("查找本月点赞新增数：{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("查找本月点赞新增数：{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * 查询，获取 count
	 * 
	 * @param sql 需要执行的SQL
	 * @return 获得的count值
	 * @throws SQLException 数据库异常
	 */
	public int getCount(String sql) throws SQLException {
		try {
			ResultSet resultSet = query(sql);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} finally {
			close();
		}
		return 0;
	}

	public int getUserTotal() throws SQLException {
		String sql = "SELECT COUNT(`id`) AS count FROM `t_user`";
		return getCount(sql);
	}

	public int getBlogTotal() throws SQLException {
		String sql = "SELECT COUNT(`id`) AS count FROM `t_blog`";
		return getCount(sql);
	}

	public BlogEntity[] getBlogInfoByPageOrderByTime(int pagination) throws SQLException {
		String sql = "SELECT * " + "FROM `t_blog` " + "ORDER BY `create_timestamp` DESC " + "LIMIT "
				+ pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("获取博文列表：{} - {}", sql, pagination);
		}

		BlogEntity[] blogEntities = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql);
			for (int i = 0; i < blogEntities.length; i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if (blogEntity == null) {
					// 不够 blogEntities.length 的长度，重新建个数组替换一下
					BlogEntity[] blogEntitiesTemp = new BlogEntity[i];
					for (int j = 0; j < i; j++) {
						blogEntitiesTemp[j] = blogEntities[j];
					}
					blogEntities = blogEntitiesTemp;
					// 跳出
					break;
				} else {
					blogEntities[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return blogEntities;
	}

	private BlogEntity createBlogEntity(ResultSet resultSet) throws SQLException {
		return BlogDb.createBlogEntity(resultSet);
	}

	public int getCommentTotal() throws SQLException {
		String sql = "SELECT COUNT(`id`) AS count FROM `t_comment`";
		return getCount(sql);
	}

	public CommentEntity[] getCommentInfoByPageOrderByTime(int pagination) throws SQLException {
		String sql = "SELECT * " + "FROM `t_comment` " + "ORDER BY `create_timestamp` DESC " + "LIMIT "
				+ pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("获取评论列表：{} - {}", sql, pagination);
		}

		CommentEntity[] commentEntities = new CommentEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql);
			for (int i = 0; i < commentEntities.length; i++) {
				CommentEntity blogEntity = createCommentEntity(resultSet);
				if (blogEntity == null) {
					// 不够 blogEntities.length 的长度，重新建个数组替换一下
					CommentEntity[] commentEntitiesTemp = new CommentEntity[i];
					for (int j = 0; j < i; j++) {
						commentEntitiesTemp[j] = commentEntities[j];
					}
					commentEntities = commentEntitiesTemp;
					// 跳出
					break;
				} else {
					commentEntities[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return commentEntities;
	}

	private CommentEntity createCommentEntity(ResultSet resultSet) throws SQLException {
		return CommentDb.createCommentEntity(resultSet);
	}

	public int deleteItemByTypeAndIds(int type, int[] ids) throws SQLException {
		if (ids == null || ids.length < 1) {
			return 0;
		}

		String sql = "DELETE FROM `t_";
		if (type == 0) { // user
			sql += "user";
		} else if (type == 1) { // blog
			sql += "blog";
		} else if (type == 2) { // comment
			sql += "comment";
			// DELETE FROM `t_comment` WHERE `id` = 115 OR `id`=113
		} else {
			return 0;
		}

		sql += "` WHERE ";

		for (int i = 0; i < ids.length; i++) {
			sql += " `id` = " + ids[i];
			if (i != (ids.length - 1)) {
				sql += " OR ";
			}
		}

		if (DebugConfig.isDebug) {
			log.debug("删除数据：{}", sql);
		}

		try {
			return update(sql);
		} finally {
			close();
		}

	}

	public int activeItemByTypeAndIds(boolean isActive, int type, int[] ids) throws SQLException {
		if (ids == null || ids.length < 1) {
			return 0;
		}
		// UPDATE `t_comment` SET `active` = 0 WHERE `id` = 111 OR `id` = 110
		String sql = "UPDATE `t_";
		if (type == 0) { // user
			sql += "user";
		} else if (type == 1) { // blog
			sql += "blog";
		} else if (type == 2) { // comment
			sql += "comment";
		} else {
			return 0;
		}

		sql += "` SET `active` = ";
		if (isActive) {
			sql += " 1 ";
		} else {
			sql += " 0 ";
		}
		sql += " WHERE ";

		for (int i = 0; i < ids.length; i++) {
			sql += " `id` = " + ids[i];
			if (i != (ids.length - 1)) {
				sql += " OR ";
			}
		}

		if (DebugConfig.isDebug) {
			log.debug("冻结/解冻 数据：{}", sql);
		}

		try {
			return update(sql);
		} finally {
			close();
		}

	}

	/**
	 * 更改用户资料
	 *
	 * @param entity 用户资料
	 * @throws SQLException SQL异常
	 */
	public boolean editUser(UserEntity entity) throws SQLException {
		String sql = "UPDATE `t_user` " + "SET `active` = ?, `role` = ?, `nick` = ?, "
				+ "`signature` = ?, `sex` = ?, `hometown` = ?, " + "`link` = ?, `avatar` = ?, `blog_count` = ?, "
				+ "`blog_read_count` = ?, `blog_like_count` = ? " + "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("更改资料：{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, String.valueOf(entity.getActive()), String.valueOf(entity.getRole()), entity.getNick(),
					entity.getSignature(), String.valueOf(entity.getSex()), entity.getHometown(), entity.getLink(),
					String.valueOf(entity.getAvatar()), String.valueOf(entity.getBlogCount()),
					String.valueOf(entity.getBlogReadCount()), String.valueOf(entity.getBlogLikeCount()),
					String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("更新资料：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * 更改密码
	 *
	 * @param entity 用户资料，id，密码必须有
	 * @return 是否修改
	 * @throws SQLException SQL异常
	 */
	public boolean editUserPasswd(UserEntity entity) throws SQLException {
		String sql = "UPDATE `t_user` " + "SET `passwd` = ? " + "WHERE `id`= ?";

		if (DebugConfig.isDebug) {
			log.debug("更改密码：{} - {}，{}", sql, entity.getId(), entity.getPasswd());
		}

		int result = 0;
		try {
			result = update(sql, entity.getPasswd(), String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("更新密码：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	public boolean editBlog(BlogEntity entity) throws SQLException {
		String sql = "UPDATE `t_blog` " + "SET `active` = ?, `author_id` = ?, `title` = ?, "
				+ "`read_count` = ?, `like_count` = ?, " + "`comment_count` = ?, `create_timestamp` = ?, "
				+ "`update_timestamp` = ? " + "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("更改资料：{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, String.valueOf(entity.getActive()), String.valueOf(entity.getAuthorId()),
					entity.getTitle(), String.valueOf(entity.getReadCount()), String.valueOf(entity.getLikeCount()),
					String.valueOf(entity.getCommentCount()), String.valueOf(entity.getCreateTimestamp()),
					String.valueOf(entity.getUpdateTimestamp()), String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("更新资料：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	public boolean editComment(CommentEntity entity) throws SQLException {
		String sql = "UPDATE `t_comment` " + "SET `active` = ?, `blog_id` = ?, `user_id` = ?, "
				+ "`reply_id` = ?, `content` = ?, " + "`create_timestamp` = ?, `update_timestamp` = ? "
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("更改资料：{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, String.valueOf(entity.getActive()), String.valueOf(entity.getBlogId()),
					String.valueOf(entity.getUserId()), String.valueOf(entity.getReplyId()), entity.getContent(),
					String.valueOf(entity.getCreateTimestamp()), String.valueOf(entity.getUpdateTimestamp()),
					String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("更新资料：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	public int getBlogSearchTotal(String keyword) throws SQLException {
		String sql = "SELECT COUNT(*) AS count " + "FROM `t_blog` " + "WHERE " + "`title` LIKE ? OR "
				+ "`content` LIKE ? OR " + "CONVERT(`author_id`,CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`create_timestamp`/1000),CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`update_timestamp`/1000),CHAR) LIKE ? ";

		keyword = "%" + keyword + "%";

		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} finally {
			close();
		}
		return 0;
	}

	public BlogEntity[] searchBlogInfoByPageOrderByTime(int pagination, String keyword) throws SQLException {
		String sql = "SELECT * " + "FROM `t_blog` " + "WHERE " + "`title` LIKE ? OR " + "`content` LIKE ? OR "
				+ "CONVERT(`author_id`,CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`create_timestamp`/1000),CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`update_timestamp`/1000),CHAR) LIKE ? " + "ORDER BY `create_timestamp` DESC "
				+ "LIMIT " + pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		keyword = "%" + keyword + "%";

		if (DebugConfig.isDebug) {
			log.debug("获取搜索博文列表：{} - {}，{}", sql, pagination, keyword);
		}

		BlogEntity[] blogEntities = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword);
			for (int i = 0; i < blogEntities.length; i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if (blogEntity == null) {
					// 不够 blogEntities.length 的长度，重新建个数组替换一下
					BlogEntity[] blogEntitiesTemp = new BlogEntity[i];
					for (int j = 0; j < i; j++) {
						blogEntitiesTemp[j] = blogEntities[j];
					}
					blogEntities = blogEntitiesTemp;
					// 跳出
					break;
				} else {
					blogEntities[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return blogEntities;
	}

	public int getUserSearchTotal(String keyword) throws SQLException {
		String sql = "SELECT COUNT(*) AS count " + "FROM `t_user` " + "WHERE " + "`account` LIKE ? OR "
				+ "`nick` LIKE ? OR " + "CONVERT(FROM_UNIXTIME(`create_timestamp`/1000),CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`last_login_timestamp`/1000),CHAR) LIKE ? ";

		keyword = "%" + keyword + "%";

		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} finally {
			close();
		}
		return 0;
	}

	public UserEntity[] searchUserInfoByPageOrderByTime(int pagination, String keyword) throws SQLException {
		String sql = "SELECT * " + "FROM `t_user` " + "WHERE " + "`account` LIKE ? OR " + "`nick` LIKE ? OR "
				+ "`last_login_ip` LIKE ? OR " + "CONVERT(FROM_UNIXTIME(`create_timestamp`/1000),CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`last_login_timestamp`/1000),CHAR) LIKE ? "
				+ "ORDER BY `create_timestamp` DESC " + "LIMIT " + pagination * DatabaseConfig.PAGE_ITEM_COUNT + ","
				+ DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		keyword = "%" + keyword + "%";

		if (DebugConfig.isDebug) {
			log.debug("获取搜索用户列表：{} - {}，{}", sql, pagination, keyword);
		}

		UserEntity[] entities = new UserEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword);
			for (int i = 0; i < entities.length; i++) {
				UserEntity entity = createUserEntity(resultSet);
				if (entity == null) {
					// 不够 blogEntities.length 的长度，重新建个数组替换一下
					UserEntity[] entitiesTemp = new UserEntity[i];
					for (int j = 0; j < i; j++) {
						entitiesTemp[j] = entities[j];
					}
					entities = entitiesTemp;
					// 跳出
					break;
				} else {
					entities[i] = entity;
				}
			}
		} finally {
			close();
		}
		return entities;
	}

	public int getCommentSearchTotal(String keyword) throws SQLException {
		String sql = "SELECT COUNT(*) AS count " + "FROM `t_comment` " + "WHERE "
				+ "CONVERT(`reply_id`,CHAR) LIKE ? OR " + "CONVERT(`user_id`,CHAR) LIKE ? OR "
				+ "CONVERT(`blog_id`,CHAR) LIKE ? OR " + "`content` LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`create_timestamp`/1000),CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`update_timestamp`/1000),CHAR) LIKE ? ";

		keyword = "%" + keyword + "%";

		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword, keyword);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} finally {
			close();
		}
		return 0;
	}

	public CommentEntity[] searchCommentInfoByPageOrderByTime(int pagination, String keyword) throws SQLException {
		String sql = "SELECT * " + "FROM `t_comment` " + "WHERE " + "CONVERT(`reply_id`,CHAR) LIKE ? OR "
				+ "CONVERT(`user_id`,CHAR) LIKE ? OR " + "CONVERT(`blog_id`,CHAR) LIKE ? OR " + "`content` LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`create_timestamp`/1000),CHAR) LIKE ? OR "
				+ "CONVERT(FROM_UNIXTIME(`update_timestamp`/1000),CHAR) LIKE ? " + "ORDER BY `create_timestamp` DESC "
				+ "LIMIT " + pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		keyword = "%" + keyword + "%";

		if (DebugConfig.isDebug) {
			log.debug("获取搜索评论列表：{} - {}，{}", sql, pagination, keyword);
		}

		CommentEntity[] entities = new CommentEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword, keyword);
			for (int i = 0; i < entities.length; i++) {
				CommentEntity entity = createCommentEntity(resultSet);
				if (entity == null) {
					// 不够 blogEntities.length 的长度，重新建个数组替换一下
					CommentEntity[] entitiesTemp = new CommentEntity[i];
					for (int j = 0; j < i; j++) {
						entitiesTemp[j] = entities[j];
					}
					entities = entitiesTemp;
					// 跳出
					break;
				} else {
					entities[i] = entity;
				}
			}
		} finally {
			close();
		}
		return entities;
	}

	public ArrayList<Integer> getUserIdsBySql(String sql, int page, int pageSize) throws SQLException {
		if (DebugConfig.isDebug) {
			log.debug("获取ID列表：{} - {}", sql, page);
		}

		sql += " LIMIT " + (pageSize * page) + "," + pageSize;

		ArrayList<Integer> ids = new ArrayList<>();
		try {
			ResultSet resultSet = query(sql);
			while (true) {
				if (resultSet.next()) {
					int id = resultSet.getInt("id"); // 主键，唯一标识
					ids.add(id);
				} else {
					break;
				}
			}
		} finally {
			close();
		}
		return ids;
	}

}

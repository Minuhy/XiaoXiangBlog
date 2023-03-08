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
 * ����Ա�������ݿ����ض���
 * 
 * @author y17mm ����ʱ��:2023-02-28 23:57
 */
public class AdminDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(AdminDb.class);

	public UserEntity[] getUserInfoByPageOrderByTime(int pagination) throws SQLException {
		String sql = "SELECT * " + "FROM `t_user` " + "ORDER BY `create_timestamp` DESC " + "LIMIT "
				+ pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("��ȡ�û��б�{} - {}", sql, pagination);
		}

		UserEntity[] userEntities = new UserEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql);
			for (int i = 0; i < userEntities.length; i++) {
				UserEntity userEntity = createUserEntity(resultSet);
				if (userEntity == null) {
					// ���� blogEntitys.length �ĳ��ȣ����½��������滻һ��
					UserEntity[] userEntitiesTemp = new UserEntity[i];
					for (int j = 0; j < i; j++) {
						userEntitiesTemp[j] = userEntities[j];
					}
					userEntities = userEntitiesTemp;
					// ����
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
	 * ���ұ����û���������������-1
	 * 
	 * @return �����û�������
	 */
	public int getNewUserMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_user` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("���ұ����û���������{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("���ұ����û���������{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * ���ұ��²�����������������-1
	 * 
	 * @return ���²���������
	 */
	public int getNewBlogMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_blog` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("���ұ��²�����������{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("���ұ��²�����������{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * ���ұ���������������������-1
	 * 
	 * @return ��������������
	 */
	public int getNewCommentMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_comment` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("���ұ���������������{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("���ұ���������������{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * ���ұ��µ�����������������-1
	 * 
	 * @return ���µ���������
	 */
	public int getNewLikeMonthCount() {
		String sql = "SELECT COUNT(`id`) AS count " + "FROM `t_like` "
				+ "WHERE DATE_FORMAT(FROM_UNIXTIME(`create_timestamp`/1000),'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m');";

		if (DebugConfig.isDebug) {
			log.debug("���ұ��µ�����������{} ", sql);
		}
		int count = 0;
		try {
			count = getCount(sql);
		} catch (SQLException e) {
			if (DebugConfig.isDebug) {
				log.error("���ұ��µ�����������{0}", e);
			}
			return -1;
		}
		return count;
	}

	/**
	 * ��ѯ����ȡ count
	 * 
	 * @param sql ��Ҫִ�е�SQL
	 * @return ��õ�countֵ
	 * @throws SQLException ���ݿ��쳣
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
				+ pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("��ȡ�����б�{} - {}", sql, pagination);
		}

		BlogEntity[] blogEntities = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql);
			for (int i = 0; i < blogEntities.length; i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if (blogEntity == null) {
					// ���� blogEntities.length �ĳ��ȣ����½��������滻һ��
					BlogEntity[] blogEntitiesTemp = new BlogEntity[i];
					for (int j = 0; j < i; j++) {
						blogEntitiesTemp[j] = blogEntities[j];
					}
					blogEntities = blogEntitiesTemp;
					// ����
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
				+ pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("��ȡ�����б�{} - {}", sql, pagination);
		}

		CommentEntity[] commentEntities = new CommentEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql);
			for (int i = 0; i < commentEntities.length; i++) {
				CommentEntity blogEntity = createCommentEntity(resultSet);
				if (blogEntity == null) {
					// ���� blogEntities.length �ĳ��ȣ����½��������滻һ��
					CommentEntity[] commentEntitiesTemp = new CommentEntity[i];
					for (int j = 0; j < i; j++) {
						commentEntitiesTemp[j] = commentEntities[j];
					}
					commentEntities = commentEntitiesTemp;
					// ����
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
			log.debug("ɾ�����ݣ�{}", sql);
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
			log.debug("����/�ⶳ ���ݣ�{}", sql);
		}

		try {
			return update(sql);
		} finally {
			close();
		}

	}

	/**
	 * �����û�����
	 *
	 * @param entity �û�����
	 * @throws SQLException SQL�쳣
	 */
	public boolean editUser(UserEntity entity) throws SQLException {
		String sql = "UPDATE `t_user` " + "SET `active` = ?, `role` = ?, `nick` = ?, "
				+ "`signature` = ?, `sex` = ?, `hometown` = ?, " + "`link` = ?, `avatar` = ?, `blog_count` = ?, "
				+ "`blog_read_count` = ?, `blog_like_count` = ? " + "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("�������ϣ�{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, String.valueOf(entity.getActive()), String.valueOf(entity.getRole()), entity.getNick(),
					entity.getSignature(), String.valueOf(entity.getSex()), entity.getHometown(), entity.getLink(),
					String.valueOf(entity.getAvatar()), String.valueOf(entity.getBlogCount()),
					String.valueOf(entity.getBlogReadCount()), String.valueOf(entity.getBlogLikeCount()),
					String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("�������ϣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * ��������
	 *
	 * @param entity �û����ϣ�id�����������
	 * @return �Ƿ��޸�
	 * @throws SQLException SQL�쳣
	 */
	public boolean editUserPasswd(UserEntity entity) throws SQLException {
		String sql = "UPDATE `t_user` " + "SET `passwd` = ? " + "WHERE `id`= ?";

		if (DebugConfig.isDebug) {
			log.debug("�������룺{} - {}��{}", sql, entity.getId(), entity.getPasswd());
		}

		int result = 0;
		try {
			result = update(sql, entity.getPasswd(), String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("�������룺{}", result);
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
			log.debug("�������ϣ�{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, String.valueOf(entity.getActive()), String.valueOf(entity.getAuthorId()),
					entity.getTitle(), String.valueOf(entity.getReadCount()), String.valueOf(entity.getLikeCount()),
					String.valueOf(entity.getCommentCount()), String.valueOf(entity.getCreateTimestamp()),
					String.valueOf(entity.getUpdateTimestamp()), String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("�������ϣ�{}", result);
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
			log.debug("�������ϣ�{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, String.valueOf(entity.getActive()), String.valueOf(entity.getBlogId()),
					String.valueOf(entity.getUserId()), String.valueOf(entity.getReplyId()), entity.getContent(),
					String.valueOf(entity.getCreateTimestamp()), String.valueOf(entity.getUpdateTimestamp()),
					String.valueOf(entity.getId()));

			if (DebugConfig.isDebug) {
				log.debug("�������ϣ�{}", result);
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
				+ "LIMIT " + pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		keyword = "%" + keyword + "%";

		if (DebugConfig.isDebug) {
			log.debug("��ȡ���������б�{} - {}��{}", sql, pagination, keyword);
		}

		BlogEntity[] blogEntities = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword);
			for (int i = 0; i < blogEntities.length; i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if (blogEntity == null) {
					// ���� blogEntities.length �ĳ��ȣ����½��������滻һ��
					BlogEntity[] blogEntitiesTemp = new BlogEntity[i];
					for (int j = 0; j < i; j++) {
						blogEntitiesTemp[j] = blogEntities[j];
					}
					blogEntities = blogEntitiesTemp;
					// ����
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
				+ DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		keyword = "%" + keyword + "%";

		if (DebugConfig.isDebug) {
			log.debug("��ȡ�����û��б�{} - {}��{}", sql, pagination, keyword);
		}

		UserEntity[] entities = new UserEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword);
			for (int i = 0; i < entities.length; i++) {
				UserEntity entity = createUserEntity(resultSet);
				if (entity == null) {
					// ���� blogEntities.length �ĳ��ȣ����½��������滻һ��
					UserEntity[] entitiesTemp = new UserEntity[i];
					for (int j = 0; j < i; j++) {
						entitiesTemp[j] = entities[j];
					}
					entities = entitiesTemp;
					// ����
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
				+ "LIMIT " + pagination * DatabaseConfig.PAGE_ITEM_COUNT + "," + DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		keyword = "%" + keyword + "%";

		if (DebugConfig.isDebug) {
			log.debug("��ȡ���������б�{} - {}��{}", sql, pagination, keyword);
		}

		CommentEntity[] entities = new CommentEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql, keyword, keyword, keyword, keyword, keyword, keyword);
			for (int i = 0; i < entities.length; i++) {
				CommentEntity entity = createCommentEntity(resultSet);
				if (entity == null) {
					// ���� blogEntities.length �ĳ��ȣ����½��������滻һ��
					CommentEntity[] entitiesTemp = new CommentEntity[i];
					for (int j = 0; j < i; j++) {
						entitiesTemp[j] = entities[j];
					}
					entities = entitiesTemp;
					// ����
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
			log.debug("��ȡID�б�{} - {}", sql, page);
		}

		sql += " LIMIT " + (pageSize * page) + "," + pageSize;

		ArrayList<Integer> ids = new ArrayList<>();
		try {
			ResultSet resultSet = query(sql);
			while (true) {
				if (resultSet.next()) {
					int id = resultSet.getInt("id"); // ������Ψһ��ʶ
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

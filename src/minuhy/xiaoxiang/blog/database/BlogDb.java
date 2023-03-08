package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.blog.MiniBlogBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;
import minuhy.xiaoxiang.blog.entity.BlogEntity;

public class BlogDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(BlogDb.class);
	
	/**
	 * ��ȡĳ���û��Ĳ�������
	 * @param userId �û�ID
	 * @return ��Ϣ����
	 */
	public int getBlogTotalByUserId(int userId) {
		String sql = "SELECT COUNT(`id`) as `count` " + 
				"FROM `t_blog` " + 
				"WHERE `author_id` = ?  and `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("���Ҳ���������{} - {}", sql, userId);
		}

		int blogTotal = 0;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId),
					"1"
				);
			if (resultSet.next()) {
				blogTotal = resultSet.getInt("count");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("���Ҳ����������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return blogTotal;
	}

	/**
	 * �������ģ�д�����ݿ�
	 * 
	 * @param blogEntity ������Ϣ
	 * @return Ӱ������
	 * @throws SQLException ���ݿ����
	 */
	public boolean writeBlog(BlogEntity blogEntity) throws SQLException {
		String sql = "INSERT INTO `t_blog`" 
				+ "(`author_id`, `title`, `content`, `create_timestamp`) "
				+ "VALUES (?, ?, ?, ?)";

		if (DebugConfig.isDebug) {
			log.debug("�������ģ�{} - {}", sql, blogEntity.toString());
		}

		int result1 = 0;
		int result2 = 0;
		try {
			result1 = insert(sql, 
					String.valueOf(blogEntity.getAuthorId()), 
					blogEntity.getTitle(),
					blogEntity.getContent(), 
					String.valueOf(blogEntity.getCreateTimestamp())
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result1);
			}
			
			// �������߲��ļ���
			sql = "UPDATE `t_user` "
					+ "SET `blog_count` = `blog_count` + 1 "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(blogEntity.getAuthorId())
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
	 * ͨ�^ID���Ҳ���
	 * 
	 * @param blogId ���ı��
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public BlogEntity getBlogById(String blogId,boolean isActive) throws SQLException {
		String sql = "select * " 
				+ "from `t_blog` " 
				+ "where `id`=?  and `active`=? " 
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("���Ҳ��ģ�{} - {}��{}", sql, blogId,isActive);
		}

		BlogEntity blogEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					blogId, 
					isActive?"1":"0"
				);
			
			blogEntity = createBlogEntity(resultSet);
		} finally {
			close();
		}
		return blogEntity;
	}
	
	/**
	 * ͨ�^ID���Ҳ���
	 * 
	 * @param blogId ���ı��
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public BlogEntity getBlogById(String blogId) throws SQLException {
		return getBlogById(blogId,true);
	}

	/**
	 * ����һ���û����µĲ���ID
	 * 
	 * @param userId �û�ID
	 * @return ����鵽������ID����t����null
	 * @throws SQLException ���ݿ����
	 */
	public String getNewestBlogIdByUserId(String userId) throws SQLException {
		String sql = "SELECT `id` " 
				+ "FROM `t_blog` " 
				+ "where `author_id`=?  and `active`=? "
				+ "ORDER BY `create_timestamp` DESC " 
				+ "LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("�����û������²���ID��{} - {}", sql, userId);
		}

		String blogId = null;
		try {
			ResultSet resultSet = query(sql, 
					userId, 
					String.valueOf(1)
				);
			
			if (resultSet.next()) {
				blogId = String.valueOf(resultSet.getInt("id")); // ������Ψһ��ʶ
			} else {
				if (DebugConfig.isDebug) {
					log.debug("û�в鵽����");
				}
			}
		} finally {
			close();
		}
		return blogId;
	}

	/**
	 * ͨ�����������һ�� BlogEntity
	 * 
	 * @param resultSet �����
	 * @return BlogEntity
	 * @throws SQLException
	 */
	public static BlogEntity createBlogEntity(ResultSet resultSet) throws SQLException {
		BlogEntity blogEntity = null;
		if (resultSet.next()) {

			int id = resultSet.getInt("id"); // ������Ψһ��ʶ
			int active = resultSet.getInt("active"); // �Ƿ���Ч��1��Ч��0��Ч
			int authorId = resultSet.getInt("author_id"); // �����������ID
			String title = resultSet.getString("title"); // ���ͱ���
			String content = resultSet.getString("content"); // ��������
			int readCount = resultSet.getInt("read_count"); // ������
			int likeCount = resultSet.getInt("like_count"); // ������
			int commentCount = resultSet.getInt("comment_count"); // ������
			int likeMsgSendCount = resultSet.getInt("like_msg_send_count"); // ������
			long createTimestamp = resultSet.getLong("create_timestamp"); // ����ʱ��
			long updateTimestamp = resultSet.getLong("update_timestamp"); // �޸�ʱ��

			blogEntity = new BlogEntity(id, 
					active, 
					authorId, 
					title, 
					content, 
					readCount, 
					likeCount, 
					commentCount,
					likeMsgSendCount,
					createTimestamp, 
					updateTimestamp
				);

			if (DebugConfig.isDebug) {
				log.debug("�鵽���ݣ�{}", blogEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("û�в鵽����");
			}
		}
		return blogEntity;
	}

	/**
	 * ���²���
	 * 
	 * @param id        ����ID
	 * @param title     ���ı���
	 * @param content   ��������
	 * @param timestamp ʱ���
	 * @return �ɹ�/��
	 * @throws SQLException
	 */
	public boolean UpdateBlog(String id, String title, String content, long timestamp) throws SQLException {
		String sql = "UPDATE `t_blog` " 
				+ "SET `title` = ?, `content` = ?, `update_timestamp` = ? " 
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("���²��ģ�{} - {},{},{},{}", sql, id, title, content, timestamp);
		}

		int result = 0;
		try {
			result = update(sql, 
					title, 
					content, 
					String.valueOf(timestamp), 
					id);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * ɾ������
	 * 
	 * @param id ����ID
	 * @return �ɹ�/��
	 * @throws SQLException
	 */
	public boolean deleteBlog(String id,int UserId) throws SQLException {
		String sql = "UPDATE `t_blog` "
				+ "SET `active` = ? "
				+ "WHERE `id` = ?  and `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("ɾ�����ģ�{} - {}", sql, id);
		}

		int result1 = 0;
		int result2 = 0;
		try {
			result1 = update(sql, 
					String.valueOf(0), 
					id,
					"1"
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result1);
			}
			
			// �������߲��ļ���
			sql = "UPDATE `t_user` "
					+ "SET `blog_count` = IF(`blog_count` - '1'>'0',`blog_count` - '1','0') "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(UserId)
					);
			
			if(result2<1) {
				log.warn("�û����ͼ�������{}",UserId);
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
	 * ���ӷ�����
	 * 
	 * @param userId �û�
	 * @param blogId ����
	 * @return �ɹ����
	 * @throws SQLException
	 */
	public boolean increaseReadCount(String authorId, String blogId) throws SQLException {
		int result1 = 0;
		int result2 = 0;
		try {
			// ------------------------------------------------------------
			String sql1 = "UPDATE `t_blog` " 
					+ "SET `read_count` = `read_count` + 1 " 
					+ "WHERE `id` = ?";

			if (DebugConfig.isDebug) {
				log.debug("���ӷ�������{} - {}", sql1, blogId);
			}

			result1 = update(sql1, 
					blogId
				);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result1);
			}

			// ------------------------------------------------------------
			String sql2 = "UPDATE `t_user` " 
					+ "SET `blog_read_count` = `blog_read_count` + 1 " 
					+ "WHERE `id` = ?";

			if (DebugConfig.isDebug) {
				log.debug("���ӷ�������{} - {}", sql2, authorId);
			}

			result2 = update(sql2, 
					authorId
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
	 * �����ȡ����
	 * 
	 * @param len ��ȡ������
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public BlogEntity[] getRandomBlog(int len) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_blog` " + 
				"WHERE `active`=? "+
				"ORDER BY RAND() " + 
				"LIMIT "+len; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("�����ȡ���ģ�{} - {}", sql, len);
		}

		BlogEntity[] blogEntitys = new BlogEntity[len];
		try {
			ResultSet resultSet = query(sql,
					"1"
					);
			for(int i=0;i<len;i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if(blogEntity == null) {
					// ���� len �ĳ��ȣ����½��������滻һ��
					BlogEntity[] blogEntitysTemp = new BlogEntity[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// ����
					break;
				}else {
					blogEntitys[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return blogEntitys;
	}
	

	/**
	 * ��ʱ��˳���ȡ�û�����
	 * @param userId
	 * @param pageNumber ҳ������0��ʼ
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException
	 */
	public BlogEntity[] getUserBlogsByPageOrderByTime(int userId,int pagination) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_blog` " + 
				"WHERE `author_id` = ? AND `active`=? " + 
				"ORDER BY `create_timestamp` DESC " + 
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("��ȡ�û��Ĳ��ģ�{} - {}", sql, userId);
		}

		BlogEntity[] blogEntitys = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql,
					String.valueOf(userId),
					"1"
					);
			for(int i=0;i<blogEntitys.length;i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if(blogEntity == null) {
					// ���� blogEntitys.length �ĳ��ȣ����½��������滻һ��
					BlogEntity[] blogEntitysTemp = new BlogEntity[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// ����
					break;
				}else {
					blogEntitys[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return blogEntitys;
	}
	
	/**
	 * ��ѯһ�����µĵ�������
	 * @param blogId
	 * @return һ���������飬��һ��Ϊϲ���������ڶ���Ϊϲ����Ϣ���ͼ���
	 */
	public int[] getBlogLikeCountByBlogId(String blogId) {
		String sql = "SELECT `like_count`,`like_msg_send_count` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��ѯһ�����µĵ���������{} - {}", sql, blogId);
		}

		int[] likeCount = new int[2];
		try {
			ResultSet resultSet = query(sql, 
					blogId
				);
			if (resultSet.next()) {
				likeCount[0] = resultSet.getInt("like_count");
				likeCount[1] = resultSet.getInt("like_msg_send_count");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("��ѯһ�����µĵ����������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return likeCount;
	}
	
	/**
	 * ��ѯһ�����µ�����
	 * @param blogId
	 * @return 
	 */
	public int getBlogAuthorIdByBlogId(String blogId) {
		String sql = "SELECT `author_id` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��ѯ���µ����ߣ�{} - {}", sql, blogId);
		}

		int authorId = 0;
		try {
			ResultSet resultSet = query(sql, 
					blogId
				);
			if (resultSet.next()) {
				authorId = resultSet.getInt("author_id");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("��ѯһ�����µ��������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return authorId;
	}

	/**
	 * ��ѯһ�����µı���
	 * @param blogId
	 * @return
	 */
	public String getBlogTitleByBlogId(String blogId) {
		String sql = "SELECT `title` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��ѯһ�����µı��⣺{} - {}", sql, blogId);
		}

		String blogTitle = "";
		try {
			ResultSet resultSet = query(sql, 
					blogId
				);
			if (resultSet.next()) {
				blogTitle = resultSet.getString("title");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("��ѯһ�����µı������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return blogTitle;
	}
	
	/**
	 * ���õ�����Ϣ���ͼ���
	 * @param blogId
	 * @param likeMsgSendCount
	 * @return
	 * @throws SQLException
	 */
	public boolean setLikeMsgSendCount(String blogId, int likeMsgSendCount) throws SQLException {
		String sql = "UPDATE `t_blog` "
				+ "SET `like_msg_send_count` = ? "
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("���µ�����Ϣ���ͼ�����{} - {},{}", sql, blogId, likeMsgSendCount);
		}

		int result = 0;
		try {
			result = update(sql, 
					String.valueOf(likeMsgSendCount), 
					String.valueOf(blogId)
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
	 * �޸Ĳ���
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public boolean editBlog(BlogEntity entity) throws SQLException {
		String sql = "UPDATE `t_blog` "
				+ "SET `title` = ?, `content` = ?, `update_timestamp` = ? "
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("�޸Ĳ��ģ�{} - {}", sql, entity.toString());
		}

		int result = 0;
		try {
			result = update(sql, 
					entity.getTitle(),
					entity.getContent(),
					String.valueOf(entity.getUpdateTimestamp()),
					String.valueOf(entity.getId())
					);

			if (DebugConfig.isDebug) {
				log.debug("�������ݣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0 ;
	}
	
	/**
	 * �����ޡ��������ʱ��˳���ȡ�û�����
	 * @param pageNumber ҳ������0��ʼ
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException
	 */
	public BlogEntity[] getBlogsByPageOrderBySql(int pagination,String sqlOrder) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_blog` " + 
				"WHERE `active`= ? " + 
				sqlOrder + " "+
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("��ȡ�������ģ�{} - {}", sql);
		}

		BlogEntity[] blogEntitys = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql,
					"1"
					);
			for(int i=0;i<blogEntitys.length;i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if(blogEntity == null) {
					// ���� blogEntitys.length �ĳ��ȣ����½��������滻һ��
					BlogEntity[] blogEntitysTemp = new BlogEntity[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// ����
					break;
				}else {
					blogEntitys[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return blogEntitys;
	}

	
	/**
	 * ��ȡ��������
	 * @return ��������
	 */
	public int getBlogTotal() {
		String sql = "SELECT COUNT(`id`) as `count` " + 
				"FROM `t_blog` " + 
				"WHERE `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("�������в���������{} - {}", sql);
		}

		int blogTotal = 0;
		try {
			ResultSet resultSet = query(sql, 
					"1"
				);
			if (resultSet.next()) {
				blogTotal = resultSet.getInt("count");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("�������в����������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return blogTotal;
	}
	
	
	/**
	 * ���ؼ�����������
	 * @param keyword �ؼ���
	 * @param pagination ҳ��
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException
	 */
	public MiniBlogBean[] searchBlogByKeywordAndPagination(String keyword,int pagination) throws SQLException {
		String sql = "SELECT " + 
				"`t_blog`.`id` AS `id`, " + 
				"`t_user`.`nick` AS `author`, " + 
				"`t_blog`.`title` AS `title`, " + 
				"`t_blog`.`content` AS `content`, " + 
				"`t_blog`.`create_timestamp` AS `create_timestamp`, " + 
				"`t_blog`.`read_count` AS `read_count`, " + 
				"`t_blog`.`like_count` AS `like_count`, " + 
				"`t_blog`.`comment_count` AS `comment_count` " + 
				"FROM `t_blog` JOIN `t_user` " + 
				"ON `t_blog`.`author_id` = `t_user`.`id` " + 
				"WHERE `t_blog`.`active`=? AND (`t_blog`.`title` LIKE ? OR `t_blog`.`content` LIKE ? OR `t_user`.`nick` LIKE ?) " + 
				"ORDER BY `t_blog`.`like_count` DESC,`t_blog`.`read_count` DESC,`t_blog`.`create_timestamp` DESC " + 
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // �������ݣ�ֻ��ƴ��

		if (DebugConfig.isDebug) {
			log.debug("�������ģ�{} - {}��{}", sql, keyword,pagination);
		}

		MiniBlogBean[] blogEntitys = new MiniBlogBean[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql,
					"1",
					"%"+keyword+"%",
					"%"+keyword+"%",
					"%"+keyword+"%"
					);
			for(int i=0;i<blogEntitys.length;i++) {
				MiniBlogBean blogEntity = createMiniBlogEntity(resultSet);
				if(blogEntity == null) {
					// ���� blogEntitys.length �ĳ��ȣ����½��������滻һ��
					MiniBlogBean[] blogEntitysTemp = new MiniBlogBean[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// ����
					break;
				}else {
					blogEntitys[i] = blogEntity;
				}
			}
		} finally {
			close();
		}
		return blogEntitys;
	}
	
	/**
	 * ͨ�����������һ�� MiniBlogEntity
	 * 
	 * @param resultSet �����
	 * @return BlogEntity
	 * @throws SQLException
	 */
	public MiniBlogBean createMiniBlogEntity(ResultSet resultSet) throws SQLException {
		MiniBlogBean blogBean = null;
		if (resultSet.next()) {
//			"`t_blog`.`id` AS `id`, " + 
//			"`t_user`.`nick` AS `author`, " + 
//			"`t_blog`.`title` AS `title`, " + 
//			"`t_blog`.`content` AS `content`, " + 
//			"`t_blog`.`create_timestamp` AS `create_timestamp`, " + 
//			"`t_blog`.`read_count` AS `read_count`, " + 
//			"`t_blog`.`like_count` AS `like_count`, " + 
//			"`t_blog`.`comment_count` AS `comment_count` " + 
			int id = resultSet.getInt("id"); // ������Ψһ��ʶ
			String author = resultSet.getString("author"); // ���������ǳ�
			String title = resultSet.getString("title"); // ���ͱ���
			String content = resultSet.getString("content"); // ��������
			int readCount = resultSet.getInt("read_count"); // ������
			int likeCount = resultSet.getInt("like_count"); // ������
			int commentCount = resultSet.getInt("comment_count"); // ������
			long createTimestamp = resultSet.getLong("create_timestamp"); // ����ʱ��

			blogBean = new MiniBlogBean().build(
					id,
					author,
					title,
					content,
					readCount,
					likeCount,
					commentCount,
					createTimestamp
					);

			if (DebugConfig.isDebug) {
				log.debug("�鵽���ݣ�{}", blogBean);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("û�в鵽����");
			}
		}
		return blogBean;
	}
	
	/**
	 * ��ȡĳ���ؼ��ʵĲ�������
	 * @param keyword �ؼ���
	 * @return ��Ϣ����
	 */
	public int getBlogTotalBySearchKeyword(String keyword) {
		String sql = "SELECT COUNT(*) AS `count` " + 
				"FROM `t_blog` JOIN `t_user` " + 
				"ON `t_blog`.`author_id` = `t_user`.`id` " + 
				"WHERE `t_blog`.`active`= ? AND (`t_blog`.`title` LIKE ? OR `t_blog`.`content` LIKE ? OR `t_user`.`nick` LIKE ?)";

		if (DebugConfig.isDebug) {
			log.debug("������������������{} - {}", sql, keyword);
		}

		int blogTotal = 0;
		try {
			ResultSet resultSet = query(sql, 
					"1",
					"%"+keyword+"%",
					"%"+keyword+"%",
					"%"+keyword+"%"
				);
			if (resultSet.next()) {
				blogTotal = resultSet.getInt("count");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			if (DebugConfig.isDebug) {
				log.error("�������������������ݿ����{}", e);
			}
		} finally {
			close();
		}
		return blogTotal;
	}

	/**
	 * ���Ҳ����Ķ���
	 * @param blogId ����ID
	 * @return
	 * @throws SQLException 
	 */
	public int getReadCountByBlogId(String blogId) throws SQLException {
		String sql = "SELECT `read_count` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("���Ҳ��ļ�����{} - {}", sql, blogId);
		}

		int readCount = 0;
		try {
			ResultSet resultSet = query(sql, 
					blogId
				);
			if (resultSet.next()) {
				readCount = resultSet.getInt("read_count");
			}
		} finally {
			close();
		}
		return readCount;
	}
}

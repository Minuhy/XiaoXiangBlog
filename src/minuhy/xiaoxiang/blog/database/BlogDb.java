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
	 * 获取某个用户的博文总数
	 * @param userId 用户ID
	 * @return 消息总数
	 */
	public int getBlogTotalByUserId(int userId) {
		String sql = "SELECT COUNT(`id`) as `count` " + 
				"FROM `t_blog` " + 
				"WHERE `author_id` = ?  and `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("查找博文总数：{} - {}", sql, userId);
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
				log.error("查找博文总数数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return blogTotal;
	}

	/**
	 * 发布博文，写入数据库
	 * 
	 * @param blogEntity 博文信息
	 * @return 影响行数
	 * @throws SQLException 数据库错误
	 */
	public boolean writeBlog(BlogEntity blogEntity) throws SQLException {
		String sql = "INSERT INTO `t_blog`" 
				+ "(`author_id`, `title`, `content`, `create_timestamp`) "
				+ "VALUES (?, ?, ?, ?)";

		if (DebugConfig.isDebug) {
			log.debug("发布博文：{} - {}", sql, blogEntity.toString());
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
				log.debug("插入数据：{}", result1);
			}
			
			// 增加作者博文计数
			sql = "UPDATE `t_user` "
					+ "SET `blog_count` = `blog_count` + 1 "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(blogEntity.getAuthorId())
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
	 * 通過ID查找博文
	 * 
	 * @param blogId 博文标号
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public BlogEntity getBlogById(String blogId,boolean isActive) throws SQLException {
		String sql = "select * " 
				+ "from `t_blog` " 
				+ "where `id`=?  and `active`=? " 
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找博文：{} - {}，{}", sql, blogId,isActive);
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
	 * 通過ID查找博文
	 * 
	 * @param blogId 博文标号
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public BlogEntity getBlogById(String blogId) throws SQLException {
		return getBlogById(blogId,true);
	}

	/**
	 * 查找一个用户最新的博文ID
	 * 
	 * @param userId 用户ID
	 * @return 如果查到，返回ID，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public String getNewestBlogIdByUserId(String userId) throws SQLException {
		String sql = "SELECT `id` " 
				+ "FROM `t_blog` " 
				+ "where `author_id`=?  and `active`=? "
				+ "ORDER BY `create_timestamp` DESC " 
				+ "LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找用户的最新博文ID：{} - {}", sql, userId);
		}

		String blogId = null;
		try {
			ResultSet resultSet = query(sql, 
					userId, 
					String.valueOf(1)
				);
			
			if (resultSet.next()) {
				blogId = String.valueOf(resultSet.getInt("id")); // 主键，唯一标识
			} else {
				if (DebugConfig.isDebug) {
					log.debug("没有查到数据");
				}
			}
		} finally {
			close();
		}
		return blogId;
	}

	/**
	 * 通过结果集创建一个 BlogEntity
	 * 
	 * @param resultSet 结果集
	 * @return BlogEntity
	 * @throws SQLException
	 */
	public static BlogEntity createBlogEntity(ResultSet resultSet) throws SQLException {
		BlogEntity blogEntity = null;
		if (resultSet.next()) {

			int id = resultSet.getInt("id"); // 主键，唯一标识
			int active = resultSet.getInt("active"); // 是否有效，1有效，0无效
			int authorId = resultSet.getInt("author_id"); // 外键，发布者ID
			String title = resultSet.getString("title"); // 博客标题
			String content = resultSet.getString("content"); // 博客内容
			int readCount = resultSet.getInt("read_count"); // 访问量
			int likeCount = resultSet.getInt("like_count"); // 点赞量
			int commentCount = resultSet.getInt("comment_count"); // 评论数
			int likeMsgSendCount = resultSet.getInt("like_msg_send_count"); // 评论数
			long createTimestamp = resultSet.getLong("create_timestamp"); // 发布时间
			long updateTimestamp = resultSet.getLong("update_timestamp"); // 修改时间

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
				log.debug("查到数据：{}", blogEntity);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("没有查到数据");
			}
		}
		return blogEntity;
	}

	/**
	 * 更新博文
	 * 
	 * @param id        博文ID
	 * @param title     博文标题
	 * @param content   博文内容
	 * @param timestamp 时间戳
	 * @return 成功/否
	 * @throws SQLException
	 */
	public boolean UpdateBlog(String id, String title, String content, long timestamp) throws SQLException {
		String sql = "UPDATE `t_blog` " 
				+ "SET `title` = ?, `content` = ?, `update_timestamp` = ? " 
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("更新博文：{} - {},{},{},{}", sql, id, title, content, timestamp);
		}

		int result = 0;
		try {
			result = update(sql, 
					title, 
					content, 
					String.valueOf(timestamp), 
					id);

			if (DebugConfig.isDebug) {
				log.debug("更新数据：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * 删除博文
	 * 
	 * @param id 博文ID
	 * @return 成功/否
	 * @throws SQLException
	 */
	public boolean deleteBlog(String id,int UserId) throws SQLException {
		String sql = "UPDATE `t_blog` "
				+ "SET `active` = ? "
				+ "WHERE `id` = ?  and `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("删除博文：{} - {}", sql, id);
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
				log.debug("更新数据：{}", result1);
			}
			
			// 减少作者博文计数
			sql = "UPDATE `t_user` "
					+ "SET `blog_count` = IF(`blog_count` - '1'>'0',`blog_count` - '1','0') "
					+ "WHERE `id` = ?";
			result2 = update(sql, 
					String.valueOf(UserId)
					);
			
			if(result2<1) {
				log.warn("用户博客计数错误，{}",UserId);
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
	 * 增加访问量
	 * 
	 * @param userId 用户
	 * @param blogId 博文
	 * @return 成功与否
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
				log.debug("增加访问量：{} - {}", sql1, blogId);
			}

			result1 = update(sql1, 
					blogId
				);

			if (DebugConfig.isDebug) {
				log.debug("更新数据：{}", result1);
			}

			// ------------------------------------------------------------
			String sql2 = "UPDATE `t_user` " 
					+ "SET `blog_read_count` = `blog_read_count` + 1 " 
					+ "WHERE `id` = ?";

			if (DebugConfig.isDebug) {
				log.debug("增加访问量：{} - {}", sql2, authorId);
			}

			result2 = update(sql2, 
					authorId
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
	 * 随机获取博文
	 * 
	 * @param len 获取的条数
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public BlogEntity[] getRandomBlog(int len) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_blog` " + 
				"WHERE `active`=? "+
				"ORDER BY RAND() " + 
				"LIMIT "+len; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("随机获取博文：{} - {}", sql, len);
		}

		BlogEntity[] blogEntitys = new BlogEntity[len];
		try {
			ResultSet resultSet = query(sql,
					"1"
					);
			for(int i=0;i<len;i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if(blogEntity == null) {
					// 不够 len 的长度，重新建个数组替换一下
					BlogEntity[] blogEntitysTemp = new BlogEntity[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// 跳出
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
	 * 按时间顺序获取用户博文
	 * @param userId
	 * @param pageNumber 页数，从0开始
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException
	 */
	public BlogEntity[] getUserBlogsByPageOrderByTime(int userId,int pagination) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_blog` " + 
				"WHERE `author_id` = ? AND `active`=? " + 
				"ORDER BY `create_timestamp` DESC " + 
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("获取用户的博文：{} - {}", sql, userId);
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
					// 不够 blogEntitys.length 的长度，重新建个数组替换一下
					BlogEntity[] blogEntitysTemp = new BlogEntity[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// 跳出
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
	 * 查询一个文章的点赞数量
	 * @param blogId
	 * @return 一个整型数组，第一个为喜欢计数，第二个为喜欢消息发送计数
	 */
	public int[] getBlogLikeCountByBlogId(String blogId) {
		String sql = "SELECT `like_count`,`like_msg_send_count` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查询一个文章的点赞数量：{} - {}", sql, blogId);
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
				log.error("查询一个文章的点赞数量数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return likeCount;
	}
	
	/**
	 * 查询一个文章的作者
	 * @param blogId
	 * @return 
	 */
	public int getBlogAuthorIdByBlogId(String blogId) {
		String sql = "SELECT `author_id` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查询文章的作者：{} - {}", sql, blogId);
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
				log.error("查询一个文章的作者数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return authorId;
	}

	/**
	 * 查询一个文章的标题
	 * @param blogId
	 * @return
	 */
	public String getBlogTitleByBlogId(String blogId) {
		String sql = "SELECT `title` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查询一个文章的标题：{} - {}", sql, blogId);
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
				log.error("查询一个文章的标题数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return blogTitle;
	}
	
	/**
	 * 设置点赞消息发送计数
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
			log.debug("更新点赞消息发送计数：{} - {},{}", sql, blogId, likeMsgSendCount);
		}

		int result = 0;
		try {
			result = update(sql, 
					String.valueOf(likeMsgSendCount), 
					String.valueOf(blogId)
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
	 * 修改博文
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public boolean editBlog(BlogEntity entity) throws SQLException {
		String sql = "UPDATE `t_blog` "
				+ "SET `title` = ?, `content` = ?, `update_timestamp` = ? "
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("修改博文：{} - {}", sql, entity.toString());
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
				log.debug("更新数据：{}", result);
			}
		} finally {
			close();
		}
		return result > 0 ;
	}
	
	/**
	 * 按点赞、浏览量、时间顺序获取用户博文
	 * @param pageNumber 页数，从0开始
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException
	 */
	public BlogEntity[] getBlogsByPageOrderBySql(int pagination,String sqlOrder) throws SQLException {
		String sql = "SELECT * " + 
				"FROM `t_blog` " + 
				"WHERE `active`= ? " + 
				sqlOrder + " "+
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("获取排名博文：{} - {}", sql);
		}

		BlogEntity[] blogEntitys = new BlogEntity[DatabaseConfig.PAGE_ITEM_COUNT];
		try {
			ResultSet resultSet = query(sql,
					"1"
					);
			for(int i=0;i<blogEntitys.length;i++) {
				BlogEntity blogEntity = createBlogEntity(resultSet);
				if(blogEntity == null) {
					// 不够 blogEntitys.length 的长度，重新建个数组替换一下
					BlogEntity[] blogEntitysTemp = new BlogEntity[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// 跳出
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
	 * 获取博文总数
	 * @return 博文总数
	 */
	public int getBlogTotal() {
		String sql = "SELECT COUNT(`id`) as `count` " + 
				"FROM `t_blog` " + 
				"WHERE `active`=? ";

		if (DebugConfig.isDebug) {
			log.debug("查找所有博文总数：{} - {}", sql);
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
				log.error("查找所有博文总数数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return blogTotal;
	}
	
	
	/**
	 * 按关键词搜索博文
	 * @param keyword 关键词
	 * @param pagination 页数
	 * @return 如果查到，返回實體，否則返回null
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
				"LIMIT "+pagination*DatabaseConfig.PAGE_ITEM_COUNT+","+DatabaseConfig.PAGE_ITEM_COUNT; // 不是数据，只能拼接

		if (DebugConfig.isDebug) {
			log.debug("搜索博文：{} - {}，{}", sql, keyword,pagination);
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
					// 不够 blogEntitys.length 的长度，重新建个数组替换一下
					MiniBlogBean[] blogEntitysTemp = new MiniBlogBean[i];
					for(int j=0;j<i;j++) {
						blogEntitysTemp[j] = blogEntitys[j];
					}
					blogEntitys = blogEntitysTemp;
					// 跳出
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
	 * 通过结果集创建一个 MiniBlogEntity
	 * 
	 * @param resultSet 结果集
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
			int id = resultSet.getInt("id"); // 主键，唯一标识
			String author = resultSet.getString("author"); // 博客作者昵称
			String title = resultSet.getString("title"); // 博客标题
			String content = resultSet.getString("content"); // 博客内容
			int readCount = resultSet.getInt("read_count"); // 访问量
			int likeCount = resultSet.getInt("like_count"); // 点赞量
			int commentCount = resultSet.getInt("comment_count"); // 评论数
			long createTimestamp = resultSet.getLong("create_timestamp"); // 发布时间

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
				log.debug("查到数据：{}", blogBean);
			}
		} else {
			if (DebugConfig.isDebug) {
				log.debug("没有查到数据");
			}
		}
		return blogBean;
	}
	
	/**
	 * 获取某个关键词的博文总数
	 * @param keyword 关键词
	 * @return 消息总数
	 */
	public int getBlogTotalBySearchKeyword(String keyword) {
		String sql = "SELECT COUNT(*) AS `count` " + 
				"FROM `t_blog` JOIN `t_user` " + 
				"ON `t_blog`.`author_id` = `t_user`.`id` " + 
				"WHERE `t_blog`.`active`= ? AND (`t_blog`.`title` LIKE ? OR `t_blog`.`content` LIKE ? OR `t_user`.`nick` LIKE ?)";

		if (DebugConfig.isDebug) {
			log.debug("查找搜索博文总数：{} - {}", sql, keyword);
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
				log.error("查找搜索博文总数数据库出错：{}", e);
			}
		} finally {
			close();
		}
		return blogTotal;
	}

	/**
	 * 查找博文阅读数
	 * @param blogId 博客ID
	 * @return
	 * @throws SQLException 
	 */
	public int getReadCountByBlogId(String blogId) throws SQLException {
		String sql = "SELECT `read_count` " + 
				"FROM `t_blog` " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查找博文计数：{} - {}", sql, blogId);
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

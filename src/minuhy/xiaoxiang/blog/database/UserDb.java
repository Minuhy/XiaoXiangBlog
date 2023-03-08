package minuhy.xiaoxiang.blog.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.common.Executant;
import minuhy.xiaoxiang.blog.entity.UserEntity;

public class UserDb extends Executant {
	private static final Logger log = LoggerFactory.getLogger(UserDb.class);

	/**
	 * 查询一个用户的昵称
	 * @param userId
	 * @return 
	 * @throws SQLException 
	 */
	public String getNickById(int userId) throws SQLException {
		String sql = "SELECT `nick` "
				+ "FROM `t_user` "
				+ "WHERE `id`= ?";

		if (DebugConfig.isDebug) {
			log.debug("查询昵称：{} - {}", sql, userId);
		}

		String nick = "";
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId)
				);
			if (resultSet.next()) {
				nick = resultSet.getString("nick");
			}
		} finally {
			close();
		}
		return nick;
	}
	
	/**
	 * 通过评论的回复ID查询一个用户的昵称
	 * @param replyId
	 * @return 
	 * @throws SQLException 
	 */
	public String getNickByReplyId(int replyId) throws SQLException {
		String sql = "SELECT `nick` " + 
				"FROM `t_user` " + 
				"WHERE `id` = ( " + 
				"	SELECT `user_id` " + 
				"	FROM `t_comment` " + 
				"	WHERE `id` = ? " + 
				")";

		if (DebugConfig.isDebug) {
			log.debug("评论的回复ID查询昵称：{} - {}", sql, replyId);
		}

		String nick = "";
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(replyId)
				);
			if (resultSet.next()) {
				nick = resultSet.getString("nick");
			}
		} finally {
			close();
		}
		return nick;
	}
	
	/**
	 * 通過ID查找用戶昵称和头像
	 * 
	 * @param id ID
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public UserEntity getCommentUserInfoById(int userId) throws SQLException {
		String sql = "SELECT `nick`,`avatar` " + 
				"FROM `t_user` " + 
				"WHERE `id` = ? " + 
				"LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找用戶信息：{} - {}", sql, userId);
		}

		UserEntity userEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId)
				);
			if (resultSet.next()) {
				String nick = resultSet.getString("nick"); // 昵称
				int avatar = resultSet.getInt("avatar"); // 头像ID

				userEntity = new UserEntity();
				userEntity.setNick(nick);
				userEntity.setAvatar(avatar);

				if (DebugConfig.isDebug) {
					log.debug("查到数据：{}", userEntity);
				}
			}else {
				if (DebugConfig.isDebug) {
					log.debug("没查到数据");
				}
			}
		} finally {
			close();
		}
		return userEntity;
	}
	
	/**
	 * 查询一个文章的作者
	 * @param userId
	 * @return 
	 * @throws SQLException 
	 */
	public int getIdByAccount(String account) throws SQLException {
		String sql = "SELECT `id` "
				+ "FROM `t_user` "
				+ "WHERE `account` = ?";

		if (DebugConfig.isDebug) {
			log.debug("查询ID：{} - {}", sql, account);
		}

		int userId = 0;
		try {
			ResultSet resultSet = query(sql, 
					account
				);
			if (resultSet.next()) {
				userId = resultSet.getInt("id");
			}
		} finally {
			close();
		}
		return userId;
	}
	
	
	/**
	 * 通過ID查找用戶
	 * 
	 * @param id ID
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public UserEntity getUserById(String id) throws SQLException {
		return getUserById(id,true);
	}
	
	/**
	 * 通過ID查找用戶
	 * 
	 * @param id ID
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public UserEntity getUserById(String id,boolean isActive) throws SQLException {
		String sql = "select * "
				+ "from `t_user` "
				+ "where `id`=?  and `active`=? "
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找用戶：{} - {}", sql, id);
		}

		UserEntity userEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					id, 
					isActive?"1":"0"
				);
			userEntity = createUserEntity(resultSet);
		} finally {
			close();
		}
		return userEntity;
	}

	/**
	 * 通過賬號查找用戶
	 * 
	 * @param account 賬號
	 * @return 如果查到，返回實體，否則返回null
	 * @throws SQLException 数据库错误
	 */
	public UserEntity getUserByAccount(String account) throws SQLException {
		String sql = "select * "
				+ "from `t_user` "
				+ "where `account`=?  and `active`=? "
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找用戶：{} - {}", sql, account);
		}

		UserEntity userEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					account, 
					String.valueOf(1)
				);
			userEntity = createUserEntity(resultSet);
		} finally {
			close();
		}
		return userEntity;
	}

	/**
	 * 通过结果集创建 UserEntity
	 * 
	 * @param resultSet 结果集
	 * @return null 为无
	 * @throws SQLException
	 */
	public static UserEntity createUserEntity(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			int id = resultSet.getInt("id"); // ID，自动生成，唯一
			int active = resultSet.getInt("active"); // 账号是否激活：1激活，0禁用
			String passwd = resultSet.getString("passwd"); // 密码，登录用，MD5加密
			int role = resultSet.getInt("role"); // 角色，0：普通，1：管理员
			String nick = resultSet.getString("nick"); // 昵称
			String account = resultSet.getString("account"); // 账号
			String signature = resultSet.getString("signature"); // 签名
			int sex = resultSet.getInt("sex"); // 性别，0：未设置，1：男，2：女
			String hometown = resultSet.getString("hometown"); // 家乡
			String link = resultSet.getString("link"); // 联系方式
			int avatar = resultSet.getInt("avatar"); // 头像ID
			String avatarUrl = resultSet.getString("avatar_url"); // 头像URL
			int hasNewMsg = resultSet.getInt("has_new_msg"); // 新消息计数
			int blogCount = resultSet.getInt("blog_count"); // 博客数量计数
			int blogReadCount = resultSet.getInt("blog_read_count"); // 博客阅读计数
			int blogLikeCount = resultSet.getInt("blog_like_count"); // 博客被点赞计数
			long createTimestamp = resultSet.getLong("create_timestamp"); // 创建时间
			long updateTimestamp = resultSet.getLong("update_timestamp"); // 最后修改时间
			long lastLoginTimestamp = resultSet.getLong("last_login_timestamp"); // 最后登录时间
			String lastLoginIp = resultSet.getString("last_login_ip"); // 最后登录时间

			UserEntity userEntity = new UserEntity(
					id, 
					active, 
					account, 
					passwd, 
					role, 
					nick, 
					signature, 
					sex, 
					hometown,
					link, 
					avatar, 
					avatarUrl, 
					hasNewMsg, 
					blogCount, 
					blogReadCount, 
					blogLikeCount, 
					createTimestamp,
					updateTimestamp, 
					lastLoginTimestamp,
					lastLoginIp
				);

			if (DebugConfig.isDebug) {
				log.debug("查到数据：{}", userEntity);
			}

			return userEntity;
		} else {
			if (DebugConfig.isDebug) {
				log.debug("没有查到数据");
			}
		}
		return null;
	}

	/**
	 * 注册，写入数据库
	 * 
	 * @param userEntity 用户信息
	 * @return 影响行数
	 * @throws SQLException 数据库错误
	 */
	public boolean writeUser(UserEntity userEntity) throws SQLException {
		String sql = "INSERT INTO `t_user`" 
				+ "(`account`,`role`, `passwd`, `nick`, `create_timestamp`) "
				+ "VALUES (?, ?, ?, ?,?)";

		if (DebugConfig.isDebug) {
			log.debug("注册：{} - {}", sql, userEntity.toString());
		}

		int result = 0;
		try {
			result = insert(sql, 
					userEntity.getAccount(), 
					String.valueOf(userEntity.getRole()), 
					userEntity.getPasswd(), 
					userEntity.getNick(),
					String.valueOf(userEntity.getCreateTimestamp())
				);

			if (DebugConfig.isDebug) {
				log.debug("插入数据：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * 更新登录时间
	 * 
	 * @param id          用户ID
	 * @param currentTime 时间戳
	 * @return 成功/否
	 * @throws SQLException
	 */
	public boolean UpdateLoginTimeAndIp(int id, long currentTime,String ip) throws SQLException {
		String sql = "UPDATE `t_user` "
				+ "SET `last_login_timestamp` = ?,`last_login_ip` = ? "
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("更新登錄時間IP：{} - {}，{}，{}", sql, id, currentTime,ip);
		}

		int result = 0;
		try {
			result = update(sql, 
					String.valueOf(currentTime), 
					ip,
					String.valueOf(id)
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
	 * 更新个人资料
	 * @param entity 个人资料（id，nick signature sex hometown link avatar）
	 * @return
	 * @throws SQLException
	 */
	public boolean UpdateProfile(UserEntity entity) throws SQLException {
		String sql = "UPDATE `t_user` " + 
				"SET `nick` = ?, `signature` = ?, `sex` = ?, `hometown` = ?, `link` = ?, `avatar` = ?,`update_timestamp`=? " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("更新个人资料：{} - {}", sql, entity);
		}

		int result = 0;
		try {
			result = update(sql, 
					entity.getNick(),
					entity.getSignature(),
					String.valueOf(entity.getSex()),
					entity.getHometown(),
					entity.getLink(),
					String.valueOf(entity.getAvatar()),
					String.valueOf(entity.getUpdateTimestamp()),
					String.valueOf(entity.getId())
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
	 * 更新个人密码
	 * @param rawPwd 原始密码
	 * @param newPwd 新密码
	 * @return true，成功，false，原密码不正确
	 * @throws SQLException
	 */
	public boolean UpdatePasswd(int userId,String rawPwd,String newPwd) throws SQLException {
		String sql = "UPDATE `t_user` " + 
				"SET `passwd` = IF(`passwd`= ? ,?,`passwd`) " + 
				"WHERE `id`= ?";

		if (DebugConfig.isDebug) {
			log.debug("更新密码：{} - {}，{}，{}", sql, userId,rawPwd,newPwd);
		}

		int result = 0;
		try {
			result = update(sql, 
					rawPwd,
					newPwd,
					String.valueOf(userId)
				);

			if (DebugConfig.isDebug) {
				log.debug("更新密码：{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}
	
	public String getAccountById(String id) throws SQLException{
		String sql = "SELECT `account` " + 
				"FROM `t_user` " + 
				"WHERE `id`= ? "
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("查找用戶：{} - {}", sql, id);
		}

		String account = null;
		try {
			ResultSet resultSet = query(sql, 
					id
				);
			if (resultSet.next()) {
				account = resultSet.getString("account"); // 账号
				
				if (DebugConfig.isDebug) {
					log.debug("查到数据：{}", account);
				}

				return account;
			} else {
				if (DebugConfig.isDebug) {
					log.debug("没有查到数据");
				}
			}
		} finally {
			close();
		}
		return account;
	}
}

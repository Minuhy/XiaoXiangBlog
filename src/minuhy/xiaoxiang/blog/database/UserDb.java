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
	 * ��ѯһ���û����ǳ�
	 * @param userId
	 * @return 
	 * @throws SQLException 
	 */
	public String getNickById(int userId) throws SQLException {
		String sql = "SELECT `nick` "
				+ "FROM `t_user` "
				+ "WHERE `id`= ?";

		if (DebugConfig.isDebug) {
			log.debug("��ѯ�ǳƣ�{} - {}", sql, userId);
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
	 * ͨ�����۵Ļظ�ID��ѯһ���û����ǳ�
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
			log.debug("���۵Ļظ�ID��ѯ�ǳƣ�{} - {}", sql, replyId);
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
	 * ͨ�^ID�����Ñ��ǳƺ�ͷ��
	 * 
	 * @param id ID
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public UserEntity getCommentUserInfoById(int userId) throws SQLException {
		String sql = "SELECT `nick`,`avatar` " + 
				"FROM `t_user` " + 
				"WHERE `id` = ? " + 
				"LIMIT 0,1";

		if (DebugConfig.isDebug) {
			log.debug("�����Ñ���Ϣ��{} - {}", sql, userId);
		}

		UserEntity userEntity = null;
		try {
			ResultSet resultSet = query(sql, 
					String.valueOf(userId)
				);
			if (resultSet.next()) {
				String nick = resultSet.getString("nick"); // �ǳ�
				int avatar = resultSet.getInt("avatar"); // ͷ��ID

				userEntity = new UserEntity();
				userEntity.setNick(nick);
				userEntity.setAvatar(avatar);

				if (DebugConfig.isDebug) {
					log.debug("�鵽���ݣ�{}", userEntity);
				}
			}else {
				if (DebugConfig.isDebug) {
					log.debug("û�鵽����");
				}
			}
		} finally {
			close();
		}
		return userEntity;
	}
	
	/**
	 * ��ѯһ�����µ�����
	 * @param userId
	 * @return 
	 * @throws SQLException 
	 */
	public int getIdByAccount(String account) throws SQLException {
		String sql = "SELECT `id` "
				+ "FROM `t_user` "
				+ "WHERE `account` = ?";

		if (DebugConfig.isDebug) {
			log.debug("��ѯID��{} - {}", sql, account);
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
	 * ͨ�^ID�����Ñ�
	 * 
	 * @param id ID
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public UserEntity getUserById(String id) throws SQLException {
		return getUserById(id,true);
	}
	
	/**
	 * ͨ�^ID�����Ñ�
	 * 
	 * @param id ID
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public UserEntity getUserById(String id,boolean isActive) throws SQLException {
		String sql = "select * "
				+ "from `t_user` "
				+ "where `id`=?  and `active`=? "
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("�����Ñ���{} - {}", sql, id);
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
	 * ͨ�^�~̖�����Ñ�
	 * 
	 * @param account �~̖
	 * @return ����鵽�����،��w����t����null
	 * @throws SQLException ���ݿ����
	 */
	public UserEntity getUserByAccount(String account) throws SQLException {
		String sql = "select * "
				+ "from `t_user` "
				+ "where `account`=?  and `active`=? "
				+ "limit 0,1";

		if (DebugConfig.isDebug) {
			log.debug("�����Ñ���{} - {}", sql, account);
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
	 * ͨ����������� UserEntity
	 * 
	 * @param resultSet �����
	 * @return null Ϊ��
	 * @throws SQLException
	 */
	public static UserEntity createUserEntity(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			int id = resultSet.getInt("id"); // ID���Զ����ɣ�Ψһ
			int active = resultSet.getInt("active"); // �˺��Ƿ񼤻1���0����
			String passwd = resultSet.getString("passwd"); // ���룬��¼�ã�MD5����
			int role = resultSet.getInt("role"); // ��ɫ��0����ͨ��1������Ա
			String nick = resultSet.getString("nick"); // �ǳ�
			String account = resultSet.getString("account"); // �˺�
			String signature = resultSet.getString("signature"); // ǩ��
			int sex = resultSet.getInt("sex"); // �Ա�0��δ���ã�1���У�2��Ů
			String hometown = resultSet.getString("hometown"); // ����
			String link = resultSet.getString("link"); // ��ϵ��ʽ
			int avatar = resultSet.getInt("avatar"); // ͷ��ID
			String avatarUrl = resultSet.getString("avatar_url"); // ͷ��URL
			int hasNewMsg = resultSet.getInt("has_new_msg"); // ����Ϣ����
			int blogCount = resultSet.getInt("blog_count"); // ������������
			int blogReadCount = resultSet.getInt("blog_read_count"); // �����Ķ�����
			int blogLikeCount = resultSet.getInt("blog_like_count"); // ���ͱ����޼���
			long createTimestamp = resultSet.getLong("create_timestamp"); // ����ʱ��
			long updateTimestamp = resultSet.getLong("update_timestamp"); // ����޸�ʱ��
			long lastLoginTimestamp = resultSet.getLong("last_login_timestamp"); // ����¼ʱ��
			String lastLoginIp = resultSet.getString("last_login_ip"); // ����¼ʱ��

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
				log.debug("�鵽���ݣ�{}", userEntity);
			}

			return userEntity;
		} else {
			if (DebugConfig.isDebug) {
				log.debug("û�в鵽����");
			}
		}
		return null;
	}

	/**
	 * ע�ᣬд�����ݿ�
	 * 
	 * @param userEntity �û���Ϣ
	 * @return Ӱ������
	 * @throws SQLException ���ݿ����
	 */
	public boolean writeUser(UserEntity userEntity) throws SQLException {
		String sql = "INSERT INTO `t_user`" 
				+ "(`account`,`role`, `passwd`, `nick`, `create_timestamp`) "
				+ "VALUES (?, ?, ?, ?,?)";

		if (DebugConfig.isDebug) {
			log.debug("ע�᣺{} - {}", sql, userEntity.toString());
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
				log.debug("�������ݣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}

	/**
	 * ���µ�¼ʱ��
	 * 
	 * @param id          �û�ID
	 * @param currentTime ʱ���
	 * @return �ɹ�/��
	 * @throws SQLException
	 */
	public boolean UpdateLoginTimeAndIp(int id, long currentTime,String ip) throws SQLException {
		String sql = "UPDATE `t_user` "
				+ "SET `last_login_timestamp` = ?,`last_login_ip` = ? "
				+ "WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("���µ�䛕r�gIP��{} - {}��{}��{}", sql, id, currentTime,ip);
		}

		int result = 0;
		try {
			result = update(sql, 
					String.valueOf(currentTime), 
					ip,
					String.valueOf(id)
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
	 * ���¸�������
	 * @param entity �������ϣ�id��nick signature sex hometown link avatar��
	 * @return
	 * @throws SQLException
	 */
	public boolean UpdateProfile(UserEntity entity) throws SQLException {
		String sql = "UPDATE `t_user` " + 
				"SET `nick` = ?, `signature` = ?, `sex` = ?, `hometown` = ?, `link` = ?, `avatar` = ?,`update_timestamp`=? " + 
				"WHERE `id` = ?";

		if (DebugConfig.isDebug) {
			log.debug("���¸������ϣ�{} - {}", sql, entity);
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
				log.debug("�������ݣ�{}", result);
			}
		} finally {
			close();
		}
		return result > 0;
	}
	
	/**
	 * ���¸�������
	 * @param rawPwd ԭʼ����
	 * @param newPwd ������
	 * @return true���ɹ���false��ԭ���벻��ȷ
	 * @throws SQLException
	 */
	public boolean UpdatePasswd(int userId,String rawPwd,String newPwd) throws SQLException {
		String sql = "UPDATE `t_user` " + 
				"SET `passwd` = IF(`passwd`= ? ,?,`passwd`) " + 
				"WHERE `id`= ?";

		if (DebugConfig.isDebug) {
			log.debug("�������룺{} - {}��{}��{}", sql, userId,rawPwd,newPwd);
		}

		int result = 0;
		try {
			result = update(sql, 
					rawPwd,
					newPwd,
					String.valueOf(userId)
				);

			if (DebugConfig.isDebug) {
				log.debug("�������룺{}", result);
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
			log.debug("�����Ñ���{} - {}", sql, id);
		}

		String account = null;
		try {
			ResultSet resultSet = query(sql, 
					id
				);
			if (resultSet.next()) {
				account = resultSet.getString("account"); // �˺�
				
				if (DebugConfig.isDebug) {
					log.debug("�鵽���ݣ�{}", account);
				}

				return account;
			} else {
				if (DebugConfig.isDebug) {
					log.debug("û�в鵽����");
				}
			}
		} finally {
			close();
		}
		return account;
	}
}

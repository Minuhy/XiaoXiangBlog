package minuhy.xiaoxiang.blog.bean.user;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;

/**
 * peopleҳ��ר�ã���UserBean��Щ��Ϣ
 * @author y17mm
 *
 */
public class UserInfoBean  extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(UserInfoBean.class);
	int id; // ID���Զ����ɣ�Ψһ
	String account; // �˺ţ���¼�ã������޸�
	String nick; // �ǳ�
	String signature; // ǩ��
	int sex; // �Ա�0��δ���ã�1���У�2��Ů
	String hometown; // ����
	String link; // ��ϵ��ʽ
	int avatar; // ͷ��ID
	String avatarUrl; // ͷ��URL
	int blogCount; // ������������
	int blogReadCount; // �����Ķ�����
	int blogLikeCount; // ���ͱ����޼���
	long createTimestamp; // ����ʱ��
	long lastLoginTimestamp; // ����¼ʱ��
	String lastLoginIp; // ����¼IP
	
	boolean loaded = false; // �Ƿ��Ѿ���ȡ������
	
	
	
	public boolean isLoaded() {
		return loaded;
	}

	public UserInfoBean() {}
	
	public UserInfoBean(int id, String account, String nick, String signature, int sex, String hometown, String link, int avatar, String avatarUrl, int blogCount, int blogReadCount, int blogLikeCount, long createTimestamp, long lastLoginTimestamp,String lastLoginIp) {
        this.id = id;
        this.account = account;
        this.nick = nick;
        this.signature = signature;
        this.sex = sex;
        this.hometown = hometown;
        this.link = link;
        this.avatar = avatar;
        this.avatarUrl = avatarUrl;
        this.blogCount = blogCount;
        this.blogReadCount = blogReadCount;
        this.blogLikeCount = blogLikeCount;
        this.createTimestamp = createTimestamp;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.lastLoginIp = lastLoginIp;
    }
	
	public void getData(int userId) throws SQLException {
        if (DebugConfig.isDebug) {
            log.debug("��ȡ�û���{}",userId);
        }
		// ����û���Ϣ
    	UserDb userDb = new UserDb();
    	UserEntity userEntity;
    	userEntity = userDb.getUserById(String.valueOf(userId));
    	
    	if(userEntity != null) {
            if (DebugConfig.isDebug) {
                log.debug("�û���{}��ͷ��{}",userEntity.getAccount(), userEntity.getAvatar());
            }
            this.id = userEntity.getId(); // ID���Զ����ɣ�Ψһ
            this.nick = userEntity.getNick(); // �ǳ�
            this.account = userEntity.getAccount(); // �˺�
            this.signature = userEntity.getSignature(); // ǩ��
            this.sex = userEntity.getSex(); // �Ա�0��δ���ã�1���У�2��Ů
            this.hometown = userEntity.getHometown(); // ����
            this.link = userEntity.getLink(); // ��ϵ��ʽ
            this.avatar = userEntity.getAvatar(); // ͷ��ID
            this.avatarUrl = userEntity.getAvatarUrl(); // ͷ��URL
            this.blogCount = userEntity.getBlogCount(); // ������������
            this.blogReadCount = userEntity.getBlogReadCount(); // �����Ķ�����
            this.blogLikeCount = userEntity.getBlogLikeCount(); // ���ͱ����޼���
            this.createTimestamp = userEntity.getCreateTimestamp(); // ����ʱ��
            this.lastLoginTimestamp = userEntity.getLastLoginTimestamp(); // ����¼ʱ��
			this.lastLoginIp = userEntity.getLastLoginIp(); // ����¼IP
    	}else {
    		try {
    			this.id = userId;
    		}catch (NumberFormatException e) {
    			this.id = -1;
			}
            this.account = "null";
            this.nick = "�û���ע��";
            this.signature = "";
            this.avatar = 0;
            this.sex = 0; // �Ա�0��δ���ã�1���У�2��Ů
            this.hometown = ""; // ����
            this.link = ""; // ��ϵ��ʽ
            this.avatarUrl = ""; // ͷ��URL
            this.blogCount = 0; // ������������
            this.blogReadCount = 0; // �����Ķ�����
            this.blogLikeCount = 0; // ���ͱ����޼���
            this.createTimestamp = 0; // ����ʱ��
            this.lastLoginTimestamp = 0; // ����¼ʱ��
            this.lastLoginIp = "";
            if (DebugConfig.isDebug) {
                log.debug("�û���ȡʧ�ܣ�{}",this.avatar);
            }
    	}
    	
    	refresh();
	}
	
	public String getUserNiceById(int userId) {
		UserDb userDb = new UserDb();
		try {
			return userDb.getNickById(userId);
		} catch (SQLException e) {
			e.printStackTrace();
			log.warn("��ȡ�ǳ�ʧ�ܣ�{}",userId);
			return "";
		}
	}

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getSex() {
		return sex;
	}

	public String getSexStr() {
		// �Ա�0��δ���ã�1���У�2��Ů
		if(getSex() == 1) {
			return "��";
		}else if(getSex() == 2) {
			return "Ů";
		}else {
			return "δ����";
		}
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	
	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getBlogCount() {
		return blogCount;
	}

	public void setBlogCount(int blogCount) {
		this.blogCount = blogCount;
	}

	public int getBlogReadCount() {
		return blogReadCount;
	}

	public void setBlogReadCount(int blogReadCount) {
		this.blogReadCount = blogReadCount;
	}

	public int getBlogLikeCount() {
		return blogLikeCount;
	}

	public void setBlogLikeCount(int blogLikeCount) {
		this.blogLikeCount = blogLikeCount;
	}

	public long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public long getLastLoginTimestamp() {
		return lastLoginTimestamp;
	}

	public void setLastLoginTimestamp(long lastLoginTimestamp) {
		this.lastLoginTimestamp = lastLoginTimestamp;
	}

	@Override
    public String toString() {
        return "UserInfoBean{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", nick='" + nick + '\'' +
                ", signature='" + signature + '\'' +
                ", sex=" + sex +
                ", hometown='" + hometown + '\'' +
                ", link='" + link + '\'' +
                ", avatar=" + avatar +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", blogCount=" + blogCount +
                ", blogReadCount=" + blogReadCount +
                ", blogLikeCount=" + blogLikeCount +
                ", createTimestamp=" + createTimestamp +
                ", lastLoginTimestamp=" + lastLoginTimestamp +
                '}';
    }
	
}

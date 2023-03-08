package minuhy.xiaoxiang.blog.bean.user;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;

/**
 * people页面专用，比UserBean多些信息
 * @author y17mm
 *
 */
public class UserInfoBean  extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(UserInfoBean.class);
	int id; // ID，自动生成，唯一
	String account; // 账号，登录用，不可修改
	String nick; // 昵称
	String signature; // 签名
	int sex; // 性别，0：未设置，1：男，2：女
	String hometown; // 家乡
	String link; // 联系方式
	int avatar; // 头像ID
	String avatarUrl; // 头像URL
	int blogCount; // 博客数量计数
	int blogReadCount; // 博客阅读计数
	int blogLikeCount; // 博客被点赞计数
	long createTimestamp; // 创建时间
	long lastLoginTimestamp; // 最后登录时间
	String lastLoginIp; // 最后登录IP
	
	boolean loaded = false; // 是否已经获取数据了
	
	
	
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
            log.debug("获取用户：{}",userId);
        }
		// 查出用户信息
    	UserDb userDb = new UserDb();
    	UserEntity userEntity;
    	userEntity = userDb.getUserById(String.valueOf(userId));
    	
    	if(userEntity != null) {
            if (DebugConfig.isDebug) {
                log.debug("用户：{}，头像：{}",userEntity.getAccount(), userEntity.getAvatar());
            }
            this.id = userEntity.getId(); // ID，自动生成，唯一
            this.nick = userEntity.getNick(); // 昵称
            this.account = userEntity.getAccount(); // 账号
            this.signature = userEntity.getSignature(); // 签名
            this.sex = userEntity.getSex(); // 性别，0：未设置，1：男，2：女
            this.hometown = userEntity.getHometown(); // 家乡
            this.link = userEntity.getLink(); // 联系方式
            this.avatar = userEntity.getAvatar(); // 头像ID
            this.avatarUrl = userEntity.getAvatarUrl(); // 头像URL
            this.blogCount = userEntity.getBlogCount(); // 博客数量计数
            this.blogReadCount = userEntity.getBlogReadCount(); // 博客阅读计数
            this.blogLikeCount = userEntity.getBlogLikeCount(); // 博客被点赞计数
            this.createTimestamp = userEntity.getCreateTimestamp(); // 创建时间
            this.lastLoginTimestamp = userEntity.getLastLoginTimestamp(); // 最后登录时间
			this.lastLoginIp = userEntity.getLastLoginIp(); // 最后登录IP
    	}else {
    		try {
    			this.id = userId;
    		}catch (NumberFormatException e) {
    			this.id = -1;
			}
            this.account = "null";
            this.nick = "用户已注销";
            this.signature = "";
            this.avatar = 0;
            this.sex = 0; // 性别，0：未设置，1：男，2：女
            this.hometown = ""; // 家乡
            this.link = ""; // 联系方式
            this.avatarUrl = ""; // 头像URL
            this.blogCount = 0; // 博客数量计数
            this.blogReadCount = 0; // 博客阅读计数
            this.blogLikeCount = 0; // 博客被点赞计数
            this.createTimestamp = 0; // 创建时间
            this.lastLoginTimestamp = 0; // 最后登录时间
            this.lastLoginIp = "";
            if (DebugConfig.isDebug) {
                log.debug("用户获取失败：{}",this.avatar);
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
			log.warn("获取昵称失败：{}",userId);
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
		// 性别，0：未设置，1：男，2：女
		if(getSex() == 1) {
			return "男";
		}else if(getSex() == 2) {
			return "女";
		}else {
			return "未设置";
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

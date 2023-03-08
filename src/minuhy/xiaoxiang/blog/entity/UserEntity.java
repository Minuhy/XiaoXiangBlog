package minuhy.xiaoxiang.blog.entity;
/**
 * 对应着数据库中的表实体
 * 
 * @author y17mm
 *
 */
public class UserEntity {
	int id; // ID，自动生成，唯一
	int active; // 账号是否激活：1激活，0禁用
	String account; // 账号，登录用，不可修改
	String passwd; // 密码，登录用，MD5加密
	int role; // 角色，0：普通，1：管理员
	String nick; // 昵称
	String signature; // 签名
	int sex; // 性别，0：未设置，1：男，2：女
	String hometown; // 家乡
	String link; // 联系方式
	int avatar; // 头像ID
	String avatarUrl; // 头像URL
	int hasNewMsg; // 新消息计数
	int blogCount; // 博客数量计数
	int blogReadCount; // 博客阅读计数
	int blogLikeCount; // 博客被点赞计数
	long createTimestamp; // 创建时间
	long updateTimestamp; // 最后修改时间
	long lastLoginTimestamp; // 最后登录时间
	String lastLoginIp; // 最后登录的IP
	
	public UserEntity() {}
	
    public UserEntity(int id, int active, String account, String passwd, int role, String nick, String signature, int sex, String hometown, String link, int avatar, String avatarUrl, int hasNewMsg, int blogCount, int blogReadCount, int blogLikeCount, long createTimestamp, long updateTimestamp, long lastLoginTimestamp,String lastLoginIp) {
        this.id = id;
        this.active = active;
        this.account = account;
        this.passwd = passwd;
        this.role = role;
        this.nick = nick;
        this.signature = signature;
        this.sex = sex;
        this.hometown = hometown;
        this.link = link;
        this.avatar = avatar;
        this.avatarUrl = avatarUrl;
        this.hasNewMsg = hasNewMsg;
        this.blogCount = blogCount;
        this.blogReadCount = blogReadCount;
        this.blogLikeCount = blogLikeCount;
        this.createTimestamp = createTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.lastLoginIp = lastLoginIp;
    }
    
	
	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
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
	public void setSex(int sex) {
		this.sex = sex;
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
	public int getHasNewMsg() {
		return hasNewMsg;
	}
	public void setHasNewMsg(int hasNewMsg) {
		this.hasNewMsg = hasNewMsg;
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
	public long getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(long updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	public long getLastLoginTimestamp() {
		return lastLoginTimestamp;
	}
	public void setLastLoginTimestamp(long lastLoginTimestamp) {
		this.lastLoginTimestamp = lastLoginTimestamp;
	}
	
	@Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", active=" + active +
                ", account='" + account + '\'' +
                ", passwd='" + passwd + '\'' +
                ", role=" + role +
                ", nick='" + nick + '\'' +
                ", signature='" + signature + '\'' +
                ", sex=" + sex +
                ", hometown='" + hometown + '\'' +
                ", link='" + link + '\'' +
                ", avatar=" + avatar +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", hasNewMsg=" + hasNewMsg +
                ", blogCount=" + blogCount +
                ", blogReadCount=" + blogReadCount +
                ", blogLikeCount=" + blogLikeCount +
                ", createTimestamp=" + createTimestamp +
                ", updateTimestamp=" + updateTimestamp +
                ", lastLoginTimestamp=" + lastLoginTimestamp +
                ", lastLoginIp=" + lastLoginIp +
                '}';
    }
}

package minuhy.xiaoxiang.blog.entity;
/**
 * ��Ӧ�����ݿ��еı�ʵ��
 * 
 * @author y17mm
 *
 */
public class UserEntity {
	int id; // ID���Զ����ɣ�Ψһ
	int active; // �˺��Ƿ񼤻1���0����
	String account; // �˺ţ���¼�ã������޸�
	String passwd; // ���룬��¼�ã�MD5����
	int role; // ��ɫ��0����ͨ��1������Ա
	String nick; // �ǳ�
	String signature; // ǩ��
	int sex; // �Ա�0��δ���ã�1���У�2��Ů
	String hometown; // ����
	String link; // ��ϵ��ʽ
	int avatar; // ͷ��ID
	String avatarUrl; // ͷ��URL
	int hasNewMsg; // ����Ϣ����
	int blogCount; // ������������
	int blogReadCount; // �����Ķ�����
	int blogLikeCount; // ���ͱ����޼���
	long createTimestamp; // ����ʱ��
	long updateTimestamp; // ����޸�ʱ��
	long lastLoginTimestamp; // ����¼ʱ��
	String lastLoginIp; // ����¼��IP
	
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

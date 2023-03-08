package minuhy.xiaoxiang.blog.entity;

public class LikeEntity {
	int id; // 主键，唯一
	int state; // 状态：1点赞，-1反对，0取消
	int blogId; // 被点赞的博文
	int userId; // 点赞的用户
	long createTimestamp; // 点赞时间
	long updateTimestamp; // 修改时间
	
	public LikeEntity() { }
	
	public LikeEntity(int id, int state, int blogId, int userId, long createTimestamp, long updateTimestamp) {
        this.id = id;
        this.state = state;
        this.blogId = blogId;
        this.userId = userId;
        this.createTimestamp = createTimestamp;
        this.updateTimestamp = updateTimestamp;
    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getBlogId() {
		return blogId;
	}
	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
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
	
    @Override
    public String toString() {
        return "LikeEntity{" +
                "id=" + id +
                ", state=" + state +
                ", blogId=" + blogId +
                ", userId=" + userId +
                ", createTimestamp=" + createTimestamp +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }
}

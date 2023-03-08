package minuhy.xiaoxiang.blog.entity;

public class LikeEntity {
	int id; // ������Ψһ
	int state; // ״̬��1���ޣ�-1���ԣ�0ȡ��
	int blogId; // �����޵Ĳ���
	int userId; // ���޵��û�
	long createTimestamp; // ����ʱ��
	long updateTimestamp; // �޸�ʱ��
	
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

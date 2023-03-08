package minuhy.xiaoxiang.blog.entity;

public class CommentEntity {
	int id; // ������Ψһ��ʶ
	int active; // �����Ƿ���ڣ�1�����ڣ�0����ɾ��
	int blogId; // ����ID������ƪ�����µ�����
	int userId; // ���۷�����ID
	int replyId; // ���ظ�������ID
	String content; // �ظ�������
	long createTimestamp; // �ظ�ʱ��
	long updateTimestamp; // ����ʱ��
	
	
    public CommentEntity(int id, int active, int blogId, int userId, int replyId, String content, long createTimestamp, long updateTimestamp) {
        this.id = id;
        this.active = active;
        this.blogId = blogId;
        this.userId = userId;
        this.replyId = replyId;
        this.content = content;
        this.createTimestamp = createTimestamp;
        this.updateTimestamp = updateTimestamp;
    }
	
	public CommentEntity() {
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
	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
        return "CommentEntity{" +
        		 "id=" + id +
                 ", active=" + active +
                 ", blogId=" + blogId +
                 ", userId=" + userId +
                 ", replyId=" + replyId +
                 ", content='" + content + '\'' +
                 ", createTimestamp=" + createTimestamp +
                 ", updateTimestamp=" + updateTimestamp +
                 '}';
    }
}

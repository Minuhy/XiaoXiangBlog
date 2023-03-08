package minuhy.xiaoxiang.blog.entity;

public class CommentEntity {
	int id; // 主键，唯一标识
	int active; // 评论是否存在，1：存在，0：已删除
	int blogId; // 博文ID，在哪篇博文下的评论
	int userId; // 评论发送者ID
	int replyId; // 被回复的评论ID
	String content; // 回复的内容
	long createTimestamp; // 回复时间
	long updateTimestamp; // 更新时间
	
	
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

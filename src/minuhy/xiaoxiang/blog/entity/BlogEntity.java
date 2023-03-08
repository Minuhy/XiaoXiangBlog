package minuhy.xiaoxiang.blog.entity;
/**
 * 对应着数据库中的表实体
 * 
 * @author y17mm
 *
 */
public class BlogEntity {
	int id; // 主键，唯一标识
	int active; // 是否有效，1有效，0无效
	int authorId; // 外键，发布者ID
	String title; // 博客标题
	String content; // 博客内容
	int readCount; // 访问量
	int likeCount; // 点赞量
	int commentCount; // 评论数
	int likeMsgSendCount;
	long createTimestamp; // 发布时间
	long updateTimestamp; // 修改时间
	
	public BlogEntity() {}
	
    public BlogEntity(int id, int active, int authorId, String title, String content, int readCount, int likeCount, int commentCount,int likeMsgSendCount, long createTimestamp, long updateTimestamp) {
        this.id = id;
        this.active = active;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likeMsgSendCount = likeMsgSendCount;
        this.createTimestamp = createTimestamp;
        this.updateTimestamp = updateTimestamp;
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
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int userId) {
		this.authorId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String context) {
		this.content = context;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
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
	
	
	
    public int getLikeMsgSendCount() {
		return likeMsgSendCount;
	}

	public void setLikeMsgSendCount(int likeMsgSendCount) {
		this.likeMsgSendCount = likeMsgSendCount;
	}

	@Override
    public String toString() {
        return "BlogEntity{" +
                "id=" + id +
                ", active=" + active +
                ", authorId=" + authorId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", readCount=" + readCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", likeMsgSendCount=" + likeMsgSendCount +
                ", createTimestamp=" + createTimestamp +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }
}

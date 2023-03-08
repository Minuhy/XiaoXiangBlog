package minuhy.xiaoxiang.blog.bean.blog;

import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;

public class MiniBlogBean {
	

//	"`t_blog`.`id` AS `id`, " + 
//	"`t_user`.`nick` AS `author`, " + 
//	"`t_blog`.`title` AS `title`, " + 
//	"`t_blog`.`content` AS `content`, " + 
//	"`t_blog`.`create_timestamp` AS `create_timestamp`, " + 
//	"`t_blog`.`read_count` AS `read_count`, " + 
//	"`t_blog`.`like_count` AS `like_count`, " + 
//	"`t_blog`.`comment_count` AS `comment_count` " + 
	

    int id; // 博客的ID
    String author; // 博客作者昵称
    String title; // 博客的标题
    String preview; // 博客的摘要预览 250字内
    String content; // 博客正文内容
    String dateTime; // 博客发表的时间
    int readCount; // 阅读计数
    int likeCount; // 喜欢计数
    int commentCount; // 评论计数

    public MiniBlogBean() {}
    
    public MiniBlogBean(int id, String author, String title, String preview, String content, String dateTime, int readCount, int likeCount, int commentCount) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.preview = preview;
        this.content = content;
        this.dateTime = dateTime;
        this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    @Override
    public String toString() {
        return "MiniBlogBean{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", preview='" + preview + '\'' +
                ", content='" + content + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", readCount=" + readCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                '}';
    }

	public MiniBlogBean build(int id, String author, String title, String content, int readCount, int likeCount,
			int commentCount, long createTimestamp) {
		this.id=id;
		this.author=author;
		this.title=title;
		this.content=content;
		this.preview = TextUtil.maxLen(TextUtil.delHtmlTag(content), 200); // 预览
        this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
		this.dateTime = TimeUtil.timestamp2DateTime(createTimestamp);
		return this;
	}
	
}

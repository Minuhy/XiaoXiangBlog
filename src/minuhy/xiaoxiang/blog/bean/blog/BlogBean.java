package minuhy.xiaoxiang.blog.bean.blog;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.LikeBean;
import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
/**
 * 
 * 2-23修改：取消评论，评论采用动态加载方式
 * @author xxxy1116
 *
 */
public class BlogBean {
	private static final Logger log = LoggerFactory.getLogger(BlogBean.class);
	
	int id; // 博客的ID
	int authorId; // 博客作者ID
	String title; // 博客的标题
	String dateTime; // 博客发表的时间
	String upDateTime; // 博客修改的时间
	String preview; // 博客的摘要预览 250字内
	String content; // 博客正文内容
	UserBean user; // 发表者
	LikeBean like;
	int readCount; // 阅读计数
	int likeCount; // 喜欢计数
	int commentCount; // 评论计数
	// CommentBean[] comments; // 评论列表
	
	public BlogBean() {}
	
	public BlogBean(int id,int authorId, String title, String dateTime,String upDateTime, String preview, String content, UserBean user, int readCount, int likeCount, int commentCount/*, CommentBean[] comments*/) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.dateTime = dateTime;
        this.upDateTime = upDateTime;
        this.preview = preview;
        this.content = content;
        this.user = user;
        this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        //this.comments = comments;
    }

	public BlogBean(BlogEntity blogEntity) {
		this.id = blogEntity.getId();
		this.authorId = blogEntity.getAuthorId();
		this.title = blogEntity.getTitle();
		this.dateTime = TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp());
		this.upDateTime = blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp());
		this.preview = TextUtil.maxLen(TextUtil.delHtmlTag(blogEntity.getContent()), 200); // 预览
		this.content = null; // String content
		this.user = null; // user
		this.readCount = blogEntity.getReadCount();
		this.likeCount = blogEntity.getLikeCount();
		this.commentCount = blogEntity.getCommentCount();
		// this.comments = null; // 评论列表
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUpDateTime() {
		return upDateTime;
	}

	public void setUpDateTime(String upDateTime) {
		this.upDateTime = upDateTime;
	}

	public LikeBean getLike() {
		return like;
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

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
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

//    public CommentBean[] getComments() {
//        return comments;
//    }
//
//    public void setComments(CommentBean[] comments) {
//        this.comments = comments;
//    }
    
    public boolean getData(String id) throws SQLException {
    	// return getData(id,true,true);
    	return getData(id,true);
    }
    
    
    
    public int getAuthorId() {
		return authorId;
	}

	public boolean getData(String id,boolean isGetAuthor/*,boolean isGetComment*/) throws SQLException {
        if (DebugConfig.isDebug) {
            log.debug("获取文章：{}",id);
        }
    	
    	// 查出文章信息
    	BlogDb blogDb = new BlogDb();
    	BlogEntity blogEntity = blogDb.getBlogById(id);
    	
    	if(blogEntity!=null) {
            if(isGetAuthor) {
            	if (DebugConfig.isDebug) {
                    log.debug("获取作者：{}",blogEntity.getAuthorId());
                }
    	    	// 查出作者信息
    	    	this.user = new UserBean();
    	    	this.user.getData(
    	    			String.valueOf(blogEntity.getAuthorId())
    	    			); // 发表者
            }else {
            	this.user = null;
            }
	    	
	    	// 填写信息
	    	this.id = blogEntity.getId(); // 博客的ID
	    	this.authorId = blogEntity.getAuthorId(); // 作者ID
	    	this.title= blogEntity.getTitle(); // 博客的标题
	    	this.dateTime= TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp()); // 博客发表的时间
			this.upDateTime = blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp());
	    	this.preview= TextUtil.maxLen(blogEntity.getContent(), 200); // 博客的摘要预览 200字内
	    	this.content= blogEntity.getContent(); // 博客正文内容
	    	this.readCount= blogEntity.getReadCount(); // 阅读计数
	    	this.likeCount= blogEntity.getLikeCount(); // 喜欢计数
	    	this.commentCount= blogEntity.getCommentCount(); // 评论计数
	    	
	    	
//	    	if(isGetComment) {
//	    		// CommentBean comments[]; // 评论列表
//	    	}else {
//	    		this.comments = null;
//	    	}
	    	
	    	return true;
    	}else {
    		return false;
    	}
    }
}

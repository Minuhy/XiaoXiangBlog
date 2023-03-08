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
 * 2-23�޸ģ�ȡ�����ۣ����۲��ö�̬���ط�ʽ
 * @author xxxy1116
 *
 */
public class BlogBean {
	private static final Logger log = LoggerFactory.getLogger(BlogBean.class);
	
	int id; // ���͵�ID
	int authorId; // ��������ID
	String title; // ���͵ı���
	String dateTime; // ���ͷ����ʱ��
	String upDateTime; // �����޸ĵ�ʱ��
	String preview; // ���͵�ժҪԤ�� 250����
	String content; // ������������
	UserBean user; // ������
	LikeBean like;
	int readCount; // �Ķ�����
	int likeCount; // ϲ������
	int commentCount; // ���ۼ���
	// CommentBean[] comments; // �����б�
	
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
		this.preview = TextUtil.maxLen(TextUtil.delHtmlTag(blogEntity.getContent()), 200); // Ԥ��
		this.content = null; // String content
		this.user = null; // user
		this.readCount = blogEntity.getReadCount();
		this.likeCount = blogEntity.getLikeCount();
		this.commentCount = blogEntity.getCommentCount();
		// this.comments = null; // �����б�
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
            log.debug("��ȡ���£�{}",id);
        }
    	
    	// ���������Ϣ
    	BlogDb blogDb = new BlogDb();
    	BlogEntity blogEntity = blogDb.getBlogById(id);
    	
    	if(blogEntity!=null) {
            if(isGetAuthor) {
            	if (DebugConfig.isDebug) {
                    log.debug("��ȡ���ߣ�{}",blogEntity.getAuthorId());
                }
    	    	// ���������Ϣ
    	    	this.user = new UserBean();
    	    	this.user.getData(
    	    			String.valueOf(blogEntity.getAuthorId())
    	    			); // ������
            }else {
            	this.user = null;
            }
	    	
	    	// ��д��Ϣ
	    	this.id = blogEntity.getId(); // ���͵�ID
	    	this.authorId = blogEntity.getAuthorId(); // ����ID
	    	this.title= blogEntity.getTitle(); // ���͵ı���
	    	this.dateTime= TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp()); // ���ͷ����ʱ��
			this.upDateTime = blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp());
	    	this.preview= TextUtil.maxLen(blogEntity.getContent(), 200); // ���͵�ժҪԤ�� 200����
	    	this.content= blogEntity.getContent(); // ������������
	    	this.readCount= blogEntity.getReadCount(); // �Ķ�����
	    	this.likeCount= blogEntity.getLikeCount(); // ϲ������
	    	this.commentCount= blogEntity.getCommentCount(); // ���ۼ���
	    	
	    	
//	    	if(isGetComment) {
//	    		// CommentBean comments[]; // �����б�
//	    	}else {
//	    		this.comments = null;
//	    	}
	    	
	    	return true;
    	}else {
    		return false;
    	}
    }
}

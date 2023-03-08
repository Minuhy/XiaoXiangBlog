package minuhy.xiaoxiang.blog.bean.user;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.LikeDb;
import minuhy.xiaoxiang.blog.entity.LikeEntity;

public class LikeBean {
	private static final Logger log = LoggerFactory.getLogger(LikeBean.class);
	
	int id; // 主键，唯一
	int state; // 状态：1点赞，-1反对，0取消
	int blogId; // 被点赞的博文
	int userId; // 点赞的用户
	
	public LikeBean() {}
	
    public LikeBean(int id, int state, int blogId, int userId) {
        this.id = id;
        this.state = state;
        this.blogId = blogId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LikeBean{" +
                "id=" + id +
                ", state=" + state +
                ", blogId=" + blogId +
                ", userId=" + userId +
                '}';
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
	
	public void getData(int userId,int blogId) {
		if (DebugConfig.isDebug) {
            log.debug("查找用户对此文的点赞状态：{}，{}",userId,blogId);
        }
		
		LikeDb likeDb = new LikeDb();
		try {
			LikeEntity likeEntity = likeDb.getLikeByUserIdAndBlogId(userId, blogId);
			if(likeEntity != null) {
				this.id = likeEntity.getId();
				this.state = likeEntity.getState();
				this.blogId = likeEntity.getBlogId();
				this.userId = likeEntity.getUserId();
				
				if (DebugConfig.isDebug) {
		            log.debug("点赞状态：{}",toString());
		        }
				
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询点赞数据时数据库出错：{}",e);
		}
		
		// 默认情况
		this.id = 0;
		this.state = 0;
		this.blogId = blogId;
		this.userId = userId;
		
		if (DebugConfig.isDebug) {
            log.debug("点赞状态默认");
        }
	}
}

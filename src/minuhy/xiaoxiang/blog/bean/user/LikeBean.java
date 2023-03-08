package minuhy.xiaoxiang.blog.bean.user;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.LikeDb;
import minuhy.xiaoxiang.blog.entity.LikeEntity;

public class LikeBean {
	private static final Logger log = LoggerFactory.getLogger(LikeBean.class);
	
	int id; // ������Ψһ
	int state; // ״̬��1���ޣ�-1���ԣ�0ȡ��
	int blogId; // �����޵Ĳ���
	int userId; // ���޵��û�
	
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
            log.debug("�����û��Դ��ĵĵ���״̬��{}��{}",userId,blogId);
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
		            log.debug("����״̬��{}",toString());
		        }
				
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("��ѯ��������ʱ���ݿ����{}",e);
		}
		
		// Ĭ�����
		this.id = 0;
		this.state = 0;
		this.blogId = blogId;
		this.userId = userId;
		
		if (DebugConfig.isDebug) {
            log.debug("����״̬Ĭ��");
        }
	}
}

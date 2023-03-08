package minuhy.xiaoxiang.blog.bean.admin;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.database.CommentDb;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;
import minuhy.xiaoxiang.blog.entity.CommentEntity;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.util.TimeUtil;

public class AdminEditBean {
	private static final Logger log = LoggerFactory.getLogger(AdminEditBean.class);
	int type;
	int page;
	int id;

	public AdminEditBean(int type, int page, int id) {
		this.type = type;
		this.page = page;
		this.id = id;
	}

	public AdminEditBean() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if(type<0) {
			type=0;
			if(DebugConfig.isDebug) {
				log.debug("type不能为负数");
			}
		}
		if(type>2) {
			type=2;
			if(DebugConfig.isDebug) {
				log.debug("type不能大于2");
			}
		}
		this.type = type;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		if(id<0) {
			id=0;
			if(DebugConfig.isDebug) {
				log.debug("id不能为负数");
			}
		}
		this.id = id;
	}

	public EditBean[] getData() throws SQLException {
		EditBean[] editBeans = null;
		if (this.type == 0) {
			// user
			UserDb userDb = new UserDb();
			UserEntity userEntity = userDb.getUserById(String.valueOf(this.id),true);
			if(userEntity == null) {
				userEntity = userDb.getUserById(String.valueOf(this.id),false);
			}
			
			editBeans = new EditBean[13];

			// int id; // ID，自动生成，唯一
			// String label, String name, String hint, String value
			editBeans[0] = new EditBean(false, "编号", "id", "编号，不可修改", String.valueOf(userEntity.getId()));

			// int active; // 账号是否激活：1激活，0禁用
			// String label, String name, String hint, String value
			editBeans[1] = new EditBean("状态", "active", "1激活，0禁用", userEntity.getActive());
			editBeans[1].setValChoose(new String[]{"1","激活","0","冻结"});
			
			// String passwd; // 密码，登录用，MD5加密
			// String label, String name, String hint, String value
			editBeans[2] = new EditBean("密码", "passwd", "（未修改）", "");
			editBeans[2].setType("password");
			
			// int role; // 角色，0：普通，1：管理员
			// String label, String name, String hint, String value
			editBeans[3] = new EditBean("角色", "role", "0：普通，1：管理员", userEntity.getRole());
			editBeans[3].setValChoose(new String[]{"0","普通","1","管理员"});
			
			// String nick; // 昵称
			// String label, String name, String hint, String value
			editBeans[4] = new EditBean("昵称", "nick", "至多24字", userEntity.getNick());

			// String signature; // 签名
			// String label, String name, String hint, String value
			editBeans[5] = new EditBean("签名", "signature", "至多60字", userEntity.getSignature());

			// int sex; // 性别，0：未设置，1：男，2：女
			// String label, String name, String hint, String value
			editBeans[6] = new EditBean("性别", "sex", "0：未设置，1：男，2：女", userEntity.getSex());
			editBeans[6].setValChoose(new String[]{"0","未设置","1","男","2","女"});
			
			// String hometown; // 家乡
			// String label, String name, String hint, String value
			editBeans[7] = new EditBean("家乡", "hometown", "至多60字", userEntity.getHometown());

			// String link; // 联系方式
			// String label, String name, String hint, String value
			editBeans[8] = new EditBean("联系方式", "link", "至多30字", userEntity.getLink());

			// int avatar; // 头像ID
			// String label, String name, String hint, String value
			editBeans[9] = new EditBean("头像编号", "avatar", "1-138", userEntity.getAvatar());
			editBeans[9].setType("number");
			
			// int blogCount; // 博客数量计数
			// String label, String name, String hint, String value
			editBeans[10] = new EditBean("博客数量计数", "blogCount", "大于等于 0", userEntity.getBlogCount());
			editBeans[10].setType("number");
			
			// int blogReadCount; // 博客阅读计数
			// String label, String name, String hint, String value
			editBeans[11] = new EditBean("博客被阅读计数", "blogReadCount", "大于等于 0", userEntity.getBlogReadCount());
			editBeans[11].setType("number");
			
			// int blogLikeCount; // 博客被点赞计数
			// String label, String name, String hint, String value
			editBeans[12] = new EditBean("博客被点赞计数", "blogLikeCount", "大于等于 0", userEntity.getBlogLikeCount());
			editBeans[12].setType("number");
			
		} else if (this.type == 1) {
			// blog
			BlogDb blogDb = new BlogDb();
			
			BlogEntity blogEntity = blogDb.getBlogById(String.valueOf(this.id),false);
			if(blogEntity == null) {
				blogEntity = blogDb.getBlogById(String.valueOf(this.id),true);
			}
			
			editBeans = new EditBean[10];

			// int id; // 主键，唯一标识
			// String label, String name, String hint, String value
			editBeans[0] = new EditBean(false, "编号", "id", "编号，不可修改", String.valueOf(blogEntity.getId()));

			// int active; // 是否有效，1有效，0无效
			// String label, String name, String hint, String value
			editBeans[1] = new EditBean("状态", "active", "是否有效，1有效，0无效", blogEntity.getActive());
			editBeans[1].setValChoose(new String[]{"1","正常","0","隐藏"});
			
			// int authorId; // 外键，发布者ID
			// String label, String name, String hint, String value
			editBeans[2] = new EditBean("作者编号", "authorId", "发布者编号", blogEntity.getAuthorId());
			editBeans[2].setType("number");
			
			// String title; // 博客标题
			// String label, String name, String hint, String value
			editBeans[3] = new EditBean("标题", "title", "至多200字", blogEntity.getTitle());

			// String content; // 博客内容
			// String label, String name, String hint, String value
			editBeans[4] = new EditBean("内容", "content", "到列表页点修改", "");

			// int readCount; // 访问量
			// String label, String name, String hint, String value
			editBeans[5] = new EditBean("访问量", "readCount", "大于等于 0", blogEntity.getReadCount());
			editBeans[5].setType("number");
			
			// int likeCount; // 点赞量
			// String label, String name, String hint, String value
			editBeans[6] = new EditBean("点赞量", "likeCount", "大于等于 0", blogEntity.getLikeCount());
			editBeans[6].setType("number");
			
			// int commentCount; // 评论数
			// String label, String name, String hint, String value
			editBeans[7] = new EditBean("评论数", "commentCount", "大于等于 0", blogEntity.getCommentCount());
			editBeans[7].setType("number");
			
			// long createTimestamp; // 发布时间
			// String label, String name, String hint, String value
			editBeans[8] = new EditBean("发布时间", "createTimestamp", "格式：yyyy-MM-dd HH:mm 或者 时间戳",
					TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp()));
			editBeans[8].setType("datetime-local");
			
			// long updateTimestamp; // 修改时间
			// String label, String name, String hint, String value
			editBeans[9] = new EditBean("修改时间", "updateTimestamp", "格式：yyyy-MM-dd HH:mm 或者 时间戳（默认0）",
					blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp()));
			editBeans[9].setType("datetime-local");
			
		} else if (this.type == 2) {
			// comment
			CommentDb commentDb = new CommentDb();
			CommentEntity commentEntity = commentDb.getCommentById(this.id);

			editBeans = new EditBean[8];

			// int id; // 主键，唯一标识
			// String label, String name, String hint, String value
			editBeans[0] = new EditBean(false, "编号", "id", "编号，不可修改", String.valueOf(commentEntity.getId()));

			// int active; // 评论是否存在，1：存在，0：已删除
			// String label, String name, String hint, String value
			editBeans[1] = new EditBean("状态", "active", "1：存在，0：已删除", commentEntity.getActive());
			editBeans[1].setValChoose(new String[]{"1","正常","0","隐藏"});
			
			// int blogId; // 博文ID，在哪篇博文下的评论
			// String label, String name, String hint, String value
			editBeans[2] = new EditBean("所属博文编号", "blogId", "大于 0", commentEntity.getBlogId());
			editBeans[2].setType("number");
			
			// int userId; // 评论发送者ID
			// String label, String name, String hint, String value
			editBeans[3] = new EditBean("评论发送者编号", "userId", "大于 0", commentEntity.getUserId());
			editBeans[3].setType("number");
			
			// int replyId; // 被回复的评论ID
			// String label, String name, String hint, String value
			editBeans[4] = new EditBean("被回复的评论编号", "replyId", "大于 0", commentEntity.getReplyId());
			editBeans[4].setType("number");
			
			// String content; // 回复的内容
			// String label, String name, String hint, String value
			editBeans[5] = new EditBean("回复的内容", "content", "至多两千字", commentEntity.getContent());

			// long createTimestamp; // 回复时间
			// String label, String name, String hint, String value
			editBeans[6] = new EditBean("回复时间", "createTimestamp", "格式：yyyy-MM-dd HH:mm 或者 时间戳",
					TimeUtil.timestamp2DateTime(commentEntity.getCreateTimestamp()));
			editBeans[6].setType("datetime-local");
			
			// long updateTimestamp; // 更新时间
			// String label, String name, String hint, String value
			editBeans[7] = new EditBean("更新时间", "updateTimestamp", "格式：yyyy-MM-dd HH:mm 或者 时间戳（默认0）",
					commentEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(commentEntity.getUpdateTimestamp()));
			editBeans[7].setType("datetime-local");
			
		}
		return editBeans;
	}

	@Override
	public String toString() {
		return "AdminEditBean{" + "type=" + type + ", page=" + page + ", id=" + id + '}';
	}
	
	

}
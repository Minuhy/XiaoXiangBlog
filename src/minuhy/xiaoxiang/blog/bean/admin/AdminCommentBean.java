package minuhy.xiaoxiang.blog.bean.admin;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.bean.PaginationBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.AdminDb;
import minuhy.xiaoxiang.blog.entity.CommentEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class AdminCommentBean extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(AdminCommentBean.class);
	PaginationBean paginationBean;
	Map<Integer, CommentEntity[]> cache;

	public AdminCommentBean() {
		cache = new HashMap<>();
		paginationBean = new PaginationBean();
	}

	public int getCurrentPage() {
		if (paginationBean == null) {
			return 0;
		}
		return paginationBean.getCurrent();
	}

	public PaginationBean getPaginationBean() {
		return paginationBean;
	}

	public int getTotal() {
		if (paginationBean == null) {
			return 0;
		}
		return paginationBean.getTotal();
	}

	public CommentEntity[] getData(int page) throws SQLException {
		boolean refresh = false;
		AdminDb adminDb = new AdminDb();
		if (getTotal() < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = adminDb.getCommentTotal();
			int total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);

			paginationBean.setTotal(total);
			paginationBean.setCurrent(page + 1);

			if (DebugConfig.isDebug) {
				log.debug("页数：{}-{}", total, page);
			}
		}

		if (page < 0 || page > getTotal()) {
			if (DebugConfig.isDebug) {
				log.debug("页面数据不正确：{}-{}，{}", 0, getTotal(), page);
			}
			return null;
		}

		// 如果没到刷新时候，并且缓存中有数据
		if (!isCanRefresh() && cache.containsKey(page)) {
			return cache.get(page);
		}

		// 从数据库中拿数据
		CommentEntity[] commentEntities = adminDb.getCommentInfoByPageOrderByTime(page);
		if (commentEntities != null && commentEntities.length > 0) {
			cache.put(page, commentEntities);
		}

		// 设置刷新时间
		if (refresh) {
			refresh();
		}

		return commentEntities;
	}

	/**
	 * 编辑评论
	 **/
	public String editComment(HttpServletRequest req) {

		CommentEntity entity = new CommentEntity();

		String id = req.getParameter("id");
		// int id; // 主键，唯一标识
		// String label, String name, String hint, String value
		// editBeans[0] = new EditBean(false, "编号", "id", "编号，不可修改",
		// String.valueOf(commentEntity.getId()));
		try {
			int i = Integer.parseInt(id);
			if (i < 1) {
				throw new NumberFormatException("编号范围不受支持");
			}
			entity.setId(i);
		} catch (Exception e) {
			return "编号有误：" + e.getMessage();
		}

		String active = req.getParameter("active");
		// int active; // 评论是否存在，1：存在，0：已删除
		// String label, String name, String hint, String value
		// editBeans[1] = new EditBean("状态", "active", "1：存在，0：已删除",
		// commentEntity.getActive());
		// editBeans[1].setValChoose(new String[]{"1","正常","0","隐藏"});
		if (active == null) {
			return "缺少状态参数";
		}
		if (!active.equals("1") && !active.equals("0")) {
			return "状态参数错误";
		}
		entity.setActive(Integer.parseInt(active));

		String blogId = req.getParameter("blogId");
		// int blogId; // 博文ID，在哪篇博文下的评论
		// String label, String name, String hint, String value
		// editBeans[2] = new EditBean("所属博文编号", "blogId", "大于 0",
		// commentEntity.getBlogId());
		// editBeans[2].setType("number");
		try {
			int i = Integer.parseInt(blogId);
			if (i < 0) {
				throw new NumberFormatException("编号不受支持");
			}
			entity.setBlogId(i);
		} catch (Exception e) {
			return "博文编号有误：" + e.getMessage();
		}

		String userId = req.getParameter("userId");
		// int userId; // 评论发送者ID
		// String label, String name, String hint, String value
		// editBeans[3] = new EditBean("评论发送者编号", "userId", "大于 0",
		// commentEntity.getUserId());
		// editBeans[3].setType("number");
		try {
			int i = Integer.parseInt(userId);
			if (i < 0) {
				throw new NumberFormatException("编号不受支持");
			}
			entity.setUserId(i);
		} catch (Exception e) {
			return "作者编号有误：" + e.getMessage();
		}

		String replyId = req.getParameter("replyId");
		// int replyId; // 被回复的评论ID
		// String label, String name, String hint, String value
		// editBeans[4] = new EditBean("被回复的评论编号", "replyId", "大于 0",
		// commentEntity.getReplyId());
		// editBeans[4].setType("number");
		try {
			int i = Integer.parseInt(replyId);
			if (i < 0) {
				throw new NumberFormatException("编号不受支持");
			}
			entity.setReplyId(i);
		} catch (Exception e) {
			return "被回复评论编号有误：" + e.getMessage();
		}

		String content = req.getParameter("content");
		// String content; // 回复的内容
		// String label, String name, String hint, String value
		// editBeans[5] = new EditBean("回复的内容", "content", "至多两千字",
		// commentEntity.getContent());
		if (content == null) {
			return "缺少评论内容参数";
		}
		if (content.length() > 2000 || content.length() < 1) {
			return "评论内容长度不正确（一到两千字）";
		}
		entity.setContent(content);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String createTimestamp = req.getParameter("createTimestamp");
		// long createTimestamp; // 回复时间
		// String label, String name, String hint, String value
		// editBeans[6] = new EditBean("回复时间", "createTimestamp", "格式：yyyy-MM-dd HH:mm
		// 或者 时间戳",
		// TimeUtil.timestamp2DateTime(commentEntity.getCreateTimestamp()));
		// editBeans[6].setType("datetime-local");
		if (createTimestamp != null) {
			try {
				createTimestamp = createTimestamp.replace('T', ' ');
				long t = format.parse(createTimestamp).getTime();
				if (t < 0) {
					throw new ParseException("时间不能早于1970年", 0);
				}
				entity.setCreateTimestamp(t);
			} catch (ParseException e) {
				return "创建时间有误：" + e.getMessage();
			}
		} else {
			return "缺少创建时间参数";
		}

		String updateTimestamp = req.getParameter("updateTimestamp");
		// long updateTimestamp; // 更新时间
		// String label, String name, String hint, String value
		// editBeans[7] = new EditBean("更新时间", "updateTimestamp", "格式：yyyy-MM-dd HH:mm
		// 或者 时间戳（默认0）",
		// commentEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(commentEntity.getUpdateTimestamp()));
		// editBeans[7].setType("datetime-local");
		if (updateTimestamp == null || updateTimestamp.length() < 1) {
			entity.setUpdateTimestamp(0);
		} else {
			try {
				updateTimestamp = updateTimestamp.replace('T', ' ');
				long t = format.parse(updateTimestamp).getTime();
				if (t < 0) {
					throw new ParseException("时间不能早于1970年", 0);
				}
				entity.setUpdateTimestamp(t);
			} catch (ParseException e) {
				return "修改时间有误：" + e.getMessage();
			}
		}

		try {
			AdminDb adminDb = new AdminDb();
			if (!adminDb.editComment(entity)) {
				return "资料未更改";
			}
		} catch (SQLException e) {
			log.error("写入数据库出错:" + e);
			return "数据库错误";
		}

		return null;
	}


}

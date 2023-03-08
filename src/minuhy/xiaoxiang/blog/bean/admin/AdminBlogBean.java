package minuhy.xiaoxiang.blog.bean.admin;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.bean.PaginationBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.AdminDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class AdminBlogBean extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(AdminBlogBean.class);
	PaginationBean paginationBean;
	Map<Integer, BlogEntity[]> cache;

	public AdminBlogBean() {
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

	/**
	 * 从数据库中分页取出博客数据
	 * 
	 * @param page 页数
	 * @return 博客列表
	 * @throws SQLException SQL错误
	 */
	public BlogEntity[] getData(int page) throws SQLException {
		boolean refresh = false;
		AdminDb adminDb = new AdminDb();
		if (getTotal() < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = adminDb.getBlogTotal();
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
		BlogEntity[] blogEntities = adminDb.getBlogInfoByPageOrderByTime(page);
		if (blogEntities != null && blogEntities.length > 0) {
			cache.put(page, blogEntities);
		}

		// 设置刷新时间
		if (refresh) {
			refresh();
		}

		return blogEntities;
	}

	/**
	 * 编辑博客
	 **/
	public String editBlog(HttpServletRequest req) {

		BlogEntity entity = new BlogEntity();

		String id = req.getParameter("id");
		// int id; // 主键，唯一标识
		// String label, String name, String hint, String value
		// editBeans[0] = new EditBean(false, "编号", "id", "编号，不可修改",
		// String.valueOf(blogEntity.getId()));
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
		// int active; // 是否有效，1有效，0无效
		// String label, String name, String hint, String value
		// editBeans[1] = new EditBean("状态", "active", "是否有效，1有效，0无效",
		// blogEntity.getActive());
		// editBeans[1].setValChoose(new String[]{"1","正常","0","隐藏"});
		if (active == null) {
			return "缺少状态参数";
		}
		if (!active.equals("1") && !active.equals("0")) {
			return "状态参数错误";
		}
		entity.setActive(Integer.parseInt(active));

		String authorId = req.getParameter("authorId");
		// int authorId; // 外键，发布者ID
		// String label, String name, String hint, String value
		// editBeans[2] = new EditBean("作者编号", "authorId", "发布者编号",
		// blogEntity.getAuthorId());
		// editBeans[2].setType("number");
		try {
			int i = Integer.parseInt(authorId);
			if (i < 0) {
				throw new NumberFormatException("作者编号不受支持");
			}
			entity.setAuthorId(i);
		} catch (Exception e) {
			return "作者编号有误：" + e.getMessage();
		}

		String title = req.getParameter("title");
		// String title; // 博客标题
		// String label, String name, String hint, String value
		// editBeans[3] = new EditBean("标题", "title", "至多200字", blogEntity.getTitle());
		if (title == null) {
			return "缺少标题参数";
		}
		if (title.length() > 200 || title.length() < 1) {
			return "标题长度不正确（1-200字）";
		}
		entity.setTitle(title);

		String content = req.getParameter("content");
		// String content; // 博客内容
		// String label, String name, String hint, String value
		// editBeans[4] = new EditBean("内容", "content", "到列表页点修改", "");
		if (content != null && content.length() > 0) {
			return "博文内容数据请到博文编辑页修改";
		}

		String readCount = req.getParameter("readCount");
		// int readCount; // 访问量
		// String label, String name, String hint, String value
		// editBeans[5] = new EditBean("访问量", "readCount", "大于等于 0",
		// blogEntity.getReadCount());
		// editBeans[5].setType("number");
		try {
			int i = Integer.parseInt(readCount);
			if (i < 0) {
				throw new NumberFormatException("不能为负数");
			}
			entity.setReadCount(i);
		} catch (Exception e) {
			return "博文访问量有误：" + e.getMessage();
		}

		String likeCount = req.getParameter("likeCount");
		// int likeCount; // 点赞量
		// String label, String name, String hint, String value
		// editBeans[6] = new EditBean("点赞量", "likeCount", "大于等于 0",
		// blogEntity.getLikeCount());
		// editBeans[6].setType("number");
		try {
			int i = Integer.parseInt(likeCount);
			if (i < 0) {
				throw new NumberFormatException("不能为负数");
			}
			entity.setLikeCount(i);
		} catch (Exception e) {
			return "博文点赞量有误：" + e.getMessage();
		}

		String commentCount = req.getParameter("commentCount");
		// int commentCount; // 评论数
		// String label, String name, String hint, String value
		// editBeans[7] = new EditBean("评论数", "commentCount", "大于等于 0",
		// blogEntity.getCommentCount());
		// editBeans[7].setType("number");
		try {
			int i = Integer.parseInt(commentCount);
			if (i < 0) {
				throw new NumberFormatException("不能为负数");
			}
			entity.setCommentCount(i);
		} catch (Exception e) {
			return "博文评论量有误：" + e.getMessage();
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String createTimestamp = req.getParameter("createTimestamp"); // 2023-02-22T00:43
		// long createTimestamp; // 发布时间
		// String label, String name, String hint, String value
		// editBeans[8] = new EditBean("发布时间", "createTimestamp", "格式：yyyy-MM-dd HH:mm
		// 或者 时间戳",
		// TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp()));
		// editBeans[8].setType("datetime-local");
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

		String updateTimestamp = req.getParameter("updateTimestamp"); // 2023-02-22T00:59
		// long updateTimestamp; // 修改时间
		// String label, String name, String hint, String value
		// editBeans[9] = new EditBean("修改时间", "updateTimestamp", "格式：yyyy-MM-dd HH:mm
		// 或者 时间戳（默认0）",
		// blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp()));
		// editBeans[9].setType("datetime-local");
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
			if (!adminDb.editBlog(entity)) {
				return "资料未更改";
			}
		} catch (SQLException e) {
			log.error("写入数据库出错:" + e);
			return "数据库错误";
		}

		return null;
	}
}

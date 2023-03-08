package minuhy.xiaoxiang.blog.bean.blog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;

public class RankBlogsBean extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(RankBlogsBean.class);

	Map<String, Map<Integer, BlogBean[]>> cache;// 数据库缓存，页数，数据组
	Map<String, Integer> pageTotalCache;// 页数 缓存
	String rank;

	public RankBlogsBean() {
		cache = new HashMap<>();
		pageTotalCache = new HashMap<>();
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		if ((!"lik".equals(rank)) // 按点赞数
				&& (!"com".equals(rank)) // 按评论数
				&& (!"rea".equals(rank)) // 按浏览量
				&& (!"cre".equals(rank)) // 按发表时间
				&& (!"upd".equals(rank))) { // 按修改时间
			// 默认
			rank = "def";
		}
		this.rank = rank;
	}

	public int getTotal() {
		if (!pageTotalCache.containsKey(getRank())) {
			return 0;
		}
		return pageTotalCache.get(getRank());
	}
	
	public void setTotal(Integer total) {
		pageTotalCache.put(getRank(), total);
	}

	public BlogBean[] getDataByPage(int page) throws SQLException {

		boolean refresh = false;
		int total = 0;
		BlogDb blogDb = new BlogDb();
		if (getTotal() < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = blogDb.getBlogTotal();
			total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);
			setTotal(total);
		}

		if (page < 0 || page > total) {
			if (DebugConfig.isDebug) {
				log.debug("页面数据不正确：{}-{}，{}", 0, total, page);
			}
			return null;
		}

		// 如果没到刷新时候，并且缓存中有数据
		if(!isCanRefresh() && cache.containsKey(getRank()) && cache.get(getRank()).containsKey(page)) {
			return cache.get(getRank()).get(page);
		}

		// 从数据库中拿数据
		BlogBean[] bbs = new BlogBean[0];
		
		BlogEntity[] blogs = null;
		
		String sql = "ORDER BY `like_count` DESC,`read_count` DESC,`create_timestamp` DESC "; // 默认
		if("lik".equals(getRank())){ // 按点赞数
			sql = "ORDER BY `like_count` DESC";
    	}else if("com".equals(getRank())){ // 按评论数
			sql = "ORDER BY `comment_count` DESC";
		}else if("rea".equals(getRank())){ // 按浏览量
			sql = "ORDER BY `read_count` DESC";
		}else if("cre".equals(getRank())){ // 按发表时间
			sql = "ORDER BY `create_timestamp` DESC";
    	}else if("upd".equals(getRank())){ // 按修改时间
			sql = "ORDER BY `update_timestamp` DESC";
    	}

		blogs = blogDb.getBlogsByPageOrderBySql(page,sql);
    	
		if (DebugConfig.isDebug) {
			log.debug("排序规则：{}", getRank());
		}
		
		if (blogs != null && blogs.length > 0) {
			bbs = new BlogBean[blogs.length];

			for (int i = 0; i < bbs.length; i++) {
				bbs[i] = new BlogBean(blogs[i]);
			}

			Map<Integer, BlogBean[]> map = new HashMap<>();
			map.put(page, bbs);
			cache.put(getRank(),map);
		}

		// 设置刷新时间
		if (refresh) {
			refresh();
		}

		return bbs;
	}

}
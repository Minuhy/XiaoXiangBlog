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

public class TimeBlogsBean extends BeanTimeController{	
	private static final Logger log = LoggerFactory.getLogger(TimeBlogsBean.class);

	int userId; // 用户ID
	Map<Integer,BlogBean[]> ceche; // 数据库缓存，页数，数据组
	int total; // 总页数
	
	public TimeBlogsBean() {
		ceche = new HashMap<Integer,BlogBean[]>();
		total = 0;
		userId = -1;
	}
	
	public void setUserId(int userId) {
		if(userId!=this.userId) {
			this.userId = userId;
			setRefresh();
			ceche = new HashMap<Integer,BlogBean[]>();
		}
	}

	public int getTotal() {
		return total;
	}

	public BlogBean[] getDataByPage(int page) throws SQLException {
		if(userId < 1) {
			if(DebugConfig.isDebug) {
				log.debug("没有设置UserID就开始获取博文列表数据");
			}
			return null;
		}
		
		boolean refresh = false; 
		BlogDb blogDb = new BlogDb();
		if(total < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = blogDb.getBlogTotalByUserId(userId);
			total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n%DatabaseConfig.PAGE_ITEM_COUNT)==0?0:1);
		}
		
		if(page < 0 || page > total) {
			if(DebugConfig.isDebug) {
				log.debug("页面数据不正确：{}-{}，{}",0,total,page);
			}
			return null;
		}
		
		// 如果没到刷新时候，并且缓存中有数据
		if(!isCanRefresh() && ceche.containsKey(page)) {
			return ceche.get(page);
		}
		
		// 从数据库中拿数据
		BlogBean[] bbs = new BlogBean[0];
		BlogEntity[] blogs = blogDb.getUserBlogsByPageOrderByTime(userId, page);
		if(blogs!=null && blogs.length>0) {
			bbs = new BlogBean[blogs.length];
			
			for(int i=0;i<bbs.length;i++) {
				bbs[i] = new BlogBean(blogs[i]);
			}
			
			ceche.put(page, bbs);
		}
		
		// 设置刷新时间
		if(refresh) {
			refresh();
		}
		
		return bbs;
	}
	
}

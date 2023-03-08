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

public class SearchBlogsBean extends BeanTimeController{
	private static final Logger log = LoggerFactory.getLogger(SearchBlogsBean.class);

	
	Map<String,Map<Integer,MiniBlogBean[]>> keywordCeche; // 数据库缓存，页数，数据组
	Map<String,Integer> totalCeche;
	
	public SearchBlogsBean() {
		keywordCeche = new HashMap<String,Map<Integer,MiniBlogBean[]>>();
		totalCeche = new HashMap<String,Integer>();
	}
	
	public int getTotal(String keyword){
		if(totalCeche.containsKey(keyword)) {
			return totalCeche.get(keyword);
		}
		
		if(DebugConfig.isDebug) {
			log.debug("获取一个未统计的总数");
		}
		
		return 0;
	}
	
	public void setTotal(String keyword,int total) {
		totalCeche.put(keyword, total);
	}

	public MiniBlogBean[] getDataByPage(String keyword,int page) throws SQLException {
		
		boolean refresh = false; 
		BlogDb blogDb = new BlogDb();
		if(getTotal(keyword) < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = blogDb.getBlogTotalBySearchKeyword(keyword);
			int total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n%DatabaseConfig.PAGE_ITEM_COUNT)==0?0:1);
			setTotal(keyword, total);
		}
		
		if(page < 0 || page > getTotal(keyword)) {
			if(DebugConfig.isDebug) {
				log.debug("页面数据不正确：{}-{}，{}",0,getTotal(keyword),page);
			}
			return null;
		}
		
		
		// 如果没到刷新时候，并且缓存中有数据
		Map<Integer,MiniBlogBean[]> mapCeche = null;
		if(keywordCeche.containsKey(keyword)) {
			mapCeche = keywordCeche.get(keyword);
		}
		if(!isCanRefresh() && mapCeche!=null && mapCeche.containsKey(page)) {
			return mapCeche.get(page);
		}
		
		
		// 从数据库中拿数据
		MiniBlogBean[] bbs = blogDb.searchBlogByKeywordAndPagination(keyword, page);
		if(bbs!=null && bbs.length>0) {
			if(mapCeche==null) {
				mapCeche = new HashMap<Integer,MiniBlogBean[]>();
			}
			mapCeche.put(page, bbs);
			keywordCeche.put(keyword, mapCeche);
		}
		
		// 设置刷新时间
		if(refresh) {
			refresh();
		}
		
		return bbs;
	}
}

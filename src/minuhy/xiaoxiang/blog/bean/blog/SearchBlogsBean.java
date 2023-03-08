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

	
	Map<String,Map<Integer,MiniBlogBean[]>> keywordCeche; // ���ݿ⻺�棬ҳ����������
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
			log.debug("��ȡһ��δͳ�Ƶ�����");
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
			// ��ȡ��ҳ��
			refresh = true;
			int n = blogDb.getBlogTotalBySearchKeyword(keyword);
			int total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n%DatabaseConfig.PAGE_ITEM_COUNT)==0?0:1);
			setTotal(keyword, total);
		}
		
		if(page < 0 || page > getTotal(keyword)) {
			if(DebugConfig.isDebug) {
				log.debug("ҳ�����ݲ���ȷ��{}-{}��{}",0,getTotal(keyword),page);
			}
			return null;
		}
		
		
		// ���û��ˢ��ʱ�򣬲��һ�����������
		Map<Integer,MiniBlogBean[]> mapCeche = null;
		if(keywordCeche.containsKey(keyword)) {
			mapCeche = keywordCeche.get(keyword);
		}
		if(!isCanRefresh() && mapCeche!=null && mapCeche.containsKey(page)) {
			return mapCeche.get(page);
		}
		
		
		// �����ݿ���������
		MiniBlogBean[] bbs = blogDb.searchBlogByKeywordAndPagination(keyword, page);
		if(bbs!=null && bbs.length>0) {
			if(mapCeche==null) {
				mapCeche = new HashMap<Integer,MiniBlogBean[]>();
			}
			mapCeche.put(page, bbs);
			keywordCeche.put(keyword, mapCeche);
		}
		
		// ����ˢ��ʱ��
		if(refresh) {
			refresh();
		}
		
		return bbs;
	}
}

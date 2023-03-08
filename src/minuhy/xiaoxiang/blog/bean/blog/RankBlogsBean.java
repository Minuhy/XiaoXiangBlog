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

	Map<String, Map<Integer, BlogBean[]>> cache;// ���ݿ⻺�棬ҳ����������
	Map<String, Integer> pageTotalCache;// ҳ�� ����
	String rank;

	public RankBlogsBean() {
		cache = new HashMap<>();
		pageTotalCache = new HashMap<>();
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		if ((!"lik".equals(rank)) // ��������
				&& (!"com".equals(rank)) // ��������
				&& (!"rea".equals(rank)) // �������
				&& (!"cre".equals(rank)) // ������ʱ��
				&& (!"upd".equals(rank))) { // ���޸�ʱ��
			// Ĭ��
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
			// ��ȡ��ҳ��
			refresh = true;
			int n = blogDb.getBlogTotal();
			total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);
			setTotal(total);
		}

		if (page < 0 || page > total) {
			if (DebugConfig.isDebug) {
				log.debug("ҳ�����ݲ���ȷ��{}-{}��{}", 0, total, page);
			}
			return null;
		}

		// ���û��ˢ��ʱ�򣬲��һ�����������
		if(!isCanRefresh() && cache.containsKey(getRank()) && cache.get(getRank()).containsKey(page)) {
			return cache.get(getRank()).get(page);
		}

		// �����ݿ���������
		BlogBean[] bbs = new BlogBean[0];
		
		BlogEntity[] blogs = null;
		
		String sql = "ORDER BY `like_count` DESC,`read_count` DESC,`create_timestamp` DESC "; // Ĭ��
		if("lik".equals(getRank())){ // ��������
			sql = "ORDER BY `like_count` DESC";
    	}else if("com".equals(getRank())){ // ��������
			sql = "ORDER BY `comment_count` DESC";
		}else if("rea".equals(getRank())){ // �������
			sql = "ORDER BY `read_count` DESC";
		}else if("cre".equals(getRank())){ // ������ʱ��
			sql = "ORDER BY `create_timestamp` DESC";
    	}else if("upd".equals(getRank())){ // ���޸�ʱ��
			sql = "ORDER BY `update_timestamp` DESC";
    	}

		blogs = blogDb.getBlogsByPageOrderBySql(page,sql);
    	
		if (DebugConfig.isDebug) {
			log.debug("�������{}", getRank());
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

		// ����ˢ��ʱ��
		if (refresh) {
			refresh();
		}

		return bbs;
	}

}
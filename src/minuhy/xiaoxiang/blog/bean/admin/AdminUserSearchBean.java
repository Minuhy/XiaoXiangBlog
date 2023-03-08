package minuhy.xiaoxiang.blog.bean.admin;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.bean.PaginationBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.AdminDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class AdminUserSearchBean extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(AdminUserSearchBean.class);

	String keyWord;

	Map<String, Map<Integer, UserEntity[]>> keywordCeche; // ���ݿ⻺�棬ҳ����������
	Map<String, PaginationBean> totalCeche;

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
		keywordCeche = new HashMap<>();
		totalCeche = new HashMap<>();
	}

	public AdminUserSearchBean() {
	}

	public int getTotal() {

		if (keyWord != null) {
			if (totalCeche.containsKey(getKeyWord())) {
				return totalCeche.get(getKeyWord()).getTotal();
			}

			if (DebugConfig.isDebug) {
				log.debug("��ȡһ��δͳ�Ƶ�����");
			}

		}
		return 0;
	}

	public PaginationBean getPaginationBean() {

		if (keyWord != null) {
			if (totalCeche.containsKey(getKeyWord())) {
				return totalCeche.get(getKeyWord());
			}

			if (DebugConfig.isDebug) {
				log.debug("��ȡһ��δͳ�Ƶ�����");
			}

		}
		return new PaginationBean();
	}
	
	public UserEntity[] getDataBySearch(int page) throws SQLException {
		boolean refresh = false;
		AdminDb adminDb = new AdminDb();

		// ��ȡ��ҳ��
		if (getTotal() < 1 || isCanRefresh()) {
			// ��ȡ��ҳ��
			refresh = true;
			int n = adminDb.getUserSearchTotal(getKeyWord());
			int total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);

			PaginationBean paginationBean = getPaginationBean();
			
			paginationBean.setTotal(total);
			paginationBean.setCurrent(page + 1);
			totalCeche.put(getKeyWord(), paginationBean);

			if (DebugConfig.isDebug) {
				log.debug("ҳ����{}-{}", total, page);
			}
		}

		if (page < 0 || page > getTotal()) {
			if (DebugConfig.isDebug) {
				log.debug("ҳ�����ݲ���ȷ��{}-{}��{}", 0, getTotal(), page);
			}
			return null;
		}

		// ���û��ˢ��ʱ�򣬲��һ�����������
		Map<Integer, UserEntity[]> mapCeche = null;
		if (keywordCeche.containsKey(getKeyWord())) {
			mapCeche = keywordCeche.get(getKeyWord());
		}
		if (!isCanRefresh() && mapCeche != null && mapCeche.containsKey(page)) {
			return mapCeche.get(page);
		}

		// �����ݿ���������
		UserEntity[] entities = adminDb.searchUserInfoByPageOrderByTime(page, getKeyWord());
		if (entities != null && entities.length > 0) {
			if (mapCeche == null) {
				mapCeche = new HashMap<>();
			}
			mapCeche.put(page, entities);
			keywordCeche.put(getKeyWord(), mapCeche);
		}

		// ����ˢ��ʱ��
		if (refresh) {
			refresh();
		}

		return entities;
	}
}

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
	 * �����ݿ��з�ҳȡ����������
	 * 
	 * @param page ҳ��
	 * @return �����б�
	 * @throws SQLException SQL����
	 */
	public BlogEntity[] getData(int page) throws SQLException {
		boolean refresh = false;
		AdminDb adminDb = new AdminDb();
		if (getTotal() < 1 || isCanRefresh()) {
			// ��ȡ��ҳ��
			refresh = true;
			int n = adminDb.getBlogTotal();
			int total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);

			paginationBean.setTotal(total);
			paginationBean.setCurrent(page + 1);

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
		if (!isCanRefresh() && cache.containsKey(page)) {
			return cache.get(page);
		}

		// �����ݿ���������
		BlogEntity[] blogEntities = adminDb.getBlogInfoByPageOrderByTime(page);
		if (blogEntities != null && blogEntities.length > 0) {
			cache.put(page, blogEntities);
		}

		// ����ˢ��ʱ��
		if (refresh) {
			refresh();
		}

		return blogEntities;
	}

	/**
	 * �༭����
	 **/
	public String editBlog(HttpServletRequest req) {

		BlogEntity entity = new BlogEntity();

		String id = req.getParameter("id");
		// int id; // ������Ψһ��ʶ
		// String label, String name, String hint, String value
		// editBeans[0] = new EditBean(false, "���", "id", "��ţ������޸�",
		// String.valueOf(blogEntity.getId()));
		try {
			int i = Integer.parseInt(id);
			if (i < 1) {
				throw new NumberFormatException("��ŷ�Χ����֧��");
			}
			entity.setId(i);
		} catch (Exception e) {
			return "�������" + e.getMessage();
		}

		String active = req.getParameter("active");
		// int active; // �Ƿ���Ч��1��Ч��0��Ч
		// String label, String name, String hint, String value
		// editBeans[1] = new EditBean("״̬", "active", "�Ƿ���Ч��1��Ч��0��Ч",
		// blogEntity.getActive());
		// editBeans[1].setValChoose(new String[]{"1","����","0","����"});
		if (active == null) {
			return "ȱ��״̬����";
		}
		if (!active.equals("1") && !active.equals("0")) {
			return "״̬��������";
		}
		entity.setActive(Integer.parseInt(active));

		String authorId = req.getParameter("authorId");
		// int authorId; // �����������ID
		// String label, String name, String hint, String value
		// editBeans[2] = new EditBean("���߱��", "authorId", "�����߱��",
		// blogEntity.getAuthorId());
		// editBeans[2].setType("number");
		try {
			int i = Integer.parseInt(authorId);
			if (i < 0) {
				throw new NumberFormatException("���߱�Ų���֧��");
			}
			entity.setAuthorId(i);
		} catch (Exception e) {
			return "���߱������" + e.getMessage();
		}

		String title = req.getParameter("title");
		// String title; // ���ͱ���
		// String label, String name, String hint, String value
		// editBeans[3] = new EditBean("����", "title", "����200��", blogEntity.getTitle());
		if (title == null) {
			return "ȱ�ٱ������";
		}
		if (title.length() > 200 || title.length() < 1) {
			return "���ⳤ�Ȳ���ȷ��1-200�֣�";
		}
		entity.setTitle(title);

		String content = req.getParameter("content");
		// String content; // ��������
		// String label, String name, String hint, String value
		// editBeans[4] = new EditBean("����", "content", "���б�ҳ���޸�", "");
		if (content != null && content.length() > 0) {
			return "�������������뵽���ı༭ҳ�޸�";
		}

		String readCount = req.getParameter("readCount");
		// int readCount; // ������
		// String label, String name, String hint, String value
		// editBeans[5] = new EditBean("������", "readCount", "���ڵ��� 0",
		// blogEntity.getReadCount());
		// editBeans[5].setType("number");
		try {
			int i = Integer.parseInt(readCount);
			if (i < 0) {
				throw new NumberFormatException("����Ϊ����");
			}
			entity.setReadCount(i);
		} catch (Exception e) {
			return "���ķ���������" + e.getMessage();
		}

		String likeCount = req.getParameter("likeCount");
		// int likeCount; // ������
		// String label, String name, String hint, String value
		// editBeans[6] = new EditBean("������", "likeCount", "���ڵ��� 0",
		// blogEntity.getLikeCount());
		// editBeans[6].setType("number");
		try {
			int i = Integer.parseInt(likeCount);
			if (i < 0) {
				throw new NumberFormatException("����Ϊ����");
			}
			entity.setLikeCount(i);
		} catch (Exception e) {
			return "���ĵ���������" + e.getMessage();
		}

		String commentCount = req.getParameter("commentCount");
		// int commentCount; // ������
		// String label, String name, String hint, String value
		// editBeans[7] = new EditBean("������", "commentCount", "���ڵ��� 0",
		// blogEntity.getCommentCount());
		// editBeans[7].setType("number");
		try {
			int i = Integer.parseInt(commentCount);
			if (i < 0) {
				throw new NumberFormatException("����Ϊ����");
			}
			entity.setCommentCount(i);
		} catch (Exception e) {
			return "��������������" + e.getMessage();
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String createTimestamp = req.getParameter("createTimestamp"); // 2023-02-22T00:43
		// long createTimestamp; // ����ʱ��
		// String label, String name, String hint, String value
		// editBeans[8] = new EditBean("����ʱ��", "createTimestamp", "��ʽ��yyyy-MM-dd HH:mm
		// ���� ʱ���",
		// TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp()));
		// editBeans[8].setType("datetime-local");
		if (createTimestamp != null) {
			try {
				createTimestamp = createTimestamp.replace('T', ' ');
				long t = format.parse(createTimestamp).getTime();
				if (t < 0) {
					throw new ParseException("ʱ�䲻������1970��", 0);
				}
				entity.setCreateTimestamp(t);
			} catch (ParseException e) {
				return "����ʱ������" + e.getMessage();
			}
		} else {
			return "ȱ�ٴ���ʱ�����";
		}

		String updateTimestamp = req.getParameter("updateTimestamp"); // 2023-02-22T00:59
		// long updateTimestamp; // �޸�ʱ��
		// String label, String name, String hint, String value
		// editBeans[9] = new EditBean("�޸�ʱ��", "updateTimestamp", "��ʽ��yyyy-MM-dd HH:mm
		// ���� ʱ�����Ĭ��0��",
		// blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp()));
		// editBeans[9].setType("datetime-local");
		if (updateTimestamp == null || updateTimestamp.length() < 1) {
			entity.setUpdateTimestamp(0);
		} else {
			try {
				updateTimestamp = updateTimestamp.replace('T', ' ');
				long t = format.parse(updateTimestamp).getTime();
				if (t < 0) {
					throw new ParseException("ʱ�䲻������1970��", 0);
				}
				entity.setUpdateTimestamp(t);
			} catch (ParseException e) {
				return "�޸�ʱ������" + e.getMessage();
			}
		}

		try {
			AdminDb adminDb = new AdminDb();
			if (!adminDb.editBlog(entity)) {
				return "����δ����";
			}
		} catch (SQLException e) {
			log.error("д�����ݿ����:" + e);
			return "���ݿ����";
		}

		return null;
	}
}

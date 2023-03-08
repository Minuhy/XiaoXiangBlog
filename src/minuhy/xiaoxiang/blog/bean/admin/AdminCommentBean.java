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
			// ��ȡ��ҳ��
			refresh = true;
			int n = adminDb.getCommentTotal();
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
		CommentEntity[] commentEntities = adminDb.getCommentInfoByPageOrderByTime(page);
		if (commentEntities != null && commentEntities.length > 0) {
			cache.put(page, commentEntities);
		}

		// ����ˢ��ʱ��
		if (refresh) {
			refresh();
		}

		return commentEntities;
	}

	/**
	 * �༭����
	 **/
	public String editComment(HttpServletRequest req) {

		CommentEntity entity = new CommentEntity();

		String id = req.getParameter("id");
		// int id; // ������Ψһ��ʶ
		// String label, String name, String hint, String value
		// editBeans[0] = new EditBean(false, "���", "id", "��ţ������޸�",
		// String.valueOf(commentEntity.getId()));
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
		// int active; // �����Ƿ���ڣ�1�����ڣ�0����ɾ��
		// String label, String name, String hint, String value
		// editBeans[1] = new EditBean("״̬", "active", "1�����ڣ�0����ɾ��",
		// commentEntity.getActive());
		// editBeans[1].setValChoose(new String[]{"1","����","0","����"});
		if (active == null) {
			return "ȱ��״̬����";
		}
		if (!active.equals("1") && !active.equals("0")) {
			return "״̬��������";
		}
		entity.setActive(Integer.parseInt(active));

		String blogId = req.getParameter("blogId");
		// int blogId; // ����ID������ƪ�����µ�����
		// String label, String name, String hint, String value
		// editBeans[2] = new EditBean("�������ı��", "blogId", "���� 0",
		// commentEntity.getBlogId());
		// editBeans[2].setType("number");
		try {
			int i = Integer.parseInt(blogId);
			if (i < 0) {
				throw new NumberFormatException("��Ų���֧��");
			}
			entity.setBlogId(i);
		} catch (Exception e) {
			return "���ı������" + e.getMessage();
		}

		String userId = req.getParameter("userId");
		// int userId; // ���۷�����ID
		// String label, String name, String hint, String value
		// editBeans[3] = new EditBean("���۷����߱��", "userId", "���� 0",
		// commentEntity.getUserId());
		// editBeans[3].setType("number");
		try {
			int i = Integer.parseInt(userId);
			if (i < 0) {
				throw new NumberFormatException("��Ų���֧��");
			}
			entity.setUserId(i);
		} catch (Exception e) {
			return "���߱������" + e.getMessage();
		}

		String replyId = req.getParameter("replyId");
		// int replyId; // ���ظ�������ID
		// String label, String name, String hint, String value
		// editBeans[4] = new EditBean("���ظ������۱��", "replyId", "���� 0",
		// commentEntity.getReplyId());
		// editBeans[4].setType("number");
		try {
			int i = Integer.parseInt(replyId);
			if (i < 0) {
				throw new NumberFormatException("��Ų���֧��");
			}
			entity.setReplyId(i);
		} catch (Exception e) {
			return "���ظ����۱������" + e.getMessage();
		}

		String content = req.getParameter("content");
		// String content; // �ظ�������
		// String label, String name, String hint, String value
		// editBeans[5] = new EditBean("�ظ�������", "content", "������ǧ��",
		// commentEntity.getContent());
		if (content == null) {
			return "ȱ���������ݲ���";
		}
		if (content.length() > 2000 || content.length() < 1) {
			return "�������ݳ��Ȳ���ȷ��һ����ǧ�֣�";
		}
		entity.setContent(content);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String createTimestamp = req.getParameter("createTimestamp");
		// long createTimestamp; // �ظ�ʱ��
		// String label, String name, String hint, String value
		// editBeans[6] = new EditBean("�ظ�ʱ��", "createTimestamp", "��ʽ��yyyy-MM-dd HH:mm
		// ���� ʱ���",
		// TimeUtil.timestamp2DateTime(commentEntity.getCreateTimestamp()));
		// editBeans[6].setType("datetime-local");
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

		String updateTimestamp = req.getParameter("updateTimestamp");
		// long updateTimestamp; // ����ʱ��
		// String label, String name, String hint, String value
		// editBeans[7] = new EditBean("����ʱ��", "updateTimestamp", "��ʽ��yyyy-MM-dd HH:mm
		// ���� ʱ�����Ĭ��0��",
		// commentEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(commentEntity.getUpdateTimestamp()));
		// editBeans[7].setType("datetime-local");
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
			if (!adminDb.editComment(entity)) {
				return "����δ����";
			}
		} catch (SQLException e) {
			log.error("д�����ݿ����:" + e);
			return "���ݿ����";
		}

		return null;
	}


}

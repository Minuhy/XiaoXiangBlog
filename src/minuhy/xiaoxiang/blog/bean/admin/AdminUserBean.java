package minuhy.xiaoxiang.blog.bean.admin;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.bean.PaginationBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.AdminDb;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.util.EncryptionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class AdminUserBean extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(AdminUserBean.class);
	PaginationBean paginationBean;
	Map<Integer, UserEntity[]> cache;


	public AdminUserBean() {
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
	 * ��ȡ����
	 * 
	 * @param page ��0��ʼ
	 * @return
	 * @throws SQLException
	 */
	public UserEntity[] getData(int page) throws SQLException {
		boolean refresh = false;
		AdminDb adminDb = new AdminDb();
		if (getTotal() < 1 || isCanRefresh()) {
			// ��ȡ��ҳ��
			refresh = true;
			int n = adminDb.getUserTotal();
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
		UserEntity[] userEntities = adminDb.getUserInfoByPageOrderByTime(page);
		if (userEntities != null && userEntities.length > 0) {
			cache.put(page, userEntities);
		}

		// ����ˢ��ʱ��
		if (refresh) {
			refresh();
		}

		return userEntities;
	}

	/**
	 * �༭�û�
	 **/
	public String editUser(HttpServletRequest req) {
		UserEntity entity = new UserEntity();

		String id = req.getParameter("id");
		// int id; // ID���Զ����ɣ�Ψһ
		// String label, String name, String hint, String value
		// editBeans[0] = new EditBean(false, "���", "id", "��ţ������޸�",
		// String.valueOf(userEntity.getId()));
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
		// int active; // �˺��Ƿ񼤻1���0����
		// String label, String name, String hint, String value
		// editBeans[1] = new EditBean("״̬", "active", "1���0����",
		// userEntity.getActive());
		// editBeans[1].setValChoose(new String[]{"1","����","0","����"});
		if (active == null) {
			return "ȱ�ټ���״̬����";
		}
		if (!active.equals("1") && !active.equals("0")) {
			return "����״̬��������";
		}
		entity.setActive(Integer.parseInt(active));

		String passwd = req.getParameter("passwd");
		// String passwd; // ���룬��¼�ã�MD5����
		// String label, String name, String hint, String value
		// editBeans[2] = new EditBean("����", "passwd", "��δ�޸ģ�", "");
		// editBeans[2].setType("password");
		if (passwd != null && passwd.length() > 0) {
			if (passwd.length() > 5) {
				// ��������
				try {
					UserDb userDb = new UserDb();
					String account = userDb.getAccountById(id);
					if (account == null) {
						throw new Exception("�鲻���˺�");
					}
					passwd = EncryptionUtil.EncodePasswd(account, passwd);
					entity.setPasswd(passwd);
				} catch (Exception e) {
					return "��������ʱ����" + e.getMessage();
				}
			} else {
				return "���볤��̫��";
			}
		} else {
			passwd = null;
		}

		String role = req.getParameter("role");
		// int role; // ��ɫ��0����ͨ��1������Ա
		// String label, String name, String hint, String value
		// editBeans[3] = new EditBean("��ɫ", "role", "0����ͨ��1������Ա",
		// userEntity.getRole());
		// editBeans[3].setValChoose(new String[]{"0","��ͨ","1","����Ա"});
		if (role == null) {
			return "ȱ�ٽ�ɫ����";
		}
		if (!role.equals("1") && !role.equals("0")) {
			return "��ɫ��������";
		}
		entity.setRole(Integer.parseInt(role));

		String nick = req.getParameter("nick");
		// String nick; // �ǳ�
		// String label, String name, String hint, String value
		// editBeans[4] = new EditBean("�ǳ�", "nick", "����24��", userEntity.getNick());
		if (nick == null) {
			return "ȱ���ǳƲ���";
		}
		if (nick.length() < 1 || nick.length() > 24) {
			return "�ǳƳ��Ȳ���ȷ��1-24�֣�";
		}
		entity.setNick(nick);

		String signature = req.getParameter("signature");
		// String signature; // ǩ��
		// String label, String name, String hint, String value
		// editBeans[5] = new EditBean("ǩ��", "signature", "����60��",
		// userEntity.getSignature());
		if (signature == null) {
			signature = "";
		}
		if (signature.length() > 60) {
			return "ǩ�����Ȳ���ȷ������60�֣�";
		}
		entity.setSignature(signature);

		String sex = req.getParameter("sex");
		// int sex; // �Ա�0��δ���ã�1���У�2��Ů
		// String label, String name, String hint, String value
		// editBeans[6] = new EditBean("�Ա�", "sex", "0��δ���ã�1���У�2��Ů",
		// userEntity.getSex());
		// editBeans[6].setValChoose(new String[]{"0","δ����","1","��","2","Ů"});
		if (sex == null) {
			return "ȱ���Ա����";
		}
		if (!sex.equals("1") && !sex.equals("0") && !sex.equals("2")) {
			return "�Ա��������";
		}
		entity.setSex(Integer.parseInt(sex));

		String hometown = req.getParameter("hometown");
		// String hometown; // ����
		// String label, String name, String hint, String value
		// editBeans[7] = new EditBean("����", "hometown", "����60��",
		// userEntity.getHometown());
		if (hometown == null) {
			hometown = "";
		}
		if (hometown.length() > 60) {
			return "���糤�Ȳ���ȷ������60�֣�";
		}
		entity.setHometown(hometown);

		String link = req.getParameter("link");
		// String link; // ��ϵ��ʽ
		// String label, String name, String hint, String value
		// editBeans[8] = new EditBean("��ϵ��ʽ", "link", "����30��", userEntity.getLink());
		if (link == null) {
			link = "";
		}
		if (link.length() > 30) {
			return "��ϵ��ʽ���Ȳ���ȷ������30�֣�";
		}
		entity.setLink(link);

		String avatar = req.getParameter("avatar");
		// int avatar; // ͷ��ID
		// String label, String name, String hint, String value
		// editBeans[9] = new EditBean("ͷ����", "avatar", "1-138",
		// userEntity.getAvatar());
		// editBeans[9].setType("number");
		try {
			int i = Integer.parseInt(avatar);
			if (i < 1 || i > 138) {
				throw new NumberFormatException("ͷ���Ų���֧��");
			}
			entity.setAvatar(i);
		} catch (Exception e) {
			return "ͷ��������" + e.getMessage();
		}

		String blogCount = req.getParameter("blogCount");
		// int blogCount; // ������������
		// String label, String name, String hint, String value
		// editBeans[10] = new EditBean("������������", "blogCount", "���ڵ��� 0",
		// userEntity.getBlogCount());
		// editBeans[10].setType("number");
		try {
			int i = Integer.parseInt(blogCount);
			if (i < 0) {
				throw new NumberFormatException("����Ϊ����");
			}
			entity.setBlogCount(i);
		} catch (Exception e) {
			return "������������" + e.getMessage();
		}

		String blogReadCount = req.getParameter("blogReadCount");
		// int blogReadCount; // �����Ķ�����
		// String label, String name, String hint, String value
		// editBeans[11] = new EditBean("���ͱ��Ķ�����", "blogReadCount", "���ڵ��� 0",
		// userEntity.getBlogReadCount());
		// editBeans[11].setType("number");
		try {
			int i = Integer.parseInt(blogReadCount);
			if (i < 0) {
				throw new NumberFormatException("����Ϊ����");
			}
			entity.setBlogReadCount(i);
		} catch (Exception e) {
			return "�����Ķ�������" + e.getMessage();
		}

		String blogLikeCount = req.getParameter("blogLikeCount");
		// int blogLikeCount; // ���ͱ����޼���
		// String label, String name, String hint, String value
		// editBeans[12] = new EditBean("���ͱ����޼���", "blogLikeCount", "���ڵ��� 0",
		// userEntity.getBlogLikeCount());
		// editBeans[12].setType("number");
		try {
			int i = Integer.parseInt(blogLikeCount);
			if (i < 0) {
				throw new NumberFormatException("����Ϊ����");
			}
			entity.setBlogLikeCount(i);
		} catch (Exception e) {
			return "���͵���������" + e.getMessage();
		}

		AdminDb adminDb = new AdminDb();

		try {
			if (!adminDb.editUser(entity)) {
				return "����δ����";
			}
		} catch (SQLException e) {
			log.error("д�����ݿ����:" + e);
			return "���ݿ����";
		}

		if (passwd != null) {
			try {
				if (!adminDb.editUserPasswd(entity)) {
					return "����δ����";
				}
			} catch (SQLException e) {
				log.error("д�����ݿ����:" + e);
				return "���ݿ����";
			}
		}

		return null;
	}

}

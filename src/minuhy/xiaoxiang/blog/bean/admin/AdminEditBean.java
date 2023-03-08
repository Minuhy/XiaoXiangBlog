package minuhy.xiaoxiang.blog.bean.admin;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.database.CommentDb;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;
import minuhy.xiaoxiang.blog.entity.CommentEntity;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.util.TimeUtil;

public class AdminEditBean {
	private static final Logger log = LoggerFactory.getLogger(AdminEditBean.class);
	int type;
	int page;
	int id;

	public AdminEditBean(int type, int page, int id) {
		this.type = type;
		this.page = page;
		this.id = id;
	}

	public AdminEditBean() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if(type<0) {
			type=0;
			if(DebugConfig.isDebug) {
				log.debug("type����Ϊ����");
			}
		}
		if(type>2) {
			type=2;
			if(DebugConfig.isDebug) {
				log.debug("type���ܴ���2");
			}
		}
		this.type = type;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		if(id<0) {
			id=0;
			if(DebugConfig.isDebug) {
				log.debug("id����Ϊ����");
			}
		}
		this.id = id;
	}

	public EditBean[] getData() throws SQLException {
		EditBean[] editBeans = null;
		if (this.type == 0) {
			// user
			UserDb userDb = new UserDb();
			UserEntity userEntity = userDb.getUserById(String.valueOf(this.id),true);
			if(userEntity == null) {
				userEntity = userDb.getUserById(String.valueOf(this.id),false);
			}
			
			editBeans = new EditBean[13];

			// int id; // ID���Զ����ɣ�Ψһ
			// String label, String name, String hint, String value
			editBeans[0] = new EditBean(false, "���", "id", "��ţ������޸�", String.valueOf(userEntity.getId()));

			// int active; // �˺��Ƿ񼤻1���0����
			// String label, String name, String hint, String value
			editBeans[1] = new EditBean("״̬", "active", "1���0����", userEntity.getActive());
			editBeans[1].setValChoose(new String[]{"1","����","0","����"});
			
			// String passwd; // ���룬��¼�ã�MD5����
			// String label, String name, String hint, String value
			editBeans[2] = new EditBean("����", "passwd", "��δ�޸ģ�", "");
			editBeans[2].setType("password");
			
			// int role; // ��ɫ��0����ͨ��1������Ա
			// String label, String name, String hint, String value
			editBeans[3] = new EditBean("��ɫ", "role", "0����ͨ��1������Ա", userEntity.getRole());
			editBeans[3].setValChoose(new String[]{"0","��ͨ","1","����Ա"});
			
			// String nick; // �ǳ�
			// String label, String name, String hint, String value
			editBeans[4] = new EditBean("�ǳ�", "nick", "����24��", userEntity.getNick());

			// String signature; // ǩ��
			// String label, String name, String hint, String value
			editBeans[5] = new EditBean("ǩ��", "signature", "����60��", userEntity.getSignature());

			// int sex; // �Ա�0��δ���ã�1���У�2��Ů
			// String label, String name, String hint, String value
			editBeans[6] = new EditBean("�Ա�", "sex", "0��δ���ã�1���У�2��Ů", userEntity.getSex());
			editBeans[6].setValChoose(new String[]{"0","δ����","1","��","2","Ů"});
			
			// String hometown; // ����
			// String label, String name, String hint, String value
			editBeans[7] = new EditBean("����", "hometown", "����60��", userEntity.getHometown());

			// String link; // ��ϵ��ʽ
			// String label, String name, String hint, String value
			editBeans[8] = new EditBean("��ϵ��ʽ", "link", "����30��", userEntity.getLink());

			// int avatar; // ͷ��ID
			// String label, String name, String hint, String value
			editBeans[9] = new EditBean("ͷ����", "avatar", "1-138", userEntity.getAvatar());
			editBeans[9].setType("number");
			
			// int blogCount; // ������������
			// String label, String name, String hint, String value
			editBeans[10] = new EditBean("������������", "blogCount", "���ڵ��� 0", userEntity.getBlogCount());
			editBeans[10].setType("number");
			
			// int blogReadCount; // �����Ķ�����
			// String label, String name, String hint, String value
			editBeans[11] = new EditBean("���ͱ��Ķ�����", "blogReadCount", "���ڵ��� 0", userEntity.getBlogReadCount());
			editBeans[11].setType("number");
			
			// int blogLikeCount; // ���ͱ����޼���
			// String label, String name, String hint, String value
			editBeans[12] = new EditBean("���ͱ����޼���", "blogLikeCount", "���ڵ��� 0", userEntity.getBlogLikeCount());
			editBeans[12].setType("number");
			
		} else if (this.type == 1) {
			// blog
			BlogDb blogDb = new BlogDb();
			
			BlogEntity blogEntity = blogDb.getBlogById(String.valueOf(this.id),false);
			if(blogEntity == null) {
				blogEntity = blogDb.getBlogById(String.valueOf(this.id),true);
			}
			
			editBeans = new EditBean[10];

			// int id; // ������Ψһ��ʶ
			// String label, String name, String hint, String value
			editBeans[0] = new EditBean(false, "���", "id", "��ţ������޸�", String.valueOf(blogEntity.getId()));

			// int active; // �Ƿ���Ч��1��Ч��0��Ч
			// String label, String name, String hint, String value
			editBeans[1] = new EditBean("״̬", "active", "�Ƿ���Ч��1��Ч��0��Ч", blogEntity.getActive());
			editBeans[1].setValChoose(new String[]{"1","����","0","����"});
			
			// int authorId; // �����������ID
			// String label, String name, String hint, String value
			editBeans[2] = new EditBean("���߱��", "authorId", "�����߱��", blogEntity.getAuthorId());
			editBeans[2].setType("number");
			
			// String title; // ���ͱ���
			// String label, String name, String hint, String value
			editBeans[3] = new EditBean("����", "title", "����200��", blogEntity.getTitle());

			// String content; // ��������
			// String label, String name, String hint, String value
			editBeans[4] = new EditBean("����", "content", "���б�ҳ���޸�", "");

			// int readCount; // ������
			// String label, String name, String hint, String value
			editBeans[5] = new EditBean("������", "readCount", "���ڵ��� 0", blogEntity.getReadCount());
			editBeans[5].setType("number");
			
			// int likeCount; // ������
			// String label, String name, String hint, String value
			editBeans[6] = new EditBean("������", "likeCount", "���ڵ��� 0", blogEntity.getLikeCount());
			editBeans[6].setType("number");
			
			// int commentCount; // ������
			// String label, String name, String hint, String value
			editBeans[7] = new EditBean("������", "commentCount", "���ڵ��� 0", blogEntity.getCommentCount());
			editBeans[7].setType("number");
			
			// long createTimestamp; // ����ʱ��
			// String label, String name, String hint, String value
			editBeans[8] = new EditBean("����ʱ��", "createTimestamp", "��ʽ��yyyy-MM-dd HH:mm ���� ʱ���",
					TimeUtil.timestamp2DateTime(blogEntity.getCreateTimestamp()));
			editBeans[8].setType("datetime-local");
			
			// long updateTimestamp; // �޸�ʱ��
			// String label, String name, String hint, String value
			editBeans[9] = new EditBean("�޸�ʱ��", "updateTimestamp", "��ʽ��yyyy-MM-dd HH:mm ���� ʱ�����Ĭ��0��",
					blogEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(blogEntity.getUpdateTimestamp()));
			editBeans[9].setType("datetime-local");
			
		} else if (this.type == 2) {
			// comment
			CommentDb commentDb = new CommentDb();
			CommentEntity commentEntity = commentDb.getCommentById(this.id);

			editBeans = new EditBean[8];

			// int id; // ������Ψһ��ʶ
			// String label, String name, String hint, String value
			editBeans[0] = new EditBean(false, "���", "id", "��ţ������޸�", String.valueOf(commentEntity.getId()));

			// int active; // �����Ƿ���ڣ�1�����ڣ�0����ɾ��
			// String label, String name, String hint, String value
			editBeans[1] = new EditBean("״̬", "active", "1�����ڣ�0����ɾ��", commentEntity.getActive());
			editBeans[1].setValChoose(new String[]{"1","����","0","����"});
			
			// int blogId; // ����ID������ƪ�����µ�����
			// String label, String name, String hint, String value
			editBeans[2] = new EditBean("�������ı��", "blogId", "���� 0", commentEntity.getBlogId());
			editBeans[2].setType("number");
			
			// int userId; // ���۷�����ID
			// String label, String name, String hint, String value
			editBeans[3] = new EditBean("���۷����߱��", "userId", "���� 0", commentEntity.getUserId());
			editBeans[3].setType("number");
			
			// int replyId; // ���ظ�������ID
			// String label, String name, String hint, String value
			editBeans[4] = new EditBean("���ظ������۱��", "replyId", "���� 0", commentEntity.getReplyId());
			editBeans[4].setType("number");
			
			// String content; // �ظ�������
			// String label, String name, String hint, String value
			editBeans[5] = new EditBean("�ظ�������", "content", "������ǧ��", commentEntity.getContent());

			// long createTimestamp; // �ظ�ʱ��
			// String label, String name, String hint, String value
			editBeans[6] = new EditBean("�ظ�ʱ��", "createTimestamp", "��ʽ��yyyy-MM-dd HH:mm ���� ʱ���",
					TimeUtil.timestamp2DateTime(commentEntity.getCreateTimestamp()));
			editBeans[6].setType("datetime-local");
			
			// long updateTimestamp; // ����ʱ��
			// String label, String name, String hint, String value
			editBeans[7] = new EditBean("����ʱ��", "updateTimestamp", "��ʽ��yyyy-MM-dd HH:mm ���� ʱ�����Ĭ��0��",
					commentEntity.getUpdateTimestamp()==0?"":TimeUtil.timestamp2DateTime(commentEntity.getUpdateTimestamp()));
			editBeans[7].setType("datetime-local");
			
		}
		return editBeans;
	}

	@Override
	public String toString() {
		return "AdminEditBean{" + "type=" + type + ", page=" + page + ", id=" + id + '}';
	}
	
	

}
package minuhy.xiaoxiang.blog.bean.admin;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.SendMessageBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.AdminDb;
import minuhy.xiaoxiang.blog.util.TimeUtil;

public class AdminNoticeBean {
    private static final Logger log = LoggerFactory.getLogger(AdminNoticeBean.class);

	public static class SendException extends Exception {
		/**
		 * UID
		 */
		private static final long serialVersionUID = -3372411721968403021L;
		String errorMsg;

		public SendException(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		@Override
		public String getMessage() {
			return errorMsg;
		}
	}

	String title;
	String message;
	String object;
	String link;

	public AdminNoticeBean() {
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * �������
	 */
	public void clean() {
		setMessage(null);
		setTitle(null);
		setObject(null);
		setLink(null);
		if(DebugConfig.isDebug) {
			log.debug("������Ϣ");
		}
	}

	/**
	 * ������Ϣ
	 * 
	 * @param sendId
	 * @return
	 * @throws SendException
	 */
	public String send(int sendId) throws SendException {
		String msg = null;
		if (getTitle() != null && getTitle().length() > 0 && getTitle().length() < 80) {
			if (getMessage() != null && getMessage().length() > 0 && getMessage().length() < 360) {
				if (getObject() != null && getObject().length() > 0) {
					String sql = null;
					if (getObject().equals("all")) {
						sql = "SELECT `id` FROM `t_user`";
					} else if (getObject().equals("in7d")) {
						long in7d = TimeUtil.getTimestampMs() - (1000 * 60 * 60 * 24 * 7);
						sql = "SELECT `id` FROM `t_user` WHERE `create_timestamp` > " + in7d;
					} else if (getObject().equals("inSeason")) {
						long inSeason = TimeUtil.getTimestampMs() - (1000 * 60 * 60 * 24 * 90);
						sql = "SELECT `id` FROM `t_user` WHERE `create_timestamp` > " + inSeason;
					} else if (getObject().equals("outSeason")) {
						long inSeason = TimeUtil.getTimestampMs() - (1000 * 60 * 60 * 24 * 90);
						sql = "SELECT `id` FROM `t_user` WHERE `create_timestamp` < " + inSeason;
					} else if (getObject().equals("male")) {
						sql = "SELECT `id` FROM `t_user` WHERE `sex`=1";
					} else if (getObject().equals("female")) {
						sql = "SELECT `id` FROM `t_user` WHERE `sex`=2";
					} else if (getObject().equals("unset")) {
						sql = "SELECT `id` FROM `t_user` WHERE `sex`=0";
					} else if (getObject().equals("like1000")) {
						sql = "SELECT `id` FROM `t_user` WHERE `blog_like_count`>1000";
					} else if (getObject().equals("like100")) {
						sql = "SELECT `id` FROM `t_user` WHERE `blog_like_count`>100";
					} else if (getObject().equals("blog100")) {
						sql = "SELECT `id` FROM `t_user` WHERE `blog_count`>100";
					} else if (getObject().equals("blog10")) {
						sql = "SELECT `id` FROM `t_user` WHERE `blog_count`>10";
					}

					int sendCount = 0;
					int sendFailCount = 0;

					if (sql != null) {
						try {

							AdminDb adminDb = new AdminDb();
							SendMessageBean smb = new SendMessageBean();

							int page = 0;
							while (true) {
								ArrayList<Integer> ids = adminDb.getUserIdsBySql(sql, page, 100);
								page++;
								if (ids.size() < 1) {
									break;
								}

								for (Integer id : ids) {
									smb.setContent(getMessage());
									smb.setMsgType(4);// ��Ϣ���ͣ�1�ظ���2�ᵽ��3���ޣ�4ϵͳ��Ϣ
									smb.setReceiverId(id);
									smb.setSenderId(sendId);
									smb.setTargetUrl(getLink());
									smb.setTitle(getTitle());
									if (smb.send()) {
										sendCount++;
									} else {
										sendFailCount++;
									}
								}
							}

							if (sendFailCount == 0) {
								msg = "���ͳɹ���" + sendCount + "��";
							} else {
								msg = "�ɹ�/ʧ�ܣ�" + sendCount + "/" + sendFailCount;
							}
						} catch (Exception e) {
							throw new SendException(
									"���ݿ����" + "�ɹ�/ʧ�ܣ�" + sendCount + "/" + sendFailCount + "����" + e.getMessage());
						}

					} else {
						throw new SendException("δ����Ķ������");
					}
				} else {
					throw new SendException("�����������");
				}
			} else {
				throw new SendException("���ݳ���1-360��");
			}
		} else {
			throw new SendException("���ⳤ��1-80��");
		}
		return msg;
	}
}

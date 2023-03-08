package minuhy.xiaoxiang.blog.servlet.blog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.SendMessageBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.MessageTypeConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.UrlGeneratorUtil;

/**
 * �Ķ�������
 * 
 * ʹ��JSON����
 * 
 * @author y17mm
 *
 */
@WebServlet("/blog/count/read")
public class ReadCountServlet  extends BaseHttpServlet{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 679982953512705973L;
	private static final Logger log = LoggerFactory.getLogger(ReadCountServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/index.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();

		if (DebugConfig.isDebug) {
			log.debug("���ӷ�����");
		}

		// 1. ��ȡ����
		long getBlogTime = 0;
		int getBlogId = 0;
		int getBlogUserId = 0;
		
		String timestamp = RequestUtil.getReqParam(req, "timestamp", "0");
		String blogId = RequestUtil.getReqParam(req, "blogId", "0");
		String blogAuthorId = RequestUtil.getReqParam(req, "blogAuthorId", "0");
		
		
		HttpSession session = req.getSession();
		//��¼һ�»�ȡ���µ�ʱ�䣬���ڵ������ӷ�����
		Object obj = session.getAttribute(SessionAttributeNameConfig.GET_BLOG_TIME);
		if(obj instanceof Long) {
			getBlogTime = (Long)obj;
			session.removeAttribute(SessionAttributeNameConfig.GET_BLOG_TIME);
		}
		
		obj = session.getAttribute(SessionAttributeNameConfig.GET_BLOG_ID);
		if(obj instanceof Integer) {
			getBlogId = (Integer)obj;
			session.removeAttribute(SessionAttributeNameConfig.GET_BLOG_ID);
		}
		
		obj = session.getAttribute(SessionAttributeNameConfig.GET_BLOG_USER_ID);
		if(obj instanceof Integer) {
			getBlogUserId = (Integer)obj;
			session.removeAttribute(SessionAttributeNameConfig.GET_BLOG_USER_ID);
		}
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}��{}��{}��{}��{}��{}",getBlogId,getBlogTime,getBlogUserId,blogId,timestamp,blogAuthorId);
		}
		
		if(getBlogId == 0 || getBlogTime == 0 || getBlogUserId == 0) {
			if (DebugConfig.isDebug) {
				log.debug("��������ȷ��{}��{}��{}",getBlogId,getBlogTime,getBlogUserId);
			}
			String jsonStr = ResultUtil.fail("��������ȷ").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if(!(String.valueOf(getBlogId).equals(blogId) 
				&& String.valueOf(getBlogTime).equals(timestamp) 
				&& String.valueOf(getBlogUserId).equals(blogAuthorId))) {
			if (DebugConfig.isDebug) {
				log.debug("�������ԣ�{}��{}��{} - {}��{}��{}",blogId,timestamp,blogAuthorId,
						String.valueOf(getBlogId).equals(blogId),
						String.valueOf(getBlogTime).equals(timestamp) ,
						 String.valueOf(getBlogUserId).equals(blogAuthorId));
			}
			String jsonStr = ResultUtil.fail("��������").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (TimeUtil.getTimestampMs() - getBlogTime < 8000) {

			if (DebugConfig.isDebug) {
				log.debug("ʱ�䲻����{}", TimeUtil.getTimestampMs() - getBlogTime);
			}

			String jsonStr = ResultUtil.fail("ʱ�䲻��8��").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// �޸����ݿ�
		BlogDb blogDb = new BlogDb();
		try {
			if(blogDb.increaseReadCount(String.valueOf(getBlogUserId),
					String.valueOf(getBlogId))) {
				
				if (DebugConfig.isDebug) {
					log.debug("�Ķ�����д��ɹ���{}", blogId);
				}

				try { // ��Ϣ

					// �жϵ�ǰ�����Ķ��������Ķ���������һ��ֵ���û�������Ϣ
					int readCount = blogDb.getReadCountByBlogId(blogId);

					String msgTitle = null;
					if (readCount > 10) { // ʮ
						msgTitle = "ʮ���Ķ�";
					} else if (readCount > 100) { // ��
						msgTitle = "һ�ٴ��Ķ�";
					} else if (readCount > 1000) { // ǧ
						msgTitle = "һǧ���Ķ�";
					} else if (readCount > 10000) { // ��
						msgTitle = "һ����Ķ�";
					} else if (readCount > 100000) { // ʮ��
						msgTitle = "ʮ����Ķ�";
					} else if (readCount > 1000000) { // ����
						msgTitle = "һ������Ķ�";
					} else if (readCount > 10000000) { // ǧ��
						msgTitle = "һǧ����Ķ�";
					} else if (readCount > 100000000) { // ��
						msgTitle = "һ�ڴ��Ķ���";
					}

					if (msgTitle != null) { // ��Ҫ������Ϣ
						String title = blogDb.getBlogTitleByBlogId(blogId);
						int authorId = blogDb.getBlogAuthorIdByBlogId(blogId);

						if (authorId != 0) {
							SendMessageBean msgSender = new SendMessageBean();

							msgSender.setTitle("������¡�" + TextUtil.maxLenJustify(title, 18) + "�������" + msgTitle);
							msgSender.setContent("���ڲ����Ϸ���Ĳ��ġ�" + title + "���ܵ��������û��Ļ�ӭ���ش�֪ͨ��ϣ�������ٽ�������Ϊ������Ѵ������м�ֵ�����£�");
							msgSender.setSenderId(0);
							msgSender.setReceiverId(authorId);
							msgSender.setMsgType(MessageTypeConfig.SYSTEM);
							msgSender.setTargetUrl(UrlGeneratorUtil.getReadUrl(blogId));
							if (!msgSender.send()) {
								log.warn("�Ķ���Ϣ����ʧ�ܣ�{}", blogDb);
							} else {
								if (DebugConfig.isDebug) {
									log.debug("�Ķ���Ϣ���ͳɹ���{}", blogId);
								}
							}
						}

					}
				} catch (Exception e) {
					log.warn("�Ķ���Ϣ����ʧ�ܣ�{}", e);
				}
				
				
				String jsonStr = ResultUtil.success("���������ӳɹ�").toString();
				backJson(jsonStr,resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("���ݿ����{}", e);
			
			String jsonStr = ResultUtil.error("���ݿ����").toString();
			backJson(jsonStr,resp);
			return;
		}
		
	}
}

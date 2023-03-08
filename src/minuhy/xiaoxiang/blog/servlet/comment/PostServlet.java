package minuhy.xiaoxiang.blog.servlet.comment;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.bean.user.SendMessageBean;
import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.MessageTypeConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.database.CommentDb;
import minuhy.xiaoxiang.blog.entity.CommentEntity;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.UrlGeneratorUtil;

/**
 * ��������
 * 
 * ʹ�ô�ͳ��ʽ����
 * 
 * @author xxxy1116
 *
 */
@WebServlet("/comment/post")
public class PostServlet extends BaseHttpServlet{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1537487628816124275L;
	private static final Logger log = LoggerFactory.getLogger(PostServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/index.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		HttpSession session = req.getSession();
		UserBean userBean = getLoginUserBean(req);
		
		// 1. ��ȡ����
		String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		int userId,blogId;
		
		session.setAttribute(SessionAttributeNameConfig.COMMENT_CONTENT, content);
		
		if (DebugConfig.isDebug) {
			log.debug("��������");
		}
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{} {}", blogIdStr,content);
		}
		
		try {
			blogId = Integer.parseInt(blogIdStr);
			if(blogId<1) {
				throw new  NumberFormatException("����ID��Χ����ȷ");
			}
		}catch ( NumberFormatException e) {
			e.printStackTrace();
			if(DebugConfig.isDebug) {
				log.debug(e.getMessage());
			}
			
			session.removeAttribute(SessionAttributeNameConfig.COMMENT_CONTENT);
			forwardTipWarnPage("��������", "��ҳ",currentPath+ "/index.jsp", 
					req, resp);
			return;
		}
		
		if(userBean == null) {
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼���ܷ�����");
			}
			
			forwardTipWarnPage("��������ǰ���ȵ�¼", "��¼",
					currentPath + UrlGeneratorUtil.getLoginUrl(currentPath+ "/read.jsp?i="+blogId,  "�Ķ�����"),
					req, resp);
			return;
		}

		userId = userBean.getId();
		
		
		if (content == null || content.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("�ظ�����̫�̣�{}", content);
			}

			forwardTipWarnPage("��������������", "����ҳ��",currentPath+ "/read.jsp?i="+blogId, 
					req, resp);
			return;
		}
		
		if (content == null || content.length() > 2000) {

			if (DebugConfig.isDebug) {
				log.debug("�ظ�����̫����{}", content);
			}

			forwardTipWarnPage("���2000�֣�"+content.length()+"��", "����ҳ��",currentPath+ "/read.jsp?i="+blogId, 
					req, resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// �������۶���
		CommentEntity commentEntity = new CommentEntity();
		commentEntity.setContent(content);
		commentEntity.setBlogId(blogId);
		commentEntity.setUserId(userId);
		commentEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
		
		// �������ݿ�
		CommentDb commentDb = new CommentDb();
		try {
			if(commentDb.writePostComment(commentEntity)) {
				// д��ɹ�
				/*************************************************************************/

				if (DebugConfig.isDebug) {
					log.debug("��������д��ɹ���{}", blogId);
				}
				
				try {
					// ����Ϣ
					BlogDb blogDb = new BlogDb();
					int blogAuthorId = blogDb.getBlogAuthorIdByBlogId(blogIdStr);
					
					if(blogAuthorId!=userBean.getId()) { // ���Լ��ظ��Ĳ���Ҫ����Ϣ
						String blogTitle = blogDb.getBlogTitleByBlogId(blogIdStr);
						int commentId = commentDb.getNewCommentIdByUserIdAndBlogId(userId, blogId);
						
						SendMessageBean msgSender = new SendMessageBean();
						
						msgSender.setTitle("<strong>"+ userBean.getNick() + "</strong> ���ҵĲ��ķ���������");
						msgSender.setContent("��<strong>��"+ 
						TextUtil.maxLenJustify(blogTitle, 20) +
								"��</strong>��˵��"+
								TextUtil.maxLen(content, 160)
							);
						msgSender.setSenderId(userBean.getId());
						msgSender.setReceiverId(blogAuthorId);
						msgSender.setMsgType(MessageTypeConfig.REPLY);
						msgSender.setTargetUrl(UrlGeneratorUtil.getReadCommentUrl(blogId,commentId));
						if(!msgSender.send()) {
							log.warn("�ظ���Ϣ����ʧ�ܣ�{}",commentId);
						}else {
							// ������Ϣ�ɹ���������Ϣ����
							if (DebugConfig.isDebug) {
								log.debug("�ظ���Ϣ���ͳɹ���{}", blogId);
							}
						}
					}
				}catch (Exception e) {
					log.warn("�ظ���Ϣ����ʧ�ܣ�{}",e);
				}

				/**************************************************************/
				session.removeAttribute(SessionAttributeNameConfig.COMMENT_CONTENT); 
				forwardTipOkPage("�������۳ɹ�", "����ҳ��",currentPath+ "/read.jsp?i="+blogId+"&comment=true", // comment=1�Զ�����������
						req, resp);
				return;
			}else {
				forwardTipWarnPage("д������ʧ��", "����ҳ��",currentPath+ "/read.jsp?i="+blogId, 
						req, resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("���ݿ����{}", e);

			forwardTipErrorPage("���ݿ����", "����ҳ��",currentPath+ "/read.jsp?i="+blogId, 
					req, resp);
			return;
		}
	}
}

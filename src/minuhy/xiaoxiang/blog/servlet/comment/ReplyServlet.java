package minuhy.xiaoxiang.blog.servlet.comment;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import minuhy.xiaoxiang.blog.bean.user.SendMessageBean;
import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.MessageTypeConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.database.CommentDb;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.CommentEntity;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.UrlGeneratorUtil;
/**
 * �ظ�����
 * 
 * ʹ��JSON����
 * 
 * @author y17mm
 *
 */
@WebServlet("/comment/reply")
public class ReplyServlet extends BaseHttpServlet{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4099421294983714048L;
	private static final Logger log = LoggerFactory.getLogger(ReplyServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/index.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		UserBean userBean = getLoginUserBean(req);
		
		// 1. ��ȡ����
		String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String replyIdStr = RequestUtil.getReqParam(req, "replyId", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		int userId,blogId,replyId;
		
		if (DebugConfig.isDebug) {
			log.debug("�ظ�����");
		}
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}�� {}��{}", replyIdStr,blogIdStr,content);
		}
		
		try {
			blogId = Integer.parseInt(blogIdStr);
			if(blogId<1) {
				throw new  NumberFormatException("����ID��Χ����ȷ");
			}
		}catch ( NumberFormatException e) {
			e.printStackTrace();
			if(DebugConfig.isDebug) {
				log.debug("���ı�Ŵ���{}��{}",blogIdStr,e.getMessage());
			}
			
			String jsonStr = ResultUtil.fail("���ı�Ŵ���").toString();
			backJson(jsonStr,resp);
			return;
		}
		

		try {
			replyId = Integer.parseInt(replyIdStr);
			if(replyId<1) {
				throw new  NumberFormatException("����ID��Χ����ȷ");
			}
		}catch ( NumberFormatException e) {
			e.printStackTrace();
			if(DebugConfig.isDebug) {
				log.debug("����ID����{}��{}",replyIdStr,e.getMessage());
			}
			
			String jsonStr = ResultUtil.fail("���۱�Ŵ���").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if(userBean == null) {
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼���ܷ�����");
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("���ȵ�¼",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}

		userId = userBean.getId();
		
		
		if (content == null || content.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("�ظ�����̫�̣�{}", content);
			}

			String jsonStr = ResultUtil.fail("������ظ�����").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (content == null || content.length() > 2000) {

			if (DebugConfig.isDebug) {
				log.debug("�ظ�����̫����{}", content);
			}

			String jsonStr = ResultUtil.fail("���2000�֣�"+content.length()+"��").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// �������ݿ�
		CommentDb commentDb = new CommentDb();
		try {
			int replyBlogId = commentDb.getBlogIdByReplyId(replyId);
			if(replyBlogId == blogId) {
				// �������۶���
				CommentEntity commentEntity = new CommentEntity();
				commentEntity.setContent(content);
				commentEntity.setBlogId(blogId);
				commentEntity.setUserId(userId);
				commentEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
				
				commentEntity.setReplyId(replyId);
				
				if(commentDb.writePostComment(commentEntity)) {
					// д��ɹ�
					
					// ��ID
					int id = commentDb.getNewCommentIdByUserIdAndBlogId(userId,blogId);
					
					// �鷢�����û���Ϣ
					UserDb userDb = new UserDb();
					UserEntity userEntity = userDb.getCommentUserInfoById(userId);
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", id);
					jsonObject.put("authorId", userId);
					jsonObject.put("authorNick", userEntity.getNick());
					jsonObject.put("avatar", String.format("h%03d", userEntity.getAvatar()));
					jsonObject.put("datetime", 
							TimeUtil.timestamp2DateTime(
									TimeUtil.getTimestampMs() // ʹ�÷�����ʱ��
									)
							); 
					
					
					/*************************************************************************/

					if (DebugConfig.isDebug) {
						log.debug("��������д��ɹ���{}", blogId);
					}
					
					try {
						// ����Ϣ
						// ��ȡ�����ظ�����������
						CommentEntity repliedEntity = commentDb.getCommentById(replyId); // ���ظ�������
						UserEntity repliedUserEntity = userDb.getCommentUserInfoById(repliedEntity.getUserId()); // ���ظ����û�
						
						if(repliedEntity.getUserId()!=userBean.getId()) { // ���Լ��ظ��Ĳ���Ҫ����Ϣ
							BlogDb blogDb = new BlogDb();
							String title = blogDb.getBlogTitleByBlogId(blogIdStr);
							
							SendMessageBean msgSender = new SendMessageBean();
							
							msgSender.setTitle("<strong>"+ userBean.getNick() + "</strong> �ظ����ҵ�����");
							msgSender.setContent("��<strong>��"+TextUtil.maxLen(title, 20)+"��</strong>��<br/>"
									+ "�ظ� "
									+ "<a href=\""+currentPath+"/people.jsp\">"
										+ "@"+repliedUserEntity.getNick()+" ��"
									+ "</a>"
										+ TextUtil.maxLen(content, 100)
									+ "<p style=\"color: #aaa;border-left: #888 2px solid;padding-left: 10px;\">"
										+ repliedUserEntity.getNick() + "��"
										+ TextUtil.maxLen(repliedEntity.getContent(), 100)
									+ "</p>"
								);
							msgSender.setSenderId(userBean.getId());
							msgSender.setReceiverId(repliedEntity.getUserId());
							msgSender.setMsgType(MessageTypeConfig.MENTION);
							msgSender.setTargetUrl(UrlGeneratorUtil.getReadCommentUrl(blogId,id));
							if(!msgSender.send()) {
								log.warn("�ظ�������Ϣ����ʧ�ܣ�{}",id);
							}else {
								// ������Ϣ�ɹ���������Ϣ����
								if (DebugConfig.isDebug) {
									log.debug("�ظ�������Ϣ���ͳɹ���{}", id);
								}
							}
						}
					}catch (Exception e) {
						log.warn("�ظ���Ϣ����ʧ�ܣ�{}",e);
					}

					/**************************************************************/
					
					
					
					String jsonStr = ResultUtil.success("�ظ��ɹ�",jsonObject).toString();
					backJson(jsonStr,resp);
					return;
				}else {
					String jsonStr = ResultUtil.fail("д������ʧ��").toString();
					backJson(jsonStr,resp);
					return;
				}
			}else {
				String jsonStr = ResultUtil.fail("���۲��ڴ���").toString();
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
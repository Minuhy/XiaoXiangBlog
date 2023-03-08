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
 * 发表评论
 * 
 * 使用传统方式交互
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
		
		// 1. 获取参数
		String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		int userId,blogId;
		
		session.setAttribute(SessionAttributeNameConfig.COMMENT_CONTENT, content);
		
		if (DebugConfig.isDebug) {
			log.debug("发布评论");
		}
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{} {}", blogIdStr,content);
		}
		
		try {
			blogId = Integer.parseInt(blogIdStr);
			if(blogId<1) {
				throw new  NumberFormatException("博客ID范围不正确");
			}
		}catch ( NumberFormatException e) {
			e.printStackTrace();
			if(DebugConfig.isDebug) {
				log.debug(e.getMessage());
			}
			
			session.removeAttribute(SessionAttributeNameConfig.COMMENT_CONTENT);
			forwardTipWarnPage("参数出错", "首页",currentPath+ "/index.jsp", 
					req, resp);
			return;
		}
		
		if(userBean == null) {
			// 没有登录
			if (DebugConfig.isDebug) {
				log.debug("没登录不能发评论");
			}
			
			forwardTipWarnPage("发表评论前请先登录", "登录",
					currentPath + UrlGeneratorUtil.getLoginUrl(currentPath+ "/read.jsp?i="+blogId,  "阅读博文"),
					req, resp);
			return;
		}

		userId = userBean.getId();
		
		
		if (content == null || content.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("回复评论太短：{}", content);
			}

			forwardTipWarnPage("请输入评论内容", "博文页面",currentPath+ "/read.jsp?i="+blogId, 
					req, resp);
			return;
		}
		
		if (content == null || content.length() > 2000) {

			if (DebugConfig.isDebug) {
				log.debug("回复评论太长：{}", content);
			}

			forwardTipWarnPage("最多2000字（"+content.length()+"）", "博文页面",currentPath+ "/read.jsp?i="+blogId, 
					req, resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 创建评论对象
		CommentEntity commentEntity = new CommentEntity();
		commentEntity.setContent(content);
		commentEntity.setBlogId(blogId);
		commentEntity.setUserId(userId);
		commentEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
		
		// 存入数据库
		CommentDb commentDb = new CommentDb();
		try {
			if(commentDb.writePostComment(commentEntity)) {
				// 写入成功
				/*************************************************************************/

				if (DebugConfig.isDebug) {
					log.debug("评论数据写入成功：{}", blogId);
				}
				
				try {
					// 发消息
					BlogDb blogDb = new BlogDb();
					int blogAuthorId = blogDb.getBlogAuthorIdByBlogId(blogIdStr);
					
					if(blogAuthorId!=userBean.getId()) { // 给自己回复的不需要发消息
						String blogTitle = blogDb.getBlogTitleByBlogId(blogIdStr);
						int commentId = commentDb.getNewCommentIdByUserIdAndBlogId(userId, blogId);
						
						SendMessageBean msgSender = new SendMessageBean();
						
						msgSender.setTitle("<strong>"+ userBean.getNick() + "</strong> 对我的博文发表了评论");
						msgSender.setContent("在<strong>《"+ 
						TextUtil.maxLenJustify(blogTitle, 20) +
								"》</strong>中说："+
								TextUtil.maxLen(content, 160)
							);
						msgSender.setSenderId(userBean.getId());
						msgSender.setReceiverId(blogAuthorId);
						msgSender.setMsgType(MessageTypeConfig.REPLY);
						msgSender.setTargetUrl(UrlGeneratorUtil.getReadCommentUrl(blogId,commentId));
						if(!msgSender.send()) {
							log.warn("回复消息发送失败：{}",commentId);
						}else {
							// 发送消息成功，设置消息计数
							if (DebugConfig.isDebug) {
								log.debug("回复消息发送成功：{}", blogId);
							}
						}
					}
				}catch (Exception e) {
					log.warn("回复消息发送失败：{}",e);
				}

				/**************************************************************/
				session.removeAttribute(SessionAttributeNameConfig.COMMENT_CONTENT); 
				forwardTipOkPage("发表评论成功", "博文页面",currentPath+ "/read.jsp?i="+blogId+"&comment=true", // comment=1自动跳到评论区
						req, resp);
				return;
			}else {
				forwardTipWarnPage("写入评论失败", "博文页面",currentPath+ "/read.jsp?i="+blogId, 
						req, resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库错误：{}", e);

			forwardTipErrorPage("数据库错误", "博文页面",currentPath+ "/read.jsp?i="+blogId, 
					req, resp);
			return;
		}
	}
}

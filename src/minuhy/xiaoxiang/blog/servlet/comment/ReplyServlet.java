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
 * 回复评论
 * 
 * 使用JSON交互
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
		
		// 1. 获取参数
		String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String replyIdStr = RequestUtil.getReqParam(req, "replyId", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		int userId,blogId,replyId;
		
		if (DebugConfig.isDebug) {
			log.debug("回复评论");
		}
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}， {}，{}", replyIdStr,blogIdStr,content);
		}
		
		try {
			blogId = Integer.parseInt(blogIdStr);
			if(blogId<1) {
				throw new  NumberFormatException("博客ID范围不正确");
			}
		}catch ( NumberFormatException e) {
			e.printStackTrace();
			if(DebugConfig.isDebug) {
				log.debug("博文编号错误，{}，{}",blogIdStr,e.getMessage());
			}
			
			String jsonStr = ResultUtil.fail("博文编号错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		

		try {
			replyId = Integer.parseInt(replyIdStr);
			if(replyId<1) {
				throw new  NumberFormatException("评论ID范围不正确");
			}
		}catch ( NumberFormatException e) {
			e.printStackTrace();
			if(DebugConfig.isDebug) {
				log.debug("评论ID错误，{}，{}",replyIdStr,e.getMessage());
			}
			
			String jsonStr = ResultUtil.fail("评论编号错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if(userBean == null) {
			// 没有登录
			if (DebugConfig.isDebug) {
				log.debug("没登录不能发评论");
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("请先登录",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}

		userId = userBean.getId();
		
		
		if (content == null || content.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("回复评论太短：{}", content);
			}

			String jsonStr = ResultUtil.fail("请输入回复内容").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (content == null || content.length() > 2000) {

			if (DebugConfig.isDebug) {
				log.debug("回复评论太长：{}", content);
			}

			String jsonStr = ResultUtil.fail("最多2000字（"+content.length()+"）").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 存入数据库
		CommentDb commentDb = new CommentDb();
		try {
			int replyBlogId = commentDb.getBlogIdByReplyId(replyId);
			if(replyBlogId == blogId) {
				// 创建评论对象
				CommentEntity commentEntity = new CommentEntity();
				commentEntity.setContent(content);
				commentEntity.setBlogId(blogId);
				commentEntity.setUserId(userId);
				commentEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
				
				commentEntity.setReplyId(replyId);
				
				if(commentDb.writePostComment(commentEntity)) {
					// 写入成功
					
					// 查ID
					int id = commentDb.getNewCommentIdByUserIdAndBlogId(userId,blogId);
					
					// 查发表者用户信息
					UserDb userDb = new UserDb();
					UserEntity userEntity = userDb.getCommentUserInfoById(userId);
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", id);
					jsonObject.put("authorId", userId);
					jsonObject.put("authorNick", userEntity.getNick());
					jsonObject.put("avatar", String.format("h%03d", userEntity.getAvatar()));
					jsonObject.put("datetime", 
							TimeUtil.timestamp2DateTime(
									TimeUtil.getTimestampMs() // 使用服务器时间
									)
							); 
					
					
					/*************************************************************************/

					if (DebugConfig.isDebug) {
						log.debug("评论数据写入成功：{}", blogId);
					}
					
					try {
						// 发消息
						// 获取到被回复的那条评论
						CommentEntity repliedEntity = commentDb.getCommentById(replyId); // 被回复的评论
						UserEntity repliedUserEntity = userDb.getCommentUserInfoById(repliedEntity.getUserId()); // 被回复的用户
						
						if(repliedEntity.getUserId()!=userBean.getId()) { // 给自己回复的不需要发消息
							BlogDb blogDb = new BlogDb();
							String title = blogDb.getBlogTitleByBlogId(blogIdStr);
							
							SendMessageBean msgSender = new SendMessageBean();
							
							msgSender.setTitle("<strong>"+ userBean.getNick() + "</strong> 回复了我的评论");
							msgSender.setContent("在<strong>《"+TextUtil.maxLen(title, 20)+"》</strong>中<br/>"
									+ "回复 "
									+ "<a href=\""+currentPath+"/people.jsp\">"
										+ "@"+repliedUserEntity.getNick()+" ："
									+ "</a>"
										+ TextUtil.maxLen(content, 100)
									+ "<p style=\"color: #aaa;border-left: #888 2px solid;padding-left: 10px;\">"
										+ repliedUserEntity.getNick() + "："
										+ TextUtil.maxLen(repliedEntity.getContent(), 100)
									+ "</p>"
								);
							msgSender.setSenderId(userBean.getId());
							msgSender.setReceiverId(repliedEntity.getUserId());
							msgSender.setMsgType(MessageTypeConfig.MENTION);
							msgSender.setTargetUrl(UrlGeneratorUtil.getReadCommentUrl(blogId,id));
							if(!msgSender.send()) {
								log.warn("回复评论消息发送失败：{}",id);
							}else {
								// 发送消息成功，设置消息计数
								if (DebugConfig.isDebug) {
									log.debug("回复评论消息发送成功：{}", id);
								}
							}
						}
					}catch (Exception e) {
						log.warn("回复消息发送失败：{}",e);
					}

					/**************************************************************/
					
					
					
					String jsonStr = ResultUtil.success("回复成功",jsonObject).toString();
					backJson(jsonStr,resp);
					return;
				}else {
					String jsonStr = ResultUtil.fail("写入评论失败").toString();
					backJson(jsonStr,resp);
					return;
				}
			}else {
				String jsonStr = ResultUtil.fail("评论不在此文").toString();
				backJson(jsonStr,resp);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库错误：{}", e);

			String jsonStr = ResultUtil.error("数据库错误").toString();
			backJson(jsonStr,resp);
			return;
		}
	}
}
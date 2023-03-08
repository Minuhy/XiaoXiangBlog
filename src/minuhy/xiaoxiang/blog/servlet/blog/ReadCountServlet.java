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
 * 阅读量计数
 * 
 * 使用JSON交互
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
			log.debug("增加访问量");
		}

		// 1. 获取参数
		long getBlogTime = 0;
		int getBlogId = 0;
		int getBlogUserId = 0;
		
		String timestamp = RequestUtil.getReqParam(req, "timestamp", "0");
		String blogId = RequestUtil.getReqParam(req, "blogId", "0");
		String blogAuthorId = RequestUtil.getReqParam(req, "blogAuthorId", "0");
		
		
		HttpSession session = req.getSession();
		//记录一下获取文章的时间，用于等下增加访问量
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
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}，{}，{}，{}，{}，{}",getBlogId,getBlogTime,getBlogUserId,blogId,timestamp,blogAuthorId);
		}
		
		if(getBlogId == 0 || getBlogTime == 0 || getBlogUserId == 0) {
			if (DebugConfig.isDebug) {
				log.debug("参数不正确：{}，{}，{}",getBlogId,getBlogTime,getBlogUserId);
			}
			String jsonStr = ResultUtil.fail("参数不正确").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if(!(String.valueOf(getBlogId).equals(blogId) 
				&& String.valueOf(getBlogTime).equals(timestamp) 
				&& String.valueOf(getBlogUserId).equals(blogAuthorId))) {
			if (DebugConfig.isDebug) {
				log.debug("参数不对：{}，{}，{} - {}，{}，{}",blogId,timestamp,blogAuthorId,
						String.valueOf(getBlogId).equals(blogId),
						String.valueOf(getBlogTime).equals(timestamp) ,
						 String.valueOf(getBlogUserId).equals(blogAuthorId));
			}
			String jsonStr = ResultUtil.fail("参数不对").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (TimeUtil.getTimestampMs() - getBlogTime < 8000) {

			if (DebugConfig.isDebug) {
				log.debug("时间不到：{}", TimeUtil.getTimestampMs() - getBlogTime);
			}

			String jsonStr = ResultUtil.fail("时间不够8秒").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 修改数据库
		BlogDb blogDb = new BlogDb();
		try {
			if(blogDb.increaseReadCount(String.valueOf(getBlogUserId),
					String.valueOf(getBlogId))) {
				
				if (DebugConfig.isDebug) {
					log.debug("阅读数据写入成功：{}", blogId);
				}

				try { // 消息

					// 判断当前博文阅读数量，阅读数量到达一定值给用户发送消息
					int readCount = blogDb.getReadCountByBlogId(blogId);

					String msgTitle = null;
					if (readCount > 10) { // 十
						msgTitle = "十次阅读";
					} else if (readCount > 100) { // 百
						msgTitle = "一百次阅读";
					} else if (readCount > 1000) { // 千
						msgTitle = "一千次阅读";
					} else if (readCount > 10000) { // 万
						msgTitle = "一万次阅读";
					} else if (readCount > 100000) { // 十万
						msgTitle = "十万次阅读";
					} else if (readCount > 1000000) { // 百万
						msgTitle = "一百万次阅读";
					} else if (readCount > 10000000) { // 千万
						msgTitle = "一千万次阅读";
					} else if (readCount > 100000000) { // 亿
						msgTitle = "一亿次阅读！";
					}

					if (msgTitle != null) { // 需要发送消息
						String title = blogDb.getBlogTitleByBlogId(blogId);
						int authorId = blogDb.getBlogAuthorIdByBlogId(blogId);

						if (authorId != 0) {
							SendMessageBean msgSender = new SendMessageBean();

							msgSender.setTitle("你的文章《" + TextUtil.maxLenJustify(title, 18) + "》获得了" + msgTitle);
							msgSender.setContent("你在博客上发表的博文《" + title + "》受到了其他用户的欢迎，特此通知！希望你能再接再厉，为广大网友创作更有价值的文章！");
							msgSender.setSenderId(0);
							msgSender.setReceiverId(authorId);
							msgSender.setMsgType(MessageTypeConfig.SYSTEM);
							msgSender.setTargetUrl(UrlGeneratorUtil.getReadUrl(blogId));
							if (!msgSender.send()) {
								log.warn("阅读消息发送失败：{}", blogDb);
							} else {
								if (DebugConfig.isDebug) {
									log.debug("阅读消息发送成功：{}", blogId);
								}
							}
						}

					}
				} catch (Exception e) {
					log.warn("阅读消息发送失败：{}", e);
				}
				
				
				String jsonStr = ResultUtil.success("访问量增加成功").toString();
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

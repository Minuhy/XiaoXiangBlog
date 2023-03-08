package minuhy.xiaoxiang.blog.servlet.blog;

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
import minuhy.xiaoxiang.blog.database.LikeDb;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;
import minuhy.xiaoxiang.blog.util.UrlGeneratorUtil;

/**
 * 点赞程序
 * 
 * 使用JSON交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/blog/attitude")
public class AttitudeServlet extends BaseHttpServlet{
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6891187515476805415L;
	private static final Logger log = LoggerFactory.getLogger(AttitudeServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/index.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		UserBean userBean = getLoginUserBean(req);
		if(userBean == null) {
			// 没有登录
			if (DebugConfig.isDebug) {
				log.debug("没登录不能点赞");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("请先登录",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("点赞");
		}
		
		// 1. 获取参数
		String stateStr = RequestUtil.getReqParam(req, "state", "");
		String blogId = RequestUtil.getReqParam(req, "blogId", "");
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{} {}", stateStr, blogId);
		}

		if (stateStr == null || stateStr.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("点赞状态不正确：{}", stateStr);
			}

			String jsonStr = ResultUtil.fail("状态不正确").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		try {
			Integer.valueOf(blogId);
		}catch (NumberFormatException e) {
			if (DebugConfig.isDebug) {
				log.debug("博文ID格式不对", blogId);
			}

			String jsonStr = ResultUtil.fail("博文编号错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 点赞状态
		int state = 0;
		String tip = "取消成功";
		if(stateStr.toLowerCase().equals("support")) {
			state = 1;
			tip = "支持成功";
		}else if(stateStr.toLowerCase().equals("unsupport")){
			state = -1;
			tip = "反对成功";
		}
		
		// 3. 业务逻辑
		
		// 存入数据库
		LikeDb likeDb = new LikeDb();
		try {
			if(likeDb.setLike(
					state, 
					userBean.getId(), 
					blogId, 
					TimeUtil.getTimestampMs()
				)) {
				// 写入成功

				if (DebugConfig.isDebug) {
					log.debug("点赞数据写入成功：{}", blogId);
				}
				
				try { // 消息
					// 判断当前博文点赞数量，点赞数量到达一定值给用户发送消息
					BlogDb blogDb = new BlogDb();
					int[] likeCount = blogDb.getBlogLikeCountByBlogId(blogId);
					
					if(likeCount[0]>likeCount[1]) { // 判断是否需要发送消息
						String msgTitle = null;
						int likeMsgSendCount = 0;
						if(likeCount[0]>=1&&likeCount[1]<1) {
							msgTitle = "第一个赞";
							likeMsgSendCount = 1;
						}else if(likeCount[0]>=10&&likeCount[1]<10){ // 十
							msgTitle = "十个赞";
							likeMsgSendCount = 10;
						}else if(likeCount[0]>=100&&likeCount[1]<100){ // 百
							msgTitle = "一百个赞";
							likeMsgSendCount = 100;
						}else if(likeCount[0]>=1000&&likeCount[1]<1000){ // 千
							msgTitle = "一千个赞";
							likeMsgSendCount = 1000;
						}else if(likeCount[0]>=10000&&likeCount[1]<10000){ // 万
							msgTitle = "一万个赞";
							likeMsgSendCount = 10000;
						}else if(likeCount[0]>=100000&&likeCount[1]<100000){ // 十万
							msgTitle = "十万个赞";
							likeMsgSendCount = 100000;
						}else if(likeCount[0]>=1000000&&likeCount[1]<1000000){ // 百万
							msgTitle = "一百万个赞";
							likeMsgSendCount = 1000000;
						}else if(likeCount[0]>=10000000&&likeCount[1]<10000000){ // 千万
							msgTitle = "一千万个赞";
							likeMsgSendCount = 10000000;
						}else if(likeCount[0]>=100000000&&likeCount[1]<100000000){ // 亿
							msgTitle = "一亿个赞！";
							likeMsgSendCount = 100000000;
						}
						
						if(msgTitle!=null) { // 需要发送消息
							String title = blogDb.getBlogTitleByBlogId(blogId);
							int authorId = blogDb.getBlogAuthorIdByBlogId(blogId);
							if(authorId != userBean.getId()) { // 如果是自己，不用发
								if(authorId!=0) {
									SendMessageBean msgSender = new SendMessageBean();
									
									msgSender.setTitle("你的文章《"+TextUtil.maxLenJustify(title, 18)+"》收到了" + msgTitle);
									msgSender.setContent("你在博客上发表的文章《"+title+"》受到了其他用户的欢迎，特此通知！希望你能再接再厉，为广大网友创作更有价值的文章！");
									msgSender.setSenderId(userBean.getId());
									msgSender.setReceiverId(authorId);
									msgSender.setMsgType(MessageTypeConfig.LIKE);
									msgSender.setTargetUrl(UrlGeneratorUtil.getReadUrl(blogId));
									if(!msgSender.send()) {
										log.warn("点赞消息发送失败：{}",blogDb);
									}else {
										// 发送消息成功，设置消息计数
										blogDb.setLikeMsgSendCount(blogId,likeMsgSendCount);
	
										if (DebugConfig.isDebug) {
											log.debug("点赞消息发送成功：{}", blogId);
										}
									}
								}
							}
						}
					}
				}catch (Exception e) {
					log.warn("点赞消息发送失败：{}",e);
				}
				
				// 返回结果
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("state", state); // 返回最终结果
				String jsonStr = ResultUtil.success(tip,jsonObject).toString();
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

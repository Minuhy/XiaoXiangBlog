package minuhy.xiaoxiang.blog.servlet.blog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;
import minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.TipsJspUtil;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;
import minuhy.xiaoxiang.blog.util.TextUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * 发表博客
 * 
 * 使用JSON交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/blog/post")
public class PostServlet extends BaseHttpServlet{

	/**
	 * UID
	 */
	private static final long serialVersionUID = 4967339135987194906L;
	private static final Logger log = LoggerFactory.getLogger(PostServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(req.getContextPath()+"/post.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		UserBean userBean = getLoginUserBean(req);
		if(userBean == null) {
			// 没有登录
			if (DebugConfig.isDebug) {
				log.debug("没登录不能发博文");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("请先登录",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("发布博文");
		}
		
		// 1. 获取参数
		String title = RequestUtil.getReqParam(req, "title", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{} {}", title, content);
		}

		if (title == null || title.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("标题太短：{}", title);
			}

			String jsonStr = ResultUtil.fail("标题长度太短").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (title.length() > 200) {

			if (DebugConfig.isDebug) {
				log.debug("标题太长：{}", title);
			}

			String jsonStr = ResultUtil.fail("标题长度太长（最长200字）").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (content == null || content.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("正文太短：{}", content);
			}

			String jsonStr = ResultUtil.fail("正文长度太短").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (TextUtil.getStringLenByUtf8(content) > 65000) {

			if (DebugConfig.isDebug) {
				log.debug("正文太长：{}", content);
			}

			String jsonStr = ResultUtil.fail("正文太长（最长六万五千个字节）").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 创建博文对象
		BlogEntity blogEntity = new BlogEntity();
		blogEntity.setAuthorId(userBean.getId());
		blogEntity.setTitle(title);
		blogEntity.setContent(content);
		blogEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
		
		// 存入数据库
		BlogDb blogDb = new BlogDb();
		try {
			if(blogDb.writeBlog(blogEntity)) {
				// 写入成功
				String newestId = blogDb.getNewestBlogIdByUserId( // 获取博文ID
						String.valueOf(userBean.getId())
						);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("url", TipsJspUtil.generateLink(currentPath, 
						MsgTypeEnum.SUCCESS, 
						"发表博文成功", 
						currentPath+"/read.jsp?i="+newestId,
						"立即查看 “"+TextUtil.maxLenJustify(title, 20)+"”",
						false));
				String jsonStr = ResultUtil.success("发表成功",jsonObject).toString();
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

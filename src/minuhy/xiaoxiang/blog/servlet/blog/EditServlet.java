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
 * 编辑博客
 * 
 * 使用JSON交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/blog/edit")
public class EditServlet extends BaseHttpServlet{

	/**
	 * UID
	 */
	private static final long serialVersionUID = 4969939113987194906L;
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
			log.debug("修改博文");
		}
		
		// 1. 获取参数
		String id = RequestUtil.getReqParam(req, "id", "");
		String title = RequestUtil.getReqParam(req, "title", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}，{}，{}", id,title, content);
		}
		
		try {
			Integer.parseInt(id);
		}catch (NumberFormatException e) {
			if (DebugConfig.isDebug) {
				log.debug("ID错误：{}", id);
			}
			String jsonStr = ResultUtil.fail("参数错误").toString();
			backJson(jsonStr,resp);
			return;
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
		
		// 存入数据库
		BlogDb blogDb = new BlogDb();
		try {
			BlogEntity entity = blogDb.getBlogById(id);
			if(entity == null) {
				if (DebugConfig.isDebug) {
					log.debug("查不到要修改的文章",id);
				}

				String jsonStr = ResultUtil.fail("查无此文").toString();
				backJson(jsonStr,resp);
				return;
			}
			
			// 本人或管理员可修改
			if(entity.getAuthorId() == userBean.getId() || userBean.getRole() == 1) {
				
				entity.setTitle(title);
				entity.setContent(content);
				entity.setUpdateTimestamp(TimeUtil.getTimestampMs());
				
				
				if(blogDb.editBlog(entity)) {
					// 写入成功
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("url", TipsJspUtil.generateLink(currentPath, 
							MsgTypeEnum.SUCCESS, 
							"博文修改成功", 
							currentPath+"/read.jsp?i="+id,
							"立即查看 “"+TextUtil.maxLenJustify(title, 20)+"”",
							false));
					String jsonStr = ResultUtil.success("修改成功",jsonObject).toString();
					backJson(jsonStr,resp);
					return;
				}else {
					if (DebugConfig.isDebug) {
						log.debug("写入数据库失败");
					}
	
					String jsonStr = ResultUtil.fail("保存失败").toString();
					backJson(jsonStr,resp);
					return;
				}
			}else {
				String jsonStr = ResultUtil.fail("没有权限").toString();
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

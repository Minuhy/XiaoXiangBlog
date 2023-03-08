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
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;

/**
 * 删除博文
 * 
 * 使用JSON交互
 * 
 * @author y17mm
 *
 */
@WebServlet("/blog/delete")
public class DeleteBlogServlet  extends BaseHttpServlet{

	/**
	 * UID
	 */
	private static final long serialVersionUID = 4787886083585944735L;
	private static final Logger log = LoggerFactory.getLogger(DeleteBlogServlet.class);
	
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
				log.debug("没登录不能删除文章");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("请先登录",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("删除文章");
		}
		
		// 1. 获取参数
		String blogId = RequestUtil.getReqParam(req, "blogId", "");
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}",  blogId);
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
		
		// 3. 业务逻辑
		BlogDb blogDb = new BlogDb();
		
		try {
			// 查询是否有权限
			int authorId =blogDb.getBlogAuthorIdByBlogId(blogId);
			if((authorId != userBean.getId()) && userBean.getRole()!=1) {
				String jsonStr = ResultUtil.fail("无权限").toString();
				backJson(jsonStr,resp);
				return;
			}
			
			// 删除博文
			if(blogDb.deleteBlog(blogId,authorId)) {
				// 返回结果
				String jsonStr = ResultUtil.success("删除成功").toString();
				backJson(jsonStr,resp);
				return;
			}else {
				String jsonStr = ResultUtil.fail("删除失败").toString();
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

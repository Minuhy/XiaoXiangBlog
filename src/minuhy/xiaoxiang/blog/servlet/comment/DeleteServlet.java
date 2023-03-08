package minuhy.xiaoxiang.blog.servlet.comment;

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
import minuhy.xiaoxiang.blog.database.CommentDb;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;

@WebServlet("/comment/delete")
public class DeleteServlet extends BaseHttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1585231955724729886L;
	private static final Logger log = LoggerFactory.getLogger(DeleteServlet.class);
	
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
				log.debug("没登录不能删除评论");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("请先登录",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("删除评论");
		}
		
		// 1. 获取参数
		String commetIdStr = RequestUtil.getReqParam(req, "commetId", "");
		int commetId;
		
		// 2. 检查参数格式是否正确
		if (DebugConfig.isDebug) {
			log.debug("参数：{}",  commetIdStr);
		}
		
		try {
			commetId = Integer.parseInt(commetIdStr);// valueOf(commetIdStr);
			if(commetId<1) {
				throw new NumberFormatException("评论ID范围不正确");
			}
		}catch (NumberFormatException e) {
			if (DebugConfig.isDebug) {
				log.debug("评论ID格式不对", commetIdStr);
			}

			String jsonStr = ResultUtil.fail("评论编号错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. 业务逻辑
		CommentDb commentDb = new CommentDb();
		
		try {
			// 查询是否有权限
			int[] id = commentDb.getUserIdAndBlogIdByCommentId(commetId);
			int authorId = id[0];
			int blogId = id[1];
			if((authorId != userBean.getId()) && userBean.getRole()!=1) {
				String jsonStr = ResultUtil.fail("无权限").toString();
				backJson(jsonStr,resp);
				return;
			}
			
			// 删除博文
			if(commentDb.deleteComment(blogId,commetId,TimeUtil.getTimestampMs())) {
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

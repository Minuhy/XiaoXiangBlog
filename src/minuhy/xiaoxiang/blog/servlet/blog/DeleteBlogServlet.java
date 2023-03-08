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
 * ɾ������
 * 
 * ʹ��JSON����
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
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼����ɾ������");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("���ȵ�¼",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("ɾ������");
		}
		
		// 1. ��ȡ����
		String blogId = RequestUtil.getReqParam(req, "blogId", "");
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}",  blogId);
		}
		
		try {
			Integer.valueOf(blogId);
		}catch (NumberFormatException e) {
			if (DebugConfig.isDebug) {
				log.debug("����ID��ʽ����", blogId);
			}

			String jsonStr = ResultUtil.fail("���ı�Ŵ���").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. ҵ���߼�
		BlogDb blogDb = new BlogDb();
		
		try {
			// ��ѯ�Ƿ���Ȩ��
			int authorId =blogDb.getBlogAuthorIdByBlogId(blogId);
			if((authorId != userBean.getId()) && userBean.getRole()!=1) {
				String jsonStr = ResultUtil.fail("��Ȩ��").toString();
				backJson(jsonStr,resp);
				return;
			}
			
			// ɾ������
			if(blogDb.deleteBlog(blogId,authorId)) {
				// ���ؽ��
				String jsonStr = ResultUtil.success("ɾ���ɹ�").toString();
				backJson(jsonStr,resp);
				return;
			}else {
				String jsonStr = ResultUtil.fail("ɾ��ʧ��").toString();
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

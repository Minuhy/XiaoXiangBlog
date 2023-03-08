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
		String commetIdStr = RequestUtil.getReqParam(req, "commetId", "");
		int commetId;
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}",  commetIdStr);
		}
		
		try {
			commetId = Integer.parseInt(commetIdStr);// valueOf(commetIdStr);
			if(commetId<1) {
				throw new NumberFormatException("����ID��Χ����ȷ");
			}
		}catch (NumberFormatException e) {
			if (DebugConfig.isDebug) {
				log.debug("����ID��ʽ����", commetIdStr);
			}

			String jsonStr = ResultUtil.fail("���۱�Ŵ���").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. ҵ���߼�
		CommentDb commentDb = new CommentDb();
		
		try {
			// ��ѯ�Ƿ���Ȩ��
			int[] id = commentDb.getUserIdAndBlogIdByCommentId(commetId);
			int authorId = id[0];
			int blogId = id[1];
			if((authorId != userBean.getId()) && userBean.getRole()!=1) {
				String jsonStr = ResultUtil.fail("��Ȩ��").toString();
				backJson(jsonStr,resp);
				return;
			}
			
			// ɾ������
			if(commentDb.deleteComment(blogId,commetId,TimeUtil.getTimestampMs())) {
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

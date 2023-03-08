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
 * ������
 * 
 * ʹ��JSON����
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
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼���ܷ�����");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("���ȵ�¼",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("��������");
		}
		
		// 1. ��ȡ����
		String title = RequestUtil.getReqParam(req, "title", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{} {}", title, content);
		}

		if (title == null || title.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("����̫�̣�{}", title);
			}

			String jsonStr = ResultUtil.fail("���ⳤ��̫��").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (title.length() > 200) {

			if (DebugConfig.isDebug) {
				log.debug("����̫����{}", title);
			}

			String jsonStr = ResultUtil.fail("���ⳤ��̫�����200�֣�").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (content == null || content.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("����̫�̣�{}", content);
			}

			String jsonStr = ResultUtil.fail("���ĳ���̫��").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (TextUtil.getStringLenByUtf8(content) > 65000) {

			if (DebugConfig.isDebug) {
				log.debug("����̫����{}", content);
			}

			String jsonStr = ResultUtil.fail("����̫�����������ǧ���ֽڣ�").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// �������Ķ���
		BlogEntity blogEntity = new BlogEntity();
		blogEntity.setAuthorId(userBean.getId());
		blogEntity.setTitle(title);
		blogEntity.setContent(content);
		blogEntity.setCreateTimestamp(TimeUtil.getTimestampMs());
		
		// �������ݿ�
		BlogDb blogDb = new BlogDb();
		try {
			if(blogDb.writeBlog(blogEntity)) {
				// д��ɹ�
				String newestId = blogDb.getNewestBlogIdByUserId( // ��ȡ����ID
						String.valueOf(userBean.getId())
						);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("url", TipsJspUtil.generateLink(currentPath, 
						MsgTypeEnum.SUCCESS, 
						"�����ĳɹ�", 
						currentPath+"/read.jsp?i="+newestId,
						"�����鿴 ��"+TextUtil.maxLenJustify(title, 20)+"��",
						false));
				String jsonStr = ResultUtil.success("����ɹ�",jsonObject).toString();
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

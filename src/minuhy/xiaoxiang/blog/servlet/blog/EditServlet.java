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
 * �༭����
 * 
 * ʹ��JSON����
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
			log.debug("�޸Ĳ���");
		}
		
		// 1. ��ȡ����
		String id = RequestUtil.getReqParam(req, "id", "");
		String title = RequestUtil.getReqParam(req, "title", "");
		String content = RequestUtil.getReqParam(req, "content", "");
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{}��{}��{}", id,title, content);
		}
		
		try {
			Integer.parseInt(id);
		}catch (NumberFormatException e) {
			if (DebugConfig.isDebug) {
				log.debug("ID����{}", id);
			}
			String jsonStr = ResultUtil.fail("��������").toString();
			backJson(jsonStr,resp);
			return;
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
		
		// �������ݿ�
		BlogDb blogDb = new BlogDb();
		try {
			BlogEntity entity = blogDb.getBlogById(id);
			if(entity == null) {
				if (DebugConfig.isDebug) {
					log.debug("�鲻��Ҫ�޸ĵ�����",id);
				}

				String jsonStr = ResultUtil.fail("���޴���").toString();
				backJson(jsonStr,resp);
				return;
			}
			
			// ���˻����Ա���޸�
			if(entity.getAuthorId() == userBean.getId() || userBean.getRole() == 1) {
				
				entity.setTitle(title);
				entity.setContent(content);
				entity.setUpdateTimestamp(TimeUtil.getTimestampMs());
				
				
				if(blogDb.editBlog(entity)) {
					// д��ɹ�
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("url", TipsJspUtil.generateLink(currentPath, 
							MsgTypeEnum.SUCCESS, 
							"�����޸ĳɹ�", 
							currentPath+"/read.jsp?i="+id,
							"�����鿴 ��"+TextUtil.maxLenJustify(title, 20)+"��",
							false));
					String jsonStr = ResultUtil.success("�޸ĳɹ�",jsonObject).toString();
					backJson(jsonStr,resp);
					return;
				}else {
					if (DebugConfig.isDebug) {
						log.debug("д�����ݿ�ʧ��");
					}
	
					String jsonStr = ResultUtil.fail("����ʧ��").toString();
					backJson(jsonStr,resp);
					return;
				}
			}else {
				String jsonStr = ResultUtil.fail("û��Ȩ��").toString();
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

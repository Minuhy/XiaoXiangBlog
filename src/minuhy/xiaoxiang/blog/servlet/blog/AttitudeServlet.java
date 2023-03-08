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
 * ���޳���
 * 
 * ʹ��JSON����
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
			// û�е�¼
			if (DebugConfig.isDebug) {
				log.debug("û��¼���ܵ���");
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", currentPath+"/login.jsp");
			String jsonStr = ResultUtil.error("���ȵ�¼",jsonObject).toString();
			backJson(jsonStr,resp);
			return;
		}
		
		if (DebugConfig.isDebug) {
			log.debug("����");
		}
		
		// 1. ��ȡ����
		String stateStr = RequestUtil.getReqParam(req, "state", "");
		String blogId = RequestUtil.getReqParam(req, "blogId", "");
		
		// 2. ��������ʽ�Ƿ���ȷ
		if (DebugConfig.isDebug) {
			log.debug("������{} {}", stateStr, blogId);
		}

		if (stateStr == null || stateStr.length() < 1) {

			if (DebugConfig.isDebug) {
				log.debug("����״̬����ȷ��{}", stateStr);
			}

			String jsonStr = ResultUtil.fail("״̬����ȷ").toString();
			backJson(jsonStr,resp);
			return;
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
		
		// ����״̬
		int state = 0;
		String tip = "ȡ���ɹ�";
		if(stateStr.toLowerCase().equals("support")) {
			state = 1;
			tip = "֧�ֳɹ�";
		}else if(stateStr.toLowerCase().equals("unsupport")){
			state = -1;
			tip = "���Գɹ�";
		}
		
		// 3. ҵ���߼�
		
		// �������ݿ�
		LikeDb likeDb = new LikeDb();
		try {
			if(likeDb.setLike(
					state, 
					userBean.getId(), 
					blogId, 
					TimeUtil.getTimestampMs()
				)) {
				// д��ɹ�

				if (DebugConfig.isDebug) {
					log.debug("��������д��ɹ���{}", blogId);
				}
				
				try { // ��Ϣ
					// �жϵ�ǰ���ĵ���������������������һ��ֵ���û�������Ϣ
					BlogDb blogDb = new BlogDb();
					int[] likeCount = blogDb.getBlogLikeCountByBlogId(blogId);
					
					if(likeCount[0]>likeCount[1]) { // �ж��Ƿ���Ҫ������Ϣ
						String msgTitle = null;
						int likeMsgSendCount = 0;
						if(likeCount[0]>=1&&likeCount[1]<1) {
							msgTitle = "��һ����";
							likeMsgSendCount = 1;
						}else if(likeCount[0]>=10&&likeCount[1]<10){ // ʮ
							msgTitle = "ʮ����";
							likeMsgSendCount = 10;
						}else if(likeCount[0]>=100&&likeCount[1]<100){ // ��
							msgTitle = "һ�ٸ���";
							likeMsgSendCount = 100;
						}else if(likeCount[0]>=1000&&likeCount[1]<1000){ // ǧ
							msgTitle = "һǧ����";
							likeMsgSendCount = 1000;
						}else if(likeCount[0]>=10000&&likeCount[1]<10000){ // ��
							msgTitle = "һ�����";
							likeMsgSendCount = 10000;
						}else if(likeCount[0]>=100000&&likeCount[1]<100000){ // ʮ��
							msgTitle = "ʮ�����";
							likeMsgSendCount = 100000;
						}else if(likeCount[0]>=1000000&&likeCount[1]<1000000){ // ����
							msgTitle = "һ�������";
							likeMsgSendCount = 1000000;
						}else if(likeCount[0]>=10000000&&likeCount[1]<10000000){ // ǧ��
							msgTitle = "һǧ�����";
							likeMsgSendCount = 10000000;
						}else if(likeCount[0]>=100000000&&likeCount[1]<100000000){ // ��
							msgTitle = "һ�ڸ��ޣ�";
							likeMsgSendCount = 100000000;
						}
						
						if(msgTitle!=null) { // ��Ҫ������Ϣ
							String title = blogDb.getBlogTitleByBlogId(blogId);
							int authorId = blogDb.getBlogAuthorIdByBlogId(blogId);
							if(authorId != userBean.getId()) { // ������Լ������÷�
								if(authorId!=0) {
									SendMessageBean msgSender = new SendMessageBean();
									
									msgSender.setTitle("������¡�"+TextUtil.maxLenJustify(title, 18)+"���յ���" + msgTitle);
									msgSender.setContent("���ڲ����Ϸ�������¡�"+title+"���ܵ��������û��Ļ�ӭ���ش�֪ͨ��ϣ�������ٽ�������Ϊ������Ѵ������м�ֵ�����£�");
									msgSender.setSenderId(userBean.getId());
									msgSender.setReceiverId(authorId);
									msgSender.setMsgType(MessageTypeConfig.LIKE);
									msgSender.setTargetUrl(UrlGeneratorUtil.getReadUrl(blogId));
									if(!msgSender.send()) {
										log.warn("������Ϣ����ʧ�ܣ�{}",blogDb);
									}else {
										// ������Ϣ�ɹ���������Ϣ����
										blogDb.setLikeMsgSendCount(blogId,likeMsgSendCount);
	
										if (DebugConfig.isDebug) {
											log.debug("������Ϣ���ͳɹ���{}", blogId);
										}
									}
								}
							}
						}
					}
				}catch (Exception e) {
					log.warn("������Ϣ����ʧ�ܣ�{}",e);
				}
				
				// ���ؽ��
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("state", state); // �������ս��
				String jsonStr = ResultUtil.success(tip,jsonObject).toString();
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

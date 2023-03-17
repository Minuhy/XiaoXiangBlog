package minuhy.xiaoxiang.blog.servlet.comment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import minuhy.xiaoxiang.blog.bean.user.UserBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.database.CommentDb;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.CommentEntity;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
import minuhy.xiaoxiang.blog.util.RequestUtil;
import minuhy.xiaoxiang.blog.util.ResultUtil;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * ��̬��������
 * 
 * ʹ��JSON����
 * 
 * @author xxxy1116
 *
 */
@WebServlet("/comment/load")
public class DynamicLoadServlet extends BaseHttpServlet{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1805379967126937924L;
	private static final Logger log = LoggerFactory.getLogger(DynamicLoadServlet.class);
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		currentPath = req.getContextPath();
		
		UserBean userBean = getLoginUserBean(req);
		
		// 1. ��ȡ����
		String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String pageNumberStr = RequestUtil.getReqParam(req, "pageNumber", "");
		
		// 2. У�����
		int pageNumber,blogId;
		int userId = 0; // ����0��ʾδ��¼
		
		if(userBean!=null) {
			userId = userBean.getId();
		}
		
		// blogId
		try {
			blogId = Integer.parseInt(blogIdStr);
			if(blogId<1) {
				throw new NumberFormatException("����ID��Χ����");
			}
		}catch ( NumberFormatException e) {
			if(DebugConfig.isDebug) {
				log.debug("����ID������ʽ����ȷ��{}��{}",blogIdStr,e);
			}
			
			String jsonStr = ResultUtil.fail("���ı�Ŵ���").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// ҳ��
		try {
			pageNumber = Integer.parseInt(pageNumberStr);
		}catch ( NumberFormatException e) {
			pageNumber = 1;
		}
		
		if(pageNumber < 1) {
			String jsonStr = ResultUtil.fail("ҳ�����").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. ҵ���߼�
		
		// ����ҳ��
		CommentDb commentDb = new CommentDb();
		try {
			int totalItem = commentDb.getCommentTotalByBlogId(blogId);
			
			/* 
			 * �ܵ�data
			{
				'totalPageNumber': 5,
				'currentPageNumber': 5,
				'hasNext': false,
				'data': [
				    {����},
				    {����}
				]
			}
			  */
			
			boolean hasNext;
			JSONObject jsonDataObject = new JSONObject();
			
			// ���ҳ���Ƿ�Ϲ�
			int totalPageNumber = (totalItem / DatabaseConfig.PAGE_ITEM_COUNT) + ((totalItem%DatabaseConfig.PAGE_ITEM_COUNT)==0?0:1);
			
			// д����Ӧ����
			jsonDataObject.put("totalPageNumber", totalPageNumber);
			jsonDataObject.put("currentPageNumber", pageNumber);
			
			if(pageNumber > totalPageNumber) {
				// ҳ�볬���˷�Χ������Ҫ������
				hasNext = false;
				jsonDataObject.put("hasNext", hasNext);
				jsonDataObject.put("data",new ArrayList<JSONObject>());
				// String jsonStr = ResultUtil.success("ҳ�볬����Χ",jsonDataObject).toString();
				String jsonStr = ResultUtil.success(jsonDataObject).toString();
				backJson(jsonStr,resp);
				return;
			}

			// �����ݿ���ȡ��
			CommentEntity[] entitys = commentDb.getBlogCommentsByPageOrderByTime(
					blogId, 
					pageNumber-1 // ���ݿ���ҳ���Ǵ�0��ʼ�ģ�ת��һ��
				); 
			
			// �鲩�͵�����
			int blogAuthorId = 0;
			try {
				BlogDb blogDb = new BlogDb();
				blogAuthorId = blogDb.getBlogAuthorIdByBlogId(blogIdStr);
			}catch (Exception e) {
				log.error("�����۴���ѯ��������ʱ����"+e.getMessage());
			}
			
			if(entitys!=null && entitys.length>0) {
				// �������ݣ����и�������
				if(pageNumber < totalPageNumber) {
					hasNext = true;
				}else { // if(pageNumber == totalPageNumber)
					hasNext = false;
				}
				jsonDataObject.put("hasNext", hasNext);
				/*
					id:4, // ����ID
			        userId:20, // �Ķ��ߵ�id�����δ��¼Ϊ0
			        authorId:21, // ������ID
			        blogAuthorId:66, // ��Ӧ�������ߵ�ID
			        authorNick:"������", // �������ǳ�
			        baseUrl:'/XiaoXiangBlog', // ����·��
			        avatar:"h096", // ͷ���ļ�����������׺��
			        replay:{ // ���ظ����û���Ϣ
			            id:1, // ����ID��������ظ��ı�ǩ����ת�����ظ�������λ��
			            nick:"����������", // ���������ǳ�
			            content:'���õ���������' // 
			        },
			        content:"��������", // ��������
			        datetime:"2022-6-7 13:24" // ���۷����ʱ��
				 */
				ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
				UserDb userDb = new UserDb();
				
				Map<Integer, String> nickCeche = new HashMap<Integer, String>();
				Map<Integer, JSONObject> replayDataCeche = new HashMap<Integer, JSONObject>();
				Map<Integer, Integer> avatarCeche = new HashMap<Integer, Integer>();
				
				for(CommentEntity entity : entitys) {

					// ������쳣������Ժ��Ե�������Ҫ����ʧ��
					try {
						
						// ���ǳƺ�ͷ�񣨿���һ���򵥵��ڴ滺�棬����Ӧ�úܵͣ�
						UserEntity userEntity = null;
						if(nickCeche.containsKey(entity.getUserId()) 
								&& avatarCeche.containsKey(entity.getUserId())) {
							userEntity = new UserEntity();
							userEntity.setNick(nickCeche.get(entity.getUserId()));
							userEntity.setAvatar(avatarCeche.get(entity.getUserId()));
						}else {
							userEntity = userDb.getCommentUserInfoById(entity.getUserId());
						}
						
						String userNick = userEntity==null?"�û���ע��":userEntity.getNick();
						nickCeche.put(entity.getUserId(), userNick);
						int userAvatar = userEntity==null?0:userEntity.getAvatar();
						avatarCeche.put(entity.getUserId(), userAvatar);
						
						
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("id",entity.getId()); // ����ID
						jsonObject.put("userId",userId); // �Ķ��ߵ�id�����δ��¼Ϊ0
						jsonObject.put("authorId",entity.getUserId()); // ������ID
						jsonObject.put("blogAuthorId",blogAuthorId); // ��Ӧ�������ߵ�ID
						jsonObject.put("authorNick",userNick); // �������ǳ�
						jsonObject.put("baseUrl",currentPath); // ����·��
						jsonObject.put("avatar",String.format("h%03d", userAvatar)); // ͷ���ļ�����������׺��
						jsonObject.put("content",entity.getContent()); // ��������
						jsonObject.put("datetime",
								TimeUtil.timestamp2DateTime(
										entity.getCreateTimestamp()
										)
								); // ���۷����ʱ��
						
						// ���ظ���ID
						if(entity.getReplyId()>0) {
							
							// ��ظ����ݣ�����һ���򵥵��ڴ滺�棬����Ӧ�úܵͣ�
							JSONObject jsonRepliedObject = null;
							if(replayDataCeche.containsKey(entity.getReplyId())) {
								jsonRepliedObject = replayDataCeche.get(entity.getReplyId());
							}else {
								String repliedNick = userDb.getNickByReplyId(entity.getReplyId());
								String repliedContent = commentDb.getCommentContentById(entity.getReplyId());
		
								jsonRepliedObject = new JSONObject();
								jsonRepliedObject.put("id", entity.getReplyId()); // ����ID��������ظ��ı�ǩ����ת�����ظ�������λ��
								jsonRepliedObject.put("nick", repliedNick); // ���ظ������������ǳ�
								jsonRepliedObject.put("content", repliedContent); // ���ظ�����������
								
								jsonObject.put("replay",jsonRepliedObject); // ���ظ����û���Ϣ
								
								replayDataCeche.put(entity.getReplyId(), jsonRepliedObject);
							}
						}
						
						dataList.add(jsonObject);
					}catch (SQLException e) {
						if(DebugConfig.isDebug) {
							log.debug("��ѯ������ϸ��Ϣʱ���ݿ����{}",e);
						}
					}
				}
				// �ͷŻ���
				// TODO ���Ľ�����ʱ�ͷţ�ÿ���ͷ�̫�˷���
				nickCeche = null;
				replayDataCeche = null;
				avatarCeche = null;
				
				jsonDataObject.put("data",dataList);
				
				String jsonStr = ResultUtil.success(jsonDataObject).toString();
				backJson(jsonStr,resp);
				return;
			}else {
				// ���ݿ��ѯ����
				String jsonStr = ResultUtil.error("��ȡʱ����").toString();
				backJson(jsonStr,resp);
				return;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error("���ݿ����{}", e);
			
			String jsonStr = ResultUtil.error("���ݿ����").toString();
			backJson(jsonStr,resp);
			return;
		}
		
	}
}

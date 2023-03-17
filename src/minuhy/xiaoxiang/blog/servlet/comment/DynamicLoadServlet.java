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
 * 动态加载评论
 * 
 * 使用JSON交互
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
		
		// 1. 获取参数
		String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String pageNumberStr = RequestUtil.getReqParam(req, "pageNumber", "");
		
		// 2. 校验参数
		int pageNumber,blogId;
		int userId = 0; // 这里0表示未登录
		
		if(userBean!=null) {
			userId = userBean.getId();
		}
		
		// blogId
		try {
			blogId = Integer.parseInt(blogIdStr);
			if(blogId<1) {
				throw new NumberFormatException("博客ID范围错误");
			}
		}catch ( NumberFormatException e) {
			if(DebugConfig.isDebug) {
				log.debug("博客ID参数格式不正确：{}，{}",blogIdStr,e);
			}
			
			String jsonStr = ResultUtil.fail("博文编号错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 页码
		try {
			pageNumber = Integer.parseInt(pageNumberStr);
		}catch ( NumberFormatException e) {
			pageNumber = 1;
		}
		
		if(pageNumber < 1) {
			String jsonStr = ResultUtil.fail("页码错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		
		// 3. 业务逻辑
		
		// 查总页数
		CommentDb commentDb = new CommentDb();
		try {
			int totalItem = commentDb.getCommentTotalByBlogId(blogId);
			
			/* 
			 * 总的data
			{
				'totalPageNumber': 5,
				'currentPageNumber': 5,
				'hasNext': false,
				'data': [
				    {数据},
				    {数据}
				]
			}
			  */
			
			boolean hasNext;
			JSONObject jsonDataObject = new JSONObject();
			
			// 检查页数是否合规
			int totalPageNumber = (totalItem / DatabaseConfig.PAGE_ITEM_COUNT) + ((totalItem%DatabaseConfig.PAGE_ITEM_COUNT)==0?0:1);
			
			// 写入响应数据
			jsonDataObject.put("totalPageNumber", totalPageNumber);
			jsonDataObject.put("currentPageNumber", pageNumber);
			
			if(pageNumber > totalPageNumber) {
				// 页码超出了范围，不需要查数据
				hasNext = false;
				jsonDataObject.put("hasNext", hasNext);
				jsonDataObject.put("data",new ArrayList<JSONObject>());
				// String jsonStr = ResultUtil.success("页码超出范围",jsonDataObject).toString();
				String jsonStr = ResultUtil.success(jsonDataObject).toString();
				backJson(jsonStr,resp);
				return;
			}

			// 从数据库中取出
			CommentEntity[] entitys = commentDb.getBlogCommentsByPageOrderByTime(
					blogId, 
					pageNumber-1 // 数据库中页数是从0开始的，转换一下
				); 
			
			// 查博客的作者
			int blogAuthorId = 0;
			try {
				BlogDb blogDb = new BlogDb();
				blogAuthorId = blogDb.getBlogAuthorIdByBlogId(blogIdStr);
			}catch (Exception e) {
				log.error("在评论处查询文章作者时出错："+e.getMessage());
			}
			
			if(entitys!=null && entitys.length>0) {
				// 返回数据，还有更多数据
				if(pageNumber < totalPageNumber) {
					hasNext = true;
				}else { // if(pageNumber == totalPageNumber)
					hasNext = false;
				}
				jsonDataObject.put("hasNext", hasNext);
				/*
					id:4, // 评论ID
			        userId:20, // 阅读者的id，如果未登录为0
			        authorId:21, // 发表者ID
			        blogAuthorId:66, // 对应文章作者的ID
			        authorNick:"清米子", // 发表者昵称
			        baseUrl:'/XiaoXiangBlog', // 基础路径
			        avatar:"h096", // 头像文件名（不含后缀）
			        replay:{ // 被回复的用户信息
			            id:1, // 评论ID，点击被回复的标签后跳转到被回复的评论位置
			            nick:"清蒸玉米子", // 评论作者昵称
			            content:'引用的评论内容' // 
			        },
			        content:"评论内容", // 评论内容
			        datetime:"2022-6-7 13:24" // 评论发表的时间
				 */
				ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
				UserDb userDb = new UserDb();
				
				Map<Integer, String> nickCeche = new HashMap<Integer, String>();
				Map<Integer, JSONObject> replayDataCeche = new HashMap<Integer, JSONObject>();
				Map<Integer, Integer> avatarCeche = new HashMap<Integer, Integer>();
				
				for(CommentEntity entity : entitys) {

					// 这里的异常处理可以忽略掉，不需要整个失败
					try {
						
						// 查昵称和头像（可做一个简单的内存缓存，命中应该很低）
						UserEntity userEntity = null;
						if(nickCeche.containsKey(entity.getUserId()) 
								&& avatarCeche.containsKey(entity.getUserId())) {
							userEntity = new UserEntity();
							userEntity.setNick(nickCeche.get(entity.getUserId()));
							userEntity.setAvatar(avatarCeche.get(entity.getUserId()));
						}else {
							userEntity = userDb.getCommentUserInfoById(entity.getUserId());
						}
						
						String userNick = userEntity==null?"用户已注销":userEntity.getNick();
						nickCeche.put(entity.getUserId(), userNick);
						int userAvatar = userEntity==null?0:userEntity.getAvatar();
						avatarCeche.put(entity.getUserId(), userAvatar);
						
						
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("id",entity.getId()); // 评论ID
						jsonObject.put("userId",userId); // 阅读者的id，如果未登录为0
						jsonObject.put("authorId",entity.getUserId()); // 发表者ID
						jsonObject.put("blogAuthorId",blogAuthorId); // 对应文章作者的ID
						jsonObject.put("authorNick",userNick); // 发表者昵称
						jsonObject.put("baseUrl",currentPath); // 基础路径
						jsonObject.put("avatar",String.format("h%03d", userAvatar)); // 头像文件名（不含后缀）
						jsonObject.put("content",entity.getContent()); // 评论内容
						jsonObject.put("datetime",
								TimeUtil.timestamp2DateTime(
										entity.getCreateTimestamp()
										)
								); // 评论发表的时间
						
						// 被回复的ID
						if(entity.getReplyId()>0) {
							
							// 查回复数据（可做一个简单的内存缓存，命中应该很低）
							JSONObject jsonRepliedObject = null;
							if(replayDataCeche.containsKey(entity.getReplyId())) {
								jsonRepliedObject = replayDataCeche.get(entity.getReplyId());
							}else {
								String repliedNick = userDb.getNickByReplyId(entity.getReplyId());
								String repliedContent = commentDb.getCommentContentById(entity.getReplyId());
		
								jsonRepliedObject = new JSONObject();
								jsonRepliedObject.put("id", entity.getReplyId()); // 评论ID，点击被回复的标签后跳转到被回复的评论位置
								jsonRepliedObject.put("nick", repliedNick); // 被回复的评论作者昵称
								jsonRepliedObject.put("content", repliedContent); // 被回复的评论内容
								
								jsonObject.put("replay",jsonRepliedObject); // 被回复的用户信息
								
								replayDataCeche.put(entity.getReplyId(), jsonRepliedObject);
							}
						}
						
						dataList.add(jsonObject);
					}catch (SQLException e) {
						if(DebugConfig.isDebug) {
							log.debug("查询评论详细信息时数据库出错，{}",e);
						}
					}
				}
				// 释放缓存
				// TODO 待改进：定时释放，每次释放太浪费了
				nickCeche = null;
				replayDataCeche = null;
				avatarCeche = null;
				
				jsonDataObject.put("data",dataList);
				
				String jsonStr = ResultUtil.success(jsonDataObject).toString();
				backJson(jsonStr,resp);
				return;
			}else {
				// 数据库查询错误
				String jsonStr = ResultUtil.error("获取时出错").toString();
				backJson(jsonStr,resp);
				return;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error("数据库错误：{}", e);
			
			String jsonStr = ResultUtil.error("数据库错误").toString();
			backJson(jsonStr,resp);
			return;
		}
		
	}
}

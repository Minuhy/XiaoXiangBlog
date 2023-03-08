<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.SessionUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="sun.misc.Request"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<jsp:useBean id="blogBean" class="minuhy.xiaoxiang.blog.bean.blog.BlogBean"></jsp:useBean>
<jsp:useBean id="likeBean" class="minuhy.xiaoxiang.blog.bean.user.LikeBean"></jsp:useBean>
<% 
	boolean isAuthorRead = false;
	int attitude = 0; // 当前用户对本博文的态度，默认为0
	
	Object obj;
	// 尝试获取昵称
	String nick = null;
	UserBean user = null;
	
	
	// 从session中获取评论内容
	String commentContentFromSession = SessionUtil.getAttrStringAndPurge(session,
			SessionAttributeNameConfig.COMMENT_CONTENT,
			"");
	
	
	// 从URL中获取博客ID
	String blogIdStr = RequestUtil.getReqParamNotEmpty(request, "i", "0");
	
	// 自动跳到评论区
	String autoGoComment = RequestUtil.getReqParamNotEmpty(request, "comment", "false"); 
	
	// 尝试把ID转为整型
	int blogId = 0;
	try{
		blogId = Integer.parseInt(blogIdStr);
	}catch(NumberFormatException e){
		blogId = 0;
	}
	
	// 通过ID查找数据库
	if(blogId < 1){
		// ID为0或负数
		response.sendError(500,"参数不正确");
	}else{ // BlogID大于0开始
		if(!blogBean.getData(String.valueOf(blogId))){
			// 查找数据库失败
			response.sendError(404,"查无此文");
			return;
		}else{  // 查找数据库成功开始
			
			obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
			if(obj instanceof UserBean){
				user = (UserBean)obj;
				nick = user.getNick();
				
				// 查询用户点赞状态
				likeBean.getData(user.getId(), blogId);
				attitude = likeBean.getState();
			}
%>

<%
	//记录一下获取文章的时间，用于等下增加访问量
	session.setAttribute(SessionAttributeNameConfig.GET_BLOG_TIME, TimeUtil.getTimestampMs()); // 查看的时间
	session.setAttribute(SessionAttributeNameConfig.GET_BLOG_ID, blogId); // 查看的博客ID
	session.setAttribute(SessionAttributeNameConfig.GET_BLOG_USER_ID, blogBean.getUser().getId()); // 查看哪个用户的博客
	
	if(blogBean!=null && blogBean.getUser()!=null && user!=null){
		if(blogBean.getUser().getId() == user.getId()){
			isAuthorRead = true;
		}
	}
%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title><jsp:getProperty property="title" name="blogBean"/> - 潇湘博客</title>
		<style>
			.container {
				overflow: hidden;
			}
			#mainBody img{
				max-width: 100% !important; 
				height: auto!important; 
				width:expression(this.width > 600 ? "600px" : this.width)!important;
			}
		</style>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		
        <!-- 内容 -->
        <div class="container">
            <h1>
            	<%-- 标题 --%>
				<jsp:getProperty property="title" name="blogBean"/>
			</h1>
			<div style="margin:0 0 -10px 8px;height: 32px;">
				<% if(blogBean.getUpDateTime().equals("")){ %>
                <span style="line-height: 32px;">
                	<%-- 时间日期 --%>
					<jsp:getProperty property="dateTime" name="blogBean"/>
				</span>
				<%}else{ %>
				
                <div style="float:left">
	                <div style="line-height: 20px;">
	                	<%-- 时间日期 --%>
						<jsp:getProperty property="dateTime" name="blogBean"/>
					</div>
	                <div style="line-height: 12px;color:#bbb;font-size: 10px">
	                	<%-- 时间日期 --%>
						已于 <jsp:getProperty property="upDateTime" name="blogBean"/> 修改
					</div>
                </div>
				<%} %>
				
				<%-- 管理员可编辑 --%>
				<%-- 如果作者ID就是用户ID，那说明现在就是作者在看，显示编辑操作 --%>
				<%if(isAuthorRead || (user!=null && user.getRole() == 1)){ %>
					<div role="presentation" class="dropdown" style="float:right;">
				    	<button id="dropOp" class="dropdown-toggle btn btn-default" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
				        	操作
				          	<span class="caret"></span>
				       	</button>
				        <ul class="dropdown-menu pull-right" aria-labelledby="dropOp">
				        	<li><a href="<%=currentPath%>/post.jsp?ei=<%=blogId%>">编辑</a></li>
				        	<li role="separator" class="divider"></li>
				        	<li><a data-toggle="modal" data-target="#delModal" href="#" >删除</a></li>
				        </ul>
				    </div>
				<%} %>
				
            </div>
            <hr>
            <!-- 正文 -->
            <div id="mainBody" style="border: 2px #eee solid;border-radius:10px;padding: 20px;">
            	<%-- 正文 --%>
				<jsp:getProperty property="content" name="blogBean"/>
            </div>
            
            <hr>

            <div  id="commentArea" style="margin: 20px 60px;">
            	<!-- 电脑版的 -->
                <div class="hidden-xs row">
                  	<!-- 左边头像 -->
                    <div class="col-sm-6 text-left">
                        <div style="display: block;float: left;">
                            <%-- 发表者主页链接 --%>
		                    <a href="<%= currentPath %>/people.jsp?i=<%= blogBean.getUser().getId() %>">
		                    	<%-- 发表者头像链接 --%>
		                        <img src="<%= String.format(currentPath + "/img/avatar/h%03d.png", blogBean.getUser().getAvatar()) %>" alt="头像" width="80" height="80" class="img-thumbnail" />
		                    </a>
                        </div>
                        <div style="margin-left: 90px;margin-top: 18px;">
                            <h4>
                            	<%-- 发表者主页链接 --%>
		                    	<a href="<%= currentPath %>/people.jsp?i=<%= blogBean.getUser().getId() %>">
		                    		<%-- 发表者昵称 --%>
		                    		<%= blogBean.getUser().getNick() %>
		                    	</a>
	                    	</h4>
                            <p>
                            	<%-- 发表者签名 --%>
	                    		<%= blogBean.getUser().getSignature() %>
	                    	</p>
                        </div>
                    </div>
                    <!-- 右边点赞 -->
                    <div  class="col-sm-6 text-right">
                        <div class="text-center" style="display: inline-block;height: 80px;">
                            <p style="margin-bottom: 18px;">
								阅读(<jsp:getProperty property="readCount" name="blogBean"/>)&nbsp;
				                支持(<span id="likeSpan1"></span>)&nbsp; 
				                评论(<span id="commentSpan1"></span>)&nbsp;
							</p>
                            <button id="buttonS1" type="button" class="btn btn-default">
                                <img id="imgS1" src="img/svg/hand-thumbs-up.svg" alt="喜欢">
                                &nbsp;
                                <span id="textS1" style="line-height: 16px;">支持</span>
                            </button>
                            &nbsp;&nbsp;
                            <button id="buttonU1" type="button" class="btn btn-default">
                                <img id="imgU1" src="img/svg/hand-thumbs-down.svg" alt="不喜欢">
                                &nbsp;
                                <span id="textU1" style="line-height: 16px;">反对</span>
                            </button>
                        </div>
                    </div>

                </div> <!-- 电脑版结束 -->
                
                <!-- 下面是手机版的 -->
                <div class="hidden-sm hidden-md hidden-lg">
                	<!-- 上边头像 -->
	                <div class="row text-center">
	                    <%-- 发表者主页链接 --%>
	                    <a href="<%= currentPath %>/people.jsp?i=<%= blogBean.getUser().getId() %>">
	                    	<%-- 发表者头像链接 --%>
	                        <img src="<%= String.format(currentPath + "/img/avatar/h%03d.png", blogBean.getUser().getAvatar()) %>" alt="头像" class="img-thumbnail" />
	                    </a>
	                    <h4 style="margin: 5px;">
	                    	<%-- 发表者主页链接 --%>
	                    	<a href="<%= currentPath %>/people.jsp?i=<%= blogBean.getUser().getId() %>">
	                    		<%-- 发表者昵称 --%>
	                    		<%= blogBean.getUser().getNick() %>
	                    	</a>
	                    </h4>
	                    <%-- 发表者签名 --%>
	                    <%= blogBean.getUser().getSignature() %>
	                </div>
                	<!-- 数据统计 -->
	                <p class="text-center">
		                阅读(<jsp:getProperty property="readCount" name="blogBean"/>)&nbsp;
		                支持(<span id="likeSpan2"></span>)&nbsp; 
		                评论(<span id="commentSpan2"></span>)&nbsp;
	                </p>

                	<!-- 下边点赞 -->
	                <div  class="row text-center">
	                    <button id="buttonS2" type="button" class="btn btn-default">
							<img id="imgS2" src="img/svg/hand-thumbs-up.svg" alt="喜欢">
							&nbsp;
							<span id="textS2" style="line-height: 16px;">支持</span>
						</button>
						&nbsp;&nbsp;
						<button id="buttonU2" type="button" class="btn btn-default">
							<img id="imgU2" src="img/svg/hand-thumbs-down.svg" alt="不喜欢">
							&nbsp;
							<span id="textU2" style="line-height: 16px;">反对</span>
						</button>
	                </div>
            	</div><!-- 手机版结束 -->
            	
            </div>
            
           <br>
			<hr>
           <!-- 评论发表 -->
           <form class="text-right" action="<%= currentPath %>/comment/post" method="post" >
           		<%-- 从session中获取的之前的评论缓存 --%>
               	<textarea name="content" class="form-control" rows="3" placeholder="请输入评论" required="required" 
               	<%-- 如果有评论缓存则自动跳到这里，继续用户的评论操作 --%>
               	<%= TextUtil.isEmpty(commentContentFromSession)?"":"autofocus=\"autofocus\" " %> ><%= commentContentFromSession %></textarea>
               	<br>
               	<input name="blogId" type="hidden" value="<%= blogBean.getId() %>" />
               	<button type="submit" class="btn btn-primary">发表</button>
           </form>
           <br>
            
			<jsp:include page="partial/comment.jsp"></jsp:include>
		</div>

		<%-- 如果作者ID就是用户ID，那说明现在就是作者在看，显示编辑操作 --%>
		<%if(isAuthorRead || (user!=null && user.getRole() == 1)){ %>
		<!-- 删除模态框（Modal） -->
		<div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-labelledby="delModalLabel" aria-hidden="true">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="delModalLabel">确认删除这篇博客吗？</h4>
		            </div>
		            <div class="modal-footer">
		                <button type="button"  id="btnDelBlog" class="btn btn-primary">删除</button>
		                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
		            </div>
		        </div><!-- /.modal-content -->
		    </div><!-- /.modal -->
		</div>
		<%} %>
        
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
		<!-- 阅读量相关 -->
		<script type="text/javascript">
			$(document).ready(function(){
				// 打开页面十二秒后增加阅读量
				setTimeout(function(){
					$.post("<%= currentPath %>/blog/count/read",{
	                    timestamp:"<%=session.getAttribute(SessionAttributeNameConfig.GET_BLOG_TIME)%>",
	                    blogId:"<%=session.getAttribute(SessionAttributeNameConfig.GET_BLOG_ID)%>",
	                    blogAuthorId:"<%=session.getAttribute(SessionAttributeNameConfig.GET_BLOG_USER_ID)%>"
	                },
	                function(data,status){
	                	console.log(status);
	                	console.log(data['code']);
	                	console.log(data['msg']);
	                	console.log(data['data']);
	                });
				},10000);
			} );
		</script>
		<%-- 如果作者ID就是用户ID，那说明现在就是作者在看，显示编辑操作 --%>
		<%if(isAuthorRead || (user!=null && user.getRole() == 1)){ %>
		<!-- 修改相关 -->
		<script type="text/javascript">
		$(document).ready(function(){
			$("#btnDelBlog").click(function(){
				// 点击了操作按钮
				console.log("删除");
				$.post("<%= currentPath %>/blog/delete",{
                    blogId:"<%=session.getAttribute(SessionAttributeNameConfig.GET_BLOG_ID)%>"
                },
                function(data,status){
            	console.log("数据: \n" + data + "\n状态: " + status);
            	if(status == "success"){
            		
            		// 设置状态
            		let msgType = 'info';
            		if(data['code']!=undefined){
            			if(data['code'] == 200){
            				msgType = 'success';
            				setTimeout(function(){
            					window.opener=null;
            					window.open('','_self');
            					window.close();
            				},2000);
            			}else if(data['code'] == 500){
            				msgType = 'warning';
            			}
            		}
            		
            		// 提示消息
            		if(data['msg']!=undefined){
        				Toast(data['msg'],2000,msgType);
            		}
            		
            		
            		if(data['data']!=undefined){
                        // 判断是否需要跳转
            			if(undefined != data['data']['url']){
            				sikpNewPage(data['data']['url']);
            			}
            		}
            	}
            });
			});
		});
		</script>
		<%} %>
		
		<!-- 点赞相关 -->
		<script type="text/javascript">
			var preState = <%=attitude%>; // 读者表态
			var likeCount = <%=blogBean.getLikeCount()%>; // 喜欢计数

			var defStaImgSupport = "<%=currentPath%>/img/svg/hand-thumbs-up.svg"; // 默认的支持的图片
			var defStaImgUnsupport = "<%=currentPath%>/img/svg/hand-thumbs-down.svg"; // 默认的反对的图片
			var defStaClassSupport = "btn-default"; // 默认的支持的类
			var defStaClassUnsupport = "btn-default"; // 默认的反对的类
			var defStaTextSupport = "支持"; // 默认的支持的文本
			var defStaTextUnsupport = "反对"; // 默认的反对的文本

			var actStaImgSupport = "<%=currentPath%>/img/svg/hand-thumbs-up-fill.svg"; // 激活的支持的图片
			var actStaImgUnsupport = "<%=currentPath%>/img/svg/hand-thumbs-down-fill.svg"; // 激活的反对的图片
			var actStaClassSupport = "btn-info"; // 激活的支持的类
			var actStaClassUnsupport = "btn-warning"; // 激活的反对的类
			var actStaTextSupport = "已支持"; // 激活的支持的文本
			var actStaTextUnsupport = "已反对"; // 激活的反对的文本
			 
			var attitudeUrl = "<%= currentPath %>/blog/attitude"; // 点赞的提交地址
			var blogId = <%=session.getAttribute(SessionAttributeNameConfig.GET_BLOG_ID)%>; // 博客ID
		</script>
		<script type="text/javascript" src="<%= currentPath %>/lib/xiaoxiang/js/read/likeview.js"></script>
		<!-- 评论相关 -->
		<script type="text/javascript">
			var curCommentCount = <jsp:getProperty property="commentCount" name="blogBean"/>;
			var autoGoComment = <%=autoGoComment%>;
			var currentPath = "<%=currentPath%>";
			var blogId = <%=session.getAttribute(SessionAttributeNameConfig.GET_BLOG_ID)%>; // 博客ID
		</script>
		<script type="text/javascript" src="<%= currentPath %>/lib/xiaoxiang/js/read/comment.js"></script>
	</body>
</html>
<%
		} // 查找数据库成功结束
	} // BlogID大于0结束
%>
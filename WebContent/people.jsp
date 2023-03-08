<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.blog.BlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserInfoBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String currentPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 个人主页</title>
		<%
			// 设置导航栏
				request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_MY_NUMBER);
		%>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		<jsp:useBean id="paginationBean" class="minuhy.xiaoxiang.blog.bean.PaginationBean" ></jsp:useBean>
		
		<%
		Object obj;
			// 尝试获取用户
			UserBean user = null;
			obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
			if(obj instanceof UserBean){
		user = (UserBean)obj;
			}
			
			// 获取页面参数
			int pageNumber;
			String pageStr = RequestUtil.getReqParam(request, "p", "1"); // 从参数中获取的大1
			try{
		pageNumber = Integer.parseInt(pageStr);
			}catch(NumberFormatException e){
		pageNumber = 1;
			}
			
			// 从URL中获取用户ID，账号
			String peopleIdStr = RequestUtil.getReqParam(request, "i", "0");
			String peopleAccount = RequestUtil.getReqParam(request, "a", "");
			
			// 尝试把ID转为整型
			int peopleId = 0;
			try{
		peopleId = Integer.parseInt(peopleIdStr);
			}catch(NumberFormatException e){
		peopleId = 0;
			}
			
			if(peopleId == 0 && user!=null){
		peopleId = user.getId();
			}
			
			if(!peopleAccount.equals("")){ // 优先使用账号查找用户资料
		peopleId = user.getIdByAccount(peopleAccount);
			}
	%>
		
		
		<!-- 内容 -->
        <div class="container">
        	<jsp:useBean id="userInfo" class="minuhy.xiaoxiang.blog.bean.user.UserInfoBean" ></jsp:useBean>
	        <% if(peopleId>0 || !peopleAccount.equals("")){ 

				if(pageNumber<=1){
					// 个人资料页，显示资料
					userInfo.getData(peopleId);
				%>
					<div class="row text-center" style="margin: 40px;">
						<img src="<%= String.format(currentPath + "/img/avatar/h%03d.png", userInfo.getAvatar()) %>" alt="头像" width="200" height="200" class="img-thumbnail" />
		            </div>
		            <h5 style="font-size:16px;text-align: center;margin: 20px;font-size: 20px;">
		            	<!-- 总访问量 -->
		                <span class="glyphicon glyphicon-eye-open label label-default" aria-hidden="true">&nbsp;<%= userInfo.getBlogReadCount() %></span>
		                <!-- 总点赞量 -->
		                <span class="glyphicon glyphicon-thumbs-up label label-default" aria-hidden="true">&nbsp;<%= userInfo.getBlogLikeCount() %></span>
		                <!-- 总博客数 -->
		                <span class="glyphicon glyphicon-pencil label label-default" aria-hidden="true">&nbsp;<%= userInfo.getBlogCount() %></span>
		            </h5>
		            
            		<h4 class="lead text-center" style="font-size: 36px;"><%= userInfo.getNick() %></h4>
            		<% if(userInfo.getLastLoginIp()!=null && (!userInfo.getLastLoginIp().equals(""))){ %>
            			<p class="text-center text-muted">最后登录IP：<%=userInfo.getLastLoginIp()%></p>
            		<%} %>
            		<% if(user!=null&&userInfo.getId()==user.getId()&&userInfo.getLastLoginTimestamp()!=0){ %>
            			<p class="text-center text-muted">最后登录时间：<%=TimeUtil.timestamp2DateTime(userInfo.getLastLoginTimestamp())%></p>
            		<%} %>
		            <!-- 资料 -->
		            
		            <hr>
		            <h1>个人资料</h1>
		            <div class="input-group input-group-lg">
		                <span class="input-group-addon" id="profile2">签名</span>
		                <input value="<%= userInfo.getSignature()==null?"":userInfo.getSignature() %>" type="text" class="form-control" style="background-color:ivory;"  readonly="readonly" aria-describedby="profile2">
		            </div>
		            <br>
		            <div class="input-group input-group-lg">
		                <span class="input-group-addon" id="profile3">性别</span>
		                <input value="<%= userInfo.getSexStr()==null?"":userInfo.getSexStr() %>" type="text" class="form-control" style="background-color:ivory;"  readonly="readonly" aria-describedby="profile3">
		            </div>
		            <br>
		            <div class="input-group input-group-lg">
		                <span class="input-group-addon" id="profile4">家乡</span>
		                <input value="<%= userInfo.getHometown()==null?"":userInfo.getHometown() %>" type="text" class="form-control" style="background-color:ivory;"  readonly="readonly" aria-describedby="profile4">
		            </div>
		            <br>
		            <div class="input-group input-group-lg">
		                <span class="input-group-addon" id="profile4">联系方式</span>
		                <input value="<%= userInfo.getLink()==null?"":userInfo.getLink() %>" type="text" class="form-control" style="background-color:ivory;"  readonly="readonly" aria-describedby="profile4">
		            </div>
		            <!-- 博客列表 -->
		            <hr>
		            <h1>个人博文</h1>
				<%}else{ 
					String targetNick = userInfo.getUserNiceById(peopleId);
				%>
					<h1><%= targetNick %>的博文</h1>
	            <%} %>
	            <div>
	                <jsp:useBean id="timeBlogsBean" class="minuhy.xiaoxiang.blog.bean.blog.TimeBlogsBean" scope="session"></jsp:useBean>
	                <% // 获取个人博文
	                try{
		        		// 获取数据
		        		timeBlogsBean.setUserId(peopleId);
		        		BlogBean[] blogs = timeBlogsBean.getDataByPage(pageNumber - 1);
		        		// 设置页面配置
		        		paginationBean.setTotal(timeBlogsBean.getTotal());
		        		paginationBean.setCurrent(pageNumber);
	
		        		if(blogs!=null && blogs.length>0){
							// 加载博文信息
							for(BlogBean blog:blogs){
							%>
			                <blockquote>
			                    <h2 class="text-primary">
			                        <a href="<%=currentPath %>/read.jsp?i=<%=blog.getId()%>" target="_blank">
			                        	<%=TextUtil.maxLen(blog.getTitle(), 50) %>
			                        </a>
			                    </h2>
			                    <h4 class="text-muted"><%= blog.getDateTime() %></h4>
			                    <p class="lead text-info">	
			                    	<a href="<%=currentPath %>/read.jsp?i=<%=blog.getId()%>" target="_blank">
										<%= blog.getPreview() %>
									</a>
			                    </p>
			                    <%-- 数据统计预览 --%>
			                    <h5 style="font-size:16px;">
			                        <span class="glyphicon glyphicon-eye-open label label-default" aria-hidden="true">&nbsp;<%=blog.getReadCount()%></span>
			                        <span class="glyphicon glyphicon-thumbs-up label label-default" aria-hidden="true">&nbsp;<%=blog.getLikeCount()%></span>
			                        <span class="glyphicon glyphicon-comment label label-default" aria-hidden="true">&nbsp;<%=blog.getCommentCount()%></span>
			                    </h5>
			                </blockquote>
			                <%
							} // for 结束
						
							%>
							<!-- 页数导航 -->
				            <%
				            paginationBean.setTargetPage(currentPath+"/people.jsp?i="+peopleId);
				            paginationBean.setParamName("p");
				            %>
				            <nav>
				                <form action="<%= paginationBean.getTargetPage() %>" method="get" class="input-group hidden-xs" >
				                    <div class="input-group-btn">
				                        <a href="<%= paginationBean.getTargetPage() %>" class="btn btn-default">首页</a>
				                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()-1) %>" class="btn btn-default  <%= paginationBean.isPrevious()?"":"hidden" %>" type="button">上一页</a>
				                    </div>
				                    <span class="input-group-addon">第<%=paginationBean.getCurrent() %>/<%= paginationBean.getTotal() %>页</span>
				                    <input name="p" type="number" min="1" value="<%= paginationBean.getCurrent() %>" max="<%= paginationBean.getTotal() %>" class="form-control" aria-label="页码">
				                    <div class="input-group-btn">
				                        <button class="btn btn-default" type="submit">跳转</button>
				                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()+1) %>"  class="btn btn-default  <%= paginationBean.isNext()?"":"hidden" %>" type="button" >下一页</a>
				                        <a href="<%= paginationBean.getUrlPre() + paginationBean.getTotal() %>" class="btn btn-default" type="button">末页</a>
				                    </div>
				                </form>
				            
				                <form action="<%= paginationBean.getTargetPage() %>" method="get" class="hidden-sm hidden-md hidden-lg text-center">
				                    <div class="input-group">
				                        <span class="input-group-addon">第<%=paginationBean.getCurrent() %>/<%= paginationBean.getTotal() %>页</span>
				                        <input name="p" type="number" min="1" value="<%= paginationBean.getCurrent() %>" max="<%= paginationBean.getTotal() %>" class="form-control" aria-label="页码">
				                        <div class="input-group-btn">
				                            <button class="btn btn-default" type="submit">跳转</button>
				                        </div>
				                    </div>
				            
				                    <br/>
				            
				                    <div class="btn-group" role="group" aria-label="Default button group">
				                        <a href="<%= paginationBean.getTargetPage() %>" class="btn btn-default">首页</a>
				                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()-1) %>"  class="btn btn-default  <%= paginationBean.isPrevious()?"":"hidden" %>" type="button">上一页</a>
				                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()+1) %>"   class="btn btn-default <%= paginationBean.isNext()?"":"hidden" %>" type="button" >下一页</a>
				                        <a href="<%= paginationBean.getUrlPre() + paginationBean.getTotal() %>" class="btn btn-default" type="button">末页</a>
				                    </div>
				                </form>
				            </nav>
							<%
						
							}else{ // blogs.length>0
								// 获取到的博客数量是 0
								%>
								<p class="lead" style="color: orange;text-align: center;margin: 100px auto;font-size: 26px;">
				                    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
				                    <br>
				                    没有博文
				                    <br>
				                    <br>
				                    <% if(pageNumber != 1){ %>
				                    <a href="<%= currentPath %>/people.jsp" class="btn btn-primary">回到我的主页</a>
				                	<% } %>
				                </p>
								<%
							}
					}catch(Exception e){ // 捕获SQL错误
						e.printStackTrace();
					%>
					<p class="lead" style="color: red;text-align: center;margin: 100px auto;font-size: 26px;">
	                    <span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>
	                    <br>
	                    数据库错误
	                </p>
					<%
					}
	                %>
	            </div>
	
            <%}else{  // if(userInfo.isLoaded())%><!-- 没有获取到用户信息 -->
            	<p class="lead" style="text-align: center;padding: 20%;">
            		无资料<br><br>
            		<a href="<%= currentPath %>/index.jsp" class="btn btn-primary">回到主页</a>
            	</p>
				
            <%} // if(userInfo.isLoaded())%>
		</div>
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
	</body>
</html>
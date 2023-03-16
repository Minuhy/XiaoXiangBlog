<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.blog.BlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<!-- 已知漏洞：XSS跨站脚本攻击等 -->
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 首页</title>
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_INDEX_NUMBER); %>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		<% 
			Object obj;
			// 尝试获取昵称
			String nick = null;
			UserBean user = null;
			obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
			if(obj instanceof UserBean){
				user = (UserBean)obj;
				nick = user.getNick();
			}
		%>
        <div class="container">
            <!-- 标牌 -->
            <div class="jumbotron">
                <h1>潇湘网络日记</h1>
                <p>博客，仅音译，英文名为Blogger，为Web Log的混成词。它的正式名称为网络日记；又音译为部落格或部落阁等，是使用特定的软件，在网络上出版、发表和张贴个人文章的人，或者是一种通常由个人管理、不定期张贴新的文章的网站。博客上的文章通常以网页形式出现，并根据张贴时间，以倒序排列。</p>
                <%if(nick!=null){ // 已经登录 %>
                	<p class="text-right lead"><em>欢迎你：<%= nick %></em></p>
                <%}else{ // 没有登录 %>
                	<p><a class="btn btn-primary btn-lg" href="<%= currentPath %>/login.jsp" role="button">登录 / 注册</a></p>
                <%} %>
            </div>

			<h2>博文随机推荐</h2>
            <hr>
            <div>
	       	<jsp:useBean id="randomBlogsBean" class="minuhy.xiaoxiang.blog.bean.blog.RandomBlogsBean"></jsp:useBean>
				<%
				// 加载随机博文信息
				try{
					randomBlogsBean.getData(10);
					if(randomBlogsBean.getBlogBeans().length != 0){
						for(BlogBean blog:randomBlogsBean.getBlogBeans()){
					
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
						}
					}else{
						// 获取到的博客数量是 0
						%>
						<p class="lead" style="color: orange;text-align: center;margin: 100px auto;font-size: 26px;">
		                    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
		                    <br>
		                    没有博文可以推荐
		                    <br>
		                    <br>
		                    <!-- 没有博文 -->
				            <a href="<%= currentPath %>/post.jsp" class="btn btn-primary">去发表博文</a>
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
            <br>
            <hr>
            <p class="lead" style="text-align: center;">结束</p>
        </div>
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
	</body>
</html>
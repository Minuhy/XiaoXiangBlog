<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 修改密码</title>
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_MY_NUMBER); %>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		
		<%
		Object obj;
		// 尝试获取用户
		UserBean user = null;
		obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
		if(obj instanceof UserBean){
			user = (UserBean)obj;
		}
		%>
		
		 <!-- 内容 -->
        <div class="container">
            <% if(user!=null){ %>
	            <h1>修改密码</h1>
	            <hr>
	            <form action="<%= currentPath %>/user/passwd" method="post" style="margin-bottom: 10%;">
	                <div class="input-group input-group">
	                    <span class="input-group-addon" id="profile1">账号</span>
	                    <input value="<%= user.getAccount() %>"  type="text" style="background-color:ivory;"  readonly="readonly" class="form-control" aria-describedby="profile1">
	                </div>
	                <br>
	                <div class="input-group input-group">
	                    <span class="input-group-addon" id="profile2">原密码</span>
	                    <input name="mima" type="password" placeholder="请输入你现在的密码" class="form-control" aria-describedby="profile2">
	                </div>
	                <br>
	                
	                <div class="input-group input-group">
	                    <span class="input-group-addon" id="profile2">新密码</span>
	                    <input name="xinmima" type="password" placeholder="请输入新的密码" class="form-control" aria-describedby="profile2">
	                </div>
	                <br>
	                <div class="input-group input-group">
	                    <span class="input-group-addon" id="profile2">确认新密码</span>
	                    <input name="cxxinmima" type="password" placeholder="请再输入一遍新的密码" class="form-control" aria-describedby="profile2">
	                </div>
	                <br>
	                <div class="text-right">
	                    <button type="submit" class="btn btn-primary">修改密码</button>
	                </div>
	
	            </form>
            
            <% }else{ %>
            	<p class="lead" style="text-align: center;padding: 20%;">无修改项<br>请检查登录状态</p>
            <%} %>
        </div>
        
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
	</body>
</html>
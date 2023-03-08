<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<%
// 判断，如果已经登录了，那么就不需要到注册页面了
Object obj;
UserBean user = null;
obj = request.getSession().getAttribute(SessionAttributeNameConfig.USER_INFO);
if(obj instanceof UserBean){
	user = (UserBean)obj;
	request.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE_TITLE, "首页");
	request.setAttribute(RequestAttributeNameConfig.FORWARD_MSG_TYPE, MsgTypeEnum.WARNING);
	request.setAttribute(RequestAttributeNameConfig.FORWARD_MSG, "已登录账号 "+user.getAccount());
	request.setAttribute(RequestAttributeNameConfig.FORWARD_NEXT_PAGE, currentPath + "/index.jsp");
	// 这里的"/tips.jsp"与include使用方式一样，不需要currentPath
	request.getRequestDispatcher("/tips.jsp").forward(request, response);
}else{

%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 注册</title>
		<link rel="stylesheet" href="<%= currentPath %>/lib/bootcss/signin.css">
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		<%
		// 登录页（带跳转参数的）
		String loginUrl = TextUtil.isString(session.getAttribute(SessionAttributeNameConfig.LOGIN_PAGE), currentPath +"/login.jsp");
		%>
		
        <div class="container">
	        <form class="form-reg" action="<%= currentPath %>/user/register" method="post">
	            <h2 class="form-signin-heading">注册</h2>
	            <label for="inputNick" class="sr-only">昵称</label>
	            <input name="nick" type="text" id="inputNick" class="form-control in-form" placeholder="请输入昵称" required oninvalid="setCustomValidity('请输入昵称')" oninput="setCustomValidity('')" autofocus>
	            <label for="inputPhoneNumber" class="sr-only">手机号</label>
	            <input name="account" type="text" id="inputPhoneNumber" class="form-control in-form" placeholder="请输入手机号" required oninvalid="setCustomValidity('请输入手机号');" oninput="setCustomValidity('');">
	            <label for="inputPassword" class="sr-only">密码</label>
	            <input name="passwd" type="password" id="inputPassword" class="form-control in-form" placeholder="请设置密码" required oninvalid="setCustomValidity('请设置密码');" oninput="setCustomValidity('');">
	            <label for="inputPasswordRe" class="sr-only">确认密码</label>
	            <input name="repwd" type="password" id="inputPasswordRe" class="form-control in-form" placeholder="请确认设置的密码" required oninvalid="setCustomValidity('请确认你的密码');" oninput="setCustomValidity('');">
	            <div class="input-group">
	                <input name="captcha" type="text" id="inputCaptcha" class="form-control in-form" placeholder="请输入右边验证码" required oninvalid="setCustomValidity('请输入验证码');" oninput="setCustomValidity('');">
	                <span class="input-group-addon">
	                	<img id="captchaImg" alt="验证码" src="<%= currentPath %>/util/captcha" style="cursor: pointer;">
	                </span>
	            </div>
	            <br>
	            <button class="btn btn-lg btn-primary btn-block" type="submit">注册</button>
	            <div class="sig-tip">已有账号？点我去<a href="<%= loginUrl %>">登录</a>~</div>
	        </form>
	    </div> <!-- /container -->
	    
	    
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
    	<script src="<%= currentPath %>/lib/xiaoxiang/js/refreshimg.js"></script>
	</body>
</html>

<%}%>

<%@page import="minuhy.xiaoxiang.blog.util.SessionUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.config.CookieConfig"%>
<%@page import="minuhy.xiaoxiang.blog.util.CookieUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<%
// 判断，如果已经登录了，那么就不需要到登录页面了
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
		<title>潇湘博客 - 登录</title>
		
        <link rel="stylesheet" href="<%= currentPath %>/lib/bootcss/signin.css">
        
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_MY_NUMBER); %>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		
		<%
		/*-----------------------------------自动跳转处理流程------------------------------------------------*/
		// 处理一下登录之前的页面，登录后跳转到那个页面
		String prePage = RequestUtil.getReqParam(request, "u", 
				"".equals(request.getHeader("referer"))?
						currentPath + "/index.jsp" : request.getHeader("referer") 
			);
		String prePageName = RequestUtil.getReqParam(request, "n", 
				"".equals(request.getHeader("referer"))?
						"首页" : "登录之前的页面" 
			);
		
		// 获取到一个完整的URL
		String url=request.getScheme()+"://";
		url+=request.getHeader("host");
		url+=request.getRequestURI();
		if(request.getQueryString()!=null)
		url+="?"+request.getQueryString();
		session.setAttribute(SessionAttributeNameConfig.LOGIN_PAGE,url);
		
		session.setAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE, prePage);
		session.setAttribute(SessionAttributeNameConfig.LOGIN_PRE_PAGE_NAME, prePageName);
		%>
		
		<%
		String cookieAccount = "";
		String cookiePasswd = "";
		boolean isRememberMe = false;
		
		Cookie cookie = null;
		Cookie[] cookies = null;
		// 获取 cookies 的数据,是一个数组
		cookies = request.getCookies();
		if( cookies != null ){
			for (int i = 0; i < cookies.length; i++){
				cookie = cookies[i];
				if(cookie.getName().equals(CookieConfig.REMEMBER_ME_KEY_NAME)){
					String cookieStr = cookie.getValue();
					String[] strings = CookieUtil.ParseAccountAndPasswd(cookieStr);
					if(strings!=null){
						cookieAccount = strings[0];
						cookiePasswd = strings[1];
						isRememberMe = true;
					}
				}
			}
		}
		
		cookieAccount = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.LOGIN_ACC, cookieAccount);
		cookiePasswd = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.LOGIN_PWD, cookiePasswd);
		String remeStr = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.LOGIN_REME, String.valueOf(isRememberMe));
		if(remeStr.equals("on") || remeStr.equals(String.valueOf(true))){
			isRememberMe = true;
		}
		%>
		
		
        <div class="container">
	        <form class="form-signin" action="<%= currentPath %>/user/login" method="post">
	            <h2 class="form-signin-heading">登录</h2>
	            <label for="inputPhoneNumber" class="sr-only">手机号</label>
	            <input value="<%=cookieAccount%>" name="account" type="text" id="inputPhoneNumber" class="form-control" placeholder="请输入手机号" required oninvalid="setCustomValidity('请输入手机号');" oninput="setCustomValidity('');" autofocus>
	            <label for="inputPassword" class="sr-only">密码</label>
	            <input value="<%=cookiePasswd%>" name="passwd" type="password" id="inputPassword" class="form-control" placeholder="请输入密码" required oninvalid="setCustomValidity('请输入密码');" oninput="setCustomValidity('');">
	            <div class="input-group">
	                <input name="captcha" type="text" id="inputCaptcha" class="form-control in-form" placeholder="请输入右边验证码" required oninvalid="setCustomValidity('请输入验证码');" oninput="setCustomValidity('');">
	                <span class="input-group-addon">
	                	<img id="captchaImg" alt="验证码" src="<%= currentPath %>/util/captcha" style="cursor: pointer;">
	                </span>
	            </div>
	            <div class="checkbox">
	                <label>
	                    <input <% if(isRememberMe){out.print("checked");} %> name="rememberMe" type="checkbox"> <span class="remember-me">记住我（一周）</span>
	                </label>
	            </div>
	            <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
	            <div class="sig-tip">没有账号？点我去<a href="<%= currentPath %>/register.jsp">注册</a>~</div>
	        </form>
	    </div> <!-- /container -->
	    
	    
	    
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
    	<script src="<%= currentPath %>/lib/xiaoxiang/js/refreshimg.js"></script>
	</body>
</html>
<%}%>

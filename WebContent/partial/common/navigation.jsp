<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%String currentPath = request.getContextPath();%>

<%-- 导航栏，放到body中 --%>
<%-- 
通过在Request中设置 RequestAttributeNameConfig.NAVIGATION_ACTION 来高亮导航栏：
0或不设置：全部不高亮
1：首页高亮
2：博客高亮
3：发博客高亮
4：我的高亮
 --%>

<%
Object obj = null;

// 尝试获取导航栏配置
Integer activeId = 0;
obj = request.getAttribute(RequestAttributeNameConfig.NAVIGATION_ACTION);
if(obj instanceof Integer){
	activeId = (Integer)obj;
}

// 尝试获取昵称
String nick = null;
UserBean user = null;
obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
if(obj instanceof UserBean){
	user = (UserBean)obj;
	nick = user.getNick();
}
%>

<nav class="navbar navbar-default">
	<div class="container-fluid">
        <!-- 品牌和开关组合在一起，以实现更好的在移动端显示 -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
            <span class="sr-only">潇湘网络日记</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<%= currentPath %>/index.jsp">
				<img alt="潇湘博客"  style="margin-top: -3px;width: 24px;height: 24px;" src="<%= currentPath %>/img/favicon.ico">
			</a>
            <a class="navbar-brand" href="<%= currentPath %>/index.jsp">潇湘博客</a>
        </div>
  
        <!-- 导航链接、表单和其他内容以进行切换 -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
            <li class="<%=activeId==RequestAttributeNameConfig.NAVIGATION_INDEX_NUMBER?"active":""%>"><a href="<%= currentPath %>/index.jsp">首页</a></li>
            <li class="<%=activeId==RequestAttributeNameConfig.NAVIGATION_BLOG_NUMBER?"active":""%>"><a href="<%= currentPath %>/blog.jsp">博客</a></li>
            </ul>
            <form class="navbar-form navbar-left" action="<%= currentPath %>/search.jsp" method="get">
                <div class="form-group">
                    <input name="s" type="text" class="form-control" placeholder="请输入关键词">
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <li class="<%=activeId==RequestAttributeNameConfig.NAVIGATION_POST_NUMBER?"active":""%>"><a href="<%= currentPath %>/post.jsp">写博文</a></li>
				<%if(user!=null){%>
				<%-- 已登录 --%>
					
					<% if(user.getRole() == 1){ %> <!-- 管理员 -->
	                <li><a href="<%= currentPath %>/admin/index.jsp">网站管理</a></li>
	                <%} %>
					
					<jsp:useBean id="messageNewCountBean" class="minuhy.xiaoxiang.blog.bean.user.MessageNewCountBean" scope="session"></jsp:useBean>
					<%
					messageNewCountBean.setUserId(user.getId());
					%>
				
					<li class="dropdown <%=activeId==RequestAttributeNameConfig.NAVIGATION_MY_NUMBER?"active":""%>">
						<a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
							<%= TextUtil.maxLen(nick, 12) %>
							<%= messageNewCountBean.getNewMsgCount()!=0?"<span class=\"badge\">"+messageNewCountBean.getNewMsgCount()+"</span>":"" %>
							
							<span class="caret"></span>
							&nbsp;
							<img style="margin-top: -10px;float:right;" width="40" height="40" src="<%= String.format(currentPath + "/img/avatar/h%03d.png", user.getAvatar()) %>"  alt="<%= TextUtil.maxLen(nick, 12) %>的头像" class="img-circle">
							
						</a>
						<ul class="dropdown-menu">
					        <li><a href="<%= currentPath %>/message.jsp">
					        <%= messageNewCountBean.getNewMsgCount()!=0?"新消息（"+messageNewCountBean.getNewMsgCount()+"）":"我的消息" %>
					        </a></li>
					        <li><a href="<%= currentPath %>/people.jsp">我的主页</a></li>
					        <li><a href="<%= currentPath %>/profile.jsp">编辑资料</a></li>
					        <li><a href="<%= currentPath %>/passwd.jsp">修改密码</a></li>
					        <li role="separator" class="divider"></li>
					        <li><a href="<%= currentPath %>/user/logout">退出登录</a></li>
					     </ul>
					</li>
				 <%}else{ %>
                 <%-- 未登录 --%>
                 	<li class="<%=activeId==RequestAttributeNameConfig.NAVIGATION_MY_NUMBER?"active":""%>"><a href="<%= currentPath %>/login.jsp">登录</a></li>   	
                 <%} %>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>
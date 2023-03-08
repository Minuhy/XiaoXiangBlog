<%@page import="minuhy.xiaoxiang.blog.bean.admin.AdminNoticeBean.SendException"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.SendMessageBean"%>
<%@page import="java.util.Enumeration"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.bean.PaginationBean"%>
<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@ page import="minuhy.xiaoxiang.blog.util.RequestUtil" %>
<%@ page import="minuhy.xiaoxiang.blog.entity.UserEntity" %>
<%@ page import="minuhy.xiaoxiang.blog.config.StatisticsConfig" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<% 
	Object obj;
	// 尝试获取昵称
	UserBean user = null;
	obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
	if(obj instanceof UserBean){
		user = (UserBean)obj;
	}
	
	if(user == null){
		// 如果没有登录，跳转到登录
		response.sendRedirect(currentPath+"/login.jsp");
		return;
	}else if(user.getRole() != 1){
		// 如果不是管理员，跳转到主页
		response.sendRedirect(currentPath+"/index.jsp");
		return;
	}
%>
<jsp:useBean id="noticeBean" scope="session" class="minuhy.xiaoxiang.blog.bean.admin.AdminNoticeBean"></jsp:useBean>
<%
String sendTitle = request.getParameter("title");
String sendMsg = request.getParameter("msg");
String sendObj = request.getParameter("obj");
String sendLink = request.getParameter("link");
boolean isSend = false;
if(sendTitle!=null && sendMsg!=null && sendObj!=null){
	isSend = true;
	noticeBean.setTitle(sendTitle);
	noticeBean.setMessage(sendMsg);
	noticeBean.setObject(sendObj);
	noticeBean.setLink(sendLink);
}else{
	// noticeBean.clean(); // 这里不用清理，等发送成功后清理
}
%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
<head>
<meta charset="UTF-8">
<jsp:include page="common/head.jsp"></jsp:include>
<title>潇湘博客 - 发布消息</title>
</head>
<body>
	<jsp:include page="common/navigation.jsp"></jsp:include>
	<div style="text-align: center;padding: 10px;width: 1500px;margin: 0 auto;">
		<%if(!isSend){ %>
		<div style="font-size: 28px;">
			<form onsubmit="return sumbit_sure()"   action="" method="post">
				<div>
					消息标题
					<br>
					<input value="<%= noticeBean.getTitle()==null?"":noticeBean.getTitle() %>" name="title" required="required" style="padding: 14px;font-size: 22px;margin-top: 12px;width: 500px;" >
				</div>
				<div>
					消息内容
					<br>
					<textarea name="msg" autofocus="autofocus"  required="required" style="padding: 20px;font-size: 22px;margin-top: 12px;" rows="12" cols="80"><%= noticeBean.getMessage()==null?"":noticeBean.getMessage() %></textarea>
				</div>
				<div>
					消息链接（默认到网站首页）
					<br>
					<input value="<%= noticeBean.getLink()==null?"":noticeBean.getLink() %>" name="link" style="padding: 5px;font-size: 16px;width: 700px;" >
				</div>
				<div>
					发送对象
					<br>
					<select name="obj" style="font-size: 18px;">
						<option value="all"  <%= noticeBean.getObject()==null?"selected":(noticeBean.getObject().equals("all")?"selected":"") %>>所有人</option>
						<optgroup label="注册时间">
							<option value="in7d" <%= "in7d".equals(noticeBean.getObject())?"selected":""%>>一周内</option>
							<option value="inSeason" <%= "inSeason".equals(noticeBean.getObject())?"selected":""%>>一季内</option>
							<option value="outSeason" <%= "outSeason".equals(noticeBean.getObject())?"selected":""%>>一季以上</option>
						</optgroup>
						<optgroup label="性别">
							<option value="male" <%= "male".equals(noticeBean.getObject())?"selected":""%>>所有男性</option>
							<option value="female" <%= "female".equals(noticeBean.getObject())?"selected":""%>>所有女性</option>
							<option value="unset" <%= "unset".equals(noticeBean.getObject())?"selected":""%>>所有未设置</option>
						</optgroup>
						<optgroup label="点赞数">
							<option value="like1000" <%= "like1000".equals(noticeBean.getObject())?"selected":""%>>大于1000</option>
							<option value="like100" <%= "like100".equals(noticeBean.getObject())?"selected":""%>>大于100</option>
						</optgroup>
						<optgroup label="博文数">
							<option value="blog100" <%= "blog100".equals(noticeBean.getObject())?"selected":""%>>大于100</option>
							<option value="blog10" <%= "blog10".equals(noticeBean.getObject())?"selected":""%>>大于10</option>
						</optgroup>
					</select>
				</div>
				<br>
				<button style="width: 20%;" type="submit">发送</button>
			</form>
		</div>
        <%
		}else{ 
        	try{
        		String msg = noticeBean.send(user.getId());
        %>
		        <div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
				    <h1>发送成功 <%= msg %></h1>
				    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/notice.jsp">点击此处继续</a></p>
				    <%
				    response.setHeader("refresh","3;"+currentPath + "/admin/notice.jsp");
				    %>
				</div>
        <%
        		if(msg!=null){ // 发送成功，清理消息
        			noticeBean.clean();
        		}
			}catch(SendException e){
				%>
		        <div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
				    <h1>操作失败 <%= e.getMessage() %></h1>
				    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/notice.jsp">点击此处继续</a></p>
				    <%
				    response.setHeader("refresh","3;"+currentPath + "/admin/notice.jsp");
				    %>
				</div>
        		<%
        	}
        } 
        %>
        <hr>
        <div id="statistics">
            <p>
                【网站统计】&nbsp;
                在线用户数：
                <span>
					<%= application.getAttribute(StatisticsConfig.SESSION_COUNT)!=null?application.getAttribute(StatisticsConfig.SESSION_COUNT):0 %>
				</span>
                &nbsp;&nbsp;&nbsp;
                每秒请求数：
                <span>
					<%= application.getAttribute(StatisticsConfig.REQUEST_COUNT)!=null?String.format("%.3f",application.getAttribute(StatisticsConfig.REQUEST_COUNT)):0 %>
				</span>
            </p>
        </div>
    </div>
    <script>
	function sumbit_sure(){
	    var gnl=confirm("确定要发送？此操作不可撤回！");
	    if (gnl==true){
	        return true;
	    }else{
	        return false;
	    }
	}
	</script>
	
<script type="text/javascript">
console.log("<%
	    Enumeration<String> names = request.getParameterNames();
	    int index = 0;
	    while (names.hasMoreElements()) {
	        index++;
	        String name = names.nextElement();
	        Object value = request.getParameter(name);
	        out.print("["+index+":"+name+"->"+value+"]");
	        out.print("\\n");
	    }
%>");
</script>
</body>
</html>
<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<jsp:useBean id="countBean" class="minuhy.xiaoxiang.blog.bean.admin.CountBean"></jsp:useBean>
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
	
	if(user == null){
		// 如果没有登录，跳转到登录
		response.sendRedirect(currentPath+"/login.jsp");
		return;
	}else if(user.getRole() != 1){
		// 如果不是管理员，跳转到主页
		response.sendRedirect(currentPath+"/index.jsp");
		return;
	}
	countBean.getData();
%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
<head>
<meta charset="UTF-8">
<jsp:include page="common/head.jsp"></jsp:include>
<title>潇湘博客 - 管理</title>
</head>
<body>
	<jsp:include page="common/navigation.jsp"></jsp:include>
	<div style="font-size: 40px;text-align: center;padding: 10px;margin-top: 5%;">
        <table id="indexTable">
            <tbody>
                <tr>
                    <td>本月新增用户数：</td>
                    <td>
						<jsp:getProperty property="newUserCount" name="countBean"/>
					</td>
                </tr>
                <tr>
                    <td>本月新增博文数：</td>
                    <td>
						<jsp:getProperty property="newBlogCount" name="countBean"/>
					</td>
                </tr>
                <tr>
                    <td>本月新增评论数：</td>
                    <td>
						<jsp:getProperty property="newCommentCount" name="countBean"/>
					</td>
                </tr>
                <tr>
                    <td>本月新增点赞数：</td>
                    <td>
						<jsp:getProperty property="newLikeCount" name="countBean"/>
					</td>
                </tr>
                <tr>
                    <td colspan="2">
                    	<%= TimeUtil.timestamp2DateTime(
                    			TimeUtil.getTimestampMs()
                    			) %>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
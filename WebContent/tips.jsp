<%@page import="minuhy.xiaoxiang.blog.enumeration.MsgTypeEnum"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 提示</title>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		
		<%
		String autoGo = "false";
		// 获取数据
		MsgTypeEnum msgTypeEnum = MsgTypeEnum.ERROR;
		Object obj = request.getAttribute(RequestAttributeNameConfig.FORWARD_MSG_TYPE);
		if(obj instanceof MsgTypeEnum){
			msgTypeEnum = (MsgTypeEnum)obj;
		}
		
		String type = String.valueOf(msgTypeEnum).toLowerCase();
		String msg = RequestUtil.getReqAttribute(request,RequestAttributeNameConfig.FORWARD_MSG,"提示");
		String nextPage = RequestUtil.getReqAttribute(request,RequestAttributeNameConfig.FORWARD_NEXT_PAGE,currentPath+"/index.jsp");
		String nextPageTitle = RequestUtil.getReqAttribute(request,RequestAttributeNameConfig.FORWARD_NEXT_PAGE_TITLE,"新页面");
		autoGo = String.valueOf(
				RequestUtil.getReqAttribute(request,RequestAttributeNameConfig.FORWARD_AUTO_GO,true)
				);
		
		
		/*
		t: 显示图标的类型，success 绿色勾勾，warning 黄色感叹号，error 或者其他 红色叉叉
		m: 消息文本
		n: 下一个页面的链接
		h: 下一个页面的标题
		a: 自动跳转，false 不自动跳转，true 或其他 自动跳转 
		*/
		
		type = RequestUtil.getReqParam(request, "t", type); // 消息类型
		msg = RequestUtil.getReqParam(request, "m", msg); // 消息文本
		nextPage = RequestUtil.getReqParam(request, "n", nextPage); // 下一个页面的链接
		nextPageTitle = RequestUtil.getReqParam(request, "h", nextPageTitle); // 下一个页面的标题
		autoGo = RequestUtil.getReqParam(request, "a", autoGo); // 自动跳转
		
		// 设置跳转
		if(!autoGo.equals("false")){
			response.setHeader("refresh", "3;Url=" + nextPage);
		}
		
		// 简单信息安全审查
		
		// 可能被XSS跨站脚本攻击
		nextPage.replace("\"", "")
			.replace("'", "")
			.replace(";", "")
			.replace("\n", "")
			.replace("\r", "");
		
		%>
		
        <!-- 内容 -->
        <div class="container text-center">
            <div  style="padding: 10%;">
	            <% if(type.equals("success")){ %>
	            	<span class="glyphicon glyphicon-ok-sign text-success" style="font-size: 72px;"  aria-hidden="true"></span>
	            <% }else if(type.equals("warning")){ %>
	            	<span class="glyphicon glyphicon-info-sign text-warning" style="font-size: 72px;"  aria-hidden="true"></span>
	            <% }else{ %>
	            	<span class="glyphicon glyphicon-remove-sign text-danger" style="font-size: 72px;"  aria-hidden="true"></span>
	            <% } %>
                <h1><%= msg %></h1>
                <hr>
            </div>
            <% if(!autoGo.equals("false")){ // 自动跳转 %>
            	<a href="<%= nextPage %>">三秒后跳转到 <%= nextPageTitle %><br>如果没有跳转请点击此条消息跳转</a>
            <%}else{ // 不自动跳转 %>
            	<button id="buttonSkip" type="button" class="btn btn-primary"><%= nextPageTitle %></button>
            <%} %>
            <br>
            
        </div>
        
        
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
		<% if(autoGo.equals("false")){ // 不自动跳转 %>
	        <script> 
	        $("#buttonSkip").click(function(){
	            location.href='<%= nextPage %>';
	        });
	        </script>
        <%} %>
	</body>
</html>
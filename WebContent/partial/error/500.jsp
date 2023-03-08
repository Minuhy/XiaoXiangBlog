<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true"%>
<%String currentPath = request.getContextPath();%>
<!DOCTYPE html>
<html>
	<head>
		<jsp:include page="/partial/common/head.jsp"></jsp:include>
		<title>500 - 潇湘博客</title>
	</head>
	<body>
		<div class="container">
			<div class="jumbotron" style="margin-top: 10%;">
				<h1>500</h1>
				<p>发生了一点错误<%=exception==null?"":"："+exception.getMessage()%></p>
				<p>
				  <a class="btn btn-lg btn-primary" href="<%= currentPath %>/index.jsp" role="button">回到首页</a>
				</p>
			</div>
		</div>
		<jsp:include page="/partial/common/foot.jsp"></jsp:include>
	</body>
</html>
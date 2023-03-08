<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
    
<%-- 所有页面的公共头部部分，静态引入，放在title标签之上 --%>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->

<!-- Bootstrap -->
<link rel="stylesheet" href="<%= currentPath %>/lib/bootstrap-3.4.1/css/bootstrap.css">

<!-- HTML5 shim 和 Respond.js 是为了让 IE8 支持 HTML5 元素和媒体查询（media queries）功能 -->
<!-- 警告：通过 file:// 协议（就是直接将 html 页面拖拽到浏览器中）访问页面时 Respond.js 不起作用 -->
<!--[if lt IE 9]>
  <script src="<%= currentPath %>/lib/html5shiv-3.7.3/html5shiv.min.js"></script>
  <script src="<%= currentPath %>/lib/respond.js-1.4.2/respond.min.js"></script>
<![endif]-->

<link rel="stylesheet" href="<%= currentPath %>/lib/xiaoxiang/css/backtop.css">

<link rel="shortcut icon" href="<%= currentPath %>/img/icon.svg">
<style>
body { 
	overflow-y: scroll; 
}
</style>

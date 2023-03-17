<%@page import="minuhy.xiaoxiang.blog.config.StatisticsConfig"%>
<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<%-- 所有页面的页脚和返回顶部悬浮按钮，放在body标签底部 --%>

<div onclick="window.scrollTo(0,0);return false;" style="display:none; background: url(<%= currentPath %>/img/backToTop.png) no-repeat scroll 0 0 transparent;" class="back-to" id="toolBackTop">
	<a title="返回顶部" onclick="window.scrollTo(0,0);return false;" href="#top" class="back-top">回到顶部</a>
</div>
    
<footer>
	<hr>
	<p class="lead text-center">
		本站由
		<a href="https://space.bilibili.com/32778000">敏Ymm</a>
		使用
		<a href="https://www.runoob.com/jsp/jsp-tutorial.html">JSP</a>
		<span class="glyphicon glyphicon-plus" style="font-size: 16px;" aria-hidden="true"></span>
	    <a href="https://v3.bootcss.com/">Bootstrap</a>
		技术实现
	</p>
	<div class="text-center" style="padding-bottom: 70px;">
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
		<a href="<%= currentPath %>/tangerine.jsp">
			&copy; Minuhy v1.0.3
		</a>
  	</div>
</footer>
<!-- jQuery (Bootstrap 的所有 JavaScript 插件都依赖 jQuery，所以必须放在前边) -->
<script src="<%= currentPath %>/lib/jquery-1.12.4/jquery.js"></script>
<!-- 加载 Bootstrap 的所有 JavaScript 插件。你也可以根据需要只加载单个插件。 -->
<script src="<%= currentPath %>/lib/bootstrap-3.4.1/js/bootstrap.js"></script>
<script src="<%= currentPath %>/lib/xiaoxiang/js/backtop.js"></script>
<script src="<%= currentPath %>/lib/xiaoxiang/js/skippage.js"></script>
<script src="<%= currentPath %>/lib/xiaoxiang/js/toast.js"></script>

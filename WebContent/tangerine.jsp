<%@page import="minuhy.xiaoxiang.blog.config.StatisticsConfig"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>关于</title>
		<% // 设置导航栏
		/*
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_MY_NUMBER); 
		*/
		%>
		<style type="text/css">
			th{
				text-align: center;
			}
		</style>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		<div class="container text-center">
			<div style="width:200px; margin: 2% auto 2% auto;">
				<img width="200" alt="橘子" src="<%=currentPath%>/img/flower1.svg">
				<hr>
				&copy; Minuhy 2023-02 
				<hr>
				<table id="monitorTable" border="1" style="border:2px solid #f91;width: 200px;border-radius: 10px;">
					<thead>
						<tr>
							<td colspan="2">
								【网站实时统计】
							</td>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>在线用户数：</td>
							<td>
								<span id="sessionSpan">
									<%= application.getAttribute(StatisticsConfig.SESSION_COUNT)!=null?application.getAttribute(StatisticsConfig.SESSION_COUNT):0 %>
								</span>
							</td>
						</tr>
						<tr>
							<td>每秒请求数：</td>
							<td>
								<span id="qpsSpan">
									<%= application.getAttribute(StatisticsConfig.REQUEST_COUNT)!=null?String.format("%.3f",application.getAttribute(StatisticsConfig.REQUEST_COUNT)):0 %>
								</span>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<span id="msgSpan"></span>
							</td>
						</tr>
					</tbody>
				</table>
				
			</div>
			
			<table border="1" style="width: 100%;text-align: center;margin: 0;">
				<caption>代码统计 (v1.0.1)</caption>
				<thead>
					<tr>
						<th>语言</th>
						<th>文件数</th>
						<th>空行</th>
						<th>注释</th>
						<th>代码</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Java</td>
						<td>82</td>
						<td>1825</td>
						<td>1767</td>
						<td>8427</td>
					</tr>
					<tr>
						<td>JSP</td>
						<td>28</td>
						<td>374</td>
						<td>305</td>
						<td>4053</td>
					</tr>
					<tr>
						<td>HTML</td>
						<td>16</td>
						<td>363</td>
						<td>273</td>
						<td>3174</td>
					</tr>
					<tr>
						<td>JavaScript</td>
						<td>6</td>
						<td>124</td>
						<td>116</td>
						<td>495</td>
					</tr>
					<tr>
						<td>XML</td>
						<td>2</td>
						<td>15</td>
						<td>86</td>
						<td>166</td>
					</tr>
					<tr>
						<td>SQL</td>
						<td>1</td>
						<td>9</td>
						<td>26</td>
						<td>78</td>
					</tr>
					<tr>
						<td>CSS</td>
						<td>1</td>
						<td>0</td>
						<td>0</td>
						<td>20</td>
					</tr>
					<tr>
						<td>Properties</td>
						<td>2</td>
						<td>0</td>
						<td>0</td>
						<td>7</td>
					</tr>
					<tr>
						<td>总计</td>
						<td>138</td>
						<td>2710</td>
						<td>2573</td>
						<td>16420</td>
					</tr>
					<tr>
						<td colspan="5"><a href="https://github.com/minuhy/XiaoXiangBlog">项目地址：https://github.com/Minuhy/XiaoXiangBlog</a></td>
					</tr>
				</tbody>
			</table>
		</div>
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
		<script type="text/javascript">
		
		var websocket = null;

	    //判断当前浏览器是否支持WebSocket
	    if ('WebSocket' in window) {
	        websocket = new WebSocket("ws://<%=request.getHeader("host")+currentPath%>/ep/monitor");
	    
		    //连接发生错误的回调方法
		    websocket.onerror = function () {
		        document.getElementById("msgSpan").innerHTML = "WebSocket 发生错误";
		    };
	
		    //连接成功建立的回调方法
		    websocket.onopen = function () {
		        console.log("WebSocket链接建立");
			    websocket.send("start monitor");
		        document.getElementById("msgSpan").innerHTML = "WebSocket 已连接";
		    }
	
		    //接收到消息的回调方法
		    websocket.onmessage = function (event) {
		    	console.log("收到消息："+event.data);
		    	let s = event.data.toString();
		    	let name = s.split(':')[0];
		    	let data = s.split(':')[1];
		    	
		    	if(name == 'qps'){
		    		document.getElementById("qpsSpan").innerHTML = data;
		    	}
		    	
		    	if(name == 'session'){
		    		document.getElementById("sessionSpan").innerHTML = data;
		    	}
		    	
		    	
		    	// 随机调整边框颜色
		    	let n = 0,m = 256;
		    	
		    	let r = Math.floor(Math.random() * (m-n)) + n;
		    	let g = Math.floor(Math.random() * (m-n)) + n;
		    	let b = Math.floor(Math.random() * (m-n)) + n;
		    	
		    	document.getElementById("monitorTable").style.borderColor = 'rgb('+r+','+g+','+b+')';
		    }
	
		    //连接关闭的回调方法
		    websocket.onclose = function () {
		        document.getElementById("msgSpan").innerHTML = "WebSocket 连接关闭";
		    }
	
		    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
		    window.onbeforeunload = function () {
		    	websocket.close();
		    }
	    }
		</script>
	</body>
</html>
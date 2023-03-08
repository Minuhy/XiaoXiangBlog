<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.MessageTypeConfig"%>
<%@page import="minuhy.xiaoxiang.blog.entity.MessageEntity"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.blog.BlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
<!-- 已知漏洞：XSS跨站脚本攻击 -->
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 我的消息</title>
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_MY_NUMBER); %>
		
        <style>
            .msg-box{
                cursor:default;
                margin: 10px;
            }
            .msg-box:hover{
                background-color: #eee;
            }
            
            .msg-pointer{
                cursor: pointer;
            }
        </style>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		<jsp:useBean id="messageListBean" class="minuhy.xiaoxiang.blog.bean.user.MessageListBean" scope="session"></jsp:useBean>
		<jsp:useBean id="paginationBean" class="minuhy.xiaoxiang.blog.bean.PaginationBean" ></jsp:useBean>
		<% 
		Object obj;
		// 尝试获取昵称
		String nick = null;
		UserBean user = null;
		obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
		if(obj instanceof UserBean){
			user = (UserBean)obj;
			nick = user.getNick();
			messageListBean.setUserId(user.getId());
		}
		
		
		// 获取页面参数
		int pageNumber;
		String pageStr = RequestUtil.getReqParam(request, "p", "1"); // 从参数中获取的大1
		try{
			pageNumber = Integer.parseInt(pageStr);
		}catch(NumberFormatException e){
			pageNumber = 1;
		}
		// 获取数据
		MessageEntity[] msgs = messageListBean.getMsgDataByPage(pageNumber - 1);
		// 设置页面配置
		paginationBean.setTotal(messageListBean.getTotal());
		paginationBean.setCurrent(pageNumber);
		%>
        <!-- 内容 -->
        <div class="container">
            <h1>我的消息</h1>
            <hr>
            <%
            if(msgs == null || msgs.length == 0){
    			%>
                <div class="text-center" style="padding: 20% 0;">
	                <p class="lead" style="text-align: center;">无消息</p>
	                <% if(pageNumber > 1){ //页数大于1时再去显示回到主页，否则就只显示无消息 %>
		                <br>
		                <br>
		                <a href="<%= currentPath %>/message.jsp" class="btn btn-primary">回到第一页</a>
	                <%} %>
                </div>
    			<%
			}else{
				for(int i=0;i<msgs.length;i++){
					MessageEntity msg = msgs[i];

					String imgPath = currentPath;
					String iconText = "";
					String uiClass = "";
					switch (msg.getMsgType()) {
						case MessageTypeConfig.REPLY: // 回复消息
							iconText = "回复我的";
							if (msg.getState() == 0) { // 未读
								imgPath += "/img/svg/chat-square-text-fill.svg";
							} else {// 已读
								uiClass = "text-muted";
								imgPath += "/img/svg/chat-square-text.svg";
							}
							break;
						case MessageTypeConfig.SYSTEM: // 系统消息
							iconText = "系统消息";
							if (msg.getState() == 0) { // 未读
								imgPath += "/img/svg/bell-fill.svg";
							} else {// 已读
								uiClass = "text-muted";
								imgPath += "/img/svg/bell.svg";
							}
							break;
						case MessageTypeConfig.LIKE: // 点赞消息
							iconText = "收到的赞";
							if (msg.getState() == 0) { // 未读
								imgPath += "/img/svg/chat-square-heart-fill.svg";
							} else {// 已读
								uiClass = "text-muted";
								imgPath += "/img/svg/chat-square-heart.svg";
							}
							break;
						case MessageTypeConfig.MENTION: // 提到消息
							iconText = "提到我的";
							if (msg.getState() == 0) { // 未读
								imgPath += "/img/svg/envelope-at-fill.svg";
							} else {// 已读
								uiClass = "text-muted";
								imgPath += "/img/svg/envelope-at.svg";
							}
							break;
						default:
							iconText = "我的消息";
							if (msg.getState() == 0) { // 未读
								imgPath += "/img/svg/envelope-fill.svg";
							} else {// 已读
								uiClass = "text-muted";
								imgPath += "/img/svg/envelope.svg";
							}
							break;
					}
    			%>
            <!-- 消息 -->
            <div data-url="<%
            if(msg.getTargetUrl() == null){
            	out.print("");
            }else{
            	if(msg.getTargetUrl().startsWith("http")){
            		out.print(msg.getTargetUrl());
            	}else if("".equals(msg.getTargetUrl())){
            		out.print("");
            	}else{
            		out.print(currentPath + msg.getTargetUrl());
            	}
            }            
            %>" class="row thumbnail msg-box  msg-pointer <%=uiClass%>">
                <div class="col-xs-3 col-md-2 text-center" style="padding: 30px 0;">
                    <div style="padding: 5px;">
                        <img src="<%= imgPath %>" width="75" height="75" alt="回复我的">
                    </div>
                    <span class="lead"><%= iconText %></span>
                </div>
                <div class="col-xs-9 col-md-10" style="padding: 10px;">
                    <h3>
						<%= msg.getTitle() %>
					</h3>
                    <span style="color:#999;">
                    	<%= TimeUtil.timestamp2DateTime(msg.getCreateTimestamp()) %>
                    </span>
                    <p>
						<%= msg.getContent() %>
                    </p>
                </div>
            </div>
            <%
    		} // for
    		%>
    		
    		
            <br>
            <hr>
            <br>
            
            
            <!-- 页数导航 -->
            <%
            paginationBean.setTargetPage(currentPath+"/message.jsp");
            paginationBean.setParamName("p");
            %>
            <nav>
                <form action="<%= paginationBean.getTargetPage() %>" method="get" class="input-group hidden-xs" >
                    <div class="input-group-btn">
                        <a href="<%= paginationBean.getTargetPage() %>" class="btn btn-default">首页</a>
                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()-1) %>" <%= paginationBean.isPrevious()?"":"disabled=\"disabled\"" %> class="btn btn-default" type="button">上一页</a>
                    </div>
                    <span class="input-group-addon">第<%=paginationBean.getCurrent() %>/<%= paginationBean.getTotal() %>页</span>
                    <input name="p" type="number" min="1" value="<%= paginationBean.getCurrent() %>" max="<%= paginationBean.getTotal() %>" class="form-control" aria-label="页码">
                    <div class="input-group-btn">
                        <button class="btn btn-default" type="submit">跳转</button>
                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()+1) %>"  <%= paginationBean.isNext()?"":"disabled=\"disabled\"" %> class="btn btn-default" type="button" >下一页</a>
                        <a href="<%= paginationBean.getUrlPre() + paginationBean.getTotal() %>" class="btn btn-default" type="button">末页</a>
                    </div>
                </form>
            
                <form action="<%= paginationBean.getTargetPage() %>" method="get" class="hidden-sm hidden-md hidden-lg text-center">
                    <div class="input-group">
                        <span class="input-group-addon">第<%=paginationBean.getCurrent() %>/<%= paginationBean.getTotal() %>页</span>
                        <input name="p" type="number" min="1" value="<%= paginationBean.getCurrent() %>" max="<%= paginationBean.getTotal() %>" class="form-control" aria-label="页码">
                        <div class="input-group-btn">
                            <button class="btn btn-default" type="submit">跳转</button>
                        </div>
                    </div>
            
                    <br/>
            
                    <div class="btn-group" role="group" aria-label="Default button group">
                        <a href="<%= paginationBean.getTargetPage() %>" class="btn btn-default">首页</a>
                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()-1) %>" <%= paginationBean.isPrevious()?"":"disabled=\"disabled\"" %> class="btn btn-default" type="button">上一页</a>
                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()+1) %>"  <%= paginationBean.isNext()?"":"disabled=\"disabled\"" %> class="btn btn-default" type="button" >下一页</a>
                        <a href="<%= paginationBean.getUrlPre() + paginationBean.getTotal() %>" class="btn btn-default" type="button">末页</a>
                    </div>
                </form>
            </nav>
            <%
    		} // else
    		%>
            
        </div>

		<jsp:include page="partial/common/foot.jsp"></jsp:include>
		<script type="text/javascript">
        $(document).ready(function(){
            var allMsgBoxs = $(".msg-box");
            for (let index = 0; index < allMsgBoxs.length; index++) {
                let url =  $(allMsgBoxs[index]).attr("data-url");
                if (undefined == url || "" == url) {
                    $(allMsgBoxs[index]).removeClass('msg-pointer');
                }
            }
            
            var allAs = $("a");
            for (let index = 0; index < allAs.length; index++) {
                let disable = $(allAs[index]).attr("disabled");
                if ("disabled" == disable) {
                	console.log(disable);
                    allAs[index].removeAttribute('href');
                }
            }

            $(".msg-box").click(function(){
                let url = $(this).attr("data-url");
                if(undefined != url && ""!=url){
                    console.log(url);
                    location.href=url;
                }else{
                    console.log('不是一个链接');
                }
            });
        });
		</script>
	</body>
</html>
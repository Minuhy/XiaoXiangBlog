<%@page import="minuhy.xiaoxiang.blog.bean.admin.EditBean"%>
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
	
	String errorStr = "";
	
    // 类型
    int type= -1; // 类型，t，0：user，1：blog，2：comment
    String typeStr = RequestUtil.getReqParam(request, "t", ""); // 从参数中获取的大1
    try{
    	type = Integer.parseInt(typeStr);
    }catch(NumberFormatException e){
    	// response.setHeader("refresh","刷新时间间隔;目的页面地址");
    	errorStr = "类型参数错误";
    	response.setHeader("refresh","2;"+currentPath+"/admin/index.jsp");
    }
    
    // 页数
	int pageNumber;
	String pageStr = RequestUtil.getReqParam(request, "p", "1"); // 从参数中获取的大1
	try{
		pageNumber = Integer.parseInt(pageStr);
	}catch(NumberFormatException e){
		pageNumber = 1;
	}
	
	
    // ID
    int id= -1;
    String idStr = RequestUtil.getReqParam(request, "i", ""); // 从参数中获取的大1
    try{
    	id = Integer.parseInt(idStr);
    }catch(NumberFormatException e){
    	errorStr = "编号参数错误";
    	if(errorStr.equals("")){
    		if(type==0){
        		response.setHeader("refresh","2;"+currentPath+"/admin/user.jsp?p="+pageNumber);
        	}else if(type == 1){
        		response.setHeader("refresh","2;"+currentPath+"/admin/blog.jsp?p="+pageNumber);
        	}else if(type == 2){
        		response.setHeader("refresh","2;"+currentPath+"/admin/comment.jsp?p="+pageNumber);
        	}else{
            	response.setHeader("refresh","2;"+currentPath+"/admin/index.jsp");
        	}
    	}
    }
%>
<jsp:useBean id="adminEditBean" class="minuhy.xiaoxiang.blog.bean.admin.AdminEditBean"></jsp:useBean>
<%
adminEditBean.setId(id);
adminEditBean.setPage(pageNumber);
adminEditBean.setType(type);

String errorMsg = null;
EditBean[] editBeans = null;

try{
	editBeans = adminEditBean.getData();
}catch(Exception e){
	errorMsg = "获取数据失败："+e.getMessage();
}

%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
<head>
<meta charset="UTF-8">
<jsp:include page="common/head.jsp"></jsp:include>
<title>潇湘博客 - 编辑数据</title>
<style>
    table{
        font-size: large;
       min-width: 800px;
    }
    button{
        margin: 20px;
        padding: 6px 30px;
        font-size: larger;
    }
    input{
        padding: 6px 10px;
        font-size: large;
        border: 0;
        width: 90%;
    }
    td{
        padding: 10px;
        font-size: large;
    }
</style>
</head>
<body>
	<jsp:include page="common/navigation.jsp"></jsp:include>
	<div style="text-align: center;padding: 10px;width: 1500px;margin: 0 auto;">
		
        <form action="<%=currentPath%>/admin/operate.jsp" method="post">
       		<%if(editBeans!=null){%>
	       		<h1>修改数据 - <%
	       			if(type==0){
	       				out.print("用户");
	       			}else if(type==1){
	       				out.print("博客");
	       			}else if(type==2){
	       				out.print("评论");
	       			}
	       		%>编号：<%= editBeans[0].getValue() %></h1>
       		<%}else{ %>
       		<h1>修改数据</h1>
       		<%} %>
       		<input type="hidden" name="p" value="<%=pageNumber%>">
       		<input type="hidden" name="op" value="<%
       			if(type==0){
       				out.print("eu");
       			}else if(type==1){
       				out.print("eb");
       			}else if(type==2){
       				out.print("ec");
       			}
       		%>">
       		<input type="hidden" name="type" value="<%=type%>">
            <table>
                <tbody>
                	<%if(editBeans!=null){ 
                		for(EditBean editBean:editBeans){
                	%>
                    <tr>
                        <td><%=editBean.getLabel() %></td>
                        <td style="text-align: left;">
                            <% if(editBean.getValChoose() == null){ %>
                            	<input 
		                            type="<%= editBean.getType() %>" 
		                            name="<%=editBean.getName() %>" 
		                            placeholder="<%=editBean.getHint()%>" 
		                            value="<%= editBean.getValue() %>"
		                            <%= editBean.isCanEdit()?"":"readonly" %>
	                            />
                            <%}else{ %>
                            	<select 
                            		name="<%=editBean.getName() %>" 
		                            <%= editBean.isCanEdit()?"":"disabled" %>
                            	>
                            		<% 
                            		for(int i=0;i<editBean.getValChoose().length;i+=2){
                            			String itemValue = editBean.getValChoose()[i];
                            			String itemText = editBean.getValChoose()[i+1];
                            			boolean isChoose = false;
                            			if(editBean.getValue().equals(itemValue)){
                            				isChoose = true;
                            			}
                            		%>
                            			<option <%=isChoose?"selected":"" %> value ="<%=itemValue%>"><%= itemText %></option>
                            		<% } %>
                            	</select>
                            <%} %>
                        </td>
                    </tr>
                    <%	
                    	}
                	}else{ %>
                    <tr>
                        <td><%= errorMsg %></td>
                    </tr>
                    <%} %>
                </tbody>
            </table>
            <button type="button" onclick="back()">取消</button>
            <button type="submit">保存</button>
        </form>
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
    <script type="text/javascript">
    function back() {
    	let id=<%=id%>;
    	let type=<%=type%>;
    	let page=<%=pageNumber%>;
    	let baseUrl = "<%=currentPath%>";
    	if(type == 0){ // user
    		window.location.href=baseUrl + "/admin/user.jsp?p="+page;
    	}else if(type == 1){ // blog
    		window.location.href=baseUrl + "/admin/blog.jsp?p="+page;
    	}else if(type == 2){ // comment
    		window.location.href=baseUrl + "/admin/comment.jsp?p="+page;
    	}else{
    		window.location.href=baseUrl + "/admin/index.jsp";
    	}
	}
    </script>
</body>
</html>
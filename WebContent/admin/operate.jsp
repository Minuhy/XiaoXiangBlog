<%@page import="minuhy.xiaoxiang.blog.bean.admin.AdminBlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.bean.admin.AdminCommentBean"%>
<%@page import="minuhy.xiaoxiang.blog.bean.admin.AdminUserBean"%>
<%@page import="java.sql.SQLException"%>
<%@page import="minuhy.xiaoxiang.blog.database.AdminDb"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>操作结果</title>
</head>
<body>
<%!

// 获取ID列表
int[] getIdList(HttpServletRequest request){
    String idListString = request.getParameter("idList");
    String[] idList = idListString.substring(0, idListString.length()-1).split(";");
	int[] ids = new int[idList.length];
	int i=0;
	for(String idString : idList){
		ids[i++] = Integer.parseInt(idString);
	}
	return ids;
}

/**
 * 删除
 **/
String delete(int type,HttpServletRequest request){
	int[] ids = getIdList(request);
	
	AdminDb adminDb = new AdminDb();
	try{
		if(adminDb.deleteItemByTypeAndIds(type, ids) < 1){
			return "没有执行更改";
		}
	}catch(SQLException e){
		return "数据库错误："+e.getMessage();
	}
	return null;
}

/**
 * 冻结
 **/
String freeze(int type,HttpServletRequest request){
	int[] ids = getIdList(request);
	
	AdminDb adminDb = new AdminDb();
	try{
		if(adminDb.activeItemByTypeAndIds(false,type, ids) < 1){
			return "没有执行更改";
		}
	}catch(SQLException e){
		return "数据库错误："+e.getMessage();
	}
	return null;
}

/**
 * 激活
 **/
String active(int type,HttpServletRequest request){
	int[] ids = getIdList(request);
	
	AdminDb adminDb = new AdminDb();
	try{
		if(adminDb.activeItemByTypeAndIds(true,type, ids) < 1){
			return "没有执行更改";
		}
	}catch(SQLException e){
		return "数据库错误："+e.getMessage();
	}
	return null;
}


/**
 * 编辑博客
 **/
String editBlog(HttpServletRequest req){
	AdminBlogBean bean = new AdminBlogBean();
	return bean.editBlog(req);
}

/**
 * 编辑用户
 **/
String editUser(HttpServletRequest req){
	AdminUserBean bean = new AdminUserBean();
	return bean.editUser(req);
}

/**
 * 编辑评论
 **/
String editComment(HttpServletRequest req){
	AdminCommentBean bean = new AdminCommentBean();
	return bean.editComment(req);
}

%>
<%
	String id = null;
    
    id = request.getParameter("id");


	
	int type = -1;
	int pageNumber = 1;
	String pageName = "index";
    
    String pageString = request.getParameter("p");
    String operateString = request.getParameter("op");
    String typeString = request.getParameter("type");
    if(typeString!=null 
    		&& typeString.length()>0 
    		&& pageString!=null
    		&& pageString.length()>0
    		&& operateString!=null
    		&& operateString.length()>0){
		
    	try{
    		
    		try{
	    		type = Integer.parseInt(typeString);
	    		if(type!=1 && type!=0&&type!=2){
	    			throw new NumberFormatException("类型不受支持");
	    		}

	    		if(type == 0){
	    			 pageName = "user";
	    		}else if(type == 1){
	    			 pageName = "blog";
	    		}else if(type == 2){
	    			 pageName = "comment";
	    		}
	    		
    		}catch(NumberFormatException e){
    			%>
	    		<div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
				    <h1>参数错误：<%= e.getMessage() %></h1>
				    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/<%=pageName %>.jsp?p=<%=pageNumber+(id==null?"":"#"+id)%>">点击此处继续</a></p>
				    <%
				    	response.setHeader("refresh","3;"+currentPath + "/admin/"+pageName+".jsp?p="+pageNumber+(id==null?"":"#"+id));
				    %>
				</div>
    			<%
    		}
    		
    		
    		try{
    			pageNumber = Integer.parseInt(pageString);
	    		if(pageNumber<1){
	    			throw new NumberFormatException("页数不正确");
	    		}
    		}catch(NumberFormatException e){
    			%>
	    		<div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
				    <h1>参数错误：<%= e.getMessage() %></h1>
				    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/<%=pageName %>.jsp?p=<%=pageNumber+(id==null?"":"#"+id)%>">点击此处继续</a></p>
				    <%
				    response.setHeader("refresh","3;"+
			    		currentPath + "/admin/"+pageName+".jsp?"+
				    	"p="+pageNumber+
				    	"&t="+type+
				    	"&i="+(id==null?"":id)+
				    	(id==null?"":"#"+id));
				    %>
				</div>
    			<%
    		}
    		
    		
    		String result = null;
    		
    		if(operateString.equals("del")){ // 删除操作
        		result = delete(type,request);
    		}else if(operateString.equals("fre")){ // 冻结操作
        		result = freeze(type,request);
    		}else if(operateString.equals("act")){ // 激活操作
        		result = active(type,request);
        		
    		}
    		
    		else 
    			
			// 下面添加提交编辑后，返回到编辑界面，而不是都返回到列表界面
			if(operateString.equals("eu")){ // 编辑用户操作
    			pageName="edit";
        		result = editUser(request);
    		}else if(operateString.equals("eb")){ // 编辑博客操作
    			pageName="edit";
        		result = editBlog(request);
    		}else if(operateString.equals("ec")){ // 编辑评论操作
    			pageName="edit";
        		result = editComment(request);
    		}else{
    			result = "未定义的操作";
    		}
    		
    		
    		if(result == null){
	    		%>
	    		<div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
				    <h1>操作成功</h1>
				    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/<%=pageName %>.jsp?p=<%=pageNumber+(id==null?"":"#"+id)%>">点击此处继续</a></p>
				    <%
				    response.setHeader("refresh","3;"+
			    		currentPath + "/admin/"+pageName+".jsp?"+
				    	"p="+pageNumber+
				    	"&t="+type+
				    	"&i="+(id==null?"":id)+
				    	(id==null?"":"#"+id));
				    %>
				</div>
				<%
    		}else{
    			%>
        		<div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
    			    <h1>操作失败：<%= result %></h1>
    			    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/<%=pageName %>.jsp?p=<%=pageNumber+(id==null?"":"#"+id)%>">点击此处继续</a></p>
    			    <%
				    response.setHeader("refresh","3;"+
			    		currentPath + "/admin/"+pageName+".jsp?"+
				    	"p="+pageNumber+
				    	"&t="+type+
				    	"&i="+(id==null?"":id)+
				    	(id==null?"":"#"+id));
				    %>
    			</div>
    			<%
    		}
    	}catch(NumberFormatException e){
			%>
    		<div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
			    <h1>参数错误：<%= e.getMessage() %></h1>
			    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/<%=pageName %>.jsp?p=<%=pageNumber+(id==null?"":"#"+id)%>">点击此处继续</a></p>
			    <%
			    response.setHeader("refresh","3;"+
		    		currentPath + "/admin/"+pageName+".jsp?"+
			    	"p="+pageNumber+
			    	"&t="+type+
			    	"&i="+(id==null?"":id)+
			    	(id==null?"":"#"+id));
			    %>
			</div>
			<%
		}
    }else{
%>
<div style="position:absolute;top: 50%;left: 50%;transform:translate(-50%,-50%);text-align: center;">
    <h1>缺少参数</h1>
    <p>三秒后继续，若无响应<a href="<%=currentPath%>/admin/index.jsp">点击此处继续</a></p>
    <%
    response.setHeader("refresh","3;"+
   		currentPath + "/admin/"+pageName+".jsp?"+
    	"p="+pageNumber+
    	"&t="+type+
    	"&i="+(id==null?"":id)+
    	(id==null?"":"#"+id));
    %>
</div>
<%} %>

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
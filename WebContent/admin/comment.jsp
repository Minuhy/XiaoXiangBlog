<%@page import="minuhy.xiaoxiang.blog.entity.CommentEntity"%>
<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.entity.BlogEntity"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.bean.PaginationBean"%>
<%@page import="minuhy.xiaoxiang.blog.util.TimeUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@ page import="minuhy.xiaoxiang.blog.util.RequestUtil" %>
<%@ page import="minuhy.xiaoxiang.blog.config.StatisticsConfig" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
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
%>
<jsp:useBean id="searchBean" class="minuhy.xiaoxiang.blog.bean.admin.AdminCommentSearchBean"></jsp:useBean>
<jsp:useBean id="adminBean" class="minuhy.xiaoxiang.blog.bean.admin.AdminCommentBean"></jsp:useBean>
<%
    // 获取页面参数
    int pageNumber;
    String pageStr = RequestUtil.getReqParam(request, "p", "1"); // 从参数中获取的大1
    try{
        pageNumber = Integer.parseInt(pageStr);
    }catch(NumberFormatException e){
        pageNumber = 1;
    }

   	CommentEntity[] entities = null;
    String errorMsg = null;

    
    String keyWord = RequestUtil.getReqParam(request, "keyword", "");
    
    try {
		if(!keyWord.equals("")){
   	    	// 搜索
			searchBean.setKeyWord(keyWord);
   	    	entities = searchBean.getDataBySearch(pageNumber - 1);
   	    }else{
			searchBean.setKeyWord(null);
   	    	entities = adminBean.getData(pageNumber - 1);
   	    }
    }catch (Exception e){
        errorMsg = e.getMessage();
    }
%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
<head>
<meta charset="UTF-8">
<jsp:include page="common/head.jsp"></jsp:include>
<title>潇湘博客 - 评论管理</title>
</head>
<body>
	<jsp:include page="common/navigation.jsp"></jsp:include>
	<div style="text-align: center;padding: 10px;width: 1500px;margin: 0 auto;">
		<div style="padding: 40px;">
			<form method="get">
                <label>
                    <input value="<%= searchBean.getKeyWord()==null?"":searchBean.getKeyWord() %>" style="margin:0; border:0;border-bottom:2px solid #000; width: 200px;font-size: larger;padding: 4px;" name="keyword" type="text" autofocus>
                </label>
                <button style="border-radius:10px; border:0; padding: 10px;font-size: larger;width: 80px;" type="submit">搜索</button>
            </form>
        </div>
        <hr>
        <table>
            <thead>
                <tr>
                    <th>选择</th>
                    <th>编号</th>
                    <th>激活</th>
                    <th class="can-search">内容</th>
                    <th class="can-search">博文编号</th>
                    <th class="can-search">用户编号</th>
                    <th class="can-search">回复编号</th>
                    <th class="can-search">发表时间</th>
                    <th class="can-search">修改时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if(entities!=null){
                        if(entities.length>0){
                            // 写数据
                            for (CommentEntity entity:entities){
                %>
                <tr id="tr<%=entity.getId()%>">
                    <td >
                        <label>
                            <input style="padding: 0;width: 30px;height: 30px;" id="input<%=entity.getId()%>" onclick="chooseItem(<%=entity.getId()%>)" name="cb" type="checkbox">
                        </label>
                    </td>
                    <td><%=entity.getId()%></td>
                    <td><%=entity.getActive()==1?'是':'否'%></td>
                    <td>
                    	<a 
                    	title="<%=entity.getContent()%>" 
                    	href="<%=currentPath%>/read.jsp?comment=true&i=<%=entity.getBlogId()%>#commentItem<%=entity.getId()%>"
                    	>
                    		<%= TextUtil.maxLenJustify(entity.getContent(), 30) %>
                    	</a>
                    </td>
                    <td><%=entity.getBlogId()%></td>
                    <td><%=entity.getUserId()%></td>
                    <td><%=entity.getReplyId()%></td>
                    <td><%=TimeUtil.timestamp2DateTime(entity.getCreateTimestamp())%></td>
                    <td><%=entity.getUpdateTimestamp()==0?"未修改":TimeUtil.timestamp2DateTime(entity.getUpdateTimestamp())%></td>
                    <td>
                        <button onclick="optionItem(<%=entity.getId()%>)">编辑</button>
                    </td>
                </tr>
                <%
                            }
                        }else{
                            // 无数据
                %>
                <tr>
                    <td colspan="13">无数据</td>
                </tr>
                <%
                        }
                    }else{
                        // 数据库出错
						response.setHeader("refresh","3;"+currentPath + "/admin/comment.jsp");
                %>
                <tr>
                    <td colspan="13">数据库出错：<%=errorMsg%></td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        <hr>
		<%
            	PaginationBean paginationBean = null;
                if(searchBean.getKeyWord()!=null){
                	paginationBean = searchBean.getPaginationBean();
                    paginationBean.setParamName("p");
                    paginationBean.setTargetPage(currentPath+"/admin/comment.jsp?keyword="+searchBean.getKeyWord());
                }else{
                    paginationBean = adminBean.getPaginationBean();
                    paginationBean.setParamName("p");
                    paginationBean.setTargetPage(currentPath+"/admin/comment.jsp");
                }
        %>
        <div id="page-nav">
            <a href="<%=paginationBean.getUrlPre()+1%>">首页</a>
            <%if(paginationBean.isPrevious()){%>
                <a href="<%=paginationBean.getUrlPre()+(paginationBean.getCurrent()-1)%>">上一页</a>
            <%}%>
            <%if(paginationBean.isNext()){%>
            <a href="<%=paginationBean.getUrlPre()+(paginationBean.getCurrent()+1)%>">下一页</a>
            <%}%>
            <a href="<%=paginationBean.getUrlPre()+paginationBean.getTotal()%>">末页</a>
            <br>
            <br>
            <form method="get">
                <% if(searchBean.getKeyWord()!=null){ %>
                <input value="<%= searchBean.getKeyWord() %>"  name="keyword" type="hidden"/>
                <%} %>
                <input style="width: 80px;font-size: 18px; padding: 3px;" value="<%= paginationBean.getCurrent() %>"  name="p" type="number" min="1" max="<%=paginationBean.getTotal()%>" placeholder="页数" />
                <button style="width:60px; padding: 6px;font-size: 18px;"  type="submit">跳转</button>
            </form>
        </div>
        <hr>
        <div style="padding: 10px;">
	        <form style="display: inline-block;margin: 20px;" action="operate.jsp" method="post">
	        	<input name="op" value="act"  type="hidden" required="required" >
	        	<input name="p" value="<%= pageNumber %>"  type="hidden" required="required" >
	        	<input name="type" value="2" type="hidden" required="required" > <!-- 表示的是操作的类型是用户 -->
	        	<input id="inputActive" name="idList" type="hidden" required="required" > <!-- 要操作的列表 -->
	            <button style="padding: 5px;font-size: 20px;" type="submit">激活已选</button>
	        </form>
	        
	        <form style="display: inline-block;margin: 20px;" action="operate.jsp" method="post">
	        	<input name="op" value="fre"  type="hidden" required="required" >
	        	<input name="p" value="<%= pageNumber %>"  type="hidden" required="required" >
	        	<input name="type" value="2" type="hidden" required="required" > <!-- 表示的是操作的类型是用户 -->
	        	<input id="inputFreeze" name="idList" type="hidden" required="required" > <!-- 要操作的列表 -->
	            <button style="padding: 5px;font-size: 20px;" type="submit">冻结已选</button>
	        </form>
        
	        <form onsubmit="return sumbit_sure()" style="display: inline-block;margin: 20px;" action="operate.jsp" method="post">
	        	<input name="op" value="del"  type="hidden" required="required" >
	        	<input name="p" value="<%= pageNumber %>"  type="hidden" required="required" >
	        	<input name="type" value="2"  type="hidden" required="required" >
	        	<input id="inputDelete" name="idList" type="hidden" required="required" >
	            <button style="padding: 5px;font-size: 20px;" type="submit">删除已选</button>
	        </form>
        </div>
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
	var chooseList = [];

	window.onload = (event) => {
		console.log('页面加载完毕');
		for (let ele of document.getElementsByName('cb')) {
			ele.checked=false
		}
		
		setTimeout(function () {
			setItemColor('#fc9')
			setTimeout(function () {
				setItemColor('#fc9')
			},3000);
		},300);
		
	}
	
	function setItemColor(c) {
		try{
			var hele = document.getElementById(window.location.hash.replace('#','tr'))
			hele.style.background=c
		}catch (e) {
			console.log('跳过设置背景色');
		}
	}
	
    function optionItem(id) {
        console.log('操作编号：'+id)
        window.location.href="<%=currentPath%>/admin/edit.jsp?t=2&p=<%=pageNumber%>&i="+id;
    }

    function chooseItem(id) {
        console.log('选择编号：'+id)
        
        if(document.getElementById('input'+id).checked){
			console.log('选择')
			chooseList.push(id)
	        document.getElementById('tr'+id).style.backgroundColor="#ddd"
        }else{
			console.log('取消')
			try{
				chooseList.splice(chooseList.indexOf(id), 1)
	        	document.getElementById('tr'+id).style.backgroundColor="#fff"
			}catch(e){
				console.log('删除元素时出错，找不到'+e)
			}
        }
        
        console.log(chooseList)
        let v = ''
        for(let i=0;i<chooseList.length;i++){
        	v = v + chooseList[i]+';'
        }
        console.log(v)
        document.getElementById('inputFreeze').value=v
		document.getElementById('inputDelete').value=v
		document.getElementById('inputActive').value=v
    }
</script>
<script>
function sumbit_sure(){
    var gnl=confirm("确定要删除？此操作不可恢复！");
    if (gnl==true){
        return true;
    }else{
        return false;
    }
}
</script>
</body>
</html>
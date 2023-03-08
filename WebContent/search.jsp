<%@page import="minuhy.xiaoxiang.blog.bean.blog.MiniBlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<jsp:useBean id="searchBlogsBean" class="minuhy.xiaoxiang.blog.bean.blog.SearchBlogsBean" scope="page"></jsp:useBean>
<jsp:useBean id="paginationBean" class="minuhy.xiaoxiang.blog.bean.PaginationBean" ></jsp:useBean>
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<%
	// 获取页面参数 关键词
	String searchKey = RequestUtil.getReqParam(request, "s", ""); // 从参数中获取
	// 获取页面参数 页数
	int pageNumber;
	String pageStr = RequestUtil.getReqParam(request, "p", "1"); // 从参数中获取的大1
	try{
		pageNumber = Integer.parseInt(pageStr);
		if(pageNumber<1){
			pageNumber = 1;
		}
	}catch(NumberFormatException e){
		pageNumber = 1;
	}
	%>
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>搜索“<%= TextUtil.maxLenJustify(searchKey, 20) %>” - 潇湘博客</title>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
        <!-- 内容 -->
        <div class="container">
            <form class="form-inline text-center" style="margin: 50px 0;"  action="<%= currentPath %>/search.jsp" method="get">
                <div class="form-group">
                  <label class="sr-only" for="keyWord">关键词</label>
                  <input name="s" value="<%= searchKey %>" type="text" class="form-control" id="keyWord" placeholder="请输入关键词">
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
            <hr>
            <!-- 博客列表 -->
            <div>
      		<% 
      		// 获取个人博文
			try{
	       		// 获取数据
				MiniBlogBean[] blogs = searchBlogsBean.getDataByPage(searchKey, pageNumber-1);
	       		// 设置页面配置
	       		paginationBean.setTotal(searchBlogsBean.getTotal(searchKey));
	       		paginationBean.setCurrent(pageNumber);
	
        		if(blogs!=null && blogs.length>0){
					// 加载博文信息
					for(MiniBlogBean blog:blogs){
			%>
			        	<blockquote>
			            	<h2 class="text-primary">
			                	<a href="<%=currentPath %>/read.jsp?i=<%=blog.getId()%>" target="_blank">
			                    	<%=TextUtil.maxLen(blog.getTitle(), 50) %>
			                    </a>
			                </h2>
			                <h4 class="text-muted" style="font-size: 14px"><%= blog.getAuthor() %>&nbsp;<%= blog.getDateTime() %></h4>
			                <p class="lead text-info">	
			                	<a href="<%=currentPath %>/read.jsp?i=<%=blog.getId()%>" target="_blank">
									<%= blog.getPreview() %>
								</a>
							</p>
		                    <%-- 数据统计预览 --%>
		                    <h5 style="font-size:16px;">
		                        <span class="glyphicon glyphicon-eye-open label label-default" aria-hidden="true">&nbsp;<%=blog.getReadCount()%></span>
		                        <span class="glyphicon glyphicon-thumbs-up label label-default" aria-hidden="true">&nbsp;<%=blog.getLikeCount()%></span>
		                        <span class="glyphicon glyphicon-comment label label-default" aria-hidden="true">&nbsp;<%=blog.getCommentCount()%></span>
		                    </h5>
						</blockquote>
	                <%
					} // for 结束
				
					%>
					<!-- 页数导航 -->
		            <%
		            paginationBean.setTargetPage(currentPath+"/search.jsp?s="+searchKey);
		            paginationBean.setParamName("p");
		            %>
				    <nav>
				    	<form action="<%= paginationBean.getTargetPage() %>" method="get" class="input-group hidden-xs" >
				        	<div class="input-group-btn">
				            	<a href="<%= paginationBean.getTargetPage() %>" class="btn btn-default">首页</a>
				            	<a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()-1) %>" class="btn btn-default  <%= paginationBean.isPrevious()?"":"hidden" %>" type="button">上一页</a>
				            </div>
				            <span class="input-group-addon">第<%=paginationBean.getCurrent() %>/<%= paginationBean.getTotal() %>页</span>
				            <input name="p" type="number" min="1" value="<%= paginationBean.getCurrent() %>" max="<%= paginationBean.getTotal() %>" class="form-control" aria-label="页码">
				            <div class="input-group-btn">
				            	<button class="btn btn-default" type="submit">跳转</button>
			                	<a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()+1) %>"  class="btn btn-default  <%= paginationBean.isNext()?"":"hidden" %>" type="button" >下一页</a>
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
		                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()-1) %>"  class="btn btn-default  <%= paginationBean.isPrevious()?"":"hidden" %>" type="button">上一页</a>
		                        <a href="<%= paginationBean.getUrlPre() + (paginationBean.getCurrent()+1) %>"   class="btn btn-default <%= paginationBean.isNext()?"":"hidden" %>" type="button" >下一页</a>
		                        <a href="<%= paginationBean.getUrlPre() + paginationBean.getTotal() %>" class="btn btn-default" type="button">末页</a>
		                    </div>
						</form>
					</nav>
				<%
						
				}else{ // blogs.length>0
					// 获取到的博客数量是 0
				%>
					<p class="lead" style="color: orange;text-align: center;margin: 100px auto;font-size: 26px;">
				    	<span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
				        <br>
				                    没有博文
				    	<br>
				    	<br>
				    	<a href="<%= currentPath %>/blog.jsp" class="btn btn-primary">回到博客</a>
					</p>
				<%
				}
			}catch(Exception e){ // 捕获SQL错误
				e.printStackTrace();
				%>
				<p class="lead" style="color: red;text-align: center;margin: 100px auto;font-size: 26px;">
	            	<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>
	           		<br>
	        		 数据库错误
	   			</p>
			<%
			}
            %>
			</div>
		</div>
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
	</body>
</html>
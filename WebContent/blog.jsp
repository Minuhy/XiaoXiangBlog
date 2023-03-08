<%@page import="minuhy.xiaoxiang.blog.util.TextUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.blog.BlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<jsp:useBean id="rankBlogsBean" class="minuhy.xiaoxiang.blog.bean.blog.RankBlogsBean" scope="application"></jsp:useBean>
<jsp:useBean id="paginationBean" class="minuhy.xiaoxiang.blog.bean.PaginationBean" ></jsp:useBean>
		
<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 博客列表</title>
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_BLOG_NUMBER); %>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		
		 <!-- 内容 -->
        <div class="container">
        
	        <%
		    // 获取页面参数
			int pageNumber;
			String pageStr = RequestUtil.getReqParam(request, "p", "1"); // 从参数中获取的大1
			try{
				pageNumber = Integer.parseInt(pageStr);
			}catch(NumberFormatException e){
				pageNumber = 1;
			}
			
			// 获取页面参数
			String rank = RequestUtil.getReqParam(request, "rank", "def"); // 从参数中获取的大1
    		rankBlogsBean.setRank(rank);
      		
	        %>

            <div style="height: 70px;">
            	<div style="float:left">
	            	<h1>所有博客</h1>
	            </div>
	            <div role="presentation" class="dropdown" style="float:right;margin-top: 20px;">
			    	<button id="dropOp" class="dropdown-toggle btn btn-default" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
			        	排序<%
			        	
			        	if("lik".equals(rankBlogsBean.getRank())){ // 按点赞数
			    			out.print("（点赞量）");
			        	}else if("com".equals(rankBlogsBean.getRank())){ // 按评论数
			    			out.print("（评论量）");
			    		}else if("rea".equals(rankBlogsBean.getRank())){ // 按浏览量
			    			out.print("（浏览量）");
			    		}else if("cre".equals(rankBlogsBean.getRank())){ // 按发表时间
			    			out.print("（发表时间）");
			        	}else if("upd".equals(rankBlogsBean.getRank())){ // 按修改时间
			    			out.print("（修改时间）");
			        	}
			        	
			        	%>
			          	<span class="caret"></span>
			       	</button>
			        <ul class="dropdown-menu pull-right" aria-labelledby="dropOp">
			        	<li><a href="<%=currentPath%>/blog.jsp">默认</a></li>
			        	<li role="separator" class="divider"></li>
			        	<li><a href="<%=currentPath%>/blog.jsp?rank=lik">按点赞量</a></li>
			        	<li><a href="<%=currentPath%>/blog.jsp?rank=com">按评论量</a></li>
			        	<li><a href="<%=currentPath%>/blog.jsp?rank=rea">按浏览量</a></li>
			        	<li role="separator" class="divider"></li>
			        	<li><a href="<%=currentPath%>/blog.jsp?rank=cre">按发表时间</a></li>
			        	<li><a href="<%=currentPath%>/blog.jsp?rank=upd">按修改时间</a></li>
			        </ul>
			    </div>
            </div>
            <hr>
            <!-- 博客列表 -->
            <div>
            	<% // 获取个人博文
	                try{
		        		// 获取数据
		        		BlogBean[] blogs = rankBlogsBean.getDataByPage(pageNumber - 1);
		        		// 设置页面配置
		        		paginationBean.setTotal(rankBlogsBean.getTotal());
		        		paginationBean.setCurrent(pageNumber);
	
		        		if(blogs!=null && blogs.length>0){
							// 加载博文信息
							for(BlogBean blog:blogs){
							%>
			                <blockquote>
			                    <h2 class="text-primary">
			                        <a href="<%=currentPath %>/read.jsp?i=<%=blog.getId()%>" target="_blank">
			                        	<%=TextUtil.maxLen(blog.getTitle(), 50) %>
			                        </a>
			                    </h2>
			                    <h4 class="text-muted"><%= blog.getDateTime() %></h4>
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
				            if(rankBlogsBean.getRank().equals("def")){
				            	paginationBean.setTargetPage(currentPath+"/blog.jsp");
				            }else{
				            	paginationBean.setTargetPage(currentPath+"/blog.jsp?rank="+rankBlogsBean.getRank());
				            }
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
				                    <a href="<%= currentPath %>/blog.jsp" class="btn btn-primary">回到第一页</a>
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
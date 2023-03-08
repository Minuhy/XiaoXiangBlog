<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<header>
    <nav>
        <div class="nav">
            <span>
                <a href="<%= currentPath %>/admin/index.jsp">管理主页</a>
            </span>
            <span>
                <a href="<%= currentPath %>/admin/user.jsp">用户管理</a>
            </span>
            <span>
                <a href="<%= currentPath %>/admin/blog.jsp">博客管理</a>
            </span>
            <span>
                <a href="<%= currentPath %>/admin/comment.jsp">评论管理</a>
            </span>
            <span>
                <a href="<%= currentPath %>/admin/notice.jsp">发布消息</a>
            </span>
            <div style="float:right;">
                <span>
                    <a href="<%= currentPath %>/people.jsp">我的主页</a>
                </span>
                <span>
                    <a href="<%= currentPath %>/user/logout">退出登录</a>
                </span>
            </div>
        </div>
    </nav>
</header>
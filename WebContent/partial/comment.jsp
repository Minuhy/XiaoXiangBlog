<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<!-- 阅读页面的评论部分 -->
<style>
	.skip-a{
	    cursor:pointer;
	    color: #42f;
	}
	.skip-a:hover{
	    text-decoration: underline;
	    color: #97f;
	}
    .comment-item{
        border: 1px solid #00000000;
        border-radius: 5px;
        padding: 4px;
    }
    .comment-item > div{
        border-bottom: 1px dashed #66666666;
    }
    
     .commentContent {
         width: 100%;
         height: auto;
         word-wrap:break-word;
         word-break:break-all;
         overflow: hidden;
     }
</style>
<% 
	boolean isAdmin = false;
	Object obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
	if(obj instanceof UserBean){
		if(((UserBean)obj).getRole() == 1){
			isAdmin = true;
		}
	}
%>    
<h4>评论</h4>
<hr>
<!-- 评论 -->
<div id="commentDiv">
	<%-- 使用 doT.js 模板引擎 --%>
    <script type="text/x-dot-temolate" id="commentListTemplate">
        <!-- 参考文档地址：https://blog.csdn.net/m0_46188681/article/details/106797908 -->
        <ul id="commentList" class="media-list">
            <!-- 回复评论 -->

            <!-- foreach 循环开始，值：value -->
            {{~it:value:index}} 

                <!-- 这个用于定位的id，编号唯一，是评论ID -->
                <li id="commentItem{{=value.id}}" class="media comment-item">

                    <!-- 头像 -->
                    <div class="media-left">

                        <!-- 点击头像后跳转到发表者个人主页，这是个个人ID -->
                        <a href="{{=value.baseUrl}}/people.jsp?i={{=value.authorId}}" target="_blank">
                            
                            <!-- 头像路径 -->
                            <img src="{{=value.baseUrl}}/img/avatar/{{=value.avatar}}.png" class="media-object img-thumbnail" alt="头像" data-holder-rendered="true" style="width: 40px; height: 40px;">
                        
                        </a>

                    </div>

                    <!-- 评论内容 -->
                    <div class="media-body">

                        <!-- 评论者昵称 -->
                        <h4 class="media-heading">
                            <!-- 点击发表者昵称后跳转到发表者个人主页，这是个个人ID -->
                            <a href="{{=value.baseUrl}}/people.jsp?i={{=value.authorId}}" target="_blank">
                                {{=value.authorNick}}
                            </a>
                        </h4>

                        <!-- 评论正文 -->
                        <p class="commentContent">

                            {{? value.replay }} <!-- 判断循环对象中有没有replay -->
                            	<div>回复<a onclick="skipComment('#commentItem{{=value.replay.id}}')" class="skip-a"  >@{{=value.replay.nick}}的评论</a>：</div>
                            	<div class="well text-muted" style="padding:6px;margin:5px;">{{=value.replay.content?value.replay.content:"无法显示"}}</div>
                      		{{?}} <!-- 判断结束 -->

                            <!--评论评论内容-->
                            {{=value.content}}

                        </p>

                        <!-- 日期时间 -->
                        <p class="text-muted">
                            {{=value.datetime}}
                        </p>

                    </div>

                    <!-- 评论操作按钮 -->
                    <div class="media-right">

                        <!-- 回复评论按钮，回复的是本条评论，所以是本评论的ID和本评论者的昵称 -->
                        <button onclick="replayComment({{=value.id}},'{{=value.authorNick}}')"  type="button" style="margin-bottom: 5px;" class="btn btn-default">回复</button>
                        
                        <!-- 删除评论按钮，在这里只有作者本人或管理员能删除，传入的是本评论的ID -->
                        <%if(isAdmin){%>

							<button onclick="deleteComment({{=value.id}})" type="button" class="btn btn-default">删除</button>
						
						<%}else{%>

						{{? value.authorId == value.userId }} <!-- 判断当前阅读的用户是不是发评论的这个用户 -->
                            <button onclick="deleteComment({{=value.id}})" type="button" class="btn btn-default">删除</button>
                        {{?? value.blogAuthorId == value.userId}}  <!-- 相当于  elese if -->
							<button onclick="deleteComment({{=value.id}})" type="button" class="btn btn-default">删除</button>
						{{?}}   <!-- 相当于if的结束括号 -->
						
						<%}%>

                    </div>

                </li>
            <!--相当于for循环的结束括号   }  -->
            {{~}}	  
        </ul>
    </script>
    <button onclick="replayComment(id,nick)"></button>
</div>
<br>
<div style="text-align: center;">
    <button id="btnLoadComments" type="button" class="btn btn-info">加载更多评论</button>
    <p id="pNotHaveMoreComments" class="lead">没有更多评论</p>
</div>


<!-- 回复评论模态框（Modal） -->
<div class="modal fade" id="replayModal" tabindex="-1" role="dialog" aria-labelledby="replayModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="replayModalLabel">回复评论</h4>
            </div>
            <div class="modal-body">
                <!-- 回复的内容，到时候把内容复制到这里面来 -->
                <div id="replayModalContent">
                    <script type="text/x-dot-temolate" id="replayCommentTemplate">
                        <!-- 参考文档地址：https://blog.csdn.net/m0_46188681/article/details/106797908 -->
                        {{?it}} <!-- 如果有数据就渲染 -->
                            <ul class="media-list">
                                <!-- 回复评论 -->
                                <li class="media comment-item">
                                    <!-- 头像 -->
                                    <div class="media-left">
        
                                        <!-- 点击头像后跳转到发表者个人主页，这是个个人ID -->
                                        <a href="{{=it.baseUrl}}/people.jsp?i={{=it.authorId}}" target="_blank">
                                            
                                            <!-- 头像路径 -->
                                            <img src="{{=it.baseUrl}}/img/avatar/{{=it.avatar}}.png" class="media-object img-thumbnail" alt="头像" data-holder-rendered="true" style="width: 40px; height: 40px;">
                                        
                                        </a>
        
                                    </div>
        
                                    <!-- 评论内容 -->
                                    <div class="media-body">
        
                                        <!-- 评论者昵称 -->
                                        <h4 class="media-heading">
                                            <!-- 点击发表者昵称后跳转到发表者个人主页，这是个个人ID -->
                                            <a href="{{=it.baseUrl}}/people.jsp?i={{=it.authorId}}" target="_blank">
                                                {{=it.authorNick}}
                                            </a>
                                        </h4>
        
                                        <!-- 评论正文 -->
                                        <p  class="commentContent">
        
                                			{{? it.replay }} <!-- 判断循环对象中有没有replay -->
                          						<div>回复<a onclick="skipComment('#commentItem{{=it.replay.id}}')" class="skip-a"  >@{{=it.replay.nick}}的评论</a>：</div>
                                 				<div class="well text-muted" style="padding:6px;margin:5px;">{{=it.replay.content?it.replay.content:"无法显示"}}</div>
                                       		{{?}} <!-- 判断结束 -->
        
                                            <!--评论评论内容-->
                                            {{=it.content}}
        
                                        </p>
        
                                        <!-- 日期时间 -->
                                        <p class="text-muted">
                                            {{=it.datetime}}
                                        </p>
        
                                    </div>
                                </li>
                            </ul>
                        {{?}}
                    </script>
                </div>
                <div>
                    <textarea id="replayCommentInput" class="form-control" rows="3" placeholder="请输入回复内容"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button onclick="replayCommentPost()" type="button" class="btn btn-primary" >回复</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<!-- 删除评论模态框（Modal） -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel"
    aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="deleteModalLabel">确认删除这个评论</h4>
            </div>
            <div id="deleteCommentModalContent" class="modal-body">
                <script type="text/x-dot-temolate" id="deleteCommentTemplate">
                    <!-- 参考文档地址：https://blog.csdn.net/m0_46188681/article/details/106797908 -->
                    {{?it}} <!-- 如果有数据就渲染 -->
                        <ul class="media-list">
                            <!-- 回复评论 -->
                            <li class="media comment-item">
                                <!-- 头像 -->
                                <div class="media-left">
        
                                    <!-- 点击头像后跳转到发表者个人主页，这是个个人ID -->
                                    <a href="{{=it.baseUrl}}/people.jsp?i={{=it.authorId}}" target="_blank">
        
                                        <!-- 头像路径 -->
                                        <img src="{{=it.baseUrl}}/img/avatar/{{=it.avatar}}.png"
                                            class="media-object img-thumbnail" alt="头像" data-holder-rendered="true"
                                            style="width: 40px; height: 40px;">
        
                                    </a>
        
                                </div>
        
                                <!-- 评论内容 -->
                                <div class="media-body">
        
                                    <!-- 评论者昵称 -->
                                    <h4 class="media-heading">
                                        <!-- 点击发表者昵称后跳转到发表者个人主页，这是个个人ID -->
                                        <a href="{{=it.baseUrl}}/people.jsp?i={{=it.authorId}}" target="_blank">
                                            {{=it.authorNick}}
                                        </a>
                                    </h4>
        
                                    <!-- 评论正文 -->
                                    <p  class="commentContent">
        
                                		{{? it.replay }} <!-- 判断循环对象中有没有replay -->
                          					<div>回复<a onclick="skipComment('#commentItem{{=it.replay.id}}')" class="skip-a"  >@{{=it.replay.nick}}的评论</a>：</div>
                                			<div class="well text-muted" style="padding:6px;margin:5px;">{{=it.replay.content?it.replay.content:"无法显示"}}</div>
                                   		{{?}} <!-- 判断结束 -->
        
                                        <!--评论评论内容-->
                                        {{=it.content}}
        
                                    </p>
        
                                    <!-- 日期时间 -->
                                    <p class="text-muted" style="margin-bottom:0px;">
                                        {{=it.datetime}}
                                    </p>
        
                                </div>
                            </li>
                        </ul>
                    {{?}}
                </script>
            </div>
            <div class="modal-footer">
                <button onclick="deleteCommentPost()" type="button" class="btn btn-primary" >确认</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<script src="<%=currentPath %>/lib/doT-1.1.3/doT.js" ></script>

<%@page import="minuhy.xiaoxiang.blog.util.SessionUtil"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserInfoBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.bean.user.UserBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 编辑资料</title>
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_MY_NUMBER); %>
		<style type="text/css">
			td{
				text-align: center;
			}
            .on{
                background-color:darkgrey;
            }
		</style>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		<% 
			Object obj;
			// 尝试获取昵称
			UserInfoBean user = null;
			obj = session.getAttribute(SessionAttributeNameConfig.USER_INFO);
			if(obj instanceof UserBean){
				UserBean userBean = (UserBean)obj;
				user = new UserInfoBean();
				user.getData(userBean.getId());
			}

		%>
        <!-- 内容 -->
        <div class="container">
        	<%
        	if(user!=null){ 
    			
        		// 001 str
    			String avatarIdH = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.PROFILE_AVATAR, String.format("%03d",user.getAvatar()));
    			// str
        		String nick = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.PROFILE_NICK, user.getNick());
    			// str
    			String signature = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.PROFILE_SIGNATURE, user.getSignature());
    			// 1 str
    			String sex = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.PROFILE_SEX, String.valueOf(user.getSex()));
    			// str
    			String hometown = SessionUtil.getAttrStringAndPurge(session, SessionAttributeNameConfig.PROFILE_HOMETOWN,user.getHometown());
    			// str
    			String link = SessionUtil.getAttrStringAndPurge(session,SessionAttributeNameConfig.PROFILE_LINK, user.getLink());
        	
        	%>
            <h1>编辑资料</h1>
            <hr>
            <div class="row text-center">
                <div>
                    <img id="userAvatarImg" src="<%= String.format(currentPath + "/img/avatar/h%s.png", avatarIdH) %>" alt="头像" width="260" height="260" class="img-thumbnail" />
                </div>
                <br/>
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#avatarModal">修改头像</button>
            </div>
            <!-- 资料 -->
            
            <hr>
            <form style="margin-bottom: 80px" method="post" action="<%=currentPath%>/user/edit">
            	<input value="<%=String.format("h%s", avatarIdH) %>" id="avatarInput" type="hidden" name="avatarId" >
                <div class="input-group input-group">
                    <span class="input-group-addon" id="profile1">昵称</span>
                    <input value="<%= nick==null?"":nick %>"  name="nick" type="text" placeholder="请输入你的昵称" class="form-control" aria-describedby="profile1">
                </div>
                <br>
                <div class="input-group input-group">
                    <span class="input-group-addon" id="profile2">签名</span>
                    <input value="<%= signature==null?"":signature %>"  name="signature" type="text" placeholder="请输入你的签名" class="form-control" aria-describedby="profile2">
                </div>
                <br>
                <div class="input-group input-group">
                    <span class="input-group-addon" id="profile3">性别</span>
                    <select name="sex" class="form-control">
                        <option value="0" <%= (!sex.equals("1")&&!sex.equals("2"))?"selected":"" %>>保密</option>
                        <option value="1" <%= sex.equals("1")?"selected":"" %>>男</option>
                        <option value="2" <%= sex.equals("2")?"selected":"" %>>女</option>
                    </select>
                </div>
                <br>
                <div class="input-group input-group">
                    <span class="input-group-addon" id="profile4">家乡</span>
                    <input value="<%= hometown==null?"":hometown %>"  name="hometown"  type="text" placeholder="请输入你的家乡" class="form-control" aria-describedby="profile4">
                </div>
                <br>
                <div class="input-group input-group">
                    <span class="input-group-addon" id="profile4">联系方式</span>
                    <input  value="<%=link==null?"":link %>"  name="link"  type="text" placeholder="请输入你公开的联系方式" class="form-control" aria-describedby="profile4">
                </div>
                <br>
                <div class="text-right">
                    <button type="submit" class="btn btn-primary">保存资料</button>
                </div>

            </form>
			<%}else{ //if(user!=null)  %>
				<p class="lead" style="text-align: center;padding: 20%;">无修改项<br>请检查登录状态</p>
            <% }%>
        </div>

        <!-- 修改头像模态框 -->
        <div class="modal fade" id="avatarModal" tabindex="-1" role="dialog" aria-labelledby="avatarModalLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="avatarModalLabel">请选择你的头像</h4>
                    </div>
                    <div class="modal-body">
                        <table class="table table-bordered">
                            <tbody>
                                <% 
                                // 动态生成头像数据
                                int total = 138;
                                int row = 6;
                                for(int i=0;i<total/row;i++){
                                	out.write("<tr>");
                                	for(int j=0;j<row;j++){
                                		int imgId = i*6+j+1;
                                		out.write(String.format("<td id='h%03d'><img  width='70'  height='70' src='%s/img/avatar/h%03d.png' alt='预选头像%03d'></td>",imgId, currentPath,imgId,imgId));
                                	}
                                	out.write("</tr>");
                                }
                                
                                %>
                            </tbody>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button id="saveAvatar" type="button" class="btn btn-primary" data-dismiss="modal">确定</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
		<script>
            var chooseAvatar = '';

            $("#avatarModal td").click(function(){ //给每个tr 绑定点击事件  主要锁定每个tr
                var trs = $(this).parent().parent().find('td'); //获取所有tr 
                if(trs.hasClass('on')){ //判断这些tr 有没有Class ‘on'’
                    trs.removeClass('on');//把class on 移除 
                }             
                $(this).addClass('on');//点击的tr 添加 on class 用于改变样式
                window.chooseAvatar = $(this).attr('id');
                
                console.log('选择的头像：' + window.chooseAvatar);
            });
            $("#saveAvatar").click(function(){ 
                console.log('保存的头像：' + window.chooseAvatar);
                $("#avatarInput").attr("value",window.chooseAvatar);
                $("#userAvatarImg").attr("src","<%=currentPath + "/img/avatar/"%>"+window.chooseAvatar+".png");
            });
        </script>
	</body>
</html>
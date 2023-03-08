<%@page import="minuhy.xiaoxiang.blog.bean.blog.BlogBean"%>
<%@page import="minuhy.xiaoxiang.blog.config.RequestAttributeNameConfig"%>
<%@page import="minuhy.xiaoxiang.blog.util.RequestUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>

<!DOCTYPE html>
<html lang="zh-cmn-Hans-CN">
	<head>
		<jsp:include page="partial/common/head.jsp"></jsp:include>
		<title>潇湘博客 - 写博文</title>
		
        <link href="<%=currentPath %>/lib/summernote-v0.8.20/summernote.css" rel="stylesheet">
        <style>
            .summernote{
                width: 100%;
            }
            .redtip{
                border: 3px red solid;
                border-radius: 8px;
            }
            .tipborder{
                border: 3px #fff solid;
                border-radius: 8px;
            }
        </style>
		
		<% // 设置导航栏
		request.setAttribute(
			RequestAttributeNameConfig.NAVIGATION_ACTION,
			RequestAttributeNameConfig.NAVIGATION_POST_NUMBER); %>
	</head>
	<body>
		<jsp:include page="partial/common/navigation.jsp"></jsp:include>
		
		
		<%
		boolean isEdit = false;
		// 编辑参数获取
		// ei
		String editBlogIdStr = RequestUtil.getReqParam(request, "ei", "0");
		
		// 尝试把ID转为整型
		int editBlogId = 0;
		try{
			editBlogId = Integer.parseInt(editBlogIdStr);
		}catch(NumberFormatException e){
			editBlogId = 0;
		}

		BlogBean blogBean = null;
		if(editBlogId > 0){
			blogBean = new BlogBean();
			// if(blogBean.getData(editBlogIdStr,false,false)){
			if(blogBean.getData(editBlogIdStr,false)){
				isEdit = true;
			}else{
				response.sendError(404,"找不到了");
			}
		}
		
		%>
		
		
		<!-- 内容 -->
        <div class="container">

            <!-- 博客列表 -->
            <% if(isEdit) {%>
            	<h1>编辑博文</h1>
            <% }else{%>
            	<h1>发表博文</h1>
            <%} %>
            <hr>
            <!-- 正文 -->
            <div class="form-group">
                <label for="blogTitle">博客标题</label>
                <div id="blogTitleArea" class="tipborder">
                    <input value="<%= isEdit?blogBean.getTitle():"" %>" id="blogTitle" type="text" class="form-control" placeholder="请输入博文标题" autofocus>
                </div>
            </div>
            <hr>
            <div class="form-group">
                <label for="summernote">博客正文</label>
                <div id="summernoteArea" class="tipborder">
                    <textarea id="summernote" class="summernote"></textarea>
                </div>
            </div>
            
            
            <div class="text-right">
	        	<button id="buttonPost" type="button" class="btn btn-primary">
		            <%= isEdit?"保存更改":"立即发表" %>
	            </button>
            </div>
        </div>
		
        <%-- 编辑模式 --%>
		<% if(isEdit) {%>
			<div class="hidden" id="blogContent">
				<%= blogBean.getContent() %>
			</div>
        <% }%>
		<jsp:include page="partial/common/foot.jsp"></jsp:include>
		<script src="<%= currentPath %>/lib/summernote-v0.8.20/summernote.js"></script>
        <script src="<%= currentPath %>/lib/summernote-v0.8.20/lang/summernote-zh-CN.js"></script>
        <script>
            $(function() {
                $('#summernote').summernote({
                    height: 400,    //设置高度
                    toolbar: [      //自定义工具栏
                        ['color',['color']],
                        ['style',['style']],
                        ['fontname',['fontname']],
                        ['font',['bold','underline','clear']],
                        ['para',['ul','ol','paragraph', 'height', 'hr']],
                        ['insert',['table','link','picture']],
                        ['option',['undo','redo']], //撤销 取消撤销
                        ['view',['fullscreen','codeview','help']]
                    ],
                    placeholder: "请输入博文内容……",
                    disableDragAndDrop: true ,//禁用拖放功能
                    dialogsInBody: true,  //对话框放在编辑框还是Body
                    dialogsFade: true ,//对话框显示效果
                    tabsize : 4,
                    focus: true,
                    lang : 'zh-CN',
                  	//调用图片上传
					callbacks: {
                        onImageUpload: function (files) {
                            sendFile($('#summernote'), files[0]);
                        }
                    }
                });
            });
            
          	//ajax上传图片
            function sendFile(summernote, file) {
                var formData = new FormData();
                formData.append("file", file);
                $.ajax({
                	url: basePath + "/file/up",
                    type: "post",
                    data: formData,
                    processData: false, // 告诉jQuery不要去处理发送的数据
                    contentType: false, // 告诉jQuery不要去设置Content-Type请求头
                    dataType: 'text',
                    success: function(data,status) {
                        console.log("数据: \n" + data + "\n状态: " + status);
                        console.log($.type(data));
                        try {
                            console.log(JSON.stringify(data));
                        } catch (e) {
                            console.log('不是json数据' + e);
                        }

                        data = $.parseJSON(data)

                        if (status == "success") {

                            // 设置消息状态
                            let msgType = 'info';
                            if (data['code'] != undefined) {
                                if (data['code'] == 200) {
                                    msgType = 'success';
                                } else if (data['code'] == 500) {
                                    msgType = 'warning';
                                }
                            }

                            // 提示消息
                            if (data['msg'] != undefined) {
                                Toast(data['msg'], 2000, msgType);
                            }

                            if (data['code'] == 200) {
                                // 上传成功 

                                if(data['data']!=undefined && data['data']['filenames']!=undefined){
                                    let filename = data['data']['filenames'][0];
                                    let url = basePath + "/file/down?f="+filename;
                                    console.log('文件名:'+filename);
                                    console.log(url);
                                     summernote.summernote('insertImage',url,'img');
                                }else{
                                    Toast('数据错误', 2000, 'warning');
                                }
                            }else{
                                 Toast('上传失败', 2000, 'warning');
                                console.log('上传失败')
                            }
                        } 
                     },
                    error: function(data) { 
                        
                        console.log("数据: \n" + data );

                        try { 
                            console.log(JSON.stringify(data));
                        } catch (e) {
                            console.log('不是json数据' + e);
                        }

                        Toast('数据错误', 2000, 'warning');
                    }
                });
            }
          	
          //赋值变量
            var basePath = getContextPath();
            // 获取项目路径
            function getContextPath() {
                var pathName = window.document.location.pathname;
                var projectName = pathName.substring(0, pathName.substr(1).indexOf(
                    '/') + 1);
                return projectName;
            }
        </script>
        <%-- 编辑模式 --%>
        <% if(isEdit) {%>
	        <script type="text/javascript">
	            $(document).ready(function(){
			        var markupStr = $("#blogContent").html();
			        $('#summernote').summernote('code', markupStr);
	            });
	        </script>
        <% }%>
        <script type="text/javascript">
        	function counterStrLength(inputStr) {
            	var totalLength = 0;
        		/* 计算utf-8编码情况下的字符串长度 */
        		for (var i = 0; i < inputStr .length ; i++){
              		if (inputStr .charCodeAt(i) <= parseInt ("0x7F") ){
                    	totalLength += 1;
              		} else if (inputStr .charCodeAt(i) <= parseInt ("0x7FF")){
                    	totalLength += 2;
              		} else if (inputStr .charCodeAt(i) <= parseInt("0xFFFF" )){
                    	totalLength += 3;
              		} else if (inputStr .charCodeAt(i) <= parseInt("0x1FFFFF" )){
                    	totalLength += 4;
              		} else if (inputStr .charCodeAt(i) <= parseInt("0x3FFFFFF" )){
                    	totalLength += 5;
              		} else {
                    	totalLength += 6;
              		}
        		}
           		return totalLength ;
  			}
        </script>
        <script>
            $("#buttonPost").click(function(){
                let titleText = $("#blogTitle").val(); // 拿到标题文本
                if(titleText.length === 0){
                    // 标题为空
                    Toast("请输入标题",2000);
                    $("#blogTitleArea").removeClass("tipborder");
                    $("#blogTitleArea").addClass("redtip");
                    
                    setTimeout(function() { 
                        $("#blogTitleArea").removeClass("redtip");
                        $("#blogTitleArea").addClass("tipborder");
                     }, 2200);
                    return;
                }
                console.log("标题 "+titleText);

                let contentTextarea = $("#summernote");
                if (contentTextarea.summernote('isEmpty')) { 
                    // 正文为空
                    Toast("请输入正文",2000);
                    $("#summernoteArea").removeClass("tipborder");
                    $("#summernoteArea").addClass("redtip");
                    
                    setTimeout(function() { 
                        $("#summernoteArea").removeClass("redtip");
                        $("#summernoteArea").addClass("tipborder");
                     }, 2200);
                    return;
                }

                let contentText = contentTextarea.summernote('code');
                console.log("正文 "+contentText);
                let contentLen = counterStrLength(contentText);
                if(contentLen > 65000){
                	// 正文太长
                    Toast("正文长度太长" + contentLen+"/65000字节",2000);
                    $("#summernoteArea").removeClass("tipborder");
                    $("#summernoteArea").addClass("redtip");
                    
                    setTimeout(function() { 
                        $("#summernoteArea").removeClass("redtip");
                        $("#summernoteArea").addClass("tipborder");
                     }, 2200);
                    return;
                }
                
                $.post("<%= currentPath %>/blog/<%=isEdit?"edit":"post"%>",{
                	id:"<%= isEdit?blogBean.getId():"" %>",
                    title:titleText,
                    content:contentText
                },
                function(data,status){
                	console.log("数据: \n" + data + "\n状态: " + status);
                	if(status == "success"){
                		
                		// 设置状态
                		let msgType = 'info';
                		if(data['code']!=undefined){
                			if(data['code'] == 200){
                				msgType = 'success';
                			}else if(data['code'] == 500){
                				msgType = 'warning';
                			}
                		}
                		
                		// 提示消息
                		if(data['msg']!=undefined){
            				Toast(data['msg'],2000,msgType);
                		}
                		
                		// 判断是否需要跳转
                		if(data['data']!=undefined){
                			if(undefined != data['data']['url']){
                				sikpNewPage(data['data']['url']);
                			}
                		}
                	}
                });
            });
        </script>
	</body>
</html>
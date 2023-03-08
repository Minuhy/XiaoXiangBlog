
var commentPageNumber = 1; // 页数从1开始
var commentTotalPageNumber; // 总页数
var commentCurrentPageNumber; // 当前页数
var commentHasNext; // 还有没有下一页
var replayCommentData; // 待回复数据
var deleteCommentData; // 待删除数据
/*
data 中：
{
    'totalPageNumber': 5,// 总页数
    'currentPageNumber': 5,// 当前页数
    'hasNext': false,// 还有没有下一页
    'data': [
        {数据},
        {数据}
    ]
}

数据：
1.
    {
        id:1, // 评论ID
        userId:20, // 阅读者的id，如果未登录为0
        authorId:20, // 发表者ID
        authorNick:"清蒸玉米子", // 发表者昵称
        baseUrl:'/XiaoXiangBlog', // 基础路径
        avatar:"h002", // 头像文件名（不含后缀）
        content:"评论内容", // 评论内容
        datetime:"2022-6-4 13:24" // 评论发表的时间
    },

2.
    {
        id:2, // 评论ID
        userId:20, // 阅读者的id，如果未登录为0
        authorId:21, // 发表者ID
        authorNick:"清米子", // 发表者昵称
        baseUrl:'/XiaoXiangBlog', // 基础路径
        avatar:"h096", // 头像文件名（不含后缀）
        replay:{ // 被回复的用户信息
            id:1, // 评论ID，点击被回复的标签后跳转到被回复的评论位置
            nick:"清蒸玉米子", // 评论作者昵称
            content:'引用的评论内容' // 
        },
        content:"评论内容", // 评论内容
        datetime:"2022-6-5 13:24" // 评论发表的时间
    },

 */


//从服务器返回的数据,一般是用ajax从服务器获取
var commentData = [];

/************************************************************************************************************************************************** */


//生成模板方法
var tpt = doT.template(document.getElementById("commentListTemplate").innerHTML);
    
//生成模板方法 回复评论相关
var tptReplay = doT.template(document.getElementById("replayCommentTemplate").innerHTML);
//生成模板方法 删除评论相关
var tptDelete = doT.template(document.getElementById("deleteCommentTemplate").innerHTML);

window.onload = function () {
    // 隐藏“没有更多评论”
    document.getElementById('pNotHaveMoreComments').style.display = "none";
    document.getElementById('pNotHaveMoreComments').style.backgroundColor="#fefefe"
    // 加载评论
    document.getElementById("btnLoadComments").onclick=function(){
        loadComment();
    }
    loadComment(true);

    document.getElementById('commentSpan2').innerHTML = curCommentCount;
    document.getElementById('commentSpan1').innerHTML = curCommentCount;
}

/************************************************************************************************************************************************** */

function changeCommentCount(n) {
    curCommentCount = curCommentCount + n;
    document.getElementById('commentSpan2').innerHTML = curCommentCount;
    document.getElementById('commentSpan1').innerHTML = curCommentCount;
}

/************************************************************************************************************************************************** */


function loadComment(first=false){
    let page = commentPageNumber;
    var button = document.getElementById("btnLoadComments");
    button.innerHTML="正在加载中，请稍等";
    button.disabled = true;
    
    $.post(currentPath + "/comment/load", {
        'blogId': blogId,
        'pageNumber': commentPageNumber
    },
    function (data, status) {
        var isCtrlButton = false;
        console.log("数据: \n" + data + "\n状态: " + status);
        
        try {
            console.log(JSON.stringify(data));
        } catch (e) {
            console.log('不是json数据'+e);
        }

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

            // 获取数据
            if (data['data'] != undefined) {

                

                let gotData = data['data'];
                commentTotalPageNumber = gotData['totalPageNumber'];
                commentCurrentPageNumber = gotData['currentPageNumber'];
                commentHasNext = gotData['hasNext'];
                let pageData = gotData['data'];
                if (commentTotalPageNumber == undefined
                    || commentCurrentPageNumber == undefined
                    || commentHasNext == undefined
                    || pageData == undefined) {
                    // 7秒后可再次获取，只在前端控制，后端不限时
                    button.innerHTML = "网络错误";
                    isCtrlButton = true;
                    setTimeout(function () { 
                        var button = document.getElementById("btnLoadComments");
                        button.disabled = false;
                        button.innerHTML = "加载更多评论";
                    }, 6000);
                }

                // 尝试将数据加入到commentData中
                try {

                    for (var i = 0; i < pageData.length; i++) {
                        commentData.push(pageData[i]);
                    }

                    // for (var i in pageData) {
                    //     commentData.push(i);
                    // }
                    //把数据渲染到指定元素中
                    document.getElementById("commentDiv").innerHTML = tpt(commentData);
                    console.debug('渲染完成');

                    commentPageNumber += 1;
                } catch (e) {
                    console.error(e);
                    console.debug('尝试渲染数据时出错');
                }
                console.debug('获取结束');

                if (commentHasNext == false) {
                    // 没有下一页了
                    document.getElementById('btnLoadComments').style.display = "none"; // 隐藏按钮
                    document.getElementById('pNotHaveMoreComments').style.display = "inline"; // 显示没有更多了
                }

            }
        }

        if (!isCtrlButton) {
            setTimeout(function () {
                var button = document.getElementById("btnLoadComments");
                button.disabled = false;
                button.innerHTML = "加载更多评论";
            }, 1000);
        }

        if (first) {
            // 自动跳转到评论区（在评论加载完后）
            if (autoGoComment) {
                skipCommentArea('#commentArea');
            }

            // 加载完后跳转到指定评论位置
            if (window.location.hash != undefined && window.location.hash != '') {
                skipComment(window.location.hash);
            }
        }
    });
}

/************************************************************************************************************************************************** */


// 回复评论
function replayComment(id,nick){
    
    console.log(id);
    console.log(nick);


    for(let i=0;i<commentData.length;i++){
        if(commentData[i].id == id){
            replayCommentData = commentData[i];
        }
    }


    $('#replayModal').modal();

    document.getElementById("replayCommentInput").innerHTML='';

    document.getElementById("replayModalLabel").innerHTML="回复 "+nick+" 的评论";
    //把数据渲染到指定元素中
    document.getElementById("replayModalContent").innerHTML=tptReplay(replayCommentData);


    console.log("回复");
}


function replayCommentPost() {
    /*
    	String blogIdStr = RequestUtil.getReqParam(req, "blogId", "");
		String replyIdStr = RequestUtil.getReqParam(req, "replyId", "");
		String content = RequestUtil.getReqParam(req, "content", "");
    */
    // replayCommentData
    let content = document.getElementById("replayCommentInput").value;
    let replyId = replayCommentData.id;

    if (content == undefined || content.length < 1) {
        console.log('内容：'+content);
        Toast('请输入评论内容', 2000, 'warning');
        return;
    }

    $.post(currentPath + "/comment/reply", {
        'blogId': blogId,
        'content': content,
        'replyId': replyId
    },
        function (data, status) {

            console.log("数据: \n" + data + "\n状态: " + status);

            try {
                console.log(JSON.stringify(data));
            } catch (e) {
                console.log('不是json数据' + e);
            }

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

                // 获取数据
                if (data['data'] != undefined) {

                    let commentId = data['data']['id'];
                    console.log('评论ID：' + commentId);

                    if (commentId != undefined && data['code'] == 200) {
                        // 发表成功了

                        /**
                         * 
                         * jsonObject.put("id", id);
                            jsonObject.put("authorId", userId);
                            jsonObject.put("authorNick", userEntity.getNick());
                            jsonObject.put("avatar", userEntity.getAvatar());
                            jsonObject.put("datetime", 
                         * 
                         */

                        let authorId = data['data']['authorId'];
                        let authorNick = data['data']['authorNick'];
                        let avatar = data['data']['avatar'];
                        let datetime = data['data']['datetime'];

                        // 隐藏模态框
                        $('#replayModal').modal('hide');

                        // 添加数据
                        let newCommentData = {
                            id: commentId, // 评论ID
                            userId: replayCommentData.userId, // 阅读者的id，如果未登录为0
                            authorId: authorId, // 发表者ID
                            authorNick: authorNick, // 发表者昵称
                            baseUrl: replayCommentData.baseUrl, // 基础路径
                            avatar: avatar, // 头像文件名（不含后缀）
                            replay: { // 被回复的用户信息
                                id: replyId, // 评论ID，点击被回复的标签后跳转到被回复的评论位置
                                nick: replayCommentData.authorNick, // 评论作者昵称
                                content: replayCommentData.content // 
                            },
                            content: content, // 评论内容
                            datetime: datetime // 评论发表的时间
                        };

                        // 尝试将数据加入到commentData中
                        commentData.unshift(newCommentData);
                        //把数据渲染到指定元素中
                        document.getElementById("commentDiv").innerHTML = tpt(commentData);
                        console.debug('渲染完成');

                        var skipId = '#commentItem' + commentId;
                        setTimeout(function () { 
                            skipComment(skipId);
                            console.debug('跳转完成');
                        }, 1000);
                        document.getElementById("replayCommentInput").value = ''; // 清空输入框
                        changeCommentCount(1); // 界面评论数量+1
                    } else {
                        console.log('数据不正确');
                    }
                }
            }
        });
}

/************************************************************************************************************************************************** */


// 删除评论
function deleteComment(id){
    console.log(id);


    for (let i = 0; i < commentData.length; i++) {
        if (commentData[i].id == id) {
            deleteCommentData = commentData[i];
        }
    }


    $('#deleteModal').modal();

    //把数据渲染到指定元素中
    document.getElementById("deleteCommentModalContent").innerHTML = tptDelete(deleteCommentData);

    console.log("删除");
}


function deleteCommentPost() {
    // deleteCommentData

    $.post(currentPath + "/comment/delete", {
        'commetId': deleteCommentData.id
    },
    function (data, status) {

        console.log("数据: \n" + data + "\n状态: " + status);

        try {
            console.log(JSON.stringify(data));
        } catch (e) {
            console.log('不是json数据' + e);
        }

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
                // 删除成功

                // 隐藏模态框
                $('#deleteModal').modal('hide');

                for (let i = 0; i < commentData.length; i++){
                    if (commentData[i].id == deleteCommentData.id) {
                        commentData.splice(i, 1); // 第二个参数为删除的次数，设置只删除一次
                        break;
                    }
                }

                //把数据渲染到指定元素中
                document.getElementById("commentDiv").innerHTML = tpt(commentData);
                console.debug('渲染完成');
                deleteCommentData = undefined;
                changeCommentCount(-1);
            }
        }
    });
}

/************************************************************************************************************************************************** */

// 跳转到评论区
function skipCommentArea(eid) { // eid：选择器
    window.eid = eid;
    console.log("跳转到评论区");
    window.scrollTo({
        top: heightToTop(document.querySelector(eid)),
        behavior: 'smooth'
    });
}

// 跳转到指定评论位置
function skipComment(eid){
    console.log(eid);
    console.log("跳转");
    try {
        document.querySelector(eid).style.borderColor = 'rgb(255,0,0)';
        setTimeout(() => {
            let a = 255;
            let ele = document.querySelector(eid);
            // 下面的代码参考文档：https://blog.csdn.net/weixin_42052760/article/details/80231854
            if (a != 0) {
                var speed = 100; // 越大速度消失越慢
                var num = 10;
                var st = setInterval(function () {
                    num--;
                    a = num / 10;
                    ele.style.borderColor = 'rgba(255,0,0,' + a + ')';
                    if (num <= 0) { clearInterval(st); }
                }, speed);
            }
            // document.querySelector(window.eid).style.borderColor="#00000000";
        }, 3000);
        window.scrollTo({
            top: heightToTop(document.querySelector(eid)),
            behavior: 'smooth'
        });
    } catch (e) {
        Toast('评论现在找不着', 2000, 'warning');
    }
}

// 跳转
function heightToTop(ele) {
    // 参考文档地址：https://www.php.cn/div-tutorial-477306.html
    //ele为指定跳转到该位置的DOM节点
    let bridge = ele;
    let root = document.body;
    let height = 0;
    do {
        height += bridge.offsetTop;
        bridge = bridge.offsetParent;
    } while (bridge !== root)

    return height - 30;
}
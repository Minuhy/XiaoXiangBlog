
// 根据结果修改样式
function setViewState(sta,isChangePreState=true){
    // 判断要加还是减
    let likeCountChange = 0;

    if(isChangePreState){
        if(preState == 0 && sta == 1) { // 之前无表态，现在点赞
            likeCountChange = 1;
        }else if(preState == -1 && sta == 1) { // 之前反对，现在点赞
            likeCountChange = 1;
        }else if(preState == 1 && sta == 1) { // 之前点赞，现在点赞
            likeCountChange = 0;
        }else
            
        if(preState == 0 && sta == 0) { // 之前无表态，现在无表态
            likeCountChange = 0;
        }else if(preState == -1 && sta == 0) { // 之前反对，现在无表态
            likeCountChange = 0;
        }else if(preState == 1 && sta == 0) { // 之前点赞，现在无表态
            likeCountChange = -1;
        }else
        
        if(preState == 0 && sta == -1) { // 之前无表态，现在反对
            likeCountChange = 0;
        }else if(preState == -1 && sta == -1) { // 之前反对，现在反对
            likeCountChange = 0;
        }else if(preState == 1 && sta == -1) { // 之前点赞，现在反对
            likeCountChange = -1;
        }
        
        preState = sta; // 重新记录读者表态
    }

    likeCount+=likeCountChange;

    // 修改喜欢数量
    $("#likeSpan1").text(likeCount.toString());
    $("#likeSpan2").text(likeCount.toString());

    let imgS1 = $("#imgS1"); // 支持
    let imgS2 = $("#imgS2"); // 支持
    let buttonS1 = $("#buttonS1"); // 支持
    let buttonS2 = $("#buttonS2"); // 支持
    let textS1 = $("#textS1"); // 支持
    let textS2 = $("#textS2"); // 支持

    let imgU1 = $("#imgU1"); // 不支持
    let imgU2 = $("#imgU2"); // 不支持
    let buttonU1 = $("#buttonU1"); // 不支持
    let buttonU2 = $("#buttonU2"); // 不支持
    let textU1 = $("#textU1"); // 不支持
    let textU2 = $("#textU2"); // 不支持

    // 恢复成默认
    imgS1.attr('src',defStaImgSupport);
    imgS2.attr('src',defStaImgSupport);
    imgU1.attr('src',defStaImgUnsupport);
    imgU2.attr('src',defStaImgUnsupport);

    textS1.text(defStaTextSupport);
    textS2.text(defStaTextSupport);
    textU1.text(defStaTextUnsupport);
    textU2.text(defStaTextUnsupport);

    buttonS1.removeClass(actStaClassSupport);
    buttonS1.removeClass(actStaClassUnsupport);                
    buttonS1.addClass(defStaClassSupport);


    buttonS2.removeClass(actStaClassSupport);
    buttonS2.removeClass(actStaClassUnsupport);  
    buttonS2.addClass(defStaClassSupport);    

    buttonU1.removeClass(actStaClassSupport);
    buttonU1.removeClass(actStaClassUnsupport);
    buttonU1.addClass(defStaClassUnsupport);

    buttonU2.removeClass(actStaClassSupport);
    buttonU2.removeClass(actStaClassUnsupport);
    buttonU2.addClass(defStaClassUnsupport);


    if(sta > 0){
        // 支持
        imgS1.attr('src',actStaImgSupport);
        imgS2.attr('src',actStaImgSupport);

        textS1.text(actStaTextSupport);
        textS2.text(actStaTextSupport);

        buttonS1.removeClass(defStaClassSupport);
        buttonS2.removeClass(defStaClassSupport);    
        buttonS1.addClass(actStaClassSupport);
        buttonS2.addClass(actStaClassSupport); 
    }else if(sta < 0){
        // 反对
        imgU1.attr('src',actStaImgUnsupport);
        imgU2.attr('src',actStaImgUnsupport);

        textU1.text(actStaTextUnsupport);
        textU2.text(actStaTextUnsupport);

        buttonU1.removeClass(defStaClassUnsupport);
        buttonU2.removeClass(defStaClassUnsupport);    
        buttonU1.addClass(actStaClassUnsupport);
        buttonU2.addClass(actStaClassUnsupport);  
    }
}

function showAttitude(attitude){
    $.post(attitudeUrl,{
            'state':attitude,
            'blogId':blogId
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
            
            
            if(data['data']!=undefined){
                // 判断是否需要跳转
                if(undefined != data['data']['url']){
                    sikpNewPage(data['data']['url']);
                }
                // 判断最终态度的状态
                if(undefined != data['data']['state']){
                    let sta = parseInt(data['data']['state']);
                    setViewState(sta);
                }
            }
        }
    });
}

$(document).ready(function(){

    // 设置读者表态
    setViewState(preState,false);
    console.log("svs");

    $("#buttonS1").click(function(){
        console.log("buttonS1");
        if(preState != 1){
            showAttitude("support");
        }else{
            showAttitude("cancel");
        }
    });
    $("#buttonS2").click(function(){
        console.log("buttonS2");
        if(preState != 1){
            showAttitude("support");
        }else{
            showAttitude("cancel");
        }
    });

    $("#buttonU1").click(function(){
        console.log("buttonU1");
        if(preState != -1){
            showAttitude("unsupport");
        }else{
            showAttitude("cancel");
        }
    });
    $("#buttonU2").click(function(){
        console.log("buttonU2");
        if(preState != -1){
            showAttitude("unsupport");
        }else{
            showAttitude("cancel");
        }
    });
});
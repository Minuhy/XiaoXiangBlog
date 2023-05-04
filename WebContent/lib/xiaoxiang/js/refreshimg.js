var captchaPicRefresh = 0;
var captchaPicSrc = '';
$('#captchaImg').click(function(){
	var captchaPic = $('#captchaImg')
    // 动作触发后执行的代码!!
    console.log('更新验证码~');
    if (captchaPicRefresh === 0) {
        captchaPicSrc = captchaPic.attr('src');
        console.log('图片地址：' + captchaPicSrc);
    }
    captchaPicRefresh += 1;
    captchaPic.attr('src', captchaPicSrc + '?t=' + captchaPicRefresh);
});
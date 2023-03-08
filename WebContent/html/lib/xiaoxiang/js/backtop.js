$(function () {
    var bt = $('#toolBackTop');
    var sw = $(document.body)[0].clientWidth;

    $(window).scroll(function() {
        var st = $(window).scrollTop();
        if(st > 30){
                bt.show();
        }else{
                bt.hide();
        }
    });
});

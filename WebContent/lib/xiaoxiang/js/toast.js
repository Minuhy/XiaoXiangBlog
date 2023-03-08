
function Toast(msg,duration,type='info'){
    console.log(msg);
    duration=isNaN(duration)?3000:duration;
    var m = document.createElement('div');
    m.innerHTML = msg;
    let color = 'rgb(217, 237, 247)';
    if(type != 'info'){
        if(type=='success'){
            color = 'rgb(223, 240, 216)';
        }else if(type == 'warning'){
            color = 'rgb(242, 222, 222)';
        }
    }
    m.style.cssText = "width: 40%;min-width: 150px;opacity: 0.9;height: 30px;color: #000;line-height: 30px;text-align: center;border-radius: 8px;position: fixed;top: 85%;left: 30%;z-index: 999999;background: " + color +";font-size: 16px;";
    document.body.appendChild(m);
    setTimeout(function() {
        var d = 0.5;
        m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
        m.style.opacity = '0';
        setTimeout(function() { document.body.removeChild(m) }, d * 1000);
    }, duration);
}
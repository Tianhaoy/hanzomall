function countdown(obj){
    var that = $(obj);
    var seconds = 60;
    that.attr("disabled", true);
    that.html(seconds+'秒后可以重发验证码');
    let promise = new Promise((resolve, reject) => {
        let setTimer = setInterval(
            () => {
                seconds -= 1;
                // console.info('倒计时:' + seconds);
                that.html(seconds+'秒后可以重发验证码');
                if (seconds <= 0) {
                    that.html('获取邮箱验证码');
                    resolve(setTimer)
                }
            }
            , 1000)
    })
    promise.then((setTimer) => {
        // console.info('清除');
        clearInterval(setTimer);
        that.attr("disabled", false);
    })
}
function countdownPhone(obj){
    var that = $(obj);
    var seconds = 60;
    that.attr("disabled", true);
    that.html(seconds+'秒后可以重发');
    let promise = new Promise((resolve, reject) => {
        let setTimer = setInterval(
            () => {
                seconds -= 1;
                // console.info('倒计时:' + seconds);
                that.html(seconds+'秒后可以重发');
                if (seconds <= 0) {
                    that.html('获取手机验证码');
                    resolve(setTimer)
                }
            }
            , 1000)
    })
    promise.then((setTimer) => {
        // console.info('清除');
        clearInterval(setTimer);
        that.attr("disabled", false);
    })
}
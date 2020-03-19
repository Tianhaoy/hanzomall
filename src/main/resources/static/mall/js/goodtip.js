function goodTip(){
    swal("暂未上线，敬请期待。。。", {
        icon: "info",
    });
}
function infoTip(){
    swal("皓宇QAQ，一名垃圾Java 开发工程师，o2o.babehome.com生活服务平台作者" +
            "以及www.babehome.com站长（现在是404），" +
            "开源技术爱好者，同时在 GitHub 维护参与0个开源项目，" +
            "擅长 Java 后端开发。", {
        icon: "success",
    });
}
function issuesTip(){
    swal("有问题点击下方qq群链接反馈给群主。", {
        icon: "info",
    });
}
function addressTip(){
    swal("没有分公司，装牌面用的。", {
        icon: "info",
    });
}
function putOrderExcel(){
    window.location.href = "/orders/putExcel";
}
function putBillExcel(){
    window.location.href = "/bill/putExcel";
}

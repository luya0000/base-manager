/**
 * Created by luya on 2017/10/19.
 */
/**
 * 右下角提示信息页设置
 *
 * @param response ajax返回值
 * @param app   应用类型：应用，服务，资源分区等
 * @param name  应用名称
 * @param message 提示信息 例：编辑成功；添加失败
 */
function showResult(response, app, name, title) {
    if (response['status'] == 200) {
        var _div = $('<div>');
        _div.load('alert_success.html', function () {
            $(this).find('#title').html(title);
            $(this).find('#message').html(app + " <strong>" + name + "</strong> " + title);
            var child = $(this).children();
            child.fadeTo(3000, 500).slideToggle(1000, function () {
                child.alert('close');
            });
            $('.content').append(child);
        });
    }
    else {
        var _div = $('<div>');
        _div.load('alert_error.html', function () {
            $(this).find('#title').html("失败");
            $(this).find('#message').html(response['message']);
            var child = $(this).children();
            child.fadeTo(3000, 500).slideToggle(1500, function () {
                child.alert('close');
            });
            $('.content').append(child);
        });
    }
}

function dateFormat(datetime) {
    var date = new Date(datetime);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + (date.getHours()) : date.getHours()) + ':';
    var m = (date.getMinutes() < 10 ? '0' + (date.getMinutes()) : date.getMinutes()) + ':';
    var s = (date.getSeconds() < 10 ? '0' + (date.getSeconds()) : date.getSeconds());
    return Y + M + D + h + m + s;
}


// 验证中的正则
// 半角英数字和-
var matchEngNum = new RegExp("^[a-zA-Z0-9]([-a-z0-9A-Z]*[a-z0-9A-Z])?$");
// 密码6-16位验证
var match6to16 = new RegExp("^[a-zA-Z0-9]{6,16}$");
// 密码6-16位验证
var matchemail = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
// 环境变量验证
var matchEnv = new RegExp("^[A-Za-z_][A-Za-z0-9_]*$");
// 镜像名称验证
var matchImage = new RegExp("^(([-a-z0-9_/]*[a-z0-9])?)[:][A-Za-z0-9]?([-._A-Za-z0-9]*[A-Za-z0-9_])?$");
// 浮点数字验证
var matchFloat  = /^\d+\.?\d*$/;
// 正数验证
var matchNumber  = /^\d+$/;
// 验证
var matchIP  =/^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
// 验证
var matchHostLabel = new RegExp("^([A-Za-z0-9][-A-Za-z0-9_.]*)?([A-Za-z0-9])?$");
// 验证ip:port,ip:port
var matchIpArray = /^(((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5]):\d{3,6},)*(((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5]):\d{3,6})$/;
// 验证半角英数字，符号包括-_
var matchHostName = new RegExp("^([A-Za-z0-9][-A-Za-z0-9_]*)?([A-Za-z0-9])?$");




// ie浏览器的脚本兼容性问题
var ua = navigator.userAgent.toLowerCase();
var isIE = ua.indexOf("msie") > -1;
var safariVersion, lodashJsPath = '4.14.0/lodash.min.js';
if (isIE) {
    safariVersion = ua.match(/msie ([\d.]+)/)[1];
}
if (safariVersion <= 8.0) {
    //ie8以下的脚本兼容性:
    // 1.loadash 脚本兼容性
    lodashJsPath = '3.10.1/lodash.custom.min.js';
}

var head = document.getElementsByTagName('head')[0];
var script = document.createElement('script');
script.type = 'text/javascript';
script.src = ctx + '/resources/lodash/' + lodashJsPath;
head.appendChild(script);

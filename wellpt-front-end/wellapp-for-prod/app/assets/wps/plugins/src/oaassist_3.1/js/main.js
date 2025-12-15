 //=======================these scripts must be the begin=======================
 if (typeof(window.wps) == "undefined") {
     window.wps = window;
 }

 var srcs = [
    'js/function/wps.js',
    'js/wpsenum/kso_enum.js',
    'js/wpsenum/wps_enum.js',
    'plugin/vendor/jquery.min.js',
    'plugin/js/base64.js',

    // 基础JS文件
    'js/common.js',
    'js/monitor.js',
    'js/about.js',

    //功能JS文件
    'js/function/clearDoc.js',
    'js/function/closeDoc.js',
    'js/function/exeAction.js',
    'js/function/importDoc.js',
    'js/function/insertDatetime.js',
    // 'js/function/evalFunc.js',
    'js/function/insertPicture.js',
    'js/function/newDoc.js',
    'js/function/openDoc.js',
    'js/function/openScan.js',
    'js/function/pageSetup.js',
    'js/function/printDoc.js',
    'js/function/redHead.js',
    'js/function/setButtonGroup.js',
    'js/function/showFilePath.js',
    'js/function/showRevision.js',
    'js/function/uploadAsPdf.js',
    'js/function/useTemplate.js',
    'js/function/uploadoa.js',
    'js/function/openType.js',
    'js/function/revisionsEditType.js',
    'js/function/saveDoc.js',
    'js/function/setUserName.js',
    'js/function/addComments.js',
    'js/function/importTemplate.js',
    'js/function/onlineEditDoc.js',
    'js/function/writeSign.js',
    'js/function/insertWatermark.js',
    'js/function/createTaskPane.js',

    // 模块JS文件
    'js/module/gaofaDemo.js',
    'js/module/redHead.js',
    'js/module/oastart.js',
    'js/module/insertTable.js',
    'js/module/useTemplate.js',
    'js/module/selectBookmark.js',
    'js/module/selectTemplate.js',
    'js/module/naturalResources2.js',

    // this script must be the last
    'js/ribbon.js'
];

var $head = document.querySelector('head');

window._load_ = function (i) {
    var s = document.createElement("script");
    s.type = "text/javascript";
    s.src = srcs[i];
    $head.appendChild(s);
};

for (var i = 0; i < srcs.length; i++) {
    window._load_(i)
}
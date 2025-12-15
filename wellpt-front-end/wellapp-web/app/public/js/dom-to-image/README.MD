# 使用说明

1. 引入脚本 dom-to-image.min.js

2. 代码调用：

domtoimage.toPng(element).then(function (dataUrl) {
        var a = document.createElement('a');
        a.href = dataUrl;
        a.download = 'xxx.png';
        a.click();
});
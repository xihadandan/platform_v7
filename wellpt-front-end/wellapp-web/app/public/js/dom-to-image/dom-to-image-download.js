
function toPng(element,picName){
    domtoimage.toPng(element).then(function (dataUrl) {
        var a = document.createElement('a');
        a.href = dataUrl;
        a.download = picName||'';
        a.click();
        a.remove();
    })
}
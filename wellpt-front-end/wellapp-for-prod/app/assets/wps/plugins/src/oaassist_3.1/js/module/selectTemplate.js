function OnSelectTemplate() {

    var url = GetUrlPath();
    if (url.length != 0) {
        url = url.concat("/selectTemplate.html");
    } else {
        url = url.concat("./selectTemplate.html");
    }
    wps.ShowDialog(url, "选择红头模板", 600, 500, false);
    //弹出一个对话框，在对话框中显示url中指定的html文件的内容，url为html文件的路径参数

}

/**
 * 获取红头模板
 */
function getRedTemplateLists() {

    var storage = wps.PluginStorage;
    var redHeadPath = storage.getItem("redHeadPath");

    if (redHeadPath == null || redHeadPath == undefined) {
        // redHeadPath = SZGD_URL.redHeadPath;
        redHeadPath=BASE_INTERFACE.allRedHeadPath;
    }

    $.ajax({
        url: redHeadPath,
        async: false,
        method: "post",
        dataType: 'json',
        success: function (res) {
            vuedata.templates = res;
            // alert(JSON.stringify(res));
        },
        error: function (res) {
            alert("获取响应失败" + JSON.stringify(res));
        }

    });


}
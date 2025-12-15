/**
 * 打开指定文档
 * params参数结构
 * params:{
 *   docId: '', 文档ID
 *   uploadPath: '', 保存文档接口
 *   fileName: '' 根据文档id获取服务器文档接口
 * }
 * @param {*} params
 */
function OpenDoc(params) {

    var doc = openFile(params);
    if (!doc) return;
    var prop = doc.CustomDocumentProperties;
    deleteCustom(); //清掉文档之前的CustomDocumentProperties里所保留的参数
    //存储web页面传递的属性
    for (var key in params) {
         if (key === "userName") { //修改当前文档用户名
            wps.EtApplication().UserName = params[key];
            propAddDefault(prop, key, params[key]);
        } else if (key === "openType") { // 打开方式
            // openType(params[key]); // TODO 设置只读模式
        } else if (key === "revisionCtrl") {
            // OnOpenRevisionClickedFromWeb(params[key])
            // OnShowRevisionClickedFromWeb(params[key])
        } else if (["picturesPath", "redHeadsPath", "getPicturePath", "getRedHeadPath",
                "picHeight", "picWidth", "originWidthHeight", "validatePath", "searchPicturePath",
                "searchRedHeadPath", "templatePath", "strBookmarkDataPath","getTemplatePath"].indexOf(key) !== -1) {
            wps.PluginStorage.setItem(key, params[key])
        } else {
            propAddDefault(prop, key, params[key]);
        }
    }
    var ribbonExt = getRibbonExt3(params["buttonGroups"]);
    propAddDefault(prop, "ribbonExt", JSON.stringify(ribbonExt));
    propAddDefault(prop, "isOA", "true");
    //更新后台编辑状态
    // UpdateEditState(params.docId, "0");

    //激活监听事情

    OnWindowActivate();
}

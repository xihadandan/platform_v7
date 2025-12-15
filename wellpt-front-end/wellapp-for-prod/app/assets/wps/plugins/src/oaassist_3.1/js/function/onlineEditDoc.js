/**
 * 文档不落地打开指定文档
 * params参数结构
 * params:{
 *   docId: '', 文档ID
 *   uploadPath: '', 保存文档接口
 *   fileName: '' 根据文档id获取服务器文档接口
 *   buttonGroups:''按钮组合
 *   userName:''用户名
 * }
 * @param {*} params
 */
function onlineEditDoc(params) {
    var doc = OnlineEditOpenFile(params.fileName);
    if (!doc) return;
    // var prop = doc.CustomDocumentProperties;
    deleteCustom(doc.DocID); //清掉文档之前的CustomDocumentProperties里所保留的参数
    //存储web页面传递的属性
    var ribbonExt = null;
    for (var key in params) {
        if (key === "buttonGroups") {
            ribbonExt = params[key];
        } else if (key === "userName") { //修改当前文档用户名
            wps.WpsApplication().UserName = params[key];
             propAddStorage(doc.DocID, key, params[key]);
        } else {
             propAddStorage(doc.DocID, key, params[key]);
        }
    }
    //获取插件按钮
    ribbonExt = ribbonExt == null ? getRibbonExt(true) : ribbonExt;
    // wps.ribbon = ribbonExt;
     propAddStorage(doc.DocID, "ribbonExt", JSON.stringify(ribbonExt));
     propAddStorage(doc.DocID, "isOA", "true");
     propAddStorage(doc.DocID, "isOnlineFile", "true");
    //更新后台编辑状态
    // UpdateEditState(params.docId, "0");

    //激活监听事情
    // wps.ApiEvent.AddApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
    // wps.ApiEvent.AddApiEventListener("DocumentBeforeClose", OnDocumentBeforeClose);
    OnWindowActivate();
}

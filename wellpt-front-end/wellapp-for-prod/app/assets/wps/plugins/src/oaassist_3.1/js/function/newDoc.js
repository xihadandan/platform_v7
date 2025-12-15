/**
 * 从OA系统打开一个新文档
 * params参数结构
 * params:{
 *   uploadPath:'',保存文档接口
 *   newFileName：'',新建文档名称
 * }
 * @param {*} params
 */
function NewDoc(params) {
    var doc;
    var fileName = params == null ? null : params.fileName;
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
    activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
    var docName = activeDoc.Name;
    if (activeDoc && (docName.indexOf("文档") != -1 || docName.indexOf("文字文稿1") != -1)) {
        doc = activeDoc;
    } else {
        doc = wpsApp.Documents.Add();
    }
    var myDate = currentTime();
    var username = wpsApp.UserName;
    var newFileName = ''+myDate;
    // 暂时用这个路径   用户 公共  公共文档
    // 如果日后版本更新了，这里也要修改，有获得temp路径的方法   wps.Env.GetTempPath()
    if(wps.Env&&wps.Env.GetTempPath){
        if (params.newFileName) {
            doc.SaveAs2(wps.Env.GetTempPath()+"/" + params.newFileName);
        } else if(doc.FullName.indexOf('C:') == -1) {
            doc.SaveAs2(wps.Env.GetTempPath() +"/"+ newFileName);
        }
    }else{
        if (params.newFileName) {
            doc.SaveAs2("C:/Users/Public/Documents/" + params.newFileName);
        } else if(doc.FullName.indexOf('C:') == -1) {
            doc.SaveAs2("C:/Users/Public/Documents/" + newFileName);
        }
    }
    // var prop = doc.CustomDocumentProperties;
    deleteCustom(doc.DocID);//清掉文档之前的CustomDocumentProperties里所保留的参数
    //存储web页面传递的属性
    var ribbonExt = null;
    for (var key in params) {
        if (key === "buttonGroups") {
            var butArr = params[key];
            // var butArr = ""
            ribbonExt = getRibbonExt3(butArr);
        } else {
             propAddStorage(doc.DocID, key, params[key]);
        }
    }
    //获取插件按钮
    // ribbonExt = ribbonExt == null ? getRibbonExt(true) : ribbonExt;
     propAddStorage(doc.DocID, "ribbonExt", JSON.stringify(ribbonExt));
     propAddStorage(doc.DocID, "isOA", "true");

    OnWindowActivate();
}

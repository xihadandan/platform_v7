/**
 * 上传当前文件
 *  * params参数结构
 * params:{
 *
 * }
 */
function OnUploadOAClicked() {
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
    if(!activeDoc) return;
    var isOA = propGetFromStorage(activeDoc.DocID,"isOA");
    var path = activeDoc.FullName;
    if (isOA && isOA.Value==="true") {
        activeDoc.SaveAs2(path);//如果是OA文档则通过保存监听上传OA文档
    }else{
        wps.OAAssist.UploadFile(path, path,OA_DOOR.uploadURL, "OnSuccess", "OnFail");
    }
}

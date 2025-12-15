/**
 * 清稿(接受所有修订痕迹和移除所有批注)
 * params参数结构
 * params:{
 *   saveOriginFile：//清稿保存类型（可选）
 *   （1，保存清稿前，保存路径为uploadPath；2, 保存清稿后,uploadNewPath；3, 都保存,uploadPath,uploadNewPath；saveOriginFile不传，保存清稿后的uploadPath）
 * }
 * @param {*} params
 */
function OnClearRevDocClicked(params) {
    var doc = wps.WpsApplication().ActiveDocument;
    var isOA;
    if (doc) {
        isOA = propGetFromStorage(doc.DocID,"isOA");
    }
    if (doc && isOA && isOA.Value === "true") {
        if (confirm("正在清稿保存来自oa的公文，请确认清稿保存并提交到oa？")) {

            wps.ApiEvent.RemoveApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
            var uploadPath;

            // var prop = doc.CustomDocumentProperties;

            // if (params && params.saveOriginFile) {
            //      propAddStorage(doc.DocID, "saveOriginFile", params.saveOriginFile)
            // }

            var saveOriginFile = propGetFromStorage(doc.DocID,"saveOriginFile");

            // 保存清稿前的文件
            if (saveOriginFile && saveOriginFile.Value && (saveOriginFile.Value == 3 || saveOriginFile.Value == 1)) {
                var uploadPathObj = propGetFromStorage(doc.DocID,"uploadPath");

                if (uploadPathObj) {
                    uploadPath = uploadPathObj.Value
                }
                if(saveOriginFile.Value == 1){
                    uploadFile(uploadPath,"OnClearSuccess")
                }else {
                    uploadFile(uploadPath)
                }
            }

            //修订
            if (doc.Revisions.Count >= 1) {
                doc.AcceptAllRevisions();
            }
            //批注
            if (doc.Comments.Count >= 1) {
                doc.RemoveDocumentInformation(WdRemoveDocInfoType.wdRDIComments);
            }
            doc.TrackRevisions = false;
            var uploadNewPath;

            var uploadNewPathObj = propGetFromStorage(doc.DocID,"uploadNewPath");
            if ((saveOriginFile && saveOriginFile.Value && (saveOriginFile.Value == 3 || saveOriginFile.Value == 2)) || !saveOriginFile) {
                if (uploadNewPathObj) {
                    uploadNewPath = uploadNewPathObj.Value
                }
                uploadFile(uploadNewPath, "OnClearSuccess")

            }
            //
            // if (!saveOriginFile) {
            //     var uploadPathObj = propGetFromStorage(doc.DocID,"uploadNewPath");
            //
            //     if (uploadPathObj) {
            //         uploadPath = uploadPathObj.Value
            //     }
            //     uploadFile(uploadPath, "OnSuccess")
            // }

            // wps.ApiEvent.AddApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
        }
    } else {
        alert("非OA文档，无法进行该功能操作！");
    }

}
function uploadFile(uploadPath,OnClearSuccess){
    var doc = wps.WpsApplication().ActiveDocument;
    var path = doc.FullName;
    var name = doc.Name;
    doc.SaveAs2(path);
    wps.OAAssist.UploadFile(name, path,uploadPath, "file",OnClearSuccess, "OnFail");
}

function OnClearSuccess(resp) {
    alert("清稿成功！");
    var doc = wps.WpsApplication().ActiveDocument;
    doc.Close(0);
}

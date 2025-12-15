/**
 * 转pdf/uof/uot上传
 */

/**
 * 转换指定类型并上传
 *  * params参数结构
 * params:{
 *   uploadPath:'',保存文档接口
 *   suffix：'',文档后缀
 * }
 */
function OnChangeToPDFClicked(params) {
    var suffix = params.suffix;
    var uploadPath = params.uploadPath;
    var doc = wps.WpsApplication().ActiveDocument;
    if(doc){
        if (uploadPath == null) {
            var uploadPathObj = propGetFromStorage(doc.DocID,"uploadPath");
            if (uploadPathObj) {
                uploadPath = uploadPathObj.Value
            } else {
                uploadPath = OA_DOOR.uploadURL; //如果是oa助手进入，oaupurl为空则给默认值
            }
        }
        handleFileAndUpload(suffix, doc, uploadPath);
        //注：本地测试上传服务器的位置：D:\软件\apache-tomcat\apache-tomcat-oa\wtpwebapps\wps-oa-ssh\uploadFiles
    }else{
        alert("WPS当前没有可操作文档！")
    }


}

/**
 * web端转换指定格式并上传服务器
 * @param {*} suffixArr 文件后缀集合
 * @param {*} uploadPath 服务器上传接口
 */
function ChangeAndUpload(suffixArr, uploadPath) {
    for (var i = 0; i < suffixArr.length; i++) {
        if (suffixArr.length == 1) {
            var suffix = suffixArr[i];
            var doc = wps.WpsApplication().ActiveDocument;
            handleFileAndUpload(suffix, doc, uploadPath);
        } else if (suffixArr.length > 1) {
            if (i < (suffixArr.length - 1)) {
                var suffix = suffixArr[i + 1];
                var doc = wps.WpsApplication().ActiveDocument;
                handleFileAndUpload(suffix, doc, uploadPath);
            } else if (i == suffixArr.length - 1) {
                var suffix = suffixArr[0];
                var doc = wps.WpsApplication().ActiveDocument;
                handleFileAndUpload(suffix, doc, uploadPath);
            }
        }
    }
}

function handleFileAndUpload(suffix, doc, uploadPath) {
    //解除保存监听，避免弹出自定义保存提示框
    wps.ApiEvent.RemoveApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
    switch (suffix.toLocaleLowerCase()) {
        case '.pdf':
            var path = doc.FullName.split(".")[0] + ".pdf";
            doc.ExportAsFixedFormat(path, WdSaveFormat.wdFormatPDF, true);
            var name = doc.Name.split(".")[0] + ".pdf";
            uploadingFile(name, path, uploadPath);
            break;
        case '.uof':
            var path = doc.FullName.split(".")[0] + suffix;
            doc.SaveAs(path, 111);
            var name = doc.Name.split(".")[0] + suffix;
            uploadingFile(name, path, uploadPath);
            break;
        case '.uot':
            var path = doc.FullName.split(".")[0] + suffix;
            doc.ExportAsFixedFormat(path, WdSaveFormat.wdFormatOpenDocumentText, true);
            doc.SaveAs2(path);
            var name = doc.Name.split(".")[0] + suffix;
            uploadingFile(name, path, uploadPath)
            break;
        case '.html':
            doc.SaveAs2(path,WdSaveFormat.wdFormatHTML);
            uploadingFile(doc.Name, path, uploadPath)
            var dir = path.split(".")[0] + ".files"
            var wpsApp = wps.WpsApplication();
            var search = wpsApp.FileSearch;
            search.LookIn = dir
            search.SearchSubFolders = true;
            search.FileName = "*.png"
            if(search.Execute() > 0) {
                var files = search.FoundFiles
                for(var i = 1; i<=files.Count; i++){
                    var filePath = files.Item(1)
                    uploadingFile(filePath.substring(filePath.lastIndexOf("\\") + 1,filePath.length), filePath, uploadPath);
                }
            }
            break;
        default:
            var path = doc.FullName.split(".")[0] + suffix;
            doc.SaveAs2(path);
            var name = doc.Name.split(".")[0] + suffix;
            uploadingFile(name, path, uploadPath);
            break;
    }
}

//开始上传文件
function uploadingFile(name, path, uploadPath){
    var doc = wps.WpsApplication().ActiveDocument;
    var _isOnlineFile = propGetFromStorage(doc.DocID,"isOnlineFile");
    if (_isOnlineFile && _isOnlineFile.Value === "true") {
        //文档不落地上传文件
        // alert("_isOnlineFile")
        doc.SaveAsUrl(name, uploadPath, "file",  "OnChangeSuffixUploadSuccess", "OnChangeSuffixUploadFail");
    }else{
        //文档落地时候上传文件
        wps.OAAssist.UploadFile(name, path, uploadPath, "file", "OnChangeSuffixUploadSuccess", "OnChangeSuffixUploadFail");
    }


}

function OnChangeSuffixUploadSuccess(response) {
    // alert("转换文件上传成功");
    closeWpsIfNoDocument();
}

function OnChangeSuffixUploadFail(response) {
    alert("转换文件上传失败");
    closeWpsIfNoDocument();
}

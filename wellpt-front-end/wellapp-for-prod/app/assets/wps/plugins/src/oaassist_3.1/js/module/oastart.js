wps.oaFiles = [];
/**
 * web页面调用WPS的方法入口
 *  * info参数结构
 * info:[
 *      {
 *       '方法名':'方法参数',需要执行的方法
 *     },
 *     ...
 *   ]
 * @param {*} info
 */
function dispatcher(info) {
    var fucns = info.fucns;
    NotifyToWeb();

    //执行web页面传递的方法
    for (var index = 0; index < fucns.length; index++) {
        var fucn = fucns[index];
        for (var key in fucn) {
            if (key === "OpenDoc") {
                var params = fucn[key];
                //从web端打开oa文档时先判断wps是否已经打开，如果已打开则不做任何操作
                var documents = wps.WpsApplication().Documents;
                var count = documents.Count;
                if (count > 0) {
                    var temp;
                    for (var i = 1; i <= documents.Count; i++) {
                        temp = documents.Item(i);
                        // var prop = temp.CustomDocumentProperties;
                        var _isOA = propGetFromStorage(temp.DocID,"isOA");
                        var _docId = propGetFromStorage(temp.DocID,"docId");
                        if ((_isOA && _isOA.Value === "true") && (_docId && _docId.Value === params.docId)) {
                            alert("文档已打开!");
                            return;
                        }
                    }
                }
            }
            window[key]&&window[key](fucn[key]);
        }
    }

    var doc = wps.WpsApplication().ActiveDocument;
    if (doc) {
        // var prop = doc.CustomDocumentProperties;
        // wps.oaFiles.push(doc.FullName.split(".")[0]);
        //文件关闭标记，若存在，则删除
         propAddStorage(doc.DocID, "isOA", "true");
         propAddStorage(doc.DocID, "showSavePromptFlag", "true");
    }
}

/**
 * 更新编辑状态
 * @param {*} docId 文档id
 * @param {*} state 0-正在编辑中 1-文件保存 2-文件关闭
 */
function UpdateEditState(docId, state) {
    var formData = {
        "docId": docId,
        "state": state
    };
    $.ajax({
        url: URL + '/document/stateMonitor',
        async: false,
        data: formData,
        method: "post",
        dataType: 'json',
        success: function(response) {
            if (response == "success") {
                console.log(response);
            }
        },
        error: function(response) {
            console.log(response);
        }
    });
}

/**
 * 打开wps后通知到后台
 */
function NotifyToWeb() {
    $.ajax({
        url: URL + '/wps/wpsCanOpen',
        async: true,
        method: "post",
        dataType: 'json'
    });
}

//保存文件
function SaveFile(cb) {
    wps.ApiEvent.Cancel = true
    var doc = wps.WpsApplication().ActiveDocument;
    // if(doc.Saved) return
    var myDate = currentTime();
    var username = wps.WpsApplication().UserName;
    var fileName = '' + myDate;
    if (doc.FullName.indexOf('C:') == -1) {
        doc.SaveAs2(fileName);
    } else {
        doc.SaveAs2();
    }

    OnWindowActivate();
    var name = doc.Name;
    var path = doc.FullName;
    var docId = "",
        uploadPath = "",
        suffix = "";
    var docIdObj = propGetFromStorage(doc.DocID,"docId");

    if (docIdObj) {
        docId = docIdObj.Value
    }
    var uploadPathObj = propGetFromStorage(doc.DocID,"uploadPath");
    if (uploadPathObj) {
        uploadPath = uploadPathObj.Value
    }
    var suffixObj = propGetFromStorage(doc.DocID,"suffix");
    if (suffixObj) {
        suffix = suffixObj.Value
    }
    //如果后缀不为空就转换文件并上传
    if (suffix.length > 0) {
        var suffixArr = suffix.split("|");
        ChangeAndUpload(suffixArr, uploadPath);
    } else {
        wps.OAAssist.UploadFile(name, path, uploadPath, "file", cb ? cb : "OnSuccess", "OnFail");
    }
}

//在线编辑保存文件
function SaveOnlineFile() {
    var docId = "";
    var doc = wps.WpsApplication().ActiveDocument;
    var name = doc.Name;
    if (name.indexOf('.') == -1) { //新建文档保存时定义文档名称
        var myDate = currentTime();
        var username = wps.WpsApplication().UserName;
        name = username + '_' + myDate + '.docx';
    }

    var uploadPathObj = propGetFromStorage(doc.DocID,"uploadPath");
    if (uploadPathObj) {
        uploadPath = uploadPathObj.Value;
    }
    doc.SaveAsUrl(name, uploadPath, "file", "OnSuccess", "OnFail");

    //对修改后未保存文件进行先保存后关闭
    var close = propGetFromStorage(doc.DocID,"close");
    var docIdObj = propGetFromStorage(doc.DocID,"docId");
    if (docIdObj) {
        docId = docIdObj.Value
    }
    if (close && close.Value == 'true') {
        doc.Close();
        // UpdateEditState(docId, "2");
    }
    wps.ApiEvent.Cancel = true //防止保存后弹出另存为窗口
}

function OnSuccess(resp) {
    var doc = wps.WpsApplication().ActiveDocument
    var result = handleResultBody(resp);

    // var prop = doc.CustomDocumentProperties;

     propAddStorage(doc.DocID, "docId", result);
    // alert("上传成功")
}

function OnFail(resp) {
    // alert(JSON.stringify(resp))
    // console.log(JSON.stringify(resp))
    // var result = "";
    // result = handleResultBody(resp);
    // var selction = wps.WpsApplication().ActiveDocument.ActiveWindow.Selection;
    // selction.Text = JSON.stringify(resp);
    // alert("上传失败："+JSON.stringify(result));
    alert("上传失败");

}
function OnUploadSuccessAndClose(response) {
    // alert("保存成功，文档即将关闭。");
    wps.WpsApplication().ActiveDocument.Close()
    closeWpsIfNoDocument();
    return true
}

function OnUploadFail(response) {

    // alert("上传失败返回响应：" + JSON.stringify(response));

    alert("上传失败");
    wps.ApiEvent.Cancel = true;
    wps.WpsApplication().ActiveDocument.Saved = false;
}

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
            //alert(key);
            window[key](fucn[key]);
        }
    }

    doc = wps.WppApplication().ActivePresentation;
    if (doc) {
        var prop = doc.CustomDocumentProperties;

        //文件关闭标记，若存在，则删除
        var close = prop.Item("close");
        if (close && close.Value == 'true') {
            close.Delete();
        }
        propAddDefault(prop, "isOA", "true");
    }
}

//保存文件
function SaveFile() {
    var doc = wps.WppApplication().ActivePresentation;
    var myDate = CurentTime();
    var username = wps.WppApplication().UserName;
    var filename = username + '_' + myDate;
    if (doc.FullName.indexOf('C:') == -1) {
        doc.SaveAs(filename);
    } else {
        doc.Save();
    }
    var uploadPathObj = doc.CustomDocumentProperties.Item("uploadPath");
    if (uploadPathObj) {
        uploadPath = uploadPathObj.Value
    }
	// alert("name" + doc.Name);
    // alert("本地路径path" + doc.FullName);
    // alert("上传路径：" + uploadPath);
    wps.OAAssist.UploadFile(doc.Name, doc.FullName, uploadPath, "file", "OnUploadSuccess", "OnUploadFail");
}

//保存文件
function SaveFileAndClose() {
    var doc = wps.WppApplication().ActivePresentation;
    var myDate = CurentTime();
    var username = wps.WppApplication().UserName;
    var filename = username + '_' + myDate;
    if (doc.FullName.indexOf('C:') == -1) {
        doc.SaveAs(filename);
    } else {
        doc.SaveAs(doc.FullName);
    }
    var uploadPathObj = doc.CustomDocumentProperties.Item("uploadPath");
    if (uploadPathObj) {
        uploadPath = uploadPathObj.Value
    }
    wps.OAAssist.UploadFile(doc.Name, doc.FullName, uploadPath, "file", "OnUploadSuccessAndClose", "OnUploadFail");
}

//打开wps后通知到后台
function NotifyToWeb(url) {
    $.ajax({
        url: url,
        async: true,
        method: "post",
        dataType: 'json'
    });
}
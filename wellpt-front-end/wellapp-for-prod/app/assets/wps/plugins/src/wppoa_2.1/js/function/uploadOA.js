function OnUploadOAClicked() {
    var doc = wps.WppApplication().ActivePresentation;
    if (!doc) {
        alert("当前没有打开任何文档");
        return;
    }
    //doc.Save();//这个不知道，点击【保存到OA】按钮，无法将修改内容更新到服务器上，所以下面去保存，提示留空。
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
    wps.OAAssist.UploadFile(doc.Name, doc.FullName, uploadPath, "file", "", "");
}
/**
 * 打开指定文档（ 从页面打开一个OA文档， 不关闭， 再打开另一个OA文档时， 提示是否保存当前文档， 保存以后再关闭当前文档才打开新的文档）
 * params参数结构
 * params:{
 *   docId: '', 文档ID
 *   uploadPath: '', 保存文档接口
 *   fileName: '' 根据文档id获取服务器文档接口
 *   userName:用户名
 * }
 * @param {*} params 
 */
function OpenDoc(params) {
    //alert(params.fileName);
    // 重新处理路径
    //params.domain = "http://172.16.2.73:8080";
    // params.uploadPath = params.domain + params.uploadPath + params.urlParams;
    // params.lockDocUrl = params.domain + SZGD_URL.lockDocUrl + params.urlParams;
    // params.copyUrl = params.domain + SZGD_URL.copyUrl + params.urlParams;
    // params.unLockDocurl = params.domain + SZGD_URL.unLockDocUrl + params.urlParams;

    //var fileName = params.fileName;
	// var fileName = params.domain + SZGD_URL.downLoadFileUrl + params.urlParams;//这里拼接下载的文件全路径和名称
    //alert("下载路径="+fileName);
    var doc = openFile(params.fileName);
    if (!doc) {
        //wps.alert("文件打开失败，请联系管理员");
        return;
    }

    //下载文档
    var path = doc.FullName;//wps.OAAssist.DownloadFile(fileName);

    if (doc) {
        var prop = doc.CustomDocumentProperties;

        //打开文档，给文档加锁
        NotifyToWeb(SZGD_URL.lockDocUrl + params.urlParams);

        //存储web页面传递的属性
        var ribbonExt = null;
        var storage = wps.PluginStorage;
        for (var key in params) {
            if (key === "buttonGroups") {
                ribbonExt = params[key];
            } else if (key === "userName") { //修改当前文档用户名
                wps.WppApplication().UserName = params[key];
                propAddDefault(prop, key, params[key]);
            } else if (key == "uploadPath") { //文件保存路径
                storage.setItem("uploadPath", params[key]);
                propAddDefault(prop, key, params[key]);
            } else {
                propAddDefault(prop, key, params[key]);
            }
        }
        storage.setItem("btnSaveflag", "false");
        //获取插件按钮
        if (params.showButton) {
            ribbonExt = showButton(params.showButton);
            var butObj = prop.Item("ribbonExt");
            if (butObj) {
                butObj.Delete();
            }
            prop.Add("ribbonExt", false, 4, JSON.stringify(ribbonExt));
        }
        if (params.btnFileSaveFlag == 0) {
            btnSaveflag = false; //WPS保存按钮
        }
        if (params.btnFilePrintflag == 0) {
            btnFilePrintflag = false; //WPS打印按钮
        }
        btnSaveAsflag = false; //WPS另存为按钮-强制，因为wps自带的另存为触发的是保存方法
        wps.UpdateRibbon();

        //更新后台编辑状态
        UpdateEditState(params.docId, "0");

        // 默认显示OA助手页签
        // wps.ActivateTab("WPSExtOfficeTab");

        // 自动登录
        // wps.WPSCloudService.LoginWithLoginCode(params.code20, params.codetype);

        //激活监听事情
        wps.ApiEvent.AddApiEventListener("PresentationBeforeSave", OnPresentationBeforeSave);
        wps.ApiEvent.AddApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
        OnWindowActivate();
        doc.Saved = true;
    }
}
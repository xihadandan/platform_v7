// --------------------------  监听事件  ---------------------------

//切换窗口时触发的事件
function OnWindowActivate() {
    wps.UpdateRibbon(); //Ribbon更新
}

//文档关闭时触发的事件
function OnPresentationBeforeClose(doc) {
    var fullName = doc.FullName;
    wps.ApiEvent.Cancel = true;
    var isOA = doc.CustomDocumentProperties.Item("isOA");

    //若不是OA文件，走默认关闭流程
    if (!isOA || isOA.Value === "false") {
		wps.ApiEvent.RemoveApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
        wps.ApiEvent.Cancel = true;
		doc.Close();
        wps.ApiEvent.AddApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
        closeWpsIfNoDocument();
        return

    }

    // 已保存，直接关闭
    if (doc.Saved) {
		wps.ApiEvent.RemoveApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
        doc.Close();
        wps.ApiEvent.AddApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
        closeWpsIfNoDocument();
        wps.FileSystem.Remove(fullName);//清理OA打开正文等的下载缓存文件
        return;
    }

    ///文件修改未保存时点击关闭，关闭时会提示是否保存
    if (confirm("请确认是否保存文件？")) {
		doc.CustomDocumentProperties.Add("close", false, 4, "true");
        doc.Save();
        wps.FileSystem.Remove(fullName);//清理OA打开正文等的下载缓存文件
    } else { //取消保存
        doc.Close(2);
        closeWpsIfNoDocument();
        wps.FileSystem.Remove(fullName);//清理OA打开正文等的下载缓存文件
    }
}

//文档保存时触发的事件
function OnPresentationBeforeSave(doc) {
    //wps.ApiEvent.Cancel = true;
    wps.WppApplication().DisplayAlerts = false;
    var isOA = doc.CustomDocumentProperties.Item("isOA");


    // 如果是拟稿步骤，将操作痕迹进行清除
    var storage = wps.PluginStorage;
    var beginStep = storage.getItem("beginStep");
    if (beginStep == 1) {
        OnClearRevDocClicked();//清稿
    }

    var close = doc.CustomDocumentProperties.Item("close");

    //若不是OA文件，走默认保存流程
    if (!isOA || isOA.Value === "false") {
        if (close && close.Value == 'true') {
            close.Delete();
            doc.Save();
            doc.Close();
        } else {
            doc.Save();
        }
        return;
    }

    wps.ApiEvent.RemoveApiEventListener("PresentationBeforeSave", OnPresentationBeforeSave);
    wps.ApiEvent.RemoveApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
    //关闭时提示是否保存，取消保存，直接关闭
    if (close && close.Value == 'true') {
        close.Delete();
        SaveFileAndClose();
    } else {
        SaveFile();
    }
    wps.ApiEvent.AddApiEventListener("PresentationBeforeSave", OnPresentationBeforeSave);
    wps.ApiEvent.AddApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
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
        success: function (response) {
            if (response == "success") {
                console.log(response);
            }
        },
        error: function (response) {
            console.log(response);
        }
    });
}

//加载时会执行的方法
function OnOATabLoad(nothing) {
    var ribbon = getRibbonExt(false);
    wps.ribbon = ribbon;
    wps.ApiEvent.AddApiEventListener("WindowActivate", OnWindowActivate);
    try{
    	var l_wpsUserName = wps.WpsApplication().UserName;
    	wps.PluginStorage.setItem(constStrEnum.WPSInitUserName, l_wpsUserName); //在OA助手加载前，先保存用户原有的WPS应用用户名称    
    }catch(ex){
    	console.log(ex);
    }
    return true;
}

//获取插件按钮
function getRibbonExt(flag) {
    return {
        "btnSaveFile": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "保存到OA",
            "GetImage": "icon/uploadoa.ico"
        },
        "btnSaveAsLocal": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "另存到本地",
            "GetImage": "icon/DecomposeDoc.ico"
        }
    };
}

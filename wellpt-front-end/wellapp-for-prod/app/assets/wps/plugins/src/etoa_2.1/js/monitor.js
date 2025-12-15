// --------------------------  监听事件  ---------------------------
var currentDocFullName = "";

//切换窗口时触发的事件
function OnWindowActivate() {
    var doc = wps.EtApplication().ActiveWorkbook;
    var activeName = doc.Name;
 
    var isOA = doc.CustomDocumentProperties.Item("isOA");
    var _docId = doc.CustomDocumentProperties.Item("docId");
    var docId = '';
    if (_docId) {
        docId = _docId.Value;
    }
    var ribbon;
    if (!isOA || isOA.Value !== "true") { //如果不是OA文件则打开的时候设置ribbonExt值
        // wpsApp.CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Enable");//非OA文档时候将接收修订和拒绝修订按钮放开

        ribbon = getRibbonExt2(false); //如果不是OA文件则隐藏OA助手菜单按钮
        var prop = doc.CustomDocumentProperties;
        propAddDefault(prop, "ribbonExt", JSON.stringify(ribbon));

    } else {
        //如果是OA文档,告知服务器文档已打开
        setDocStatus(docId, 1);
        // wpsApp.CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable");//OA文档时候将接收修订和拒绝修订按钮隐藏
        // 激活页面必须要页签显示出来，所以做2秒延迟
        // setTimeout(activeTab,2000);
    }
    ribbon = doc.CustomDocumentProperties.Item("ribbonExt");
    if (ribbon) {
        wps.ribbon = ribbon.Value; //更新ribbonExt按钮
    }
}

function getRibbonExt2(flag) {
    return {
        "WPSExtOfficeTab": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
        }
    }
}

//当文件保存前触发的事件
function OnWorkbookBeforeSave(book) {
    wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);


    var isOA = book.CustomDocumentProperties.Item("isOA");
    var etApp = wps.EtApplication();
    if (isOA && isOA.Value === "true") {
        var _bookId = book.CustomDocumentProperties.Item("bookId");
        var bookId = "";
        if (_bookId) {
            bookId = _bookId.Value;
        }

        var close = book.CustomDocumentProperties.Item("close")

        if (close && close.Value == 'true') {
            //UpdateEditState(docId, "2");
            // alert("取消保存  判断是否是关闭  是关闭   begin");


            if (confirm("oa文件未保存，请确认是否保存并提交到oa？")) {
                // alert("保存来自oa的文件  选择保存");
                SaveFile();
            } else { //取消保存
                //     alert("取消保存   " + close);

                // alert("取消保存  判断  是关闭   begin");

                book.Close(XlSaveAction.xlDoNotSaveChanges);
                // alert("取消保存  判断 是关闭   end");
            }

            // alert("取消保存  判断是否是关闭  是关闭   end");

        } else {
            // alert("保存来自oa的文件  选择保存");

            SaveFile();
        }


    }
    wps.ApiEvent.AddApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);

    // window.ApiEvent.AddApiEventListener("WorkbookBeforeSave", OnBookBeforeSave);
    // if(doc.Saved) return
    // var isOA = doc.CustomDocumentProperties.Item("isOA");
    // var _docId = doc.CustomDocumentProperties.Item("docId");
    // var close = doc.CustomDocumentProperties.Item("close");
    // var _isOnlineFile = doc.CustomDocumentProperties.Item("isOnlineFile");
    // var docId = "";
    // if (_docId) {
    //     docId = _docId.Value;
    // }
    // //若不是OA文件，走默认保存流程
    // if (!isOA || isOA.Value === false || isOA.Value === "false") {
    //     if (doc.FullName == "WPS文档1" || doc.FullName == "WPS文档2") {
    //         doc.Close(0);
    //         return;
    //     }
    //     return;
    // }

    // // 增加禁止保存的功能
    // var cannot_save = doc.CustomDocumentProperties.Item("cannot_save")

    // if(cannot_save && cannot_save.Value === "true") {
    //     return
    // }
    // var isClosed = 0;
    // if (docId != null && docId != "") {

    //     $.ajax({
    //         url: OA_DOOR.checkOAClosed + docId,
    //         method: "get",
    //         async: false,
    //         success: function (res) {
    //             isClosed = res
    //         }
    //     });
    // }
    // if (isClosed == 1) {
    //     alert("当前文档所属OA系统已经关闭，禁止再次保存！")
    //     return
    // }
    // // wps.ApiEvent.Cancel = true;
    // wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);
    // if (close && close.Value == 'true') {
    //     close.Delete();
    //     SaveFile("OnUploadSuccessAndClose");
    // }else {
    //     if (confirm("正在保存当前打开的OA公文，请确认保存并提交到OA？")) {
    //         if (_isOnlineFile && _isOnlineFile.Value === "true") {
    //             SaveOnlineFile(); //文档不落地保存文件
    //         } else {
    //             SaveFile();
    //         }
    //         if (docId) {
    //             if (!doc.CustomDocumentProperties) {
    //                 setDocStatus(docId, 4);
    //             } else {
    //                 setDocStatus(docId, 3);
    //             }
    //         }
    //     }
    // }
    // wps.ApiEvent.AddApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);
}

//当文件关闭时触发的事件
function OnWorkbookBeforeClose(book) {
    
    // var prop = doc.CustomDocumentProperties
    // var isOA = prop.Item("isOA");
    // var _docId = prop.Item("docId");
    // var docId = "";
    // if (_docId) {
    //     docId = _docId.Value;
    // }
    // //若不是OA文件，走默认关闭流程
    // if (!isOA || isOA.Value === false||isOA.Value === "false") {
    //     if (doc.FullName == "WPS文档1" || doc.FullName == "WPS文档2") {
    //         doc.Close();
    //         return;
    //     }
    //     return;
    // }
    // prop.Add("close", false, 4, "true")

    // // var floder = doc.FullName.split(doc.Name)[0];
    // wps.ApiEvent.Cancel = true;
    // // wps.EtApplication().DisplayAlerts = 0;
    // var notSave = doc.CustomDocumentProperties.Item("notSave");
    // if(doc.Saved){
    //     doc.Close()
    // }else{
    //     if(notSave && notSave.Value=="true"){
    //         // deleteCustom()
    //         wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeClose", OnWorkbookBeforeClose);
    //         doc.Close(0);
    //         closeWpsIfNoDocument();
    //         return

    //     }else if(notSave && notSave.Value=="false"){
    //         wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeClose", OnWorkbookBeforeClose);
    //         doc.Close(2)
    //         return
    //     }else{
    //         OnShowDialog("closeNotify.html", "WPS 文字", 460, 220);
    //         wps.ApiEvent.AddApiEventListener("WorkbookBeforeClose", OnWorkbookBeforeClose);
    //     }
    // }
   // alert("begin Close")
   var isOA = book.CustomDocumentProperties.Item("isOA");

   if (isOA && isOA.Value === "true") {
       var _bookId = book.CustomDocumentProperties.Item("bookId");
       var bookId = "";
       if (_bookId) {
           bookId = _bookId.Value;
       }

       if (book.Saved) {
           // ("book  is  Saved")
           // 已保存，弹出关闭提示
           book.Close();

           // if (confirm("正在关闭来自oa的公文，请确认是否关闭oa文件？")) {
           //     wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeClose", OnBookBeforeClose)

           //     //UpdateEditState(bookId, "2");
           //     book.Close();

           // } else { //取消关闭
           //     wps.ApiEvent.Cancel = true
           // }
       } else { //修改未保存时点击关闭，关闭时会提示是否保存
           // alert("book is not saved")
           book.CustomDocumentProperties.Add("close", false, 4, "true")
           book.Close(XlSaveAction.xlSaveChanges)

           // if (confirm("正在关闭来自oa的公文，请确认是否关闭oa文件？")) {
           //     wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeClose", OnBookBeforeClose)

           // book.CustomDocumentProperties.Add("close", false, 4, "true")
           // book.Close(XlSaveAction.xlSaveChanges)
           // }else { //取消关闭
           //     wps.ApiEvent.Cancel = true
           // }
           // alert("book.Close(XlSaveAction.xlSaveChanges)");

       }


       wps.ApiEvent.AddApiEventListener("WorkbookBeforeClose", OnWorkbookBeforeClose)

   }
}

function OnWorkbookAfterClose(doc) {
    closeWpsIfNoDocument();
}

//加载时会执行的方法
function OnOATabLoad(nothing) {
    // var ribbon = getRibbonExt(true);
    wps.ApiEvent.AddApiEventListener("WindowActivate", OnWindowActivate);
    wps.ApiEvent.AddApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);
    wps.ApiEvent.AddApiEventListener("WorkbookBeforeClose", OnWorkbookBeforeClose);
    wps.ApiEvent.AddApiEventListener("WorkbookAfterClose", OnWorkbookAfterClose);
    wps.ApiEvent.AddApiEventListener("WorkbookBeforePrint", OnWorkbookBeforePrint);
    try{
    	var l_wpsUserName = wps.WpsApplication().UserName;
    	wps.PluginStorage.setItem(constStrEnum.WPSInitUserName, l_wpsUserName); //在OA助手加载前，先保存用户原有的WPS应用用户名称    
    }catch(ex){
    	console.log(ex);
    }
    return true;
}

// 打印前监听事件
function OnWorkbookBeforePrint(doc){
    // 判断是否OA文件
    var isOA = doc.CustomDocumentProperties.Item("isOA")

    if(isOA && isOA.Value === "true"){
        // 获取文档剩余打印次数
        var count = findCountByDocId(doc.docId);
        if(count <= 0) {
            alert('当前文档打印次数为0，不允许打印！')
            wps.ApiEvent.Cancel = true;
        }
    }
    return
}

var i = 5
function findCountByDocId(docId){
    // TODO 从后台查询文档剩余打印次数
    return i--;
}




//获取插件按钮
function getRibbonExt(flag) {
    return {
        "btnSaveAsFile": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "另存至本地",
            "GetImage": "icon/saveAs.ico"
        },
        "btnImportDoc": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "导入表格",
            "GetImage": "icon/ImportDoc.ico"
        },
        "btnPrintDOC": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "打印公文",
            "GetImage": "icon/printdoc.ico"
        },
        "btnUploadOA": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "上传OA文件",
            "GetImage": "icon/uploadoa.ico",
        },
        "btnCloseDOC":{
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "关闭文档",
            "GetImage": "",
        }
        
    };
}

function setDocStatus(id, status) {
    if (!id || id == '' || !status || status == '') {
        return
    }
    $.ajax({
        url: OA_DOOR.setDocStatus + id + "/" + status,
        method: "get",
        success: function (res) {
            console.log("succes");
        }
    });
}


//获取插件按钮
var _buttonGroups = {
    "btnSaveAsFile": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "另存至本地",
        "GetImage": "icon/saveAs.ico"
    },
    "btnImportDoc": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "导入表格",
        "GetImage": "icon/ImportDoc.ico"
    },
    "btnPrintDOC": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "打印公文",
        "GetImage": "icon/printdoc.ico"
    },
    "btnUploadOA": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "上传OA文件",
        "GetImage": "icon/uploadoa.ico",
    },
    "btnCloseDOC":{
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "关闭文档",
        "GetImage": "",
    }
};

function getRibbonExt3(butStr) {
    if(!butStr || butStr == ""){
        _buttonGroups["WPSExtOfficeTab"] = {
            "OnGetEnabled": true,
            "OnGetVisible": true,
        }
        return _buttonGroups;
    }
    var butArr = butStr.split(",");
    var buttonGroups = {
        "WPSExtOfficeTab": {
            "OnGetEnabled": true,
            "OnGetVisible": true,
        }};
    butArr.forEach(function (but) {
        buttonGroups[but] = _buttonGroups[but]
    });
    return buttonGroups;
}

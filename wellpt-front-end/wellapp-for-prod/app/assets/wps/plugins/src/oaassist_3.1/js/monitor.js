/**
 * WPS加载项自定义的枚举值
 */
var constStrEnum = {
    AllowOADocReOpen: "AllowOADocReOpen",
    AutoSaveToServerTime: "AutoSaveToServerTime",
    bkInsertFile: "bkInsertFile",
    buttonGroups: "buttonGroups",
    CanSaveAs: "CanSaveAs",
    copyUrl: "copyUrl",
    DefaultUploadFieldName: "DefaultUploadFieldName",
    disableBtns: "disableBtns",
    insertFileUrl: "insertFileUrl",
    IsInCurrOADocOpen: "IsInCurrOADocOpen",
    IsInCurrOADocSaveAs: "IsInCurrOADocSaveAs",
    isOA: "isOA",
    notifyUrl: "notifyUrl",
    OADocCanSaveAs: "OADocCanSaveAs",
    OADocLandMode: "OADocLandMode",
    OADocUserSave: "OADocUserSave",
    openType: "openType",
    picPath: "picPath",
    picHeight: "picHeight",
    picWidth: "picWidth",
    redFileElement: "redFileElement",
    revisionCtrl: "revisionCtrl",
    ShowOATabDocActive: "ShowOATabDocActive",
    SourcePath: "SourcePath",
    /**
     * 保存文档到业务系统服务端时，另存一份其他格式到服务端，其他格式支持：.pdf .ofd .uot .uof
     */
    suffix: "suffix",
    templateDataUrl: "templateDataUrl",
    TempTimerID: "TempTimerID",
    /**
     * 文档上传到业务系统的保存地址：服务端接收文件流的地址
     */
    uploadPath: "uploadPath",
    /**
     * 文档上传到服务端后的名称
     */
    uploadFieldName: "uploadFieldName",
    /**
     * 文档上传时的名称，默认取当前活动文档的名称
     */
    uploadFileName: "uploadFileName",
    uploadAppendPath: "uploadAppendPath",
    /**
     * 标志位： 1 在保存到业务系统时再保存一份suffix格式的文档， 需要和suffix参数配合使用
     */
    uploadWithAppendPath: "uploadWithAppendPath",
    userName: "userName",
    oaUserName: "oaUserName",
    WPSInitUserName: "WPSInitUserName",
    taskpaneid: "taskpaneid",
    /**
     * 是否弹出上传前确认和成功后的确认信息：true|弹出，false|不弹出
     */
    Save2OAShowConfirm: "Save2OAShowConfirm",
    /**
     * 修订状态标志位
     */
    RevisionEnableFlag: "RevisionEnableFlag"
};

// --------------------------  监听事件  ---------------------------
var currentDocFullName = "";
//切换窗口时触发的事件
function OnWindowActivate(params) {
	pSetWPSAppUserNameForUpdate();

    var doc = wps.WpsApplication().ActiveDocument;
    var activeName = doc.Name;
    //更新文档oa路径
    if (currentDocFullName != activeName) {
        currentDocFullName = "主页 \> 文件列表\>公文详情 \>" + activeName;
        OnOpenFilePathSave(currentDocFullName); //打开文档时监听
        wps.UpdateRibbon(); //Ribbon更新
    }
    var isOA = propGetFromStorage(doc.DocID,"isOA");
    var _docId = propGetFromStorage(doc.DocID,"docId");
    var docId = '';
    if (_docId) {
        docId = _docId.Value;
    }
    var ribbon;
    if (!isOA || isOA.Value !== "true") { //如果不是OA文件则打开的时候设置ribbonExt值
        // wpsApp.CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Enable");//非OA文档时候将接收修订和拒绝修订按钮放开

        ribbon = getRibbonExt2(false); //如果不是OA文件则隐藏OA助手菜单按钮
        // var prop = doc.CustomDocumentProperties;
         propAddStorage(doc.DocID, "ribbonExt", JSON.stringify(ribbon));

        wps.WpsApplication().UserName = wps.PluginStorage.getItem(constStrEnum.WPSInitUserName);
    } else {
        //如果是OA文档,告知服务器文档已打开
        setDocStatus(docId, 1);
        wps.WpsApplication().ActiveWindow.ActivePane.View.RevisionsMode=1;// 显示标记 -> 使用标记框 -> 以嵌入方式显示所有修订
        wps.WpsApplication().UserName=wps.PluginStorage.getItem(constStrEnum.oaUserName);
        
        // 激活页面必须要页签显示出来，所以做2秒延迟
        // setTimeout(activeTab,200);
		if(params && params.fileName){
            var varPwd = "________________";
			var revisionCtrl = params.revisionCtrl || {};
            if(revisionCtrl.bEnabledRevision){
                // 取消保护
                doc.Unprotect(varPwd);
                params.docId && doc.Unprotect(params.docId); // 兼容旧数据
                // 显示接受和拒绝按钮
                wps.WpsApplication().CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Enable");
            }
            // 显示痕迹
            if(revisionCtrl.bShowRevision){
                doc.ShowRevisions = true;
                if(revisionCtrl.showRevisionsMode === 1){// 设置修订显示为：显示标记的最终状态
                    wps.WpsApplication().ActiveWindow.ActivePane.View.RevisionsView = 0;
                    wps.WpsApplication().ActiveWindow.ActivePane.View.ShowRevisionsAndComments = true;
                }else if(revisionCtrl.showRevisionsMode === 3){// 显示标记的原始状态
                    wps.WpsApplication().ActiveWindow.ActivePane.View.RevisionsView = 1; 
                    wps.WpsApplication().ActiveWindow.ActivePane.View.ShowRevisionsAndComments = true;
                }
            }else {
                doc.ShowRevisions = false;
                if(revisionCtrl.showRevisionsMode === 2){// 最终状态
                    wps.WpsApplication().ActiveWindow.ActivePane.View.RevisionsView = 0;
                    wps.WpsApplication().ActiveWindow.ActivePane.View.ShowRevisionsAndComments = false;
                }else if(revisionCtrl.showRevisionsMode === 4){// 原始状态
                    wps.WpsApplication().ActiveWindow.ActivePane.View.RevisionsView = 1; 
                    wps.WpsApplication().ActiveWindow.ActivePane.View.ShowRevisionsAndComments = false;
                }
            }
            // 启用修订
            if (revisionCtrl.bOpenRevision){
                doc.TrackRevisions = true;
            }else {
                doc.TrackRevisions = false;
            }
            if(revisionCtrl.bAcceptRevision){
                OnAcceptRevisions()
            }
            if(revisionCtrl.bRejectRevision){
                OnRejectRevisions()
            }
            if(false === revisionCtrl.bEnabledRevision) {
                // 保护文档，值允许版本修订
                doc.Protect(0, false, varPwd, false, false);
                // 隐藏接受和拒绝按钮
                wps.WpsApplication().CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable");
            }
		}
    }
    // 切换的时候切换taskPane
    changeTaskPane(doc)
    ribbon = propGetFromStorage(doc.DocID,"ribbonExt");
    if (ribbon) {
        wps.ribbon = ribbon.Value; //更新ribbonExt按钮
    }    
}

function changeTaskPane(doc){
    var docId = doc.DocID;
    var taskPaneID = localStorage.getItem("g_taskPaneID");
    var currrentOpenedTPs = localStorage.getItem('openedTaskpaneIds') ? JSON.parse(localStorage.getItem('openedTaskpaneIds')) : [];
    if (taskPaneID && taskPaneID !== "") {
        var mrTP = wps.GetTaskPane(taskPaneID);
        if (currrentOpenedTPs.indexOf(docId) === -1) {
            closedFlag = true;
            mrTP.Visible = false;
        } else {
            closedFlag = false;
            mrTP.Visible = true;
        }
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
function OnDocumentBeforeSave(doc) {
    // if(doc.Saved) return
    var isOA = propGetFromStorage(doc.DocID,"isOA");
    var _docId = propGetFromStorage(doc.DocID,"docId");
    var close = propGetFromStorage(doc.DocID,"close");
    var _isOnlineFile = propGetFromStorage(doc.DocID,"isOnlineFile");
    var uploadPath = propGetFromStorage(doc.DocID,"uploadPath");

    var docId = "";
    if (_docId) {
        docId = _docId.Value;
    }
    //若不是OA文件，走默认保存流程
    if (!isOA || isOA.Value === false || isOA.Value === "false") {
        if (doc.FullName == "WPS文档1" || doc.FullName == "WPS文档2") {
            doc.Close(0);
            return;
        }
        return;
    }

    if (!uploadPath){
        return
    }
    // 增加禁止保存的功能
    var cannot_save = propGetFromStorage(doc.DocID,"cannot_save")

    if(cannot_save && cannot_save.Value === "true") {
        return
    }
    var isClosed = 0;
    if (docId != null && docId != "") {

        $.ajax({
            url: OA_DOOR.checkOAClosed + docId,
            method: "get",
            async: false,
            success: function (res) {
                isClosed = res
            }
        });
    }
    if (isClosed == 1) {
        alert("当前文档所属OA系统已经关闭，禁止再次保存！")
        return
    }
    wps.ApiEvent.Cancel = true;

    if (close && close.Value == 'true') {
        // close.Delete();
        SaveFile("OnUploadSuccessAndClose");
    }else {
        if (confirm("正在保存当前打开的OA公文，请确认保存并提交到OA？")) {
            if (_isOnlineFile && _isOnlineFile.Value === "true") {
                SaveOnlineFile(); //文档不落地保存文件
            } else {
                SaveFile();
            }
            if (docId) {
                if (!doc.CustomDocumentProperties) {
                    setDocStatus(docId, 4);
                } else {
                    setDocStatus(docId, 3);
                }
            }
        }
    }
}

//当文件关闭时触发的事件
function OnDocumentBeforeClose(doc) {
    var isOA = propGetFromStorage(doc.DocID,"isOA");
    var _docId = propGetFromStorage(doc.DocID,"docId");
    var docId = "";
    if (_docId) {
        docId = _docId.Value;
    }
    //若不是OA文件，走默认关闭流程
    if (!isOA || isOA.Value === false||isOA.Value === "false") {
        if (doc.FullName == "WPS文档1" || doc.FullName == "WPS文档2") {
            doc.Close(0);
            return;
        }
        return;
    }

    // doc.CustomDocumentProperties.Add("close", false, 4, "true")
    var fullName = doc.FullName;
    var uploadPath = propGetFromStorage(doc.DocID,"uploadPath");
    wps.ApiEvent.Cancel = true;
    //启用保护模式时，不保存直接关闭
    if (doc.ProtectionType != -1 || !uploadPath) {
        deleteCustom(doc.DocID);//清掉文档之前的CustomDocumentProperties里所参数
        doc.Close(WdSaveOptions.wdDoNotSaveChanges);
        // wps.FileSystem.RemoveDir(floder);
        wps.FileSystem.Remove(fullName)
        setDocStatus(docId, 4);
        return;
    }
    var notSave = propGetFromStorage(doc.DocID,"notSave");
    if(doc.Saved){
        doc.Close();
        // 发送消息到后台
        setDocStatus(docId, 4);
        wps.FileSystem.Remove(fullName)
    }else{
        if(notSave && notSave.Value=="true"){
            propAddStorage(doc.DocID,"close","true")
            doc.Close(WdSaveOptions.wdDoNotSaveChanges);
            wps.FileSystem.Remove(fullName)
            // 发送消息到后台
            setDocStatus(docId, 4);
            closeWpsIfNoDocument();
        }else if(notSave && notSave.Value=="false"){
            propAddStorage(doc.DocID,"close","true")
            wps.ApiEvent.Cancel = true
            doc.Close(WdSaveOptions.wdSaveChanges)
            setDocStatus(docId, 4);
            wps.FileSystem.Remove(fullName)
            return
        }else{
            OnShowDialog("closeNotify.html", "WPS 文字", 480, 280);
        }
    }


    //判断是否需要显示关闭提示框，作用：防止未保存时候关闭WPS后执行下面文档处于保存状态后再次提示是否关闭
    // var showSavePromptFlag = propGetFromStorage(doc.DocID,"showSavePromptFlag");
    // if(showSavePromptFlag && showSavePromptFlag.Value=="true"){
    //     // 已保存时候，提示选择是否关闭
    //     if (doc.Saved) {
    //         if (confirm("请确认是否关闭文件？")) {
    //             if (docId != "") {
    //                 setDocStatus(docId, 4);
    //             }
    //             deleteCustom();//清掉文档之前的CustomDocumentProperties里所参数
    //             doc.Close();
    //             // wps.FileSystem.RemoveDir(floder)
    //         }else{
    //             //取消关闭
    //             wps.ApiEvent.Cancel = true
    //         }
    //     }else{
    //         if (confirm("请确认是否关闭文件？")) {
    //             //文件修改未保存时点击关闭，关闭时会提示是否保存
    //             if (confirm("当前文件未保存，是否保存文件后再关闭？")) {
    //                 doc.Save();
    //                 setDocStatus(docId, 4);
    //             } else { //取消保存
    //                 deleteCustom();//清掉文档之前的CustomDocumentProperties里所参数
    //                 doc.Close(WdSaveOptions.wdDoNotSaveChanges);
    //                 // wps.FileSystem.RemoveDir(floder);
    //             }
    //         }else{
    //             //取消关闭
    //             wps.ApiEvent.Cancel = true
    //         }
    //     }

    // }else{
    //     if (docId != "") {
    //         setDocStatus(docId, 4);
    //     }
    //     deleteCustom();//清掉文档之前的CustomDocumentProperties里所参数
    //     doc.Close();
    //     // wps.FileSystem.RemoveDir(floder);
    // }
}

function OnDocumentAfterClose(doc) {
    // var isOA = propGetFromStorage(doc.DocID,"isOA");
    // if(!isOA) doc.Close(0);
	pSetWPSAppUserNameForUpdate();
	pSetWPSAppUserName();
    closeWpsIfNoDocument();

    // return false
}


/**
 * 作用：判断文档关闭后，如果系统已经没有打开的文档了，则设置回初始用户名
 */
function pSetWPSAppUserName() {
    //文档全部关闭的情况下，把WPS初始启动的用户名设置回去
    if (wps.WpsApplication().Documents.Count == 1) {
        var l_strUserName = wps.PluginStorage.getItem(constStrEnum.WPSInitUserName);
        wps.WpsApplication().UserName = l_strUserName;
    }
}

/**
 * 更新本地用户信息
 */
function pSetWPSAppUserNameForUpdate() {
    var userName = wps.WpsApplication().UserName;
    if(userName != wps.PluginStorage.getItem(constStrEnum.WPSInitUserName)
        && userName != wps.PluginStorage.getItem(constStrEnum.oaUserName)
    ){
        // 如果当前用户名，不是初始用户名，也不是oa用户名 -> 用户修改了用户名，更新初始用户名
        wps.PluginStorage.setItem(constStrEnum.WPSInitUserName, userName);
    }
}

//加载时会执行的方法
function OnOATabLoad(ribbonUI) {
    // var ribbon = getRibbonExt(true);
    wps.ribbonUI = ribbonUI
    wps.ApiEvent.AddApiEventListener("WindowActivate", OnWindowActivate);
    wps.ApiEvent.AddApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
    wps.ApiEvent.AddApiEventListener("DocumentBeforeClose", OnDocumentBeforeClose);
    wps.ApiEvent.AddApiEventListener("DocumentAfterClose", OnDocumentAfterClose);
    wps.ApiEvent.AddApiEventListener("DocumentBeforePrint", OnDocumentBeforePrint);
    // wps.ApiEvent.AddApiEventListener("DocumentOpen", OnDocumentOpen);
    try{
    	var l_wpsUserName = wps.WpsApplication().UserName;
    	wps.PluginStorage.setItem(constStrEnum.WPSInitUserName, l_wpsUserName); //在OA助手加载前，先保存用户原有的WPS应用用户名称    
    }catch(ex){
    	console.log(ex);
    }
    return true;
}

// 文档打开事件

function OnDocumentOpen(doc){
    wps.ribbonUI.ActivateTab('WPSExtOfficeTab')
}

// 打印前监听事件
function OnDocumentBeforePrint(doc){
    // 判断是否OA文件
    var isOA = propGetFromStorage(doc.DocID,"isOA")

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
        // "btnOpenOA": {
        //     "OnGetEnabled": flag,
        //     "OnGetVisible": flag,
        //     "OnGetLabel": "打开OA",
        //     "GetImage": "icon/oa.ico"
        // },
        "btnSaveAsFile": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "另存至本地",
            "GetImage": "icon/saveAs.ico"
        },
        "btnOpenScan": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "打开扫描仪",
            "GetImage": "icon/openscan.ico"
        },
        "btnImportDoc": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "导入公文",
            "GetImage": "icon/ImportDoc.ico"
        },
        "btnPrintDOC": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "打印公文",
            "GetImage": "icon/printdoc.ico"
        },
        "btnPageSetup": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "页面设置",
            "GetImage": "icon/pagesetup.ico"
        },
        "btnInsertDate": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "插入时间",
            "GetImage": "icon/time.ico"
        },
        "btnInsertPic": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "插入签名",
            "GetImage": "icon/erweima.ico"
        },
        "btnUploadOA": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "上传OA文件",
            "GetImage": "icon/uploadoa.ico",
        },
        "btnChangeToPDF": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "转PDF上传",
            "GetImage": "icon/pdf.ico"
        },
        "btnInsertRedHeader": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "套红头",
            "GetImage": "icon/red.ico"
        },
        "btnChangeToUOT": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "转UOT上传",
            "GetImage": "icon/show.ico"
        },
        "btnChangeToUOF": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "转UOF上传",
            "GetImage": "icon/zhuan.ico"
        },
        "btnOpenRevision": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "",
            "GetImage": "icon/lblOAFileState.png"
        },
        "btnShowRevision": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "",
            "GetImage": "icon/ShowRevision.ico"
        },
        "btnClearRevDoc": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "清稿",
            "GetImage": "icon/clearComments.png"
        },
        "btnFilePath": {
            "OnGetVisible": flag,
            "OnGetLabel": "OA文件信息："
        },
        "btnAboutAssist": {
            "OnGetEnabled": flag,
            "GetImage": "icon/help.ico"
        },
        "btnDocCheck": {
            "OnGetEnabled": flag,
            "GetImage": "icon/btnDocInfo.ico"
        },
        "btnSelectBookmark": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "导入书签",
            "GetImage": "icon/selectBookmarks.png"
        },
        "btnAcceptAllRevisions": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "接受修订",
            "GetImage": "icon/yes.ico"

        },
        "btnRejectAllRevisions": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "拒绝修订",
            "GetImage": "icon/no.ico"

        },
        "btnImportTemplate": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "导入模板",
            "GetImage": "icon/importTemplate.ico"
        },
        "btnComposeType": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "一键排版",
            "GetImage": "icon/DecomposeDoc.ico"
        },
        "btnInsertComment": {
            "OnGetEnabled": flag,
            "OnGetVisible": flag,
            "OnGetLabel": "插入批注",
            "GetImage": "icon/comment.ico"
        },

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
    "btnOpenScan": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "打开扫描仪",
        "GetImage": "icon/openscan.ico"
    },
    "btnImportDoc": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "导入公文",
        "GetImage": "icon/ImportDoc.ico"
    },
    "btnPrintDOC": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "打印公文",
        "GetImage": "icon/printdoc.ico"
    },
    "btnPageSetup": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "页面设置",
        "GetImage": "icon/pagesetup.ico"
    },
    "btnInsertDate": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "插入时间",
        "GetImage": "icon/time.ico"
    },
    "btnInsertPic": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "插入签名",
        "GetImage": "icon/erweima.ico"
    },
    "btnUploadOA": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "上传OA文件",
        "GetImage": "icon/uploadoa.ico",
    },
    "btnChangeToPDF": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "转PDF上传",
        "GetImage": "icon/pdf.ico"
    },
    "btnInsertRedHeader": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "套红头",
        "GetImage": "icon/red.ico"
    },
    "btnChangeToUOT": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "转UOT上传",
        "GetImage": "icon/show.ico"
    },
    "btnChangeToUOF": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "转UOF上传",
        "GetImage": "icon/zhuan.ico"
    },
    "btnChangeToHTML": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "转HTML上传",
        "GetImage": "icon/html.png"
    },
    "btnOpenRevision": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "",
        "GetImage": "icon/lblOAFileState.png"
    },
    "btnShowRevision": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "",
        "GetImage": "icon/ShowRevision.ico"
    },
    "btnClearRevDoc": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "清稿",
        "GetImage": "icon/clearComments.png"
    },
    "btnFilePath": {
        "OnGetVisible": true,
        "OnGetLabel": "OA文件信息："
    },
    "btnAboutAssist": {
        "OnGetEnabled": true,
        "GetImage": "icon/help.ico"
    },
    "btnDocCheck": {
        "OnGetEnabled": true,
        "GetImage": "icon/btnDocInfo.ico"
    },
    "btnSelectBookmark": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "导入书签",
        "GetImage": "icon/selectBookmarks.png"
    },
    "btnAcceptAllRevisions": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "接受修订",
        "GetImage": "icon/yes.ico"

    },
    "btnRejectAllRevisions": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "拒绝修订",
        "GetImage": "icon/no.ico"

    },
    "btnImportTemplate": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "导入模板",
        "GetImage": "icon/importTemplate.ico"
    },
    "btnComposeType": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "一键排版",
        "GetImage": "icon/DecomposeDoc.ico"
    },
    "btnInsertComment": {
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "插入批注",
        "GetImage": "icon/comment.ico"
    },
    "btnInsertTable":{
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "动态插入表格",
        "GetImage": "icon/insertTable.png"
    },
    "btnInsertWatermark":{
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "自定义水印",
        "GetImage": "icon/waterMark.png"
    },
    "btnTaskPane":{
        "OnGetEnabled": true,
        "OnGetVisible": true,
        "OnGetLabel": "taskPane",
        "GetImage": "icon/addTaskPane.png"
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

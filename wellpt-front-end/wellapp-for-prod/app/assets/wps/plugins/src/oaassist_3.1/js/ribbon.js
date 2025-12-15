function OnAction(ctrl) {
    ctrl = changeCtrl(ctrl)
    switch (ctrl) {
        // case "btnOpenOA":
        //     OnOpenOAClicked("http://wpspro.support.wps.cn/oaassist/demo_js.html");
        //     break;
        case "btnOpenScan":
            OnOpenScanBtnClicked();
            break;
        case "btnInsertDate":
            OnInsertDateClicked();
            break;
        case "btnPrintDOC":
            OnPrintDocBtnClicked();
            break;
        case "btnPageSetup":
            OnPageSetupClicked();
            break;
        case "btnImportDoc":
            OnImportDocClicked();
            break;
        case "btnInsertRedHeader":
            var height = 200;
            if (wps.PluginStorage.getItem("searchRedHeadPath")) {
                height = height + 50;
            }
            OnShowDialog("redHead2.html", "OA助手", 400, height);
            // OnShowDialog("redHead.html", "OA助手", 0, 0);
            break;
        case "btnUploadOA":
            OnUploadOAClicked();
            break;
        case "btnSaveAsFile":
            OnSaveToLocalClicked();
            break;
        case "btnChangeToPDF":
            OnChangeToPDFClicked({
                suffix: '.pdf'
            });
            break;
        case "btnChangeToUOT":
            OnChangeToPDFClicked({
                suffix: '.uot'
            });
            break;
        case "btnChangeToUOF":
            OnChangeToPDFClicked({
                suffix: '.uof'
            });
            break;
        case "btnChangeToHTML":
            OnChangeToPDFClicked({
                suffix: '.html'
            });
            break;
        case "btnOpenRevision":
            OnOpenRevisionClicked();
            break;
        case "btnShowRevision":
            OnShowRevisionClicked();
            break;
        case "btnAboutAssist":
            OnAboutAssist();
            break;
        case "btnClearRevDoc":
            OnClearRevDocClicked();
            break;
        case "btnFilePath":
            OnOpenFilePathSave(filePath);
            break;
        case "btnAcceptAllRevisions":
            // OnAcceptAllRevisions();
            OnAcceptRevisions();
            break;
        case "btnRejectAllRevisions":
            // OnRejectAllRevisions();
            OnRejectRevisions();
            break;
        case "btnInsertTable":
            insertTable();
            break;
        case "btnInsertWatermark":
            insertWatermark();
            break;
        case "btnInsertPic":
            var height = 200;
            if (wps.PluginStorage.getItem("validatePath")) {
                height = height + 80;
            }
            if (wps.PluginStorage.getItem("searchPicturePath")) {
                height = height + 50;
            }
            OnShowDialog("insertPicturesFromServer.html", "OA助手", 400, height);

            // OnInsertPicture();
            break;
        case "btnDocCheck":
            OnDocCheck();
            break;
        case "btnComposeType":
            splitCourtFile();
            break;
        case "btnSelectBookmark":
            OnSelectBookmark();
            break;
        case "btnImportTemplate":
            OnImportTemlateFileClicked();
            break;
        case "btnInsertComment":
            OnAddCommentsClicked();
            break;
        case "btnWriteSign":
            // OnShowDialog("writeSign.html", "OA助手", 751,745);
            alert("开发中，敬请期待！");
            break;
        case "btnTaskPane":
            OnCreateTaskPane()
            break;
        default:
            ;
    }

    return true;
}

function GetValue(strId, strFunc, defaultValue) {
    if ("ribbon" in wps) {
        if (!wps.ribbon.hasOwnProperty(strId))
            return defaultValue;
        if (!wps.ribbon[strId].hasOwnProperty(strFunc))
            return defaultValue;
        return wps.ribbon[strId][strFunc];
    }
    return defaultValue;

}

function GetExtValue(strId, strFunc, defaultValue) {
    var doc = wps.WpsApplication().ActiveDocument;
    if (doc) {
        var data = propGetFromStorage(doc.DocID,"ribbonExt");
        if (data) {
            ribbonExt = JSON.parse(data.Value);
            if (!ribbonExt.hasOwnProperty(strId))
                return GetValue(strId, strFunc, defaultValue);
            if (!ribbonExt[strId].hasOwnProperty(strFunc))
                return GetValue(strId, strFunc, defaultValue);
            return ribbonExt[strId][strFunc];
        }
    }
    return GetValue(strId, strFunc, defaultValue);

}

var COMMANDS_BTN = [
    "ReviewTrackChangesMenu",
    "FileSaveAsMenu",
    "FileSaveAs",
    "FileSave",
    "FilePrint",
    "FilePrintMenu",
    "FilePrintPreview",
    "ReviewRejectChangeMenu",
    "ReviewAcceptChangeMenu",
    "ExportToPDF",
    "FileSaveAsPicture",
    "SaveAsPicture",
    "FileMenuSendMail",
    "ExportToOFD"
]

function OnGetEnabled(ctrl) {
    ctrl = changeCtrl(ctrl)
    var doc = wps.WpsApplication().ActiveDocument;
    var isOA, _flag;
    if (doc) {
        isOA = propGetFromStorage(doc.DocID,"isOA");
    }
    if (doc && isOA && (isOA.Value === true || isOA.Value === "true")) {
        if (COMMANDS_BTN.indexOf(ctrl) > -1) {
            // _flag = getBtnFlag(doc, ctrl)
            _flag = true;
        }
    } else {
        _flag = true;
    }
    if (_flag) {
        return _flag;
    } else {
        return GetExtValue(ctrl, "OnGetEnabled", false);
    }
}

function getBtnFlag(doc, strId) {
    var btns = propGetFromStorage(doc.DocID,"disableBtns");
    if (btns && btns.Value ) {
        var btnStr = btns.Value;
        // var btnArr = btnStr.split(",");
        return btnStr.indexOf(strId) === -1
    } else {
        return true
    }

}

function OnGetVisible(ctrl) {
    ctrl = changeCtrl(ctrl)
    return GetExtValue(ctrl, "OnGetVisible", false);
}

function GetImage(ctrl) {
    ctrl = changeCtrl(ctrl)
    return GetExtValue(ctrl, "GetImage", "");
}

function GetSize(ctrl) {
    ctrl = changeCtrl(ctrl)
    return GetExtValue(ctrl, "GetSize", "large");
}

function OnGetLabel(ctrl) {
    ctrl = changeCtrl(ctrl)
    var label = "";
    switch (ctrl) {
        case "btnOpenRevision":
            label = OnGetOpenRevisionLabel();
            break;
        case "btnShowRevision":
            label = OnGetShowRevisionLabel();
            break;
        case "btnFilePath":
            label = OnGetShowFilePath();
            break;
        default:
            break;
    }
    if (label) {
        return label;
    } else {
        return GetExtValue(ctrl, "OnGetLabel", "");
    }
}

function changeCtrl(ctrl){
    return ctrl = typeof(ctrl) === "object" ? ctrl.Id : ctrl
}

function OnAction(ctrl) {
    switch (ctrl.Id) {
        case "btnPrintDOC":
            OnPrintDocBtnClicked();
            break;
        case "btnImportDoc":
            OnImportDocClicked();
            break;
        case "btnUploadOA":
            OnUploadOAClicked();
            break;
        case "btnSaveAsFile":
            OnSaveToLocalClicked();
            break;
        // case "btnCloseDOC":
        //     wps.EtApplication().ActiveWorkbook.Close();
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
    var doc = wps.EtApplication().ActiveWorkbook;
    if (doc) {
        var data = doc.CustomDocumentProperties.Item("ribbonExt");
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
    var doc = wps.EtApplication().ActiveWorkbook;
    var strId = ctrl.Id;
    var isOA, _flag;
    if (doc) {
        isOA = doc.CustomDocumentProperties.Item("isOA");
    }
    if (doc && isOA && (isOA.Value === true || isOA.Value === "true")) {
        if (COMMANDS_BTN.indexOf(strId) > -1) {
            _flag = getBtnFlag(doc, strId)
        }
    } else {
        _flag = true;
    }
    if (_flag) {
        return _flag;
    } else {
        return GetExtValue(strId, "OnGetEnabled", false);
    }
}

function getBtnFlag(doc, strId) {
    var btns = doc.CustomDocumentProperties.Item("disableBtns");
    if (btns) {
        var btnStr = btns.Value;
        // var btnArr = btnStr.split(",");
        return btnStr.indexOf(strId) === -1
    } else {
        return true
    }

}

function OnGetVisible(ctrl) {
    return GetExtValue(ctrl.Id, "OnGetVisible", true);
}

function GetImage(ctrl) {
    return GetExtValue(ctrl.Id, "GetImage", "");
}

function GetSize(ctrl) {
    return GetExtValue(ctrl.Id, "GetSize", "large");
}

function OnGetLabel(ctrl) {
    var label = "";
    if (label) {
        return label;
    } else {
        return GetExtValue(ctrl.Id, "OnGetLabel", "");
    }
}
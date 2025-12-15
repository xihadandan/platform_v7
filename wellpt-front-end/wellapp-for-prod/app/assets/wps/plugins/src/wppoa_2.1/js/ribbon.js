function OnAction(strId) {
    switch (strId) {
        case "btnSaveFile":
            OnUploadOAClicked();
            break;
        case "btnSaveAsLocal":
            OnSaveAsLocalClicked();
            break;
        default:
            ;
    }
    return true;
}

function GetValue(strId, strFunc, defaultValue) {
    if ("ribbon" in window) {
        if (!window.ribbon.hasOwnProperty(strId))
            return defaultValue;
        if (!window.ribbon[strId].hasOwnProperty(strFunc))
            return defaultValue;
        return window.ribbon[strId][strFunc];
    }
    return defaultValue;
}

function GetExtValue(strId, strFunc, defaultValue) {
    var doc = wps.WppApplication().ActivePresentation;
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

function OnGetEnabled(strId) {
    return GetExtValue(strId, "OnGetEnabled", false);
}

function OnGetVisible(strId) {
    return GetExtValue(strId, "OnGetVisible", false);
}

function GetImage(strId) {
    return GetExtValue(strId, "GetImage", "");
}

function GetSize(strId) {
    return GetExtValue(strId, "GetSize", "large");
}

function OnGetLabel(strId) {
    return GetExtValue(strId, "OnGetLabel", "");
}
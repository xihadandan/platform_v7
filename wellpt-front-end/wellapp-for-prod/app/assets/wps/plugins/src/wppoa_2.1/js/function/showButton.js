/**
 * 处理要加载的按钮
 */
function showButton(buttons) {
    var ribbonExt = getRibbonExt(false);
    if (!buttons) {
        return ribbonExt;
    }
    var temp = buttons.split(";");
    var len = temp.length;
    for (var k = 0; k < len; k++) {
        ribbonExt = GetRibbon(ribbonExt, temp[k]);
    }

    return ribbonExt;
}

function GetRibbon(ribbonExt, btnName) {
    switch (btnName) {
        case "btnSaveFile":
            ribbonExt.btnSaveFile.OnGetEnabled = true;
            ribbonExt.btnSaveFile.OnGetVisible = true;
            break;
        case "btnSaveAsLocal":
            ribbonExt.btnSaveAsLocal.OnGetEnabled = true;
            ribbonExt.btnSaveAsLocal.OnGetVisible = true;
            break;
        default:
            ;
    }

    return ribbonExt;
}

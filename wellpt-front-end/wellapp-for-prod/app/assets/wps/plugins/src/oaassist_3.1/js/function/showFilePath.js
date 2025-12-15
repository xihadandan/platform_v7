/**
 * 显示文件路径
 */
var CurrentfileName = "";

function OnGetShowFilePath() {
    return CurrentfileName;
}

/**
 * 更新OA菜单中当前显示的文件名
 * @param fileName
 * @constructor
 */
function OnOpenFilePathSave(fileName) {
    CurrentfileName = fileName;
    wps.UpdateRibbon("btnFilePath");
}
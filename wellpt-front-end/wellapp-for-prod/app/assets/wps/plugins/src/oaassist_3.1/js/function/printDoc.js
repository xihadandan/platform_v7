/**
 * 打印窗口
 * params参数结构
 * params:{
 *   
 * }
 * @param {*} params 
 */
function OnPrintDocBtnClicked(params) {
    var fileName = params == null ? null : params.fileName;
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
        activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
        wpsApp.Dialogs.Item(WdWordDialog.wdDialogFilePrint).Show();
}
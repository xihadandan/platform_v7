/**
 * 打开页面设置窗口
 * params参数结构
 * params:{
 *   
 * }
 * @param {*} info 
 */
function OnPageSetupClicked(params) {
    var fileName = params == null ? null : params.fileName;
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
        activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
        wpsApp.Dialogs.Item(WdWordDialog.wdDialogFilePageSetup).Show();
}
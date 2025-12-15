/**
 * 打开扫描仪
 * params参数结构
 * params:{
 *   
 * }
 * @param {*} params 
 */
function OnOpenScanBtnClicked(params) {
    var fileName = params == null ? null : params.fileName;
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
    var wordbasic = wpsApp.WordBasic;
        activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
        wordbasic.InsertImagerScan();
}
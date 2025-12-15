/**
 * 定制按钮组合
 * params:{
 *   'setButtonGroupFlag':true,定制按钮标记
 *   'buttonGroups':{},所选按钮组合
 * }
 * @param {*} info
 */
function SetButtonGroup(params) {
    var fileName = params == null ? null : params.fileName;
    var activeDoc = wps.WpsApplication().ActiveDocument;
        activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
    var setButtonGroupFlag = params.setButtonGroupFlag;
    var buttonGroups = params.buttonGroups;
        if (setButtonGroupFlag) {
            if (buttonGroups) {
                propAddStorage(activeDoc.DocID,"ribbonExt", JSON.stringify(buttonGroups));
                // propAddDefault(activeDoc.CustomDocumentProperties, "ribbonExt", JSON.stringify(buttonGroups));
            }
        }
        wps.ribbon = JSON.stringify(buttonGroups);
        OnWindowActivate();
}

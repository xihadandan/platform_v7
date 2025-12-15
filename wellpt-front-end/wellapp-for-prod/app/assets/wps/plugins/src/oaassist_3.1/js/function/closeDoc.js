/**
 * 关闭指定文档
 * params参数结构
 * params:{
 *   docId: '', 文档ID
 *   docFileName: '' 文档名称
 * }
 * @param {*} params
 */
function CloseDoc(params) {
    var docFileName = params.docFileName;
    var docId = params.docId;

    var docs = wps.WpsApplication().Documents;
    if (docs.Count > 0) {
        for (var i = 1; i <= docs.Count; i++) {
            var tempDoc = docs.Item(i);
            var tempDocName = tempDoc.Name;
            var oDocId = propGetFromStorage(tempDoc.DocID,"docId");
            if (oDocId) {
                oDocId = oDocId.Value;
            }
            if (docId == oDocId && tempDocName.indexOf(docFileName) != -1) {
                tempDoc.Close();
            }
        }
    }
}

function closeActiveDocument(){
    var doc = wps.WpsApplication().ActiveDocument;
    // propAddDefault(doc.CustomDocumentProperties, "notSave", "false");
    propAddStorage(doc.DocID,"notSave", "false");
    doc.Close(WdSaveOptions.wdSaveChanges)
}

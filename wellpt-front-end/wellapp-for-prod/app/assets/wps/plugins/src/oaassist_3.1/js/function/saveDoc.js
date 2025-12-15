/**
 * 另存至本地
 * @constructor
 */
function OnSaveToLocalClicked() {
    var wpsApp= wps.WpsApplication();
    var doc=wpsApp.ActiveDocument;
    var storage = wps.PluginStorage;
    if(doc){
     var isOA = propGetFromStorage(doc.DocID,"isOA");
     var ksoFileDialog = wpsApp.FileDialog(2);
     ksoFileDialog.InitialFileName = doc.Name;//文档名称
     if(isOA && isOA.Value==="true"){
         // isOA.Delete();//必须先删掉再赋值，不然还是之前的值,会触发保存监听
         propAddStorage(doc.DocID,"isOA","false")
         // doc.CustomDocumentProperties.Add("isOA",false,4,"false");
         // isOA = propGetFromStorage(doc.DocID,"isOA");
         ksoFileDialog.Show();
         ksoFileDialog.Execute();//会触发保存文档的监听函数
         // doc.CustomDocumentProperties.Add("isOA",false,4,"true");
     }else{
         ksoFileDialog.Show();
         ksoFileDialog.Execute();//会触发保存文档的监听函数
     }
     propAddStorage(doc.DocID,"isOA","true")
     // doc.CustomDocumentProperties.Add("isOA",false,4,"true");
    }else{
        alert("WPS当前没有可操作文档！")
    }

}

/**
 * 导入公文
 */
function OnImportDocClicked() {
    var wpsApp = wps.WpsApplication();
    var ActiveDoc = wpsApp.ActiveDocument;
    if(ActiveDoc){
        var selction = ActiveDoc.ActiveWindow.Selection;
        var ksoFileDialog = wpsApp.FileDialog(3);
        ksoFileDialog.Show();
        var filePath = ksoFileDialog.SelectedItems.Item(1);
        selction.InsertFile(filePath);
    }else{
        alert("WPS当前没有可操作文档！")
    }
}
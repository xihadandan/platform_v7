/**
 * 导入表格
 */
function OnImportDocClicked() {
    var wpsApp = wps.EtApplication();
    var ActiveDoc = wpsApp.ActiveWorkbook;
    if(ActiveDoc){
        var ksoFileDialog = wpsApp.FileDialog(3);
        ksoFileDialog.Show();
        var filePath = ksoFileDialog.SelectedItems.Item(1);
        wpsApp.Workbooks.Open(filePath, false, false)
    }else{
        alert("WPS当前没有可操作文档！")
    }
}
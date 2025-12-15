/**
 * 另存至本地
 * @constructor
 */
function OnSaveToLocalClicked() {
    var wpsApp = wps.EtApplication();
    var doc = wpsApp.ActiveWorkbook;
    if (doc) {
        var fd = wpsApp.FileDialog(2);
        fd.Title = "另存为";
        fd.Filters.Clear();
        fd.Filters.Add("Microsoft Excel 文件", "*.xls;*.xlsx;*.xlsm");
        var SaveAsFileName = doc.CustomDocumentProperties.Item("SaveAsFileName");
        if (SaveAsFileName) {
            fd.InitialFileName = SaveAsFileName.Value;
        } else {
            fd.InitialFileName = doc.Name;
        }
        fd.Show();
        if (fd.SelectedItems.Count == 0) {
            return;
        }
        var tmp;
        var isOA = doc.CustomDocumentProperties.Item("isOA");
        if (isOA) {
            tmp = true;
            isOA.Delete();
        }

        wps.ApiEvent.RemoveApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);
        fd.Execute();
        if (tmp) {
            doc.CustomDocumentProperties.Add("isOA", false, 4, "true");
        }
        doc.Saved = true;
        wps.ApiEvent.AddApiEventListener("WorkbookBeforeSave", OnWorkbookBeforeSave);
    }
}
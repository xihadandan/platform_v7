/**
 * 导入公文模板，并替换当前文档全部内容
 * @param templateURL  模板路径
 */
function importTemlateFile(templateURL) {
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;

    if (activeDoc) {
        var selection = wpsApp.ActiveWindow.Selection;
        selection.WholeStory(); //选取全文
        selection.Delete();
        selection.InsertFile(templateURL);

        if (activeDoc.Revisions.Count >= 1) {
            activeDoc.AcceptAllRevisions();
        }
    }

}

/**
 * 点击导入模板按钮触发事件
 * @constructor
 */
function OnImportTemlateFileClicked() {
    var wpsApp = wps.WpsApplication();
    var ActiveDoc = wpsApp.ActiveDocument;
    if(ActiveDoc){
        var url = GetUrlPath();
        if (url.length != 0) {
            url = url.concat("/importTemplate.html");
        } else {
            url = url.concat("./importTemplate.html");
        }
        wps.ShowDialog(url, "选择公文模板", 500, 400, false);
        //弹出一个对话框，在对话框中显示url中指定的html文件的内容，url为html文件的路径参数
    }else{
        alert("WPS当前没有可操作文档！")
    }

}


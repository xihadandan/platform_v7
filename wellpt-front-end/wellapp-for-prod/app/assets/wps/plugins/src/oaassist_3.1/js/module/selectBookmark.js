function setBookmarkPath(params){
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    // var prop = doc.CustomDocumentProperties;
     propAddStorage(doc.DocID, "strBookmarkDataPath", params.strBookmarkDataPath);
}

function OnSelectBookmark() {
    var ActiveDoc = wps.WpsApplication().ActiveDocument;
    if(ActiveDoc){
        var url = GetUrlPath();
        if (url.length != 0) {
            url = url.concat("/selectBookmark.html");
        } else {
            url = url.concat("./selectBookmark.html");
        }
        wps.ShowDialog(url, "自定义书签", 600, 430, false);
        //弹出一个对话框，在对话框中显示url中指定的html文件的内容，url为html文件的路径参数
    }else{
        alert("WPS当前没有可操作文档！")
    }
}

function insertBookmark(sign,strContent) {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    var s = wpsApp.ActiveWindow.Selection;
    var range = s.Range; //获取当前光标位置
    var fields = doc.FormFields;
    fields.Shaded = false; //显示底纹
    var formField = fields.Add(range, WdFieldType.wdFieldFormTextInput);
    formField.Name = sign; //书签名称设置
    formField.Result = strContent; //窗体域的在文档上的显示内容
}

function delBookmark(sign) { //删除书签
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    var bookmarks = doc.Bookmarks;
    if (bookmarks.Exists(sign)) {
        var bookmark = bookmarks.Item(sign);
        bookmark.Range.Text = "";
        bookmark.Delete();
    } else {
        alert("此书签不存在，请重新选择要删除的书签！");
    }
}
/**
 * 插入文字型窗体域
 * @param {*} sign 窗体域标签
 */
function insertFormField(sign) {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    var s = wpsApp.ActiveWindow.Selection;
    var range = s.Range;
    var fields = doc.FormFields;
    fields.Shaded = true; //显示底纹
    var formField = fields.Add(range, WdFieldType.wdFieldFormTextInput);
    formField.Name = sign.bookmarksign; //书签名称设置
    formField.Result = sign.bookmarkname; //窗体域的显示结果设置
}

/**
 * 删除指定窗体域
 * @param {*} sign
 */
function delFormField(sign) {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    var fields = doc.FormFields;
    var flag = false;
    if (fields.Count > 0) {
        for (var i = 1; i <= fields.Count; i++) {
            var formField = fields.Item(i);
            if (formField.Name == sign) {
                flag = true;
                formField.Delete();

            }
        }
        if (flag) {
            $("#showResult").html("书签【" + sign + "】删除成功！");
        }
    }

    if (!flag) {
        alert("此书签不存在，请重新选择要删除的书签！");
    }
}

/**
 * 获取书签数据
 */
function getAllBookmarklists() {
    var storage = wps.PluginStorage;
    var bookmarkPath = storage.getItem("strBookmarkDataPath");

    $.ajax({
        url: bookmarkPath || OA_DOOR.bookmarkPath,
        async: false,
        method: "post",
        dataType: 'json',
        success: function (res) {
            vuedata.bookmarks = res;
        },
        error: function (response) {
            alert("获取响应失败");
            vuedata.bookmarks = {}
        }

    });
}

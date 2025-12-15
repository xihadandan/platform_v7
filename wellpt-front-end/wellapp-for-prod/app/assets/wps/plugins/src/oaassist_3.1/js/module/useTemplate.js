function OnUseTemplate(type) {
    OnShowDialog(type, '套用模板', 350, 350);
}

//套用模板、模板文件和内容都来自OA厂商
// function UseTemplate(info) {
//     //获取模板
//     var templatePath = wps.OAAssist.DownloadFile(info.templateURL);
//     var template = wps.WpsApplication().Documents.Open(templatePath);
//     //获取文档内容
//     var docPath = wps.OAAssist.DownloadFile(info.docURL);

//     var data = {
//         "title": "这是标题被替换的效果，只作为演示用"
//     }
//     var title = template.Bookmarks.Item("title");
//     if (title) {
//         title.Range.Text = data.title;
//     }
//     var bookMark = template.Bookmarks.Item("content");
//     bookMark.Range.Select();
//     bookMark.Range.Text = "";
//     bookMark.Range.InsertFile(docPath)

//     var ribbonExt = getRibbonExt(true);
//     propAddDefault(template.CustomDocumentProperties, "ribbonExt", JSON.stringify(ribbonExt));
// }

function getContent(data, path) {
    var doc = wps.WpsApplication().Documents.Open(path);
    data.forEach(function(it) {
        var bookmark = doc.Bookmarks.Item(it.name);

        if (bookmark && bookmark.length > 0) {
            bookmark.forEach(function(item) {
                item.Range.Text = it.text;
            })
        } else {
            var formField = doc.FormFields.Item(it.name);
            if (formField) {
                formField.Range.Text = it.text;
            } else {
                for (var i = 1, cnt = doc.FormFields.Count; i <= cnt; ++i) {
                    var fd = doc.FormFields.Item(i);
                    if (fd.Range.Text == it.name) {
                        fd.Range.Text = it.text;
                        break;
                    }
                }
            }
        }
    })
}
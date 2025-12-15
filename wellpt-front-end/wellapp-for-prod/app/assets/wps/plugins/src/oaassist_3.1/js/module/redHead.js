function insertRedHeadDocFromFile(strFile, docTemplate) { //插入红头
    var app = wps.WpsApplication()
    var activeDoc = app.ActiveDocument;
    if (!activeDoc)
        return;
    if (!docTemplate) {
        if (activeDoc) {
            var bookMark = activeDoc.Bookmarks.Item(docTemplate.replaceBookMark)
            if (bookMark) {
                bookMark.Range.Select();
                bookMark.Range.Text = "";
                bookMark.Range.InsertFile(strFile)
            } else {
                var selection = activeDoc.ActiveWindow.Selection
                if (selection) {
                    Selection.InsertFile(strFile);
                }
            }
        }
        return;
    }

    if (activeDoc) {
        app.ActiveWindow.Selection.WholeStory(); //选取全文
        var content = activeDoc.Content
        if (content) {
            content.Cut(); //剪切活动文档中的内容
            content.InsertFile(strFile); //向文档中插入红头文件
            if (docTemplate.replaceBookMark.length != 0) {
                var bookMark = activeDoc.Bookmarks.Item(docTemplate.replaceBookMark)
                if (bookMark) {
                    bookMark.Range.Select(); //选择bookMark对象所含的标签
                }
            } else if (docTemplate.replaceText.length != 0) {
                app.ActiveWindow.Selection.HomeKey(6); //6为wdStory,，将所选内容移至文档的开头
                //var r=activeDoc.Range
                var s = app.ActiveWindow.Selection
                if (s) {
                    var f = s.Find;
                    if (f) {
                        f.Wrap = 0;
                        f.Text = docTemplate.replaceText; //设置要查找的文本
                        f.Forward = true; //往前搜索
                        f.MatchCase = false; //相当于“查找和替换”对话框中的“区分大小写”复选框，false为不区分大小写
                        f.MatchByte = true; //搜索过程中不区分全角和半角字母或字符
                        f.MatchWildcards = false; //查找的文字不包含特殊搜索操作符
                        f.MatchWholeWord = false; //相当于“查找和替换”对话框中的“全字匹配”复选框,false为非全字匹配
                        f.MatchFuzzy = false; //执行特定搜索
                        f.Replacement.Text = ""; //将文本替换为""
                        f.Replacement.Style = "";
                        f.Replacement.Highlight = 9999999;
                        f.Style = ""; //返回或设置指定对象的样式
                        f.Highlight = 9999999; //指定文本的突出显示颜色
                        var e;
                        f.Execute(e, e, e, e, e, e, e, e, e, e, 0) //运行指定的查找操作,如果查找成功，则返回 True
                        if (f.Found == true) {
                            s.Paste();
                        } else {
                            s.EndKey(6); //将所选内容移至文档末尾
                            s.Paste();
                        }
                    }
                }
            } else {
                s.EndKey(6);
                s.Paste();
            }
            s.HomeKey(6);
        }
    }
}
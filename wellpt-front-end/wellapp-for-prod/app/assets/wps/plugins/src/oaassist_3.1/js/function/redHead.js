/**
 * 套用模板插入文字/图片/文档
 *  * params参数结构
 * params:{
 *     'docId':'',文档id
 *     'templateURL':'',获取模板接口
 *     'fileName':'',获取文档接口
 *     'uploadPath':'',文档保存上传接口
 *     'replaceBookMark':'',标签
 *     'replaceText':'',内容
 * }
 * @param {*} params
 */
function RedHead(params) { //插入红头
  var docId = params.docId;
  var templateURL = params.templateURL;
  var fileName = params.fileName;
  var uploadPath = params.uploadPath;
  var replaceBookMark = params.replaceBookMark;
  var replaceText = params.replaceText;
  //下载文件
  var path = wps.OAAssist.DownloadFile(fileName);
  //打开文件
  var doc = wps.WpsApplication().Documents.Open(path);
  // var prop = doc.CustomDocumentProperties;
  var ribbonExt = getRibbonExt(true);

  //加载工具栏
  propAddStorage(doc.DocID, "ribbonExt", JSON.stringify(ribbonExt));
  //设置公文id
  propAddStorage(doc.DocID, "docId", docId);
  propAddStorage(doc.DocID, "isOA", "true");
  propAddStorage(doc.DocID, "uploadPath", uploadPath);

  // if (doc) {
  //     wps.oaFiles.push(doc.FullName.split(".")[0]);
  // }

  var app = wps.WpsApplication()
  var activeDoc = app.ActiveDocument;
  if (!activeDoc)
    return;
  //套红头
  // 如果红头模板存在的话，在doc找书签存不存在，存在，替换书签，不存在，模板插到文档开始
  if (templateURL) {
    if (activeDoc) {
      var bookMark = activeDoc.Bookmarks.Item(replaceBookMark)
      if (bookMark) {
        bookMark.Range.Select();
        bookMark.Range.Text = "";
        bookMark.Range.InsertFile(templateURL)
      } else {
        var selection = activeDoc.ActiveWindow.Selection
        if (selection) {
          selection.InsertFile(templateURL);
          activeDoc.SaveAs();
        }
      }
    }

    return;
  }
  if (activeDoc) {
    app.ActiveWindow.Selection.WholeStory(); //选取全文
    var content = activeDoc.Content
    if (content) {
      content.Cut();
      content.InsertFile(templateURL);
      if (replaceBookMark && replaceBookMark.length != 0) {
        var bookMark = activeDoc.Bookmarks.Item(replaceBookMark)
        if (bookMark) {
          bookMark.Range.Select(); //
        }
      } else if (replaceText && replaceText.length != 0) {
        app.ActiveWindow.Selection.HomeKey(6); //6为wdStory
        //var r=activeDoc.Range
        var s = app.ActiveWindow.Selection
        if (s) {
          var f = s.Find;
          if (f) {
            f.Wrap = 0;
            f.Text = replaceText;
            f.Forward = true;
            f.MatchCase = false;
            f.MatchByte = true;
            f.MatchWildcards = false;
            f.MatchWholeWord = false;
            f.MatchFuzzy = false;
            f.Replacement.Text = "";
            f.Replacement.Style = "";
            f.Replacement.Highlight = 9999999;
            f.Style = "";
            f.Highlight = 9999999;
            var e;
            f.Execute(e, e, e, e, e, e, e, e, e, e, 0)
            if (f.Found == true) {
              s.Paste();
            } else {
              s.EndKey(6);
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
  //更新后台编辑状态
  // UpdateEditState(docId, "0")
  OnWindowActivate();
}


/**
 * 从WPS的OA助手菜单上点击套红头(支持多次点击套后按钮，和重复套红头)
 *@param 参数：
 *     'strFile':'',红头模板url
 */
function insertRedHeadDocFromWps(strFile) { //插入红头
  var wpsApp = wps.WpsApplication();
  var activeDoc = wpsApp.ActiveDocument;
  var selection = wpsApp.ActiveWindow.Selection;
  var fields = activeDoc.FormFields;

  // var bookmarks = activeDoc.Bookmarks;
  var readhead = fields.Item("readhead");
  if (!readhead) {
    selection.GoTo(WdGoToItem.wdGoToLine, WdGoToDirection.wdGoToFirst, 1)
    fields.Shaded = true; //显示底纹
    var formField = fields.Add(selection.Range, WdFieldType.wdFieldFormTextInput);
    formField.Name = "readhead"; //书签名称设置
    formField.Result = "红头"; //窗体域的显示结果设置
    // alert("123")
    // bookmarks.Add("readhead",selection.Range)
    readhead = fields.Item("readhead");
    // activeDoc.Protect(WdProtectionType.wdAllowOnlyFormFields, false, "123456", false);
  }

  activeDoc.TrackRevisions = false; //准备以非批注的模式插入红头文件
  readhead.Select();
  readhead.Range.Text = "";
  readhead.Range.InsertFile(strFile);

}

/**
 * 从OA-web端点击套红头
 *@param {*} params
 *     'templateURL':'',获取模板接口
 *     'replaceBookMark':'',标签
 */
function insertRedHeadDocFromWeb(params) { //插入红头
  var bookmark = params.replaceBookMark;
  var strFile = params.templateURL;
  if (bookmark) { //如果红头模板上有指定书签【"zhengwen"】的套红头方式
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
    if (activeDoc) {
      var bookMarks = activeDoc.Bookmarks;
      if (!bookMarks.Item("quanwen")) { //当前文档没有"quanwen"书签时候表示第一次套红
        //WPS上第一次点击套红头按钮
        firstInsertRedHead(strFile, bookMarks, bookmark);
      } else {
        alert("web端重复选择红头模板套红时，请在WPS-OA助手上操作")
      }
    }
  } else {
    alert("套红头失败，您选择的红头模板没有正文书签！")
  }
  //取消WPS关闭时的提示信息
  wps.WpsApplication().DisplayAlerts = WdAlertLevel.wdAlertsNone;

}

//第一次点击套红头按钮
function firstInsertRedHead(strFile, bookMarks, bookmark) {
  var wpsApp = wps.WpsApplication();
  var activeDoc = wpsApp.ActiveDocument;
  var selection = wpsApp.ActiveWindow.Selection;
  selection.WholeStory(); //选取全文
  bookMarks.Add("quanwen", selection.Range)
  selection.Cut();
  activeDoc.TrackRevisions = false; //准备以非批注的模式插入红头文件
  selection.InsertFile(strFile);
  if (bookMarks.Exists(bookmark)) {
    var bookmark1 = bookMarks.Item(bookmark);
    bookmark1.Range.Select(); //获取指定书签位置
    var s = activeDoc.ActiveWindow.Selection;
    s.Paste();
    //取消WPS关闭时的提示信息
    wps.WpsApplication().DisplayAlerts = WdAlertLevel.wdAlertsNone;
  } else {
    alert("套红头失败，您选择的红头模板没有对应书签：" + bookmark);
  }
}

//多次点击套红头按钮
function manyInsertRedHead(strFile, bookMarks, bookmark) {
  var wpsApp = wps.WpsApplication();
  var activeDoc = wpsApp.ActiveDocument;
  var bookmark2 = bookMarks.Item("quanwen")
  bookmark2.Range.Select();
  var selection = wpsApp.ActiveWindow.Selection;
  // selection.Cut();//不可缺少，否则第一次套完后有文件有修改，再套红头粘贴的依旧是第一次粘贴的内容
  selection.WholeStory(); //选取"quanwen"书签标记的全文
  selection.Delete(); //删除剪切了正文以后多余的东西(既第一次的红头文件)
  activeDoc.TrackRevisions = false; //准备以非批注的模式插入红头文件
  selection.InsertFile(strFile);
  var bookmark3 = bookMarks.Item(bookmark);
  if (bookmark3) {
    bookmark3.Range.Select(); //获取指定书签位置
    var s = activeDoc.ActiveWindow.Selection;
    s.Paste();
  }

}

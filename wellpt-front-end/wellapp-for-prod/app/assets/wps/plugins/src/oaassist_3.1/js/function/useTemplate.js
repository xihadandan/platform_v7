function fillData(data, template) {
  data.forEach(function (it) {
    var bookmark = template.Bookmarks.Item(it.name);
    if (bookmark) {
      var type = it.type
      if (!type || type === "text") {
        bookmark.Range.Text = it.text;
      } else if (type === "link") {
        bookmark.Range.InsertFile(it.text)
      } else if (type === "pic") {
        var pic = bookmark.Range.InlineShapes.AddPicture(it.text);
        pic.LockAspectRatio = 0;
        if (it.picHeight) {
          pic.Height = it.picHeight; //设定图片高度
        }
        if (it.picWidth) {
          pic.Width = it.picWidth; //设定图片宽度
        }
      } else if (type === "table") {   // 表格中插入数据
        insertTableFormWeb(template, bookmark, it.text)
      }
    }
  })
}

/**
 * 套用模板插入文字/图片/文档
 *  * params参数结构
 * params:{
 *     'docId': docId, //文档ID
 *     'templateURL':'',获取模板接口
 *     'fileName':'',获取文档接口
 *     'uploadPath':'',文档保存上传接口
 * }
 * @param {*} params
 */
function UseTemplate(params) {
  var templateURL = params.templateURL;
  var dataFromWeb = params.dataFromWeb;
  var dataFromServer = params.dataFromServer;
  //获取模板
  var templatePath = wps.OAAssist.DownloadFile(templateURL);
  var template = wps.WpsApplication().Documents.Open(templatePath);

  // fillData(dataFromWeb, template);
  // var prop = template.CustomDocumentProperties;
  //获取文档内容
  if (dataFromServer) {
    $.ajax({
      url: dataFromServer,
      async: false,
      method: "post",
      dataType: 'json',
      success: function (res) {
        res.push.apply(res, dataFromWeb);
        fillData(res, template);
      }
    });
  } else {
    fillData(dataFromWeb, template);
  }
  var ribbonExt = null;
  var buttonGroups;
  for (var key in params) {
    if (key === "buttonGroups") {
      buttonGroups = params[key]
    } else {
      propAddStorage(template.DocID, key, params[key]);
    }
  }
  propAddStorage(template.DocID, "isOA", "true");
  // var butArr = params[key];
  ribbonExt = getRibbonExt3(buttonGroups);
  propAddStorage(template.DocID, "ribbonExt", JSON.stringify(ribbonExt));
  //保存上传路径

  OnWindowActivate();
}

function fillTemplate(params) {
  var dataFromWeb = params.dataFromWeb;
  var dataFromServer = params.dataFromServer;
  //获取模板

  var template = wps.WpsApplication().ActiveDocument;
  //获取文档内容
  if (dataFromServer) {
    $.ajax({
      url: dataFromServer,
      async: false,
      method: "post",
      dataType: 'json',
      success: function (res) {
        res.push.apply(res, dataFromWeb);
        fillData(res, template);
      }
    });
  } else {
    fillData(dataFromWeb, template);
  }
}

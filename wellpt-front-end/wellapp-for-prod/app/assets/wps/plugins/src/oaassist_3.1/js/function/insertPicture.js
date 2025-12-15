/**
 * 插入图片
 * params参数结构
 * params:{
 *   'username':'',//用户名
 *   'picPath':'',//图片路径
 *   'templateURL':'',//模板文件路径
 *   'picHeight':'',//图片高
 *   'picWidth':'',//图片宽
 *   'docId':'',//文档ID
 *   'uploadPath':'',//保存路径
 * }
 * @param {*} params
 */
function OnInsertPicture(params) {
  var doc; //文档名
  var username; //用户名
  var picPath; //图片路径
  var templateurl; //模板文件路径
  var picHeight; //图片高
  var picWidth; //图片宽
  var docId; //文档ID
  var uploadPath; //保存路径
  if (params) {
    username = params.username;
    picPath = params.picPath;
    templateurl = params.templateURL;
    picHeight = params.picHeight;
    picWidth = params.picWidth;
    docId = params.docId;
    uploadPath = params.uploadPath;
  }

  if (templateurl) {
    var template = wps.OAAssist.DownloadFile(templateurl);
    doc = wps.WpsApplication().Documents.Open(template);
  } else {
    doc = wps.WpsApplication().ActiveDocument;
  }

  // var prop = doc.CustomDocumentProperties;

  if (username) {
    wps.WpsApplication().UserName = username; //修改当前文档用户名
  }

  //保存文档ID
  if (docId) {
    propAddStorage(doc.DocID, "docId", docId);
  }

  //保存上传路径
  if (uploadPath) {
    propAddStorage(doc.DocID, "uploadPath", uploadPath);
  }

  if (picPath == undefined || picPath == null) { //设定图片路径
    var url = GetUrlPath();
    if (url.length != 0) {
      picPath = url.concat("/icon/erweima.png").substring(8); //截取file:///后的路径
    } else {
      picPath = url.concat("./icon/erweima.png");
    }
  }
  if (picHeight == undefined || picPath == null) { //设定图片高度
    picHeight = 39.117798; //13.8mm=39.117798磅
  }
  if (picWidth == undefined || picPath == null) { //设定图片宽度
    picWidth = 140.880768; //49.7mm=140.880768磅
  }
  // 这是从浏览器传递到插件图片的宽高 ,也可以从wps里的insertpicture页面中传递过来，
  var webSetPicHeight = wps.PluginStorage.getItem("picHeight");
  var webSetPicWidth = wps.PluginStorage.getItem("picWidth");
  // 是否使用图片原始高度的标志
  var originWidthHeight = wps.PluginStorage.getItem("originWidthHeight");

  if (webSetPicWidth) {
    picWidth = webSetPicWidth;
  }
  if (webSetPicHeight) {
    picHeight = webSetPicHeight
  }

  var s = wps.WpsApplication().ActiveWindow.Selection;
  var pagecount = doc.BuiltInDocumentProperties.Item(WdBuiltInProperty.wdPropertyPages); //获取文档页数
  s.GoTo(WdGoToItem.wdGoToPage, WdGoToItem.wdGoToPage, pagecount.Value); //将光标指向文档最后一页


  var pic2 = s.InlineShapes.AddPicture(picPath, false, true); //插入图片
  pic2.LockAspectRatio = 0;

  // 是否使用图片原始高度 1:为采用原始宽高,不需要传递picHeight，picWidth;0为不采用原始宽高，可以设置picHeight，picWidth
  if (originWidthHeight != undefined && 0 == originWidthHeight) {
    if (picHeight) {
      pic2.Height = picHeight; //设定图片高度
    }
    if (picWidth) {
      pic2.Width = picWidth; //设定图片宽度
    }
  }

  pic2.LockAspectRatio = 0;
  pic2.Select(); //当前图片为焦点
  var seal_shape = pic2.ConvertToShape(); //类型转换:嵌入型图片->粘贴版型图片

  seal_shape.RelativeHorizontalPosition = WdRelativeHorizontalPosition.wdRelativeHorizontalPositionPage;
  seal_shape.RelativeVerticalPosition = WdRelativeHorizontalPosition.wdRelativeHorizontalPositionPage;
  seal_shape.Left = 315; //设置指定形状或形状范围的垂直位置（以磅为单位）。
  seal_shape.Top = 630; //指定形状或形状范围的水平位置（以磅为单位）。

}

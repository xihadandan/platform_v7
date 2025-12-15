/**
 * 添加水印
 * param参数结构
 * param:{
 *   text: '', 传入水印文字，可选，不传则生成当前时间戳
 * }
 * @param {*} param 
 */
function insertWatermark(param){
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    var sel = doc.Sections.Item(1)
    // '插入水印前需更改视图样式为页眉视图
    wpsApp.ActiveWindow.ActivePane.View.SeekView = WdSeekView.wdSeekCurrentPageHeader
    // 生成水印
    var oShape = sel.Headers.Item(WdHeaderFooterIndex.wdHeaderFooterPrimary).Shapes.AddTextEffect(MsoPresetTextEffect.msoTextEffect1, (param && param.text !== "") ? param.text : currentTime(), "微软雅黑", 36, false,false, 0, 0)
    oShape.TextEffect.NormalizedHeight = false //'文字文字效果
    oShape.Line.Visible = false  // 线条是否可见
    oShape.Fill.Visible = true //'填充是否可见
    oShape.Fill.Transparency = 0.5
    oShape.Fill.Solid() // '填充类型（本例为纯色）
    oShape.Rotation = 315 //'设置旋转角度
    oShape.LockAspectRatio = true //'锁定纵横比
    oShape.Height = wpsApp.CentimetersToPoints(1.27) //'高度
    oShape.Width = wpsApp.CentimetersToPoints(8.25) //'宽度
    oShape.WrapFormat.AllowOverlap = true //'是否允许重叠
    oShape.WrapFormat.Side = WdWrapType.wdWrapNone //'是否设置文字环绕
    oShape.WrapFormat.Type = 3 //'设置折回样式（本例设为不折回）
    oShape.RelativeHorizontalPosition = WdRelativeVerticalPosition.wdRelativeVerticalPositionMargin //'设置水平位置与纵向页边距关联
    oShape.RelativeVerticalPosition = WdRelativeVerticalPosition.wdRelativeVerticalPositionMargin //'设置垂直位置与横向页边距关联
    oShape.Left = WdShapePosition.wdShapeCenter //'水平居中
    oShape.Top = WdShapePosition.wdShapeCenter //'垂直居中
    oShape.ZOrder(MsoZOrderCmd.msoSendBehindText);
    wpsApp.ActiveWindow.ActivePane.View.SeekView = WdSeekView.wdSeekMainDocument //'恢复视图样式到原来样式
}

/**
* 如何描述该JS
*
* @author ${author}
* @date ${createDate}
*/
$(function() {
// 保存
var btn_save = "B001001";
// 提交
var btn_submit = "B001002";
// 删除
var btn_delete = "B001003";
// 业务VO类
var bean = {
"formUuid" : null, // 表单定义UUID
"dataUuid" : null, // 表单数据UUID
"dyFormData" : null // 表单数据
};
// 表单选择器
var dytableSelector = "#dyform";
var optSelector = ".form_operate";
// 操作服务
var getDataService = "${entityLowFisrt}DyformMaintain.getData";
var saveDataService = "${entityLowFisrt}DyformMaintain.saveData";
var submitDataService = "${entityLowFisrt}DyformMaintain.submitData";
var deleteDataService = "${entityLowFisrt}DyformMaintain.deleteData";

/** ********************************* 初始化开始 ********************************* */
// 业务数据初始化
bean.formUuid = $("#biz_formUuid").val();
bean.dataUuid = $("#biz_dataUuid").val();
// 获取数据
JDS.call({
service : getDataService,
data : [ bean ],
success : function(result) {
// 获取数据后处理
bean = result.data;
// 初使化表单
onGetBizData(bean);
}
});
// 初使化表单
function onGetBizData(data) {
var isFirst = StringUtils.isBlank(bean.dataUuid);
var dyFormData = bean.dyFormData;
try {
$(dytableSelector).dyform({
formData : dyFormData,
optional : {
isFirst : isFirst
},
success : function() {
// 表单解析成功
onDyformOpen();
},
error : function() {
oAlert("表单解析失败!");
}
});
} catch (e) {
oAlert2("表单解析失败： " + e);
$(":button", optSelector).each(function() {
$(this).hide();
});
throw e;
}
}
// 动态表单初始化后回调处理
function onDyformOpen() {
// 绑定调整自适应表单宽度
$(window).on("resize", function(e) {
adjustWidthToForm();
}).trigger("resize");
}
// 关闭窗口
$(".form_title>.form_close").on("click", function(e) {
window.close();
});
/** ********************************* 初始化结束 ********************************* */

/** ********************************* 保存开始 ********************************* */
// 保存
$(":button[name='" + btn_save + "']", optSelector).each(function() {
$(this).on("click", $.proxy(onSave, this));
$(this).show();
});
// 保存事件处理
function onSave(event) {
// 获取表单数据
bean.dyFormData = getDyformData();
JDS.call({
service : saveDataService,
data : [ bean ],
success : function(result) {
oAlert("保存成功！", function() {
var url = ctx + "/${moduleRequestPath!}${classRequestMappingPath}/maintain/view";
var formUuid = bean.formUuid;
var dataUuid = result.data;
window.location = url + "?formUuid=" + formUuid + "&dataUuid=" + dataUuid;
});
},
error : function(jqXHR) {
oAlert2("保存失败!");
}
});
}
/** ********************************* 保存结束 ********************************* */

/** ********************************* 提交开始 ********************************* */
// 提交
$(":button[name='" + btn_submit + "']", optSelector).each(function() {
$(this).on("click", $.proxy(onSubmit, this));
$(this).show();
});
// 提交事件处理
function onSubmit(event) {
// 提交动态表单
try {
// 会签待办提交不进行表单验证
if ($(dytableSelector).dyform("validateForm") === true) {
submitForm(event, $(this));
}
} catch (e) {
oAlert2("表单数据验证出错" + e + "，无法提交数据！");
throw e;
}
}
// 提交动态表单操作
function submitForm(event, $btn) {
// 获取表单数据
bean.dyFormData = getDyformData();
JDS.call({
service : submitDataService,
data : [ bean ],
success : function(result) {
oAlert("提交成功!", function() {
returnWindow();
window.close();
});
},
error : function(jqXHR) {
// 提交失败
oAlert2("提交失败!");
}
});
}
/** ********************************* 提交结束 ********************************* */

/** ********************************* 删除开始 ********************************* */
// 删除
$(":button[name='" + btn_delete + "']", optSelector).each(function() {
$(this).on("click", $.proxy(onDelete, this));
});
// 删除事件处理
function onDelete(event) {
// 获取表单数据
bean.dyFormData = getDyformData();
JDS.call({
service : deleteDataService,
data : [ bean ],
success : function(result) {
// 删除成功
oAlert("删除成功!", function() {
returnWindow();
window.close();
});
},
error : function(jqXHR) {
// 删除失败
oAlert2("删除失败!");
}
});
}
/** ********************************* 删除结束 ********************************* */

/** ******************************** 公共方法开始 ******************************** */
// 调整自适应表单宽度
function adjustWidthToForm() {
var div_body_width = $(window).width() * 0.95;
$(".form_header").css("width", div_body_width - 5);
$(".div_body").css("width", div_body_width);
}
// 获取表单数据
function getDyformData() {
var dyFormData = null;
try {
dyFormData = $(dytableSelector).dyform("collectFormData");
} catch (e) {
oAlert2("表单数据收集失败[ + " + e + "]，无法提交数据！");
throw e;
}
return dyFormData;
}
/** ******************************** 公共方法结束 ******************************** */

});

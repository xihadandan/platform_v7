/***
* @author ${author}
* @date ${createDate}
*
*/
${r'$'}(function() {
// 防止JS加载失败可提交
WorkFlow.beforeSubmit = function() {
return false;
};

// 保存成功后处理
WorkFlow.afterSuccessSave = function(data) {
};

// 提交成功后处理
WorkFlow.afterSuccessSubmit = function(bean, result) {
return false;
};

// 判断字符串不为undefined、null、空串、空格串
var isNotBlank = StringUtils.isNotBlank;
// 判断字符串为undefined、null、空串、空格串
var isBlank = StringUtils.isBlank;
// 获取动态表单选择器
var dytableSelector = WorkFlow.getDyformSelector();

// 设置动态表单字段值
function setFieldValue(mappingName, value, key, type) {
var fieldValue = {
mappingName : mappingName,
value : value
};

if (key != null) {
fieldValue.key = key;
}
if (type != null) {
fieldValue.type = type;
}
$(dytableSelector).dyform("setFieldValue", mappingName, value);
}
// 获取动态表单字段值
function getFieldValue(mappingName) {
var value = $(dytableSelector).dyform("getFieldValue", {
fieldMappingName : mappingName
});
return value;
}

// 保存前服务处理
// WorkFlow.setWorkData("beforeSaveService", "XZSPBizService.beforeSave");

// 保存后服务处理
// WorkFlow.setWorkData("afterSaveService", "XZSPBizService.afterSave");

// 提交前服务处理
// WorkFlow.setWorkData("beforeSubmitService", "XZSPBizService.beforeSubmit");
// 提交后服务处理
// WorkFlow.setWorkData("afterSubmitService", "XZSPBizService.afterSubmit");

// 退回前服务处理
// WorkFlow.setWorkData("beforeRollbackService", "XZSPBizService.beforeRollback");

// 行政审批打印服务
// WorkFlow.setWorkData("printService", "XZSPPrintService.print");
// 打印结果直接显示
// WorkFlow.setWorkData("printToDisplay", true);

});
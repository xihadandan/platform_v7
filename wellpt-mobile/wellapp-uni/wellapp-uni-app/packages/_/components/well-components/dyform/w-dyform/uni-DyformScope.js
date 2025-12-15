// 代理一笔表单，字段通过该对象获取所需要的信息
var DyformScope = function (dyform, formDefinition, formData) {
  this.dyform = dyform;
  this.formDefinition = formDefinition;
  this.formData = formData;
  this.isSubformScope = this.dyform.formDefinition.uuid !== formDefinition.uuid;
};
// 获取数据UUID
DyformScope.prototype.getDataUuid = function () {
  return this.formData.uuid;
};
DyformScope.prototype.setDataUuid = function (dataUuid) {
  this.formData.uuid = dataUuid;
};
// 获取表单ID
DyformScope.prototype.getFormId = function () {
  return this.formDefinition.id;
};
// 获取表单定义UUID
DyformScope.prototype.getFormUuid = function () {
  return this.formDefinition.uuid;
};
// 获取表单解析对象
DyformScope.prototype.getDyform = function () {
  return this.dyform;
};
// 获取表单数据
DyformScope.prototype.getFormData = function () {
  return this.formData;
};
// 判断是否从表数据
DyformScope.prototype.isSubform = function () {
  return this.isSubformScope;
};
// 设置所在从表对象
DyformScope.prototype.setSubform = function (subform) {
  return (this.subform = subform);
};
// 获取所在从表对象
DyformScope.prototype.getSubform = function () {
  return this.subform;
};
// 判断表单是否创建完成
DyformScope.prototype.isDyformCreateComplete = function () {
  return this.dyform.dyformCreateComplete;
};
// 表单是否显示为文本
DyformScope.prototype.isDisplayAsLabel = function () {
  return this.dyform.isDisplayAsLabel();
};
// 设置区块组件编号列表
DyformScope.prototype.setBlockWidgetCodes = function (blockWidgetCodes) {
  this.blockWidgetCodes = blockWidgetCodes;
};
// 获取区块组件编号列表
DyformScope.prototype.getBlockWidgetCodes = function () {
  return this.blockWidgetCodes || [];
};
module.exports = DyformScope;

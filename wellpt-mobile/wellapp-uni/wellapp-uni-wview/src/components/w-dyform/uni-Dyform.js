const _ = require("lodash");
const DyformScope = require("./uni-DyformScope.js");
const DyformValidation = require("./uni-DyformValidation.js");
var Dyform = function (options) {
  this.options = options;
  this.formDefinition = options.formDefinition;
  this.formData = options.formData;
  this.formFields = [];
  this.mainFormData = options.mainFormData;
  this.formValidation = new DyformValidation(this);
  this.displayState = options.displayState || "edit";
};
Dyform.prototype.getFormUuid = function () {
  return this.formDefinition.uuid; // this.formData.formUuid;
};
Dyform.prototype.getDataUuid = function () {
  return this.getMainFormData().uuid; // this.formData.dataUuid;
};
Dyform.prototype.isDisplayAsLabel = function () {
  return this.options.displayAsLabel;
};
Dyform.prototype.addField = function (field) {
  this.formFields.push(field);
};
Dyform.prototype.getFields = function () {
  return this.formFields;
};
// 获取字段对象
Dyform.prototype.getField = function (fieldName) {
  var _self = this;
  for (var i = 0; i < _self.formFields.length; i++) {
    if (fieldName == _self.formFields[i].getName()) {
      return _self.formFields[i];
    }
  }
  return null;
};
// 获取从表对象
Dyform.prototype.getSubform = function (formId) {
  var _self = this;
  for (var i = 0; i < _self.formFields.length; i++) {
    if (formId == _self.formFields[i].getName()) {
      return _self.formFields[i];
    }
  }
  return null;
};
Dyform.prototype.getMainFormData = function () {
  var _self = this;
  if (_self.mainFormData) {
    return _self.mainFormData;
  }
  var defaultFormData = _self.formDefinition.defaultFormData || {};
  var mfd = _self.formData.formDatas[_self.formData.formUuid];
  var formData = mfd == null || mfd.length == 0 ? defaultFormData : mfd[0];
  for (var key in defaultFormData) {
    if (formData[key] == null) {
      formData[key] = defaultFormData[key];
    }
  }
  _self.mainFormData = formData;
  _self.formData.formDatas[_self.formData.formUuid] = [formData];
  return _self.mainFormData;
};
Dyform.prototype.getFormData = function (formUuid) {
  var mfd = this.formData.formDatas[formUuid];
  if (mfd == null) {
    mfd = [];
    this.formData.formDatas[formUuid] = mfd;
  }
  return mfd;
};
Dyform.prototype.createDyformScope = function (formDefinition, formData) {
  return new DyformScope(this, formDefinition, formData);
};
// 收集表单数据
Dyform.prototype.collectFormData = function () {
  var _self = this;
  // 表单数据
  var formDatas = {};
  // 新增的数据
  var addedFormDatas = _self.formData.addedFormDatas || {};
  // 更新的数据
  var updatedFormDatas = _self.formData.updatedFormDatas || {};
  // 删除的数据
  var deletedFormDatas = _self.formData.deletedFormDatas || {};
  // 主表数据
  var mainFormData = _self.getMainFormData();
  var mainFormDataUpdatedFieldMap = {};
  _.each(_self.formFields, function (field) {
    // var formUuid = field.getFormUuid();
    var dataUuid = field.getDataUuid();
    var fieldName = field.getName();
    // 从表数据
    if (field.isSubform()) {
      var subformUuid = field.getSubFormUuid();
      // 新增的数据Map<String/* 表单定义id */, List<String>/* 表单数据id */>
      addedFormDatas[subformUuid] = field.getAddedData();
      // 更新的数据Map<String/* 表单定义id */, Map<String /* 数据记录uuid */,
      // Set<String> /* 字段值 */>>
      updatedFormDatas[subformUuid] = field.getUpdatedData();
      // 删除的数据Map<String/* 表单定义id */, List<String>/* 表单数据id */>
      deletedFormDatas[subformUuid] = field.getDeletedData();
      // 从表数据
      formDatas[subformUuid] = field.getValue();
    } else {
      // 主表数据
      mainFormData[fieldName] = field.getValueForSave();
      // 变更数据的字段
      if (field.isValueChanged()) {
        var updatedFields = mainFormDataUpdatedFieldMap[dataUuid];
        if (updatedFields == null) {
          updatedFields = [];
          mainFormDataUpdatedFieldMap[dataUuid] = updatedFields;
        }
        updatedFields.push(fieldName);
      }
    }
  });
  formDatas[_self.formData.formUuid] = [];
  formDatas[_self.formData.formUuid].push(mainFormData);
  updatedFormDatas[_self.formData.formUuid] = mainFormDataUpdatedFieldMap;

  // 最终的数据
  var dyformData = {
    addedFormDatas: addedFormDatas,
    updatedFormDatas: updatedFormDatas,
    deletedFormDatas: deletedFormDatas,
    formDatas: formDatas,
    formUuid: _self.formData.formUuid,
  };
  return dyformData;
};
// 表单验证
Dyform.prototype.validateForm = function () {
  var validationResult = this.formValidation.validate(true);
  if (validationResult.hasErrors()) {
    this.showValidationErrors(validationResult);
    console.log("表单验证失败!");
    console.log(validationResult);
    return false;
  }
  console.log("表单验证成功!");
  uni.$emit("hideValidationErrors", validationResult);
  return true;
};
// 显示验证错误信息列表，可快速进行错误定位
Dyform.prototype.showValidationErrors = function (validationResult) {
  uni.$emit("showValidationErrors", validationResult);
};

module.exports = Dyform;

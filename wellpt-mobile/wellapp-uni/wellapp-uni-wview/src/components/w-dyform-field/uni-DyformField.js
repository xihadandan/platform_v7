const _ = require("lodash");
const uuidv4 = require("uuid");

// 验证字段错误
var DyformFieldError = function (field, validResult) {
  // 验证的字段对象
  this.field = field;
  // 验证的字段名
  this.fieldName = field.getName();
  // 验证的字段显示名
  this.displayName = field.getDisplayName();
  // 验证不通过的字段值
  this.rejectedValue = field.getValue();
  // 验证规则
  this.checkedRule = validResult.checkedRule;
  // 验证错误提示信息
  this.message = validResult.tipMsg;
  // 验证的表单ID
  this.formId = field.formScope.getFormId();
  // 验证的数据UUID
  this.dataUuid = field.formScope.getDataUuid();
  // 是否为从表字段
  this.isSubformField = field.formScope.isSubform();
};

var DyformField = function (options) {
  this.formScope = options.formScope;
  this.definition = options.definition;
  // 字段所在表单的一笔数据
  this.formData = options.formData;
  this.originalValue = options.value;
  // 创建验证器
  this.createValidators();
  this.errorMessage = "";
};

// 获取字段所在定义UUID
DyformField.prototype.getFormUuid = function () {
  return this.formScope.getFormUuid();
};
// 获取字段所在数据UUID
DyformField.prototype.getDataUuid = function () {
  return this.formData.uuid;
};
DyformField.prototype.getFormScope = function () {
  return this.formScope;
};
DyformField.prototype.getDefinition = function () {
  return this.definition;
};
DyformField.prototype.getFormData = function () {
  return this.formData;
};
DyformField.prototype.getId = function () {
  if (_.isEmpty(this.id)) {
    this.id = uuidv4() + "_" + this.definition.name;
  }
  return this.id;
};
// 获取字段名
DyformField.prototype.getName = function () {
  return this.definition.name;
};
// 获取字段显示名
DyformField.prototype.getDisplayName = function () {
  return this.definition.displayName;
};
// 是否从表
DyformField.prototype.isSubform = function () {
  return false;
};
// 设置字段值
DyformField.prototype.setValue = function (value) {
  this.formData[this.definition.name] = value;
};
// 获取字段值
DyformField.prototype.getValue = function () {
  return this.formData[this.definition.name];
};
// 获取要保存的数据
DyformField.prototype.getValueForSave = function () {
  return this.formData[this.definition.name];
};
// 控件值是否修改
DyformField.prototype.isValueChanged = function () {
  var val = this.getValueForSave();
  var oriVal = this.originalValue;
  if (typeof val == "object" && typeof oriVal == "object") {
    return JSON.stringify(val) !== JSON.stringify(oriVal);
  }
  return val !== oriVal;
};
// 判断字段是否必填
DyformField.prototype.isRequired = function () {
  var _self = this;
  if (_self.required != null) {
    return _self.required;
  }
  if (_self.definition.required) {
    _self.required = true;
    return _self.required;
  }
  var fieldCheckRules = _self.definition.fieldCheckRules || {};
  for (var i = 0; i < fieldCheckRules.length; i++) {
    if (fieldCheckRules[i].value == "1") {
      _self.required = true;
      return _self.required;
    }
  }
  _self.required = false;
  return _self.required;
};
// 创建字段验证器
DyformField.prototype.createValidators = function () {
  var _self = this;
  var validators = {};
  var fieldCheckRules = _self.getFieldCheckRules(_self.definition);
  var formValidation = _self.formScope.getDyform().formValidation;
  _.each(fieldCheckRules, function (fieldCheckRule) {
    if (fieldCheckRule.value != null) {
      // 去重复
      var validator = formValidation.createValidator(fieldCheckRule);
      if (_.isFunction(validator.fn)) {
        validators[fieldCheckRule.value] = validator;
      } else {
        console.log("field[" + _self.getName() + "]rule[" + fieldCheckRule.value + "] fn no implement");
      }
    }
  });
  _self.validators = validators;
};
DyformField.prototype.getFieldCheckRules = function (options) {
  options.fieldCheckRules = options.fieldCheckRules || [];
  if (options.length > 0) {
    options.fieldCheckRules.push({
      value: "10",
      label: "普通",
    });
  }
  return options.fieldCheckRules;
};
// 获取字段验证器
DyformField.prototype.getValidators = function () {
  return this.validators;
};
// 验证
DyformField.prototype.validate = function () {
  var _self = this;
  // 表单创建完成才可验证
  if (!_self.formScope.isDyformCreateComplete()) {
    return;
  }
  var results = [];
  var validators = this.getValidators();
  _.each(validators, function (validator) {
    validator.validate && results.push(validator.validate(_self));
  });
  var fieldErrors = [];
  _.each(results, function (validResult) {
    if (validResult.valid == false) {
      fieldErrors.push(new DyformFieldError(_self, validResult));
    }
  });
  // 显示错误信息
  if (fieldErrors.length > 0) {
    _self.showErrors(fieldErrors);
  } else {
    // 隐藏错误信息
    _self.hideErrors();
  }
  return fieldErrors;
};
// 显示验证错误信息
DyformField.prototype.showErrors = function (errors) {
  if (errors && errors.length > 0) {
    this.errorMessage = errors[0].message;
  }
};
DyformField.prototype.hideErrors = function () {
  this.errorMessage = "";
};
// 添加验证规则
DyformField.prototype.addCustomValidate = function (fn) {
  // this.validators.push(this.dyform.formValidation.createCustomValidator(fn));
  var _self = this;
  var validator = _self.formScope.getDyform().formValidation.createCustomValidator(fn);
  _self.validators[validator.rule] = validator;
};
module.exports = DyformField;

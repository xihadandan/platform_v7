const _ = require("lodash");

var Subform = function (options) {
  var _self = this;
  _self.formScope = options.formScope;
  _self.definition = options.definition;
  _self.dataList = options.value;
  _self.dataListJson = JSON.stringify(options.value);
  _self.addedDataUuids = [];
  _self.updateData = {};
  _self.deletedDataUuids = [];
  _self.formFields = [];
};

// 获取字段所在定义UUID
Subform.prototype.getFormUuid = function () {
  return this.formScope.getFormUuid();
};
// 获取字段所在数据UUID
Subform.prototype.getDataUuid = function () {
  return this.formScope.getDataUuid();
};
// 获取字段名
Subform.prototype.getName = function () {
  return this.definition.name;
};
// 是否从表
Subform.prototype.isSubform = function () {
  return true;
};
// 获取字段所在定义UUID
Subform.prototype.getSubFormUuid = function () {
  return this.definition.uuid || this.definition.formUuid;
};
Subform.prototype.addField = function (field) {
  return this.formFields.push(field);
};
Subform.prototype.getFields = function () {
  return this.formFields;
};
Subform.prototype.clearFields = function () {
  return (this.formFields = []);
};
Subform.prototype.validate = function () {
  var _self = this;
  var errors = [];
  var formFields = _self.getFields();
  _.each(formFields, function (formField) {
    var validResults = formField.validate();
    errors = errors.concat(validResults);
  });
  return errors;
};
// 新增的数据
Subform.prototype.getAddedData = function () {
  return this.addedDataUuids;
};
// 获取更新的数据字段
Subform.prototype.getUpdatedData = function () {
  var _self = this;
  if (_self.definition.displayStyle != "1") {
    return _self.updateData;
  }
  // var fields = _self.definition.fields;
  var dataList = _self.value;
  var originalDataList = JSON.parse(_self.dataListJson);
  _.each(dataList, function (formData) {
    // var updatedFields = [];
    var originalData = _self.getOriginalData(originalDataList, formData.uuid);
    if (originalData == null) {
      return;
    }
    _self.setUpdatedData(formData, originalData);
  });
  return _self.updateData;
};
// 获取更新的数据字段
Subform.prototype.setUpdatedData = function (newData, oldData) {
  var _self = this;
  var originalData = oldData;
  if (originalData == null) {
    var originalDataList = JSON.parse(_self.dataListJson);
    originalData = _self.getOriginalData(originalDataList, newData.uuid);
    if (originalData == null) {
      return;
    }
  }
  if (newData.uuid != originalData.uuid) {
    return;
  }
  var fields = _self.definition.fields;
  var updatedFields = [];
  _.each(fields, function (fieldDefinition) {
    var fieldName = fieldDefinition.name;
    var newVal = newData[fieldName];
    var oldVal = originalData[fieldName];
    if (typeof newVal == "object" && JSON.stringify(newVal) != JSON.stringify(oldVal)) {
      updatedFields.push(fieldDefinition.name);
    } else if (newVal != oldVal) {
      updatedFields.push(fieldDefinition.name);
    }
  });
  _self.updateData[newData.uuid] = updatedFields;
};
Subform.prototype.getOriginalData = function (dataList, dataUuid) {
  for (var i = 0; i < dataList.length; i++) {
    if (dataList[i].uuid == dataUuid) {
      return dataList[i];
    }
  }
};
Subform.prototype.setUpdatedFields = function (dataUuid, updateFields) {
  if (this.addedDataUuids.indexOf(dataUuid) != -1) {
    this.updateData[dataUuid] = updateFields;
  }
};
// 删除的数据
Subform.prototype.getDeletedData = function () {
  return this.deletedDataUuids;
};
// 获取字段值
Subform.prototype.getValue = function () {
  return this.dataList;
};
// 获取要保存的数据
Subform.prototype.getValueForSave = function () {
  return this.formData[this.definition.name];
};
// 控件值是否修改
Subform.prototype.isValueChanged = function () {
  return this.getValueForSave() !== this.originalValue;
};

module.exports = Subform;

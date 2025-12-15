define(['mui', 'constant', 'commons', 'server', 'mui-DyformConstant', 'mui-DyformField', 'multiOrg', 'mui-DyformCommons'], function (
  $,
  constant,
  commons,
  server,
  DyformConstant,
  DyformField,
  unit,
  dyformCommons
) {
  var StringUtils = commons.StringUtils;
  var loadPersonInfo = server.SpringSecurityUtils.getUserDetails;
  // 组织选择框
  var wUnit = function ($placeHolder, options) {
    DyformField.apply(this, arguments);
    this.value = options.fieldValue;
  };
  var dyFormOrgSelectType = DyformConstant.dyFormOrgSelectType;
  commons.inherit(wUnit, DyformField, {
    init: function () {
      var self = this;
      self.isChanged = false;
      self.separator = ';';
      var value = self.value;
      var defaultValue = self.options.fieldDefinition.defaultValue;
      // 新建表单时，value为undefined，如果有默认值，则设置默认值
      if (value === undefined && defaultValue) {
        self.value = value = defaultValue;
      }
      if (typeof value === 'string' && value.charAt(0) === '{' && value.charAt(value.length - 1) === '}') {
        self.value = Object.keys($.parseJSON(value)).join(';');
        self.isChanged = true;
      }
      // 表单编辑，如果清空value，则设值为"{}",覆盖已填写的默认值
      if (self.value === null || self.value === '') {
        self.value = '{}';
      }
      self._superApply(arguments);
    },
    // 渲染
    render: function () {
      var _self = this;
      _self._super();
      _self.$editableElem[0].id = _self.getId();
      _self.$editableElem[0].value = _self.getDisplayValue();
      _self.$editableElem[0].setAttribute('hiddenValue', _self.getValue());
      _self.$editableElem[0].setAttribute('readonly', 'readonly');
      _self.$editableElem[0].addEventListener('focus', function () {
        if (!_self.isReadonly()) {
          this.blur();
          var excludeValues = [],
            otherParams;
          var valueFormat = _self.definition.valueFormat;
          var filterCondition = _self.definition.filterCondition;
          if (filterCondition) {
            //按中英文的逗号和分号分割
            var paramsSchema = 'otherParams://';
            if (filterCondition.indexOf(paramsSchema) === 0) {
              try {
                otherParams = eval('(' + filterCondition.substr(paramsSchema.length) + ')');
                if (otherParams && otherParams.filterCondition) {
                  excludeValues = otherParams.filterCondition.split(/;|,|；|，/);
                }
              } catch (ex) {}
            } else {
              //按中英文的逗号和分号分割
              excludeValues = filterCondition.split(/;|,|；|，/);
            }
          }
          var dyform = _self.dyform;
          var formScope = _self.formScope;
          var viewStyle = _self.definition.viewStyle;
          var mappingValues = _self.definition.mappingValues;
          _self.definition.typeList = _self.definition.typeList || ['all'];
          _self.definition.selectTypeList = _self.definition.selectTypeList || ['all'];
          var computeInitData = $.isPlainObject(mappingValues)
            ? function (initData) {
                $.each(mappingValues, function (mappingName, mappingObj) {
                  var field = formScope.getField(mappingObj.fieldName);
                  if (!field || !field.setValue) {
                    field = dyform.getField(mappingObj.fieldName);
                  }
                  if (null == field || null == field.getValue) {
                    return null;
                  }
                  var values = field.getValue();
                  if (typeof values === 'string' && values.length > 0) {
                    values = values.split(';');
                  }
                  if ($.isArray(values) && values.length === initData.length) {
                    $.each(initData, function (idx, node) {
                      node.extValues = node.extValues || {};
                      node.extValues[mappingName] = values[idx];
                    });
                  }
                });
              }
            : null;

          function setExtValue(treeNodes, oldValue) {
            if ($.isPlainObject(mappingValues) && treeNodes && treeNodes.length > 0) {
              var setTodos = [];
              var mappingChn = {
                Dept: '部门',
                Job: '职位',
                Unit: '单位',
                OrgElement: '组织'
              };
              var getMappingChnName = function (mn) {
                for (var k in mappingChn) {
                  if (mn.indexOf(k) == 0) {
                    return mappingChn[k];
                  }
                }
              };
              $.each(mappingValues, function (mappingName, mappingObj) {
                var field = formScope.getField(mappingObj.fieldName);
                if (!field || !field.setValue) {
                  field = dyform.getField(mappingObj.fieldName);
                }
                if (null == field || null == field.setValue) {
                  return null;
                }
                var values = [],
                  msg;
                for (var i = 0; i < treeNodes.length; i++) {
                  var treeNode = treeNodes[i];
                  var mappingValue = treeNode.extValues && treeNode.extValues[mappingName];
                  if ($.trim(mappingValue).length > 0) {
                    values.push(mappingObj.path ? mappingValue : mappingValue.substr(mappingValue.lastIndexOf('/') + 1));
                  } else {
                    // values.push("");
                    if (oldValue && oldValue.indexOf(treeNode.id) > -1) {
                      msg =
                        treeNode.name +
                        '无法获取' +
                        getMappingChnName(mappingName) +
                        '，因为已有数据无法获取组织选择项，请先删除，再重新选择该人员!';
                    } else {
                      msg = treeNode.name + '无法获取' + getMappingChnName(mappingName) + '，请尝试在其他选择项中选择该人员!';
                    }
                    if (treeNode.extValues && treeNode.extValues.fromTypeText) {
                      msg = treeNode.extValues.fromTypeText + '中的' + msg;
                    }
                    alert(msg);
                    throw new Error(msg);
                  }
                }
                setTodos.push({
                  field: field,
                  value: values.join(';')
                });
              });
              $.each(setTodos, function (idx, todo) {
                todo.field.setValue(todo.value);
              });
            }
          }
          var viewStyles = null;
          if ($.isArray(viewStyle) && viewStyle.length == _self.definition.typeList.length) {
            viewStyles = {};
            $.each(_self.definition.typeList, function (idx, type) {
              viewStyles[type] = viewStyle[idx];
            });
          }
          var oldValue = _self.getValue();
          unit.open({
            title: '组织选择',
            labelField: _self.getId(),
            separator: this.separator,
            otherParams: otherParams,
            excludeValues: excludeValues,
            // selectType : _self.getSelectType(),
            type: _self.definition.typeList.join(';'),
            // showType : _self.definition.showUnitType,
            multiple: _self.definition.mutiSelect,
            filterCondition: _self.definition.filterCondition,
            typeList: _self.definition.typeList,
            selectTypes: _self.definition.selectTypeList.join(';'),
            valueFormat: valueFormat ? valueFormat : 'justId',
            defaultType: _self.definition.defaultType,
            ok: function (returnValue, treeNodes) {
              _self.setValue2Object();
              _self.setToRealDisplayColumn();
              _self.afterUnitChoose();
              setExtValue(treeNodes, oldValue); // 设置扩增属性
            },
            viewStyles: viewStyles,
            computeInitData: computeInitData
          });
        }
      });
    },
    _getDefaultType: function () {
      if (this.definition.defaultType) {
        return this.definition.defaultType;
      }
      if (this.definition.inputMode) {
        return this.getTypeByinputMode(this.definition.inputMode);
      }
      return '';
    },
    _getTemplateData: function () {
      var data = {
        displayName: this.definition.tagName || this.definition.displayName,
        fieldValue: this.getDisplayValue(),
        fieldName: this.definition.name
      };
      return data;
    },
    setToRealDisplayColumn: function () {
      if (!this.definition.realDisplay) {
        return;
      }
      var real = this.definition.realDisplay.real;
      var display = this.definition.realDisplay.display;

      if (commons.StringUtils.isNotBlank(real)) {
        var realField = this.dyform.getField(real);
        if (realField) {
          realField.setValue(this.getRealValue(), true);
        }
      }
      if (commons.StringUtils.isNotBlank(display)) {
        var displayField = this.formScope.getField(display);
        if (displayField) {
          displayField.setValue(this.getDisplayValue(), true);
        }
      }
    },
    // 渲染编辑元素
    renderEditableElem: function ($editableElem) {
      var self = this;
      if (self.$editableElem) {
        self.$editableElem[0].value = self.getDisplayValue();
        self.$editableElem[0].setAttribute('hiddenValue', self.getRealValue());
      }
    },
    // 渲染文本元素
    renderLabelElem: function ($labelElem) {
      var self = this;
      if (self.$labelElem) {
        self.$labelElem[0].innerHTML = self.getDisplayValue();
        self.$labelElem[0].setAttribute('hiddenValue', self.getRealValue());
      }
    },
    setValue2Object: function () {
      var self = this;
      self.isChanged = true;
      var realValue = self.$editableElem[0].getAttribute('hiddenvalue');
      if (realValue.length == 0) {
        this.value = {};
        return;
      }
      var displayValue = self.$editableElem[0].value;
      var values = realValue.split(';');
      var displayvalue = displayValue.split(';');
      if (values.length != displayvalue.length) {
        throw new Error('隐藏值和显示值长度不一致!');
      }
      var v = self.cacheOptionSet || (self.cacheOptionSet = {});
      for (var i = 0; i < values.length; i++) {
        v[values[i]] = displayvalue[i];
      }
      self.value = realValue;
    },
    isValueChanged: function () {
      return this.isChanged;
    },
    // 判断是否键值控件域
    isValueMap: function () {
      return true;
    },
    afterUnitChoose: function () {
      var self = this;
      var inputMode = self.definition.inputMode;
      self.trigger('afterOrgInfo', self.getValueMap());
      self.trigger('afterSetValue', self.getValueMap());
      self.validate();
    },
    getDataProvider: function () {
      var self = this;
      var value = self.getValue();
      var cacheOptionSet = self.cacheOptionSet || (self.cacheOptionSet = {});
      if (mui.isPlainObject(value)) {
        value = Object.keys(value);
      } else if ($.trim(value).length <= 0) {
        value = [];
      } else if (typeof value === 'string') {
        if (value.charAt(0) === '{' && value.charAt(value.length - 1) === '}') {
          value = Object.keys($.parseJSON(value));
        } else {
          value = value.split(';');
        }
      } else if (false === $.isArray(value)) {
        value = [value];
      }
      var reqValues = [];
      for (var i = 0; i < value.length; i++) {
        if (cacheOptionSet[value[i]] == null) {
          reqValues.push(value[i]);
        }
      }
      if (reqValues.length > 0) {
        $.ajax({
          type: 'POST',
          url: ctx + '/api/org/facade/getNameByOrgEleIds',
          dataType: 'json',
          async: false,
          data: {
            orgIds: reqValues
          },
          success: function (result) {
            $.extend(cacheOptionSet, result.data);
          }
        });
      }

      var dataProvider = [];
      for (var value in cacheOptionSet) {
        dataProvider.push({
          value: value,
          text: cacheOptionSet[value]
        });
      }
      return dataProvider;
    },
    getTypeByinputMode: function (inputMode) {
      var type = 'MyUnit';
      if (dyFormOrgSelectType.orgSelectAddress == inputMode) {
        // 单位通讯录
        type = 'Unit';
      } else if (dyFormOrgSelectType.orgSelectGroup == inputMode) {
        // 集团通讯录
        type = 'UnitUser';
      } else if (dyFormOrgSelectType.orgSelectStaff == inputMode) {
        // 组织选择框(仅选择组织人员)
        type = 'MyUnit';
      } else if (dyFormOrgSelectType.orgSelectDepartment == inputMode) {
        // 组织选择框(仅选择组织部门)
        type = 'Dept';
      } else if (dyFormOrgSelectType.orgSelect == inputMode) {
        // 组织选择框(仅选择组织人员)
        type = 'MyUnit';
      } else if (dyFormOrgSelectType.orgSelectStaDep == inputMode) {
        // 组织选择框（部门+人员）
        type = 'MyUnit';
      } else if (dyFormOrgSelectType.orgSelectJob == inputMode) {
        // 组织选择框(职位)
        type = 'Job';
      } else if (dyFormOrgSelectType.orgSelectPublicGroup == inputMode) {
        // 组织选择框(公共群组)
        type = 'PublicGroup';
      } else if (dyFormOrgSelectType.orgSelectPublicGroupSta == inputMode) {
        // 组织选择框(公共群组+人员)
        type = 'PublicGroup';
      } else if (dyFormOrgSelectType.orgSelectMyDept == inputMode) {
        // 组织选择框(我的部门)
        type = 'MyDept';
      } else if (dyFormOrgSelectType.orgSelectMyParentDept == inputMode) {
        // 组织选择框(上级部门)
        type = 'MyParentDept';
      } else if (dyFormOrgSelectType.orgSelectMyUnit == inputMode) {
        // 组织选择框(部门+职位+人员)
        type = 'MyUnit';
      }
      return type;
    },
    getSelectTypeByinputMode: function (inputMode) {
      var selectType = 1;
      if (dyFormOrgSelectType.orgSelectAddress == inputMode) {
        // 单位通讯录
        selectType = 1;
      } else if (dyFormOrgSelectType.orgSelectGroup == inputMode) {
        // 集团通讯录
        selectType = 1;
      } else if (dyFormOrgSelectType.orgSelectStaff == inputMode) {
        // 组织选择框(仅选择组织人员)
        selectType = 4;
      } else if (dyFormOrgSelectType.orgSelectDepartment == inputMode) {
        // 组织选择框(仅选择组织部门)
        selectType = 2;
      } else if (dyFormOrgSelectType.orgSelect == inputMode) {
        // 组织选择框(仅选择组织人员)
        selectType = 1;
      } else if (dyFormOrgSelectType.orgSelectStaDep == inputMode) {
        // 组织选择框（部门+人员）
        selectType = 6;
      } else if (dyFormOrgSelectType.orgSelectJob == inputMode) {
        // 组织选择框(职位)
        selectType = 32;
      } else if (dyFormOrgSelectType.orgSelectPublicGroup == inputMode) {
        // 组织选择框(公共群组)
        selectType = 1;
      } else if (dyFormOrgSelectType.orgSelectPublicGroupSta == inputMode) {
        // 组织选择框(公共群组+人员)
        selectType = 4;
      } else if (dyFormOrgSelectType.orgSelectMyDept == inputMode) {
        // 组织选择框(我的部门)
        selectType = 1;
      } else if (dyFormOrgSelectType.orgSelectMyParentDept == inputMode) {
        // 组织选择框(上级部门)
        selectType = 1;
      } else if (dyFormOrgSelectType.orgSelectMyUnit == inputMode) {
        // 组织选择框(部门+职位+人员)
        selectType = 1;
      }
      return selectType;
    },
    getSelectType: function () {
      var selectType = 0;
      if (this.definition.selectTypeList) {
        for (var i in this.definition.selectTypeList) {
          selectType += parseInt(this.definition.selectTypeList[i]);
        }
      } else if (this.definition.inputMode) {
        selectType = this.getSelectTypeByinputMode(this.definition.inputMode);
      }

      return selectType;
    }
  });
  return wUnit;
});

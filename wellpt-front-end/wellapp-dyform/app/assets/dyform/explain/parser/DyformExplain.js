define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'DyformDevelopment', 'DyformExplainDevelopment'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  DyformDevelopment,
  DyformExplainDevelopment
) {
  var systemFields = ['creator', 'create_time', 'modifier', 'modify_time', 'rec_ver', 'sort_order'];
  // 表单解析对象
  var DyformExplain = function (element, options) {
    this.element = element;
    this.options = options;
    this.$dyform = $(element);
    $(element).data('DyformExplain', this);
    $(element).data('options', options);
    this.init();
  };

  // 获取$dyform的dom元素上的表单解析对象
  var getDyformExplain = function ($dyform) {
    var dyformExplain = $dyform.data('DyformExplain');
    if (dyformExplain.$dyform == null) {
      dyformExplain.$dyform = $dyform;
    }
    return dyformExplain;
  };

  // 导出API，后面可以覆盖
  for (var name in $.fn.dyform) {
    if (false === $.isFunction($.fn.dyform[name])) {
      continue;
    }
    DyformExplain.prototype[name] = (function (name) {
      return function () {
        return $.fn.dyform[name].apply(this.$dyform, arguments);
      };
    })(name);
  }

  // 表单接口适配
  var DyformEventAdapter = function (dyform) {
    this.dyform = dyform;
  };

  $.extend(DyformEventAdapter.prototype, {
    beforeParseForm: function () {
      getDyformExplain(this).invokeDevelopmentMethod('beforeParseForm', getDyformExplain(this));
    },
    afterParseForm: function () {
      alert('该方法无实现,对应的功能可以移到beforeSetData里面去实现');
      getDyformExplain(this).invokeDevelopmentMethod('afterParseForm', getDyformExplain(this));
    },
    beforeSetData: function () {
      getDyformExplain(this).invokeDevelopmentMethod('beforeSetData', getDyformExplain(this));
    },
    afterSetData: function () {
      getDyformExplain(this).invokeDevelopmentMethod('afterSetData', getDyformExplain(this));
    },
    beforeImportData: function () {
      getDyformExplain(this).invokeDevelopmentMethod('beforeImportData', getDyformExplain(this));
    },
    beforeFillImportData: function () {
      getDyformExplain(this).invokeDevelopmentMethod('beforeFillImportData', getDyformExplain(this));
    },
    beforeSetReadOnly: function () {
      getDyformExplain(this).invokeDevelopmentMethod('beforeSetReadOnly', getDyformExplain(this));
    },
    afterSetReadOnly: function () {
      getDyformExplain(this).invokeDevelopmentMethod('afterSetReadOnly', getDyformExplain(this));
    },
    afterSetEditable: function () {
      getDyformExplain(this).invokeDevelopmentMethod('afterSetEditable', getDyformExplain(this));
    }
  });

  // 代理字段方法
  var _proxiedFieldMethod = function (proxiedField, field, method, callback) {
    $.each(field, function (prop, value) {
      if ($.isFunction(value)) {
        proxiedField[prop] = (function () {
          var _proxied = function () {
            if (prop === method) {
              return callback.apply(field, arguments);
            }
            return field[prop].apply(field, arguments);
          };
          return _proxied;
        })();
      }
    });
  };
  // 字段对象包装
  var wrapperField = function ($dyform, field) {
    if (field == null) {
      return field;
    }
    // 包装新的字段对象接口
    var proxiedField = {};
    _proxiedFieldMethod(proxiedField, field);

    // 获取真实值
    //        if (!$.isFunction(proxiedField.getRealValue)) {
    //            proxiedField.getRealValue = function () {
    //                 return this.getValue(true);
    //            };
    //        }

    // 获取显示值
    //        if (!$.isFunction(proxiedField.getDisplayValue)) {
    //            proxiedField.getDisplayValue = function () {
    //                 return this.getValue(false);
    //            };
    //        }

    // 事件监听
    if (!$.isFunction(proxiedField.on)) {
      $.extend(proxiedField, {
        on: function (type, listener) {
          var wrapperListener = function () {
            listener.apply(this, arguments);
          };
          proxiedField.bind(type, wrapperListener, true);
        }
      });
    }
    return proxiedField;
  };

  // 表单对象包装
  var wrapperSubform = function ($dyform, subform) {
    if (subform == null) {
      return subform;
    }

    // 注册从表事件
    if (!$.isFunction(subform.on)) {
      $.extend(subform, {
        on: function (type, listener) {
          var wrapperListener = function (rowdata) {
            if (type == 'afterInsertRow') {
              var e = new jQuery.Event(type, true, true);
              e.detail = {};
              e.detail.record = new RowRecord($dyform, subform, rowdata);
              listener.apply(this, [e]);
              return;
            }

            listener.apply(this, arguments);
          };
          subform.bind(type, wrapperListener);
        }
      });
    }

    // 获取从表数据
    if (!$.isFunction(subform.getData)) {
      $.extend(subform, {
        getData: function (fnSuccessCallback, fnFailCallback) {
          var subformId = subform.getFormId();
          return $dyform.getAllRowData({
            id: subformId,
            fnSuccessCallback: fnSuccessCallback,
            fnFailCallback: fnFailCallback
          });
        }
      });
    }

    // 获取从表记录
    if (!$.isFunction(subform.getRecord)) {
      $.extend(subform, {
        getRecord: function (rowid, fnSuccessCallback, fnFailCallback) {
          var id = rowid;
          if (typeof rowid === 'object') {
            id = rowid.id;
          }
          var subformId = subform.getFormId();
          $dyform.getAllRowData({
            id: subformId,
            fnSuccessCallback: function (allRowData) {
              $.each(allRowData, function (i, rowData) {
                if (id == rowData.uuid) {
                  var record = new RowRecord($dyform, subform, rowData);
                  fnSuccessCallback.call(this, record);
                }
              });
            },
            fnFailCallback: function () {
              if ($.isFunction(fnFailCallback)) {
                fnSuccessCallback.apply(this, arguments);
              }
            }
          });
        }
      });
    }

    // 添加从表数据
    if (!$.isFunction(subform.addRowData)) {
      $.extend(subform, {
        addRowData: function (rowData) {
          var subformId = subform.getFormId();
          $dyform.addRowData(subformId, rowData);
        }
      });
    }

    // 删除从表数据
    if (!$.isFunction(subform.deleteRowData)) {
      $.extend(subform, {
        deleteRowData: function (rowData) {
          var subformId = subform.getFormId();
          $dyform.deleteRowData(subformId, rowData);
        }
      });
    }

    // 更新从表数据
    if (!$.isFunction(subform.updateRowData)) {
      $.extend(subform, {
        updateRowData: function (rowData) {
          var subformId = subform.getFormId();
          $dyform.updateRowData(subformId, rowData);
        }
      });
    }

    return subform;
  };

  // 从表数据行记录
  var RowRecord = function ($dyform, subform, data) {
    this.$dyform = $dyform;
    this.subform = subform;
    this.data = data || {};
  };
  $.extend(RowRecord.prototype, {
    getField: function (fieldName) {
      return wrapperField(this.$dyform, this.$dyform.getControl(fieldName, this.data.id));
    },
    getFieldValue: function (fieldName) {
      return this.data[fieldName];
    },
    setFieldValue: function (fieldName, value) {
      return (this.data[fieldName] = value);
    },
    getData: function () {
      return this.data;
    },
    getId: function () {
      return this.data.id;
    },
    update: function () {
      var _self = this;
      var data = _self.data;
      this.$dyform.updateRowData(_self.subform.getFormId(), _self.data);
    }
  });

  // 表单解析对象接口
  $.extend(DyformExplain.prototype, {
    getOption: function () {
      return this.$dyform.getOption();
    },
    init: function () {
      var _self = this;
      var options = _self.options;
      if (!options['renderTo']) {
        throw new Error('options[renderTo] is require');
      }
      var element = _self.element;
      var before_init_event_result;
      var formDefinition = options.formData.formDefinition;
      var dyformEvents;
      if (typeof formDefinition == 'string') {
        // 字段串类型
        formDefinition = eval('(' + formDefinition + ')');
        if (formDefinition.events && formDefinition.events != '') {
          dyformEvents = eval('(' + formDefinition.events + ')');
          if (dyformEvents['beforeInit_event']) {
            appContext.eval(
              dyformEvents['beforeInit_event'],
              this,
              {
                formDefinition: formDefinition
              },
              function (v) {
                before_init_event_result = v;
              }
            );
          }
        }
        if (before_init_event_result == false) {
          return;
        }
        // 去掉之前的二开js
        formDefinition.customJs = '';
        formDefinition.customJsNew = '';
        _self.formDefinition = formDefinition;
        options.formData.formDefinition = formDefinition;
      }
      var sc = options.success;
      var success = function () {
        if ($.isFunction(sc)) {
          sc.apply(_self, arguments);
        }
        // 记录初始化的表单数据
        if (options.recordInitFormDatas) {
          setTimeout(function () {
            _self._recordInitFormDatas(options.formData);
          }, 500);
        }
      };
      var ec = options.error;
      var error = function () {
        if ($.isFunction(ec)) {
          ec.apply(_self, arguments);
        }
      };
      options.success = success;
      options.error = error;
      var callback = function (dyformDevelopment) {
        var customDyformDevelopment = dyformDevelopment;
        // 包装外部模块引用表单二开扩展
        if (options.dyformDevelopment) {
          var innerDyformDevelopment = function () {
            dyformDevelopment.apply(this, arguments);
          };
          commons.inherit(innerDyformDevelopment, dyformDevelopment, options.dyformDevelopment);
          customDyformDevelopment = innerDyformDevelopment;
        }
        _self.dyformDevelop = new customDyformDevelopment(_self);
        // 方法回调——初始化
        _self.invokeDevelopmentMethod('onInit', _self);
        // 注册事件,TODO 防止外部方法影响解析
        options.events = options.events || {};
        $.extend(options.events, new DyformEventAdapter(_self));
        _self.$dyform = $(element).dyform(options);
        // 初始化结束后
        if (dyformEvents && dyformEvents['afterInit_event']) {
          appContext.eval(dyformEvents['afterInit_event'], this, {
            $dyform: _self.$dyform
          });
        }
      };
      var customJsModules = [],
        customJsModule = formDefinition.customJsModule;
      if (commons.StringUtils.isNotBlank(customJsModule)) {
        customJsModules.push(customJsModule);
      } else {
        customJsModules.push('DyformDevelopment');
      }
      customJsModules = customJsModules.concat($.dyform.parseRequireModules(formDefinition));
      appContext.require(customJsModules, callback);
    },
    // 记录初始化的表单数据
    _recordInitFormDatas: function () {
      var _self = this;
      _self.$dyform.collectFormData(
        function (dyFormData) {
          var formDatas = dyFormData.formDatas || {};
          var initFormDatas = {};
          for (var key in formDatas) {
            var datas = formDatas[key];
            initFormDatas[key] = [];
            for (var i = 0; i < datas.length; i++) {
              initFormDatas[key].push(datas[i]);
            }
          }
          _self.initFormDatas = initFormDatas;
        },
        function () {}
      );
    },
    // 根据字段名称获取字段对象
    getField: function (fieldName, dataUuid) {
      return wrapperField(this.$dyform, this.$dyform.getControl(fieldName, dataUuid));
    },
    /**
     * 获取从表控件
     */
    getSubform: function (subformId) {
      var _self = this;
      var subformUuid = _self.$dyform.getFormUuid(subformId);
      var subform = _self.$dyform.getSubformControl(subformUuid);
      return wrapperSubform(_self.$dyform, subform);
    },
    /**
     * 获取从表数据
     */
    getSubformData: function (subformId, fnSuccessCallback, fnFailCallback) {
      return this.$dyform.getAllRowData({
        id: subformId,
        fnSuccessCallback: fnSuccessCallback,
        fnFailCallback: fnFailCallback
      });
    },
    /**
     * 获取主表数据
     */
    getMainFormData: function (fnSuccessCallback, fnFailCallback) {
      var _this = this;
      this.$dyform.collectFormData(function (formData) {
        var datas = formData.formDatas[formData.formUuid];
        if (datas && datas.length > 0) {
          var data = datas[0];
        } else {
          var data = null;
        }
        fnSuccessCallback.call(_this, data);
      }, fnFailCallback);
    },
    /**
     * 注册公共
     */
    registerFormula: function (formula) {
      $.ControlManager.registFormula(formula);
    },
    /**
     * 打印表单信息
     */
    print: function () {
      var _self = this;
      var fields = _self.formDefinition.fields;
      $.each(fields, function (i, fieldDefinition) {
        var field = _self.getField(fieldDefinition.name);
        if (!field.getCtlName) {
          return;
        }
        console.log(field);
        console.log('fieldId: unknown' + ', fieldName: ' + field.getCtlName());
        console.log('isValueMap: ' + field.isValueMap());
        console.log('fieldValue: ');
        console.log(field.getValue());
        console.log('fieldValueType: ');
        console.log(typeof field.getValue());
      });
    },
    // 调用表单二开回调接口
    invokeDevelopmentMethod: function (method) {
      var _self = this;

      if (_self.dyformDevelop == null) {
        return;
      }

      if ($.isFunction(_self.dyformDevelop[method])) {
        try {
          var args = [];
          for (var i = 1; i < arguments.length; i++) {
            args.push(arguments[i]);
          }
          return _self.dyformDevelop[method].apply(_self.dyformDevelop, args);
        } catch (e) {
          console.error(e);
        }
      } else {
        console.log("cann't invoke method[" + method + '],method not find.');
      }
    },
    isDyformDataChanged: function (newFormData) {
      var _self = this;
      var dyformData = newFormData;
      if (dyformData == null) {
        _self.collectFormData(
          function (formData) {
            dyformData = formData;
          },
          function () {
            throw new Error('表单数据收集失败！');
          }
        );
        while (dyformData == null) {
          continue;
        }
      }
      return _self._isDyformDataChangedWithFormData(dyformData);
      //		 return _self._isDyformDataChangedWithDefinition(dyformData);
      //		 return _self._isDyformDataChangedWithFormDataChangeInfo(dyformData);
    },
    _isDyformDataChangedWithFormData: function (formData) {
      var _self = this;
      var dyformDataChanged = false;
      var dyFormData = formData || {};
      var initFormDatas = _self.initFormDatas || {};
      var newFormDatas = dyFormData.formDatas || {};
      var mainFormData = initFormDatas[dyFormData.formUuid][0];
      var newMainFormData = newFormDatas[dyFormData.formUuid][0];
      // 主表字段
      for (var fieldName in newMainFormData) {
        if ($.inArray(fieldName, systemFields) >= 0) {
          continue;
        }
        var oldValue = mainFormData[fieldName];
        var newValue = newMainFormData[fieldName];
        if (_self._isValueChanged(oldValue, newValue)) {
          dyformDataChanged = true;
        }
      }
      if (dyformDataChanged) {
        return dyformDataChanged;
      }

      // 从表字段
      var subforms = _self.formDefinition.subforms || {};
      $.each(subforms, function (subformName, subformDefinition) {
        var subformDatas = initFormDatas[subformDefinition.formUuid] || [];
        var newSubformDatas = newFormDatas[subformDefinition.formUuid] || [];
        if (_self._isSubformValueChanged(subformDatas, newSubformDatas)) {
          dyformDataChanged = true;
        }
      });
      return dyformDataChanged;
    },
    // 同步检测表单数据是否变更
    _isDyformDataChangedWithDefinition: function (formData) {
      var _self = this;
      var dyformDataChanged = false;
      var dyFormData = formData || {};
      var initFormDatas = _self.initFormDatas || {};
      var formDefinition = _self.formDefinition; //dyFormData.formDefinition || _self.formDefinition[dyFormData.formUuid];
      var fields = formDefinition.fields || {};
      var subforms = formDefinition.subforms || {};
      var mainFormData = initFormDatas[dyFormData.formUuid][0];
      // 主表字段
      $.each(fields, function (fieldName, fildDefinition) {
        var oldValue = mainFormData[fieldName];
        var newValue = null;
        var control = _self.getControl(fieldName);
        if (control != null) {
          if (control.isGetValueByAsyn && control.isGetValueByAsyn()) {
            control.getValue(
              function (val) {
                newValue = val;
              },
              function () {
                throw new Error('附件数据收集失败！');
              }
            );
            while (newValue == null) {
              continue;
            }
          } else {
            newValue = control.getValue();
          }
        }
        if (_self._isValueChanged(oldValue, newValue)) {
          dyformDataChanged = true;
        }
      });
      if (dyformDataChanged) {
        return dyformDataChanged;
      }

      // 从表字段
      $.each(subforms, function (subformName, subformDefinition) {
        var subformDatas = initFormDatas[subformDefinition.formUuid] || [];
        var newSubformDatas = [];
        var subform = _self.getSubform(subformDefinition.outerId);
        if (subform != null) {
          newSubformDatas = subform.getData();
        }
        if (_self._isSubformValueChanged(subformDatas, newSubformDatas)) {
          dyformDataChanged = true;
        }
      });
      return dyformDataChanged;
    },
    _isSubformValueChanged: function (subformDatas, newSubformDatas) {
      var _self = this;
      if (subformDatas.length == 0 && newSubformDatas.length == 0) {
        return false;
      }
      if (subformDatas.length != newSubformDatas.length) {
        return true;
      }
      // 获取表单从表行数据
      var getSubformData = function (uuid, formDatas) {
        for (var i = 0; i < formDatas.length; i++) {
          if (uuid == formDatas[i].uuid) {
            return formDatas[i];
          }
        }
        return null;
      };
      for (var index = 0; index < subformDatas.length; index++) {
        var subformData = subformDatas[index];
        var newSubformData = newSubformDatas[index];
        // var newSubformData = getSubformData(subformData.uuid, newSubformDatas);
        if (newSubformData == null) {
          return true;
        }
        // 顺序比较，uuid不一致则发生顺序变化
        if (subformData.uuid != newSubformData.uuid) {
          return true;
        }
        // 从表行字段比较
        for (var key in newSubformData) {
          if ($.inArray(key, ['id', 'seqNO']) >= 0) {
            continue;
          }
          if (_self._isValueChanged(subformData[key], newSubformData[key])) {
            return true;
          }
        }
      }
      return false;
    },
    _isValueChanged: function (oldV, newV) {
      if (typeof newV == 'object' && typeof oldV == 'object' && newV instanceof Array) {
        // 附件
        if (newV.length == 0 && oldV == null) {
          return false;
        }
        if (oldV instanceof Array && this._objectLength(newV) != this._objectLength(oldV)) {
          return true;
        } else {
          var equal = WellFileUpload.isEqual(newV, oldV);
          if (!equal) {
            // 前后的值不一致
            return true;
          } else {
            // 文件顺序一致性判断
            if (newV != null && oldV != null) {
              for (var i = 0; i < newV.length; i++) {
                if (newV[i].fileID != oldV[i].fileID || newV[i].fileName != oldV[i].fileName) {
                  return true;
                }
              }
            }
          }
        }
      } else if (newV !== oldV) {
        if (!(newV === '' && (oldV === null || oldV === undefined || oldV === ''))) {
          return true;
        }
      }
      return false;
    },
    _objectLength: function (obj) {
      var count = 0;
      for (var i in obj) {
        count++;
      }
      return count;
    },
    _isDyformDataChangedWithFormDataChangeInfo: function (formData) {
      var dyFormData = formData;
      // 添加的数据
      var addedFormDatas = dyFormData.addedFormDatas;
      if (addedFormDatas != null && JSON.stringify(addedFormDatas) != '{}') {
        return true;
      }
      // 删除的数据
      var deletedFormDatas = dyFormData.deletedFormDatas;
      for (var key in deletedFormDatas) {
        var deletedFormData = deletedFormDatas[key];
        if ($.isArray(deletedFormData) && deletedFormData.length > 0) {
          return true;
        }
      }
      // 更新的数据
      var updatedFormDatas = dyFormData.updatedFormDatas;
      for (var key in updatedFormDatas) {
        var updatedFormData = updatedFormDatas[key];
        if (updatedFormData) {
          for (var p in updatedFormData) {
            var updatedFields = updatedFormData[p];
            // 忽略从表排序号
            if ($.isArray(updatedFields) && updatedFields.length > 0) {
              var updateFieldsCount = 0;
              $.each(updatedFields, function (i, item) {
                if ($.inArray(item, ['id', 'seqNO']) < 0) {
                  updateFieldsCount++;
                }
              });
              if (updateFieldsCount) {
                return true;
              }
            }
          }
        }
      }
      return false;
    }
  });
  return DyformExplain;
});

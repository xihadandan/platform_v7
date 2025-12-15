(function ($) {
  var columnProperty = {
    // 控件字段属性
    applyTo: null, // 应用于
    columnName: null, // 字段定义 fieldname
    displayName: null, // 描述名称 descname
    dbDataType: '', // 字段类型 datatype type
    indexed: null, // 是否索引
    showed: null, // 是否界面表格显示
    sorted: null, // 是否排序
    sysType: null, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, // 长度
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null // 只读状态下设置跳转的url
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  var Select = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wselect'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  Select.prototype = {
    constructor: Select
  };

  $.Select = {
    createEditableElem: function () {
      var _self = this;
      if (_self.$editableElem != null) {
        // 创建可编辑框
        return;
      }

      var options = _self.options;
      var ctlName = _self.getCtlName();

      var editableElem = document.createElement('select');
      editableElem.setAttribute('class', _self.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('id', ctlName);
      var inputCss = _self.getTextInputCss();
      var tdWidth = _self.$placeHolder.parents('td').width();
      var width = inputCss.width;
      if (width.indexOf('%') == -1 && parseInt(width) > tdWidth) {
        inputCss.width = tdWidth + 'px';
      }
      $(editableElem).css(inputCss);

      if (options.selectMultiple) {
        $(editableElem).attr('multiple', 'multiple');
      }

      _self.$placeHolder.after($(editableElem));
      // _self.$editableElem = this.$placeHolder.next("." + this.editableClass);

      _self.$editableElem = $(editableElem);
      _self.setDefaultCondition();
      //来源数据仓库
      if (options.optionDataSource === '4') {
        _self.initDataStore();
      }
      _self.reRender();

      _self.initAsWellSelect(options);

      if (options.lazyLoading) {
        _self.lazySetDictOptions(function (optionSet) {
          _self.setOptionSet(optionSet);
          _self.reRender('reRenderOption');
          if (_self.lazySetValue == null) {
            _self.lazySetValue = true;
            _self.setValue(_self.value);
          }
          _self.invoke('afterLazySetOption');
        }, true);
      }

      _self.$editableElem.on('change', function () {
        var $this = $(this);
        var $selectedOpt = $this.find('option:selected');
        var realValue = $this.val();

        if (realValue != null && realValue !== '') {
          _self.setValue(typeof realValue === 'string' ? realValue : realValue.join(';'));
          if (options.columnProperty.realDisplay.placeholder) {
            $('span[fieldname="' + options.columnProperty.realDisplay.placeholder + '"]')
              .text(realValue)
              .show();
          }
          if (realValue.length > 1) {
            var _arr = [];
            $selectedOpt.each(function () {
              var _$this = $(this);
              _arr.push(_$this.data('data'));
            });
            _self.setValueData2FormFields(_arr);
          } else {
            _self.setValueData2FormFields([$selectedOpt.data('data')]);
          }
        } else {
          _self.setValue('');
          if (options.columnProperty.realDisplay.placeholder) {
            $('span[fieldname="' + options.columnProperty.realDisplay.placeholder + '"]')
              .text('')
              .show();
          }
          _self.setValueData2FormFields([]);
        }
      });

      if (options.optionDataAutoSet) {
        var $dyform = DyformFacade.get$dyform();
        $dyform.bind2Dyform2('beforeSetData', function () {
          // 表单初始化后调用
          _self.getDynamicOptionSet();
        });
      }
    },

    setDefaultCondition: function () {
      var fieldDef = this.getFieldParams();
      this.conditions = [
        {
          sql: fieldDef.defaultCondition
        }
      ];
    },

    setValueData2FormFields: function (selectDatas) {
      var fieldDef = this.getFieldParams();
      var mapping = fieldDef.dataSourceFieldMapping;
      if (mapping == null || typeof mapping === 'undefined' || mapping.length == 0) {
        return;
      }
      var values = {};
      var formData = null;
      this.dyform$Context().collectFormData(function (result) {
        formData = result.formDatas[result.formUuid][0];
      }, $.noop);
      for (var i = 0, len = mapping.length; i < len; i++) {
        if (selectDatas.length) {
          for (var j = 0, jlen = selectDatas.length; j < jlen; j++) {
            var data = selectDatas[j];
            if (data && mapping) {
              var value = data[mapping[i].dataSourceColumn];
              if (mapping[i].formater) {
                //只支持格式化
                var returnParams = appContext.resolveParams(
                  {
                    result: mapping[i].formater
                  },
                  {
                    value: data[mapping[i].dataSourceColumn],
                    dataSourceData: data,
                    formData: formData
                  }
                );
                value = returnParams.result;
              }
              if (!values[mapping[i].formField]) {
                values[mapping[i].formField] = [];
              }
              values[mapping[i].formField].push(value);
            }
          }
        } else {
          values[mapping[i].formField] = [];
        }
      }

      if (!$.isEmptyObject(values)) {
        for (var k in values) {
          var control = $.ControlManager.getCtl(this.getContronId(k));
          if (typeof control == 'undefined' || control == null) {
            continue;
          }
          if (control.isCkeditor()) {
            control.setCkValue(values[k].join(';'));
          } else {
            control.setValue(values[k].join(';'));
          }
        }
      }
    },
    //获取动态备选项
    getDynamicOptionSet: function () {
      var options = this.options;
      if (options.optionDataSource == '1') {
        this.reloadDataOptions(options);
      } else if (options.optionDataSource == '2') {
        this.reloadDictOptions(options);
      } else if (options.optionDataSource == '4') {
        this.reloadSourceOptions(options);
      }
    },
    // 重新加载常量的备选项
    reloadDataOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke(
          'setValue',
          _.debounce(
            function (value) {
              var optionSet = [];
              if ($.trim(value).length > 0) {
                optionSet = self.getConstantOptionSet(options.dataOptsList, value, []);
              }
              self.setOptionSet(self.arrayRepeat(optionSet));
              self.reRender('reRenderOption');
              self.setValue(self.getValue());
            },
            500,
            {
              leading: false,
              trailing: true
            }
          )
        );
      }
    },

    // 重新加载数据字典的备选项
    reloadDictOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke('setValue', function (value) {
          if (value == null) {
            value = '';
          } else if ($.isArray(value)) {
            value = value.join(';');
          }
          var dictType = '';
          options.dictUuid = '';
          options.dictCode = '';
          var optionSet = [];
          var data = [];
          if (relateField.options.dictCode && relateField.options.dictCode != '') {
            dictType = relateField.options.dictCode.split(':')[0]; // 类型为关联字段的字典
          }

          if (value != '' && value.split(';').length == 1) {
            $.ajax({
              type: 'get',
              url: ctx + '/basicdata/datadict/type/' + value,
              dataType: 'json',
              async: false,
              success: function (result) {
                if (result.data && result.data.uuid) {
                  options.dictUuid = result.data.uuid;
                  options.dictCode = $.isArray(value) ? value[0] : value;
                }
              }
            });

            data = self.getNewOptionSet(dictType, optionSet, value); //  获取备选项的值
            $.each(data, function (index, item) {
              optionSet.push({
                data: item.uuid,
                name: item.name,
                value: item.code
              });
            });
          }

          self.setOptionSet(optionSet);
          self.reRender();

          self.setValue(self.getValue());
        });
      }
    },

    // 重新加载数据仓库的备选项
    reloadSourceOptions: function (options) {
      var self = this;
      var relateFieldList = options.relateField.split(';');
      var dataList = {};
      for (var i = 0; i < relateFieldList.length; i++) {
        (function (i) {
          var relateField = DyformFacade.get$dyform().getControl(relateFieldList[i]);
          if (relateField != undefined) {
            dataList[relateFieldList[i]] = relateField.getValue();
            relateField.proxiedAfterInvoke(
              'setValue',
              _.debounce(
                function (value) {
                  value = value || '';
                  dataList[relateFieldList[i]] = value;
                  self.Datastore.clearParams();
                  for (var k in dataList) {
                    self.Datastore.addParam(k.toLocaleLowerCase(), dataList[k]);
                  }
                  self.Datastore.load();
                },
                500,
                {
                  leading: false,
                  trailing: true
                }
              )
            );
          }
        })(i);
      }
    },

    // 重新设置数据字典dictType
    reSetDictCode: function (type) {
      this.options.dictCode = type;
    },

    isSearchable: function () {
      return this.options.searchable;
    },

    initAsSelect2: function (options) {
      var _this = this;
      // window.setTimeout(function(){
      _this.$editableElem.select2();
      var css = _this.getTextInputCss();
      var tdWidth = _this.$placeHolder.parents('td').width();
      var width = css.width;
      if (width.indexOf('%') == -1 && parseInt(width) > tdWidth) {
        css.width = tdWidth + 'px';
      }
      _this.$editableElem.select2(css);
      if (!css.width || $.trim(css.width).length === 0) {
        _this.$editableElem.select2({
          width: '100%'
        });
      }
      _this.$editableElem.hide();
      _this.$select2Container = _this.$editableElem.prev();
      // _this.isSelect2 = true;
      // }, 500);

      // _this.$editableElem.focus = function () {
      //     _this.$editableElem.select2("focus");
      // }
      // _this.$editableElem.click = function () {
      //     _this.$editableElem.select2("click");
      // }
    },

    initAsWellSelect: function () {
      var _self = this;
      var ctrName = _self.getCtlName();
      var options = _self.options;
      if (!_self.$editableElem) {
        _self.createEditableElem();
        _self.initInputEvents();
        if (_self.isShowAsLabel()) {
          _self.hideEditableElem();
        }
        return;
      }
      var width = options.commonProperty.ctlWidth;
      var tdWidth = _self.$placeHolder.parents('td').width();
      if (width.indexOf('%') == -1 && parseInt(width) > tdWidth) {
        width = tdWidth + 'px';
      }
      var clearAll = options.clearAll == true || options.clearAll == undefined ? true : false;
      if (!options.selectMultiple) {
        var fieldCheckRules = options.commonProperty.fieldCheckRules;

        var rIndex = _.findIndex(fieldCheckRules, function (o) {
          return o.value == '1';
        });
        if (rIndex > -1) {
          clearAll = false;
        }
      }

      _self.$editableElem.wellSelect('destroy').wellSelect({
        editableClass: _self.editableClass,
        valueField: ctrName,
        labelField: '_' + ctrName,
        ctlWidth: width,
        searchable: options.searchable,
        multiple: options.selectMultiple,
        maxSelectNum: options.maxSelectionLength,
        chooseAll: options.selectCheckAll,
        source: 'wSelect',
        JDS: JDS,
        dictUuid: options.dictUuid || '',
        clearAll: clearAll,
        custom_more:
          options.dictCode != '' && options.showRightBtn && (options.dicCodeAddBtn || options.dicCodeMoveUp || options.dicCodeMoveDown),
        customEvent: {
          add: options.dictCode != '' && options.dicCodeAddBtn,
          moveUp: options.dicCodeMoveUp,
          moveDown: options.dicCodeMoveDown,
          delete: options.dicCodeDelBtn
        },
        beforeOpen: options.reloadOption
          ? function (obj, callback) {
              _self.reloadOptionSet();
            }
          : null
      });
      _self.$editableElem.hide();
      _self.$select2Container = _self.$editableElem.prev();
      if (_self.isShowAsLabel()) {
        _self.hideEditableElem();
      }
      _self.$editableElem.trigger('change');
    },

    /**
     * @Deprecated
     * 判断是否为[{value:"1",name:"笔记本"},{}]格式 add by wujx 20160803
     *
     * @param optionSet
     * @returns {Boolean}
     */
    isValueNameFormater: function (optionSet) {
      if (optionSet instanceof Array) {
        var optionSetArray = optionSet;
        for (var i = 0; i < optionSetArray.length; i++) {
          var optionSet = optionSetArray[i];
          if (!(optionSet.hasOwnProperty('name') && optionSet.hasOwnProperty('value'))) {
            return false;
          }
        }
        return true;
      } else {
        if (optionSet.hasOwnProperty('name') && optionSet.hasOwnProperty('value')) {
          return true;
        } else {
          return false;
        }
      }
    },

    reRender: function (type) {
      var optionSet = this.getOptionSet(this.options);
      this.appendOption2Select(optionSet);
      this.setValue2EditableElem();
      if (type && type === 'reRenderOption') {
        this.$editableElem.wellSelect('reRenderOption');
      } else {
        this.initAsWellSelect();
      }
    },

    //刷新，重新获取选项数据
    refresh: function (cb) {
      this.reloadOptionSet(cb);
      return this;
    },

    // 重新设置是否动态设置备选项和关联字段,二开使用
    reSetRelatedOptions: function (options) {
      if (options == undefined) {
        return;
      }
      var ctlOptions = this.options;
      ctlOptions.optionDataAutoSet = options.optionDataAutoSet == undefined ? options.optionDataAutoSet : ctlOptions.optionDataAutoSet;
      if (options.optionDataAutoSet) {
        ctlOptions.relateField = options.relateField;
        if (ctlOptions.optionDataSource == '1') {
          ctlOptions.dataOptsList = options.dataOptsList;
        }
        this.getDynamicOptionSet();
      }
    },

    //增加数据仓库参数
    addDsParams: function (key, value) {
      this.Datastore.addParam(key, value);
    },
    //增加数据仓库条件
    addDsConditions: function (criterion) {
      var _self = this;
      if ($.isArray(criterion)) {
        _self.conditions = _self.conditions.concat(criterion);
      } else {
        _self.conditions.push(criterion);
      }
      $.extend(_self.Datastore.options, {
        defaultCriterions: _self.resetDsDefaultConditions()
      });
      return _self;
    },

    //清除数据仓库条件
    clearDsConditions: function () {
      this.conditions = [];
      return this;
    },

    //重置数据仓库默认条件
    resetDsDefaultConditions: function () {
      var _self = this;
      var defaultConditions = [];
      var formData = {};
      _self.dyform$Context().collectFormData(function (result) {
        formData = result.formDatas[result.formUuid] && result.formDatas[result.formUuid][0];
      }, $.noop);
      for (var i = 0, len = _self.conditions.length; i < len; i++) {
        var criterion = _self.conditions[i];
        if ($.isFunction(criterion)) {
          var ret = criterion.call(_self);
          if (ret && !ret.sql) {
            criterion = {
              sql: ret
            };
          } else {
            criterion = ret;
          }
        } else {
          criterion = $.extend({}, criterion);
          //支持sql语句解析表单字段值
          if (criterion.sql) {
            var returnParams = appContext.resolveParams(
              {
                result: criterion.sql
              },
              formData
            );
            criterion.sql = returnParams.result;
          }
          if (criterion.value) {
            var returnParams = appContext.resolveParams(
              {
                result: criterion.value
              },
              formData
            );
            criterion.value = returnParams.result;
          }
        }
        if ($.trim(criterion.sql).length > 0 || $.trim(criterion.value).length > 0) {
          defaultConditions.push(criterion);
        }
      }
      return defaultConditions;
    },

    initDataStore: function () {
      var _self = this;
      var fieldDef = _self.getFieldParams();
      var DataStore = require('dataStoreBase');
      _self.Datastore = new DataStore({
        dataStoreId: fieldDef.dataSourceId,
        receiver: _self,
        defaultCriterions: _self.resetDsDefaultConditions(),
        pageSize: 1000,
        onDataChange: function (data, totalCount, params, getDefinitionJson) {
          _self.options.optionSet = _self.buildOptionSet(data);
          _self.reRender('reRenderOption');
          _self.setValue(_self.getValue());
          if (params && params.afterReRenderOptionCb && $.isFunction(params.afterReRenderOptionCb)) {
            params.afterReRenderOptionCb();
          }
        },
        error: function (jqXHR) {
          jqXHR.responseJSON && console.log(jqXHR.responseJSON.msg);
        }
      });
      var $dyform = DyformFacade.get$dyform();
      $dyform.bind2Dyform2('beforeSetData', function () {
        // 表单初始化后调用
        _self.Datastore.load();
      });
    },

    reloadOptionSet: function (afterReRenderOptionCb) {
      var _self = this;
      if (this.options.optionDataSource == '4') {
        var params = {};
        if (afterReRenderOptionCb) {
          params.afterReRenderOptionCb = afterReRenderOptionCb;
        }
        $.extend(_self.Datastore.options, {
          defaultCriterions: _self.resetDsDefaultConditions()
        });
        _self.Datastore.load([], params);
      } else if (this.options.optionDataSource == '2') {
        this.reRenderDictOption(this.options.dictCode, '');
      } else if (this.options.optionDataSource == '1') {
      }
    },

    buildOptionSet: function (dataSourceData) {
      var optionSet = [];
      var _self = this;
      var fieldDef = _self.getFieldParams();
      for (var i = 0, len = dataSourceData.length; i < len; i++) {
        optionSet.push({
          value: dataSourceData[i][fieldDef.dataSourceFieldName],
          name: dataSourceData[i][fieldDef.dataSourceDisplayName],
          data: dataSourceData[i]
        });
      }
      return optionSet;
    },

    appendOption2Select: function (optionSet) {
      if (!this.$editableElem) {
        return false;
      }
      var _oVal = this.getValue();
      this.$editableElem.empty();
      var $options = [];
      // $options.push($('<option>'));
      if (this.isValueNameFormater(optionSet)) {
        var optionArray = new Array();
        if (optionSet instanceof Array) {
          optionArray = optionSet;
        } else {
          optionArray.push(optionSet);
        }
        for (var i = 0; i < optionArray.length; i++) {
          var option = optionArray[i];
          var value = option.value;
          var name = option.name;
          if ($.inArray(value, this.options.hiddenValues) < 0) {
            var $opt = $('<option>', {
              value: value
            }).text(name);
            $opt.data('data', option.data || option);
            $options.push($opt);
          }
        }
      } else {
        for (var attribute in optionSet) {
          if ($.inArray(attribute, this.options.hiddenValues) < 0) {
            var $opt = $('<option>', {
              value: attribute
            }).text(optionSet[attribute]);
            $opt.data('data', optionSet[attribute].data || optionSet[attribute]);
            $options.push($opt);
          }
        }
      }
      this.$editableElem.append($options);
      if (_oVal) {
        if (typeof _oVal !== 'string') {
          _oVal = _oVal.toString();
        }
        this.value = _oVal;
        this.$editableElem.val(_oVal.split(';'));
      }
    },

    /**
     * 隐藏选项
     *
     * @param values
     *            要隐藏的option对应value组成的数组
     */
    hideOptions: function (values) {
      var self = this;
      self.options.hiddenValues = $.unique($.merge(self.options.hiddenValues || [], values));
      self.reRender();
    },

    /**
     * 隐藏选项
     *
     * @param value
     *            要隐藏的option对应value
     */
    hideOption: function (value) {
      var values = [];
      values.push(value);
      this.hideOptions(values);
    },

    setOptionSet: function (optionSet) {
      if (!this.isValueNameFormater(optionSet)) {
        return $.wRadioCheckBoxCommonMethod.setOptionSet.call(this, optionSet);
      } else {
        this.options.optionSet = optionSet;
        // this.options.optionDataSource = dyDataSourceType.custom; // 用户自定义
        return true;
      }
    },

    /**
     *
     * @param options
     * @param isValueNameFormater
     *            是否返回[{value:"1",name:"笔记本"},{}]格式
     * @returns {___anonymous5944_5945}
     */
    getOptionSet: function (options) {
      options = options || this.options;
      //如果是array格式，则返回array,如果是map格式，直接返回
      //modify by zyguo 2017-03-17
      if (this.isValueNameFormater(options.optionSet)) {
        return options.optionSet;
      } else {
        var optionSet = $.wRadioCheckBoxCommonMethod.getOptionSet.call(this, options);
        return optionSet;
      }
    },

    getValueMapInOptionSet: function (value) {
      if ($.trim(value).length == 0) {
        return '';
      }

      var valueMap = {};

      var optionSet = {};

      optionSet = this.getOptionSet(this.options);

      // modify by wujx 20160803 begin
      if (this.isValueNameFormater(optionSet)) {
        var optionArray = new Array();
        if (optionSet instanceof Array) {
          optionArray = optionSet;
        } else {
          optionArray.push(optionSet);
        }
        for (var i = 0; i < optionArray.length; i++) {
          var option = optionArray[i];
          var valueO = option.value;
          var nameO = option.name;
          if (value == valueO) {
            valueMap[value] = nameO;
          } else if (value === i) {
            /*动态数据源时选择默认选择第几个选项*/
            valueMap[valueO] = nameO;
          }
        }
      } else {
        // modify by wujx 20160803 end
        for (attrbute in optionSet) {
          if (attrbute == value) {
            valueMap[attrbute] = optionSet[attrbute];
          }
        }
      }

      return JSON.cStringify(valueMap);
    },

    /**
     * 设置默认值
     *
     * @param defaultValue
     */
    setDefaultValue: function (defaultValue) {
      // 加空判断
      if ($.trim(defaultValue).length == 0) {
        return;
      }
      var self = this;
      self.setValue(defaultValue);
    },

    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null) {
        return;
      }
      if (self.options.lazyLoading && self.options.optionSet.length == 0) {
        self.lazySetDictOptions(function (optionSet) {
          self.setOptionSet(optionSet);
          if (self.lazySetValue == null) {
            self.lazySetValue = true;
            self.setValue(self.value);
          }
          self.invoke('afterLazySetOption');
        }, true);
      }
      self.$labelElem.html(self.getDisplayValue());
      self.$labelElem.attr('title', self.getDisplayValue());
      self.$labelElem.siblings('.form-field-tag-wrap').remove();
      if (self.isShowAsLabel()) {
        self.renderFieldTag();
      }
    },

    /* 设置到可编辑元素中 */
    setValue2EditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return;
      }
      var value = self.value,
        valueObj = {};
      if ($.trim(value).length == 0) {
        if (self.isSearchable()) {
          self.$editableElem.val('');
        } else {
          self.$editableElem.val('');
        }
        return;
      }
      if (typeof value === 'string') {
        value = value.split(self.getSeparator());
      } else if (false === $.isArray(value)) {
        value = [value];
      }
      self.$editableElem.wellSelect('val', value);
      if (self.isSearchable()) {
        self.$editableElem.hide();
      }
    },

    // set............................................................//

    // 设值,值为真实值
    setValue: function (value) {
      if (typeof value == 'undefined' || value == null) {
        return;
      }
      var self = this;
      if ($.trim(value).length == 0) {
        if (self.$editableElem) {
          self.$editableElem.wellSelect('val', '');
        }
        // $.wControlInterface.setValue.call(self, "");
      }
      $.wControlInterface.setValue.call(self, value);
    },

    hideEditableElem: function () {
      $.wControlInterface.hideEditableElem.call(this);
      if (this.$select2Container) {
        this.$select2Container.hide();
      }
    },

    showEditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        self.createEditableElem();
        self.initInputEvents();
      }
      if (self.$select2Container) {
        self.$select2Container.show();
      }
    },

    isValueMap: function () {
      return true;
    },

    setEnable: function (isenable) {
      if (this.isShowAsLabel()) {
        return;
      }
      this.render();
      if (isenable) {
        this.options.columnProperty.showType = dyshowType.edit;
        this.$editableElem.removeAttr('disabled');
        this.$editableElem.wellSelect('disable', false);
      } else {
        this.options.columnProperty.showType = dyshowType.disabled;
        this.$editableElem.attr('disabled', 'disabled');
        this.$editableElem.wellSelect('disable', true);
        this.removeValidate();
      }
    },

    setReadOnly: function (isReadOnly) {
      //if (this.isShowAsLabel() && !this.isInSubform()) {
      //return;
      //}
      this.render(true);
      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        this.$editableElem.attr('readonly', 'readonly');
        this.$editableElem.wellSelect('readonly', true);
        this.removeValidate();
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.$editableElem.removeAttr('readonly');
        this.$editableElem.wellSelect('readonly', false);
      }
      this.render();
    }
  };

  $.fn.wselect = function (option) {
    var method = false;
    var args = null;
    if (arguments.length === 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option === 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this);
        data = $this.data('wselect');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wselect'),
      options = typeof option === 'object' && option;
    if (!data) {
      data = new Select($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.wSelectCommonMethod);
      $.extend(data, $.Select);
      $this.data('wselect', data);
      if (options.columnProperty.uninit) {
        return data;
      }
      data.init();
    }
    if (typeof option === 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wselect.Constructor = Select;

  $.fn.wselect.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    isShowAsLabel: false,
    isHide: false, // 是否隐藏
    disabled: false,
    optionDataSource: '1', // 备选项来源1:常量,2:字段
    optionSet: [],
    dictCode: null,
    searchable: false
    /*,// 该功能通过select2实现，而select2与正文的dll控件有冲突，所以该属性的值为true请不要使用正文控件
                hiddenValues : []*/
  };
})(jQuery);

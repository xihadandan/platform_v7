// add by wujx 20160719 下拉选项分组控件
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

  /*
   * SELECTOPTGROUP CLASS DEFINITION ======================
   */
  var ComboSelect = function ($placeHolder /* 占位符 */, options) {
    // this.$element = $(element);
    this.options = $.extend({}, $.fn['wcomboSelect'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  ComboSelect.prototype = {
    constructor: ComboSelect
  };

  $.ComboSelect = {
    $checkedAllElem: null,

    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }

      var options = this.options;
      var ctlName = this.getCtlName();
      var editableElem = document.createElement('select');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      $(editableElem).css(this.getTextInputCss());

      this.$placeHolder.after($(editableElem));

      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);

      if (options.optionDataSource === '4') {
        this.initDataStore();
      }

      this.initMultiSelectSetting();

      this.reRender();

      var _this = this;
      if (_this.isSelect2Ctrl()) {
        // _this.initAsSelect2();
        _this.initAsWellSelect(options);
      }

      this.$editableElem.change(function () {
        if (_this.isMultiSelect()) {
          _this.switchAsCheckedAll(); //全选/清空复选框， 切换为选择全部
        }

        var $selectedOpts = $(this).find('option:selected');
        var realValues = new Array();
        var selectDatas = [];
        $.each($selectedOpts, function (i, selectedOpt) {
          var $selectedOpt = $(selectedOpt);
          var optVal = $selectedOpt.val();
          if (optVal != null && optVal != '') {
            realValues.push(optVal);
            selectDatas.push($selectedOpt.data('data'));
          }
        });
        _this.setValue(realValues);
        if (selectDatas.length > 0) {
          _this.setValueData2FormFields(selectDatas);
        }
      });

      if (options.optionDataAutoSet) {
        var $dyform = DyformFacade.get$dyform();
        $dyform.bind2Dyform2('beforeSetData', function () {
          _this.getDynamicOptionSet();
        });
      }

      if (options.lazyLoading) {
        _this.lazySetDictOptions(function (optionSet) {
          _this.setOptionSet(optionSet);
          _this.reRender('reRenderOption');
          _this.setValue(_this.value);
        }, true);
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
        this.initDataStore();
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
              self.reRender();
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
        relateField.proxiedAfterInvoke(
          'setValue',
          _.debounce(
            function (value) {
              if (value == null) {
                value = '';
              } else if ($.isArray(value)) {
                value = value.join(';');
              }
              var dictType = '';
              var optionSet = [];
              var data = [];
              if (relateField.options.dictCode && relateField.options.dictCode != '') {
                dictType = relateField.options.dictCode.split(':')[0]; // 类型为关联字段的字典
              }

              if (value != '' && value.split(';').length == 1) {
                data = self.getNewOptionSet(dictType, optionSet, value); //  获取备选项的值
              }

              self.setOptionSet(data);
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

    getNewOptionSet: function (dictType, optionSet, value) {
      var self = this;
      var newOptionSet = optionSet;
      if ($.isArray(value)) {
        $.each(value, function (index, item) {
          newOptionSet = self.getNewOptionSet(dictType, newOptionSet, item);
        });
      } else {
        if (!dictType) {
          dictType = value;
        }
        newOptionSet = self.getDictOptionSet(dictType, newOptionSet);
      }
      return newOptionSet;
    },

    getDictOptionSet: function (dictType, optionSet) {
      var newOptionSet = optionSet;
      var self = this;
      var fieldName = self.options.columnProperty.columnName;
      var formUuid = self.getFormUuid();
      $.ajax({
        url: ctx + '/pt/dyform/definition/getFieldDictionaryOptionSet',
        dataType: 'json',
        type: 'post',
        data: {
          fieldName: fieldName,
          formUuid: formUuid,
          paramsObj: JSON.stringify({
            dictCode: dictType
          })
        },
        async: false,
        success: function (res) {
          newOptionSet = res;
        }
      });
      return newOptionSet;
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
                    self.Datastore.addParam(k, dataList[k]);
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

    initMultiSelectSetting: function () {
      if (this.isMultiSelect()) {
        this.$editableElem.attr('multiple', 'multiple');
      }
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

    isSearchable: function () {
      return this.options.searchable == true;
    },

    isMultiSelect: function () {
      return this.options.multiSelect == true;
    },

    isSelect2Ctrl: function () {
      return true;
      // return (this.options.searchable == true || this.options.multiSelect == true);
    },

    switchCheckedAll: function () {
      if (this.$checkedAllElem == null) {
        return;
      }
      if (this.$checkedAllElem.is('.virtual-checkbox-nochecked')) {
        this.switchAsCleanAll();
      } else {
        this.switchAsCheckedAll();
      }
    },

    switchAsCheckedAll: function () {
      if (this.$checkedAllElem == null) {
        return;
      }
      this.$checkedAllElem.addClass('virtual-checkbox-nochecked');
      this.$checkedAllElem.removeClass('virtual-checkbox-checked');
      this.$checkedAllElem.attr('title', '全选');
    },

    switchAsCleanAll: function () {
      if (this.$checkedAllElem == null) {
        return;
      }

      this.$checkedAllElem.removeClass('virtual-checkbox-nochecked');
      this.$checkedAllElem.addClass('virtual-checkbox-checked');
      this.$checkedAllElem.attr('title', '清空');
    },

    initAsSelect2: function () {
      var _this = this;
      if (_this.isMultiSelect()) {
        this.$editableElem.after("<span class='virtual-checkbox-nochecked' title='全选'></span>");
        this.$editableElem.next().click(function () {
          if ($(this).is('.virtual-checkbox-nochecked')) {
            //全选
            _this.selectAll();
          } else {
            //清空
            _this.clean();
          }
          _this.switchCheckedAll();
        });
        this.$checkedAllElem = this.$editableElem.next();
      }

      // window.setTimeout(function(){
      _this.$editableElem.select2();
      var css = this.getTextInputCss();
      _this.$editableElem.select2(css);
      if (!css.width || $.trim(css.width).length == 0) {
        _this.$editableElem.select2({
          allowClear: true,
          width: '100%',
          minimumResultsForSearch: this.isSearchable() || -1,
          // multiple : true,
          formatNoMatches: '没有找到匹配项'
        });
      }
      _this.$select2Container = _this.$editableElem.prev();
      // _this.isSelect2 = true;
      // }, 500);

      this.$editableElem.focus = function () {
        _this.$editableElem.select2('focus');
      };
      this.$editableElem.click = function () {
        _this.$editableElem.select2('click');
      };
    },

    initAsWellSelect: function (options) {
      var _this = this;
      var ctrName = _this.getCtlName();
      if (!options) {
        options = _this.options;
      }
      var clearAll = options.clearAll == true || options.clearAll == undefined ? true : false;
      if (!options.multiSelect) {
        var fieldCheckRules = options.commonProperty.fieldCheckRules;

        var rIndex = _.findIndex(fieldCheckRules, function (o) {
          return o.value == '1';
        });
        if (rIndex > -1) {
          clearAll = false;
        }
      }
      _this.$editableElem.wellSelect({
        valueField: ctrName,
        labelField: '_' + ctrName,
        ctlWidth: options.commonProperty.ctlWidth,
        searchable: options.searchable,
        multiple: options.multiSelect,
        maxSelectNum: options.maxSelectionLength,
        chooseAll: options.selectCheckAll,
        source: 'wSelect',
        clearAll: clearAll
      });

      _this.$editableElem.on('focus', function () {
        console.log('xxx');
      });
      // var css = this.getTextInputCss();
      // _this.$editableElem.select2(css);
      // if (!css.width || $.trim(css.width).length == 0) {
      //     _this.$editableElem.select2({
      //         allowClear: true,
      //         width: "100%",
      //         minimumResultsForSearch: this.isSearchable() || -1,
      //         // multiple : true,
      //         formatNoMatches: "没有找到匹配项"
      //     });
      // }
      _this.$select2Container = _this.$editableElem.prev();
    },

    reRender: function () {
      var optionSet = this.getOptionSet(this.options);
      this.appendOption2Select(optionSet);
      this.setValue2EditableElem();
      this.initAsWellSelect();
    },

    /**
     * 刷新，重新获取选项数据
     * afterReRenderOptionCb: 重新渲染完数据后回调函数
     */
    refresh: function (afterReRenderOptionCb) {
      this.reloadOptionSet(afterReRenderOptionCb);
      return this;
    },

    //增加数据仓库参数
    addDsParams: function (key, value) {
      this.Datastore.addParam(key, value);
    },
    //增加数据仓库条件
    addDsConditions: function (criterion) {
      var _self = this;
      _self.conditions = _self.conditions || [];
      if ($.isArray(criterion)) {
        _self.conditions = _self.conditions.concat(criterion);
      } else {
        _self.conditions.push(criterion);
      }
      _self.Datastore.options.defaultCriterions = _self.resetDsDefaultConditions();
      return _self;
    },

    //清楚数据仓库条件
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
        pageSize: 1000,
        onDataChange: function (data, totalCount, params, getDefinitionJson) {
          _self.options.optionSet = _self.buildOptionSet(arguments[0]);
          _self.reRender('reRenderOption');
          _self.setValue(_self.getValue());
          if (params && $.isFunction(params.afterReRenderOptionCb)) {
            params.afterReRenderOptionCb();
          }
        }
      });
    },

    reloadOptionSet: function (afterReRenderOptionCb) {
      var _self = this;
      var params = {};
      if (afterReRenderOptionCb) {
        params.afterReRenderOptionCb = afterReRenderOptionCb;
      }
      _self.Datastore.load([], params);
    },

    buildOptionSet: function (dataSourceData) {
      var optionSet = [];
      var _this = this;
      var fieldDef = this.getFieldParams();
      for (var i = 0, len = dataSourceData.length; i < len; i++) {
        var optionData = {
          value: dataSourceData[i][fieldDef.dataSourceFieldName],
          name: dataSourceData[i][fieldDef.dataSourceDisplayName],
          data: dataSourceData[i]
        };
        if (fieldDef.dataSourceGroup && !$.isEmptyObject(dataSourceData[i][fieldDef.dataSourceGroup])) {
          optionData.group = dataSourceData[i][fieldDef.dataSourceGroup];
        }
        optionSet.push(optionData);
      }
      return optionSet;
    },

    appendOption2Select: function (optionSet) {
      this.$editableElem.empty();
      this.$editableElem.append($('<option>').text(''));
      var $options = [];
      if (this.isValueNameFormater(optionSet)) {
        var optionArray = new Array();
        if (optionSet instanceof Array) {
          optionArray = optionSet;
        } else {
          optionArray.push(optionSet);
        }
        var groupOptions = _.groupBy(optionArray, function (o) {
          var groupValue = o['group'];
          return !$.isEmptyObject(groupValue) ? groupValue : '_UNKNOWN_GRP';
        });

        var create$options = function (o) {
          var $opt = $('<option>', {
            value: o.value
          }).text(o.name);
          $opt.data('data', o.data);
          return $opt;
        };
        //没有分组情况下，就不用分组了
        var noGroup = Object.keys(groupOptions).length == 1 && Object.keys(groupOptions)[0] == '_UNKNOWN_GRP';
        for (var g in groupOptions) {
          var items = groupOptions[g];
          if (noGroup) {
            for (var i = 0, len = items.length; i < len; i++) {
              $options.push(create$options(items[i]));
            }
          } else {
            var $optionGrp = $('<optgroup>', {
              label: g === '_UNKNOWN_GRP' ? '其他' : g
            });
            for (var i = 0, len = items.length; i < len; i++) {
              $optionGrp.append(create$options(items[i]));
            }
            $options.push($optionGrp);
          }
        }
      }
      this.$editableElem.append($options);
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

    /**
     * 设置选项
     */
    setOptionSet: function (optionSet) {
      try {
        if (typeof optionSet == 'string') {
          optionSet = eval('(' + optionSet + ')');
        }
        var optionSetArray = new Array();
        if (optionSet instanceof Array) {
          optionSetArray = optionSet;
        } else {
          optionSetArray.push(optionSet);
        }
        for (var i = 0; i < optionSetArray.length; i++) {
          var optionSet = optionSetArray[i];
          if (!(optionSet.hasOwnProperty('name') && optionSet.hasOwnProperty('value'))) {
            throw new Error();
          }
        }
      } catch (e) {
        console.error(
          this.getCtlName() +
            ':非法的自定义选项，格式参考如下：' +
            '[{value:"notebook",name:"笔记本",group:"电脑"},{value:"desktop",name:"台式",group:"电脑"}]（其中group属性非必须）！'
        );
        return false;
      }

      this.options.optionSet = optionSetArray;
      this.options.optionDataSource = dyDataSourceType.custom; // 用户自定义
      this.reRender(); // 重新渲染完，值会被清空，需再次设值
      return true;
    },

    getOptionSet: function (options) {
      var optionSet = {};
      var selectobj = options.optionSet;
      if (typeof selectobj == 'undefined' || selectobj == null || (typeof selectobj == 'string' && $.trim(selectobj).length == 0)) {
        console.error('a json parameter is null , used to initialize checkbox options ');
        return;
      }
      optionSet = selectobj;

      if (typeof optionSet == 'object') {
        // 为了兼容IE8,先通过JSON.cStringify进行排序后，再转换成对象
        optionSet = eval('(' + JSON.cStringify(optionSet) + ')');
      } else if (typeof optionSet == 'string') {
        try {
          optionSet = eval('(' + optionSet + ')');
        } catch (e) {
          console.error(optionSet + ' -->not json format ');
          return;
        }
      }
      var optionSetArray = new Array();
      if (optionSet instanceof Array) {
        optionSetArray = optionSet;
      } else {
        optionSetArray.push(optionSet);
      }
      return optionSetArray;
    },

    getValueMapInOptionSet: function (value) {
      if (typeof value == 'undefined' || value.length == 0) {
        return '';
      }

      var valueArray = new Array();
      if (!(value instanceof Array)) {
        valueArray.push(value);
      } else {
        valueArray = value;
      }

      var optionSet = {};
      optionSet = this.getOptionSet(this.options);

      var optionSetArray = new Array();
      for (var i = 0; i < valueArray.length; i++) {
        var value = valueArray[i];
        for (var j in optionSet) {
          var valueOS = optionSet[j]['value'];
          if (valueOS == value) {
            optionSetArray.push(optionSet[j]);
          } else if (value === i) {
            /*动态数据源时选择默认选择第几个选项*/
            optionSetArray.push(optionSet[j]);
          }
        }
      }

      return JSON.cStringify(optionSetArray);
    },

    selectAll: function () {
      var optionSet = {};
      optionSet = this.getOptionSet(this.options);

      var value = [];
      for (var i in optionSet) {
        value.push(optionSet[i].value);
      }

      this.setValue(value);
    },

    /**
     * 设置默认值
     *
     * @param defaultValue
     */
    setDefaultValue: function (defaultValue) {
      // 加空判断
      if ($.trim(defaultValue).length == 0 || defaultValue == '{}') {
        defaultValue = '';
      }
      self.value = defaultValue;
    },

    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var _this = this;
      if (this.$labelElem == null) {
        return;
      }
      if (this.options.lazyLoading && this.options.optionSet.length == 0) {
        _this.lazySetDictOptions(function (optionSet) {
          _this.options.optionSet = optionSet;
          _this.setValue(_this.value);
        }, true);
      }

      this.$labelElem.html(this.getDisplayValue());
      this.$labelElem.attr('title', this.getDisplayValue());
    },

    /* 设置到可编辑元素中 */
    setValue2EditableElem: function () {
      var self = this;
      if (this.$editableElem == null) {
        return;
      }
      var value = self.value,
        valueObj = [];
      if ($.trim(value).length == 0 || value == '{}') {
        if (self.isSelect2Ctrl()) {
          // self.$editableElem.select2("val", "");
          self.$editableElem.wellSelect('val', '');
        } else {
          self.$editableElem.val('');
        }
      } else {
        if (typeof value === 'string') {
          value = value.split(self.getSeparator());
        } else if (false === $.isArray(value)) {
          value = [value];
        }
        if (self.isSelect2Ctrl()) {
          // self.$editableElem.select2("val", value)
          self.$editableElem.wellSelect('val', value);
        } else {
          self.$editableElem.val(value);
        }
      }
      if (self.isSelect2Ctrl()) {
        self.$editableElem.hide();
      }
      if (self.options.isShowAsLabel) {
        self.hideEditableElem();
      }
    },

    hideEditableElem: function () {
      $.wControlInterface.hideEditableElem.call(this);
      if (this.isSelect2Ctrl()) {
        if (this.$select2Container) {
          this.$select2Container.hide();
        }
        if (this.$checkedAllElem) {
          this.$checkedAllElem.hide();
        }
      }
    },

    showEditableElem: function () {
      $.wControlInterface.showEditableElem.call(this);
      if (this.isSelect2Ctrl()) {
        if (this.$select2Container) {
          this.$select2Container.show();
        }
        if (this.$checkedAllElem) {
          this.$checkedAllElem.show();
        }
      }
    },

    isValueMap: function () {
      return true;
    },
    setReadOnly: function (isReadOnly) {
      // this.render(true);
      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        if (this.$editableElem) {
          this.$editableElem.attr('readonly', 'readonly');
          this.$editableElem.wellSelect('readonly', true);
        }

        this.removeValidate();
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.$editableElem.removeAttr('readonly');
        this.$editableElem.wellSelect('readonly', false);
      }
      // this.render();
    }
    // 一些其他method ---------------------
  };

  /*
   * SELECTOPTGROUP PLUGIN DEFINITION =========================
   */
  $.fn.wcomboSelect = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this);
        data = $this.data('wcomboSelect');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wcomboSelect'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new ComboSelect($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.ComboSelect);
      data.init();
      $this.data('wcomboSelect', data);
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wcomboSelect.Constructor = ComboSelect;

  $.fn.wcomboSelect.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    isShowAsLabel: false,
    isHide: false, // 是否隐藏
    disabled: false,
    optionDataSource: '1', // 备选项来源1:常量,2:字段
    optionSet: [],
    dictCode: null
    /*,
		hiddenValues : []*/
  };
})(jQuery);

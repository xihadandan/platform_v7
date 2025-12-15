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
   * COMBOBOX CLASS DEFINITION ======================
   */
  var ComboBox = function ($placeHolder /* 占位符 */, options) {
    this.options = $.extend({}, $.fn['wcomboBox'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
    this.$select2Container = null; // select2容器
  };

  ComboBox.prototype = {
    constructor: ComboBox
  };

  $.ComboBox = {
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
      this.setDefaultCondition();

      this.reRender();

      var _this = this;
      if (_this.isSearchable()) {
        // _this.initAsSelect2();
        _this.initAsWellSelect();
      }

      this.$editableElem.change(function () {
        var $selectedOpt = $(this).find('option:selected');
        var realValue = $selectedOpt.val();
        _this.setValue(realValue);
        if (realValue != null && realValue != '') {
          _this.setValueData2FormFields($selectedOpt.data('data'));
        }
      });
    },

    setDefaultCondition: function () {
      var fieldDef = this.getFieldParams();
      this.conditions = [
        {
          sql: fieldDef.defaultCondition
        }
      ];
    },

    setValueData2FormFields: function (data) {
      var fieldDef = this.getFieldParams();
      var mapping = fieldDef.dataSourceFieldMapping;
      if (data && mapping && mapping.length > 0) {
        var formData = null;
        this.dyform$Context().collectFormData(function (result) {
          formData = result.formDatas[result.formUuid][0];
        }, $.noop);
        for (var i = 0, len = mapping.length; i < len; i++) {
          var control = $.ControlManager.getCtl(this.getContronId(mapping[i].formField));
          if (typeof control == 'undefined' || control == null) {
            continue;
          }
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
          control.setValue(value);
        }
      }
    },

    isSearchable: function () {
      return this.options.searchable == true;
    },

    initAsSelect2: function () {
      var _this = this;
      // window.setTimeout(function(){
      _this.$editableElem.select2();
      var css = _this.getTextInputCss();
      _this.$editableElem.select2(css);
      if (!css.width || $.trim(css.width).length == 0) {
        _this.$editableElem.select2({
          width: '100%'
        });
      }
      _this.$editableElem.hide();
      _this.$select2Container = _this.$editableElem.prev();
      // _this.isSelect2 = true;
      // }, 500);

      _this.$editableElem.focus = function () {
        _this.$editableElem.select2('focus');
      };
      _this.$editableElem.click = function () {
        _this.$editableElem.select2('click');
      };
    },
    initAsWellSelect: function () {
      var _self = this;
      var options = _self.options;
      var ctrName = _self.getCtlName();
      _self.$editableElem.wellSelect({
        editableClass: _self.editableClass,
        valueField: ctrName,
        labelField: '_' + ctrName,
        ctlWidth: options.commonProperty.ctlWidth,
        searchable: options.searchable,
        multiple: options.selectMultiple,
        maxSelectNum: options.maxSelectionLength,
        chooseAll: options.selectCheckAll,
        source: 'wSelect'
      });
      setTimeout(function () {
        _self.$editableElem.hide();
      }, 0);
      _self.$select2Container = _self.$editableElem.prev();
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

    reRender: function () {
      var optionSet = this.getOptionSet(this.options);
      this.appendOption2Select(optionSet);
      this.setValue2EditableElem();
      this.initAsWellSelect();
    },

    //刷新，重新获取选项数据
    refresh: function () {
      this.reloadOptionSet();
      return this;
    },

    //增加数据仓库条件
    addDsConditions: function (criterion) {
      if ($.isArray(criterion)) {
        this.conditions = this.conditions.concat(criterion);
      } else {
        this.conditions.push(criterion);
      }
      return this;
    },

    //清楚数据仓库条件
    clearDsConditions: function () {
      this.conditions = [];
      return this;
    },

    reloadOptionSet: function () {
      var _this = this;
      var fieldDef = this.getFieldParams();
      var DataStore = require('dataStoreBase');
      var Datastore = new DataStore({
        dataStoreId: fieldDef.dataSourceId,
        receiver: _this,
        defaultCriterions: (function () {
          var defaultConditions = [];
          var formData = {};
          _this.dyform$Context().collectFormData(function (result) {
            formData = result.formDatas[result.formUuid][0];
          }, $.noop);
          for (var i = 0, len = _this.conditions.length; i < len; i++) {
            var criterion = $.extend({}, _this.conditions[i]);
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
            if ($.trim(criterion.sql).length > 0 || $.trim(criterion.value).length > 0) {
              defaultConditions.push(criterion);
            }
          }
          return defaultConditions;
        })(),
        onDataChange: function () {
          _this.options.optionSet = _this.buildOptionSet(arguments[0]);
          _this.reRender();
        }
      });
      Datastore.load([], null);
    },

    buildOptionSet: function (dataSourceData) {
      var optionSet = [];
      var _this = this;
      var fieldDef = this.getFieldParams();
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
      this.$editableElem.empty();
      this.$editableElem.append($('<option>').text(''));
      if (this.isSearchable()) {
        this.$editableElem.append($('<option>').text(''));
      }
      var $options = [];
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
          if ($.inArray(value, this.options.hiddenValues) == -1) {
            var $opt = $('<option>', {
              value: value
            }).text(name);
            $opt.data('data', option.data);
            $options.push($opt);
          }
        }
      } else {
        for (var attrbute in optionSet) {
          if ($.inArray(attrbute, this.options.hiddenValues) == -1) {
            var $opt = $('<option>', {
              value: attrbute
            }).text(optionSet[attrbute]);
            $opt.data('data', optionSet[attrbute].data);
            $options.push($opt);
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

    setOptionSet: function (optionSet) {
      if (!this.isValueNameFormater(optionSet)) {
        return $.wRadioCheckBoxCommonMethod.setOptionSet.call(this, optionSet);
      } else {
        this.options.optionSet = optionSet;
        this.options.optionDataSource = dyDataSourceType.custom; // 用户自定义
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
      self.$labelElem.html(self.getDisplayValue());
      self.$labelElem.attr('title', self.getDisplayValue());
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
          self.$editableElem.wellSelect('val', '');
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
      if (self.isSearchable()) {
        self.$editableElem.wellSelect('val', value);
      } else {
        // self.$editableElem.val(value);
        self.$editableElem.wellSelect('val', value);
      }
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
      }
      $.wControlInterface.setValue.call(self, value);
    },

    hideEditableElem: function () {
      $.wControlInterface.hideEditableElem.call(this);
      if (this.isSearchable()) {
        if (this.$select2Container) {
          this.$select2Container.hide();
        }
      }
    },

    showEditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        self.createEditableElem();
        self.initInputEvents();
      }
      if (self.isSearchable()) {
        if (self.$select2Container) {
          self.$select2Container.show();
        }
      } else {
        $.wControlInterface.showEditableElem.call(self);
      }
    },

    isValueMap: function () {
      return true;
    }

    // 一些其他method ---------------------
  };

  /*
   * COMBOBOX PLUGIN DEFINITION =========================
   */
  $.fn.wcomboBox = function (option) {
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
        data = $this.data('wcomboBox');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wellSelect'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new ComboBox($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.ComboBox);
      data.init();
      $this.data('wellSelect', data);
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

  $.fn.wcomboBox.Constructor = ComboBox;

  $.fn.wcomboBox.defaults = {
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

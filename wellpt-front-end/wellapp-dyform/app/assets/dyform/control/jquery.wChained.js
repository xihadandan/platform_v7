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
    onlyreadUrl: null
    // 只读状态下设置跳转的url
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null
    // 对齐方式
  };

  /*
   * RADIO CLASS DEFINITION ======================
   */
  var Chained = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wchained'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  Chained.prototype = {
    constructor: Chained
  };

  $.extend(Chained.prototype, {
    hideEditableElem: function () {
      var self = this;
      $.wControlInterface.hideEditableElem.apply(self, arguments);
      self.$editableElem && self.$editableElem.closest('.chained-wrapper').hide();
    },
    showEditableElem: function () {
      var self = this;
      $.wControlInterface.showEditableElem.apply(self, arguments);
      self.$editableElem && self.$editableElem.closest('.chained-wrapper').show();
    },
    createEditableElem: function () {
      var _this = this;
      if (_this.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var options = this.options;
      var ctlName = this.getCtlName();
      var editableElem = document.createElement('input');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('type', 'text');
      editableElem.setAttribute('maxlength', options.columnProperty.length);
      var textInputCss = _this.getTextInputCss() || {};

      textInputCss['color'] = 'black';

      var $wrapper = $('<div>', {
        class: 'chained-wrapper',
        style: 'position:relative;'
      }).css(textInputCss);

      this.$editableElem = $(editableElem).css(
        $.extend({}, textInputCss, {
          'padding-right': '28px'
        })
      );
      $wrapper.append(editableElem);

      var $clearBtn = $('<div class="clear-btn" style="display: none"><i class="iconfont icon-ptkj-dacha-xiao"></i></div>');
      $wrapper.append($clearBtn);
      $clearBtn.on('click', function () {
        _this.$editableElem.val('');
        _this.$editableElem.attr('data-value', '');
        _this.setValue('', false);
        $clearBtn.hide();
      });
      var $icon = $('<i>', {
        class: 'iconfont icon-ptkj-xianmiaojiantou-xia'
      });
      $wrapper.append($icon);

      this.$placeHolder.after($wrapper);

      this.$editableElem.bind('change', function () {
        var _val = _this.$editableElem.attr('data-value');
        if (_val) {
          $clearBtn.show();
        }
        _this.setValue(_val, false); // 设置,再不对元素再进行渲染
      });

      var navTitls = [];
      var field = _this.getFieldParams();
      if (options.optionDataSource === dyDataSourceType.dataDictionary) {
        navTitls = field.dicTitles.split('/');
      } else if (options.optionDataSource === dyDataSourceType.dataSource) {
        var dataSources = field.dataSources;
        for (var i = 0; i < dataSources.length; i++) {
          navTitls.push(dataSources[i].dataSourceTitle);
        }
      }
      var optionSet = options.optionSet;
      this.$editableElem.cascader({
        // changeOnSelect : true,
        triggerType: 'click',
        navTitls: navTitls,
        nodes: optionSet
      });

      if (options.lazyLoading) {
        _this.lazySetDictOptions(function (optionSet) {
          _this.$editableElem.cascader('destroy');
          _this.$editableElem.cascader({
            triggerType: 'click',
            navTitls: navTitls,
            nodes: optionSet
          });
          _this.setValue(_this.value);
        }, true);
      }

      if (options.optionDataAutoSet) {
        var $dyform = DyformFacade.get$dyform();
        $dyform.bind2Dyform2('beforeSetData', function () {
          _this.getDynamicOptionSet();
        });
      }

      // if (options.optionDataSource == '4') {
      //   this.initDataStore();
      // }
    },
    //获取动态备选项
    getDynamicOptionSet: function () {
      var options = this.options;
      if (options.optionDataSource == '2') {
        this.reloadDictOptions(options);
      } else if (options.optionDataSource == '4') {
        this.reloadSourceOptions(options);
      }
    },
    // 重新加载数据字典的备选项
    reloadDictOptions: function (options) {
      var self = this;
      var field = this.getFieldParams();
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
              var navTitls = field.dicTitles;
              var data = [];
              if (value != '' && value.split(';').length == 1) {
                JDS.call({
                  service: 'formDefinitionService.chainedDataDict',
                  data: [value, navTitls],
                  version: '',
                  async: false,
                  success: function (result) {
                    if (result.data.length > 0) {
                      data = result.data;
                    }
                  }
                });
              }
              self.$editableElem.cascader('destroy');
              self.$editableElem.cascader({
                triggerType: 'click',
                navTitls: navTitls,
                nodes: data
              });
              var _val = self.getValue();
              var isTrueValue = self.valueInOptionset(data, _val);
              if (isTrueValue) {
                self.setValue(_val);
              } else {
                self.setValue('');
                self.get$InputElem().val('');
                self.$editableElem.siblings('.clear-btn').hide();
              }
            },
            500,
            { leading: false, trailing: true }
          )
        );
      }
    },

    // 重新加载数据仓库的备选项
    reloadSourceOptions: function (options) {
      var self = this;
      var relateFieldList = options.relateField.split(';');
      var dataList = {};
      var field = this.getFieldParams();
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
                  // self.$editableElem.siblings('.clear-btn').trigger('click');
                  dataList[relateFieldList[i]] = value;
                  JDS.call({
                    service: 'formDefinitionService.chainedDataSource',
                    data: [JSON.stringify(field.dataSources), dataList],
                    version: '',
                    async: false,
                    success: function (result) {
                      var data = result.data;
                      self.$editableElem.cascader('destroy');
                      self.$editableElem.cascader({
                        triggerType: 'click',
                        navTitls: field.navTitls,
                        nodes: data
                      });
                      var _val = self.getValue();
                      var isTrueValue = self.valueInOptionset(data, _val);
                      if (isTrueValue) {
                        self.setValue(_val);
                      } else {
                        self.setValue('');
                        self.get$InputElem().val('');
                        self.$editableElem.siblings('.clear-btn').hide();
                      }
                    },
                    error: function (jqXHR) {
                      jqXHR.responseJSON && console.log(jqXHR.responseJSON.msg);
                    }
                  });
                },
                500,
                { leading: false, trailing: true }
              )
            );
          }
        })(i);
      }
    },

    valueInOptionset: function (data, val) {
      return getValueText(data, val, 0, false);

      function getValueText(data, val, index, bol) {
        var newVal = val.split('/');
        for (var i = 0; i < data.length; i++) {
          var item = data[i];
          if ((newVal[index] = item.id)) {
            bol = true;
            if (newVal.length > index + 1 && item.children && item.children.length > 0) {
              index++;
              getValueText(item.children, val, index, bol);
            }
            break;
          }
        }
        return bol;
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

        this.getDynamicOptionSet();
      }
    },

    initDataStore: function () {
      var _self = this;
      var fieldDef = _self.getFieldParams();
      var DataStore = require('dataStoreBase');
      _self.Datastore = new DataStore({
        dataStoreId: fieldDef.dataSources[0].dataSourceId,
        receiver: _self,
        pageSize: 1000,
        onDataChange: function (data, totalCount, params, getDefinitionJson) {
          _self.$editableElem.cascader('destroy');
          _self.$editableElem.cascader({
            triggerType: 'click',
            navTitls: fieldDef.navTitls,
            nodes: data.data
          });
        },
        error: function (jqXHR) {
          jqXHR.responseJSON && console.log(jqXHR.responseJSON.msg);
        }
      });
    },

    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null || typeof self.$labelElem === 'undefined') {
        return;
      }
      if (self.get$InputElem() && self.get$InputElem().length) {
        var objValue = self.get$InputElem().cascader('getValue');
        self.$labelElem.html(objValue.text);
      }
    },
    // 设值
    setValue: function (value) {
      var self = this;
      self.value = value;
      self.createEditableElem();
      if (self.get$InputElem().attr('data-value') !== value) {
        self.get$InputElem().cascader('setValue', value);
      }
      self.setValue2LabelElem && self.setValue2LabelElem();

      if (this.options.isShowAsLabel) {
        this.hideEditableElem && this.hideEditableElem();
      }

      if (self.culateByFormula) {
        self.culateByFormula(); // 根据运算公式计算
      }
      if (self.setValueLock) {
        return self.setValueLock;
      }
      self.setValueLock = true;
      try {
        if (self.isValueMap()) {
          self.setToRealDisplayColumn();
        }
        self.invoke('afterSetValue', self.value);
      } finally {
        self.setValueLock = false;
      }
    },
    isValueMap: function () {
      return true;
    },
    getDisplayValue: function () {
      var self = this;
      if (self.get$InputElem() && self.get$InputElem().length) {
        var objValue = self.get$InputElem().cascader('getValue');
        return objValue.text;
      }
    }
  });

  /*
   * RADIO PLUGIN DEFINITION =========================
   */
  $.fn.wchained = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this),
          data = $this.data('wchained');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wchained'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new Chained($(this), options);
      data = $.extend({}, $.wControlInterface, data);

      data.init();
      $this.data('wchained', data);
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

  $.fn.wchained.Constructor = Chained;

  $.fn.wchained.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    // 私有属性
    isHide: false, // 是否隐藏
    checked: false,
    disabled: false,
    isShowAsLabel: false,
    // hiddenValues : [],
    optionDataSource: '1', // 备选项来源1:常量,2:字段
    optionSet: [],
    dictCode: null,
    value: ''
  };
})(jQuery);

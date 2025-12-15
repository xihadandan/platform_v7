define([
  'mui',
  'server',
  'commons',
  'constant',
  'appContext',
  'appModal',
  'mui-DyformConstant',
  'mui-DyformField',
  'mui-DyformScope',
  'mui-DyformValidation',
  'mui-DyformCommons'
], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  DyformConstant,
  DyformField,
  DyformScope,
  DyformValidation,
  DyformCommons
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;
  var CLASS_DYFORM = 'mui-dyform';
  var DyformExplain = function (element, options) {
    var self = this;
    // 保存dyform对象
    var id = 'dyform-obj-' + ++$.uuid;
    $.data[id] = self;
    // 初始化
    self.element = element;
    $(element)[0].classList.add(CLASS_DYFORM);
    $(element)[0].setAttribute('DyformExplain', id);
    // options.formData.formDefinition = loadDefinitionJsonDefaultInfo(options.formData.formDefinition);
    self.init(options);
  };

  function loadDefinitionJsonDefaultInfo(formDefinition) {
    // formDefinition = JSON.parse(formDefinition);
    JDS.call({
      service: 'dyFormFacade.loadDefinitionJsonDefaultInfo',
      async: false,
      data: [formDefinition, '', '', ''],
      success: function (result) {
        formDefinition = result.data;
      }
    });
    return formDefinition;
  }

  DyformExplain.fn = {};
  $.extend(DyformExplain.fn, {
    // 根据字段名称获取字段对象
    getField: function (fieldName) {
      var _self = this;
      var fieldId = _self._getFormScope().getFieldId(fieldName);
      return _self.formFieldMap[fieldId];
    },
    getControl: function (fieldName) {
      var _self = this;
      return _self.getField(fieldName);
    },
    getFields: function () {
      return this.formFields;
    },
    // 根据从表ID获取从表对象
    getSubform: function (subformId) {
      var _self = this;
      var fieldId = _self._getFormScope().getFieldIdForSubform(subformId);
      return _self.formFieldMap[fieldId];
    },
    // 根据从表ID获取从表数据
    getSubformData: function (subformId) {
      var subform = this.getSubform(subformId);
      if (subform == null) {
        return [];
      }
      return subform.getData();
    },
    // 设置字段值
    getFieldValue: function (fieldName) {
      return this.getField(fieldName).getValue();
    },
    // 设置字段值
    setFieldValue: function (fieldName, fieldVal) {
      this.getField(fieldName).setValue(fieldVal, true);
    },
    // 设置字段显示为文本
    setFieldDisplayAsLabel: function (fieldName) {
      this.getField(fieldName).setDisplayAsLabel();
    },
    // 判断字段是否必填
    isFieldRequired: function (fieldName) {
      return this.getField(fieldName).isRequired();
    },
    // 设置字段是否必填
    setFieldRequired: function (fieldName, required) {
      this.getField(fieldName).setRequired(required);
    },
    // 判断字段是否可编辑
    isFieldEditable: function (fieldName) {
      return this.getField(fieldName).isEditable();
    },
    // 设置字段是否可编辑
    setFieldEditable: function (fieldName, editable) {
      this.getField(fieldName).setEditable(editable);
    },
    // 判断字段是否只读
    isFieldReadonly: function (fieldName) {
      return this.getField(fieldName).isReadonly();
    },
    // 设置字段是否只读
    setFieldReadonly: function (fieldName, readonly) {
      this.getField(fieldName).setReadonly(readonly);
    },
    // 判断字段是否隐藏
    isFieldHidden: function (fieldName) {
      return this.getField(fieldName).isHidden();
    },
    // 设置字段是否隐藏
    setFieldHidden: function (fieldName, hidden) {
      this.getField(fieldName).setHidden(hidden);
    },
    // 获取参数
    getOption: function () {
      return this.options;
    },
    // 获取可选参数
    getOptional: function () {
      var options = this.getOption();
      return options.optional;
    },
    // 是否新建
    isCreate: function () {
      var self = this,
        optional;
      if ((optional = self.getOptional()) && optional.isFirst) {
        return true;
      } else {
        return false;
      }
    },
    // 是否不可编辑
    isDisplayAsLabel: function () {
      var options = this.getOption();
      return options.displayAsLabel;
    },
    // 给主表设置Uuid
    setDataUuid: function (dataUuid) {
      var self = this;
      return self._getFormScope().setDataUuid(dataUuid);
    },
    // 获取主表Uuid
    getDataUuid: function () {
      var self = this;
      return self._getFormScope().getDataUuid();
    },
    // 获取主表的定义UUID
    getFormUuid: function () {
      var self = this;
      return self._getFormScope().getFormUuid();
    },
    // 获取主表的定义id
    getFormId: function (formUuid) {
      var self = this;
      // TODO subform formUuid
      return self._getFormScope().getFormId();
    },
    getFormDefinition: function (formUuid) {},
    createUuid: function () {
      return commons.UUID.createUUID();
    },
    // 整个表单设置为只读
    setReadOnly: function () {
      // TODO this.invoke("afterSetReadOnly");
    },
    // 整个表单设置为可编辑
    setEditable: function () {
      // TODO this.invoke("afterSetEditable");
    },
    // 整个表单附件设置为可下载
    setAllowDownload: function () {},
    // 整个表单附件设置为不可下载
    setNoAllowDownload: function () {},
    // 按标签展示
    showAsLabel: function () {
      // TODO this.invoke("afterShowAsLabel");
    },
    // 打印表单信息
    print: function () {
      DyformCommons.print(this);
    },
    // 表单验证
    validateForm: function () {
      var validationResult = this.formValidation.validate(true);
      if (validationResult.hasErrors()) {
        this.showValidationErrors(validationResult);
        console.log('表单验证失败!');
        console.log(validationResult);
        return false;
      }
      console.log('表单验证成功!');
      return true;
    },
    // 显示验证错误信息列表，可快速进行错误定位
    showValidationErrors: function (validationResult) {
      // TODO
    },
    // 异常处理
    handleException: function (exceptionData) {
      function errorFun() {
        var msg =
          "<div><a id='wf_save_data_error_msg' title='" +
          exceptionData +
          "' style='color: #000;text-decoration: none;'>表单数据保存失败！</a></div>";
        $.alert(msg);
      }
      try {
        var msg = JSON.parse(exceptionData);
        // TODO soft code
        if (msg.code == 'SQLGRAM') {
          if (msg.msg && msg.msg.indexOf('ORA-01461') > -1) {
            return $.alert('输入字符串长度超过限制,请先校验通过后保存');
          } else {
            $.alert('保存时,后台语法错误!!!有可能是人为去修改了表单后台数据库表字段,更详细的信息如下:\n' + msg.msg);
          }
        } else if (msg.code == 'DATA_OUT_OF_DATE') {
          $.alert('请重新加载并修改数据:\n' + msg.msg);
        } else {
          errorFun();
        }
      } catch (e) {
        errorFun();
      }
    },
    // 收集表单数据
    collectFormData: function (fnCallback) {
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
      var mainFormData = _self._getFormScope().getFormData();
      var mainFormDataUpdatedFieldMap = {};
      $.each(_self.formFields, function (i, field) {
        var formUuid = field.getFormUuid();
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
          mainFormData[fieldName] = DyformCommons.getFieldValueForSave(field);
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
        formUuid: _self.formData.formUuid
      };
      // console.log(dyformData);
      if ($.isFunction(fnCallback)) {
        fnCallback.call(this, dyformData);
      }
      return dyformData;
    },
    // 注册公共
    registerFormula: function (formula) {
      var _self = this;
      var formulas = _self.formulas;
      if (formulas == null) {
        formulas = {};
        _self.formulas = formulas;
      }
      // 触发公式的元素
      var triggerElements = formula.triggerElements;
      // 公式
      var formulaFun = formula.formula;
      if (!$.isArray(triggerElements) || !$.isFunction(formulaFun)) {
        console.error('非法的公式:');
        console.error(formula);
      }
      for (var i = 0; i < triggerElements.length; i++) {
        var triggerElementFieldName = triggerElements[i];
        var fieldFormulas = [];
        for (var fn in formulas) {
          if (fn == triggerElementFieldName) {
            fieldFormulas = formulas[fn];
          }
        }
        fieldFormulas.push(formulaFun);
        formulas[triggerElementFieldName] = fieldFormulas;
      }
    },
    getBlockId: function (blockCode) {
      return 'block_' + blockCode;
    },
    /**
     * 隐藏区块
     */
    hideBlock: function (blockCode) {
      var self = this;
      var blockId = self.getBlockId(blockCode);
      var $block = $('a[code=' + blockId + ']', self.element[0]);
      if ($block && $block.length === 1) {
        $block[0].classList.add('mui-hidden');
        $('div#' + blockId, self.element[0])[0].classList.add('mui-hidden');
      }
    },
    /**
     * 显示区块
     */
    showBlock: function (blockCode) {
      var self = this;
      var blockId = self.getBlockId(blockCode);
      var $block = $('a[code=' + blockId + ']', self.element[0]);
      if ($block && $block.length === 1) {
        $block[0].classList.remove('mui-hidden');
        $('div#' + blockId, self.element[0])[0].classList.remove('mui-hidden');
      }
    },
    getLayout: function (layoutId) {
      var self = this;
      return {
        hide: function (tabId) {
          var layoutObj = this;
          var blockId = layoutObj.getTabId(tabId);
          var $block = $('a[code=' + blockId + ']', self.element[0]);
          if ($block && $block.length === 1) {
            $block[0].classList.add('mui-hidden');
            $('div#' + blockId, self.element[0])[0].classList.add('mui-hidden');
          }
        },
        show: function (tabId) {
          var layoutObj = this;
          var blockId = layoutObj.getTabId(tabId);
          var $block = $('a[code=' + blockId + ']', self.element[0]);
          if ($block && $block.length === 1) {
            $block[0].classList.remove('mui-hidden');
            $('div#' + blockId, self.element[0])[0].classList.remove('mui-hidden');
          }
        },
        getTabId: function (tabCode) {
          return 'tab_' + layoutId + '-' + tabCode;
        }
      };
    }
  });

  $.extend(
    DyformExplain.prototype,
    {
      // 初始化
      init: function (options) {
        var _self = this;
        var sc = options.success;
        var success = function () {
          if ($.isFunction(sc)) {
            sc.apply(_self, arguments);
          }
        };
        var ec = options.error;
        var error = function () {
          if ($.isFunction(ec)) {
            ec.apply(_self, arguments);
          }
        };
        var cc = options.complete;
        var complete = function () {
          if ($.isFunction(cc)) {
            cc.apply(_self, arguments);
          }
        };
        options.success = success;
        options.error = error;
        options.complete = complete;
        try {
          this._init(options);
        } catch (e) {
          console.error(e);
          /// alert(e);
          options.error.apply(this, arguments);
          options.complete.apply(this, arguments);
        }
      },
      _init: function (options) {
        var _self = this;
        _self.options = options;
        _self.renderTo = options.renderTo;
        _self.formData = options.formData;
        // 表单的字段对象，包含从表
        _self.formFields = [];
        // 表单的字段对象Map，包含从表
        _self.formFieldMap = {};
        _self.formValidation = new DyformValidation(this);
        _self.formDefinition = options.formData.formDefinition;
        if (typeof _self.formDefinition == 'string') {
          _self.formDefinition = JSON.parse(_self.formDefinition);
        }
        var formDefinition = _self.formDefinition;
        // 字段信息同步源表单定义
        if (formDefinition.formType === 'M' && formDefinition.pFormUuid) {
          var pFormDefinition = {},
            formData = DyformCommons.loadFormDefinitionData(formDefinition.pFormUuid);
          if (formData && typeof formData.formDefinition == 'string') {
            pFormDefinition = JSON.parse(formData.formDefinition);
          }

          function extend(target, source, ignores) {
            for (var name in source) {
              var copy = source[name];
              if (copy == null || $.inArray(name, ignores) > -1) {
                continue;
              }
              if ($.isPlainObject(copy) && target[name]) {
                extend(target[name], copy, ignores);
              } else {
                target[name] = copy;
              }
            }
          }
          var ignores = ['showType', 'fieldCheckRules', 'hideButtons', 'hide', 'editable'];
          $.each(['fields', 'blocks', 'subforms', 'templates'], function (idx, cpf) {
            extend(formDefinition[cpf], pFormDefinition[cpf], ignores);
          });
        }
        _self.mobileConfiguration = _self.formDefinition.mobileConfiguration;
        _self.subformDefinitions = [];
        $.each(_self.formDefinition.subformDefinitions, function (i, json) {
          if (typeof json == 'string') {
            json = JSON.parse(json);
          }
          _self.subformDefinitions.push(json);
        });
        // 将PC端隐藏的区块合并到手机，隐藏以PC端优先(流程设置)，即：PC端设置了隐藏，则手机设置里面的是否隐藏设置无效，一直隐藏。
        _self._mergeBlocks();
        // 将主表定义的从表字段信息合并到相应的从表定义的字段定义
        _self._mergeSubformFields();
        // 二开JS模块
        _self.customJsModule = _self.formDefinition.customJsModule;
        // 预处理
        this._prepare(function () {
          if (StringUtils.isNotBlank(_self.customJsModule)) {
            var dyformDevelopModule = appContext.require(_self.customJsModule);
            if (dyformDevelopModule != null) {
              _self.dyformDevelop = new dyformDevelopModule(_self);
            }
          }
          // 方法回调——初始化
          _self.invokeDevelopmentMethod('onInit', _self);
          // 方法回调——表单解析前
          _self.invokeDevelopmentMethod('beforeParseForm', _self);

          var mobileConfiguration = _self.mobileConfiguration;
          var blockLayout = mobileConfiguration.blockLayout;
          var isLayout = mobileConfiguration.isLayout;
          // 默认布局加滚动包装
          var placeHolder = _self.element[0];
          if (false === isLayout && blockLayout == '1') {
            placeHolder.classList.add('mui-scroll-wrapper');
            var scroll = document.createElement('div');
            scroll.classList.add('mui-scroll');
            placeHolder.appendChild(scroll);
            $(placeHolder).scroll();
            placeHolder = scroll;
          }
          _self._appendContent(placeHolder, mobileConfiguration, blockLayout);

          // 方法回调——数据设置前
          _self.invokeDevelopmentMethod('beforeSetData', _self);

          _self._render();

          // 顶部滑块
          var deceleration = mui.os.ios ? 0.003 : 0.0009;
          $('.mui-scroll-wrapper', $(_self.element)[0]).scroll({
            bounce: false,
            indicators: true, // 是否显示滚动条
            deceleration: deceleration
          });

          _self.bindEvent();

          // 标记创建完成
          _self.dyformCreateComplete = true;
          // 事件解发——解发初始化完成事件
          _self.trigger(DyformConstant.dyformEvent.DyformCreationComplete);
          // 方法回调——数据设置后
          _self.invokeDevelopmentMethod('afterSetData', _self);
          // 方法回调——表单解析后
          _self.invokeDevelopmentMethod('afterParseForm', _self);

          options.success.apply(_self);
          options.complete.apply(_self, arguments);
        });
      },
      bindEvent: function () {
        var self = this;
        $(document.body).on('tap', '.mui-dyform label.mui-ellipsis,.mui-dyform label.dyform-field-label', function (event) {
          var popText = event.target && event.target.innerText;
          if (popText) {
            $.toast(popText);
          }
        });
      },
      _getFormScope: function () {
        var self = this;
        if (!self.formScope) {
          self.formScope = self.createDyformScope(self.formDefinition, self.formFieldMap, self._getMainFormData());
        }
        return self.formScope;
      },
      _appendMuiList: function (placeHolder, children) {
        if (children.length) {
          var _self = this;
          var html = new StringBuilder();
          html.appendFormat('<ul class="mui-table-view">');
          for (var i = 0; i < children.length; i++) {
            html.appendFormat(' <li class="mui-table-view-cell"></li>');
          }
          html.appendFormat(' </ul>');
          placeHolder.appendChild($.dom(html.toString())[0]); // DyformCommons.dom
          // placeHolder.innerHTML = placeHolder.innerHTML +
          // html.toString();
          var liElements = placeHolder.querySelectorAll('li');
          for (var i = 0; i < children.length; i++) {
            this._appendChild(liElements[i], children[i]);
          }
        }
      },
      _appendContent: function (placeHolder, root, blockLayout) {
        var self = this;
        var children = self._mergeChirldren(root);
        if (true === root.isLayout) {
          self._appendLayout(placeHolder, children, blockLayout);
        } else if (blockLayout == '2') {
          self._appendTobTab(placeHolder, children);
        } else if (blockLayout == '3') {
          self._appendBottomTab(placeHolder, children);
        } else if (blockLayout == '4') {
          self._appendTobSlider(placeHolder, children);
        } else if (blockLayout == '5') {
          self._appendOffCanvas(placeHolder, children);
        } else {
          self._appendChildren(placeHolder, children);
        }
      },
      _appendLayout: function (placeHolder, layouts, blockLayout) {
        var self = this;
        var isLayout = self.mobileConfiguration.isLayout || false;
        if (layouts.length === 1) {
          // 就一个页签，直接显示内容
          if (true === isLayout && blockLayout == '1') {
            placeHolder.classList.add('mui-scroll-wrapper');
            var scroll = document.createElement('div');
            scroll.classList.add('mui-scroll');
            placeHolder.appendChild(scroll);
            $(placeHolder).scroll();
            placeHolder = scroll;
          }
          return self._appendContent(placeHolder, layouts[0], blockLayout);
        }
        var html = new StringBuilder();
        // tablayout-slider全局唯一
        html.appendFormat('<div class="mui-slider tablayout-slider mui-fullscreen">');
        html.appendFormat('<div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">');
        html.appendFormat('	<div class="mui-scroll">');
        for (var i = 0; i < layouts.length; i++) {
          var layout = layouts[i];
          var className = 'mui-control-item';
          if (layout.isActive) {
            className += ' mui-active';
          } else if ($.isEmptyObject(layout.blocks)) {
            className += ' mui-hidden';
          }
          html.appendFormat(
            '<a class="{2}" href="#tab_{0}" code="tab_{0}" name="{3}"> {1} </a>',
            layout.id,
            layout.displayName,
            className,
            layout.name
          );
        }
        html.appendFormat('</div>');
        html.appendFormat('</div>');

        html.appendFormat('<div class="mui-slider-group" > ');
        for (var i = 0; i < layouts.length; i++) {
          var layout = layouts[i];
          var className = 'mui-slider-item mui-control-content';
          if (layout.isActive) {
            className += ' mui-active';
          } else if ($.isEmptyObject(layout.blocks)) {
            className += ' mui-hidden';
          }
          html.appendFormat('	<div id="tab_{0}" class="{1}">', layout.id, className);
          html.appendFormat('	<div class="mui-scroll-wrapper">');
          html.appendFormat('	<div class="mui-scroll mui-input-group">');
          html.appendFormat('	</div >');
          html.appendFormat('	</div >');
          html.appendFormat('	</div >');
        }
        html.appendFormat('</div>');
        html.appendFormat('</div>');
        placeHolder.appendChild($.dom(html.toString())[0]); // DyformCommons.dom
        var muiSlider = placeHolder.querySelector('.tablayout-slider');
        $.Slider.prototype.handleVisiableSlide = function (event) {
          var self = this;
          if (event.target !== self.wrapper) {
            return;
          }
          var detail = event.detail;
          var options = self.options;
          detail.slideNumber = detail.slideNumber || 0;
          var temps = self.scroller.querySelectorAll(options.snap);
          var items = [];
          for (var i = 0, len = temps.length; i < len; i++) {
            var item = temps[i];
            if (item.parentNode === self.scroller) {
              items.push(item);
            }
          }
          var _slideNumber = detail.slideNumber;
          if (self.loop) {
            _slideNumber += 1;
          }
          var CLASS_ACTIVE = 'mui-active';
          if (!self.wrapper.classList.contains('mui-segmented-control')) {
            for (var i = 0, len = items.length; i < len; i++) {
              var item = items[i];
              if (item.parentNode === self.scroller) {
                if (i === _slideNumber) {
                  item.classList.add(CLASS_ACTIVE);
                } else {
                  item.classList.remove(CLASS_ACTIVE);
                }
              }
            }
          }
          var indicatorWrap = self.wrapper.querySelector('.mui-slider-indicator');
          if (indicatorWrap) {
            if (indicatorWrap.getAttribute('data-scroll')) {
              //scroll
              $(indicatorWrap).scroll().gotoPage(detail.slideNumber);
            }
            var indicators = indicatorWrap.querySelectorAll('.mui-indicator');
            if (indicators.length > 0) {
              //图片轮播
              for (var i = 0, len = indicators.length; i < len; i++) {
                indicators[i].classList[i === detail.slideNumber ? 'add' : 'remove'](CLASS_ACTIVE);
              }
            } else {
              var number = indicatorWrap.querySelector('.mui-number span');
              if (number) {
                //图文表格
                number.innerText = detail.slideNumber + 1;
              } else {
                //segmented controls
                var controlItems = indicatorWrap.querySelectorAll('.mui-control-item:not(.mui-hidden)');
                for (var i = 0, len = controlItems.length; i < len; i++) {
                  controlItems[i].classList[i === detail.slideNumber ? 'add' : 'remove'](CLASS_ACTIVE);
                }
              }
            }
          }
          event.stopPropagation();
        };
        var layoutSlider = (self.layoutSlider = $(muiSlider).slider({
          snap: '.mui-slider-item:not(.mui-hidden)',
          scrollTime: 0
        }));
        // 解决侧滑支持隐藏，不覆盖原方法，只覆盖实例方法
        layoutSlider._handleSlide = $.Slider.prototype.handleVisiableSlide;
        // placeHolder.innerHTML = placeHolder.innerHTML + html.toString();
        for (var i = 0; i < layouts.length; i++) {
          var layout = layouts[i];
          var contentHolder = placeHolder.querySelector('#tab_' + layout.id + ' .mui-scroll');
          // var children = self._mergeChirldren(layout);
          // self._appendChildren(contentHolder, children);
          self._appendContent(contentHolder, layout, blockLayout);
        }

        //默认选中第一个
        //var firstTab = placeHolder.querySelector('.mui-control-item');
        //firstTab.classList.add("mui-active");
      },
      _appendTobTab: function (placeHolder, blocks) {
        var _self = this;
        var html = new StringBuilder();
        html.appendFormat('<div class="mui-content mui-top-tabs mui-fullscreen">');
        html.appendFormat('<div class="mui-segmented-control">');
        for (var i = 0; i < blocks.length; i++) {
          var blockClass = blocks[i].isShow ? 'mui-control-item' : 'mui-control-item mui-hidden';
          html.appendFormat(
            '<a class="{2}" href="#block_{0}" code="block_{0}"> {1} </a>',
            blocks[i].blockCode,
            blocks[i].blockTitle,
            blockClass
          );
        }
        html.appendFormat('</div>');
        html.appendFormat('<div class="mui-top-tab-group">');
        for (var i = 0; i < blocks.length; i++) {
          html.appendFormat(
            '<div id="block_{0}" class="mui-control-content mui-top-tab-content mui-scroll-wrapper"><div class="mui-block-content mui-scroll mui-input-group"></div></div>',
            blocks[i].blockCode
          );
        }
        html.appendFormat('</div>');
        html.appendFormat('</div>');
        var domList = $.dom(html.toString());
        for (var i in domList) {
          placeHolder.appendChild(domList[i]); // DyformCommons.dom
        }
        // placeHolder.innerHTML = placeHolder.innerHTML + html.toString();
        for (var i = 0; i < blocks.length; i++) {
          var contentHolder = placeHolder.querySelector('#block_' + blocks[i].blockCode + ' .mui-block-content');
          var children = this._mergeChirldren(blocks[i]);
          this._appendChildren(contentHolder, children);
        }

        //触发第一个tab的tap事件
        var firstTab = placeHolder.querySelector('.mui-control-item');
        firstTab.classList.add('mui-active');
        var blockCode = firstTab.getAttribute('code');
        placeHolder.querySelector('#' + blockCode).classList.add('mui-active');
      },
      _appendTobSlider: function (placeHolder, blocks) {
        var html = new StringBuilder();
        // layout-slider全局唯一
        html.appendFormat('<div class="mui-slider layout-slider mui-fullscreen">');
        html.appendFormat('<div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">');
        html.appendFormat('	<div class="mui-scroll">');
        for (var i = 0; i < blocks.length; i++) {
          var isShow = blocks[i].isShow;
          var className = 'mui-control-item' + (isShow ? '' : ' mui-hidden');
          html.appendFormat(
            '<a class="{2}" href="#block_{0}" code="block_{0}"> {1} </a>',
            blocks[i].blockCode,
            blocks[i].blockTitle,
            className
          );
        }
        html.appendFormat('</div>');
        html.appendFormat('</div>');

        html.appendFormat('<div class="mui-slider-group" > ');
        for (var i = 0; i < blocks.length; i++) {
          var isShow = blocks[i].isShow;
          var className = 'mui-slider-item mui-control-content' + (isShow ? '' : ' mui-hidden');
          html.appendFormat('	<div id="block_{0}" class="{1}">', blocks[i].blockCode, className);
          html.appendFormat('	<div class="mui-scroll-wrapper">');
          html.appendFormat('	<div class="mui-scroll mui-input-group">');
          html.appendFormat('	</div >');
          html.appendFormat('	</div >');
          html.appendFormat('	</div >');
        }
        html.appendFormat('</div>');
        //填充隐藏的字段
        html.appendFormat('<div class="mui-hidden">');

        html.appendFormat('</div>');

        html.appendFormat('</div>');
        placeHolder.appendChild($.dom(html.toString())[0]); // DyformCommons.dom
        var muiSlider = placeHolder.querySelector('.layout-slider');
        $(muiSlider).slider({
          scrollTime: 0
        });
        // placeHolder.innerHTML = placeHolder.innerHTML + html.toString();
        for (var i = 0; i < blocks.length; i++) {
          var contentHolder = placeHolder.querySelector('#block_' + blocks[i].blockCode + ' .mui-scroll');
          var children = this._mergeChirldren(blocks[i]);
          this._appendChildren(contentHolder, children);
        }

        //默认选中第一个
        var firstTab = placeHolder.querySelector('.mui-control-item');
        firstTab.classList.add('mui-active');
      },
      _appendBottomTab: function (placeHolder, blocks) {
        var _self = this;
        var html = new StringBuilder();
        html.appendFormat('<nav class="mui-bar mui-bar-tab">');
        for (var i = 0; i < blocks.length; i++) {
          var blockClass = blocks[i].isShow ? 'mui-tab-item' : 'mui-tab-item mui-hidden';
          html.appendFormat('<a class="{1}" code="block_{0}" href="#block_{0}">', blocks[i].blockCode, blockClass);
          html.appendFormat('	<span class="mui-tab-label">{0}</span>', blocks[i].blockTitle);
          html.appendFormat('</a>');
        }
        html.appendFormat('</nav>');
        html.appendFormat('<div class="mui-content mui-bottom-tabs mui-fullscreen">');
        for (var i = 0; i < blocks.length; i++) {
          html.appendFormat('<div id="block_{0}" class="mui-control-content mui-bottom-tab-content">', blocks[i].blockCode);
          html.appendFormat('	<div class="mui-scroll-wrapper">');
          html.appendFormat('	<div class="mui-scroll mui-input-group">');
          html.appendFormat('	</div >');
          html.appendFormat('	</div >');
          html.appendFormat('	</div >');
        }
        html.appendFormat('</div>');
        var domList = $.dom(html.toString());
        for (var i in domList) {
          placeHolder.appendChild(domList[i]); // DyformCommons.dom
        }
        for (var i = 0; i < blocks.length; i++) {
          var contentHolder = placeHolder.querySelector('#block_' + blocks[i].blockCode + ' .mui-scroll');
          var children = this._mergeChirldren(blocks[i]);
          this._appendChildren(contentHolder, children);
        }

        //触发第一个tab的tap事件
        var firstTab = placeHolder.querySelector('.mui-tab-item');
        firstTab.classList.add('mui-active');
        var blockCode = firstTab.getAttribute('code');
        placeHolder.querySelector('#' + blockCode).classList.add('mui-active');
      },
      _appendOffCanvas: function (placeHolder, blocks) {
        var _self = this;
        var html = new StringBuilder();
        //侧滑菜单栏的布局html
        var canvasHtml =
          "<div id='layout-off-canvas' class='mui-off-canvas-wrap mui-draggable mui-fullscreen'>" +
          "<aside class='mui-off-canvas-left' style='background-color:#f7f7f7'>" +
          "<div class='mui-scroll-wrapper'>" +
          "<header class='mui-bar mui-bar-nav' style='position:relative!important'>" + //主要是为了占位置加这个header
          '</header>' +
          "<div class='mui-scroll' style='position: relative'>" +
          '{0}' +
          '</div>' +
          '</div>' +
          '</aside>' +
          "<div class='mui-inner-wrap mui-dyform-inner-wrap'>" +
          "<header class='mui-bar mui-bar-nav'>" +
          "<a class='mui-icon mui-action-menu mui-icon-bars mui-pull-left'></a>" +
          "<h1 class='mui-title'>" +
          blocks[0].blockTitle +
          '</h1>' +
          '</header>' +
          "<div class='mui-content mui-scroll-wrapper' style='bottom:50px'>" +
          "<span class='mui-icon mui-icon-forward drag-icon'></span>" +
          "<div class='mui-scroll'>" +
          '{1}' +
          '</div>' +
          '</div>' +
          '</div>' +
          '</div>';

        //菜单栏
        var menuHtml = '';
        for (var i = 0; i < blocks.length; i++) {
          var code = blocks[i].blockCode;
          var title = blocks[i].blockTitle;
          var isShow = blocks[i].isShow;
          if (isShow) {
            var li =
              "<li id='menu_" +
              code +
              "'  class='mui-table-view-cell mui-control-item' href='#block_'" +
              code +
              "' blockCode='" +
              code +
              "' >" +
              title +
              '</li>';
            menuHtml += li;
          }
        }
        menuHtml = "<ul class='mui-table-view'>" + menuHtml + '</ul>';

        //区块内容
        var contentHtml = new StringBuilder();
        contentHtml.appendFormat('<div class="mui-slider-group">');
        for (var i = 0; i < blocks.length; i++) {
          contentHtml.appendFormat(
            '<div id="block_{0}" class="mui-slider-item mui-control-content mui-input-group" title="{1}">',
            blocks[i].blockCode,
            blocks[i].blockTitle
          );
          contentHtml.appendFormat('</div>');
        }
        contentHtml.appendFormat('</div>');

        html.appendFormat(canvasHtml, menuHtml, contentHtml);
        var canvasElement = $.dom(html.toString())[0];
        placeHolder.appendChild(canvasElement);

        //填充内容
        for (var i = 0; i < blocks.length; i++) {
          var contentHolder = placeHolder.querySelector('#block_' + blocks[i].blockCode);
          var children = this._mergeChirldren(blocks[i]);
          this._appendChildren(contentHolder, children);
        }

        var offCanvas = $('#layout-off-canvas').offCanvas();
        //捆绑左滑事件，默认关闭状态
        placeHolder.querySelector('.mui-action-menu').addEventListener('tap', function (event) {
          offCanvas.toggle();
        });
        //绑定拖拉按钮事件
        placeHolder.querySelector('.drag-icon').addEventListener('tap', function (event) {
          offCanvas.toggle();
        });

        //捆绑菜单的点击事件
        var titleEle = placeHolder.querySelector('.mui-title');
        var menus = placeHolder.querySelectorAll('.mui-control-item');
        for (var i = 0; i < menus.length; i++) {
          menus[i].addEventListener('tap', function (event) {
            //关闭菜单，切换内容
            var blockCode = this.getAttribute('blockCode');
            var contents = placeHolder.querySelectorAll('.mui-control-content');
            var menuId = this.getAttribute('id');

            for (var j = 0; j < menus.length; j++) {
              if (menuId == menus[j].getAttribute('id')) {
                menus[j].classList.add('menu-seleccted');
              } else {
                menus[j].classList.remove('menu-seleccted');
              }
            }

            for (var k = 0; k < contents.length; k++) {
              if (contents[k].getAttribute('id') == 'block_' + blockCode) {
                contents[k].classList.add('mui-active');
                titleEle.innerHTML = contents[k].getAttribute('title');
              } else {
                contents[k].classList.remove('mui-active');
              }
            }
            try {
              if (offCanvas.offCanvas == null) {
                offCanvas.offCanvas = document.querySelector('aside.mui-off-canvas-left');
              }
              offCanvas.close();
            } catch (e) {
              console.error(e);
            }
          });
        }

        //显示第一菜单的内容
        //必须先展开菜单栏，否则触发tap事件会报错
        offCanvas.show();
        $.trigger(menus[0], 'tap');

        //添加滚动事件
        $('.mui-scroll-wrapper', placeHolder).scroll();
      },

      _mergeChirldren: function (block) {
        var children = [];
        if (block.fields) {
          for (var key in block.fields) {
            var child = $.extend(
              {
                type: 'field'
              },
              block.fields[key]
            );
            children.push(child);
          }
        }

        if (block.subforms) {
          for (var key in block.subforms) {
            var child = $.extend(
              {
                type: 'subform'
              },
              block.subforms[key]
            );
            children.push(child);
          }
        }
        if (block.labels) {
          for (var key in block.labels) {
            var child = $.extend(
              {
                type: 'label'
              },
              block.labels[key]
            );
            children.push(child);
          }
        }
        if (block.layouts) {
          for (var key in block.layouts) {
            var child = $.extend(
              {
                type: 'layout'
              },
              block.layouts[key]
            );
            children.push(child);
          }
        } else if (block.blocks) {
          for (var key in block.blocks) {
            var child = $.extend(
              {
                type: 'block'
              },
              block.blocks[key]
            );
            children.push(child);
          }
        }
        children.sort(function (child, child2) {
          return child.order - child2.order;
        });
        return children;
      },
      _appendBlock: function (contentHolder, block) {
        if (block.blockLayout == '2') {
          this._appendMuiCollapse(contentHolder, block);
        } else {
          this._appendMuiCard(contentHolder, block);
        }
      },
      _appendChild: function (placeHolder, child) {
        if (child.type == 'block') {
          this._appendBlock(placeHolder, child);
        } else if (child.type == 'field') {
          this._appendField(placeHolder, child);
        } else if (child.type == 'subform') {
          this._appendSubform(placeHolder, child);
        } else if (child.type == 'label') {
          this._appendLabel(placeHolder, child);
        }
      },
      _appendChildren: function (placeHolder, children) {
        for (var i = 0; i < children.length; i++) {
          this._appendChild(placeHolder, children[i]);
        }
      },
      _appendMuiCollapse: function (placeHolder, block) {
        var _self = this;
        var html = new StringBuilder();
        var hideClass = block.isShow == null || block.isShow ? '' : 'mui-hidden';
        html.appendFormat('<div class="mui-table-view dyform-tab-view {1}" id="block_{0}"> ', block.blockCode, hideClass);
        html.appendFormat('	<div class="mui-table-view-cell mui-collapse">');
        html.appendFormat('		<a class="mui-navigate-right" href="#block_{0}" code="block_{0}">{1}</a>', block.blockCode, block.blockTitle);
        html.appendFormat('		<div class="mui-collapse-content mui-input-group"></div>');
        html.appendFormat('	</div>');
        html.appendFormat('</div>');
        placeHolder.appendChild($.dom(html.toString())[0]); // DyformCommons.dom
        // placeHolder.innerHTML = placeHolder.innerHTML + html.toString();
        var contentHolder = placeHolder.querySelector('#block_' + block.blockCode + ' .mui-collapse-content');
        var children = _self._mergeChirldren(block);
        _self._appendChildren(contentHolder, children);
      },
      _appendMuiCard: function (placeHolder, block) {
        var _self = this;
        var html = new StringBuilder();
        var hideClass = block.isShow == null || block.isShow ? '' : 'mui-hidden';
        html.appendFormat('<div class="mui-card {1}" id="block_{0}">', block.blockCode, hideClass);
        html.appendFormat(' <div class="mui-card-header">{0}</div>', block.blockTitle);
        html.appendFormat('	<div class="mui-card-content mui-input-group">');
        html.appendFormat('	</div>');
        html.appendFormat('</div>');
        placeHolder.appendChild($.dom(html.toString())[0]); // DyformCommons.dom
        // placeHolder.innerHTML = placeHolder.innerHTML + html.toString();
        var contentHolder = placeHolder.querySelector('#block_' + block.blockCode + ' .mui-card-content');
        var children = _self._mergeChirldren(block);
        _self._appendChildren(contentHolder, children);
      },
      _appendLabel: function (placeHolder, label) {
        var html = new StringBuilder();
        var hideClass = label.isShow == null || label.isShow ? '' : 'mui-hidden';
        html.appendFormat('<div class="mui-input-row {0}">', hideClass);
        html.appendFormat('	<label style="width:100%">{0}</label>', label.html || label.text);
        html.appendFormat('</div>');
        placeHolder.appendChild($.dom(html.toString())[0]);
      },
      _appendSubform: function (placeHolder, subform) {
        var _self = this;
        var subformDefinitions = _self.subformDefinitions;
        $.each(subformDefinitions, function (i, subformDefinition) {
          if (subformDefinition.uuid == subform.formUuid || subformDefinition.id == subform.formId) {
            subform = $.extend({}, subformDefinition, subform);
          }
        });
        var formData = this._getMainFormData();
        var fieldValue = _self._getFormData(subform.uuid);
        var option = {
          formScope: _self._getFormScope(),
          fieldDefinition: subform,
          fieldValue: fieldValue
        };
        var fieldModuleName = DyformConstant.muiFieldModule.subform;
        var fieldModule = require(fieldModuleName);
        // 按字段类型模块初始化
        var formField = new fieldModule($(_self._getFieldWrapper(placeHolder, formData)), option);
        if (fieldModule === null || typeof fieldModule === 'undefined') {
          return console.log("can't not find controlModule：" + fieldModuleName);
        }
        formField.init();
        // formField.render();
        _self.formFields.push(formField);
        // 存储字段映射
        _self.formFieldMap[formField.getId()] = formField;
      },
      _getFieldWrapper: function (placeHolder, formData) {
        var formData = this._getMainFormData();
        var wrapper = document.createElement('div');
        var fieldContainer = 'dyts' + formData.uuid + '_default_tab';
        wrapper.id = fieldContainer;
        // wrapper.classList.add("mui-input-group");
        placeHolder.appendChild(wrapper);
        return wrapper;
      },
      _appendField: function (placeHolder, field) {
        var _self = this;
        var fields = _self.formDefinition.fields;
        if (field.name in fields) {
          field = $.extend({}, fields[field.name], field);
        }
        var formData = this._getMainFormData();
        var option = {
          formScope: _self._getFormScope(),
          fieldDefinition: field,
          fieldValue: formData[field.name]
        };
        var inputMode = field.inputMode;
        if (inputMode == null || typeof inputMode === 'undefined' || inputMode == '') {
          return console.log('unknown inputMode:' + inputMode);
        }
        // 按字段类型模块初始化
        var fieldModuleName = DyformConstant.muiFieldModule[inputMode];
        if (fieldModuleName === null || typeof fieldModuleName === 'undefined') {
          return console.log("can't not find inputMode：" + inputMode);
        }
        var fieldModule = require(fieldModuleName);
        if (fieldModule === null || typeof fieldModule === 'undefined') {
          return console.log("can't not find controlModule：" + fieldModuleName);
        }
        var formField = new fieldModule($(_self._getFieldWrapper(placeHolder, formData)), option);
        formField.init();
        // formField.render();
        _self.formFields.push(formField);
        // 存储字段映射
        _self.formFieldMap[formField.getId()] = formField;
      },
      // 将PC端隐藏的区块合并到手机，隐藏以PC端优先(流程设置)
      _mergeBlocks: function () {
        var _self = this;
        var blocks = _self.formDefinition.blocks;
        if (_self.mobileConfiguration == null) {
          // 旧表单区块编号为空处理
          $.each(blocks, function (i, block) {
            if (StringUtils.isBlank(block.blockCode)) {
              block.blockCode = commons.UUID.createUUID();
            }
          });
          return;
        }
        var mobileBlocks = _self.mobileConfiguration.blocks,
          layouts;
        if (null == mobileBlocks && (layouts = _self.mobileConfiguration.layouts)) {
          mobileBlocks = {};
          for (var layoutId in layouts) {
            // 浅拷贝设置isShow属性
            $.extend(mobileBlocks, layouts[layoutId].blocks);
          }
        }
        if (blocks == null || mobileBlocks == null) {
          return;
        }
        $.each(blocks, function (i, block) {
          if (block.hide !== true) {
            return;
          }
          for (var p in mobileBlocks) {
            if (mobileBlocks[p].blockCode === block.blockCode) {
              mobileBlocks[p].isShow = false;
            }
          }
        });
      },
      // 将主表定义的从表字段信息合并到相应的从表定义的字段定义
      _mergeSubformFields: function () {
        var _self = this;
        $.each(_self.formDefinition.subforms, function (i, subformConfig) {
          var subformId = subformConfig.id;
          var subformUuid = subformConfig.formUuid;
          var fields = subformConfig.fields;
          // 是否隐藏操作按钮
          var hideButtons = subformConfig.hideButtons === DyformConstant.dySubFormHideButtons.hide;
          $.each(_self.subformDefinitions, function (i, subformDefinition) {
            if (subformUuid == subformDefinition.uuid || subformId == subformDefinition.id) {
              var subformFields = subformDefinition.fields;
              $.each(fields, function (key) {
                var field = fields[key];
                var subformField = subformFields[key];
                if (subformField != null) {
                  for (var p in field) {
                    // 合并表单从表设置的不可编辑
                    if (p === 'editable' && field[p] === '0') {
                      // 显示模式显示为文本
                      subformField.showType = DyformConstant.dyshowType.showAsLabel;
                    }
                    subformField[p] = field[p];
                  }
                }
              });
              subformDefinition.hideButtons = hideButtons;
            }
          });
        });
      },
      _prepareFields: function (fieldModules, blocks) {
        var _self = this;
        var fields = _self.formDefinition.fields;
        $.each(blocks, function (key, block) {
          $.each(block.fields || {}, function (key, field) {
            if (field.name in fields) {
              field = $.extend({}, fields[field.name], field);
            }
            var fieldModule = DyformConstant.muiFieldModule[field.inputMode];
            if (StringUtils.isNotBlank(fieldModule) && fieldModules.indexOf(fieldModule) == -1) {
              fieldModules.push(fieldModule);
            }
          });
          if (false === $.isEmptyObject(block.blocks)) {
            _self._prepareFields(fieldModules, block.blocks);
          }
        });
      },
      // 预处理
      _prepare: function (callback) {
        var _self = this;
        var fields = _self.formDefinition.fields;
        // 按字段类型加载相应的模块

        if (!_self.mobileConfiguration) {
          _self._initDefaultMobileConfiguration();
        }
        var fieldModules = [];
        // 二开JS模块
        if (StringUtils.isNotBlank(_self.customJsModule)) {
          fieldModules.push(_self.customJsModule);
        }

        var mobileConfiguration = _self.mobileConfiguration;
        var isLayout = mobileConfiguration.layouts != null && typeof mobileConfiguration.layouts !== 'undefined';
        _self.isLayout = mobileConfiguration.isLayout = isLayout;
        _self._prepareFields(fieldModules, isLayout ? mobileConfiguration.layouts : mobileConfiguration.blocks);
        if (_self.formDefinition.subforms) {
          fieldModules.push(DyformConstant.muiFieldModule.subform);
          $.each(_self.subformDefinitions, function (i, subformDefinition) {
            $.each(subformDefinition.fields, function (i, fieldDefinition) {
              var fieldModule = DyformConstant.muiFieldModule[fieldDefinition.inputMode];
              if (StringUtils.isNotBlank(fieldModule) && fieldModules.indexOf(fieldModule) == -1) {
                fieldModules.push(fieldModule);
              }
            });
          });
        }
        appContext.require(fieldModules, callback);
      },

      _render: function () {
        var _self = this;
        $.each(_self.formFields, function (i, formField) {
          // 渲染
          formField.render();
          // 标记字段初始化完成
          formField.initComplete = true;
        });
      },
      _getMainFormData: function () {
        var self = this;
        var defaultFormData = self.formDefinition.defaultFormData;
        var mfd = self.formData.formDatas[this.formData.formUuid];
        var formData = mfd == null || mfd.length == 0 ? {} : mfd[0];
        return $.extend(true, defaultFormData, formData);
      },
      _getFormData: function (formUuid) {
        var mfd = this.formData.formDatas[formUuid];
        return mfd == null || mfd.length == 0 ? [] : mfd;
      },
      createDyformScope: function (formDefinition, formFieldMap, formData) {
        return new DyformScope(this, formDefinition, formFieldMap, formData);
      },
      _initDefaultMobileConfiguration: function () {
        var _self = this;
        var mobileConfiguration = {
          blockLayout: '1',
          blocks: {}
        };
        var blocks = _self.formDefinition.blocks;
        var fields = _self.formDefinition.fields;
        var subforms = _self.formDefinition.subforms;
        // 处理区块信息start
        var html = _self.formDefinition.html || ''; // 手机端的html配置在WorkServiceImpl.extractDyformData中被去掉，需要放开
        var definitionPtVersion = _self.formDefinition.definitionPtVersion;
        _self.isNotPtVersion = definitionPtVersion == null || typeof definitionPtVersion === 'undefined';
        var formEle = mui.dom('<div>' + html.replace(/ src=/g, ' data-src=') + '</div>')[0];
        mui('.title[blockcode][colspan]', formEle).each(function (idx, blockEle) {
          var blockCode = blockEle.getAttribute('blockcode');
          var blockEle = $(blockEle).closest('table');
          if (blockEle == null || typeof blockEle === 'undefined') {
            return; // continue;
          }
          // var blockTitle = blockEle.innerText || blocks[blockCode].blockTitle;
          mui('label.label', blockEle).each(function (idx, labelEle) {
            // 默认标签不做处理
          });
          mui('.value[name]', blockEle).each(function (idx, fieldEle) {
            var name = fieldEle.getAttribute('name');
            var field = fields[name];
            if (field == null || typeof field === 'undefined') {
              return; // continue;
            }
            var block = (field.block = field.block || {});
            block.blockCode = block.blockCode || blockCode;
          });
          mui('table[formuuid]', blockEle).each(function (idx, tableEle) {
            var formUuid = tableEle.getAttribute('formuuid');
            var subformDef = subforms[formUuid];
            if (subformDef == null || typeof subformDef === 'undefined') {
              return; // continue;
            }
            var block = (subformDef.block = subformDef.block || {});
            block.blockCode = block.blockCode || blockCode;
          });
        });
        // 处理区块信息end
        var index = 0;
        $.each(blocks, function (key, block) {
          var blockNode = {
            blockCode: block.blockCode,
            ParentBlockCode: '',
            blockTitle: block.blockTitle,
            blockLayout: '1',
            isShow: !block.hide,
            order: index++,
            fields: {},
            subforms: {},
            blocks: {}
          };
          mobileConfiguration.blocks[block.blockCode] = blockNode;
        });
        mobileConfiguration.blocks.other = {
          blockCode: 'other',
          ParentBlockCode: '',
          blockTitle: '其他',
          blockLayout: '1',
          order: index++,
          isShow: true,
          fields: {},
          subforms: {},
          blocks: {}
        };
        index = 0;
        $.each(fields, function (key, field) {
          var blockCode = field.block ? field.block.blockCode : 'other';
          if (!(blockCode in mobileConfiguration.blocks)) {
            blockCode = 'other';
          }
          var fieldNode = {
            name: field.name,
            displayName: field.displayName,
            show: field.showType != 5,
            order: index++,
            blockCode: blockCode,
            dispalyStyle: 1,
            displayTemplate: ''
          };
          mobileConfiguration.blocks[blockCode].fields[field.name] = fieldNode;
        });

        index = 1000;
        $.each(subforms, function (key, subform) {
          var blockCode = subform.block ? subform.block.blockCode : 'other';
          if (!(blockCode in mobileConfiguration.blocks)) {
            blockCode = 'other';
          }
          var subformNode = {
            formUuid: subform.formUuid,
            displayName: subform.displayName,
            isShow: true,
            order: index++,
            blockCode: blockCode,
            dispalyStyle: 1
          };
          mobileConfiguration.blocks[blockCode].subforms[subform.name] = subformNode;
        });
        _self.mobileConfiguration = mobileConfiguration;
      },

      trigger: function (eventType, eventData) {
        $.trigger(this.element[0], eventType, eventData);
      },
      on: function (event, selector, callback) {
        $(this.element).on(event, selector, callback);
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
      }
    },
    DyformExplain.fn
  );

  // PC端操作处理
  $.extend(DyformExplain.fn, {
    dyform: function (method) {
      var self = this;
      var fn = self[method];
      if ($.isFunction(fn)) {
        return fn.apply(self, Array.prototype.slice.call(arguments, 1));
      }
      console.error('dyform.method[' + method + '] is undefined');
    }
  });

  $.each(DyformExplain.fn, function (fk, fv) {
    $.fn[fk] = function () {
      var self = this[0];
      if (false === self instanceof DyformExplain) {
        self = $.data[self.getAttribute('DyformExplain')] || self;
      }
      return fv.apply(self, arguments);
    };
  });
  return DyformExplain;
});

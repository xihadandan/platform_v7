define([
  'jquery',
  'server',
  'commons',
  'constant',
  'appContext',
  'appModal',
  'DmsDataServices',
  'DmsDocumentViewDevelopment',
  'DmsActionDispatcher',
  'DyformExplain'
], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  DmsDataServices,
  DmsDocumentViewDevelopment,
  DmsActionDispatcher,
  DyformExplain
) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  // 数据管理单据开发
  var BizDataView = function () {
    DmsDocumentViewDevelopment.apply(this, arguments);
  };
  commons.inherit(BizDataView, DmsDocumentViewDevelopment, {
    // 初始化
    init: function (options) {},
    // 加载数据
    load: function () {},
    // 初始化数据加载的数据
    initLoadedData: function (result) {
      var _self = this;
      _self.bizData = result.data;
      // _self.actions = _self._wrapperActions(result.actions);
      _self.dyFormData = _self.bizData.dyFormData;
      _self.initDyform();
      _self.createSidebar();
    },
    // 初始化表单数据
    initDyform: function () {
      var _self = this;
      var dyFormData = _self.dyFormData;
      var displayAsLabel = _self.isDyformDisplayAsLabel();
      var dataUuid = _self.options.dataUuid;
      var dyformDevelopment = {};
      try {
        var dyformOptions = {
          renderTo: _self.dyformSelector,
          formData: dyFormData,
          displayAsLabel: displayAsLabel,
          optional: {
            isFirst: StringUtils.isBlank(dataUuid)
          },
          success: function () {
            _self.dyform = this;
            _self.createActionButtons();
            _self.onDyformInitSuccess.apply(_self, arguments);
          },
          error: function () {
            _self.dyform = this;

            _self.onDyformInitFailure.apply(_self, arguments);
          },
          dyformDevelopment: dyformDevelopment //表单二开
        };
        // 准备初始化表单
        _self.prepareInitDyform(dyformOptions);
        _self.dyform = new DyformExplain($(_self.dyformSelector), dyformOptions);
      } catch (e) {
        appModal.error('表单解析失败： ' + e);
        throw e;
      }
    },
    // 创建右边栏
    createSidebar: function () {},
    // 表单是否显示为文本
    isDyformDisplayAsLabel: function () {
      return this.bizData.displayAsLabel;
    },
    // 准备初始化表单
    prepareInitDyform: function (dyformOptions) {},
    // 表单初始化成功回调
    onDyformInitSuccess: function () {
      var _self = this;
      // 输入的动态表单值
      var extras = _self.getExtras();
      for (var key in extras) {
        if (key.length > 8 && key.substring(0, 8) == 'ep_dyfs_') {
          var fieldName = key.substring(8);
          var fieldVal = extras[key];
          _self.dyform.setFieldValue(fieldName, fieldVal);
        }
      }
    },
    // 表单初始化失败回调
    onDyformInitFailure: function () {},
    // 获取表单对象
    getDyform: function () {
      return this.dyform;
    },
    // 获取表单数据
    getDyformData: function (fnCallback) {
      this.dyform.collectFormData(fnCallback, function (errorInfo) {
        appModal.error('表单数据收集失败[ + ' + JSON.stringify(errorInfo) + ']，无法提交数据！');
        throw e;
      });
    },
    // 验证表单数据
    validateDyformData: function () {
      return this.dyform.validateForm();
    },
    // 执行验证
    validate: function () {
      var _self = this;
      var result = false;
      try {
        result = _self.validateDyformData();
      } catch (e) {
        console.error(e);
        appModal.error('表单数据验证异常！');
      }
      return result;
    },
    // 创建操作按钮
    createActionButtons: function () {
      var _self = this;
      var actions = _self.getActions();
      _self.actionMap = {};
      // 生成按钮
      var button = '<button btnId="{0}" name="{1}" class="btn btn-primary btn-minier smaller {3}">{2}</button>';
      // 操作按钮占位符
      var toolbarPlaceholder = _self.options.toolbarPlaceholder;
      var sb = new StringBuilder();

      $.each(actions, function (i, action) {
        // 隐藏按钮
        if (action.hidden === '1' || action.hidden == true) {
          return;
        }

        var btnId = action.id;
        var text = action.name;
        var cssClass = action.cssClass || '';
        var disabled = false;
        var code = action.code;

        _self.actionMap[btnId] = action;
        sb.appendFormat(
          '<button type="button" btnId="{0}" name="{0}" class="well-btn w-btn-primary {2} {4}" {3} code="{5}">{1}</button>',
          btnId,
          text,
          cssClass,
          disabled ? 'disabled' : '',
          disabled ? 'w-disable-btn' : '',
          code
        );
      });

      $(toolbarPlaceholder, _self.$element).append(sb.toString());
      // 绑定事件
      $(toolbarPlaceholder, _self.$element).on('click', 'button', function (e) {
        var btnId = $(this).attr('btnId');
        if (!btnId) {
          return;
        }
        var action = _self.actionMap[btnId];
        if (action == null) {
          console.error('button of id[' + btnId + '] is not found');
          return;
        }
        _self.currentEvent = e;
        _self.performed($.extend(true, {}, action));
        _self.currentEvent = null;
      });
    },
    getActions: function () {
      var _self = this;
      return [];
    },
    triggerActionById: function (actionId) {
      var _self = this;
      var toolbarPlaceholder = _self.options.toolbarPlaceholder;
      $(`button[btnId='${actionId}']`, $(toolbarPlaceholder)).trigger('click');
    },
    // 执行操作
    performed: function (action) {
      var _self = this;
      var extras = _self.getExtras();
      _self.getDyformData(function (dyFormData) {
        var urlParams = {
          dms_id: _self.dmsId,
          ac_id: action.id
        };
        // 验证处理
        if (action.validate === true && !_self.validate(action)) {
          return;
        }
        // 解析事件参数作为附加传递的参数
        // 原始参数
        var rawParams = action.params || {};
        var params = $.extend(true, {}, rawParams);
        var eventParams = appContext.resolveParams(params, _self.options);
        var data = {
          action: action,
          extras: $.extend(true, extras, eventParams),
          formUuid: _self.formUuid,
          dataUuid: _self.dataUuid,
          dyFormData: dyFormData,
          actionMap: _self.actionMap
        };
        data.extras.ep_target = _self.options.target;
        var options = {
          urlParams: urlParams,
          params: eventParams,
          rawParams: rawParams,
          data: data,
          appType: action.appType,
          appPath: action.appPath,
          appFunction: action,
          action: action.id,
          ui: _self,
          event: _self.currentEvent,
          appContext: appContext
        };
        if ($.isFunction(action.success)) {
          options.success = action.success;
        }
        // 准备执行操作
        _self.preparePerformed(options);
        // 默认派发器派发处理
        if (StringUtils.isNotBlank(action.appPath)) {
          appContext.getDispatcher().dispatch(options);
        } else if ($.isFunction(_self[action.id]) && StringUtils.isNotBlank(action.id)) {
          // 调用自身编码一样的函数
          _self[action.id].call(_self, options);
        } else {
          // 单据操作派发处理
          appContext.executeJsModule(DmsActionDispatcher, options);
        }
      });
    },
    // 准备执行操作
    preparePerformed: function (performedOptions) {},
    // 重新加载单据
    reload: function (itemInstUuid) {},
    // 打印表单
    printForm() {
      var _self = this;
      _self.dyform.$dyform.find('textarea').each(function () {
        if (this.value) {
          this.innerHTML = this.value;
        }
        console.log(this);
      });
      _self.dyform.$dyform.printArea();
    }
  });
  return BizDataView;
});

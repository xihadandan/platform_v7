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
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var dmsDataServices = new DmsDataServices();
  // 获取表单数据操作ID
  var DYFORM_ACTION_GET = 'btn_dyform_get';
  // 数据管理表单单据开发
  var DmsDyformDocumentView = function () {
    DmsDocumentViewDevelopment.apply(this, arguments);
    this.dmsDataServices = dmsDataServices;
  };
  commons.inherit(DmsDyformDocumentView, DmsDocumentViewDevelopment, {
    // 初始化
    init: function (options) {
      var _self = this;
      options.extraParams = options.extraParams || {};
      _self.options = options;
      _self.dmsId = options.dmsId;
      _self.formUuid = options.formUuid;
      _self.dataUuid = options.dataUuid;
      _self.dyformSelector = options.dyformSelector;
    },

    createRightSidebarLayout: function () {
      var _self = this;
      if (StringUtils.isBlank(_self.dmsId)) {
        return;
      }
      var dmsDef = appContext.getWidgetDefinition(_self.dmsId);
      var dmsdocument = JSON.parse(dmsDef.definitionJson).configuration.document;
      if (!dmsdocument.rightSlidebarTabs || dmsdocument.rightSlidebarTabs.length == 0) {
        return;
      }
      appContext.require(['AppTabs'], function () {
        //右侧标签页
        $.fn.appTabs({
          siblings: $('#dyform-container'),
          tabs: (function () {
            return _self.getSidebarTabData(dmsdocument);
          })(), //tab数据项
          tabShowType: dmsdocument.tabShowType, //浮动展现
          defaultShowTab: dmsdocument.defaultShowTab, //默认展示的标签页
          dragable: dmsdocument.dragableTabWidth, //是否可拖动
          tabPrefix: 'dyfrom_app_tab_',
          top: $('.widget-header').height(), //距离顶部距离
          bottom: $('.footer').outerHeight(), //距离底部距离
          tabWidth: '30%',
          successCallback: function () {
            $('#dyform-container').css('padding-right', 11);
          }
        });
      });
    },
    // 获取侧边tab数据
    getSidebarTabData: function (dmsdocument) {
      var _self = this;
      var tabs = [];
      for (var i = 0, len = dmsdocument.rightSlidebarTabs.length; i < len; i++) {
        var t = dmsdocument.rightSlidebarTabs[i];
        tabs.push({
          name: t.name,
          appType: t.eventHandler.type,
          appPath: t.eventHandler.path,
          appContext: appContext,
          formUuid: _self.formUuid,
          dataUuid: _self.dataUuid,
          dmsId: _self.dmsId,
          dmsDataServices: dmsDataServices,
          ui: _self
        });
      }
      return tabs;
    },
    // 获取加载数据的操作ID
    getLoadDataActionId: function () {
      var _self = this;
      // 如果传入获取数据的action，则使用该action
      var extraParams = _self.options.extraParams || {};
      return extraParams['ep_ac_get'] || DYFORM_ACTION_GET;
    },
    // 加载数据
    load: function () {
      var _self = this;
      var success = function () {
        _self.onLoadSuccess.apply(_self, arguments);
        _self.initDocumentLoadedData.apply(_self, arguments);
      };
      var failure = function () {
        _self.onLoadFailure.apply(_self, arguments);
      };
      var dyformLoadActionId = _self.getLoadDataActionId();
      var extras = _self.getExtras();
      var urlParams = {
        dms_id: _self.dmsId,
        ac_id: dyformLoadActionId
      };
      var data = $.extend({}, _self.options, {
        extras: extras,
        formUuid: _self.formUuid,
        dataUuid: _self.dataUuid
      });

      // dmsDataServices.performed({
      // urlParams : urlParams,
      // data : data,
      // success : success,
      // failure : failure
      // });
      var options = {
        urlParams: urlParams,
        data: data,
        success: success,
        failure: failure,
        ui: _self
      };
      // 准备加载数据
      _self.prepareLoad(options);
      // 单据操作派发处理
      appContext.executeJsModule(DmsActionDispatcher, options);
    },
    // 准备加载数据
    prepareLoad: function (loadOptions) {},
    // 初始化文档加载的数据
    initDocumentLoadedData: function (result) {
      var _self = this;
      _self.documentData = result;
      _self.actions = _self._wrapperActions(result.actions);
      _self.dyFormData = result.dyFormData;
      _self.initDyform();
    },
    // 获取文档数据
    getDocumentData: function () {
      var _self = this;
      if (!_self.documentData) {
        _self.documentData = {};
      }
      return _self.documentData;
    },
    // 获取打开窗口参数
    getUrlParam: function (name, defaultValue) {
      var self = this;
      var value = Browser.getQueryString(name);
      if (typeof value === 'undefined' || value == null) {
        value = self.options[name];
      }
      return value == null ? defaultValue : value;
    },
    // 获取文档额外数据
    getExtras: function () {
      var _self = this;
      var docData = _self.getDocumentData();
      var extras = docData.extras;
      if (extras == null) {
        extras = {};
        var extraParams = _self.options.extraParams;
        for (var p in extraParams) {
          extras[p] = extraParams[p];
        }
        docData.extras = extras;
      }
      return extras;
    },
    // 获取文档额外数据
    getExtra: function (key) {
      return this.getExtras()[key];
    },
    // 设置文档额外数据
    setExtra: function (key, value) {
      this.getExtras()[key] = value;
    },
    // 包装操作对象，将properties属性的key、value合并到action中
    _wrapperActions: function (actions) {
      if (!actions) {
        return actions;
      }
      $.each(actions, function (i, action) {
        var properties = action.properties;
        for (var p in properties) {
          // 名称不复制
          if (p === 'name') {
            continue;
          }
          action[p] = properties[p];
        }
      });
      return actions;
    },
    // 初始化表单数据
    initDyform: function () {
      var _self = this;
      var dyFormData = _self.dyFormData;
      var displayAsLabel = _self.documentData.displayAsLabel;
      var acId = _self.options.acId;
      var dataUuid = _self.options.dataUuid;
      var dyformDevelopment = {};
      if (_self.onBeforeSetData) {
        dyformDevelopment.beforeSetData = function () {
          _self.dyform = this.dyform; //给数据管理对象提供一个操作表单的钩子
          _self.onBeforeSetData.apply(_self, arguments);
        };
      }

      if (_self.onAfterSetData) {
        dyformDevelopment.afterSetData = function () {
          _self.dyform = this.dyform; //给数据管理对象提供一个操作表单的钩子
          _self.onAfterSetData.apply(_self, arguments);
        };
      }
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
            // 已过时
            _self.onInitDyformSuccess.apply(_self, arguments);
            //如果表单解析嵌入为iframe，则加载完成通知父级窗口
            var iframeid = commons.Browser.getQueryString('iframeid');
            if (iframeid) {
              if (window.parent && window.parent.dmsDialogEvent && window.parent.dmsDialogEvent[iframeid]) {
                window.parent.dmsDialogEvent[iframeid].onDyformInitSuccess(_self.dyform);
              }
            }
          },
          error: function () {
            _self.dyform = this;

            _self.onDyformInitFailure.apply(_self, arguments);
            // 已过时
            _self.onInitDyformFailure.apply(_self, arguments);
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
    // 表单初始化成功回调
    onDyformInitSuccess: function () {
      var _self = this;
      // 输入的动态表单值
      var acId = _self.options.acId;
      // 如果是复制操作事件，则需要清掉dyFormData的UUID, 以及对应的从表的dataUuid
      if ('btn_list_view_copy' == acId) {
        $(_self.dyformSelector).dyform('reFillFormData', true);
      }
      var extras = _self.getExtras();
      for (var key in extras) {
        if (key.length > 8 && key.substring(0, 8) == 'ep_dyfs_') {
          var fieldName = key.substring(8);
          var fieldVal = extras[key];
          _self.dyform.setFieldValue(fieldName, fieldVal);
        }
      }
      if (false === appContext.isMobileApp() && _self.isCreateRightSidebarLayout()) {
        _self.createRightSidebarLayout();
      }
    },
    isCreateRightSidebarLayout: function () {
      return StringUtils.isNotBlank(this.options.dataUuid);
    },
    // 表单初始化失败回调
    onDyformInitFailure: function () {},
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
      var requestid = commons.Browser.getQueryString('_requestCode'),
        item;
      if (requestid) {
        item = window.sessionStorage.getItem(requestid + '_dataStoreParams');
        if (item) {
          item = JSON.parse(item);
        } else {
          item = window.opener && window.opener.sessionStorage.getItem(requestid + '_dataStoreParams');
          if (item) {
            window.sessionStorage.setItem(requestid + '_dataStoreParams', item);
            item = JSON.parse(item);
          }
        }
      }

      $.each(actions, function (i, action) {
        // 隐藏按钮
        if (action.hidden === '1' || (['btn_dyform_prev_document', 'btn_dyform_next_document'].indexOf(btnId) != -1 && !_self.dataUuid)) {
          return;
        }
        _self.actionMap[action.id] = action;
        var btnId = action.id,
          disabled = false;
        var name = action.id;
        var text = action.name;
        var cssClass = action.cssClass || '';

        if ((btnId == 'btn_dyform_prev_document' && item && item.first) || (btnId == 'btn_dyform_next_document' && item && item.last)) {
          disabled = true;
        }

        if (action.btnLib) {
          button = '<button btnId="{0}" name="{1}" class="{3}">{2}</button>';
          if ($.inArray(action.btnLib.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
            if (action.btnLib.iconInfo) {
              sb.appendFormat(
                '<button type="button" btnId="{5}" name="{5}" class="well-btn {0} {1} {2} {7}" {6} code="{8}"><i class="{3}"></i>{4}</button>',
                action.btnLib.btnColor,
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.btnLib.iconInfo.fileIDs,
                text,
                btnId,
                disabled ? 'disabled' : '',
                disabled ? 'w-disable-btn' : '',
                action.code
              );
            } else {
              sb.appendFormat(
                '<button type="button" btnId="{4}" name="{4}" class="well-btn {0} {1} {2} {6}" {5} code="{7}">{3}</button>',
                action.btnLib.btnColor,
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                text,
                btnId,
                disabled ? 'disabled' : '',
                disabled ? 'w-disable-btn' : '',
                action.code
              );
            }
          } else {
            if (action.btnLib.btnInfo.icon) {
              sb.appendFormat(
                '<button type="button" btnId="{4}" name="{4}" class="well-btn {0} {1} {6}" {5} code="{7}"><i class="{2}"></i>{3}</button>',
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.btnLib.btnInfo.icon,
                action.btnLib.btnInfo.text,
                btnId,
                disabled ? 'disabled' : '',
                disabled ? 'w-disable-btn' : '',
                action.code
              );
            } else {
              sb.appendFormat(
                '<button type="button" btnId="{3}" name="{3}" class="well-btn {0} {1} {4}" {5} code="{6}">{2}</button>',
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.btnLib.btnInfo.text,
                btnId,
                disabled ? 'w-disable-btn' : '',
                disabled ? 'disabled' : '',
                action.code
              );
            }
          }
        } else {
          sb.appendFormat(
            '<button type="button" btnId="{0}" name="{0}" class="well-btn w-btn-primary {2} {4}" {3} code="{5}">{1}</button>',
            btnId,
            text,
            cssClass,
            disabled ? 'disabled' : '',
            disabled ? 'w-disable-btn' : '',
            action.code
          );
        }
      });
      $(toolbarPlaceholder, _self.$element).append(sb.toString());
      // 绑定事件
      $(toolbarPlaceholder, _self.$element).on('click', 'button', function (e) {
        var btnId = $(this).attr('btnId');
        if (!btnId) {
          return;
        }
        if (btnId.indexOf('save') != -1) {
          var result = _self.saveEventHandler('beforeSave_event');
          if (result == false) {
            return;
          }
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
      // 解析自定义的表单事件
      var formDefinition = _self.getDyform().formDefinition;
      _self.defineDyformEvents = {}; // 自定义js事件
      if (formDefinition.events && formDefinition.events != '') {
        _self.defineDyformEvents = eval('(' + formDefinition.events + ')');
      }
    },
    // 保存前后事件处理
    saveEventHandler: function (event_type, args) {
      var _self = this;
      var result;
      if (_self.defineDyformEvents != 'undefined' && _self.defineDyformEvents != undefined) {
        if (_self.defineDyformEvents[event_type]) {
          _self.getDyformData(function (dyFormData) {
            appContext.eval(
              _self.defineDyformEvents[event_type],
              this,
              $.extend(
                {
                  dyformData: dyFormData
                },
                args
              ),
              function (v) {
                result = v;
              }
            );
          });
        }
      }
      return result;
    },
    // 准备初始化表单
    prepareInitDyform: function (dyformOptions) {},
    // 获取表单选择器
    getDyformSelector: function () {
      return this.dyformSelector;
    },
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
        if (action.id && action.id.indexOf('save') > -1) {
          options.failure =
            options.failure ||
            function (jqXHR, statusText, error) {
              var dyform = _self.getDyform();
              var options = $.extend(
                {},
                {
                  dyformDataOptions: function () {
                    return dyform.dyformDataOptions();
                  },
                  callback: function () {
                    _self.performed(action);
                  }
                }
              );
              dyform.handleError(jqXHR.responseText, options);
            };
          // 自定义的保存后的脚本代码片段
          if (_self.defineDyformEvents && _self.defineDyformEvents['afterSave_event']) {
            options.afterSaveEventJs = function (result) {
              _self.saveEventHandler('afterSave_event', {
                dyformData: dyFormData,
                result: result
              });
            };
          }
        }
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
    preparePerformed: function (performedOptions) {}
  });
  return DmsDyformDocumentView;
});

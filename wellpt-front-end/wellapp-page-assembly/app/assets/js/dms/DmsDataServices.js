define(['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal
) {
  // 操作服务
  var serviceName = 'dmsService';
  var dmsUrl = ctx + '/dms/data/services';
  var errorHandler = server.ErrorHandler.getInstance();
  var Browser = commons.Browser;
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var DmsDataServices = function () {
    this.serviceUrl = dmsUrl;
    this.beforeServiceCallback = function () {
      appModal.showMask();
    };
    this.afterServiceCallback = function () {
      appModal.hideMask();
    };
  };
  $.extend(DmsDataServices.prototype, {
    // 获取服务地址
    getServiceUrl: function () {
      return this.serviceUrl;
    },
    // 设置服务地址
    setServiceUrl: function (url) {
      this.serviceUrl = url;
    },
    // 加载数据
    load: function (data, successCallback, failureCallback) {
      var _self = this;
      var success = function (result) {
        if ($.isFunction(successCallback)) {
          successCallback.call(_self, result);
        }
      };
      _self._service([], data, true, success, failureCallback);
    },

    _setTableDataStore: function (options, urlParams, url) {
      var _self = this,
        _requestId;
      if (options.ui && options.ui.$tableElement) {
        _requestId = commons.UUID.createUUID();
        urlParams._requestId = _requestId;
        var dataStoreParams = $.extend({}, options.ui.getDataProvider().getParams());
        var bootstrapTableOptions = options.ui.$tableElement.bootstrapTable('getOptions');
        if (event && $(event.currentTarget).parents('tr').length) {
          dataStoreParams.index = parseInt($(event.currentTarget).parents('tr').attr('data-index'));
        } else {
          dataStoreParams.index = options.ui.getSelectionIndexes()[0];
        }

        if (dataStoreParams.index === 0 && bootstrapTableOptions.pageNumber == 1) {
          dataStoreParams.first = true;
        } else if (
          bootstrapTableOptions.pageNumber == bootstrapTableOptions.totalPages &&
          bootstrapTableOptions.data.length - 1 == dataStoreParams.index
        ) {
          dataStoreParams.last = true;
        }
        dataStoreParams.idKey = options.urlParams.idKey;
        dataStoreParams.idValue = options.urlParams.idValue;
        dataStoreParams.url = url + '&_requestCode=' + _requestId;
        window.sessionStorage.setItem(_requestId + '_dataStoreParams', JSON.stringify(dataStoreParams));
      }
      return _requestId;
    },
    // 打开单据
    openWindow: function (options) {
      var _self = this;
      // 生成服务地址
      var urlParams = options.urlParams;
      var url = UrlUtils.appendUrlParams(_self.serviceUrl, urlParams);
      var requestCode = _self._setTableDataStore(options, urlParams, url);
      // 替换当前窗口组件
      if (appContext.isMobileApp()) {
        var widget = appContext.getWidgetById(urlParams.dms_id);
        var wType = widget.getWtype();
        var configuration = widget.getConfiguration();
        var mydoc = configuration.document || {};
        var store = configuration.store || {};
        var docTitle = mydoc.title;
        var jsModule = mydoc.jsModule;
        if ($.trim(jsModule).length <= 0) {
          jsModule = wType === 'wMobileFileManager' ? 'DmsFileManagerDyformDocumentView' : 'DmsDyformDocumentView';
        }
        var options = $.extend(true, urlParams, {
          dmsId: urlParams.dms_id,
          formUuid: store.formUuid,
          dataUuid: urlParams.idValue,
          documentViewModule: jsModule,
          documentTitle: docTitle
        });
        appContext.require([jsModule, 'mui-dmsDocumentView'], function (documentViewModule, createDmsDocumentView) {
          options.documentViewModule = documentViewModule;
          var dmsDocumentView = createDmsDocumentView(widget.element[0], options);
        });
      } else if (options.target === constant.TARGET_POSITION.TARGET_WIDGET) {
        var iframe =
          '<div class="embed-responsive embed-responsive-4by3"><iframe src="' + url + '" class="embed-responsive-item"></iframe></div>';
        appContext.getWidgetRenderPlaceholder(options.targetWidgetId).html(iframe);
      } else {
        var windowOptions = {};
        windowOptions.id = urlParams.idValue || urlParams.ac_id;
        windowOptions.url = url;
        windowOptions.useRequestCode = options.useRequestCode;
        windowOptions.useUniqueName = options.useUniqueName;
        windowOptions.ui = options.ui;
        windowOptions.size = 'large';
        windowOptions.requestCode = requestCode;
        appContext.openWindow(windowOptions);
      }

      _self.afterOpenWindow(options);
    },

    afterOpenWindow: function (options) {
      //触发数据管理查看器服务调用
      this.dmsServiceInvokeDone({ success: true }, this.eventOptions);
    },

    openDialog: function (options) {
      var _self = this,
        requestCode;
      // 生成服务地址
      var urlParams = options.urlParams;
      _self._setTableDataStore(options, urlParams);
      var iframeId = commons.UUID.createUUID();
      urlParams.iframeid = iframeId;
      var url = UrlUtils.appendUrlParams(_self.serviceUrl, urlParams);
      var iframeOptions = {
        src: url,
        id: iframeId,
        style: 'height:500px;overflow-x: hidden;border: 0px;'
      };
      var $iframe = $('<iframe>', iframeOptions);

      if (!window.dmsDialogEvent) {
        window.dmsDialogEvent = {};
      }
      window.dmsDialogEvent[iframeId] = {
        onDyformInitSuccess: function (dyform) {
          var title = '';
          if (options.eventTarget && options.eventTarget.widgetDialogTitle) {
            title = options.eventTarget.widgetDialogTitle;
          } else {
            title = $iframe.contents().find('title').text();
          }

          var $body = $iframe.contents().find('body');

          // 更新标题
          if (title) {
            $dialog.find('.modal-title').text(title);
          }

          // 隐藏单据头,并调整表单样式
          $iframe.contents().find('html').addClass('dms-hide-title');
          $iframe.contents().find('.widget-header.fixed').hide();
          $iframe.contents().find('.widget-box').addClass('widget-box-by-dialog');
          $iframe.contents().find('.widget-body').removeClass('display-none');

          if (options.urlParams.ep_displayAsLabel == true) {
            // 查看状态，不需要生成其他的按钮
            return;
          } else {
            // 编辑状态，需要生成表单的操作按钮
            var btns = $body.find('.widget-toolbar').find('button');
            if (btns.length > 0) {
              var $cancelBtn = $dialog.find('button.js-dialog-cancel');
              //清空除了取消按钮的所有其他按钮
              $cancelBtn
                .parent()
                .find('button')
                .each(function () {
                  if ($(this).attr('class').indexOf('js-dialog-cancel') == -1) {
                    $(this).remove();
                  }
                });
              // 加入新按钮
              $.each(btns, function (i, btn) {
                var cloneBtn = $(btn).clone();
                var newId = $(cloneBtn).attr('btnid') + '_clone';
                $(cloneBtn).attr('btnid', newId);
                $(cloneBtn).insertBefore($cancelBtn);
                $(cloneBtn).click(function () {
                  $(btn).trigger('click');
                  return false;
                });
              });
            }
          }
          if ($.isFunction(options.onDyformInitSuccess)) {
            options.onDyformInitSuccess.apply(this, arguments);
          }
        }
      };
      var $dialog = appModal.dialog({
        title: options.title || '&nbsp;',
        size: 'large',
        message: $iframe,
        buttons: {
          cancel: {
            label: '取消',
            className: 'btn-default js-dialog-cancel',
            callback: function () {}
          }
        },
        shown: function () {
          var width = $dialog.find('.modal-lg').width();
          $('iframe').css('width', width - 5 + 'px');
          $('.modal-body').css('padding', '0');

          // 隐藏单据头,并调整表单样式
          $iframe.contents().find('html').addClass('dms-hide-title');
          $iframe.contents().find('.widget-header.fixed').hide();
          $iframe.contents().find('.widget-box').addClass('widget-box-by-dialog');
          $iframe.contents().find('.widget-body').addClass('display-none');
        }
      });
      $dialog.on('keyup', function (e) {
        var e = event || window.event;
        var keyCode = e.keyCode || e.which;
        if (keyCode == 27) {
          // 按esc关闭dialog
          $('.js-dialog-cancel').trigger('click');
        }
      });

      _self.afterOpenWindow(options);
    },

    // 操作
    performed: function (options) {
      var _self = this;
      var urlParams = options.urlParams;
      var data = options.data;
      var successCallback = options.success;
      var failureCallback = options.failure;
      var afterSaveEventJs = options.afterSaveEventJs; //保存后自定义代码事件
      var async = options.async;
      if (async == null) {
        async = true;
      }

      // 弹窗模式就不能用options.中的success
      if (data && data.action && options.ui && options.ui.options.target == '_dialog' && data.action.stopDialogEvent !== true) {
        // action 执行完后是否需要关闭对话框，默认需要关闭
        var isNeedCloseDialog = true;
        // 进入编辑页面操作不需要关闭对话框
        if ('btn_dyform_edit' == data.action.id) {
          isNeedCloseDialog = false;
        }
        if (isNeedCloseDialog) {
          successCallback = function (result, opts) {
            // 执行JS模块
            if (StringUtils.isNotBlank(result.executeJsModule)) {
              appContext.require([result.executeJsModule], function (app) {
                options.result = data;
                appContext.executeJsModule(app, options);
              });
              return;
            }
            if (result && result.close == true) {
              // 关闭弹窗，
              $('.js-dialog-cancel', window.parent.document).trigger('click');
            } else {
              var url = window.location.href;
              if(result.data.dataUuid == "" || result.data.dataUuid){
                url = _self.changeURLArg(url,"idValue",result.data.dataUuid);
                window.location.replace(url);
              }else{
                window.location.reload();
              }
            }
            // 提示结果
            var msg = result.msg;
            if (!msg) {
              msg = '操作成功';
            }
            window.parent.appModal.success({
              message: msg,
              type: result.msgType
            });
            // 触发事件
            if (result.triggerEvents && result.triggerEvents.length > 0) {
              $.each(result.triggerEvents, function (i, eventType) {
                if (window.parent && window.parent.appContext) {
                  window.parent.appContext.getPageContainer().trigger(eventType, result.data);
                }
              });
            }
            // 刷新当前的bootstrap table
            if (parent.$('.ui-wPage').length > 0) {
              parent.$('.ui-wPage').trigger(constant.WIDGET_EVENT.BootstrapTableRefresh);
            } else if (parent.$('.ui-wBootstrapTable').length > 0) {
              parent.$('body').trigger(constant.WIDGET_EVENT.BootstrapTableRefresh);
            } else if (window.parent && window.parent.appContext && window.parent.setTimeout) {
              window.parent.setTimeout('window.parent.appContext.getWindowManager().refresh()', 2000);
            }
          };
        }
      }

      var success = function (result) {
        _self.dmsServiceInvokeDone.call(_self, result, _self.eventOptions, options);

        if ($.isFunction(successCallback)) {
          return successCallback.apply(_self, arguments);
        } else if (async == false) {
          return result;
        } else {
          return _self.onPerformedResult.call(_self, result, options);
        }
      };

      // 确认前Action
      var beforeConfirmAction = null;
      if (options.params && StringUtils.isNotBlank(options.params.beforeConfirmAction)) {
        beforeConfirmAction = options.params.beforeConfirmAction;
      }
      if (StringUtils.isBlank(beforeConfirmAction) && data && data.action && StringUtils.isNotBlank(data.action.beforeConfirmAction)) {
        beforeConfirmAction = data.action.beforeConfirmAction;
      }
      if (StringUtils.isNotBlank(beforeConfirmAction)) {
        var confirmUrlParams = $.extend(true, {}, urlParams, { ac_id: beforeConfirmAction });
        var confirmResult = _self._service(
          confirmUrlParams,
          data,
          false,
          function (success) {
            return success;
          },
          $.noop,
          null
        );
        if (confirmResult && confirmResult.success != true) {
          appModal.alert({
            message: confirmResult.msg,
            type: confirmResult.msgType
          });
          return;
        } else if (
          confirmResult &&
          confirmResult.success == true &&
          confirmResult.msgType == 'confirm' &&
          StringUtils.isNotBlank(confirmResult.msg)
        ) {
          // 确认返回的确认信息，配置优先于代码
          if (options.params == null || StringUtils.isBlank(options.params.confirmMsg)) {
            options.params = options.params || {};
            options.params.confirmMsg = confirmResult.msg;
          }
        } else if (confirmResult == null) {
          appModal.error('确认前操作[' + beforeConfirmAction + ']执行出错！');
          return;
        }
      }
      // 操作确认判断处理
      if (data && data.action && StringUtils.isNotBlank(data.action.confirmMsg)) {
        var params = options.params || {};
        var confirmMsg = params.confirmMsg ? params.confirmMsg : data.action.confirmMsg;
        appModal.confirm(confirmMsg, function (result) {
          if (result === true || result === 1) {
            return _self._service(urlParams, data, async, success, failureCallback, afterSaveEventJs);
          }
        });
      } else {
        return _self._service(urlParams, data, async, success, failureCallback, afterSaveEventJs);
      }
    },

    changeURLArg: function(url, arg, arg_val) {

      var pattern = arg + '=([^&]*)';
      var replaceText = arg + '=' + arg_val;
      if (url.match(pattern)) {
        var tmp = '/(' + arg + '=)([^&]*)/gi';
        tmp = url.replace(eval(tmp), replaceText);
        return tmp;
      }
      if (url.match('[\?]')) {
        return url + '&' + replaceText;
      } else {
        return url + '?' + replaceText;
      }
    },

    dmsServiceInvokeDone: function (result, dmsOptions, options) {
      //服务调用成功
      try {
        //1.通知数据管理查看器操作结果
        if (dmsOptions && dmsOptions.ui && dmsOptions.ui.element && dmsOptions.ui.element.data('dms_id')) {
          appContext.widgetMap[dmsOptions.ui.element.data('dms_id')].trigger('dataManagementViewerAction', {
            invokeResult: result,
            options: dmsOptions
          });
        }

        //2.表格操作结果通知
        if (options && options.ui && options.ui.getSelections) {
          options.ui.trigger('dmsEventHanlderActionDone', {
            invokeResult: result,
            options: options
          });
        }

        //3.表单操作结果通知
        if (options && options.ui && options.ui.dyform && options.ui.dmsId) {
          appContext
            .getWindowManager()
            .triggerParentWidgetEvent(options.ui.dmsId, 'dataManagementViewerAction', { invokeResult: result, options: options });
        }
      } catch (e) {
        console.log(e);
      }
    },

    // 操作结果处理
    onPerformedResult: function (result, options) {
      var self = this;
      var data = result;
      // 关闭窗口
      var close = data.close;
      // 刷新窗口
      var refresh = data.refresh;
      // 刷新父窗口
      var refreshParent = data.refreshParent;
      // 返回的数据
      var resultData = data.data;
      // 要附加的URL参数，存在替换，不存在附加
      var appendUrlParams = data.appendUrlParams;
      // 操作结果执行的JS模块，模块不为空时不处理其他属性行为
      var executeJsModule = data.executeJsModule;
      // 显示提示信息
      var showMsg = data.showMsg;
      // 操作结果提示
      var msg = data.msg || '操作成功';
      // 操作结果提示类型
      var msgType = data.msgType;
      // 触发的事件
      var triggerEvents = data.triggerEvents;

      // 执行JS模块
      if (StringUtils.isNotBlank(executeJsModule)) {
        appContext.require([executeJsModule], function (app) {
          options.result = data;
          appContext.executeJsModule(app, options);
        });
        return;
      }

      var reloadWindow = false;
      var href = window.location.href;
      if (data.close === true && appContext.isMobileApp() === false /*手机端不支持关闭*/) {
        if (refreshParent === true) {
          //刷新父窗口
          appContext.getWindowManager().refreshParent();
          if (StringUtils.isNotBlank(msg)) {
            appModal[msgType]({
              message: msg,
              type: msgType,
              resultCode: 0
            });
          }
          appContext.getWindowManager().close(); //关闭当前窗口
        } else {
          // 不刷新父窗口
          if (window.opener && window.opener.appModal) {
            window.opener.appModal[msgType]({
              message: msg,
              type: msgType
            });
            //关闭当前窗口
            appContext.getWindowManager().close();
          } else {
            appModal[msgType]({
              message: msg,
              type: msgType,
              callback: function () {
                if (options.ui.options.target === '_dialog') {
                  parent.$('.js-dialog-cancel').trigger('click');
                } else {
                  appContext.getWindowManager().close();
                }
              }
            });
          }
        }
      } else if (data.appendUrlParams) {
        for (var p in data.appendUrlParams) {
          var v = data.appendUrlParams[p];
          var value = Browser.getQueryString(p);
          if (v !== value) {
            if (StringUtils.contains(href, p + '=' + value)) {
              href = href.replace(p + '=' + value, p + '=' + v);
            } else {
              var prefix = href.indexOf('?') < 0 ? '?' : '&';
              href += prefix + p + '=' + v;
            }
            reloadWindow = true;
          }
        }
      }

      // 触发事件
      if (close) {
        if (triggerEvents && triggerEvents.length > 0 && window.opener && window.opener.appContext) {
          $.each(triggerEvents, function (i, eventType) {
            window.opener.appContext.getPageContainer().trigger(eventType, resultData);
          });
        }
      } else {
        if (options.ui && triggerEvents && triggerEvents.length > 0) {
          $.each(triggerEvents, function (i, eventType) {
            options.ui.trigger(eventType, resultData);
          });
        }
      }

      // 异步刷新-手机端先刷新当前页面---start
      if (appContext.isMobileApp()) {
        data.msg && $.toast(msg);
        if (options.ui && data.refresh === true && data.appendUrlParams) {
          var extraParams = {},
            urlParams = { extraParams: extraParams };
          for (var p in data.appendUrlParams) {
            var v = data.appendUrlParams[p];
            if (p.indexOf('ep_') === 0) {
              extraParams[p] = v;
            } else if (p === 'ac_id') {
              urlParams['acId'] = v;
            } else if (p === 'idValue') {
              urlParams['dataUuid'] = v;
            } else if (p === 'dms_id') {
              urlParams['dmsId'] = v;
            }
            urlParams[p] = v;
          }
          // 刷新当前页
          var element = options.ui.element || options.ui.$element;
          if (element && element.length > 0 && $.data[element[0].id] && $.data[element[0].id].refresh) {
            $.data[element[0].id].refresh(urlParams);
          }
          // 刷新列表
          if (options.ui.getWtype && options.ui.getWtype() === 'wMobileListView') {
            options.ui.refresh(urlParams);
          }
        } else {
          setTimeout(function () {
            appContext.getWindowManager().refresh(href);
          }, 1500);
        }
        return;
      }
      // 异步刷新-手机端先刷新当前页面---end
      // 异步刷新
      if (options.ui && data.refresh === true && reloadWindow !== true) {
        // 界面组件UI异步刷新
        if (options.ui.getWtype) {
          if (refreshParent === true) {
            appContext.getWindowManager().refreshParent();
          }
          options.ui.refresh(true);
          if (showMsg) {
            appModal[msgType]({
              message: msg,
              type: msgType
            });
          }
        } else {
          // 单据UI同步刷新
          var uiCallback = function () {
            if (refreshParent === true) {
              appContext.getWindowManager().refreshParent();
            }
            options.ui.refresh(true);
          };
          if (showMsg) {
            appModal[msgType]({
              message: msg,
              type: msgType,
              callback: uiCallback
            });
          } else {
            uiCallback();
          }
        }
      } else {
        var refreshCallback = function () {
          if (refreshParent === true) {
            appContext.getWindowManager().refreshParent();
          }
          if (reloadWindow === true) {
            appContext.getWindowManager().refresh(href);
            return;
          }
          if (data.refresh === true) {
            if (options.ui) {
              options.ui.refresh(true);
            } else {
              appContext.getWindowManager().refresh();
            }
          }
        };
        if (showMsg) {
          appModal[msgType]({
            message: msg,
            type: msgType,
            callback: refreshCallback
          });
        } else {
          refreshCallback();
        }
      }
    },
    // 生成服务地址
    _generateServiceUrl: function (urlParams) {
      // URL参数处理
      var array = urlParams;
      if ($.isArray(urlParams) === false) {
        array = [];
        for (var p in urlParams) {
          array.push({
            key: p,
            value: urlParams[p]
          });
        }
      }

      var sb = new StringBuilder();
      sb.append(this.serviceUrl);
      if (array.length > 0) {
        sb.append('?');
      }
      for (var i = 0; i < array.length; i++) {
        var param = array[i];
        sb.append(param.key + '=' + escape(param.value));
        if (i < array.length - 1) {
          sb.append('&');
        }
      }
      return sb.toString();
    },
    // 服务处理
    _service: function (urlParams, data, async, successCallback, failureCallback, afterSaveEventJs) {
      var _self = this;
      if ($.isFunction(_self.beforeServiceCallback)) {
        _self.beforeServiceCallback.apply(_self, arguments);
      }

      // 生成服务地址
      var url = UrlUtils.appendUrlParams(_self.serviceUrl, urlParams);
      var result = null;
      $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        contentType: 'application/json',
        dataType: 'json',
        async: async,
        success: function (success, statusText, jqXHR) {
          if ($.isFunction(_self.afterServiceCallback)) {
            _self.afterServiceCallback.apply(_self, arguments);
          }

          if ($.isFunction(afterSaveEventJs)) {
            afterSaveEventJs.call(_self, result);
          }

          if ($.isFunction(successCallback)) {
            result = successCallback.apply(_self, arguments);
          }
        },
        error: function (jqXHR, statusText, error) {
          try {
            if ($.isFunction(_self.afterServiceCallback)) {
              _self.afterServiceCallback.apply(_self, arguments);
            }

            if ($.isFunction(afterSaveEventJs)) {
              afterSaveEventJs.call(_self, { success: false, msg: error });
            }

            if ($.isFunction(failureCallback)) {
              failureCallback.apply(_self, arguments);
            } else {
              // 默认的异常处理器处理
              errorHandler.handle.apply(errorHandler, arguments);
            }
          } catch (e) {
            console.error(e);
          } finally {
          }
        }
      });
      // 同步返回
      if (async == false) {
        return result;
      }
    }
  });

  return DmsDataServices;
});

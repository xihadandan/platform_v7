(function (factory) {
  "use strict";
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui', 'constant', 'commons', 'server', 'appContext', "formBuilder"], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
}(function ($, constant, commons, server, appContext, formBuilder) {
  "use strict";
  var KEY_DMS_ID = "dms_id";
  var StringBuilder = commons.StringBuilder;
  var StringUtils = commons.StringUtils;
  $.widget("mui.wMobileFileManager", $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
    },

    allowActionMap: {

    },

    _createView: function () {
      var self = this;
      var requires = [];
      var configuration = self.options.widgetDefinition.configuration || {};
      self.options.widgetDefinition.configuration = configuration;
      if (configuration.document && configuration.document.jsModule) {
        configuration.jsModule = configuration.document.jsModule;
        requires.push(configuration.jsModule);
      } else {
        requires.push("DmsFileManagerDyformDocumentView");
      }

      appContext.require(requires, function (Development) {
        if (typeof Development === 'function') {
          self.development = new Development(self);;
        }
        self._executeJsModule('beforeRender', [self.options, configuration]);
        // "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
        self._renderView();
        self._executeJsModule('afterRender', [self.options, configuration]);
      });
    },
    _init: function () {
      var _self = this;

      var actionMap = {};

      var configuration = _self.getConfiguration();
      if (configuration && configuration.view && configuration.view.fileActions) {
        $.each(configuration.view.fileActions, function () {
          if (this.checked) {
            actionMap[this.id] = this;
          }
        });
      }

      server.JDS.call({
        service: "personalDocumentService.getMyFolder",
        async: false,
        success: function (result) {
          _self.folderUuid = result.data.uuid;
        }
      });


      server.JDS.call({
        service: "dmsFileActionService.getFolderActions",
        data: [_self.folderUuid],
        async: false,
        version: "", //绕过门面服务权限验证
        success: function (result) {
          var data = result.data;
          for (var actionId in actionMap) {
            for (var i = 0; i < data.length; i++) {
              if (typeof actionId == "string" && data[i].id.indexOf(actionId) != -1) {
                _self.allowActionMap[actionId] = actionMap[actionId];
                break;
              }

              if (actionId == 'viewAttributes' && data[i].id.indexOf("view") != -1) {
                _self.allowActionMap[actionId] = actionMap[actionId];
                break;
              }

            }
          }
        }
      });
    },

    _checkAndAddFileManagerListViewWidgetDevelopment: function (listViewWidgetDefinition) {
      var _self = this;
      var listViewConfiguration = listViewWidgetDefinition.configuration || {};
      listViewConfiguration.dmsWidgetDefinition = _self.options.widgetDefinition;
      var listViewJsModule = listViewConfiguration.jsModule;
      var dyModule = "mui-MobileDefaultFileList";
      if (StringUtils.isBlank(listViewJsModule)) {
        listViewConfiguration.jsModule = dyModule;
      }
    },

    _renderView: function () {
      var self = this;

      self._createContentView();
      // 生成页面组件
      self._createWidgets();
      // 绑定事件
      var viewConfiguration = self.options.widgetDefinition.configuration.view || {};
      var unitTreeWidgetId = viewConfiguration.leftSidebarTreeId;
      var listViewWidgetId = viewConfiguration.listViewId;
      var relationshipQuery = viewConfiguration.relationshipQuery;

      var pageContainer = self.pageContainer;
      // 视图创建完成处理
      pageContainer.onWidgetCreated(listViewWidgetId, function (e, listViewWidget) {
        listViewWidget = listViewWidget || e.detail[1];
        // window.ListViewInstance = listViewWidget;
        $(listViewWidget.element).data(KEY_DMS_ID, self.getId());
      });

    },
    _createContentView: function () {

      var _self = this;

      var configuration = _self.getConfiguration();
      var viewConfiguration = configuration.view || {};
      var listViewWidgetId = viewConfiguration.listViewId;

      var widgetDefinition = _self.options.widgetDefinition;
      var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
      if (listViewDefinition) {
        _self.listViewWidgetDefinition = listViewDefinition;
        _self._checkAndAddFileManagerListViewWidgetDevelopment(_self.listViewWidgetDefinition);
        //参照：_self.dmsFileServices.getFolderActions(options);
        var buttons = [];
        for (var action in _self.allowActionMap) {
          if (_self.allowActionMap[action].hidden != '1') {
            var button = {
              checked: _self.allowActionMap[action].checked,
              code: _self.allowActionMap[action].id,
              cssClass: _self.allowActionMap[action].cssClass,
              icon: _self.allowActionMap[action].icon,
              confirmMsg: _self.allowActionMap[action].confirmMsg,
              group: "",
              position: ["2"],
              text: _self.allowActionMap[action].name,
              uuid: _self.allowActionMap[action].id + "_mobile_file_button",
            };
            buttons.push(button);
          }
        }
        var listButtons = _self.listViewWidgetDefinition.configuration.buttons;
        if (listButtons instanceof Array) {
          _self.listViewWidgetDefinition.configuration.buttons = listButtons.concat(buttons);
        } else {
          _self.listViewWidgetDefinition.configuration.buttons = buttons;
        }
      }
    },
    _createWidgets: function () {
      var self = this;
      var widgetDefinition = self.options.widgetDefinition;
      var viewConfiguration = self.getConfiguration().view || {};
      var listViewWidgetId = viewConfiguration.listViewId;
      var listViewDefinition = self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
      if (listViewDefinition) {
        listViewDefinition.configuration.dmsWidgetDefinition = self.options.widgetDefinition;
      }
      widgetDefinition.instance = self;
      $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
        appContext.createWidget(childWidgetDefinition, widgetDefinition);
      });
    },

    setListFileMode: function (listFileMode) {
      this.listFileMode = listFileMode;
    },

    getListFileMode: function () {
      return this.listFileMode;
    },

    getListViewDefinition: function () {
      var self = this;
      var widgetDefinition = self.options.widgetDefinition;
      var viewConfiguration = self.getConfiguration().view || {};
      var listViewWidgetId = viewConfiguration.listViewId;
      return self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
    },
    _getListViewDefinition: function (listViewWidgetId, items) {
      var _self = this;
      if (items == null || items.length === 0) {
        return null;
      }
      for (var i = 0; i < items.length; i++) {
        var childWidgetDefinition = items[i];
        if (listViewWidgetId === childWidgetDefinition.id) {
          return items[i];
        }
        var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, childWidgetDefinition.items)
        if (listViewDefinition) {
          return listViewDefinition;
        }
      }
      return null;
    }
  });
}));

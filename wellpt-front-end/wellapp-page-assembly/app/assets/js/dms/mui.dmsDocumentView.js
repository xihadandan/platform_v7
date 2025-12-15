(function (factory) {
  /* global define */
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'appContext', "formBuilder"], factory);
  } else if (typeof module === 'object' && module.exports) {
    // Node/CommonJS
    module.exports = factory(require('jquery'));
  } else {
    // Browser globals
    factory(window.mui);
  }
}(function ($, commons, appContext, formBuilder) {
  'use strict';
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var DmsDocumentView = function (element, options) {
    var _self = this;
    options = $.extend(true, {}, $.fn.dmsDocumentView.defaults, options);
    _self.options = options;
    _self.$element = $(element);
    _self._createDocumentView();
  }

  DmsDocumentView.prototype.refresh = function (options) {
    var _self = this;
    options = $.extend(true, {}, _self.options, options);
    _self.options = options;
    _self._createDocumentView();
  }
  // 创建流程二开
  DmsDocumentView.prototype._createDocumentView = function () {
    var _self = this;

    _self._create();
    _self._init();
    _self._loadData();
  }
  // 创建并绑定事件
  DmsDocumentView.prototype._create = function () {
    var _self = this;
    _self._createView();
    var documentViewModule = _self.options.documentViewModule;
    _self.documentView = new documentViewModule(_self.$element);
    _self.documentView.$element = _self.$element;
  }
  // 创建表单元素
  DmsDocumentView.prototype._createView = function () {
    var _self = this;
    var placeholder = $(".mui-content", _self.$element[0])[0];
    var wrapper = placeholder.querySelector(_self.options.dyformSelector);
    if (wrapper != null) {
      wrapper.parentNode.removeChild(wrapper);
    }
    var dyform = document.createElement("div");
    dyform.classList.add("mui-dyform");
    placeholder.appendChild(dyform);
  }
  // 初始化
  DmsDocumentView.prototype._init = function () {
    this.documentView.init(this.options);
  }
  // 加载数据
  DmsDocumentView.prototype._loadData = function () {
    this.documentView.load();
  }

  // jQuery插件
  $.fn.dmsDocumentView = function (options) {
  };

  // jQuery插件默认参数
  $.fn.dmsDocumentView.defaults = {
    documentViewModule: null,// 单据二开模块
    dyformSelector: ".mui-dyform",// 表单选择器
    toolbarPlaceholder: "dms-widget-toolbar"// 操作按钮占位符
  };

  var createDmsDocumentView = function (element, options) {
    // 数据管理表单单据开发
    var documentViewModule = options.documentViewModule || $.noop;
    var DmsDyformDocumentView = function () {
      documentViewModule.apply(this, arguments);
    };
    commons.inherit(DmsDyformDocumentView, documentViewModule, {
      // 创建操作按钮
      createActionButtons: function () {
        var _self = this;
        // 设置标题
        var docData = _self.getDocumentData();
        if (docData && docData.title) {
          $.ui.setTitle(docData.title);
        }
        // 创建操作按钮
        var actions = _self.getActions();
        _self.actionMap = {};
        // 操作按钮占位符
        var tabBarSelector = "dms_tab_bar_mobile_view";
        var actionSheetSelector = "dms_action_sheet_mobile_view";
        var toolbarPlaceholder = _self.options.toolbarPlaceholder || "";
        var actionData = [];
        var actionSheetData = [];
        var dmsMoreId = "dms-more";
        $.each(actions, function (i, action) {
          var self = this;
          _self.actionMap[action.id] = action;
          // 按钮名称
          var name = action.name;
          // 按钮编号
          var code = action.id;
          // 按钮ID
          var btnId = action.id;
          // 样式
          var cssClass = action.cssClass;
          // 操作名称
          if (actionData.length === 3) {
            actionData.push({
              text: "更多",
              id: dmsMoreId,
              name: dmsMoreId,
              cssClass: cssClass,
              icon: "mui-icon mui-icon-more",
            });
          }
          if (actionData.length < 4) {
            actionData.push({
              id: btnId,
              name: code,
              text: name,
              cssClass: cssClass,
            });
          } else {
            actionSheetData.push({
              id: btnId,
              name: code,
              text: name,
              cssClass: cssClass,
            });
          }
        });
        // 操作按钮
        var placeholder = $(".mui-content", _self.$element[0])[0];
        var wrapper = placeholder.querySelector("." + tabBarSelector);
        if (wrapper != null) {
          wrapper.parentNode.removeChild(wrapper);
        }
        wrapper = document.createElement("div");
        wrapper.id = toolbarPlaceholder;
        wrapper.classList.add(tabBarSelector);
        wrapper.classList.add("dms-tab-bar");
        placeholder.appendChild(wrapper);
        // 更多操作按钮
        var acWrapper = placeholder.querySelector("." + actionSheetSelector);
        if (acWrapper != null) {
          acWrapper.parentNode.removeChild(acWrapper);
        }
        acWrapper = document.createElement("div");
        acWrapper.classList.add(actionSheetSelector);
        placeholder.appendChild(acWrapper);
        if (actionData.length > 0) {
          formBuilder.buildTabBar({
            data: actionData,
            container: wrapper
          });
          formBuilder.buildActionSheet({
            sheetId: dmsMoreId,
            data: actionSheetData,
            container: acWrapper
          });
          // 添加底部偏移
          _self.dyform.element[0].classList.add("wui-scroll-bottom");
        }
        // 绑定TabBar事件
        $("." + tabBarSelector, placeholder).on("tap", ".mui-tab-item", function (event) {
          var self = this;
          event.origTarget = self;
          var name = self.getAttribute("name");
          if (name === dmsMoreId) {
            _self.toggleMoreActionSheel(name);
          } else {
            // 绑定当前事件
            _self.buttonAction(name, event);
          }
          return true;
        });
        // 绑定ActionSheet事件
        $("." + actionSheetSelector, placeholder).on("tap", ".mui-table-view-cell", function (event) {
          var self = this;
          event.origTarget = self;
          var name = self.getAttribute("name");
          if (StringUtils.isNotBlank(name)) {
            // 隐藏ActionSheet操作按钮
            _self.toggleMoreActionSheel(dmsMoreId);
            // 绑定当前事件
            _self.buttonAction(name, event);
          }
          return true;
        });
      },
      toggleMoreActionSheel: function (name) {
        $("#action_sheet_" + name).popover("toggle");
      },
      buttonAction: function (name, event) {
        var _self = this;
        var action = _self.actionMap[name];
        if (typeof action === "undefined" || action == null) {
          console.error("button of id[" + name + "] is not found");
          return;
        }
        _self.currentEvent = event;
        _self.performed(action);
        _self.currentEvent = null;
      },
    });
    options.documentViewModule = DmsDyformDocumentView;

    // 创建面板
    var id = "DmsDocumentPanel" + (++$.uuid);
    var panel = document.createElement("div");
    panel.id = id;
    var pageContainer = appContext.getPageContainer();
    var renderPlaceholder = pageContainer.getRenderPlaceholder();
    renderPlaceholder[0].appendChild(panel);
    formBuilder.buildPanel({
      title: options.documentTitle,
      container: "#" + id
    });
    $.ui.loadContent("#" + id);
    var dmsDocumentView = new DmsDocumentView(panel, options);
    $.data[panel.id] = dmsDocumentView;
    panel.addEventListener("panel.back", function (event) {
      delete $.data[panel.id];
    });
    return dmsDocumentView;
  };
  return createDmsDocumentView;
}));

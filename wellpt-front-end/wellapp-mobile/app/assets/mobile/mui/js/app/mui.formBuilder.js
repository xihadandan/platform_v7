define(['mui', 'server', 'constant', 'commons', 'appContext', 'moment', 'mui-dtpicker'], function (
  $,
  server,
  constant,
  commons,
  appContext,
  moment,
  dt
) {
  var JDS = server.JDS;
  var StringBuilder = commons.StringBuilder;
  var StringUtils = commons.StringUtils;
  var muiIcons = new StringBuilder();
  muiIcons.append('|contact|person|personadd|phone|email|chatbubble|chatboxes|weibo|weixin|pengyouquan|chat|');
  muiIcons.append('|qq|videocam|camera|image|mic|micoff|location|map|compose|trash|upload|download|close|');
  muiIcons.append('|closeempty|redo|undo|refresh|refreshempty|reload|loop|spinner|spinner-cycle|star|starhalf|');
  muiIcons.append('|plus|plusempty|minus|checkmarkempty|search|home|navigate|gear|settings|list|bars|paperplane|');
  muiIcons.append('|info|help|more|locked|flag|paperclip|back|forward|arrowup|arrowdown|arrowleft|arrowright|');
  muiIcons.append('|arrowthinup|arrowthindown|arrowthinleft|arrowthinright|pulldown|');
  muiIcons = muiIcons.toString();

  var colPrefix = 'mui-col-xs-';
  var defaultOptions = {
    container: '',
    labelColSpan: '4',
    controlColSpan: '8',
    labelClass: '',
    controlClass: '',
    divClass: 'form-group',
    label: '',
    name: '',
    value: '',
    inputClass: '',
    events: null
  };
  var defaultDatetimepickerOptions = {
    format: 'YYYY-MM-DD HH:mm:ss',
    locale: 'zh-cn'
  };
  var initOptions = function (specialDefault, options) {
    var specialDefault = $.extend(specialDefault, defaultOptions);
    specialDefault = $.extend(specialDefault, options);
    return specialDefault;
  };

  var applyDomEvents = function ($dom, events) {
    $.each($dom || [], function (idx, dom) {
      for (var type in events) {
        var callback = events[type];
        if ($.isFunction(callback)) {
          dom.addEventListener(type, callback);
        }
      }
    });
  };
  var formBuilder = {};

  function isSupportIcon(name) {
    return muiIcons.indexOf('|' + name + '|') != -1;
  }

  // 创建面板
  formBuilder.buildPanel = function (options) {
    var options = initOptions(
      {
        buttonIcon: '',
        buttonText: '',
        title: '',
        content: '',
        contentClass: 'mui-content',
        actionBack: true
      },
      options
    );
    var StringBuilder = commons.StringBuilder;
    var sb = new StringBuilder();
    sb.appendFormat('<header class="mui-bar mui-bar-nav" style="position: fixed!important">');
    if (options.actionBack == true) {
      sb.appendFormat('<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>');
    } else {
      sb.appendFormat('<a class="mui-icon mui-pull-left"></a>');
    }
    var menu;
    if (options.buttonText) {
      menu = '<a class="mui-icon mui-pull-right btn-text mui-header-menu">' + options.buttonText + '</a>';
    } else if (options.buttonIcon) {
      menu = '<a class="mui-icon ' + options.buttonIcon + ' mui-pull-right mui-header-menu mui-hidden"></a>';
    } else {
      menu = '<a class="mui-icon mui-action-menu mui-icon-bars mui-pull-right mui-header-menu mui-hidden"></a>';
    }

    sb.appendFormat(menu);
    sb.appendFormat('<h1 class="mui-title" toast-text>{0}</h1>', options.title);
    sb.appendFormat('</header>');
    sb.appendFormat('<div class="{0}">', options.contentClass);
    // sb.appendFormat(options.content);
    sb.appendFormat('</div>');
    var panel = $(options.container)[0];
    panel.classList.add('panel');
    panel.innerHTML = sb.toString();
    var contentElement = $('.' + options.contentClass, panel)[0];
    if (contentElement == null && options.contentClass) {
      contentElement = $('.' + options.contentClass.split(' ')[0], panel)[0];
    }
    contentElement.innerHTML = options.content;
    // 如果内容比较高滚动条
    if (options.autoScroll === true && contentElement.firstElementChild) {
      var contentWidth = contentElement.clientWidth;
      var contentHeight = contentElement.clientHeight;
      var clientWidth = document.documentElement['clientWidth'];
      var clientHeight = document.documentElement['clientHeight'];
      if (contentHeight > clientHeight || contentWidth > clientWidth) {
        contentElement.classList.add('mui-scroll-wrapper');
        contentElement.firstElementChild.classList.add('mui-scroll');
        $(contentElement).scroll(
          $.extend(
            {
              scrollY: contentHeight > clientHeight,
              scrollX: contentWidth > clientWidth
            },
            options
          )
        );
      }
    }
    return panel;
  };

  // 展示面板
  formBuilder.showPanel = function (options) {
    var container = $(options.container);
    if (!container || container.length <= 0) {
      var wrapper = document.createElement('div');
      if (options.container.indexOf('#') === 0) {
        wrapper.id = options.container.substr(1);
      } else if (options.container.indexOf('.') === 0) {
        wrapper.classList.add(options.container.substr(1));
      }
      var pageContainer = appContext.getPageContainer();
      if (pageContainer && pageContainer.getRenderPlaceholder) {
        pageContainer.getRenderPlaceholder()[0].appendChild(wrapper);
      } else {
        document.body.appendChild(wrapper);
      }
    }
    // var content = options.content;
    // options.content = "";
    formBuilder.buildPanel(options);
    var panel = $(options.container)[0];
    var contentClas = options.contentClass || 'mui-content';
    var contentElement = $('.' + contentClas, panel)[0];
    if (contentElement == null && options.contentClass) {
      contentElement = $('.' + options.contentClass.split(' ')[0], panel)[0];
    }
    // 左菜单
    if ($.isPlainObject(options.actionBack)) {
      var actionBack = options.actionBack;
      var defaultBackLabel = '返回';
      var backLabel = actionBack.label || defaultBackLabel;
      var oldBtn = $('header>.mui-pull-left', panel)[0];
      var newBtn = null;
      if (actionBack.showNavTitle === true) {
        newBtn = $.dom(
          '<button class="mui-btn mui-btn-blue mui-btn-link mui-btn-nav mui-pull-left"><span class="mui-icon mui-icon-left-nav"></span><span class="mui-left-nav-text">' +
            $.ui.getTitle() +
            '</span></button>'
        )[0];
      } else if (actionBack.showLeftIcon === true) {
        newBtn = $.dom(
          '<button class="mui-btn mui-btn-blue mui-btn-link mui-btn-nav mui-pull-left"><span class="mui-icon mui-icon-left-nav"></span><span class="mui-left-nav-text">' +
            backLabel +
            '</span></button>'
        )[0];
      } else {
        newBtn = $.dom(
          '<button class="mui-btn mui-btn-blue mui-btn-link mui-pull-left"><span class="mui-left-nav-text">' +
            backLabel +
            '</span></button>'
        )[0];
      }
      newBtn.addEventListener('tap', function (event) {
        if (this.getAttribute('disabled') === 'disabled') {
          return;
        }
        this.setAttribute('disabled', 'disabled');
        try {
          if ($.isFunction(actionBack.callback) && actionBack.callback.call(this, event, panel) === false) {
            return;
          }
          return $.ui.goBack();
        } finally {
          this.removeAttribute('disabled');
        }
      });
      oldBtn.parentNode.replaceChild(newBtn, oldBtn);
    }
    // 右菜单
    if ($.isPlainObject(options.optButton)) {
      var optButton = options.optButton;
      var actionSheet = optButton.actionSheet;
      var optLabel = optButton.label || '完成';
      var oldBtn = $('header>.mui-pull-right', panel)[0];
      var newBtn = $.dom('<button id="type" class="mui-btn mui-btn-link mui-pull-right">' + optLabel + '</button>')[0];
      oldBtn.parentNode.replaceChild(newBtn, oldBtn);

      if ($.isArray(actionSheet) && actionSheet.length > 0) {
        // 更多操作按钮
        var actionSheetSelector = 'wui-opt-action-sheet';
        var actionSheetWrapper = contentElement.querySelector('.' + actionSheetSelector);
        if (actionSheetWrapper != null) {
          actionSheetWrapper.parentNode.removeChild(actionSheetWrapper);
        }
        actionSheetWrapper = document.createElement('div');
        actionSheetWrapper.classList.add(actionSheetSelector);
        contentElement.appendChild(actionSheetWrapper);
        formBuilder.buildActionSheet({
          sheetId: actionSheetSelector,
          data: actionSheet,
          container: actionSheetWrapper
        });
        newBtn.addEventListener('tap', function (event) {
          $('#action_sheet_' + actionSheetSelector).popover('toggle');
        });
        // 绑定ActionSheet事件
        $(actionSheetWrapper).on('tap', '.mui-table-view-cell', function (event) {
          var self = this;
          var name = self.getAttribute('name');
          var idx = self.getAttribute('data-index');
          if (StringUtils.isNotBlank(idx)) {
            // 隐藏ActionSheet操作按钮
            $('#action_sheet_' + actionSheetSelector).popover('toggle');
            if ($.isFunction(actionSheet[idx].callback) && actionSheet[idx].callback.call(this, event, panel) === false) {
            }
          } else {
            // console.log("actionSheet Error");
          }
          return true;
        });
      } else {
        newBtn.addEventListener('tap', function (event) {
          if (this.getAttribute('disabled') === 'disabled') {
            return;
          }
          this.setAttribute('disabled', 'disabled');
          try {
            if ($.isFunction(optButton.callback) && optButton.callback.call(this, event, panel) === false) {
              return;
            }
            return $.ui.goBack();
          } finally {
            this.removeAttribute('disabled');
          }
        });
      }
    }

    // contentElement.innerHTML = content || "";
    // 底部工具栏
    if ($.isArray(options.actions) && options.actions.length > 0) {
      options.moreNum = options.moreNum || 3;
      var actionData = [];
      var actionSheetData = [];
      var actionMoreId = 'action-more-' + ++$.uuid;
      options._callbacks = {};
      $.each(options.actions, function (idx, action) {
        var self = this;
        // 按钮名称
        var name = action.name;
        // 按钮编号
        var code = action.id;
        // 按钮ID
        var btnId = action.id;
        // 样式
        var cssClass = action.cssClass;
        // 回调
        options._callbacks[code] = action.callback;
        // 操作名称
        if (actionData.length === options.moreNum) {
          actionData.push({
            text: '更多',
            id: actionMoreId,
            name: actionMoreId,
            cssClass: cssClass,
            icon: 'mui-icon mui-icon-more'
          });
        }
        if (actionData.length <= options.moreNum) {
          actionData.push({
            id: btnId,
            name: code,
            text: name,
            cssClass: cssClass
          });
        } else {
          actionSheetData.push({
            id: btnId,
            name: code,
            text: name,
            cssClass: cssClass
          });
        }
      });
      // 操作按钮
      var placeholder = contentElement;
      var tabBarSelector = 'wui-tab-bar';
      var actionSheetSelector = 'wui-action-sheet';
      var tabBarWrapper = placeholder.querySelector('.' + tabBarSelector);
      if (tabBarWrapper != null) {
        tabBarWrapper.parentNode.removeChild(tabBarWrapper);
      }
      tabBarWrapper = document.createElement('div');
      tabBarWrapper.classList.add(tabBarSelector);
      placeholder.appendChild(tabBarWrapper);
      // 更多操作按钮
      var actionSheetWrapper = placeholder.querySelector('.' + actionSheetSelector);
      if (actionSheetWrapper != null) {
        actionSheetWrapper.parentNode.removeChild(actionSheetWrapper);
      }
      actionSheetWrapper = document.createElement('div');
      actionSheetWrapper.classList.add(actionSheetSelector);
      placeholder.appendChild(actionSheetWrapper);
      if (actionData.length > 0) {
        formBuilder.buildTabBar({
          data: actionData,
          container: tabBarWrapper
        });
        formBuilder.buildActionSheet({
          sheetId: actionMoreId,
          data: actionSheetData,
          container: actionSheetWrapper
        });
        // 添加底部偏移
        placeholder.classList.add('wui-content-bottom');
      }
      // 绑定TabBar事件
      $(tabBarWrapper).on('tap', '.mui-tab-item', function (event) {
        var self = this;
        var name = self.getAttribute('name');
        if (name === actionMoreId) {
          $('#action_sheet_' + actionMoreId).popover('toggle');
        } else if ($.isFunction(options._callbacks[name]) && options._callbacks[name].call(this, event, panel) === false) {
        }
      });
      // 绑定ActionSheet事件
      $(actionSheetWrapper).on('tap', '.mui-table-view-cell', function (event) {
        var self = this;
        var name = self.getAttribute('name');
        if (StringUtils.isNotBlank(name)) {
          // 隐藏ActionSheet操作按钮
          $('#action_sheet_' + actionMoreId).popover('toggle');
          if ($.isFunction(options._callbacks[name]) && options._callbacks[name].call(this, event, panel) === false) {
          }
        }
        return true;
      });
    }
    if ($.isFunction(options.show) && options.show.call(panel, options) === false) {
      return panel;
    }
    // 展示面板
    $.ui.loadContent(panel);
    if ($.isFunction(options.shown) && options.shown.call(panel, options) === false) {
    }
    return panel;
  };

  formBuilder.buildTabBarItems = function (items) {
    var StringBuilder = commons.StringBuilder;
    var sb = new StringBuilder();
    $.each(items, function (i) {
      var id = this.id;
      var name = this.name;
      var code = this.code;
      var text = this.text;
      var cssClass = this.cssClass || '';
      sb.appendFormat('<a class="mui-tab-item mui-active {2}" id="{0}" name="{1}" href="#{1}">', id, name, cssClass);
      if (isSupportIcon(id)) {
        sb.appendFormat('<span class="mui-icon mui-icon-{0}"></span>', id);
      } else {
        switch (text) {
          case '提交':
            sb.appendFormat('<span class="mui-icon iconfont icon-ptkj-tijiaofabufasong"></span>');
            break;
          case '保存':
            sb.appendFormat('<span class="mui-icon iconfont icon-ptkj-baocun"></span>');
            break;
          case '退回':
            sb.appendFormat('<span class="mui-icon iconfont  icon-ptkj-huanyuanhuifu"></span>');
            break;
          case '直接退回':
            sb.appendFormat('<span class="mui-icon iconfont  icon-oa-zhijietuihui"></span>');
            break;
          case '撤回':
            sb.appendFormat('<span class="mui-icon iconfont  icon-oa-chehui"></span>');
            break;
          case '转办':
            sb.appendFormat('<span class="mui-icon iconfont icon-oa-zhuanban"></span>');
            break;
          case '会签':
            sb.appendFormat('<span class="mui-icon iconfont  icon-oa-huiqian"></span>');
            break;
          case '关注':
            sb.appendFormat('<span class="mui-icon iconfont  icon-oa-guanzhu"></span>');
            break;
          case '套打':
            sb.appendFormat('<span class="mui-icon iconfont  icon-wsbs-dayin"></span>');
            break;
          case '抄送':
            sb.appendFormat('<span class="mui-icon iconfont  icon-oa-chaosongwode"></span>');
            break;
          case '签署意见':
            sb.appendFormat('<span class="mui-icon iconfont  icon-oa-qianshuyijian"></span>');
            break;
          case '取消关注':
            sb.appendFormat('<span class="mui-icon iconfont   icon-oa-quxiaoguanzhu"></span>');
            break;
          case '办理过程':
            sb.appendFormat('<span class="mui-icon iconfont   icon-oa-banliguocheng"></span>');
            break;
          case '催办':
            sb.appendFormat('<span class="mui-icon iconfont   icon-ptkj-cuibanjian"></span>');
            break;
          case '特送个人':
            sb.appendFormat('<span class="mui-icon iconfont   icon-oa-tesonggeren"></span>');
            break;
          case '特送环节':
            sb.appendFormat('<span class="mui-icon iconfont    icon-ptkj-zaitechengjian"></span>');
            break;
          case '挂起':
            sb.appendFormat('<span class="mui-icon iconfont    icon-oa-guaqi"></span>');
            break;
          case '分享':
            sb.appendFormat('<span class="mui-icon iconfont    icon-ptkj-fenxiang"></span>');
            break;
          case '更多':
            sb.appendFormat('<span class="mui-icon iconfont    icon-ptkj-gengduocaidan"></span>');
            break;
          case '下载':
            sb.appendFormat('<span class="mui-icon iconfont    icon-ptkj-xiazai"></span>');
            break;
          case '移动到':
            sb.appendFormat('<span class="mui-icon iconfont    icon-ptkj-tuodong"></span>');
            break;
        }
        sb.appendFormat('<span class="mui-tab-label mui-label">{0}</span>', text);
      }
      sb.appendFormat('</a>');
    });
    return sb.toString();
  };

  // 创建标签
  formBuilder.buildTabBar = function (options) {
    var options = initOptions(
      {
        data: []
      },
      options
    );
    var StringBuilder = commons.StringBuilder;
    var sb = new StringBuilder();
    sb.appendFormat('<nav class="mui-bar mui-bar-tab wui-bar-tab">');
    sb.appendFormat(formBuilder.buildTabBarItems(options.data));
    sb.appendFormat('</nav>');
    return ($(options.container)[0].innerHTML = sb.toString());
  };

  // 创建ActionSheet可选操作按钮
  formBuilder.buildActionSheet = function (options) {
    var options = initOptions(
      {
        sheetId: '',
        data: []
      },
      options
    );
    var StringBuilder = commons.StringBuilder;
    var sb = new StringBuilder();
    sb.appendFormat('<div id="action_sheet_{0}" class="mui-popover mui-popover-action mui-popover-bottom">', options.sheetId);
    sb.appendFormat('<ul class="mui-table-view">');
    $.each(options.data, function (idx) {
      var id = this.id;
      var name = this.name;
      var text = this.text;
      var cssClass = this.cssClass || '';
      sb.appendFormat('<li class="mui-table-view-cell {2}" id={0} name={1} data-index="{3}">', id, name, cssClass, idx);
      sb.appendFormat('<a class="mui-label">{0}</a>', text);
      sb.appendFormat('</li>');
    });
    sb.appendFormat('</ul>');
    // 添加取消按钮
    sb.appendFormat('<ul class="mui-table-view">');
    sb.appendFormat('<li class="mui-table-view-cell">');
    sb.appendFormat('<a href="#action_sheet_{0}" class="mui-label"><b>取消</b></a>', options.sheetId);
    sb.appendFormat('</li>');
    sb.appendFormat('</ul>');

    sb.appendFormat('</div>');
    return ($(options.container)[0].innerHTML = sb.toString());
  };

  formBuilder.selectEditor = function (options, event) {
    var aEditor = this;
    var content = new StringBuilder();
    var value = null;
    if (typeof options.val === 'string') {
      value = options.val;
    } else if ($.isFunction(options.val)) {
      value = options.val.apply(options, [aEditor]);
    } else {
      value = $('input[name=' + options.name + ']', aEditor)[0].value;
    }
    var values = value.split(';') || [];
    content.append("<!--<div class='mui-scroll-wrapper'>--><ul class='mui-scroll mui-input-group' style=\"margin-top:10px;\">");
    var items = (options.items = options.items || []);
    var type = options.multiple ? 'checkbox' : 'radio';
    if (items.length === 0) {
      content.append("<div class='mui-input-row'>");
      content.appendFormat("<div class='mui-{0}'>", type);
      content.append('<label>没有可选数据!</label>');
      content.append('</div>');
      content.append('</div>');
    } else {
      if (!(options.require === true)) {
        var emptyText = options.emptyText || '空';
        var itemId = options.emptyId || 'empty-id-' + ++$.uuid;
        content.appendFormat("<li class='mui-input-row'>");
        content.appendFormat("<div class='mui-{0}'>", type);
        content.appendFormat("<label for='{0}'>{1}</label>", itemId, emptyText);
        content.appendFormat("<input type='{0}' id='{1}' value='{2}' name='{3}' text='{4}'>", type, itemId, '', options.name, emptyText);
        content.appendFormat('</div>');
        content.appendFormat('</li>');
      }
      for (var i = 0, len = items.length; i < len; i++) {
        var item = items[i];
        var itemId = options.name + item.id;
        var itemValue = item.value || item.id;
        var checkedStatu = values.indexOf(itemValue) >= 0 ? 'checked="checked"' : '';
        content.appendFormat("<li class='mui-input-row'>");
        content.appendFormat("<div class='mui-{0}'>", type);
        content.appendFormat("<label for='{0}'>{1}</label>", itemId, item.text);
        content.appendFormat(
          "<input type='{0}' id='{1}' value='{2}' name='{3}' text='{4}' {5}>",
          type,
          itemId,
          itemValue,
          options.name,
          item.text,
          checkedStatu
        );
        content.appendFormat('</div>');
        content.appendFormat('</li>');
      }
    }
    content.appendFormat('</ul><!--</div>-->');
    return formBuilder.showPanel({
      title: options.title || options.label,
      autoScroll: true,
      content: content.toString(),
      actionBack: {
        label: '取消',
        callback: options.cancel || $.noop
      },
      optButton:
        options.readonly === true
          ? null
          : {
              lable: '完成',
              callback: function (event, panel) {
                var valueObj = {
                  ids: [],
                  texts: [],
                  values: {}
                };
                var inputs = $('input[name=' + options.name + ']', panel);
                inputs.each(function (idx, element) {
                  var self = this;
                  if (self.checked === true) {
                    var value = self.value;
                    var text = self.getAttribute('text');
                    valueObj.values[value] = text;
                    valueObj.ids.push(value);
                    valueObj.texts.push(text);
                  }
                });
                if (options.require === true && valueObj.ids.length < 1) {
                  $.toast('请至少选择一项');
                  return false;
                } else if ($.isFunction(options.callback)) {
                  // 第一个返回值
                  Array.prototype.splice.call(arguments, 0, 0, valueObj);
                  var valueObj = options.callback.apply(this, arguments);
                  if ($.isPlainObject(valueObj) === false) {
                    return;
                  }
                }
                $('input[name=' + options.name + ']', aEditor)[0].value = valueObj.ids.join(';');
                $('input[name=' + options.display + ']', aEditor)[0].value = valueObj.texts.join(';');
                $('label[name=' + options.display + ']', aEditor)[0].innerHTML = valueObj.texts.join(';');
                $.trigger($('input[name=' + options.name + ']', aEditor)[0], 'change', valueObj);
              }
            },
      show: function (options) {
        // 滚动条
      },
      container: '#editpanel-' + options.name
    });
  };
  formBuilder.buildSwitch = function (options) {
    var options = initOptions({}, options);
    options.value = options.value === 'true' || options.value === true ? 'true' : 'false';
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat('<div class="form-group formbuilder mui-table-view-cell {0}">', options.divClass);
    fieldHtml.appendFormat('<a class="{0}">', 'mui-table-view-cell');
    fieldHtml.appendFormat(
      '<label for="{2}" class="{0} {1} control-label control-label-{3}">{3}</label>',
      colPrefix + options.labelColSpan,
      options.labelClass,
      options.name,
      options.label
    );
    fieldHtml.appendFormat(
      '<input type="hidden" name="{2}" id="{2}" value="{3}" data-val="{4}" class="{0} {1} form-control form-control-{1}" {5}>',
      colPrefix + options.controlColSpan,
      options.inputClass,
      options.name,
      options.value,
      options.value,
      options.readonly === true ? 'readonly="readonly"' : ''
    );
    fieldHtml.appendFormat(
      '<div class="mui-switch {0} {1}"><div class="mui-switch-handle"></div></div>',
      options.value === 'true' ? 'mui-active' : '',
      options.readonly === true ? 'mui-disabled' : ''
    );
    fieldHtml.append('</a>');
    fieldHtml.append('</div>');
    var fieldDom = $.dom(fieldHtml.toString())[0];
    var conatiner = $(options.container)[0];
    if (conatiner && conatiner.appendChild) {
      conatiner.appendChild(fieldDom);
    }
    var inputDom = $('input', fieldDom)[0];
    var $switchDom = $('.mui-switch', fieldDom);
    $switchDom['switch']();
    $switchDom[0].addEventListener('toggle', function (event) {
      inputDom.value = event.detail.isActive ? 'true' : 'false';
    });
    if ($.isPlainObject(options.events)) {
      applyDomEvents($('input[name]', fieldDom), events);
    }
    return fieldDom;
  };
  /**
   * 创建一个包含lable的Checkbox
   */
  formBuilder.buildOutlineCheckbox = function (options, textInited) {
    var self = this;
    var options = initOptions(
      {
        multiple: true,
        editor: formBuilder.selectEditor,
        items: []
      },
      options
    );
    if (textInited === true) {
    } else {
      var optionDatas = self._getFieldOptionValue(options.controlOption, options.items);
      options = $.extend(options, {
        items: optionDatas
      });
      if (StringUtils.isNotBlank(options.value)) {
        options.text = self._getSelectText(optionDatas, options.value);
      } else {
        options.text = '';
      }
    }
    return formBuilder.buildMapInput(options);
  };
  /**
   * 创建一个包含lable的Radio
   */
  formBuilder.buildOutlinRadio = function (options, textInited) {
    var self = this;
    var options = initOptions(
      {
        multiple: false,
        editor: formBuilder.selectEditor,
        items: []
      },
      options
    );
    if (textInited === true) {
    } else {
      var optionDatas = self._getFieldOptionValue(options.controlOption, options.items);
      options = $.extend(options, {
        items: optionDatas
      });
      if (StringUtils.isNotBlank(options.value)) {
        options.text = self._getSelectText(optionDatas, options.value);
      } else {
        options.text = '';
      }
    }
    return formBuilder.buildMapInput(options);
  };

  /**
   * 创建一个包含lable的Checkbox
   */
  formBuilder.buildCheckbox = function (options) {
    var options = initOptions(
      {
        items: []
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='mui-input-group formbuilder {0}'>", options.divClass);
    $.each(options.items, function (index, item) {
      var checkboxId = options.name + '_' + index;
      var checked = item.id == options.value ? 'checked' : '';
      fieldHtml.appendFormat("<div class='mui-input-row mui-checkbox mui-left label-formbuilder' >");
      fieldHtml.appendFormat("<label for='{0}'>{1}</label>", checkboxId, item.text);
      fieldHtml.appendFormat(
        "<input value='{0}' class='{1}' height='34px'" + " {2} name='{3}' id='{4}' type='checkbox' />",
        item.id,
        options.inputClass,
        checked,
        options.name,
        checkboxId
      );
      fieldHtml.appendFormat('</div>');
    });
    fieldHtml.appendFormat('</div>');
    var $container = $(options.container);
    $container[0].innerHTML = fieldHtml.toString();
    if ($.isPlainObject(options.events)) {
      $container.find("input[name='" + options.name + "']").on(options.events);
    }
  };
  /**
   * 创建一个包含lable的Radio
   */
  formBuilder.buildRadio = function (options) {
    var options = initOptions(
      {
        items: []
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='mui-input-group formbuilder {0}'>", options.divClass);
    $.each(options.items, function (index, item) {
      var radioId = options.name + '_' + index;
      var checked = item.id == options.value ? 'checked' : '';
      fieldHtml.appendFormat("<div class='mui-input-row mui-radio mui-left label-formbuilder' >");
      fieldHtml.appendFormat("<label for='{0}'>{1}</label>", radioId, item.text);
      fieldHtml.appendFormat(
        "<input value='{0}' class='{1}' height='34px'" + " {2} name='{3}' id='{4}' type='radio' />",
        item.id,
        options.inputClass,
        checked,
        options.name,
        radioId
      );
      fieldHtml.appendFormat('</div>');
    });
    fieldHtml.appendFormat('</div>');
    var $container = $(options.container);
    $container[0].innerHTML = fieldHtml.toString();
    if ($.isPlainObject(options.events)) {
      $container.find("input[name='" + options.name + "']").on(options.events);
    }
  };
  /**
   * 手机中的select转成radio列表
   */
  formBuilder.buildSelect = function (options) {
    this.buildSelect2(options);
  };
  /**
   * 手机中的select2转成radio、checkbox列表
   */
  formBuilder.buildSelect2 = function (options) {
    options.items = options.select2.data;
    var multiple = options.select2.multiple;
    if (multiple === true) {
      this.buildCheckbox(options);
    } else {
      this.buildRadio(options);
    }
  };
  // 获取select2group控件对应的数据源
  formBuilder.getSelect2GroupOptionValue = function (options, selected) {
    var optionValue = {};
    JDS.call({
      service: options.serviceUrl,
      data: {
        params: {
          id: selected
        }
      },
      async: false,
      success: function (result) {
        if (result.msg == 'success') {
          $.each(result.data.results, function (i) {
            $.each(result.data.results[i].children, function (j) {
              optionValue[this.id] = this.text;
            });
          });
        }
      }
    });
    return optionValue;
  };
  // 获取select2控件对应的数据源
  formBuilder.getSelect2OptionValue = function (options) {
    var select2 = {};
    if (options.optionType != 3) {
      select2.defaultBlank = false;
      select2.data = this._getFieldOptionValue(options);
    } else {
      select2.serviceName = 'select2DataStoreQueryService';
      select2.queryMethod = 'loadSelectData';
      select2.selectionMethod = 'loadSelectDataByIds';
      select2.remoteSearch = true;
      select2.defaultBlank = true;
      select2.defaultBlankText = '';
      select2.multiple = true;
      select2.params = {
        dataStoreId: options.dataStore,
        idColumnIndex: options.valueColumn,
        textColumnIndex: options.textColumn
      };
    }
    return select2;
  };

  formBuilder._getSelectText = function (optionDatas, selectValues) {
    var values = typeof selectValues == 'string' ? selectValues.split(';') : selectValues;
    var texts = [];
    $.each(optionDatas, function () {
      if ($.inArray(this.id, values) >= 0) {
        texts.push(this.text);
      }
    });
    return texts.join(';');
  };

  formBuilder._getFieldOptionValue = function (options, items) {
    var optionValue = [];
    if ((typeof options === 'undefined' || options == null) && items && items.length > 0) {
      return items;
    }
    switch (options.optionType) {
      case '1': // 常量
        return options.optionValue;
      case '2': // 数据字典
        JDS.call({
          service: 'dataDictionaryMaintain.getDataDictionariesByParentUuid',
          data: [options.dataDic],
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              $.each(result.data, function (idx, data) {
                optionValue.push({
                  id: data.code,
                  text: data.name
                });
              });
            }
          }
        });
        return optionValue;
      case '3': // 数据仓库
        JDS.call({
          service: 'viewComponentService.loadAllData',
          data: [options.dataStore],
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              $.each(result.data.data, function (idx, data) {
                optionValue.push({
                  id: data[options.valueColumn],
                  text: data[options.textColumn]
                });
              });
            }
          }
        });
        return optionValue;
      case '4':
        return optionValue;
      default:
        return optionValue;
    }
  };
  /**
   * 手机中的select转成radio列表
   */
  formBuilder.buildOutlineSelect = function (options, textInited) {
    var self = this;
    options.items = $.isArray(options.select2.data) ? options.select2.data : [];
    if ($.isArray(options.select2.data) === false) {
      $.each(options.select2.data, function (id, text) {
        options.items.push({
          id: id,
          text: text
        });
      });
    }
    if (textInited === true) {
    } else {
      var options = self._getFieldOptionValue(options.controlOption);
      options = $.extend(options, {
        items: optionDatas
      });
      if (StringUtils.isNotBlank(options.value)) {
        options.text = self._getSelectText(optionDatas, options.value);
      } else {
        options.text = '';
      }
    }
    var multiple = options.select2.multiple;
    if (multiple === true) {
      return self.buildOutlineCheckbox(options, true);
    } else {
      return self.buildOutlinRadio(options, true);
    }
  };
  /**
   * 手机中的select2转成radio、checkbox列表
   */
  formBuilder.buildOutlineSelect2 = function (options) {
    var self = this;
    var selectData = self.getSelect2OptionValue(options.controlOption);
    options.select2 = $.extend(options.select2, {
      data: selectData.data
    });
    if (StringUtils.isNotBlank(options.value)) {
      options.text = self._getSelectText(selectData.data, options.value);
    } else {
      options.text = '';
    }
    return self.buildOutlineSelect(options, true);
  };
  /**
   * 手机中的select2转成radio、checkbox列表
   */
  formBuilder.buildOutlineSelect2Group = function (options) {
    var self = this;
    var selectData = self.getSelect2GroupOptionValue(options.controlOption);
    options.select2 = $.extend(options.select2, {
      data: selectData
    });
    if (StringUtils.isNotBlank(options.value)) {
      options.text = selectData[options.value];
    } else {
      options.text = '';
    }
    return self.buildOutlineSelect(options, true);
  };
  formBuilder.textEditor = function (options, event) {
    var aEditor = this;
    var title = options.title || options.label;
    return formBuilder.showPanel({
      title: title,
      content: '<textarea rows="15" class="mui-input-clear" placeholder="请输入' + title + '" style="margin-top:10px;"></textarea>',
      actionBack: {
        label: '取消',
        callback: options.cancel || $.noop
      },
      optButton:
        options.readonly === true
          ? null
          : {
              lable: '完成',
              callback: function (event, panel) {
                var value = $('textarea', panel)[0].value || '';
                if (options.require === true && StringUtils.isBlank(value)) {
                  $.toast('请输入内容');
                  return false;
                } else if (options.minlength > 0 && value.length < options.minlength) {
                  $.toast('请输入内容不能少于' + options.minlength + '个字');
                  return false;
                } else if (options.maxlength > 0 && value.length >= options.maxlength) {
                  $.toast('请输入内容不能超过' + options.maxlength + '个字');
                  return false;
                } else if ($.isFunction(options.callback)) {
                  // 第一个返回值
                  Array.prototype.splice.call(arguments, 0, 0, value);
                  value = options.callback.apply(this, arguments);
                  if (typeof value !== 'string') {
                    return;
                  }
                }
                $('input[name=' + options.name + ']', aEditor)[0].value = value;
                $('label[name=' + options.name + ']', aEditor)[0].innerHTML = value;
                $.trigger($('input[name=' + options.name + ']', aEditor)[0], 'change', value);
              }
            },
      show: function () {
        var value = null;
        if (typeof options.val === 'string') {
          value = options.val;
        } else if ($.isFunction(options.val)) {
          value = options.val.apply(this, [aEditor, options]);
        } else {
          value = $('input[name=' + options.name + ']', aEditor)[0].value;
        }
        $('textarea', this)[0].innerHTML = value;
      },
      shown: function () {
        if (options.readonly === true) {
        } else {
          $.focus($('textarea', this)[0]);
        }
      },
      container: '#editpanel-' + options.name
    });
  };
  /**
   * 创建一个包含lable的input
   */
  formBuilder.buildMapInput = function (options) {
    var options = initOptions(
      {
        editor: formBuilder.selectEditor
      },
      options
    );
    if (StringUtils.isBlank(options.display)) {
      options.display = options.name + '_display';
    }
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat('<div class="form-group formbuilder mui-table-view-cell {0}">', options.divClass);
    fieldHtml.appendFormat('<a class="{0}">', options.inline === true ? 'mui-table-view-cell' : 'mui-navigate-right');
    fieldHtml.appendFormat(
      '<label for="{2}" class="{0} {1} control-label control-label-{3}">{3}</label>',
      colPrefix + options.labelColSpan,
      options.labelClass,
      options.name,
      options.label
    );
    fieldHtml.appendFormat(
      '<input type="hidden" name="{2}" id="{2}" value="{3}" class="{0} {1} form-control form-control-{1}" {4}>',
      colPrefix + options.controlColSpan,
      options.inputClass,
      options.name,
      options.value,
      options.readonly === true ? 'readonly="readonly"' : ''
    );
    fieldHtml.appendFormat(
      '<input type="hidden" name="{2}" id="{2}" value="{3}" class="{0} {1} form-control form-control-{1}" {4}>',
      colPrefix + options.controlColSpan,
      options.inputClass,
      options.display,
      options.text || options.value,
      options.readonly === true ? 'readonly="readonly"' : ''
    );
    fieldHtml.appendFormat(
      '<label name="{2}" id="{2}" value="{3}" class="{0} {1} form-control form-control-{1}" {4}>{3}</label>',
      colPrefix + options.controlColSpan,
      options.inputClass,
      options.display,
      options.text || options.value,
      options.readonly === true ? 'readonly="readonly"' : ''
    );
    fieldHtml.append('</a>');
    fieldHtml.append('</div>');
    var fieldDom = $.dom(fieldHtml.toString())[0];
    var conatiner = $(options.container)[0];
    if (conatiner && conatiner.appendChild) {
      conatiner.appendChild(fieldDom);
    }
    $(fieldDom).on('tap', '.mui-navigate-right', function (event) {
      // Array.prototype.push.call(arguments, options);
      Array.prototype.splice.call(arguments, 0, 0, options);
      if ($.isFunction(options.editor) && options.editor.apply(this, arguments)) {
      }
    });
    if ($.isPlainObject(options.events)) {
      applyDomEvents($('input[name]', fieldDom), events);
    }
    return fieldDom;
  };
  /**
   * 创建一个包含lable的input
   */
  formBuilder.buildInput = function (options) {
    var options = initOptions(
      {
        editor: formBuilder.textEditor
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat('<div class="form-group formbuilder mui-table-view-cell {0}">', options.divClass);
    fieldHtml.appendFormat('<a class="{0}">', options.inline === true ? 'mui-table-view-cell' : 'mui-navigate-right');
    fieldHtml.appendFormat(
      '<label for="{2}" class="{0} {1} control-label control-label-{3}">{3}</label>',
      colPrefix + options.labelColSpan,
      options.labelClass,
      options.name,
      options.label
    );
    fieldHtml.appendFormat(
      '<input type="hidden" name="{2}" id="{2}" value="{3}" data-val="{4}" class="{0} {1} form-control form-control-{1}" {5}>',
      colPrefix + options.controlColSpan,
      options.inputClass,
      options.name,
      options.text || options.value,
      options.value,
      options.readonly === true ? 'readonly="readonly"' : ''
    );
    fieldHtml.appendFormat(
      '<label name="{2}" id="{2}" value="{3}" data-val="{4}" class="{0} {1} form-control form-control-{1}" {5}>{3}</label>',
      colPrefix + options.controlColSpan,
      options.inputClass,
      options.name,
      options.text || options.value,
      options.value,
      options.readonly === true ? 'readonly="readonly"' : ''
    );
    fieldHtml.append('</a>');
    fieldHtml.append('</div>');
    var fieldDom = $.dom(fieldHtml.toString())[0];
    var conatiner = $(options.container)[0];
    if (conatiner && conatiner.appendChild) {
      conatiner.appendChild(fieldDom);
    }
    $(fieldDom).on('tap', '.mui-navigate-right', function (event) {
      // Array.prototype.push.call(arguments, options);
      Array.prototype.splice.call(arguments, 0, 0, options);
      if ($.isFunction(options.editor) && options.editor.apply(this, arguments)) {
      }
    });
    if ($.isPlainObject(options.events)) {
      applyDomEvents($('input[name]', fieldDom), events);
    }
    return fieldDom;
  };

  formBuilder.getDtPickerType = function (contentFormat) {
    var format = '';
    var fmt = contentFormat;
    if (fmt == 'YYYY-MM-DD HH:mm:ss' || fmt == 'YYYY-MM-DD HH:mm') {
      format = 'datetime';
    } else if (fmt == 'YYYY-MM-DD HH:mm') {
      format = 'hour';
    } else if (fmt == 'YYYY-MM-DD') {
      format = 'date';
    } else if (fmt == 'HH:mm:ss') {
      format = 'time';
    } else if (fmt == 'YYYY-MM' || fmt == 'YYYY') {
      format = 'month';
    }
    return format;
  };

  // 扩展DtPicker
  var btnClear = '<button data-id="btn-clear" class="mui-clear">清空</button>';
  var btnToday = '<button data-id="btn-today" class="mui-today" style="margin-left: 2px;">今天</button>';
  var WDtPicker = (formBuilder.WDtPicker = $.WDtPicker = $.DtPicker.extend({
    init: function (options) {
      var self = this;
      self._super(options);
      var ui = self.ui;
      var picker = ui.picker;
      if (!self.options.cancelable) {
        ui.cancel.style.display = 'none';
      }
      ui.ok.classList.add('dt-btn-ok');
      ui.cancel.classList.add('dt-btn-cancel');
      ui.ok.parentNode.insertBefore($.dom(btnClear)[0], ui.ok);
      ui.ok.parentNode.insertBefore($.dom(btnToday)[0], ui.ok);
      ui.clear = $('[data-id="btn-clear"]', picker)[0];
      ui.today = $('[data-id="btn-today"]', picker)[0];
      ui.clear.addEventListener(
        'tap',
        function () {
          var type = self.options.type;
          var selected = {
            type: type,
            toString: function () {
              return this.value;
            }
          };
          selected.y = selected.m = selected.d = selected.h = selected.i = 0;
          selected.value = selected.text = '';
          var rs = self.callback(selected);
          if (rs !== false) {
            self.hide();
          }
        },
        false
      );
      ui.today.addEventListener(
        'tap',
        function () {
          function callback() {
            var rs = self.callback(self.getSelected());
            if (rs !== false) {
              self.hide();
            }
          }
          // 设置当前时间，并回调
          self.setSelectedValue(null, callback); // undefined
        },
        false
      );
    },
    /**
     * setSelectedValue添加callback
     */
    setSelectedValue: function (value, callback) {
      var self = this;
      var ui = self.ui;
      var parsedValue = self._parseValue(value);
      // setSelectedValue(value, duration, callback)中callback为异步触发
      ui.y.picker.setSelectedValue(parsedValue.y, 0, function () {
        ui.m.picker.setSelectedValue(parsedValue.m, 0, function () {
          ui.d.picker.setSelectedValue(parsedValue.d, 0, function () {
            ui.h.picker.setSelectedValue(parsedValue.h, 0, function () {
              ui.i.picker.setSelectedValue(parsedValue.i, 0, callback);
            });
          });
        });
      });
    },
    setReadOnly: function (readonly) {
      var self = this;
      var ui = self.ui;
      ui.ok.style.display = readonly ? 'none' : '';
      ui.today.style.display = readonly ? 'none' : '';
      ui.clear.style.display = readonly ? 'none' : '';
      ui.cancel.style.display = readonly ? '' : 'none';
    }
  }));

  formBuilder.dateEditor = function (options, event) {
    var aEditor = this;
    var picker = $.data[aEditor.getAttribute('data-dtpicker')];
    if (!picker) {
      var id = 'wdate' + ++$.uuid;
      aEditor.setAttribute('data-dtpicker', id);
      var timePicker = $.extend({}, defaultDatetimepickerOptions, options.timePicker);
      picker = $.data[id] = new $.WDtPicker(timePicker);
    }
    var value = null;
    if (typeof options.val === 'string') {
      value = options.val;
    } else if ($.isFunction(options.val)) {
      value = options.val.apply(this, [aEditor, options]);
    } else {
      value = $('input[name=' + options.name + ']', aEditor)[0].value;
    }
    picker.setSelectedValue(value);
    picker.setReadOnly(options.readonly === true);
    picker.show(function (selectItem) {
      var value = selectItem.value;
      if ($.isFunction(options.callback)) {
        // 第一个返回值
        Array.prototype.splice.call(arguments, 0, 0, value);
        value = options.callback.apply(this, arguments);
        if (typeof value !== 'string') {
          return;
        }
      }
      $('input[name=' + options.name + ']', aEditor)[0].value = value;
      $('label[name=' + options.name + ']', aEditor)[0].innerHTML = value;
      $.trigger($('input[name=' + options.name + ']', aEditor)[0], 'change', value);
    });
    return picker;
  };
  /**
   * 创建一个包含lable的时间选择控件
   */
  formBuilder.buildDatetimepicker = function (options) {
    var options = initOptions(
      {
        editor: formBuilder.dateEditor,
        dataIcon: 'glyphicon-calendar'
      },
      options
    );
    var ctrlFormat = options.controlOption && options.controlOption.format;
    options.timePicker = $.extend(options.timePicker, {
      type: formBuilder.getDtPickerType(ctrlFormat)
    });
    if (ctrlFormat && options.value) {
      var text = moment(options.value).format(ctrlFormat);
      options.text = text;
    }
    return formBuilder.buildInput(options);
  };
  formBuilder.getOrgName = function (ids) {
    var texts = [];
    JDS.call({
      version: null,
      service: 'orgApiFacade.getNameByOrgEleIds',
      data: [ids],
      async: false,
      success: function (result) {
        if (result.success) {
          $.each(result.data, function (k, v) {
            texts.push(v);
          });
        }
      }
    });
    return texts.join(';');
  };

  formBuilder.unitEditor = function (options, event) {
    var aEditor = this;
    // 添加选择域
    aEditor.id = aEditor.id || 'unit-id-' + ++$.uuid;
    appContext.require(['multiOrg'], function (unit) {
      var controlOption = options.controlOption || {};
      unit.open({
        defaultType: controlOption.defaultType,
        type: controlOption.orgTypes,
        readonly: options.readonly,
        labelField: '#' + aEditor.id + ' #' + options.display,
        valueField: '#' + aEditor.id + ' #' + options.name,
        container: options.container,
        afterSelect: function (returnValue) {
          var self = this;
          $('label[name=' + options.display + ']', aEditor)[0].innerHTML = returnValue.name;
          $.trigger($('label[name=' + options.display + ']', aEditor)[0], 'change', returnValue);
          if ($.isFunction(options.afterSelect) && options.afterSelect.apply(self, arguments)) {
          }
        }
      });
    });
    return aEditor;
  };

  /**
   * 创建一个包含lable的组织选择框
   */
  formBuilder.buildUnit = function (options) {
    var _self = this;
    var options = initOptions(
      {
        editor: formBuilder.unitEditor
      },
      options
    );
    if (StringUtils.isNotBlank(options.value)) {
      var values = options.value.split(';');
      options.text = _self.getOrgName(values);
    }
    return formBuilder.buildMapInput(options);
  };

  formBuilder.fileEditor = function (options, event) {
    var aEditor = this;
    appContext.require(['mui-fileupload'], function (muiFileupload) {
      var ftl =
        '<div class="mui-input-row mui-attach-row" style="margin-top:10px;"><input type="file" id="{1}" name="{1}" class="dyform-field-editable">\
				<ul class="mui-table-view attach-container">\
				<li class="mui-table-view-cell ">\
						<div class="mui-button-row mui-clearfix attach-button-row">\
							<input type="file" name="{1}-clone" class="mui-btn mui-btn-blue attach-picker"/>\
							<buttom type="button" class="mui-btn mui-btn-blue btn-fileshowtype">图标/</buttom>\
							<buttom type="button" class="mui-btn mui-btn-blue btn-filedownall">下载</buttom>\
							<buttom type="button" class="mui-btn mui-btn-blue btn-filedelall">清空</buttom>\
						</div>\
						<ul class="mui-table-view attach-table-view">\
						</ul>\
				</li>\
			</ul><span class="dyform-field-error mui-hidden"></span></div>';
      var $upload = null; // 定义上传控件局部变量
      var fieldHtml = new StringBuilder();
      fieldHtml.appendFormat(ftl, options.label, options.name);
      formBuilder.showPanel({
        title: options.title || options.label,
        content: fieldHtml.toString(),
        actionBack: {
          label: '取消',
          callback: options.cancel || $.noop
        },
        optButton:
          options.readonly === true
            ? null
            : {
                lable: '完成',
                callback: function (event, panel) {
                  var fileObj = {
                    fileIds: [],
                    files: $upload.getItems()
                  };
                  $.each(fileObj.files, function (idx, file) {
                    fileObj.fileIds.push(file.fileID);
                  });
                  if ($.isFunction(options.callback)) {
                    // 第一个返回值
                    Array.prototype.splice.call(arguments, 0, 0, fileObj);
                    fileObj = options.callback.apply(this, arguments);
                    if ($.isPlainObject(fileObj) === false) {
                      return;
                    }
                  }
                  $('input[name=' + options.name + ']', aEditor)[0].value = fileObj.fileIds.join(';');
                  $('input[name=' + options.display + ']', aEditor)[0].value = fileObj.files.length + '个附件';
                  $('label[name=' + options.display + ']', aEditor)[0].innerHTML = fileObj.files.length + '个附件';
                  $.trigger($('input[name=' + options.name + ']', aEditor)[0], 'change', fileObj);
                }
              },
        show: function () {
          var value = null;
          if (typeof options.val === 'string') {
            value = options.val;
          } else if ($.isFunction(options.val)) {
            value = options.val.apply(this, [aEditor, options]);
          } else {
            value = $('input[name=' + options.name + ']', aEditor)[0].value;
          }
          var fileOptions = $.extend(
            null,
            {
              fileIds: value
            },
            options
          );
          fileOptions.itemFormat = function (file) {
            var self = this;
            var item =
              '<li class="mui-table-view-cell">\
							<div class="attach-handler"><h4 class="mui-ellipsis">' +
              (file && file.fileName) +
              '</h4><h5 >' +
              file.createTime +
              '<span class="filesize">' +
              self.formatSize(file.fileSize) +
              '</span></h5></div><a class="mui-btn btn-filedel mui-icon mui-icon-info"></a></li>';
            return item;
          };
          $upload = new muiFileupload($('.dyform-field-editable', this)[0], fileOptions);
          $upload.on('afterSetValue', function (files, append, itemFormat) {
            // debugger
          });
        },
        container: '#editpanel-' + options.name
      });
    });
  };
  /**
   * 创建一个包含lable的附件上传
   */
  formBuilder.buildFileUpload = function (options, isAsLabel) {
    options = initOptions(
      {
        multiple: true,
        autoUpload: true,
        editor: formBuilder.fileEditor
      },
      options
    );
    if (StringUtils.isNotBlank(options.value)) {
      options.text = options.value.split(';').length + '个附件';
    }
    var controlOption = options.controlOption || {};
    return formBuilder.buildMapInput(options);
  };

  formBuilder.buildSeperate = function (options) {
    var options = initOptions({}, options);
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat('<div class="form-group form-group-seperate formbuilder mui-table-view-cell {0}"></div>', options.divClass);
    var fieldDom = $.dom(fieldHtml.toString())[0];
    var conatiner = $(options.container)[0];
    if (conatiner && conatiner.appendChild) {
      conatiner.appendChild(fieldDom);
    }
    return fieldDom;
  };
  /**
   * 构造输入域的内容，可以支持一行一列或者一行两列的布局
   */
  formBuilder.buildContent = function (options) {
    var options = $.extend({}, defaultContentOptions, options);
    var _self = this;
    var columnNum = options.multiLine ? 2 : 1;
    options.divClass = colPrefix + (options.multiLine ? '6' : '12') + ' ' + options.divClass;
    var $container = $(options.container);
    var rowDiv = $container[0];
    rowDiv.classList.add('form-content');
    var $containerElement = null;
    $.each(options.contentItems, function (index, itemOptions) {
      itemOptions.container = rowDiv;
      copyProperty(options, itemOptions, copyProperties);
      switch (itemOptions.type) {
        case 'text':
          _self.buildInput(itemOptions);
          break;
        case 'textarea':
          _self.buildTextarea(itemOptions);
          break;
        case 'timeInterval':
          _self.buildBetweenDatetimepicker(itemOptions);
          break;
        case 'date':
          _self.buildDatetimepicker(itemOptions);
          break;
        case 'radio':
          if (itemOptions.outline === true) {
            _self.buildOutlinRadio(itemOptions);
          } else {
            _self.buildRadio(itemOptions);
          }
          break;
        case 'checkbox':
          if (itemOptions.outline === true) {
            _self.buildOutlineCheckbox(itemOptions);
          } else {
            _self.buildCheckbox(itemOptions);
          }
          break;
        case 'select':
          if (itemOptions.outline === true) {
            _self.buildOutlineSelect(itemOptions);
          } else {
            _self.buildSelect(itemOptions);
          }
          break;
        case 'select2':
          if (itemOptions.outline === true) {
            _self.buildOutlineSelect2(itemOptions);
          } else {
            _self.buildSelect2(itemOptions);
          }
          break;
        case 'select2Group':
          if (itemOptions.outline === true) {
            _self.buildOutlineSelect2Group(itemOptions);
          } else {
            _self.buildSelect2Group(itemOptions);
          }
          break;
        case 'unit':
          _self.buildUnit(itemOptions);
          break;
        case 'fileUpload':
          _self.buildFileUpload(itemOptions, true);
          break;
        case 'switch':
          _self.buildSwitch(itemOptions);
          break;
        case 'seperate':
          _self.buildSeperate(itemOptions);
          break;
        default:
          _self.buildInput(itemOptions);
      }
    });
  };

  /**
   * 创建两个日期输入框，形成日期区间
   *
   */
  formBuilder.buildBetweenDatetimepicker = function (options) {
    var options = initOptions(
      {
        beginName: '',
        beginValue: '',
        endName: '',
        endValue: '',
        dataIcon: 'glyphicon-calendar'
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='mui-input-row'>");
    fieldHtml.appendFormat("<label class='mui-ellipsis'>起始时间</label>");
    fieldHtml.appendFormat("<label class='dyform-field-label mui-hidden'></label>");
    fieldHtml.appendFormat(
      "<input type='text' id='startDatePicker' class='dyform-field-editable date-picker' value='' placeholder='请输入' style='ime-mode:disabled' readonly='readonly'>"
    );
    fieldHtml.appendFormat('</div>');
    fieldHtml.appendFormat("<div class='mui-input-row'>");
    fieldHtml.appendFormat("<label class='mui-ellipsis'>结束时间</label>");
    fieldHtml.appendFormat("<label class='dyform-field-label mui-hidden'></label>");
    fieldHtml.appendFormat(
      "<input type='text' id='endDatePicker' class='dyform-field-editable date-picker' value='' placeholder='请输入' style='ime-mode:disabled' readonly='readonly'>"
    );
    fieldHtml.appendFormat('</div>');

    var doms = $.dom(fieldHtml.toString());
    $(options.container)[0].appendChild(doms[0]);
    $(options.container)[0].appendChild(doms[1]);
    var timePicker = $.extend({}, defaultDatetimepickerOptions, options.timePicker);
    var muidtPicker = new $.DtPicker(timePicker);
    var dtInput = $(options.container)[0].querySelectorAll('.date-picker');
    $.each(dtInput, function (i, n) {
      n.setAttribute('readOnly', true);
      n.addEventListener('tap', function () {
        var _self = this;
        muidtPicker.show(function (selectItem) {
          console.log(selectItem);
          _self.value = selectItem.value;
          if ($('#endDatePicker')[0].value != '' && $('#startDatePicker')[0].value != '') {
            if ($('#startDatePicker')[0].value > $('#endDatePicker')[0].value) {
              mui.alert('起始时间不能大于结束时间！');
              _self.value = '';
              return;
            }
          }
          if (options.callback) {
            options.callback(_self.value, _self.id);
          }
        });
      });
    });

    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };

  /**
   * 创建一个包含lable的Select2
   */
  var defaultContentOptions = {
    multiLine: false,
    rowClass: '',
    container: '',
    labelColSpan: '2',
    controlColSpan: '10',
    divClass: '',
    inputClass: '',
    contentItems: []
  };
  var copyProperties = ['labelColSpan', 'controlColSpan', 'divClass', 'inputClass'];
  var copyProperty = function (source, target, properties) {
    $.each(properties, function (index, property) {
      if (StringUtils.isBlank(target[property])) {
        target[property] = source[property];
      }
    });
  };
  return formBuilder;
});

define(['mui', 'commons', 'server', 'appDispatcher', 'appContext', 'appWindowManager', 'appModal', 'app-adapter', 'i18n'], function (
  $,
  commons,
  server,
  appDispatcher,
  appContext,
  appWindowManager,
  appModal,
  adapter,
  i18n
) {
  // if (window.plus && window.plus.os.name == "Android") {
  //   var nwv = plus.android.currentWebview();
  //   var setting = plus.android.invoke(nwv, "getSettings");
  //   plus.android.invoke(setting, "setSupportZoom", true);
  //   plus.android.invoke(setting, "setBuiltInZoomControls", true);
  //   plus.android.invoke(setting, "setDisplayZoomControls", false);
  // }
  var SpringSecurityUtils = server.SpringSecurityUtils;
  try {
    if (SpringSecurityUtils.getUserDetails()) {
      // 已登录
    } else {
      // 未登录退回
      location.reload();
    }
  } catch (e) {}
  var StringUtils = commons.StringUtils;
  // 0、mui fixtools
  (function ($, window, document) {
    // 1、搜索框清理图标显示与隐藏
    if (!$.fn._input) {
      var _input = $.fn.input;
      $.fn._input = function (options) {
        var input = _input.apply(this, arguments);
        if (input && input.clearAction) {
          input.element.addEventListener('blur', function (event) {
            event.preventDefault();
            event.stopPropagation();
            var action = input.element.value === '' ? 'add' : 'remove';
            input.clearAction.classList[action]('mui-hidden');
            return false;
          });
        }
        return input;
      };
      $.fn.input = $.fn._input;
    }

    if (!Element.prototype.matches) {
      Element.prototype.matches =
        Element.prototype.matchesSelector ||
        Element.prototype.mozMatchesSelector ||
        Element.prototype.msMatchesSelector ||
        Element.prototype.oMatchesSelector ||
        Element.prototype.webkitMatchesSelector ||
        function (s) {
          var matches = (this.document || this.ownerDocument).querySelectorAll(s),
            i = matches.length;
          while (--i >= 0 && matches.item(i) !== this) {}
          return i > -1;
        };
    }

    $.screen = {
      availWidth: document.documentElement['clientWidth'],
      availHeight: document.documentElement['clientHeight']
    };
    //创建 DOM
    $.dom = function (str) {
      if (typeof str !== 'string') {
        if (str instanceof Array || (str[0] && str.length)) {
          return [].slice.call(str);
        } else {
          return [str];
        }
      }
      if (!$.__create_dom_div__) {
        $.__create_dom_div__ = document.createElement('div');
      }
      $.__create_dom_div__.innerHTML = str;
      return [].slice.call($.__create_dom_div__.childNodes);
    };

    $.fn.closest = function (selector, end) {
      if (Element.prototype.closest) {
        return Element.prototype.closest.apply(this[0], [selector]);
      }
      var elem = this[0];
      if (!document.documentElement.contains(elem)) {
        return null;
      }
      end = end || document.body;
      for (; elem && elem !== end; elem = elem.parentElement || elem.parentNode) {
        if (elem.matches && elem.matches(selector)) {
          return elem;
        }
      }
    };
    $.fn.parents = function (selector, end) {
      console.error('$.fn.parents @Deprecated, $.fn.closest instead, @see\nhttp://www.w3school.com.cn/jquery/traversing_closest.asp');
      return $.fn.closest.apply(this, arguments);
    };
    $.fn.show = function () {
      var ele = this[0];
      if (ele && ele.style) {
        ele.style.display = '';
      }
    };
    $.fn.hide = function () {
      var ele = this[0];
      if (ele && ele.style) {
        ele.style.display = 'none';
      }
    };

    document.addEventListener('scroll', function (event) {
      var scrollApi = event.detail;
      if ($.isScrolling && scrollApi instanceof $.Scroll) {
        var focusElement = document.querySelector('.mui-dyform input:focus') || document.querySelector('.mui-dyform textarea:focus');
        if (focusElement && focusElement.blur) {
          // focusElement.blur();// 滚动时,关闭输入
        }
      }
    });
    var core_indexOf = Array.prototype.indexOf;
    $.inArray = function (elem, arr, i) {
      var len;
      if (arr) {
        if (core_indexOf) {
          return core_indexOf.call(arr, elem, i);
        }

        len = arr.length;
        i = i ? (i < 0 ? Math.max(0, len + i) : i) : 0;

        for (; i < len; i++) {
          // Skip accessing in sparse arrays
          if (i in arr && arr[i] === elem) {
            return i;
          }
        }
      }
      return -1;
    };

    $(document.body).on('tap', '*[toast-text]', function (event) {
      var popText = null;
      var target = event.target;
      if (target && (popText = target.getAttribute('toast-text') || target.innerText)) {
        $.toast(popText);
      }
    });

    $.fn.data = function (key, value) {
      var self = this[0];
      if (typeof value === 'undefined' || value == null) {
        return self.getAttribute('data-' + key);
      } else {
        self.setAttribute('data-' + key, value);
      }
    };

    $.fn.offsetParent = function () {
      var self = this[0];
      return self.offsetParent || document.body;
    };

    $.fn.json2form = function (bean) {
      for (var property in bean) {
        var value = bean[property];
        if (typeof value === 'undefined' || value == null) {
          value = value || '';
        }
        var element = $("input[name='" + property + "']", this[0])[0];
        if (typeof element === 'undefined' || element == null) {
          element = $("select[name='" + property + "']", this[0])[0];
        }
        if (typeof element === 'undefined' || element == null) {
          element = $("textarea[name='" + property + "']", this[0])[0];
        }
        if (element && element.tagName.toLowerCase() === 'input') {
          element.value = value;
        } else if (element && element.tagName.toLowerCase() === 'textarea') {
          element.innerHTML = value;
        } else if (element && element.tagName.toLowerCase() === 'select') {
          $.each(element.options, function (i, option) {
            if (StringUtils.contains(value, option.value)) {
              option.setAttribute('selected', 'selected');
            }
          });
        }
      }
      return bean;
    };
    $.fn.form2json = function (bean) {
      for (var property in bean) {
        var element = $("input[name='" + property + "']", this[0])[0];
        if (typeof element === 'undefined' || element == null) {
          element = $("select[name='" + property + "']", this[0])[0];
        }
        if (typeof element === 'undefined' || element == null) {
          element = $("textarea[name='" + property + "']", this[0])[0];
        }
        if (element && (element.tagName.toLowerCase() === 'input' || element.tagName.toLowerCase() === 'select')) {
          bean[property] = element.value;
        } else if (element && element.tagName.toLowerCase() === 'textarea') {
          bean[property] = element.innerHTML;
        }
      }
      return bean;
    };

    // 根据wui-action-dragging处理侧滑背景透视问题
    var oldOffCanvas = $.fn.offCanvas;
    $.fn.offCanvas = function (options) {
      var offCanvasApis = oldOffCanvas.apply(this, arguments);
      if (false === $.isArray(offCanvasApis)) {
        offCanvasApis = [offCanvasApis];
      }
      $.each(offCanvasApis, function (idx, offCanvasApi) {
        var setTranslateX = offCanvasApi.setTranslateX;
        offCanvasApi.setTranslateX = function (x) {
          var self = this;
          var offCanvasWidth = self.slideIn ? self.offCanvasWidth : 0;
          var classAction = Math.abs(x) === offCanvasWidth ? 'remove' : 'add';
          self.wrapper.classList[classAction]('wui-off-canvas-active');
          setTranslateX.apply(self, arguments);
        };
      });
      return offCanvasApis.length === 1 ? offCanvasApis[0] : offCanvasApis;
    };
    $.fn.offCanvas.noConflict = function () {
      $.fn.offCanvas = oldOffCanvas;
      return this;
    };

    var docElem = document.documentElement;
    $.contains = docElem.contains
      ? function (a, b) {
          var adown = a.nodeType === 9 ? a.documentElement : a,
            bup = b && b.parentNode;
          return a === bup || !!(bup && bup.nodeType === 1 && adown.contains && adown.contains(bup));
        }
      : docElem.compareDocumentPosition
      ? function (a, b) {
          return b && !!(a.compareDocumentPosition(b) & 16);
        }
      : function (a, b) {
          while ((b = b.parentNode)) {
            if (b === a) {
              return true;
            }
          }
          return false;
        };
    var core_rnotwhite = /\S/;
    var rcleanScript = /^\s*<!(?:\[CDATA\[|\-\-)|[\]\-]{2}>\s*$/g;
    ($.globalEval = function (data) {
      if (data && core_rnotwhite.test(data)) {
        // We use execScript on Internet Explorer
        // We use an anonymous function so that context is window
        // rather than jQuery in Firefox
        (
          window.execScript ||
          function (data) {
            window['eval'].call(window, data);
          }
        )(data);
      }
    }),
      ($.fn.append = function (value) {
        var self = this;
        var scriptSelector = 'script:not([script-evecd])';
        if (value != null && typeof value !== 'undefined') {
          var docs = value;
          if (typeof value === 'string') {
            docs = $.dom(value);
          } else if (docs.length == null || typeof docs.length === 'undefined') {
            docs = [docs];
          }
          $(scriptSelector, self[0]).each(function (idx, elem) {
            elem.setAttribute('script-evecd', '1');
          });
          $.each(docs, function (idx, doc) {
            self[0].appendChild(doc);
          });
          $(scriptSelector, self[0]).each(function (idx, elem) {
            if (elem.src) {
              if (mui.ajax) {
                mui.ajax({
                  url: elem.src,
                  type: 'GET',
                  dataType: 'script',
                  async: false,
                  global: false,
                  throws: true
                });
              } else {
                throw new 'no ajax'();
              }
            } else {
              mui.globalEval((elem.text || elem.textContent || elem.innerHTML || '').replace(rcleanScript, ''));
            }
          });
        }
        return self;
      });
    $.fn.html = function (value) {
      var self = this;
      if (value == null || typeof value === 'undefined') {
        return self[0].innerHTML;
      }
      self[0].innerHTML = '';
      return self.append(value);
    };

    $.grep = function (elems, callback, inv) {
      var retVal,
        ret = [],
        i = 0,
        length = elems.length;
      inv = !!inv;

      // Go through the array, only saving the items
      // that pass the validator function
      for (; i < length; i++) {
        retVal = !!callback(elems[i], i);
        if (inv !== retVal) {
          ret.push(elems[i]);
        }
      }
      return ret;
    };

    // Overwrites native 'firstElementChild' prototype.
    // Adds Document & DocumentFragment support for IE9 & Safari.
    // Returns array instead of HTMLCollection.
    (function (constructor) {
      if (constructor && constructor.prototype && constructor.prototype.firstElementChild == null) {
        Object.defineProperty(constructor.prototype, 'firstElementChild', {
          get: function () {
            var node,
              nodes = this.childNodes,
              i = 0;
            while ((node = nodes[i++])) {
              if (node.nodeType === 1) {
                return node;
              }
            }
            return null;
          }
        });
      }
    })(window.Node || window.Element);

    var core_trim = String.prototype.trim;
    $.trim = function (text) {
      return text == null ? '' : core_trim.call(text);
    };

    var scrollRefresh = $.Scroll.prototype.refresh;
    if (null == $.Slider.prototype.scrollRefresh) {
      $.Slider.prototype.scrollRefresh = scrollRefresh;
      $.Slider.prototype.refresh = function () {
        var self = this;
        var elem = self.scroller;
        if (elem && elem.offsetWidth === 0 && elem.offsetHeight === 0) {
          return; // 隐藏滚动不不刷新：http://zen.well-soft.com:81/zentao/bug-view-52546.html
        }
        scrollRefresh.apply(self, arguments);
      };
    }
  })(mui, window, document);
  // 1、配置应用上下文
  (function configureAppContext(appContext) {
    var env = WebApp.environment || {};
    // 标记为手机App
    env.isMobileApp = true;
    // 1.1、设置系统参数
    appContext.setEnvironment(env);

    // 1.2、设置JS模板
    appContext.setJavaScriptTemplates(WebApp.jsTemplates);

    // 1.3、设置窗口管理器
    appContext.setWindowManager(appWindowManager(appContext));

    // 1.4、注册默认的应用派发器
    for (var i = 0; i < appDispatcher.length; i++) {
      appContext.registerAppDispatcher(appDispatcher[i]);
    }
  })(appContext);
  // 2、面板加载及返回操作
  $.ui = $.ui || {};
  $.ui.loadContentQueue = [];
  var topOffsetHead = adapter.qing ? 0 : 44;
  var topOffsetScreen = $.screen.availHeight / 2;
  $.ui.scrollIntoView = function (focusElement, focus) {
    var pageY = $.offset(focusElement).top;
    // $.alert("clientHeight:" + document.documentElement.clientHeight);
    // $.alert("topOffsetHead:" + topOffsetHead + ",topOffsetScreen:" + topOffsetScreen);
    if (pageY >= topOffsetHead && pageY < topOffsetScreen - 96) {
      // 不会被键盘遮挡(键盘过高,添加96偏差)
      console.log('pageY:' + pageY);
      // $.alert("pageY:" + pageY);
      return false;
    }
    var scrollElement = $(focusElement).closest('.mui-scroll-wrapper');
    var scrollApi = $(scrollElement).scroll();
    if (scrollApi && scrollApi.refresh) {
      var lastY = scrollApi.lastY;
      // console.log(lastY + ":" + pageY);
      // $.alert(lastY + ":" + pageY);
      var scrollTo = Math.abs(lastY) + pageY;
      scrollApi.scrollTo(0, topOffsetScreen / 2 - scrollTo); // screen.availHeight/4的TOP偏移
      focus && focusElement.focus();
    }
  };
  // 设置元素可滚动（可自定义高度和宽度）
  $.ui.scrollAble = function (scrollElement, options) {
    options = $.extend(
      {
        scrollY: true, //是否竖向滚动
        scrollX: false, //是否横向滚动
        startX: 0, //初始化时滚动至x
        startY: 0, //初始化时滚动至y
        indicators: true, //是否显示滚动条
        deceleration: 0.0006, //阻尼系数,系数越小滑动越灵敏
        bounce: true //是否启用回弹
      },
      options
    );
    var scrollView = scrollElement.parentNode;
    var availHeight = scrollView.clientHeight;
    var availWidth = scrollView.clientWidth;
    if (options.scrollY && options.height > 0) {
      availHeight = options.height;
      scrollView.style.height = availHeight + 'px';
    }
    if (options.scrollX && options.width > 0) {
      availWidth = options.width;
      scrollView.style.width = availWidth + 'px';
    }
    var i = 0,
      node,
      nodes = scrollView.childNodes;
    while (options.autoHeight && (node = nodes[i++])) {
      if (node.nodeType !== 1 || node == scrollElement) {
        continue;
      }
      availHeight -= node.clientHeight;
    }
    if (scrollElement.clientHeight > availHeight || scrollElement.clientWidth > availWidth) {
      scrollElement.classList.add('mui-scroll-wrapper');
      if (options.scrollY && availHeight > 0) {
        scrollElement.style.height = availHeight + 'px';
      }
      var i = 0,
        node,
        nodes = scrollElement.childNodes;
      while ((node = nodes[i++])) {
        if (node.nodeType === 1 && node.classList) {
          node.classList.add('mui-scroll');
        }
      }
      mui(scrollElement).scroll(options);
    }
  };
  // 触发菜单修改事件
  $.ui.triggerMenuChange = function (element) {
    var optionMenu;
    element = element || document.body;
    var $optionMenu = $('header.mui-bar-nav>.mui-pull-right', element);
    for (var i = 0; i < $optionMenu.length; i++) {
      var tmpMenu = $optionMenu[i];
      if (!tmpMenu.classList.contains('mui-hidden')) {
        optionMenu = tmpMenu;
        break;
      }
    }
    if (optionMenu) {
      var cb = function (event) {
        $.trigger(optionMenu, 'tap');
      };
      $.trigger(optionMenu, 'optionmenu.change', {
        title: '',
        callback: cb,
        customOptionMenu: true
      });
    } else {
      $.trigger(document.body, 'optionmenu.change', {
        hideOptionMenu: true
      });
    }
  };
  // 触发标题修改事件
  $.ui.triggerTitleChange = function (element) {
    element = element || document.body;
    var title = $('header.mui-bar>.mui-title', element)[0];
    $.trigger(element, 'title.change', {
      title: title ? title.innerHTML : ''
    });
  };
  $.ui.panelBackAction = function (queue) {
    // 后续重新设计此处，将back放到各个空间内部实现
    // popover
    if ($.targets._popover && $.targets._popover.classList.contains('mui-active')) {
      $($.targets._popover).popover('hide');
      return true;
    }
    // offcanvas
    var panel = queue[queue.length - 1] || document.body;
    var offCanvas = panel.querySelector('.mui-off-canvas-wrap.mui-active');
    if (offCanvas) {
      $(offCanvas).offCanvas('close');
      return true;
    }
    var previewImage = $.isFunction($.getPreviewImage) && $.getPreviewImage();
    if (previewImage && previewImage.isShown()) {
      previewImage.close();
      return true;
    }
    // FileViewer
    var filepreview = $.FileViewer && $.FileViewer.dispose();
    if (filepreview === true) {
      return true;
    }
    // popup
    return $.closePopup();
  };
  $.ui.changeViewport = function (isScale) {
    console.log('isScale-----', isScale);
    var metaEl = document.querySelector('meta[name="viewport"]');
    var metaContent = isScale
      ? 'width=device-width, initial-scale=1.0, user-scalable=yes'
      : 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';
    metaEl.setAttribute('content', metaContent);

    var refresh = document.querySelector('.refresh');
    if (!refresh) {
      refresh = document.createElement('div');
      refresh.classList.add('refresh');
      refresh.style = 'color: transparent;';
      document.querySelector('body').appendChild(refresh);
    }
    var imesTamp = Date.now();
    refresh.innerHTML = isScale ? 'Can Scale' + imesTamp : 'Can not Scale' + imesTamp;
  };
  // 2.1 面板返回处理
  $.addBack({
    name: 'panel',
    index: 6,
    handle: function () {
      // 有遮罩的情况下,不操作
      var masks = $('div.mui-backdrop>.mui-mask');
      if (masks && masks.length > 0) {
        $.toast('正在加载中...');
        return true; //
      }
      var queue = $.ui.loadContentQueue;
      // 如果有回退操作，则返回
      if ($.ui.panelBackAction(queue) === true) {
        return true;
      }
      if (queue.length === 0) {
        $.trigger(document.body, 'history.change', window.history.length);
        return false;
      }
      if (queue.length === 1) {
        queue.pop();
        return false;
      }
      var topPanel = queue.pop();
      $.ui.popState(topPanel.id);
      var backAction = topPanel.getAttribute('data-back');
      if (backAction == 'hidden') {
        topPanel.classList.add('mui-hidden');
      } else if (topPanel.parentNode) {
        var triggerEvent = topPanel.getAttribute('triggerBackEvent');
        var triggerEventData = topPanel.getAttribute('triggerBackEventData') || '{}';
        if (triggerEvent != null) {
          $.trigger(topPanel, triggerEvent, commons.JSON.parse(triggerEventData));
        }
        topPanel.parentNode.removeChild(topPanel); // topPanel.remove();
        $.trigger(topPanel, 'panel.remove');
      }
      $.trigger(topPanel, 'panel.back');
      if (queue.length > 0) {
        var newTopPanel = queue[queue.length - 1];
        newTopPanel.classList.remove('mui-hidden');
        $.ui.triggerMenuChange(newTopPanel);
        $.ui.triggerTitleChange(newTopPanel);
        $.trigger(newTopPanel, 'panel.shown');
      }
      return true;
    }
  });
  // 2.2 加载面板
  $.ui.loadContent = function (target, replaceTop, goBack, transition) {
    var newPanel = $(target);
    var queue = $.ui.loadContentQueue;
    if (queue.length === 0) {
      // 查找panel
      var panels = document.body.querySelectorAll('.panel');
      if (panels.length !== 0) {
        for (var i = 0; i < panels.length - 1; i++) {
          var topPanel = panels[i];
          topPanel = $(topPanel).closest('.ui-wMobileTabs') || topPanel;
          topPanel.classList.add('mui-hidden');
          $.trigger(topPanel, 'panel.hidden');
          queue.push(topPanel);
        }
      } else {
        // 查找头部导航
        panels = document.body.querySelectorAll('.mui-bar');
        if (panels.length !== 0) {
          panels[0].parentNode.classList.add('mui-hidden');
          queue.push(panels[0].parentNode);
        } else if (document.body.firstChild) {
          // 查找文档第一个元素
          document.body.firstChild.classList.add('mui-hidden');
          queue.push(document.body.firstChild);
        }
      }
    } else {
      if (replaceTop === true) {
        var topPanel = queue.pop();
        topPanel = $(topPanel).closest('.ui-wMobileTabs') || topPanel;
        topPanel.classList.add('mui-hidden');
        $.trigger(topPanel, 'panel.hidden');
        topPanel.parentNode.removeChild(topPanel); // topPanel.remove();
        $.trigger(topPanel, 'panel.remove');
      } else {
        var topPanel = queue[queue.length - 1];
        topPanel = $(topPanel).closest('.ui-wMobileTabs') || topPanel;
        topPanel.classList.add('mui-hidden');
        $.trigger(topPanel, 'panel.hidden');
      }
    }
    var currentPanel = newPanel[0];
    currentPanel = $(currentPanel).closest('.ui-wMobileTabs') || currentPanel;
    currentPanel.classList.remove('mui-hidden');
    $.ui.triggerMenuChange(currentPanel);
    $.ui.triggerTitleChange(currentPanel);
    queue.push(currentPanel);
    $.ui.pushState(currentPanel.id);
    return queue;
  };

  // 2.2 获取当前面板
  $.ui.getCurrentPanel = function (target) {
    var queue = $.ui.loadContentQueue;
    return queue[queue.length - 1];
  };

  // 2.3 面板返回操作
  $.ui.goBack = window.goBack = $.back;
  // H5返回键关联(开发遵循原则：谁pushState，谁popState)
  (function enableH5State(enable) {
    if (false === enable) {
      $.ui.popState = $.noop;
      $.ui.pushState = $.noop;
      return; // 不关联H5状态
    }
    // history.back模拟出栈
    var triggerState = false,
      delay = 250;
    $.ui.popState = function popState(id) {
      var state = history.state;
      $.ui.changeViewport(false);
      if (false == triggerState && state && (null == id || id == state.id)) {
        console.log('pop state:', state);
        triggerState = true;
        history.back();
        setTimeout(function (t) {
          triggerState = false;
        }, delay);
        return state.id;
      }
    };
    $.ui.pushState = function pushState(id) {
      var state = {
          id: id || 'state-id-' + ++$.uuid
        },
        prevState = history.state;
      if (prevState && prevState.id) {
        state.prev = prevState.id;
        state.path = prevState.path + '/' + state.id;
      } else {
        state.path = state.id;
      }
      if (state.prev && state.prev === 'wf_work_view') {
        $.ui.changeViewport(true);
      }
      console.log('push state:', state);
      history.pushState(state, '');
      return state.id;
    };
    // 刷新时，清空栈
    (function clearState(t) {
      var state = history.state;
      if (null == state) {
        return;
      }
      $.ui.popState(state.id);
      setTimeout(clearState, delay);
    })();
    // 监听出栈事件，模拟mui放回
    window.addEventListener(
      'popstate',
      function (event) {
        if (false == triggerState) {
          console.log('current state:', event.state);
          triggerState = true;
          $.ui.changeViewport(false);
          $.ui.goBack();
          setTimeout(function (t) {
            triggerState = false;
          }, delay);
        }
      },
      false
    );
  })(!adapter.qing);

  // 2.4 设置面板标题
  $.ui.setTitle = function (title) {
    var queue = $.ui.loadContentQueue,
      headerTitle;
    if (queue.length > 0) {
      headerTitle = queue[queue.length - 1].querySelector('.mui-title>.mui-title-text');
      if (typeof headerTitle === 'undefined' || headerTitle == null) {
        headerTitle = queue[queue.length - 1].querySelector('.mui-title');
      }
    } else {
      headerTitle = document.body.querySelector('.mui-title>.mui-title-text');
      if (typeof headerTitle === 'undefined' || headerTitle == null) {
        headerTitle = document.body.querySelector('.mui-title');
      }
    }
    if (headerTitle != null) {
      headerTitle.innerHTML = title;
      $.trigger(headerTitle, 'title.change', {
        title: title
      });
    }
  };
  // 2.6 获取面板标题
  $.ui.getTitle = function () {
    var queue = $.ui.loadContentQueue,
      headerTitle;
    if (queue.length > 0) {
      headerTitle = queue[queue.length - 1].querySelector('.mui-title>.mui-title-text');
      if (typeof headerTitle === 'undefined' || headerTitle == null) {
        headerTitle = queue[queue.length - 1].querySelector('.mui-title');
      }
    } else {
      headerTitle = document.body.querySelector('.mui-title>.mui-title-text');
      if (typeof headerTitle === 'undefined' || headerTitle == null) {
        headerTitle = document.body.querySelector('.mui-title');
      }
    }
    return headerTitle ? headerTitle.innerHTML : '';
  };
  // 2.7显示面板右侧导航，并添加事件回调
  $.ui.showAndAddRightNavEventListener = function (type, callback, navMenu, customPanel) {
    var queue = $.ui.loadContentQueue;
    var panelMenu = null,
      currentPanel;
    if (queue.length > 0) {
      currentPanel = queue[queue.length - 1];
    } else {
      currentPanel = document.body;
    }
    // 自定义面板优先
    currentPanel = customPanel || currentPanel;
    panelMenu = currentPanel.querySelector('.mui-header-menu');
    if (panelMenu == null && navMenu) {
      var panelMenu = $.dom(navMenu)[0];
      panelMenu.classList.add('mui-pull-right');
      currentPanel.querySelector('header.mui-bar-nav').appendChild(panelMenu);
    }
    if (panelMenu != null) {
      panelMenu.classList.remove('mui-hidden');
      panelMenu.addEventListener(type, function () {
        callback.apply(this, arguments);
      });
      var cb = function (event) {
        $.trigger(panelMenu, type);
      };
      $.trigger(panelMenu, 'optionmenu.change', {
        title: '',
        callback: cb,
        customOptionMenu: true
      });
    }
  };
  // 2.8设置面板着导航提示
  $.ui.setLeftNavTitle = function (title) {
    var queue = $.ui.loadContentQueue,
      headerTitle;
    if (queue.length > 0) {
      headerTitle = queue[queue.length - 1].querySelector('.mui-pull-left>.mui-left-nav-text');
    } else {
      headerTitle = document.body.querySelector('.mui-pull-left.mui-left-nav-text');
    }
    if (headerTitle != null) {
      headerTitle.innerHTML = title;
      $.trigger(headerTitle, 'title.left.change', {
        title: title
      });
    }
  };
  // 2.9自适应图片
  $.ui.resizeImage = function (container) {
    function resizeImg(event) {
      var element = this;
      if (element.width > $.screen.availWidth) {
        element.style.width = '100%';
        element.setAttribute('width', '100%');
        element.style.height = 'auto';
        element.setAttribute('height', 'auto');
      }
      element.removeEventListener('load', resizeImg);
    }
    $('img', container).each(function (idx, element) {
      element.addEventListener('load', resizeImg);
    });
  };
  // 全屏打开iframe
  $.ui.openIframe = function (url) {
    if (typeof url !== 'string' || StringUtils.isBlank(url)) {
      return false;
    }
    var wrapperIframe = document.createElement('div');
    wrapperIframe.classList.add('filepreview');
    wrapperIframe.innerHTML =
      '<iframe class="filepreview-body" src="" marginheight="0px" marginwidth="0px" width="100%" height="100%">立达信网站预览 </iframe><div class="filepreview-close"><i class="icon mui-icon mui-icon-close pt-info"></i></div>';
    document.body.appendChild(wrapperIframe);
    var stateId = $.ui.pushState(); //
    $(wrapperIframe).on('tap', '.filepreview-close', function (event) {
      $.ui.popState(stateId); //
      document.body.removeChild(wrapperIframe);
    });
    appModal.showMask('数据加载中...', $('.filepreview'));
    $('.filepreview-body', wrapperIframe)[0].addEventListener(
      'load',
      function () {
        appModal.hideMask();
      },
      false
    );
    if (url.indexOf('http') === 0) {
    } else {
      url = window.location.origin + url;
    }
    $('.filepreview-body', wrapperIframe)[0].src = url;
    return url;
  };
  /*
  try {
    // 3、国际化资源文件
    i18n.properties({
      name: 'mobile', // 资源文件名称
      path: ctx + '/i18n/pt/', // 资源文件路径
      async: true
    });
  } catch (ex) {
    // alert(JSON.stringify(ex));
  }
  */
  // 4、渲染页面
  var wtype = WebApp.pageDefiniton.wtype;
  if (wtype != null) {
    var renderTo = WebApp.pageDefiniton.id || 'body';
    var pageSelector = renderTo === 'body' ? renderTo : '#' + renderTo;
    // 初始化页面
    $(pageSelector)[wtype]({
      widgetDefinition: WebApp.pageDefiniton,
      onPrepare: function () {
        appContext.setPageContainer(this);
      }
    });
  }
});

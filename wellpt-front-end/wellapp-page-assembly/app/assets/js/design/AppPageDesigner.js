define([
  'jquery',
  'jquery-ui',
  'bootstrap',
  'server',
  'commons',
  'appContext',
  'appWindowManager',
  'appModal',
  'app_component_config_dialog',
  'jsonview'
], function ($, ui, bootstrap, server, commons, appContext, appWindowManager, appModal, appComponentConfigDialog) {
  var UUID = commons.UUID;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  // 1、页面设计器
  var AppPageDesignerDefaults = {
    widgetTitleEditable: true, // 组件标题可变更
    widgetConfigable: true, // 组件可配置
    isJsonViewer: true, // 开启JSON查看器
    isWysiwyg: false, // 开启所见即所得
    onCreate: $.noop
    // 页面设计器创建完成回调
  };
  var AppPageDesigner = function (options) {
    var _self = this;
    // 设计器选项
    _self.options = $.extend(AppPageDesignerDefaults, options || {});
    // 组件计数器
    _self.componentCounter = 0;
    // 设计器的页面定义对象
    _self.appPageDefinition = {};
    // 设置容器管理器
    appContext.setWindowManager(appWindowManager(appContext));
    // 初始化
    _self.init();
  };
  // 1.1、初始化
  AppPageDesigner.prototype.init = function () {
    var _self = this;
    // 设置组件默认选项
    $('.designer-sidebar-nav li.list-group-item').each(function () {
      var type = $(this).attr('type');
      $(this).data('defaults', WebApp.widgetDefaults[type]);
    });
    // 左导航组件可拖动
    $('.designer-sidebar-nav ul').draggable({
      connectToSortable: '.ui-sortable',
      containment: 'document',
      handle: 'li',
      cursorAt: {
        top: 12,
        left: 120
      },
      revert: 'invalid',
      helper: function (event) {
        var $target = $(event.target);
        var $li = $target;
        if ($target.hasClass('badge')) {
          $li = $target.parent();
        }
        var $widget = $li.find('.widget').clone();
        $widget.data('defaults', $.extend(true, {}, $li.data('defaults')));
        $widget.width('30em');
        return $widget.show();
      }
    });
    // 2、加载页面窗口组件
    require([WebApp.containerJsModule], function (containerFunc) {
      var $container = $('.web-app-container');
      var pageUuid = _self.getPageUuid();
      _self.$pageContainer = $container;
      var container = null;
      if (StringUtils.isBlank(pageUuid)) {
        container = new containerFunc($container, WebApp.containerDefaults);
        container.setPageDesigner(_self);
        _self.pageContainer = container;
        container.create.call(container, $container, WebApp.containerDefaults);
      } else {
        container = load(containerFunc, $container, pageUuid);
      }
      // 创建回调事件
      if ($.isFunction(_self.options.onCreate)) {
        _self.options.onCreate.call(_self);
      }
      // 加载页面定义
      function load(containerFunc, $container, pageUuid) {
        var container = null;
        // server.JDS.call({
        //   service: _self.getLoadAppPageDefinitionService(),
        //   data: [pageUuid],
        //   async: false,
        //   success: function (result) {
        server.JDS.restfulGet({
          url: `/proxy/api/app/pagemanager/getPageDefinition/${pageUuid}`,
          async: false,
          success: function (result) {
            var bean = result.data;
            _self.appPageDefinition = bean;
            var definitionJson = bean.definitionJson;
            _self.initDefinitionJson = definitionJson;
            // console.log("html:" + definitionJson);
            if (definitionJson == null) {
              var defJson = WebApp.containerDefaults || {};
              defJson.uuid = bean.uuid;
              defJson.id = bean.id;
              defJson.title = bean.name;
              definitionJson = JSON.stringify(defJson);
            }
            container = new containerFunc($container[0], JSON.parse(definitionJson));
            container.setPageDesigner(_self);
            _self.pageContainer = container;
            $container.prop('id', container.getId());
            container.create.call(container);
          }
        });
        return container;
      }
    });
  };

  // 1.2、初始化可排序默认配置项
  AppPageDesigner.prototype.getDefaultSortableOptions = function () {
    return {
      connectWith: '.ui-sortable',
      containment: 'document',
      handle: '.widget-handle',
      cancel: '.btn-delete,.widget-title',
      placeholder: 'widget-placeholder ui-corner-all'
    };
  };
  // 1.3、判断排序项是否来源于组件列表
  AppPageDesigner.prototype.isSortableItemFromOutside = function (ui) {
    return ui && ui.sender && ui.sender.hasClass('list-group');
  };
  // 1.4、初始化根据组件JSON定义创建可放置的对象
  AppPageDesigner.prototype.createDraggableByDefinitionJson = function (definitionJson) {
    var wtype = definitionJson.wtype;
    var $item = $('.list-group-item[type=' + wtype + ']');
    if ($item.length === 0) {
      alert('找不到类型为[' + wtype + ']的组件定义信息!');
    }
    var $widget = $item.find('.widget').clone();
    $widget.data('defaults', $.extend(true, {}, $item.data('defaults')));
    return $widget;
  };
  // 1.5、容器元素转为可放入子元素的通用处理
  AppPageDesigner.prototype.sortable = function (container, $container, $placeHolder) {
    var _self = container;
    // 组件配置不可放入子组件，直接返回
    var widgetDefaults = $(container.element).data('defaults');
    if (widgetDefaults && widgetDefaults.sortable === false) {
      return;
    }
    var defaultSortableOptions = _self.pageDesigner.getDefaultSortableOptions();
    $container.sortable(
      $.extend(defaultSortableOptions, {
        receive: function (event, ui) {
          if (ui.helper != null) {
            _self.pageDesigner.drop(_self, $placeHolder, $(ui.helper));
          } else {
            _self.pageDesigner.drop(_self, $placeHolder, $(ui.item));
          }
          _self.updateChildren($container.children('.widget'));
        },
        remove: function (event, ui) {
          _self.updateChildren($container.children('.widget'));
        },
        update: function (event, ui) {
          _self.updateChildren($container.children('.widget'));
        }
      })
    );
  };
  // 1.6.1、初始化元素放入容器的通用处理
  AppPageDesigner.prototype.drop = function (container, $placeHolder, $draggable, options) {
    var _self = this;
    var $widget = $draggable;
    $widget.removeAttr('style');
    // 确保放置的是widget
    if (!$widget.hasClass('widget')) {
      $widget = $widget.children('.widget');
      $draggable.after($widget);
      $draggable.remove();
    }

    // widget不存在添加
    if ($placeHolder.has($widget).length === 0) {
      $placeHolder.append($widget);
    }

    // 容器内拖动已存在组件对象，不需要再初始化等
    var component = $widget.data('component');
    if (component != null) {
      component.refresh();
      return;
    }

    // 组件添加进容器
    _self.addToContainer(container, $placeHolder, $widget, options);
  };
  // 1.6.2、初始化元素放入容器的通用处理
  AppPageDesigner.prototype.addToContainer = function (container, $placeHolder, $widget, options) {
    var _self = this;
    // 初始化选项
    var defaults = $.extend(true, {}, $widget.data('defaults'));
    var initOptions = options;
    if (initOptions == null) {
      initOptions = defaults;
    }

    // 自动生成与同步组件默认标题
    var $widgetTitle = $widget.find('.widget-title');
    this.componentCounter++;
    var counter = this.componentCounter;
    if (initOptions.title == null) {
      var fmtCounter = counter;
      if (counter < 10) {
        fmtCounter = '0' + fmtCounter;
      }
      initOptions.title = fmtCounter + '_' + $widgetTitle.text();
    }
    if (StringUtils.isNotBlank(initOptions.title)) {
      $widgetTitle.text(initOptions.title);
      $widgetTitle.attr('title', initOptions.title);
    }
    if (StringUtils.isNotBlank(initOptions.refWidgetDefTitle)) {
      var refTitle = '(引用——' + initOptions.refWidgetDefTitle + ')';
      $widget.find('.ref-widget-title').text(refTitle);
    }
    // 组件标题可更改
    if (_self.options.widgetTitleEditable) {
      var $widgetHandle = $widget.find('.widget-handle');
      $widgetHandle.on('click', function (e) {
        var $target = $(e.target);
        if ($target.hasClass('widget-title')) {
          var $widgetTitle = $target;
          var $titleInput = $('input', $widgetTitle);
          if ($titleInput.length === 0) {
            var title = $widgetTitle.text();
            $widgetTitle.html("<input type='text' />");
            $titleInput = $('input', $widgetTitle);
            $titleInput.val(title);
            $titleInput.focus();
          }
          // 失去焦点处理
          $titleInput.off('blur');
          $titleInput.on('blur', function (e) {
            var newTitle = $(this).val();
            if (StringUtils.isBlank(newTitle)) {
              alert('组件标题不能为空!');
              return true;
            }
            if (newTitle.length > 255) {
              alert('组件标题不能大于255个字符!');
              return true;
            }
            var $widgetTitle = $(this).parent();
            var $widget = $widgetTitle.closest('.widget');
            $widgetTitle.html(newTitle);
            $widgetTitle.attr('title', newTitle);
            var widgetComponent = $widget.data('component');
            widgetComponent.setOption('title', newTitle);
          });
          // 回车处理
          $titleInput.off('keypress');
          $titleInput.on('keypress', function (e) {
            if (e.keyCode === 13) {
              $(this).trigger('blur');
            }
          });
        } else if ($target.is('input')) {
          // 编辑状态忽略掉
        } else {
          $('.widget-header>.widget-handle>.widget-title input').trigger('blur');
        }
        e.stopPropagation();
      });
    }

    // 加载配置的JS模块
    var jsModule = $widget.attr('jsModule');
    if (StringUtils.isBlank(jsModule)) {
      console.log('加载配置的JS模块为空.');
      return;
    }

    var wcomponent = require(jsModule);
    var component = new wcomponent($widget[0], initOptions);
    if (!initOptions.id && component.defaultConfiguration) {
      // 支持定义组件的默认选项
      initOptions.configuration = $.extend(
        {},
        initOptions.configuration,
        $.isFunction(component.defaultConfiguration) ? component.defaultConfiguration() : component.defaultConfiguration
      );
    }
    component.setPageDesigner(container.getPageDesigner());

    // 存储对象
    $widget.data('component', component);
    if (component.options.id) {
      $widget.attr('id', component.options.id);
      _self[component.options.id] = component;
    } else {
      // 预生成id
      component.options.id = component.options.wtype + '_' + commons.UUID.createUUID();
      $widget.attr('id', component.options.id);
    }
    // 添加到容器
    container.addChild(component);

    // 绑定JSON查看器事件
    if (_self.supportsJsonViewer()) {
      var $jsonViewerBtns = $widget.find('a.btn-json-viewer');
      $jsonViewerBtns.removeClass('hide');
      $jsonViewerBtns.on(
        'click',
        $.proxy(function (e) {
          _self.showJsonViewer(this);
        }, component)
      );
    }
    // 绑定编辑事件
    var $editBtns = $widget.find('a.btn-edit');
    $editBtns.on(
      'click',
      $.proxy(function (e) {
        _self.configure.call(_self, this);
      }, component)
    );
    // 组件不可配置，隐藏配置按钮
    if (!_self.options.widgetConfigable || defaults.confiable === false) {
      $editBtns.hide();
      $editBtns.off('click');
    }
    // 绑定删除事件
    var $delBtns = $widget.find('a.btn-delete');
    $delBtns.on(
      'click',
      $.proxy(function (e) {
        var _component = this;
        // 判断组件是否被引用
        _checkWidgetRef(this, function () {
          this.destroy.call(this);
        });
      }, component)
    );
    // 组件不可拖动，隐藏头部
    if (defaults.draggable === false) {
      $widget.children('.widget-header').hide();
    }

    // 初始化
    component.create.call(component, $widget, initOptions);

    // 组件预览
    _self.preview.call(_self, component);
  };

  // 判断组件是否被引用
  function _checkWidgetRef(component, callback) {
    server.JDS.call({
      service: component.pageDesigner.getCheckWidgetReferencedService(),
      data: [component.getId()],
      success: function (result) {
        if (result.data !== true) {
          callback.call(component);
        } else {
          alert('组件被引用，不能删除!');
        }
      }
    });
  }

  // 1.7、页面设计器组件配置
  AppPageDesigner.prototype.configure = function (component) {
    var _self = this;
    _self.configureComponent = component; // 当前配置中的组件
    if (!component.usePropertyConfigurer()) {
      component.configure.call(component);
      return;
    }
    new appComponentConfigDialog(this, component);
  };
  // 1.8、页面设计器组件预览
  AppPageDesigner.prototype.preview = function (component, callback) {
    var _self = this;
    if (!_self.supportsWysiwyg()) {
      return;
    }
    var $widget = $(component.element);
    try {
      var container = component.getParent();
      var isContainer = $widget.has('.ui-sortable').length > 0;
      if (!isContainer) {
        $(container.element).prop('id', container.getId());
        $widget.find('.widget-body').html(component.toHtml());
        appContext.setPageContainer(_self.pageContainer);
        appContext.createWidget(component.getOptions(), container.getOptions(), callback);
      } else {
        $widget.prop('id', component.getId());
      }
      // 添加预览样式
      $widget.addClass(component.getOptions().wtype + '-preview');
      if ($.isFunction(component.onPreivew)) {
        component.onPreivew.call(component, container);
      }
    } catch (e) {
      console.log('error preview: ' + $widget.html());
      console.log(component);
    }
  };
  // 1.9.1、判断页面设计器是否开启JSON查看器
  AppPageDesigner.prototype.supportsJsonViewer = function () {
    var _self = this;
    if (_self.options.isJsonViewer === false) {
      return _self.options.isJsonViewer;
    }
    var storage = commons.StorageUtils.getStorage(3);
    var isJsonViewer = storage.getItem('app_designer_json_viewer');
    return isJsonViewer === 'true';
  };
  // 1.9.2、判断页面设计器是否开启所见即所得
  AppPageDesigner.prototype.supportsWysiwyg = function () {
    var _self = this;
    if (_self.options.isWysiwyg === true) {
      return _self.options.isWysiwyg;
    }
    var storage = commons.StorageUtils.getStorage(3);
    var isWysiwyg = storage.getItem('app_designer_wysiwyg');
    return isWysiwyg === 'true';
  };
  // 1.10、获取应用的页面定义UUID
  AppPageDesigner.prototype.getPageUuid = function () {
    return $('input[id=page_uuid]').val();
  };
  // 1.11、获取集成信息UUID
  AppPageDesigner.prototype.getPiUuid = function () {
    return $('input[id=pi_uuid]').val();
  };
  // 1.12、获取组件默认配置属性
  AppPageDesigner.prototype.getWidgetDefaultOptions = function (wtype) {
    return $('.designer-sidebar-nav')
      .find("li.list-group-item[type='" + wtype + "']")
      .data('defaults');
  };
  // 1.13、判断页面设计器的组件定义是否已变更
  AppPageDesigner.prototype.isWidgetDefinitionChanged = function () {
    var pageDesigner = this;
    // 刚保存成功，返回false
    if (pageDesigner.saveSuccess == true) {
      return false;
    }
    var pageContainer = pageDesigner.pageContainer;
    var $pageContainer = pageDesigner.$pageContainer;
    // 页面定义JSON
    var initDefinitionJson = JSON.parse(pageDesigner.initDefinitionJson);
    var definitionJson = pageContainer.getDefinitionJson.call(pageContainer, $pageContainer);
    // toHtml可能变更组件定义信息
    pageContainer.toHtml.call(pageContainer, $pageContainer);

    var initDefinitionHtml = initDefinitionJson.html;
    var definitionHtml = definitionJson.html;
    initDefinitionJson.html = null;
    definitionJson.html = null;
    // 比较配置信息
    var getConfigurations = function (definitionJson, configurations) {
      if (definitionJson.configuration) {
        configurations.push(definitionJson.configuration);
      }
      if ($.isArray(definitionJson.items)) {
        $.each(definitionJson.items, function (i, item) {
          getConfigurations(item, configurations);
        });
      }
    };
    var initConfigurations = [];
    var definitionConfigurations = [];
    getConfigurations(initDefinitionJson, initConfigurations);
    getConfigurations(definitionJson, definitionConfigurations);
    var isChanged = JSON.stringify(initConfigurations) !== JSON.stringify(definitionConfigurations);
    initDefinitionJson.html = initDefinitionHtml;
    definitionJson.html = definitionHtml;

    return isChanged;
  };
  // 1.13、显示组件JSON查看器
  AppPageDesigner.prototype.showJsonViewer = function (component) {
    var jsonViewerContent = $('#jsonview_template').clone().html();
    var definitionJson = component.getDefinitionJson();
    var $dialog = appModal.dialog({
      title: 'JSON查看器——' + definitionJson.title,
      message: jsonViewerContent,
      backdrop: null,
      size: 'large',
      shown: function (_$dialog) {
        var $templateHtml = _$dialog.find('.row-jsonview');
        var $jsonview = $('.jsonview', $templateHtml);
        var definitionJsonString = JSON.stringify(definitionJson);
        // JSONView对undefined无法解析问题
        definitionJsonString = definitionJsonString.replace('undefined', 'null');
        $jsonview.JSONView(JSON.parse(definitionJsonString), {
          collapsed: true
        });
        $('.btn-collapse', $templateHtml).on('click', function () {
          $jsonview.JSONView('collapse');
        });
        $('.btn-expand', $templateHtml).on('click', function () {
          $jsonview.JSONView('expand');
        });
        $('.btn-toggle', $templateHtml).on('click', function () {
          $jsonview.JSONView('toggle');
        });
        $('.btn-toggle-level1', $templateHtml).on('click', function () {
          $jsonview.JSONView('toggle', 1);
        });
        $('.btn-toggle-level2', $templateHtml).on('click', function () {
          $jsonview.JSONView('toggle', 2);
        });
        $('.btn-toggle-level3', $templateHtml).on('click', function () {
          $jsonview.JSONView('toggle', 3);
        });
      }
    });
  };

  // 1.14、获取页面容器
  AppPageDesigner.prototype.getPageContainer = function () {
    return this.pageContainer;
  };
  // 1.15、获取加载页面定义服务
  AppPageDesigner.prototype.getLoadAppPageDefinitionService = function () {
    // return "appPageDefinitionMgr.getBean";
    return '/proxy/api/app/pagemanager/getPageDefinition';
  };
  // 1.16、获取检查组件被引用服务
  AppPageDesigner.prototype.getCheckWidgetReferencedService = function () {
    return 'appWidgetDefinitionMgr.isWidgetReferencedById';
  };

  return AppPageDesigner;
});

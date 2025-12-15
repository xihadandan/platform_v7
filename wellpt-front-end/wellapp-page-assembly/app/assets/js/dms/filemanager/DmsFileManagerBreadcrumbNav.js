define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  // 数据管理_文件库_面包屑导航
  var DmsFileManagerBreadcrumbNav = function (element, options) {
    this.options = options;
    this.fileManagerWidget = options.widget;
    this.dataViewManager = options.dataViewManager;
    this.rootNav = options.rootNav;
    // 导航数据共享
    if (this.fileManagerWidget.breadcrumbDataProvider == null) {
      this.fileManagerWidget.breadcrumbDataProvider = [options.rootNav];
    }
    this.$container = $(options.container);
    this.$element = $(element);
    this.init();
  };
  $.extend(DmsFileManagerBreadcrumbNav.prototype, {
    init: function () {
      var _self = this;
      var sb = new StringBuilder();
      sb.append('<ol class="breadcrumb">');
      // sb.append('<li><a href="#">Home</a></li>');
      // sb.append('<li><a href="#">2013</a></li>');
      // sb.append('<li class="active">十一月</li>');
      sb.append('</ol>');
      _self.$element.append(sb.toString());
      _self.$breadcrumb = _self.$element.find('.breadcrumb');

      // 渲染导航
      _self.renderNavs();

      // 绑定事件
      _self.bindEvents();
    },
    refresh: function () {
      this.renderNavs();
    },
    // 渲染导航
    renderNavs: function () {
      var _self = this;
      var sb = new StringBuilder();
      var dataProvider = _self.getDataProvider();
      var length = dataProvider.length;
      if (length > 1) {
        sb.appendFormat('<li class="flie-breadcrumbnav-li"><a href="#" class="go-back">{0}</a></li>', '返回上一级');
      }
      $.each(dataProvider, function (i, nav) {
        if (i == length - 1) {
          sb.appendFormat('<li class="active">{0}</li>', nav.name);
        } else {
          sb.appendFormat('<li><a href="#" navId={0}>{1}</a></li>', nav.uuid, nav.name);
        }
      });
      _self.$breadcrumb.html(sb.toString());
    },
    bindEvents: function () {
      var _self = this;
      // 夹打开事件
      $(_self.$container).on('wFileManager.OpenFolder', function (e, ui) {
        var currentFolderUuid = e.detail.currentFolderUuid;
        var currentFolderData = e.detail.currentFolderData;
        _self.pushNav(currentFolderData);
        _self.renderNavs();
      });

      // 返回上一级
      $(_self.$breadcrumb).on('click', 'a', function () {
        // 返回上一级
        var dataArray = _self.getDataProvider();
        if ($(this).hasClass('go-back')) {
          if (dataArray.length > 1) {
            dataArray.pop();
            var nav = dataArray[dataArray.length - 1];
            _self.fileManagerWidget.setCurrentFolderUuid(nav.uuid);
            _self.dataViewManager.refresh(true);
            _self.renderNavs();
          }
        } else {
          // 跳到指定夹
          var navId = $(this).attr('navId');
          var rerender = false;
          var newArray = [];
          for (var i = 0; i < dataArray.length; i++) {
            var nav = dataArray[i];
            newArray.push(nav);
            if (nav.uuid == navId) {
              rerender = true;
              _self.fileManagerWidget.setCurrentFolderUuid(nav.uuid);
              _self.dataViewManager.refresh(true);
              break;
            }
          }
          // 重新渲染面包屑导航
          if (rerender) {
            _self.setDataProvider(newArray);
            _self.renderNavs();
          }
        }
      });
    },
    pushNav: function (nav) {
      var _self = this;
      var dataProvider = _self.getDataProvider();
      // 导航已存在，直接返回
      for (var i = 0; i < dataProvider.length; i++) {
        if (dataProvider[i].uuid == nav.uuid) {
          console.error('The breadcrumb nav of uuid is ' + nav.uuid + ' is exists');
          return;
        }
      }
      if (dataProvider.length == 0) {
        dataProvider.push(_self.rootNav);
      }
      dataProvider.push(nav);
    },
    getDataProvider: function () {
      return this.fileManagerWidget.breadcrumbDataProvider;
    },
    setDataProvider: function (data) {
      this.fileManagerWidget.breadcrumbDataProvider = data;
    }
  });
  return DmsFileManagerBreadcrumbNav;
});

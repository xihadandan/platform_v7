define([
  'jquery',
  'server',
  'commons',
  'constant',
  'appContext',
  'dataStoreBase',
  'formBuilder',
  'DmsFileServices',
  'DmsFileManagerBreadcrumbNav',
  'DmsFileOnlineViewer'
], function (
  $,
  server,
  commons,
  constant,
  appContext,
  DataStore,
  formBuilder,
  DmsFileServices,
  DmsFileManagerBreadcrumbNav,
  DmsFileOnlineViewer
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  // 文件数据提供者
  var FileDataProvider = function (options) {
    var _self = this;
    _self.init(options.data, options.key);

    // 数据源代理
    _self.proxy = new DataStore({
      dataStoreId: 'CD_DS_DMS_FILE_MANAGER',
      onDataChange: function (data, count, params) {
        _self.init(data, _self.idKey);

        options.receiver.onDataChange(data, count, params);
      },
      renderers: [],
      defaultOrders: options.receiver.getDefaultSort(),
      defaultCriterions: [],
      receiver: options.receiver,
      pageSize: options.pageSize,
      autoCount: false
    });
  };
  $.extend(FileDataProvider.prototype, {
    init: function (data, key) {
      var _self = this;
      _self.dataArray = data || [];
      _self.isShowPop = false;
      _self.timer = null;
      _self.dataMap = {};
      _self.idKey = key || 'uuid';
      $.each(_self.dataArray, function (i) {
        _self.dataMap[this[_self.idKey]] = this;
      });
    },
    get: function (id) {
      return this.dataMap[id];
    },
    size: function () {
      return this.dataArray.length;
    },
    isEmpty: function () {
      return this.size() == 0;
    },
    add: function (data) {
      var _self = this;
      _self.dataArray.push(data);
      _self.dataMap[data[_self.idKey]] = data;
    },
    update: function (data) {
      var _self = this;
      var newArray = [];
      $.each(_self.dataArray, function () {
        if (data[_self.idKey] == this[_self.idKey]) {
          newArray.push(data);
        } else {
          newArray.push(this);
        }
      });
      _self.dataArray = newArray;
      _self.dataMap[data[_self.idKey]] = data;
    },
    remove: function (id) {
      var _self = this;
      var newArray = [];
      $.each(_self.dataArray, function () {
        if (id != this[_self.idKey]) {
          newArray.push(this);
        }
      });
      _self.dataArray = newArray;
      delete _self.dataMap[id];
    },
    each: function (callback) {
      $.each(this.dataArray, function () {
        if (callback) {
          callback.apply(this, arguments);
        }
      });
    },
    getData: function () {
      return this.dataArray;
    },
    removeParam: function (key) {
      this.proxy.removeParam.apply(this.proxy, arguments);
    },
    addParam: function (key, value) {
      this.proxy.addParam.apply(this.proxy, arguments);
    },
    load: function () {
      this.proxy.load.apply(this.proxy, arguments);
    }
  });

  // 文件管理缩略视图
  var DmsFileManagerThumbnailView = function (options) {
    var _self = this;
    _self.dataViewManager = options.dataViewManager;
    _self.options = options;
    // 当前夹UUID
    _self.fileManagerWidget = options.widget;
    _self.dmsFileServices = options.widget.dmsFileServices;
    _self.$container = $(options.widget.$view);
    _self.navSelectedItemInToolbarActions = [];
    var dataOptions = {
      data: options.data,
      key: 'uuid',
      receiver: _self,
      pageSize: _self.fileManagerWidget.getPageSize() || 65536
    };
    _self.dataProvider = new FileDataProvider(dataOptions);
    _self.init();
    _self.updateOptions(_self.options);
    if (_self.options.callback) {
      _self.options.callback.call(options.widget, this);
    }
  };
  $.extend(DmsFileManagerThumbnailView.prototype, {
    // 初始化视图内容
    init: function () {
      var _self = this;
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var sb = new StringBuilder();
      sb.append('<div class="file-manager-thumbnail-view">');
      // 工具栏
      sb.append('<div class="clearfix file-manager-toolbar">');
      // 查询重置按钮
      sb.append('<div class="columns columns-right btn-group pull-right">');
      sb.append('	<button class="well-btn w-btn-primary btn-query" type="button" name="query" title="查询">');
      sb.append('		<i class="iconfont icon-ptkj-sousuochaxun"></i>查询');
      sb.append('	</button>');
      sb.append('	<button class="well-btn w-btn-primary w-line-btn btn-reset" type="button" name="reset" title="重置">');
      sb.append('		<i class="iconfont icon-ptkj-shanchu"></i>重置');
      sb.append('	</button>');
      // 是否支持展示查询字段
      if (_self.isSupportShowQueryField()) {
        sb.append(' <button class="well-btn w-btn-primary w-line-btn " type="button" name="showQueryField" title="展开字段查询">');
        sb.append(' <i class="glyphicon glyphicon-chevron-down" >');
        sb.append(' </i>');
        sb.append(' </button>');
      }
      if (_self.fileManagerWidget.isSupportSwitchDataView()) {
        var fmBtn = '<button class="well-btn w-btn-primary w-line-btn btn-thumbnail-view" type="button" title="缩略视图">';
        sb.append(fmBtn);
        sb.append('		<i class="glyphicon glyphicon-th-large"></i>');
        sb.append('	</button>');
      }
      sb.append('</div>');

      // 查询按钮
      sb.append('<div class="pull-right search">');
      sb.append('<input class="form-control" name="keyword" type="search" placeholder="搜索"></div>');

      // 左边操作按钮
      sb.append('<div class="fm-bs-bars bs-bars pull-left">');
      sb.append('</div>');
      sb.append('</div>');

      // 面包屑导航
      if (_self.fileManagerWidget.isShowBreadcrumbNav()) {
        sb.append('<div class="file-manager-breadcrumb">');
        sb.append('</div>');
      }

      // 数据内容
      sb.append('<div class="row file-manager-container">');
      var checkboxId = UUID.createUUID();
      sb.append('<form class="form-inline form-group">');
      sb.append('<div class="fm-checkbox checkbox">');
      sb.appendFormat('<input id="{0}" type="checkbox"> <label for="{0}">全选</label>', checkboxId);
      sb.append('</div>');
      sb.append('</form>');
      sb.append('<div class="file-boxes clearfix">');
      sb.append('</div>');
      sb.append('</div>');
      sb.append('</div>');
      _self.$container.append(sb.toString());
      _self.element = $(_self.$container).find('.file-manager-thumbnail-view');
      _self.$toolbar = $(_self.element).find('.file-manager-toolbar');
      _self.$element = $(_self.element);
      // 是否展示面包屑导航
      if (_self.fileManagerWidget.isShowBreadcrumbNav()) {
        var breadcrumbOptions = {
          container: _self.$element,
          dataViewManager: _self.dataViewManager,
          rootNav: _self.fileManagerWidget.getRootFolder(),
          widget: _self.fileManagerWidget
        };
        _self.breadcrumbNav = new DmsFileManagerBreadcrumbNav(_self.element.find('.file-manager-breadcrumb'), breadcrumbOptions);
      }

      _self._bindEvents();
    },
    updateOptions: function (newOptions) {
      var _self = this;
      var keyword = newOptions.keyword || '';
      _self.setKeyword(keyword);
    },
    show: function () {
      this.element.show();
    },
    isShowQueryFields: function () {
      return false;
    },
    hide: function () {
      this.element.hide();
    },
    getData: function () {
      return this.dataProvider.getData();
    },
    // 添加参数
    addParam: function (key, value) {
      this.dataProvider.addParam(key, value);
    },
    // 删除参数
    removeParam: function (key) {
      this.dataProvider.removeParam(key);
    },
    // 添加额外的查询排序
    addOtherOrder: function (sortName, sortOrder) {},
    // 清除额外查询排序,sortName为空是清楚全部，否则清楚等于sortName的一条额外查询排序
    clearOtherOrders: function (sortName) {},
    refresh: function (force) {
      var _self = this;
      // 调用JS二开模块的回调接口
      _self.fileManagerWidget.invokeDevelopmentMethod('beforeLoadData', []);
      // 触发文件管理加载数据前事件
      appContext.dispatchEvent('wFileManager.beforeLoadData', null, _self.fileManagerWidget);
      // 加载数据
      _self.dataProvider.removeParam('listFileMode');
      _self.dataProvider.addParam('listFileMode', _self.fileManagerWidget.getListFileMode());
      _self.dataProvider.removeParam('folderUuid');
      _self.dataProvider.addParam('folderUuid', _self.fileManagerWidget.getCurrentFolderUuid());
      var criterions = _self.collectCriterion();
      var keyword = _self.collectKeyword();
      _self.dataProvider.load(
        {
          pagination: {
            currentPage: 1
          },
          keyword: keyword,
          criterions: criterions
        },
        {}
      );

      // 加载导航
      // 显示导航夹操作按钮
      if (_self.fileManagerWidget.isShowNavFolderActions()) {
        _self.getNavFolderActions(_self.fileManagerWidget.getCurrentFolderUuid());
      }

      // 刷新面包屑导航
      if (_self.breadcrumbNav) {
        _self.breadcrumbNav.refresh();
      }

      // 重置全选/不选信息
      _self.resetFileCheckboxInfo();
    },
    destroy: function () {
      this.element.remove();
    },
    // 是否支持展示查询字段
    isSupportShowQueryField: function () {
      var _self = this;
      var listViewConfiguration = _self.getListViewConfiguration();
      var listViewQueryInfo = listViewConfiguration.query || {};
      if (listViewQueryInfo.fieldSearch == true) {
        return true;
      }
      return false;
    },
    getListViewConfiguration: function () {
      var _self = this;
      var listViewConfiguration = {};
      var viewWidgetDefinition = _self.fileManagerWidget.listViewWidgetDefinition;
      if (viewWidgetDefinition) {
        listViewConfiguration = viewWidgetDefinition.configuration || {};
      }
      return listViewConfiguration;
    },
    // 生成字段查询
    _renderFieldSearch: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getListViewConfiguration();
      if (configuration.query.fields) {
        $element.find('.div_search_toolbar').remove();
        var $fieldSearchElement = $("<div class='div_search_toolbar' style='padding-top:10px;'></div>");
        $element.prepend($fieldSearchElement);
        var fieldOptios = {
          container: $fieldSearchElement,
          inputClass: 'w-search-option',
          labelColSpan: '3',
          controlColSpan: '9',
          multiLine: $element.width() > 400,
          contentItems: []
        };
        $.each(configuration.query.fields, function (i, field) {
          var queryOption = field.queryOptions;
          var queryType = queryOption.queryType;
          var itemOptions = {
            label: field.label,
            name: field.name,
            type: queryType,
            value: field.defaultValue
          };
          if (queryType == 'date') {
            if (field.operator == 'between') {
              itemOptions.beginName = field.name + '_Begin';
              itemOptions.endName = field.name + '_End';
              itemOptions.beginValue = field.defaultValue;
              itemOptions.endValue = field.defaultValue;
              itemOptions.type = 'timeInterval';
            }
            itemOptions.timePicker = {
              format: queryOption.format
            };
          } else if (queryType == 'select2') {
            itemOptions.select2 = _self.getSelect2OptionValue(queryOption);
            // 是否多选
            itemOptions.select2.multiple = queryOption.multiple === '1';
            // 常量默认值
            if (queryOption.optionType == '1') {
              $.each(itemOptions.select2.data, function () {
                if (StringUtils.isBlank(this.id)) {
                  var emptyId = UUID.createUUID();
                  this.id = emptyId;
                  itemOptions.value = emptyId;
                  // 缓存空值
                  _self.fieldSearchEmptyValueMap = _self.fieldSearchEmptyValueMap || {};
                  _self.fieldSearchEmptyValueMap[this.id] = this.id;
                } else {
                  if (_self.fieldSearchEmptyValueMap && _self.fieldSearchEmptyValueMap[this.id]) {
                    itemOptions.value = this.id;
                  }
                }
              });
            } // 数据字典默认值
            else if (queryOption.optionType == '2') {
              if (StringUtils.isNotBlank(queryOption.defaultBlankId)) {
                itemOptions.select2.defaultBlank = false;
                var emptyId = UUID.createUUID();
                var defaultOption = [
                  {
                    id: emptyId,
                    text: queryOption.defaultBlankText
                  }
                ];
                itemOptions.select2.data = defaultOption.concat(itemOptions.select2.data);
                itemOptions.value = emptyId;
                // 缓存空值
                _self.fieldSearchEmptyValueMap = _self.fieldSearchEmptyValueMap || {};
                _self.fieldSearchEmptyValueMap[emptyId] = emptyId;
              }
            }
          } else if (queryType == 'radio' || queryType == 'select' || queryType == 'checkbox') {
            itemOptions.items = _self._getFieldOptionValue(queryOption);
          }
          fieldOptios.contentItems.push(itemOptions);
        });
        formBuilder.buildContent(fieldOptios);
        return $fieldSearchElement;
      }
      return '';
    },
    collectCriterion: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getListViewConfiguration();
      var criterions = [];
      // 关键字查询
      if (_self.showQueryField != true) {
        var keyword = _self.collectKeyword();
        if (StringUtils.isNotBlank(keyword)) {
          var orcriterion = {
            conditions: [],
            type: 'or'
          };
          // 视图的字段定义
          if (configuration.columns) {
            $.each(configuration.columns, function (index, column) {
              if (column.keywordQuery != '0') {
                orcriterion.conditions.push({
                  columnIndex: column.name,
                  value: keyword,
                  type: 'like'
                });
              }
            });
          } else {
            // 文件库缩略视图支持的字段查询
            var fileProperties = [
              {
                columnIndex: 'name'
              },
              {
                columnIndex: 'contentType'
              },
              {
                columnIndex: 'size'
              }
            ];
            $.each(fileProperties, function (index, property) {
              orcriterion.conditions.push({
                columnIndex: property.columnIndex,
                value: keyword,
                type: 'like'
              });
            });
          }

          criterions.push(orcriterion);
        }
      } else {
        // 字段查询
        criterions = $.map(configuration.query.fields, function (field) {
          var queryType = field.queryOptions.queryType;
          if (field.operator != 'between') {
            var value = $('#' + field.name, $element).val();
            // select2数据字典的默认空值映射
            if (_self.fieldSearchEmptyValueMap && _self.fieldSearchEmptyValueMap[value]) {
              value = '';
            }
            // 单选框的值
            if (queryType == 'radio') {
              var $radio = $('input[name=' + field.name + ']:checked', $element);
              value = $radio.val();
            }
            // 复选框的值
            if (queryType == 'checkbox') {
              var $checkbox = $('input[name=' + field.name + ']:checked', $element);
              var checkboxValues = [];
              $.each($checkbox, function () {
                checkboxValues.push($(this).val());
              });
              value = checkboxValues.join(';');
            }
            if (StringUtils.isNotBlank(value)) {
              var criterion = {};
              criterion.columnIndex = field.name;
              criterion.value = value;
              criterion.type = field.operator;
              var queryOption = field.queryOptions;
              if (queryType == 'date' && _self._getFieldDataType(criterion.columnIndex) == 'Date') {
                criterion.value = moment(criterion.value, field.queryOptions.format).format('YYYY-MM-DD HH:mm:ss');
              }
              return criterion;
            }
          } else {
            var value = $('#' + field.name + '_Begin', $element).val();
            var value2 = $('#' + field.name + '_End', $element).val();
            if (StringUtils.isNotBlank(value) || StringUtils.isNotBlank(value2)) {
              var criterion = {};
              criterion.columnIndex = field.name;
              criterion.value = [value, value2];
              criterion.type = field.operator;
              var queryOption = field.queryOptions;
              if (queryType == 'date') {
                if (_self._getFieldDataType(criterion.columnIndex) == 'Date') {
                  if (StringUtils.isNotBlank(criterion.value[0])) {
                    // criterion.value[0] =
                    // moment(criterion.value[0],
                    // field.queryOptions.format).format('YYYY-MM-DD
                    // HH:mm:ss');
                  }
                  if (StringUtils.isNotBlank(criterion.value[1])) {
                    // criterion.value[1] =
                    // moment(criterion.value[1],
                    // field.queryOptions.format).format('YYYY-MM-DD
                    // HH:mm:ss');
                  }
                }
              }
              return criterion;
            }
          }
          return null;
        });
      }
      return criterions;
    },
    getDefaultSort: function () {
      var _self = this;
      var defaultSort = [];
      var configuration = _self.getListViewConfiguration();
      if (configuration.defaultSorts) {
        defaultSort = $.map(configuration.defaultSorts, function (value) {
          return {
            sortName: value.sortName,
            sortOrder: value.sortOrder
          };
        });
      }
      return defaultSort;
    },
    collectKeyword: function (folderUuid) {
      var keyword = $("input[name='keyword']", this.$toolbar).val();
      return keyword || '';
    },
    setKeyword: function (keyword) {
      $("input[name='keyword']", this.$toolbar).val(keyword);
    },
    onDataChange: function (data, count, params) {
      var _self = this;
      // 生成内容视图
      _self.buildContentView();
    },
    getNavFolderActions: function (folderUuid) {
      var _self = this;
      // 工具栏操作按钮
      var options = {
        data: {
          folderUuid: folderUuid
        },
        success: function (result) {
          var dataList = result.data ? result.data.dataList : result.dataList;
          // 过滤掉导航在工具栏中允许的操作
          var navFileActions = _self.filterNavAction(dataList);
          _self.navSelectedItemInToolbarActions = navFileActions;
          navFileActions = _self.fileManagerWidget.fillFileActionInfoFromConfiguration(navFileActions);
          // 调用JS二开模块的回调接口
          _self.fileManagerWidget.invokeDevelopmentMethod('beforeUpdateToolbar', [navFileActions]);
          // 生成工具栏操作按钮
          _self.buildToolbar(navFileActions);
        }
      };
      _self.dmsFileServices.getFolderActions(options);
    },
    filterNavAction: function (fileActions) {
      return this.dmsFileServices.filterNavInToolbarActions(fileActions);
    },
    // 生成工具栏操作按钮
    buildToolbar: function (fileActions) {
      var _self = this;
      var $bsBars = _self.$toolbar.find('.fm-bs-bars');
      $bsBars.html('');
      var toolbarHtml = _self.dmsFileServices.buildToolbarHtml(fileActions);
      if (fileActions.length == 0) {
        $bsBars.hide();
      } else {
        $bsBars.show();
      }
      $bsBars.html(toolbarHtml);
      $.each(fileActions, function (i, fileAction) {
        var selector = "button[btnId='" + fileAction.id + "'],li[btnId='" + fileAction.id + "']";
        $bsBars.find(selector).data('fileAction', fileAction);
      });
    },
    // 生成内容视图
    buildContentView: function () {
      var _self = this;
      var $fileBoxes = $('.file-boxes', _self.$element);
      $fileBoxes.html('');
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      _self.dataProvider.each(function (i, data) {
        data.ctx = ctx;
        data.isImage = _self.isImage(data);
        _self.fillIconClass(data);
        data.index = i;
        var text = templateEngine.renderById('pt-dms-file-box', data);
        $fileBoxes.append(text);
      });
      if (_self.dataProvider.isEmpty()) {
        $fileBoxes.append('<div class="no-records-found">没有找到匹配的记录</div>');
      }
      // 调用JS二开模块的回调接口
      _self.fileManagerWidget.invokeDevelopmentMethod('onPostBody', []);
    },
    // 填充图标样式
    fillIconClass: function (fileInfo) {
      var _self = this;
      if (_self.dmsFileServices.isExcel(fileInfo)) {
        fileInfo.iconClass = 'iconfont icon-ptkj-excel excel-color';
        // 添加文件类型字段
        fileInfo.fileType = 'excel';
      } else if (_self.dmsFileServices.isPowerPoint(fileInfo)) {
        fileInfo.iconClass = 'iconfont icon-ptkj-ppt ppt-color';
        fileInfo.fileType = 'powerpoint';
      } else if (_self.dmsFileServices.isWord(fileInfo)) {
        fileInfo.iconClass = 'iconfont icon-ptkj-word word-color';
        fileInfo.fileType = 'word';
      } else if (_self.dmsFileServices.isPdf(fileInfo)) {
        fileInfo.iconClass = 'iconfont icon-ptkj-pdf pdf-color';
        fileInfo.fileType = 'pdf';
      } else if (_self.dmsFileServices.isArchive(fileInfo)) {
        fileInfo.iconClass = 'fa fa-file-archive-o';
        fileInfo.fileType = 'archive';
      } else if (_self.dmsFileServices.isVideo(fileInfo)) {
        fileInfo.iconClass = 'fa fa-file-video-o';
        fileInfo.fileType = '视频';
      } else if (_self.dmsFileServices.isAudio(fileInfo)) {
        fileInfo.iconClass = 'fa fa-file-audio-o';
        fileInfo.fileType = '音频';
      } else {
        fileInfo.iconClass = 'iconfont icon-ptkj-tongyongfujian other-color';
        fileInfo.fileType = '其他';
      }
    },
    isImage: function (fileInfo) {
      return this.dmsFileServices.isImage(fileInfo);
    },
    _bindEvents: function () {
      var _self = this;
      // 视图列表切换事件
      $(_self.element).on('click', '.btn-thumbnail-view', function () {
        var eventData = {
          currentViewType: 'fileManagerThumbnailView',
          switchToViewType: 'bootstrapTableDataView',
          keyword: _self.collectKeyword()
        };
        appContext.dispatchEvent('wFileManager.SwitchDataView', eventData, _self);
      });

      // 全选/不选
      $(_self.element).on('click', '.fm-checkbox input', function () {
        var checkAll = $(this)[0].checked;
        if (checkAll) {
          var $fileBoxes = _self.getAllFileBoxesElm();
          $.each($fileBoxes, function () {
            _self.checkFileBoxElm($(this));
          });
          _self.dispatchItemSelectionChangedEvent();
        } else {
          var $fileBoxes = _self.getAllFileBoxesElm();
          $.each($fileBoxes, function () {
            _self.unCheckFileBoxElm($(this));
          });
          _self.dispatchItemSelectionChangedEvent();
        }
      });

      // 视图夹打开
      $(_self.element).on('click', '.file', function (e) {
        // 禁用数据项点击事件，直接返回
        if (_self.fileManagerWidget.isDisableItemClick()) {
          return;
        }
        var $fileElm = $(this);
        var uuid = $fileElm.attr('uuid');
        var $fileNameEditable = $fileElm.find('.file-name-editable');
        if ($fileNameEditable.length > 0) {
          return;
        }
        var $fileChebox = $fileElm.find('.file-checkbox');
        if (StringUtils.isNotBlank(uuid) && $fileChebox.has(e.target).length == 0) {
          var data = _self.dataProvider.get(uuid);
          var isFolder = _self.dmsFileServices.isFolder(data);
          var isDyform = _self.dmsFileServices.isDyform(data);
          var dataIndex = $fileElm.attr('data-index');
          // 调用JS二开模块的回调接口
          _self.fileManagerWidget.invokeDevelopmentMethod('onItemClick', [dataIndex, data, $fileElm]);
          _self.fileManagerWidget.trigger('wFileManager.ItemClick', {
            index: dataIndex,
            item: data,
            element: $fileElm
          });
          // 判断文件项本身是否允许打开
          if (!_self.dmsFileServices.isAllowOpen(data)) {
            return;
          }
          // 视图夹打开
          if (isFolder) {
            var oldFolderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
            _self.fileManagerWidget.setCurrentFolderUuid(uuid);
            _self.refresh();
            // 派发夹变更事件
            var eventData = {
              currentFolderUuid: uuid,
              currentFolderData: data,
              oldFolderUuid: oldFolderUuid
            };
            appContext.dispatchEvent('wFileManager.OpenFolder', eventData, _self);
          } else if (_self.hasOpenFilePermission(data)) {
            if (isDyform) {
              // 打开表单数据
              _self.openDyform(uuid, data);
            } else {
              // 文件在线预览
              _self.viewFileOnline(uuid, data);
            }
          } else {
            console.error('click file, but no permission to open.');
          }
        }
      });

      // 工具栏
      var $toolbar = $('.file-manager-toolbar', _self.element);
      $toolbar.on('click', '.fm-bs-bars button,.fm-bs-bars .btn-group ul li', function (e) {
        // 重复名处理编辑状态，获取焦点并返回
        var $fileNameElem = $("input[name='file-name-editable']", _self.element);
        if ($fileNameElem.length > 0) {
          $fileNameElem.focus();
          return;
        }

        var action = $(this).attr('action');
        var fileAction = $(this).data('fileAction');
        if (StringUtils.isNotBlank(action)) {
          if (fileAction && fileAction.eventManger && !$.isEmptyObject(fileAction.eventManger.eventHandler)) {
            var eventHandler = fileAction.eventManger.eventHandler;
            var eventParams = fileAction.eventManger.eventParams || {};
            var target = fileAction.target || {};
            var opt = {
              target: target.position,
              targetWidgetId: target.widgetId,
              refreshIfExists: target.refreshIfExists,
              appId: eventHandler.id,
              appType: eventHandler.type,
              appPath: eventHandler.path,
              params: $.extend({}, eventParams.params, appContext.parseEventHashParams(eventHandler, 'menuid'))
            };
            _self.fileManagerWidget.startApp(opt);
          } else if ($.isFunction(_self[action])) {
            _self[action].call(_self, e, fileAction, _self);
          } else {
            // 调用JS二开模块的回调接口
            _self.fileManagerWidget.invokeDevelopmentMethod(action, [e, fileAction, _self.fileManagerWidget]);
          }
        }
      });

      // 工具栏查询按钮
      $toolbar.on('click', "button[name='query']", function (e) {
        _self.refresh();
      });
      // 工具栏查询框回车
      $toolbar.on('keyup', "input[name='keyword']", function (e) {
        if (e.keyCode == 13) {
          _self.refresh();
        }
      });
      // 工具栏重置按钮
      $toolbar.on('click', "button[name='reset']", function (e) {
        if (_self.showQueryField != true) {
          $("input[name='keyword']", _self.$toolbar).val('');
        } else {
          _self.$fieldSearchElement = _self._renderFieldSearch(_self.element);
        }
      });
      // 展示查询字段
      $toolbar.on('click', "button[name='showQueryField']", function (e) {
        var $this = $(this);
        if (_self.showQueryField != true) {
          if (_self.$fieldSearchElement) {
            _self.$fieldSearchElement.show();
          } else {
            _self.$fieldSearchElement = _self._renderFieldSearch();
          }
          $(_self.element).find('.file-manager-toolbar .search').hide();
          $this.find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
          $this.attr('title', '收起字段查询');
          _self.showQueryField = true;
        } else {
          _self.$fieldSearchElement.hide();
          $(_self.element).find('.file-manager-toolbar .search').show();
          $this.find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
          $this.attr('title', '展开字段查询');
          _self.showQueryField = false;
        }
      });
      // 查询字段回车查询
      $(_self.element).on('keyup', '.w-search-option', function (event) {
        if (event.keyCode == 13) {
          _self.refresh();
        }
      });

      // 文件列表checkbox选择变化事件
      $(_self.element).on('wFileManager.ItemSelectionChanged', function (e, ui) {
        var selectedItems = e.detail.selectedItems;
        // 获取选中文件可进行的操作
        var fileItemActions = _self.dmsFileServices.getIntersectionActions(selectedItems);
        var itemActions = _self.navSelectedItemInToolbarActions.concat(fileItemActions);
        // 最后过滤允许在工具栏上展示的按钮
        _self.fileManagerWidget.checkAllowedActionsInToolbar(fileItemActions);
        itemActions = _self.fileManagerWidget.fillFileActionInfoFromConfiguration(itemActions);
        // 调用JS二开模块的回调接口
        _self.fileManagerWidget.invokeDevelopmentMethod('beforeUpdateToolbar', [itemActions]);
        // 生成工具栏操作按钮
        _self.buildToolbar(itemActions);
        // 更新文件的全选/不选信息
        _self.updateFileCheckboxInfo();
      });

      // 鼠标移入事件
      $(_self.element).on('mouseenter', '.file-box', function (e) {
        // 如果有选中文件进行操作，直接返回
        var $fileNameElem = $("input[name='file-name-editable']", _self.element);
        if ($fileNameElem.length > 0) {
          return;
        }
        $(this).addClass('file-box-hover');
        $(this).find('.file-checkbox').removeClass('hide');
        var eleDom = $(this);
        _self.isShowPop = true;
        _self.timer = setTimeout(function () {
          if (_self.isShowPop == true) {
            eleDom.find('.popover').addClass('showPopover');
          }
        }, 1000);
      });
      // 鼠标移出事件
      $(_self.element).on('mouseleave', '.file-box', function (e) {
        var $fileCheckbox = $(this).find('.file-checkbox > input[type=checkbox]');
        if ($fileCheckbox.length === 1 && $fileCheckbox[0].checked !== true) {
          $(this).removeClass('file-box-hover');
          $(this).find('.file-checkbox').addClass('hide');
        }
        if (_self.isShowPop == true) {
          clearTimeout(_self.timer);
          $(this).find('.popover').removeClass('showPopover');
          _self.isShowPop = false;
        }
      });

      // 绑定确定事件
      $(_self.element).on('blur', "input[name='file-name-editable']", function () {
        var $fileNameElem = $("input[name='file-name-editable']", _self.element);
        var fileName = $fileNameElem.val();
        var extension = $fileNameElem.attr('ext');
        if (StringUtils.isBlank(fileName)) {
          appModal.error('文件（夹）名称不能为空，请输入名称！');
          return;
        }
        // if (!_self.dmsFileServices.isValidFileName(fileName)) {
        // appModal.error("无效的文件（夹）名称！");
        // return false;
        // }
        if (StringUtils.isNotBlank(extension)) {
          fileName += '.' + extension;
        }
        var uuid = $fileNameElem.closest('.file').attr('uuid');
        if (StringUtils.isBlank(uuid)) {
          // 新建夹服务处理
          var param = {};
          param.folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
          param.data = {};
          param.data.name = fileName;
          var options = {};
          options.data = param;
          options.success = function (success, statusText, jqXHR) {
            var readFolderOptions = {
              data: {
                folderUuid: success.data
              },
              success: function (success) {
                _self.dataProvider.add(success.data);
                _self._createFolderSuccess(success.data);
                // 触发文件夹创建成功事件
                _self.fileManagerWidget.trigger('wFileManager.FolderCreated', {
                  parentFolderUuid: param.folderUuid,
                  newFolderUuid: success.data.uuid
                });
              }
            };
            _self.dmsFileServices.readFolder(readFolderOptions);
          };
          _self.dmsFileServices.createFolder(options);
        } else {
          var fileItem = _self.dataProvider.get(uuid);
          var renameSuccess = function (success) {
            var newFileItem = _self.dataProvider.get(uuid);
            newFileItem.name = fileName;
            _self.dataProvider.update(newFileItem);
            _self._renameFolderSuccess(newFileItem);
            // 触发文件（夹）重命名成功事件
            _self.fileManagerWidget.trigger('wFileManager.ItemRenamed', {
              item: newFileItem
            });
          };
          // 重命名夹
          if (_self.dmsFileServices.isFolder(fileItem)) {
            var renameFolderOptions = {
              data: {
                folderUuid: uuid,
                newFolderName: fileName
              },
              success: renameSuccess
            };
            _self.dmsFileServices.renameFolder(renameFolderOptions);
          } else {
            // 重命名文件
            var renameFileOptions = {
              data: {
                fileUuid: uuid,
                newFileName: fileName
              },
              success: renameSuccess
            };
            _self.dmsFileServices.renameFile(renameFileOptions);
          }
        }
      });
      // 绑定取消事件
      $(_self.element).on('click', '.btn-remove', function () {
        var $fileBoxElem = $(this).closest('.file-box');
        var uuid = $fileBoxElem.find('.file').attr('uuid');
        // 新建夹取消
        if (StringUtils.isBlank(uuid)) {
          $fileBoxElem.remove();
        } else {
          // 重命名夹取消
          var fileItem = _self.dataProvider.get(uuid);
          _self._renameFolderSuccess(fileItem, true);
        }
      });
      // 文件项选中事件
      $(_self.element).on('change', '.file-checkbox > input[type=checkbox]', function () {
        if ($(this).prop('checked') == true) {
          $(this).parents('.file-box');
        } else {
          $(this).parents('.file-box').css('background', '');
        }
        _self.dispatchItemSelectionChangedEvent();
      });
    },
    // 新建文件夹
    createFolder: function (e, ui) {
      var _self = this;
      // 确保只有一个新增未保存状态的文件
      if ($('.file-box-editable', _self.element).length > 0) {
        $('.file-box-editable', _self.element).find('input').focus();
        return;
      }
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById('pt-dms-file-box-editable', {
        basename: '新建文件夹',
        extension: '',
        contentType: 'application/folder'
      });
      var $fileBoxes = $('.file-boxes', _self.element);
      if ($fileBoxes.children().length === 0) {
        $fileBoxes.append(text);
      } else {
        $($fileBoxes.children()[0]).before(text);
      }
    },
    // 夹创建成功处理
    _createFolderSuccess: function (data) {
      var _self = this;
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var newText = templateEngine.renderById('pt-dms-file-box', data.data);
      var $editableInput = $("input[name='file-name-editable']", _self.element);
      var fileBoxEditable = $editableInput.closest('.file-box');
      fileBoxEditable.html($(newText).html());
      fileBoxEditable.removeClass('file-box-editable');
      appModal.info('文件夹创建成功！');
    },
    // 重命名
    rename: function (e, ui) {
      var _self = this;
      var $fileCheckbox = $(_self.element).find('.file-checkbox > input[type=checkbox]:checked');
      var $selectFileBox = $fileCheckbox.closest('.file-box');
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var item = _self.getSelectItem($selectFileBox);
      var basename = _self.dmsFileServices.getFileBaseName(item.name);
      var extension = _self.dmsFileServices.getFileExtension(item.name);
      item.basename = basename;
      item.extension = extension;
      var newText = templateEngine.renderById('pt-dms-file-box-editable', item);
      $selectFileBox.html($(newText).html());
      $selectFileBox.html($(newText).html()).find("input[name='file-name-editable']").focus();
      // 禁用全选/不选checkbox
      _self.disableFileCheckboxInfo();
    },
    _renameFolderSuccess: function (data, slient) {
      var _self = this;
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var newText = templateEngine.renderById('pt-dms-file-box', data);
      var $editableInput = $("input[name='file-name-editable']", _self.element);
      var fileBoxEditable = $editableInput.closest('.file-box');
      fileBoxEditable.html($(newText).html());
      // 还原原来的标签html
      fileBoxEditable.find('.dms-file-tags').append(fileBoxEditable.data('tagsHtml'));
      fileBoxEditable.removeClass('file-box-editable');
      if (slient !== true) {
        appModal.info('重命名成功！');
      }
      // 保持选中checkbox
      var $fileCheckbox = $(fileBoxEditable).find('.file-checkbox > input[type=checkbox]');
      if ($fileCheckbox.length == 1) {
        fileBoxEditable.addClass('file-box-hover');
        $fileCheckbox.find('.file-checkbox').removeClass('hide');
        $fileCheckbox[0].checked = true;
        $fileCheckbox.trigger('mouseenter');
      }
      // 启用全选/不选checkbox
      _self.enableFileCheckboxInfo();
    },
    // 移动
    move: function () {
      var _self = this;
      var rootFolder = _self.fileManagerWidget.getRootFolderForDialog();
      var selection = _self.getSelection();
      var options = {
        rootFolderUuid: rootFolder.uuid,
        rootFolderName: rootFolder.name,
        selection: selection,
        callback: function (source, destFolderUuid) {
          _self.dataViewManager.refresh();
          appModal.info('移动成功！');
          // 触发文件（夹）移动成功事件
          _self.fileManagerWidget.trigger('wFileManager.ItemMoved', {
            source: source,
            destFolderUuid: destFolderUuid
          });
        }
      };
      _self.dmsFileServices.move(options);
    },
    // 复制
    copy: function () {
      var _self = this;
      var rootFolder = _self.fileManagerWidget.getRootFolderForDialog();
      var selection = _self.getSelection();
      var options = {
        rootFolderUuid: rootFolder.uuid,
        rootFolderName: rootFolder.name,
        selection: selection,
        callback: function (source, destFolderUuid) {
          _self.dataViewManager.refresh();
          appModal.info('复制成功！');
          // 触发文件（夹）复制成功事件
          _self.fileManagerWidget.trigger('wFileManager.ItemCopied', {
            source: source,
            destFolderUuid: destFolderUuid
          });
        }
      };
      _self.dmsFileServices.copy(options);
    },
    // 新建文件
    createFile: function (e) {
      var _self = this;
      var optins = {
        ui: _self,
        e: e,
        folderUuid: _self.fileManagerWidget.getCurrentFolderUuid(),
        fileuploaddone: function () {
          _self.refresh();
        }
      };
      _self.dmsFileServices.uploadFile(optins);
    },
    // 新建文档
    createDocument: function (e) {
      var _self = this;
      var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
      var options = {
        folderUuid: folderUuid,
        ui: _self.fileManagerWidget
      };
      _self.dmsFileServices.createDocument(options);
    },
    // 判断夹是否有打开文件的权限
    hasOpenFilePermission: function (row) {
      var _self = this;
      return _self.dmsFileServices.hasFilePermission(row.uuid, 'openFile');
    },
    // 打开表单数据
    openDyform: function (fileUuid, data) {
      var _self = this;
      var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
      var options = {
        idKey: 'UUID',
        idValue: data.dataUuid,
        folderUuid: folderUuid,
        fileUuid: fileUuid,
        data: data,
        ui: _self.fileManagerWidget
      };
      _self.dmsFileServices.openDyform(options);
    },
    // 文件在线预览
    viewFileOnline: function (fileUuid, data) {
      var viewer = new DmsFileOnlineViewer();
      viewer.viewOnline(data);
    },
    // 查看属性
    viewAttributes: function () {
      var _self = this;
      var selection = _self.getSelection();
      var options = {
        selection: selection
      };
      _self.dmsFileServices.viewAttributes(options);
    },
    // 分享
    share: function (e) {
      var _self = this;
      var selection = _self.getSelection();
      _self.dmsFileServices.share(selection);
    },
    // 取消分享
    cancelShare: function (e) {
      var _self = this;
      var selection = _self.getSelection();
      if (selection.length == 0) {
        appModal.error('请选择操作记录！');
        return;
      }
      _self.dmsFileServices.cancelShare({
        selection: selection,
        success: function () {
          _self.refresh();
          appModal.info('取消分享成功！');
        }
      });
    },
    // 下载
    download: function (e) {
      var _self = this;
      var selection = _self.getSelection();
      if (selection.length == 0) {
        appModal.error('请选择操作记录！');
        return;
      }
      _self.dmsFileServices.download(selection);
    },
    // 删除
    delete: function (e, fileAction) {
      var _self = this;
      var selection = _self.getSelection();
      var deleteOptions = {
        data: [selection],
        success: function (success) {
          _self._deleteFolderSuccess(selection);
          // 触发文件（夹）删除成功事件
          _self.fileManagerWidget.trigger('wFileManager.ItemDeleted', {
            selection: selection
          });
        }
      };
      var configMsg = '确认要删除所选文件（夹）吗？';
      if (fileAction && StringUtils.isNotBlank(fileAction.confirmMsg)) {
        configMsg = fileAction.confirmMsg;
      }
      appModal.confirm(configMsg, function (result) {
        if (result) {
          _self.dmsFileServices.deleteFile(deleteOptions);
        }
      });
    },
    _deleteFolderSuccess: function (selection) {
      var _self = this;
      $.each(selection, function () {
        _self.dataProvider.remove(this.uuid);
      });
      _self.refresh();
      appModal.success('删除成功！');
    },
    getSelectItem: function ($selectFileBox) {
      var uuid = $selectFileBox.find('.file').attr('uuid');
      return this.dataProvider.get(uuid);
    },
    getSelection: function () {
      var _self = this;
      var uuids = _self.getSelectionIndexes();
      var selectedItems = [];
      $.each(uuids, function () {
        selectedItems.push(_self.dataProvider.get(this));
      });
      return selectedItems;
    },
    getSelectionIndexes: function () {
      var _self = this;
      var $fileCheckbox = $(_self.element).find('.file-checkbox > input[type=checkbox]:checked');
      var uuids = [];
      $.each($fileCheckbox, function (i) {
        var uuid = $(this).closest('.file').attr('uuid');
        uuids.push(uuid);
      });
      return uuids;
    },
    getAllFileBoxesElm: function () {
      return $(this.element).find('.file-boxes').find('.file-box');
    },
    checkFileBoxElm: function ($fileBoxElm) {
      $fileBoxElm.addClass('file-box-hover');
      $fileBoxElm.find('.file-checkbox').removeClass('hide');
      var $checkbox = $fileBoxElm.find('.file-checkbox').find('input');
      $.each($checkbox, function () {
        this.checked = true;
      });
    },
    unCheckFileBoxElm: function ($fileBoxElm) {
      $fileBoxElm.removeClass('file-box-hover');
      $fileBoxElm.find('.file-checkbox').addClass('hide');
      var $checkbox = $fileBoxElm.find('.file-checkbox').find('input');
      $.each($checkbox, function () {
        this.checked = false;
      });
    },
    dispatchItemSelectionChangedEvent: function () {
      var _self = this;
      var uuids = _self.getSelectionIndexes();
      var selectedItems = _self.getSelection();
      var eventData = {
        selection: uuids,
        selectedItems: selectedItems
      };
      appContext.dispatchEvent('wFileManager.ItemSelectionChanged', eventData, _self);
    },
    updateFileCheckboxInfo: function () {
      var _self = this;
      var $fileCheckbox = $(_self.element).find('.fm-checkbox');
      var $checkbox = $(_self.element).find('.fm-checkbox').find('input');
      var allFileSize = _self.dataProvider.size();
      var checkSize = _self.getSelectionIndexes().length;
      if (allFileSize !== 0) {
        if (allFileSize == checkSize) {
          $checkbox[0].checked = true;
        } else {
          $checkbox[0].checked = false;
        }
      }
      if ($checkbox[0].checked || checkSize > 0) {
        $fileCheckbox.find('label').text('已选中' + checkSize + '个文件/文件夹');
      } else {
        $fileCheckbox.find('label').text('全选');
      }
    },
    resetFileCheckboxInfo: function () {
      var _self = this;
      var $fileCheckbox = $(_self.element).find('.fm-checkbox');
      var $checkbox = $(_self.element).find('.fm-checkbox').find('input');
      $checkbox[0].checked = false;
      $fileCheckbox.find('label').text('全选');
    },
    disableFileCheckboxInfo: function () {
      var _self = this;
      var $checkbox = $(_self.element).find('.fm-checkbox').find('input');
      $checkbox.attr('disabled', 'disabled');
    },
    enableFileCheckboxInfo: function () {
      var _self = this;
      var $checkbox = $(_self.element).find('.fm-checkbox').find('input');
      $checkbox.removeAttr('disabled');
    }
  });
  return DmsFileManagerThumbnailView;
});

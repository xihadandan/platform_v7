(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext', 'DmsFileServices', 'DmsFileManagerThumbnailView'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, DmsFileServices, DmsFileManagerThumbnailView) {
  'use strict';
  // var treePanelTemplate = '<div class="panel panel-default tree-panel">';
  // treePanelTemplate += '<div class="panel-heading hide"></div>';
  var treePanelTemplate = '<div class="folder-tree-container">';
  treePanelTemplate += '<ul class="folder-tree ztree"></ul>';
  treePanelTemplate += '</div>';
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var KEY_DMS_ID = 'dms_id';
  var dataViewTypes = {
    bootstrapTableDataView: 'bootstrapTableDataView',
    fileManagerThumbnailView: 'fileManagerThumbnailView'
  };
  var dataTypes = {
    dyform: 'DYFORM'
  };
  // 工具栏可操作的权限
  var toolbarAvaibleActions = {
    // "createFolder" : "创建夹",
    share: '共享夹',
    shareFolder: '共享夹',
    cancelShareFolder: '取消共享夹',
    viewFolderAttributes: '查看夹属性',
    // "renameFolder" : "重命名夹",
    moveFolder: '移动夹',
    copyFolder: '复制夹',
    deleteFolder: '删除夹(有子夹、文件不可删除)',
    forceDeleteFolder: '删除夹(有子夹、文件一起删除)',
    createFile: '创建文件',
    downloadFile: '下载文件',
    attentionFile: '收藏文件',
    unfollowFile: '取消收藏文件',
    shareFile: '共享文件',
    cancelShareFile: '取消共享文件',
    viewFileAttributes: '查看文件属性',
    // "renameFile" : "重命名文件",
    checkOutFile: '签出文件',
    checkInFile: '签入文件',
    moveFile: '移动文件',
    copyFile: '复制文件',
    deleteFile: '删除文件'
  };
  var UUID = commons.UUID;
  $.widget('ui.wFileManager', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    getDmsId: function () {
      // return this.options.widgetDefinition.srcId || this.getId();
      // bug#46019
      return this.getId();
    },
    _init: function () {
      var _self = this;
      // 是否显示导航夹的操作按钮
      _self.showNavFolderActions = true;
      // 是否禁用数据项点击事件
      _self.disableItemClick = false;
      // 是否使用自定义的文件库数据源
      _self.useCustomFileManagerDataStore = false;
      // 是否显示工具栏操作按钮
      _self.showToolbarActions = false;
      // 允许在 工具栏上展示的按钮
      _self.allowedActionsInToolbar = {
        createFolder: 'createFolder',
        createFile: 'createFile',
        createDocument: 'createDocument',
        download: 'download',
        delete: 'delete',
        rename: 'rename',
        copy: 'rename',
        move: 'move',
        viewAttributes: 'viewAttributes'
      };
      // 其他要展示在视图工具栏上的按钮，不受动态权限控制
      _self.otherActionsInToolbar = [];
      // _self.fileActionMap = {};
      _self.configFileActions = [];
      var configuration = _self.getConfiguration();
      if (configuration && configuration.view && configuration.view.fileActions) {
        // $.each(configuration.view.fileActions, function() {
        // _self.fileActionMap[this.id] = this;
        // });
        _self.configFileActions = configuration.view.fileActions;
      }
      // 所有夹配置信息
      _self.folderConfigurations = {};
      _self.dataViewManagers = _self.dataViewManagers || {};
    },
    _createView: function () {
      var _self = this;
      _self.dmsFileServices = new DmsFileServices();
      _self._renderView();
      _self._bindEvents();
    },
    _renderView: function () {
      var _self = this;
      // 显示夹导航
      var configuration = _self.getConfiguration();
      if (configuration.showFolderNav == true) {
        // 生成树导航
        _self._createFolderNav();
        // 生成页面组件
        _self._createContentView(true);
        $('.file-manager-view', _self.element)
          .css('border-left', '1px solid #E8E8E8')
          .append("<div class='folder-fold'><i class='iconfont icon-ptkj-zuoshouzhan'></i></div>");
      } else {
        // 生成页面组件
        _self._createContentView(false);
        _self.setSelectedFolderUuid(configuration.belongToFolderUuid);
        _self.setCurrentFolderUuid(configuration.belongToFolderUuid);
        _self.listFolderContent(configuration.belongToFolderUuid);
      }
    },
    _createFolderNav: function () {
      var _self = this;
      var $nav = $('.file-manager-nav', _self.element);
      _self.$nav = $nav;
      var configuration = _self.getConfiguration();
      $nav.append(treePanelTemplate);
      var treeId = _self.getFolderTreeId();
      $('ul.folder-tree', $nav).attr('id', treeId);
      _self.folderTree = $.fn.zTree.init($('#' + treeId), _self.getFolderTreeSetting());
    },
    getFolderTreeId: function () {
      var _self = this;
      if (StringUtils.isBlank(_self.folderTreeId)) {
        _self.folderTreeId = 'folder_tree_' + UUID.createUUID();
      }
      return _self.folderTreeId;
    },
    // 根据夹UUID，刷新对应的结点
    refreshFolderTreeNode: function (folderUuid) {
      var _self = this;
      var treeId = _self.getFolderTreeId();
      var folderTree = $.fn.zTree.getZTreeObj(treeId);
      if (folderTree != null) {
        var nodes = folderTree.getNodesByParam('id', folderUuid);
        $.each(nodes, function (i, node) {
          if (node.children && node.children.length == 0) {
            node.isParent = true;
          }
          folderTree.reAsyncChildNodes(node, 'refresh');
        });
      }
    },
    // 根据夹UUID，刷新对应的父结点
    refreshFolderTreeParentNode: function (folderUuid) {
      var _self = this;
      var treeId = _self.getFolderTreeId();
      var folderTree = $.fn.zTree.getZTreeObj(treeId);
      if (folderTree != null) {
        var nodes = folderTree.getNodesByParam('id', folderUuid);
        $.each(nodes, function () {
          folderTree.reAsyncChildNodes(this.getParentNode(), 'refresh');
        });
      }
    },
    // 返回zTree树配置
    getFolderTreeSetting: function (searchText) {
      var _self = this;
      var configuration = _self.getConfiguration();
      // zTree树配置
      var setting = {
        check: {
          enable: configuration.checkEnable,
          chkStyle: configuration.chkStyle,
          chkboxType: {},
          radioType: configuration.radioType
        },
        view: {
          showLine: true,
          selectedMulti: true
        },
        edit: {
          enable: false,
          showRemoveBtn: false,
          showRenameBtn: false
        },
        callback: _self.getFolderTreeCallback()
      };
      // 单选/复选联动
      var chkboxType = {
        Y: 'ps',
        N: 'ps'
      };
      setting.check.chkboxType = chkboxType;
      // 自定义配置
      if (configuration.customSetting) {
        var zTreeSetting = JSON.parse(configuration.zTreeSetting);
        for (var key in zTreeSetting) {
          setting[key] = $.extend(setting[key] || {}, zTreeSetting[key]);
        }
      }
      // 是否异常
      setting.async = {
        enable: true,
        dataType: 'json',
        autoParam: ['id=parentFolderUuid'],
        otherParam: _self.getOtherParams(searchText),
        url: ctx + '/dms/file/manager/component/load/folder/tree'
      };
      return setting;
    },
    getOtherParams: function (searchText) {
      var _self = this;
      var configuration = _self.getConfiguration();

      var otherParam = {};
      otherParam.belongToFolderUuid = configuration.belongToFolderUuid;
      if (configuration.showFolderNav) {
        otherParam.showRootFolder = configuration.showRootFolder;
      }
      // otherParam.loadAction = true;
      otherParam.checkIsParent = true;
      if (StringUtils.isNotBlank(searchText)) {
        otherParam.searchText = searchText;
      }
      return otherParam;
    },
    getFolderTreeCallback: function () {
      var _self = this;
      var callback = {
        onAsyncSuccess: function () {
          _self._onFolderTreeAsyncSuccess.apply(_self, arguments);
        },
        onClick: function () {
          _self.onFolderTreeClick.apply(_self, arguments);
        }
      };
      return callback;
    },
    _onFolderTreeAsyncSuccess: function (event, treeId, treeNode, msg) {
      var _self = this;
      // 第一次加载选中第一个结点
      if (treeNode == null) {
        var folderTree = $.fn.zTree.getZTreeObj(treeId);
        var nodes = folderTree.getNodes();
        if (nodes.length > 0) {
          folderTree.selectNode(nodes[0]);
          _self.setSelectedFolderUuid(nodes[0].id);
          _self.setCurrentFolderUuid(nodes[0].id);
          _self.listFolderContent(nodes[0].id);
        }
        var configuration = _self.getConfiguration();
        // 根据配置显示根目录名称
        var rootFolderDisplayName = configuration.rootFolderDisplayName;
        if (nodes.length == 1 && StringUtils.isNotBlank(rootFolderDisplayName)) {
          nodes[0].name = rootFolderDisplayName;
          folderTree.updateNode(nodes[0]);
        }
        // 根据配置展开根目录
        if (nodes.length == 1 && configuration.showRootFolder && configuration.expandRootFolder) {
          _self.folderTree.expandNode(nodes[0]);
        }
      }
    },
    onFolderTreeClick: function (event, treeId, treeNode) {
      var _self = this;
      // 面包屑导航——共享数据
      _self.updateBreadcrumbDataProvider(treeNode);
      _self.setSelectedFolderUuid(treeNode.id);
      _self.setCurrentFolderUuid(treeNode.id);
      _self.listFolderContent(treeNode.id);
    },
    updateBreadcrumbDataProvider: function (treeNode) {
      var _self = this;
      _self.breadcrumbDataProvider = [];
      _self._addBreadcrumbDataProvider(_self.breadcrumbDataProvider, treeNode);
    },
    _addBreadcrumbDataProvider: function (dataProvider, treeNode) {
      var _self = this;
      var nav = {
        uuid: treeNode.id,
        name: treeNode.name
      };
      _self.breadcrumbDataProvider.unshift(nav);
      var parentNode = treeNode.getParentNode();
      if (parentNode != null) {
        _self._addBreadcrumbDataProvider(dataProvider, parentNode);
      }
    },
    // 列出文件的模式
    setListFileMode: function (listFileMode) {
      this.listFileMode = listFileMode;
    },
    getListFileMode: function () {
      return this.listFileMode;
    },
    // 显示导航夹操作按钮
    setShowNavFolderActions: function (showNavFolderActions) {
      this.showNavFolderActions = showNavFolderActions;
    },
    isShowNavFolderActions: function () {
      return this.showNavFolderActions;
    },
    // 是否禁用数据项点击事件
    setDisableItemClick: function (disableItemClick) {
      this.disableItemClick = disableItemClick;
    },
    isDisableItemClick: function () {
      return this.disableItemClick;
    },
    // 允许在 工具栏上展示的按钮
    checkAllowedActionsInToolbar: function (fileActions) {
      var allowedActions = this.allowedActionsInToolbar;
      $.each(fileActions, function () {
        if (allowedActions[this.id] == null) {
          this.hidden = true;
        }
      });
    },
    // 从配置信息中填充操作信息
    fillFileActionInfoFromConfiguration: function (fileActions) {
      var _self = this;
      var returnActions = [];
      var actionMap = {};
      $.each(fileActions, function () {
        if (this.hidden !== true) {
          actionMap[this.id] = this;
        }
      });
      // 配置的文件操作
      $.each(_self.configFileActions, function (i, configAction) {
        var eventManger = configAction.eventManger;
        // 旧配置数据的文件操作ID
        if (StringUtils.isNotBlank(configAction.id)) {
          var action = actionMap[configAction.id];
          if (action != null) {
            // 创建者不可删除
            if (action.isCreator == true && eventManger && eventManger.params && eventManger.params.allowCreator == 'false') {
              return;
            }
            returnActions.push({
              id: configAction.id,
              name: configAction.name,
              group: configAction.group,
              cssClass: configAction.cssClass,
              icon: configAction.icon,
              hidden: configAction.hidden == '1',
              confirmMsg: configAction.confirmMsg,
              target: configAction.target,
              btnLib: configAction.btnLib,
              eventManger: eventManger
            });
          }
        } else if (StringUtils.isNotBlank(configAction.associatedFileAction)) {
          // 关联的文件操作
          var action = actionMap[configAction.associatedFileAction];
          if (action != null && StringUtils.isNotBlank(configAction.code)) {
            // 创建者不可删除
            if (action.isCreator == true && eventManger && eventManger.params && eventManger.params.allowCreator == 'false') {
              return;
            }
            returnActions.push({
              id: configAction.code,
              name: configAction.name,
              group: configAction.group,
              cssClass: configAction.cssClass,
              icon: configAction.icon,
              hidden: configAction.hidden == '1',
              confirmMsg: configAction.confirmMsg,
              target: configAction.target,
              btnLib: configAction.btnLib,
              eventManger: configAction.eventManger
            });
          }
        }
      });
      // $.each(fileActions, function() {
      // var fileAction = _self.fileActionMap[this.id];
      // if (fileAction) {
      // this.name = fileAction.name;
      // this.cssClass = fileAction.cssClass;
      // this.icon = fileAction.icon;
      // // 文件管理自身处理的逻辑为隐藏，则不从配置加载
      // if (this.hidden !== true) {
      // this.hidden = fileAction.hidden == "1";
      // }
      // this.confirmMsg = fileAction.confirmMsg;
      // }
      // });
      return returnActions;
    },
    addAllowedActionInToolbar: function (fileAction) {
      this.allowedActionsInToolbar[fileAction] = fileAction;
    },
    // 添加其他要展示在视图工具栏上的按钮，不受动态权限控制
    addOtherActionInToolbar: function (fileAction, fileActionName) {
      this.otherActionsInToolbar.push({
        id: fileAction,
        name: fileActionName
      });
    },
    removeAllowedActionInToolbar: function (fileAction) {
      delete this.allowedActionsInToolbar[fileAction];
    },
    // 设置导航选择的夹UUID
    setSelectedFolderUuid: function (folderUuid) {
      this.selectedFolderUuid = folderUuid;
    },
    getSelectedFolderUuid: function () {
      return this.selectedFolderUuid;
    },
    // 当前夹——共享数据
    setCurrentFolderUuid: function (folderUuid) {
      this.currentFolderUuid = folderUuid;
    },
    getCurrentFolderUuid: function () {
      return this.currentFolderUuid;
    },
    getFolderConfiguration: function (folderUuid) {
      return this.dmsFileServices.getFolderConfiguration(folderUuid);
    },
    _createContentView: function (hasNav) {
      var _self = this;
      var $view = $('.file-manager-view', _self.element);
      if (!hasNav) {
        $view.css({ 'margin-left': '0px' });
      }
      _self.$view = $view;
      var configuration = _self.getConfiguration();
      var viewConfiguration = configuration.view || {};
      var listViewWidgetId = viewConfiguration.listViewId;
      var widgetDefinition = _self.options.widgetDefinition;
      var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
      if (listViewDefinition) {
        _self.listViewWidgetDefinition = listViewDefinition;
        _self._checkAndAddFileManagerListViewWidgetDevelopment(_self.listViewWidgetDefinition);
      }

      _self.currentDataViewType = viewConfiguration.dataViewType;
      // 判断是否支持视图切换
      if (_self.isSupportSwitchDataView()) {
        // 视图不允许字段查询
        var listViewConfiguration = listViewDefinition.configuration || {};
        var listViewQueryInfo = listViewConfiguration.query || {};
        listViewQueryInfo.fieldSearch = false;
        _self.currentDataViewType = viewConfiguration.defaultDataViewType;
      } else if (_self.isConfigMultiDataView() && !_self.isFileManagerDataStore(_self.listViewWidgetDefinition)) {
        _self.currentDataViewType = dataViewTypes.bootstrapTableDataView;
      }
      if (StringUtils.isBlank(_self.currentDataViewType)) {
        _self.currentDataViewType = dataViewTypes.fileManagerThumbnailView;
      }

      $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
        // 文件库引用的视图组件延时初始化
        if (_self.listViewWidgetDefinition != null && _self.listViewWidgetDefinition.id != childWidgetDefinition.id) {
          appContext.createWidget(childWidgetDefinition, widgetDefinition);
        }
      });
    },
    // 检查文件库使用的视图组件是否配置文件库视图二开，没有动态加入
    _checkAndAddFileManagerListViewWidgetDevelopment: function (listViewWidgetDefinition) {
      var _self = this;
      var listViewConfiguration = listViewWidgetDefinition.configuration || {};
      listViewConfiguration.dmsWidgetDefinition = _self.options.widgetDefinition;
      var listViewJsModule = listViewConfiguration.jsModule;
      var dyModule = 'DmsFileManagerListViewWidgetDevelopment';
      if (StringUtils.isBlank(listViewJsModule)) {
        listViewConfiguration.jsModule = dyModule;
      }
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
        var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, childWidgetDefinition.items);
        if (listViewDefinition) {
          return listViewDefinition;
        }
      }
      return null;
    },
    // 是否显示面包屑导航
    isShowBreadcrumbNav: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      return configuration.showBreadcrumbNav;
    },
    // 判断当前视图是否使用自定义的视图
    isUseCustomDataView: function () {
      var _self = this;
      var folderUuid = _self.getCurrentFolderUuid();
      var dataViewConfiguration = _self.dmsFileServices.getFolderDataViewConfiguration(folderUuid);
      var listViewId = dataViewConfiguration.listViewId;
      return StringUtils.isNotBlank(listViewId);
    },
    // 判断是否配置多视图
    isConfigMultiDataView: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      var viewConfiguration = configuration.view || {};
      return dataViewTypes[viewConfiguration.dataViewType] == null;
    },
    // 判断是否文件库的数据源
    isFileManagerDataStore: function (listViewWidgetDefinition) {
      var _self = this;
      var listViewConfiguration = null;
      if (listViewWidgetDefinition != null) {
        listViewConfiguration = listViewWidgetDefinition.configuration || {};
        return _self.dmsFileServices.isFileManagerDataStore(listViewConfiguration.dataStoreId);
      }

      if (_self.dataViewManager != null) {
        var dataViewOptions = _self.dataViewManager.getOptions();
        if (dataViewOptions && dataViewOptions.view && dataViewOptions.view.isFileManagerDataStore == true) {
          return true;
        }
      }

      if (_self.listViewWidgetDefinition != null) {
        listViewConfiguration = _self.listViewWidgetDefinition.configuration || {};
        return _self.dmsFileServices.isFileManagerDataStore(listViewConfiguration.dataStoreId);
      }
      return false;
    },
    // 是否使用自定义的文件库数据源
    setUseCustomFileManagerDataStore: function (useCustomFileManagerDataStore) {
      this.useCustomFileManagerDataStore = useCustomFileManagerDataStore;
    },
    isUseCustomFileManagerDataStore: function () {
      return this.useCustomFileManagerDataStore;
    },
    // 是否显示工具栏操作按钮
    setShowToolbarActions: function (showToolbarActions) {
      this.showToolbarActions = showToolbarActions;
    },
    isShowToolbarActions: function () {
      return this.showToolbarActions;
    },
    setCurrentDataViewType: function (dataViewType) {
      this.currentDataViewType = dataViewType;
    },
    getCurrentDataViewType: function () {
      return this.currentDataViewType;
    },
    // 判断是否支持视图切换
    isSupportSwitchDataView: function () {
      var _self = this;
      if (_self.listViewWidgetDefinition == null) {
        return false;
      }
      var isMultiDataView = _self.isConfigMultiDataView();
      // 多视图时判断
      // 1、视图数据源为数据管理_文件库(CD_DS_DMS_FILE_MANAGER)时，只查询强关联关系的数据，缩略、列表视图可切换；
      // 2、视图数据源非数据管理_文件库(CD_DS_DMS_FILE_MANAGER)时，查询强、弱关系的数据，只有列表视图
      if (isMultiDataView && _self.isFileManagerDataStore(_self.listViewWidgetDefinition)) {
        return true;
      }
      return false;
    },
    // 判断是否根夹
    isRootFolder: function (rootFolderUuid) {
      var _self = this;
      var rootFolder = _self.getRootFolder();
      return rootFolder.uuid == rootFolderUuid;
    },
    // 判断是否叶子夹
    isLeafFolder: function (folderUuid) {
      var _self = this;
      var treeId = _self.getFolderTreeId();
      var folderTree = $.fn.zTree.getZTreeObj(treeId);
      var nodes = folderTree.getNodesByParam('id', folderUuid);
      for (var i = 0; i < nodes.length; i++) {
        if (!nodes[i].isParent) {
          return true;
        }
      }
      return false;
    },
    // 获取配置的所属根目录
    getRootFolder: function () {
      var _self = this;
      if (_self.rootFolder) {
        return _self.rootFolder;
      }
      var configuration = _self.getConfiguration();
      var root = {
        uuid: configuration.belongToFolderUuid,
        name: configuration.rootFolderDisplayName || configuration.belongToFolderName
      };
      _self.rootFolder = root;
      return _self.rootFolder;
    },
    // 获取弹出框使用的根目录
    getRootFolderForDialog: function () {
      var _self = this;
      if (_self.rootFolderForDialog) {
        return _self.rootFolderForDialog;
      }
      return _self.getRootFolder();
    },
    // 设置弹出框使用的根目录
    setRootFolderForDialog: function (rootFolderForDialog) {
      this.rootFolderForDialog = rootFolderForDialog;
    },
    // 判断数据项是否支持在线预览
    isSupportOnlinePreview: function (fileItem) {
      return this.dmsFileServices.isSupportOnlinePreview(fileItem);
    },
    listFolderContent: function (folderUuid) {
      var _self = this;
      _self.determineDataView(folderUuid, function (dataView) {
        dataView.refresh(true);
      });
    },
    // 获取当前数据视图
    getCurrentDataView: function () {
      return this.dataViewManager;
    },
    // 获取选择的数据
    getSelection: function () {
      return this.getCurrentDataView().getSelection();
    },
    // 获取所有数据
    getData: function () {
      return this.getCurrentDataView().getData();
    },
    // 获取每页大小
    getPageSize: function () {
      return this.pageSize;
    },
    // 设置每页大小
    setPageSize: function (pageSize) {
      this.pageSize = pageSize;
    },
    // 添加参数
    addParam: function (key, value) {
      this.getCurrentDataView().addParam(key, value);
    },
    // 删除参数
    removeParam: function (key) {
      this.getCurrentDataView().removeParam(key);
    },
    // 添加额外的查询排序
    addOtherOrder: function (sortName, sortOrder) {
      this.getCurrentDataView().addOtherOrder(sortName, sortOrder);
    },
    // 清除额外查询排序,sortName为空是清楚全部，否则清楚等于sortName的一条额外查询排序
    clearOtherOrders: function (sortName) {
      this.getCurrentDataView().clearOtherOrders(sortName);
    },
    // 返回数据内容展示的视图
    determineDataView: function (folderUuid, callback) {
      var _self = this;
      // 复制视图配置
      var configuration = _self.getConfiguration();
      var view = $.extend(true, {}, configuration.view);
      view.listViewWidgetDefinition = _self.listViewWidgetDefinition;
      var options = {
        widget: _self,
        callback: callback,
        folderUuid: folderUuid,
        dataViewType: _self.getCurrentDataViewType(),
        view: view
      };
      // 使用夹配置的视图
      var folderDataViewConfiguration = _self.dmsFileServices.getFolderDataViewConfiguration(folderUuid);
      if (StringUtils.isNotBlank(folderDataViewConfiguration.listViewId)) {
        // 视图列表展示
        options.dataViewType = dataViewTypes.bootstrapTableDataView;
        options.view.listViewId = folderDataViewConfiguration.listViewId;
        options.view.isFileManagerDataStore = folderDataViewConfiguration.isFileManagerDataStore;
        var appWidgetDefinition = appContext.getWidgetDefinition(folderDataViewConfiguration.listViewId);
        options.view.listViewWidgetDefinition = JSON.parse(appWidgetDefinition.definitionJson);
        _self._checkAndAddFileManagerListViewWidgetDevelopment(options.view.listViewWidgetDefinition);
      }
      // 是否展开查询条件
      var isShowQueryFields = false;
      var dataViewManagers = _self.dataViewManagers || {};
      var dataViewManager = dataViewManagers[folderUuid];
      if (dataViewManager == null) {
        $.each(dataViewManagers, function (i, dataView) {
          if (dataView.currentView) {
            isShowQueryFields = dataView.currentView.isShowQueryFields();
          }
          dataView.destroy();
          delete dataViewManagers[i];
        });
        options.isShowQueryFields = isShowQueryFields;
        _self.dataViewManager = dataViewManagers[folderUuid] = new FileDataViewManager(options);
        _self.dataViewManagers = dataViewManagers;
      } else {
        dataViewManager.determineCallbackCurrentView(folderUuid, callback);
        _self.dataViewManager = dataViewManager;
      }
    },
    // 刷新
    refresh: function () {
      this.listFolderContent(this.getCurrentFolderUuid());
    },
    _bindEvents: function () {
      var _self = this;
      // 文件夹创建成功事件
      _self.on('wFileManager.FolderCreated', function (e, ui) {
        var parentFolderUuid = e.detail.parentFolderUuid;
        var newFolderUuid = e.detail.newFolderUuid;
        _self.refreshFolderTreeNode(parentFolderUuid);
      });
      // 文件（夹）重命名成功事件
      _self.on('wFileManager.ItemRenamed', function (e, ui) {
        var item = e.detail.item;
        _self.refreshFolderTreeParentNode(item.uuid);
      });
      // 文件（夹）删除成功事件
      _self.on('wFileManager.ItemDeleted', function (e, ui) {
        var selection = e.detail.selection;
        $.each(selection, function () {
          if (_self.dmsFileServices.isFolder(this)) {
            _self.refreshFolderTreeParentNode(this.uuid);
          }
        });
      });
      // 文件（夹）复制成功事件
      _self.on('wFileManager.ItemCopied', function (e, ui) {
        var source = e.detail.source;
        var destFolderUuid = e.detail.destFolderUuid;
        _self.refreshFolderTreeNode(destFolderUuid);
      });
      // 文件（夹）移动成功事件
      _self.on('wFileManager.ItemMoved', function (e, ui) {
        var source = e.detail.source;
        var destFolderUuid = e.detail.destFolderUuid;
        $.each(source, function () {
          if (_self.dmsFileServices.isFolder(this)) {
            _self.refreshFolderTreeParentNode(this.uuid);
          }
        });
        _self.refreshFolderTreeParentNode(destFolderUuid);
      });
      // 文件夹导航折叠/展开
      $(_self.element).on('click', '.folder-fold', function () {
        // ".file-manager-nav",
        if ($(this).hasClass('foldUp')) {
          $('.scroll-wrapper').show();
          $('.file-manager-view').animate(
            {
              marginLeft: '250px',
              borderLeft: '1px solid rgb(232, 232, 232)'
            },
            200
          );
          $(this).removeClass('foldUp').find('i').removeClass('icon-ptkj-youshouzhan').addClass('icon-ptkj-zuoshouzhan');
        } else {
          $('.scroll-wrapper').hide();
          $('.file-manager-view').animate(
            {
              marginLeft: '0',
              borderLeft: 'none'
            },
            200
          );
          $(this).addClass('foldUp').find('i').removeClass('icon-ptkj-zuoshouzhan').addClass('icon-ptkj-youshouzhan');
        }
      });
    }
  });

  // 文件管理视图
  var FileDataViewManager = function (options) {
    var _self = this;
    _self.options = options;
    _self.widget = options.widget;
    _self.currentFolderUuid = options.folderUuid;
    _self.views = {};

    _self.createDataView();
    _self.bindEvents();
  };
  $.extend(FileDataViewManager.prototype, {
    // 创建数据视图
    createDataView: function () {
      var _self = this;
      var dataViewType = _self.options.dataViewType || _self.widget.currentDataViewType;
      _self.currentView = _self._createView(dataViewType);
      return _self.currentView;
    },
    // 添加参数
    addParam: function (key, value) {
      this.currentView.addParam.apply(this.currentView, arguments);
    },
    // 删除参数
    removeParam: function (key) {
      this.currentView.removeParam.apply(this.currentView, arguments);
    },
    // 添加额外的查询排序
    addOtherOrder: function (sortName, sortOrder) {
      this.currentView.addOtherOrder.apply(this.currentView, arguments);
    },
    // 清除额外查询排序,sortName为空是清楚全部，否则清楚等于sortName的一条额外查询排序
    clearOtherOrders: function (sortName) {
      this.currentView.clearOtherOrders.apply(this.currentView, arguments);
    },
    refresh: function () {
      this.currentView.refresh.apply(this.currentView, arguments);
    },
    getSelection: function () {
      return this.currentView.getSelection.apply(this.currentView, arguments);
    },
    // 获取所有数据
    getData: function () {
      return this.currentView.getData.apply(this.currentView, arguments);
    },
    _createView: function (currentDataViewType, initOptions) {
      var _self = this;
      var view = null;
      var options = {
        dataViewManager: _self,
        widget: _self.widget,
        callback: _self.options.callback,
        view: _self.options.view,
        isShowQueryFields: _self.options.isShowQueryFields
      };
      options = $.extend(options, initOptions);
      if (currentDataViewType == dataViewTypes.bootstrapTableDataView) {
        view = new FileDataListView(options);
      } else if (currentDataViewType == dataViewTypes.fileManagerThumbnailView) {
        view = new DmsFileManagerThumbnailView(options);
      } else {
        console.error('unknow data view type: ' + currentDataViewType);
      }
      _self.views[currentDataViewType] = view;
      return view;
    },
    determineCallbackCurrentView: function (folderUuid, callback) {
      var _self = this;
      if ($.isFunction(callback)) {
        callback.call(_self.widget, _self.currentView);
      }
      return _self.currentView;
    },
    // 获取参数
    getOptions: function () {
      return this.options;
    },
    // 初始化视图内容
    init: function () {},
    destroy: function () {
      var _self = this;
      $(_self.widget.element).off('wFileManager.SwitchDataView');
      if (_self.currentView) {
        _self.currentView.destroy();
      }
      $(_self.widget.element).find('.file-manager-thumbnail-view').remove();
      $(_self.widget.element).find('.ui-wBootstrapTable').remove();
    },
    // 新建文件夹
    createFolder: function (e, ui) {},
    getView: function (currentViewType, options) {
      var _self = this;
      if (_self.views[currentViewType] == null) {
        _self._createView(currentViewType, options);
      } else {
        if (options) {
          _self.views[currentViewType].updateOptions(options);
        }
      }
      return _self.views[currentViewType];
    },
    bindEvents: function () {
      var _self = this;
      $(_self.widget.element).on('wFileManager.SwitchDataView', function (e, ui) {
        ui.hide();
        var switchToViewType = e.detail.switchToViewType;
        var view = _self.getView(switchToViewType, e.detail);
        _self.currentView = view;
        _self.widget.setCurrentDataViewType(switchToViewType);
        view.show();
        view.refresh();
      });
    }
  });

  // 文件管理列表视图
  var FileDataListView = function (options) {
    var _self = this;
    _self.dataViewManager = options.dataViewManager;
    _self.options = options;
    _self.widget = options.widget;
    _self._createView();
  };
  $.extend(FileDataListView.prototype, {
    // 初始化视图内容
    init: function () {},
    _createView: function () {
      var _self = this;
      var options = _self.options;
      var widgetDefinition = options.view.listViewWidgetDefinition;
      var listViewId = widgetDefinition.id;
      if ($('#' + listViewId).length == 0) {
        _self.widget.$view.append("<div id='" + listViewId + "'></div>");
      }
      // 初始化表格时，由外部传入的初始化查询文本
      widgetDefinition.configuration.initSearchText = options.keyword;
      appContext.createWidget(widgetDefinition, _self.widget.options.widgetDefinition, function () {
        this.dataViewManager = _self;
        $(this.element).data(KEY_DMS_ID, _self.widget.getDmsId());
        _self.listViewWidget = this;
        // 展开查询条件
        if (options.isShowQueryFields) {
          _self.listViewWidget.showQueryFields();
        }
      });
    },
    // 获取所有数据
    getData: function () {
      return this.listViewWidget.getDataProvider().getData();
    },
    getSelection: function () {
      return this.listViewWidget.getSelections();
    },
    updateOptions: function (newOptions) {
      var _self = this;
      var keyword = newOptions.keyword || '';
      _self.listViewWidget.setKeyword(keyword);
    },
    hide: function () {
      if (this.listViewWidget) {
        this.listViewWidget.hide();
      }
    },
    show: function () {
      if (this.listViewWidget) {
        this.listViewWidget.show();
      }
    },
    isShowQueryFields: function () {
      if (this.listViewWidget) {
        return this.listViewWidget.isShowQueryFields();
      }
      return false;
    },
    // 添加参数
    addParam: function (key, value) {
      if (this.listViewWidget) {
        this.listViewWidget.addParam.apply(this.listViewWidget, arguments);
      }
    },
    // 删除参数
    removeParam: function (key) {
      if (this.listViewWidget) {
        this.listViewWidget.removeParam.apply(this.listViewWidget, arguments);
      }
    },
    // 添加额外的查询排序
    addOtherOrder: function (sortName, sortOrder) {
      if (this.listViewWidget) {
        this.listViewWidget.addOtherOrder.apply(this.listViewWidget, arguments);
      }
    },
    // 清除额外查询排序,sortName为空是清楚全部，否则清楚等于sortName的一条额外查询排序
    clearOtherOrders: function (sortName) {
      if (this.listViewWidget) {
        this.listViewWidget.clearOtherOrders.apply(this.listViewWidget, arguments);
      }
    },
    refresh: function (force) {
      if (this.listViewWidget) {
        this.listViewWidget.refresh.apply(this.listViewWidget, arguments);
      }
      if (this.listViewWidget && this.listViewWidget.breadcrumbNav) {
        this.listViewWidget.breadcrumbNav.refresh();
      }
    },
    destroy: function () {
      var _self = this;
      if (_self.listViewWidget) {
        _self.listViewWidget.destroy();
        $(_self.listViewWidget.element).remove();
      }
    }
  });
});

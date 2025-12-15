define(['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal', 'DmsDataServices'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal,
  DmsDataServices
) {
  // 文件服务
  var dmsFileServiceUrl = ctx + '/dms/file/services';
  var FILE_MANAGER_DATA_STORE_ID = 'CD_DS_DMS_FILE_MANAGER';
  var Browser = commons.Browser;
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var FileDownloadUtils = server.FileDownloadUtils;
  var folderType = 'application/folder';
  var btnTpl = '<button type="button" class="btn {0}" btnId="{1}" action="{2}">{3}{4}</button>';
  var groupBtn = new StringBuilder();
  groupBtn.append('<button type="button" class="btn {0} dropdown-toggle" data-toggle="dropdown">{1}');
  groupBtn.append('<span class="caret"></span></button>');
  var groupBtnTpl = groupBtn.toString();
  // 导航选择的在工具栏可操作的权限
  var navInToolbarActions = [
    {
      id: 'createFolder',
      name: '新建文件夹'
    },
    {
      id: 'createFile',
      name: '上传文件'
    },
    {
      id: 'createDocument',
      name: '新建文档'
    }
  ];
  // 允许导航选择的在工具栏可操作的权限
  var navInToolbarOptActions = {
    createFolder: ['createFolder'],
    createFile: ['createFile'],
    createDocument: ['createDocument']
  };

  // 允许进行单个的处理的操作
  var toolbarSingleFolderOptActions = {
    share: ['shareFolder'],
    download: ['downloadFile'],
    delete: ['deleteFolder', 'forceDeleteFolder'],
    rename: ['renameFolder'],
    copy: ['copyFolder'],
    move: ['moveFolder'],
    viewAttributes: ['viewFolderAttributes']
  };
  var toolbarSingleFileOptActions = {
    share: ['shareFile'],
    download: ['downloadFile'],
    delete: ['deleteFile'],
    rename: ['renameFile'],
    copy: ['copyFile'],
    move: ['moveFile'],
    viewAttributes: ['viewFileAttributes']
  };
  var initToolbarSingleActionArrays = [
    {
      id: 'share',
      name: '分享'
    },
    {
      id: 'download',
      name: '下载'
    },
    {
      id: 'delete',
      name: '删除'
    },
    {
      id: 'rename',
      name: '重命名'
    },
    {
      id: 'copy',
      name: '复制到'
    },
    {
      id: 'move',
      name: '移动到'
    },
    {
      id: 'viewAttributes',
      name: '查看属性'
    }
  ];

  // 允许同时进行多个批量处理的操作
  var toolbarMultiFolderOptActions = {
    share: ['shareFolder'],
    download: ['downloadFile'],
    delete: ['deleteFolder', 'forceDeleteFolder'],
    copy: ['copyFolder'],
    move: ['moveFolder']
  };
  var toolbarMultiFileOptActions = {
    share: ['shareFile'],
    download: ['downloadFile'],
    delete: ['deleteFile'],
    copy: ['copyFile'],
    move: ['moveFile']
  };
  var initToolbarMultiActionArrays = [
    {
      id: 'share',
      name: '分享'
    },
    {
      id: 'download',
      name: '下载'
    },
    {
      id: 'delete',
      name: '删除'
    },
    {
      id: 'copy',
      name: '复制到'
    },
    {
      id: 'move',
      name: '移动到'
    }
  ];

  // Office 后缀
  var excelExts = ['ods', 'xls', 'xlsb', 'xlsm', 'xlsx'];
  var powerPointExts = ['odp', 'pot', 'potm', 'potx', 'pps', 'ppsm', 'ppsx', 'ppt', 'pptm', 'pptx'];
  var wordExts = ['doc', 'docm', 'docx', 'dot', 'dotm', 'dotx', 'odt', 'rtf'];
  var vsdExts = ['vsd', 'vsdx'];
  var pdfExts = ['pdf'];
  var archiveExts = ['zip', 'rar', 'tar'];
  var videoExts = ['asf', 'avi', 'flv', 'mov', 'mpg', 'mpeg', 'mp4', 'qt', 'smil', 'swf', 'wmv', '3g2', '3gp'];
  var audioExts = ['aif', 'aac', 'au', 'gsm', 'mid', 'midi', 'mov', 'mp3', 'm4a', 'snd', 'rm', 'wav', 'wma'];
  var textExts = [
    'txt',
    'text',
    'mhtml',
    'html',
    'htm',
    'xml',
    'css',
    'js',
    'json',
    'sql',
    'log',
    'properties',
    'yaml',
    'yml',
    'bat',
    'sh',
    'md',
    'java',
    'mine',
    'groovy',
    'jsp'
  ];
  var imgExts = ['gif', 'png', 'jpg', 'jpeg', 'tif', 'bmp'];
  var zipExts = ['zip', 'rar', '7z'];
  var h5MediaExts = ['mp3', 'ogg', 'mp4', 'webm'];

  var DmsFileServices = function () {
    this.dmsDataServices = new DmsDataServices();
    this.dmsDataServices.setServiceUrl(dmsFileServiceUrl);
    this.fileActionMap = {};
    this.folderConfigurations = {};
    this.folderDataViewConfigurations = {};
    this.fileManagerDataStores = {};
  };
  $.extend(DmsFileServices.prototype, {
    // 判断文件项本身是否允许打开
    isAllowOpen: function (fileItem) {
      return !(fileItem.isAllowOpen === false);
    },
    // 判断是否为文件夹
    isFolder: function (fileItem) {
      return 'application/folder' == fileItem.contentType;
    },
    // 判断是否为表单
    isDyform: function (fileItem) {
      return 'application/dyform' == fileItem.contentType;
    },
    // 判断是否为文件
    isFile: function (fileItem) {
      if (StringUtils.isBlank(fileItem.contentType)) {
        return false;
      }
      return !(this.isFolder(fileItem) || this.isDyform(fileItem));
    },
    // 判断文件是否可下载
    isDownloadable: function (fileItem) {
      var _self = this;
      if (_self.fileTypeDownloadableMap == null) {
        JDS.call({
          service: 'dmsFileManagerService.getFileTypeInfos',
          data: [],
          async: false,
          success: function (result) {
            var fileTypeInfo = result.data;
            _self.fileTypeDownloadableMap = fileTypeInfo.downloadableMap;
          }
        });
      }
      var downloadable = _self.fileTypeDownloadableMap[fileItem.contentType];
      if (downloadable != null) {
        return Boolean(downloadable);
      } else {
        var extension = _self.getFileExtension(fileItem.name);
        downloadable = _self.fileTypeDownloadableMap[extension];
        if (downloadable != null) {
          return Boolean(downloadable);
        }
      }
      return true;
    },
    isExcel: function (fileInfo) {
      return this.isMatchExts(fileInfo, excelExts);
    },
    isPowerPoint: function (fileInfo) {
      return this.isMatchExts(fileInfo, powerPointExts);
    },
    isWord: function (fileInfo) {
      return this.isMatchExts(fileInfo, wordExts);
    },
    isVsd: function (fileInfo) {
      return this.isMatchExts(fileInfo, vsdExts);
    },
    isPdf: function (fileInfo) {
      return this.isMatchExts(fileInfo, pdfExts);
    },
    isArchive: function (fileInfo) {
      return this.isMatchExts(fileInfo, archiveExts);
    },
    isVideo: function (fileInfo) {
      return this.isMatchExts(fileInfo, videoExts);
    },
    isAudio: function (fileInfo) {
      return this.isMatchExts(fileInfo, audioExts);
    },
    isText: function (fileInfo) {
      return this.isMatchExts(fileInfo, textExts);
    },
    isImage: function (fileInfo) {
      return this.isMatchExts(fileInfo, imgExts);
    },
    isZip: function (fileInfo) {
      return this.isMatchExts(fileInfo, zipExts);
    },
    isH5Media: function (fileInfo) {
      return this.isMatchExts(fileInfo, h5MediaExts);
    },
    isMatchExts: function (fileInfo, exts) {
      // flv, mp3, swf mediaplayer.swf?file=flash/curtain.flv
      if (fileInfo == null || StringUtils.isBlank(fileInfo.name)) {
        return false;
      }
      var lfn = fileInfo.name.toLowerCase();
      var len = lfn.length;
      var start = lfn.lastIndexOf('.');
      if (start !== -1) {
        var ext = lfn.substring(start + 1, len);
        for (var i = 0; i < exts.length; i++) {
          if (ext === exts[i]) {
            return true;
          }
        }
      }
      return false;
    },
    getFileBaseName: function (filename) {
      var lfn = filename.toLowerCase();
      var len = lfn.length;
      var start = lfn.lastIndexOf('.');
      if (start !== -1) {
        var basename = lfn.substring(0, start);
        return basename;
      }
      return filename;
    },
    getFileExtension: function (filename) {
      var lfn = filename.toLowerCase();
      var len = lfn.length;
      var start = lfn.lastIndexOf('.');
      if (start !== -1) {
        var ext = lfn.substring(start + 1, len);
        return ext;
      }
      return '';
    },
    // 获取查看文件的信息
    getViewFileInfo: function (dmsFile, success, failure) {
      JDS.call({
        service: 'dmsFileManagerService.getViewFileInfo',
        data: [dmsFile],
        async: false,
        success: function (result) {
          if ($.isFunction(success)) {
            success.apply(this, arguments);
          }
        },
        error: function () {
          if ($.isFunction(failure)) {
            failure.apply(this, arguments);
          }
        }
      });
    },
    isValidFileName: function (fileName) {
      var isValid = false;
      JDS.call({
        service: 'dmsFileManagerService.isValidFileName',
        data: [fileName],
        async: false,
        success: function (result) {
          isValid = result.data;
        }
      });
      return isValid;
    },
    isSupportOnlinePreview: function (fileItem) {
      var _self = this;
      return (
        _self.isFolder(fileItem) ||
        _self.isDyform(fileItem) ||
        _self.isExcel(fileItem) ||
        _self.isPowerPoint(fileItem) ||
        _self.isWord(fileItem) ||
        _self.isPdf(fileItem) ||
        _self.isText(fileItem) ||
        _self.isImage(fileItem)
      );
    },
    getFolderListViewId: function (folderUuid) {
      var listViewId = null;
      JDS.call({
        service: 'dmsFileManagerService.getFolderListViewId',
        data: [folderUuid],
        async: false,
        success: function (result) {
          listViewId = result.data;
        }
      });
      return listViewId;
    },
    getFolderConfiguration: function (folderUuid) {
      var _self = this;
      if (StringUtils.isBlank(folderUuid)) {
        return {};
      }
      var folderConfigurations = _self.folderConfigurations;
      if (folderConfigurations[folderUuid] == null) {
        JDS.call({
          service: 'dmsFileManagerService.getFolderConfiguration',
          data: [folderUuid],
          async: false,
          success: function (result) {
            folderConfigurations[folderUuid] = result.data;
          }
        });
      }
      return folderConfigurations[folderUuid];
    },
    clearFolderConfiguration: function () {
      this.folderConfigurations = {};
    },
    // 获取夹视图配置
    getFolderDataViewConfiguration: function (folderUuid) {
      var _self = this;
      if (StringUtils.isBlank(folderUuid)) {
        return {};
      }
      var dataViewConfigurations = _self.folderDataViewConfigurations;
      if (dataViewConfigurations[folderUuid] == null) {
        JDS.call({
          service: 'dmsFileManagerService.getFolderDataViewConfiguration',
          data: [folderUuid],
          async: false,
          success: function (result) {
            dataViewConfigurations[folderUuid] = result.data;
          }
        });
      }
      return dataViewConfigurations[folderUuid];
    },
    // 判断是否文件库的数据源
    isFileManagerDataStore: function (dataStoreId) {
      var _self = this;
      if (dataStoreId == FILE_MANAGER_DATA_STORE_ID) {
        return true;
      }
      var fileManagerDataStores = _self.fileManagerDataStores;
      if (fileManagerDataStores[dataStoreId] == null) {
        JDS.call({
          service: 'dmsFileManagerService.isFileManagerDataStore',
          data: [dataStoreId],
          async: false,
          version: '',
          success: function (result) {
            fileManagerDataStores[dataStoreId] = result.data;
          }
        });
      }
      return fileManagerDataStores[dataStoreId];
    },
    listFolder: function (options) {
      var _self = this;
      _self.fillAction('listFolder', options);
      _self.dmsDataServices.performed(options);
    },
    listAllFolderAndFiles: function (options) {
      var _self = this;
      _self.fillAction('listAllFolderAndFiles', options);
      _self.dmsDataServices.performed(options);
    },
    createFolder: function (options) {
      var _self = this;
      var data = options.data;
      JDS.call({
        service: 'dmsFileManagerService.createFolder',
        data: [data.data.uuid, data.data.name, data.folderUuid],
        mask: true,
        success: function (result) {
          if ($.isFunction(options.success)) {
            options.success.apply(this, arguments);
          }
        }
      });
    },
    readFolder: function (options) {
      var _self = this;
      _self.fillAction('readFolder', options);
      _self.dmsDataServices.performed(options);
    },
    readFileContentAsString: function (options) {
      JDS.call({
        service: 'dmsFileManagerService.readFileContentAsString',
        data: [options.fileUuid],
        mask: true,
        success: function (result) {
          if ($.isFunction(options.success)) {
            options.success.apply(this, arguments);
          }
        }
      });
    },
    renameFolder: function (options) {
      var _self = this;
      var data = options.data;
      JDS.call({
        service: 'dmsFileManagerService.renameFolder',
        data: [data.folderUuid, data.newFolderName],
        mask: true,
        success: function (result) {
          if ($.isFunction(options.success)) {
            options.success.apply(this, arguments);
          }
        }
      });
    },
    renameFile: function (options) {
      var _self = this;
      var data = options.data;
      JDS.call({
        service: 'dmsFileManagerService.renameFile',
        mask: true,
        data: [data.fileUuid, data.newFileName],
        success: function (result) {
          if ($.isFunction(options.success)) {
            options.success.apply(this, arguments);
          }
        }
      });
    },
    // 移动
    move: function (options) {
      options.title = '移动到';
      options.onOk = function (dlgTreeId) {
        var folderTree = $.fn.zTree.getZTreeObj(dlgTreeId);
        var nodes = folderTree.getSelectedNodes();
        if (nodes.length !== 1) {
          appModal.error('请选择要移动到的夹！');
          return false;
        }
        var selection = options.selection;
        var destNode = nodes[0];
        JDS.call({
          service: 'dmsFileManagerService.moveFile',
          data: [selection, destNode.id],
          mask: true,
          success: function (result) {
            if ($.isFunction(options.callback)) {
              options.callback.call(this, selection, destNode.id);
            }
          }
        });
      };
      showFolderTreeDialog(options);
    },
    // 复制
    copy: function (options) {
      options.title = '复制到';
      options.onOk = function (dlgTreeId) {
        var folderTree = $.fn.zTree.getZTreeObj(dlgTreeId);
        var nodes = folderTree.getSelectedNodes();
        if (nodes.length !== 1) {
          appModal.error('请选择要复制到的夹！');
          return false;
        }
        var selection = options.selection;
        var destNode = nodes[0];
        JDS.call({
          service: 'dmsFileManagerService.copyFile',
          data: [selection, destNode.id],
          mask: true,
          success: function (result) {
            if ($.isFunction(options.callback)) {
              options.callback.call(this, selection, destNode.id);
            }
          }
        });
      };
      showFolderTreeDialog(options);
    },
    // 显示选择夹弹出框
    showChooseFolderDialog: function (options) {
      showFolderTreeDialog(options);
    },
    // 查看属性
    viewAttributes: function (options) {
      var _self = this;
      options.isJsModule = true;
      options.jsModule = 'DmsFileViewAttributes';
      options.dmsFileServices = _self;
      appContext.startApp(options);
    },
    deleteFile: function (options) {
      var _self = this;
      var service = 'dmsFileManagerService.deleteFile';
      if (options.physicalDelete) {
        service = 'dmsFileManagerService.physicalDeleteFile';
        JDS.call(
          $.extend(
            {
              service: service,
              mask: true
            },
            options
          )
        );
      } else {
        JDS.call(
          $.extend(
            {
              service: service,
              mask: true
            },
            options
          )
        );
      }
    },
    fillAction: function (action, options) {
      options.data.action = action;
    },
    // 获取多个文件可同时进行批量处理的操作
    getIntersectionActions: function (fileItems) {
      var _self = this;
      var toLoadActions = [];
      var downloadable = true;
      $.each(fileItems, function (i, item) {
        if (StringUtils.isBlank(item.uuid)) {
          return;
        }
        downloadable = _self.isDownloadable(item);
        if (_self.fileActionMap[item.uuid] == null) {
          toLoadActions.push(item);
        }
      });
      if (toLoadActions.length > 0) {
        var data = _self.getFileActions(toLoadActions);
        for (var key in data) {
          _self.fileActionMap[key] = data[key];
        }
      }
      var actionArrays = [];
      // 只选择一个文件，返回该文件的所有操作
      if (fileItems.length == 1) {
        actionArrays = $.extend(true, [], initToolbarSingleActionArrays);
        var toolbarSingleOptActions = _self.getToolbarSingleOptActions(fileItems[0]);
        actionArrays = _self.filterToolbarActions(actionArrays, _self.fileActionMap[fileItems[0].uuid], toolbarSingleOptActions);
      } else if (fileItems.length > 1) {
        var intersectionActions = _self.fileActionMap[fileItems[0].uuid];
        actionArrays = $.extend(true, [], initToolbarMultiActionArrays);
        var toolbarMultiOptActions = _self.getToolbarMultiOptActions(fileItems[0]);
        intersectionActions = _self.filterToolbarActions(actionArrays, intersectionActions, toolbarMultiOptActions);
        for (var i = 1; i < fileItems.length; i++) {
          var fileItem = fileItems[i];
          toolbarMultiOptActions = _self.getToolbarMultiOptActions(fileItem);
          actionArrays = _self.filterToolbarActions(actionArrays, _self.fileActionMap[fileItem.uuid], toolbarMultiOptActions);
        }
      }
      // 动态表单不可下载
      if (!downloadable) {
        $.each(actionArrays, function (i) {
          if ('download' == this.id) {
            this.hidden = true;
          }
        });
      }
      return actionArrays;
    },
    getToolbarSingleOptActions: function (fileItem) {
      var _self = this;
      if (_self.isFolder(fileItem)) {
        return toolbarSingleFolderOptActions;
      }
      return toolbarSingleFileOptActions;
    },
    getToolbarMultiOptActions: function (fileItem) {
      var _self = this;
      if (_self.isFolder(fileItem)) {
        return toolbarMultiFolderOptActions;
      }
      return toolbarMultiFileOptActions;
    },
    hasFolderPermission: function (folderUuid, fileAction) {
      var _self = this;
      var folderActions = _self.fileActionMap[folderUuid];
      if (folderActions == null) {
        folderActions = _self.getFolderActions({
          data: {
            folderUuid: folderUuid
          },
          async: false
        });
      }
      for (var i = 0; i < folderActions.length; i++) {
        if (folderActions[i].id == fileAction) {
          return true;
        }
      }
      return false;
    },
    getFolderActions: function (options) {
      var _self = this;
      var folderUuid = options.data.folderUuid;
      var success = options.success;
      if (StringUtils.isBlank(folderUuid)) {
        var result = {
          dataList: []
        };
        success.call(_self, result);
        return result.dataList;
      }
      if (_self.fileActionMap[folderUuid] != null) {
        var result = {
          dataList: _self.fileActionMap[folderUuid]
        };
        success.call(_self, result);
        return result.dataList;
      }

      options.success = function (result) {
        _self.fileActionMap[folderUuid] = result.dataList;
        if (success) {
          success.apply(this, arguments);
        }
        return result;
      };
      _self.fillAction('getFolderActions', options);
      var result = _self.dmsDataServices.performed(options);
      if (result && result.dataList) {
        return result.dataList;
      }
      return [];
    },
    hasFilePermission: function (fileUuid, fileAction) {
      var _self = this;
      var fileActions = _self.fileActionMap[fileUuid];
      if (fileActions == null) {
        var result = _self.getFileActions([
          {
            uuid: fileUuid
          }
        ]);
        for (var key in result) {
          _self.fileActionMap[key] = result[key];
        }
        _self.fileActionMap[fileUuid] = result[fileUuid];
        fileActions = _self.fileActionMap[fileUuid] || [];
      }
      for (var i = 0; i < fileActions.length; i++) {
        if (fileActions[i].id == fileAction) {
          return true;
        }
      }
      return false;
    },
    getFileActions: function (fileItems) {
      var _self = this;
      var fileUuids = [];
      var folderUuids = [];
      $.each(fileItems, function (i, item) {
        if (_self.isFolder(item)) {
          folderUuids.push(item.uuid);
        } else {
          fileUuids.push(item.uuid);
        }
      });
      // 文件操作
      var options = {
        data: {
          action: 'getFileActions',
          fileUuids: fileUuids,
          folderUuids: folderUuids
        },
        async: false
      };
      var result = _self.dmsDataServices.performed(options);
      if (result == null) {
        return {};
      }
      return result.data.data || result.data;
    },
    // 过滤出允许显示在工具栏上的操作
    filterToolbarActions: function (actionArray, fileActions, optActions) {
      var allowKeys = {};
      $.each(fileActions, function (i, fileAction) {
        var actionId = fileAction.id;
        for (var key in optActions) {
          var array = optActions[key];
          $.each(array, function (j, action) {
            if (actionId === action) {
              allowKeys[key] = fileAction;
            }
          });
        }
      });
      $.each(actionArray, function (i, action) {
        if (!allowKeys.hasOwnProperty(action.id)) {
          action.hidden = true;
        } else if (allowKeys[action.id]) {
          action.isCreator = allowKeys[action.id].isCreator;
        }
      });
      return actionArray;
    }, // 过滤出允许显示在工具栏上的操作
    filterNavInToolbarActions: function (fileActions) {
      var actionArray = $.extend(true, [], navInToolbarActions);
      var allowKeys = {};
      $.each(fileActions, function (i, fileAction) {
        var actionId = fileAction.id;
        for (var key in navInToolbarOptActions) {
          var array = navInToolbarOptActions[key];
          $.each(array, function (j, action) {
            if (actionId === action) {
              allowKeys[key] = key;
            }
          });
        }
      });
      $.each(actionArray, function (i, action) {
        if (!allowKeys.hasOwnProperty(action.id)) {
          action.hidden = true;
        }
      });
      return actionArray;
    },
    buildToolbarHtml: function (fileActions) {
      var displayActions = [];
      var buttonGroup = {};
      var buttonGroupCss = {};
      var otherGroups = [];
      $.each(fileActions, function (i, action) {
        if (action == null || action.hidden) {
          return;
        }
        var group = action.group;
        if (action.items) {
          otherGroups.push(action);
        } else if (StringUtils.isBlank(group) || group == 'undefined') {
          displayActions.push(action);
        } else {
          // 分组取第一个按钮的样式
          if (!buttonGroup[group]) {
            buttonGroup[group] = [];
            buttonGroupCss[group] = action.cssClass;
          }
          delete action['cssClass'];
          buttonGroup[group].push(action);
        }
      });
      // 分组按钮加入显示的操作
      $.each(fileActions, function (i, action) {
        var group = action.group;
        if (StringUtils.isNotBlank(group) && buttonGroup[group]) {
          displayActions.push({
            name: group,
            cssClass: buttonGroupCss[group],
            items: buttonGroup[group]
          });
          delete buttonGroup[group];
        }
      });
      displayActions = displayActions.concat(otherGroups);
      var toolbarHtml = new StringBuilder();
      toolbarHtml.append('<div class="btn-group btn-file-manager-group">');
      var baseBtnArr = displayActions;
      var moreBtnArr = []; // displayActions.slice(5,
      // displayActions.length);
      var dropDownmenuArr = [];
      $.each(baseBtnArr, function (i, data) {
        var cssClass = data.cssClass || data.id;
        var btnId = data.id;
        var action = data.action || btnId;
        var iconTpl = '';
        if (data.icon) {
          iconTpl = '<i class="' + data.icon.className + '"></i>';
        }
        if (data.items == null) {
          if (data.btnLib) {
            if ($.inArray(data.btnLib.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
              if (data.btnLib.iconInfo) {
                toolbarHtml.appendFormat(
                  '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}" btnId="{6}" action="{7}"><i class="{4}"></i>{5}</button>',
                  data.btnLib.btnColor,
                  data.btnLib.btnInfo['class'],
                  data.btnLib.btnSize,
                  data.code,
                  data.btnLib.iconInfo.fileIDs,
                  data.name,
                  btnId,
                  action
                );
              } else {
                toolbarHtml.appendFormat(
                  '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}" btnId="{5}" action="{6}">{4}</button>',
                  data.btnLib.btnColor,
                  data.btnLib.btnInfo['class'],
                  data.btnLib.btnSize,
                  data.code,
                  data.name,
                  btnId,
                  action
                );
              }
            } else {
              if (data.btnLib.btnInfo.icon) {
                toolbarHtml.appendFormat(
                  '<button type="button" class="well-btn {0} {1} btn_class_{2}" btnId="{5}" action="{6}"><i class="{3}"></i>{4}</button>',
                  data.btnLib.btnInfo['class'],
                  data.btnLib.btnSize,
                  data.code,
                  data.btnLib.btnInfo.icon,
                  data.btnLib.btnInfo.text,
                  btnId,
                  action
                );
              } else {
                toolbarHtml.appendFormat(
                  '<button type="button" class="well-btn {0} {1} btn_class_{2}" btnId="{4}" action="{5}">{3}</button>',
                  data.btnLib.btnInfo['class'],
                  data.btnLib.btnSize,
                  data.code,
                  data.btnLib.btnInfo.text,
                  btnId,
                  action
                );
              }
            }
          } else {
            toolbarHtml.appendFormat(btnTpl, cssClass, btnId, action, iconTpl, data.name);
          }
        } else {
          dropDownmenuArr.push(data);
        }
      });
      if (moreBtnArr.length != 0) {
        groupBtnTpl =
          '<button class="well-btn w-btn-primary w-line-btn dropdown-toggle" data-toggle="dropdown" title="{0}" type="button" aria-expanded="false"><span>{0}</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></button>';
        toolbarHtml.append('<div class="btn-group">');
        toolbarHtml.appendFormat(groupBtnTpl, '更多操作');
        toolbarHtml.append('<ul class="dropdown-menu w-btn-dropMenu" role="menu">');
        $.each(moreBtnArr, function (i, data) {
          var cssClass = data.cssClass || data.id;
          var btnId = data.id;
          var action = data.action || btnId;
          var iconTpl = '';
          var liTpl = '<li class="{0}" btnId="{1}" action="{2}"><a href="#">{3}{4}</a></li>';
          if (data.icon) {
            iconTpl = '<i class="' + data.icon.className + '"></i>';
          }
          if (data.items == null) {
            toolbarHtml.appendFormat(liTpl, '', btnId, action, iconTpl, data.name);
          } else {
            dropDownmenuArr.push(data);
          }
        });
        toolbarHtml.append('</ul>');
        toolbarHtml.append('</div>');
      }
      if (dropDownmenuArr.length != 0) {
        $.each(dropDownmenuArr, function (i, data) {
          groupBtnTpl =
            '<button class="well-btn w-btn-primary w-line-btn dropdown-toggle" data-toggle="dropdown" title="{0}" type="button" aria-expanded="false"><span>{0}</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></button>';
          toolbarHtml.append('<div class="btn-group">');
          toolbarHtml.appendFormat(groupBtnTpl, data.name);
          toolbarHtml.append('<ul class="dropdown-menu w-btn-dropMenu" role="menu">');
          $.each(data.items, function (i, item) {
            if (item.divider) {
              toolbarHtml.appendFormat('<li class="divider"></li>');
            } else {
              var liTpl = '<li class="{0}" btnId="{1}" action="{2}"><a href="#">{3}{4}</a></li>';
              cssClass = item.cssClass || item.id;
              btnId = item.id;
              action = item.action || btnId;
              iconTpl = '';
              if (item.icon) {
                iconTpl = '<i class="' + item.icon.className + '"></i>';
              }
              toolbarHtml.appendFormat(liTpl, cssClass, btnId, action, iconTpl, item.name);
            }
          });
          toolbarHtml.append('</ul>');
          toolbarHtml.append('</div>');
        });
      }
      toolbarHtml.append('</div>');
      return toolbarHtml.toString();
    },
    uploadFile: function (options) {
      var folderUuid = options.folderUuid;
      var uploadFileSelector = '#div_upload_file_' + folderUuid;
      var $container = $(options.ui.element);
      var fileuploaddone = options.fileuploaddone;
      var $divUploadFile = $(uploadFileSelector, $container);
      if ($divUploadFile.length == 0) {
        var sb = new StringBuilder();
        sb.appendFormat('<div id="div_upload_file_{0}" class="hidden">', folderUuid);
        sb.append('<input id="upload_file" multiple="multiple" type="file" />');
        sb.append('</div>');
        $container.append(sb.toString());
        $divUploadFile = $(uploadFileSelector, $container);
        var savefiles = getBackendUrl() + '/dms/file/upload';
        $('#upload_file', $divUploadFile)
          .fileupload({
            url: savefiles,
            autoUpload: true,
            formData: {
              folderUuid: folderUuid
            },
            dropZone: null,
            pasteZone: null,
            sequentialUploads: true,
            maxFileSize: parseInt(SystemParams.getValue('file.max.upload.size')) * 1024 * 1024
          })
          .on('fileuploadadd', function (e, data) {
          })
          .on('fileuploadprogress', function (e, data) {
          })
          .on('fileuploaddone', function (e, data) {
            if ($.isFunction(fileuploaddone)) {
              fileuploaddone.apply(options.ui, arguments);
            }
            appModal.info('文件上传成功！');
          })
          .on('fileuploadfail', function (e, data) {
            appModal.info('文件上传失败！');
          });
      }
      $('#upload_file', $divUploadFile).trigger('click');
    },
    // 新建文档
    createDocument: function (options) {
      var _self = this;
      // 指定创建的表单UUID
      var formUuid = options.formUuid || '';
      var isConfigDocumentForCreate = false;
      if (StringUtils.isNotBlank(formUuid)) {
        isConfigDocumentForCreate = true;
      } else {
        JDS.call({
          service: 'dmsFileManagerService.isConfigDocumentForCreate',
          data: [options.folderUuid],
          async: false,
          success: function (result) {
            isConfigDocumentForCreate = result.data;
          }
        });
      }
      if (isConfigDocumentForCreate) {
        var urlParams = {
          idKey: 'UUID',
          idValue: '',
          dms_id: $.isFunction(options.ui.getDmsId) ? options.ui.getDmsId() : options.ui.getId(),
          ac_id: 'btn_file_manager_dyform_create',
          fd_id: options.folderUuid,
          doc_def_uuid: formUuid
        };
        var dataServices = new DmsDataServices();
        dataServices.openWindow({
          urlParams: urlParams,
          useUniqueName: false,
          ui: options.ui
        });
      } else {
        appModal.error('夹文档配置有问题，不能新建文档！');
      }
    },
    // 打开表单数据
    openDyform: function (options) {
      var _self = this;
      var urlParams = {
        idKey: options.idKey || 'UUID',
        idValue: options.idValue || options.data.dataUuid,
        dms_id: $.isFunction(options.ui.getDmsId) ? options.ui.getDmsId() : options.ui.getId(),
        ac_id: 'btn_file_manager_dyform_create',
        fd_id: options.folderUuid,
        doc_id: options.fileUuid || '',
        doc_def_uuid: options.data.dataDefUuid || options.data.formUuid || options.data.FORM_UUID || ''
      };
      var dataServices = new DmsDataServices();
      var useUniqueName = StringUtils.isNotBlank(urlParams.doc_id);
      dataServices.openWindow({
        urlParams: urlParams,
        useUniqueName: useUniqueName,
        ui: options.ui
      });
    },
    // 分享
    share: function (fileItems) {
      var _self = this;
      appContext.require(['multiOrg'], function (unit2) {
        $.unit2.open({
          valueField: '',
          labelField: '',
          title: '选择分享人',
          type: 'all',
          valueFormat: 'justId',
          callback: function (values, labels) {
            if (values.length > 0) {
              var ids = values.join(';');
              var names = labels.join(';');
              JDS.call({
                service: 'dmsFileManagerService.shareFile',
                data: [fileItems, ids, names],
                mask: true,
                success: function (result) {
                  appModal.info('分享成功！');
                }
              });
            }
          }
        });
      });
    },
    // 取消分享
    cancelShare: function (options) {
      var _self = this;
      appModal.confirm('确认要取消分享吗！', function (result) {
        if (result) {
          JDS.call({
            service: 'dmsFileManagerService.cancelShareFile',
            data: [options.selection],
            mask: true,
            success: function (result) {
              if (options.success) {
                options.success.apply(this, arguments);
              }
            }
          });
        }
      });
    },
    // 下载
    download: function (fileItems) {
      var _self = this;
      var fileUuids = [];
      var folderUuids = [];
      $.each(fileItems, function (i, fileItem) {
        if (_self.isFolder(fileItem)) {
          folderUuids.push(fileItem.uuid);
        } else {
          fileUuids.push(fileItem.uuid);
        }
      });
      // 检查文件对应的MONGO数据是否存在
      var fileDataExists = true;
      if (fileUuids.length > 0) {
        JDS.call({
          service: 'dmsFileManagerService.checkFileDataExists',
          data: [fileUuids],
          async: false,
          success: function (result) {
            fileDataExists = result.data;
          }
        });
        if (fileDataExists == false) {
          appModal.error('文件数据不存在，无法下载！');
          return;
        }
      }
      var downloadUrl = getBackendUrl() + '/api/dms/file/download?fileUuid=' + fileUuids.join(';') + '&folderUuid=' + folderUuids.join(';');
      window.location.href = downloadUrl;
    },
    // 检查同一目录下文件名是否重复
    checkTheSameNameForCreateFolder: function (folderName, parentFolderUuid) {
      var _self = this;
      var isValid = false;
      JDS.call({
        service: 'dmsFileManagerService.checkTheSameNameForCreateFolder',
        data: [folderName, parentFolderUuid],
        async: false,
        success: function (result) {
          isValid = result.data;
        }
      });
      return isValid;
    }
  });

  // 显示文件移动、复制弹出框
  function showFolderTreeDialog(options) {
    var rootFolderUuid = options.rootFolderUuid;
    var rootFolderName = options.rootFolderName;
    var title = options.title;
    var onOk = options.onOk;
    var dlgId = UUID.createUUID();
    var dlgTreeId = 'folder_tree_' + dlgId;
    var dlgIdSelector = '#' + dlgId;
    var dlgTreeIdSelector = '#' + dlgTreeId;
    var message = new StringBuilder();
    message.appendFormat('<div id="{0}" class="folder-tree-container">', dlgId);
    message.appendFormat('<div class="folder-search-bar clearfix">');
    message.appendFormat('	<div class="input-group pull-right">');
    message.appendFormat('		<input type="search" class="form-control" aria-label="...">');
    message.appendFormat('		<div class="input-group-btn"><button class="btn btn-primary">');
    message.appendFormat('		<i class="fa fa-search"></i></button></div>');
    message.appendFormat('	</div>');
    message.appendFormat('</div>');
    message.appendFormat('<ul class="folder-tree ztree" id="{0}">', dlgTreeId);
    message.appendFormat('</ul>');
    message.appendFormat('<div class="no-records-found hidden">没有找到匹配的记录</div>');
    message.appendFormat('</div>');
    var dialogOptions = {
      title: title,
      size: 'middle',
      message: message.toString(),
      shown: function () {
        // 查询处理
        $('.folder-search-bar', dlgIdSelector).on('click', 'button', function () {
          $.fn.zTree.destroy(dlgTreeId);
          initTree();
        });
        $('.folder-search-bar', dlgIdSelector).on('keydown', 'input', function (e) {
          if (e.keyCode == 13) {
            $.fn.zTree.destroy(dlgTreeId);
            initTree();
          }
        });
        var initTree = function () {
          var treeSetting = {
            view: {
              showLine: true,
              selectedMulti: false
            },
            callback: {
              onAsyncSuccess: function (event, treeId, treeNode, msg) {
                var folderTree = $.fn.zTree.getZTreeObj(treeId);
                var nodes = folderTree.getNodes();
                var keyword = $('.folder-search-bar input', dlgIdSelector).val();
                if (StringUtils.isBlank(keyword)) {
                  if (nodes.length > 0 && treeNode == null) {
                    if (StringUtils.isNotBlank(rootFolderName)) {
                      nodes[0].name = rootFolderName;
                      folderTree.updateNode(nodes[0]);
                    }
                    // 配置选中默认节点
                    if (options.selectNode) {
                      var node = folderTree.getNodeByParam('id', options.selectNode);
                      folderTree.selectNode(node);
                    } else {
                      folderTree.selectNode(nodes[0]);
                    }
                    folderTree.expandNode(nodes[0], true, false, false, true);
                  }
                  $('.no-records-found', dlgIdSelector).addClass('hidden');
                } else if (nodes.length == 0) {
                  $('.no-records-found', dlgIdSelector).removeClass('hidden');
                }
              }
            },
            async: {
              enable: true,
              dataType: 'json',
              autoParam: ['id=parentFolderUuid'],
              otherParam: {
                withoutPermission: options.withoutPermission,
                belongToFolderUuid: rootFolderUuid,
                checkIsParent: true,
                keyword: $('.folder-search-bar input', dlgIdSelector).val()
              },
              url: ctx + '/dms/file/manager/component/load/folder/tree'
            }
          };
          treeSetting = $.extend(treeSetting, options.treeSetting);
          $.fn.zTree.init($(dlgTreeIdSelector), treeSetting);
        };
        initTree();
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            if ($.isFunction(onOk)) {
              var folderTree = $.fn.zTree.getZTreeObj(dlgTreeId);
              var nodes = folderTree.getSelectedNodes();
              return onOk.call(this, dlgTreeId, nodes);
            }
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {
          }
        }
      }
    };
    appModal.dialog(dialogOptions);
  }

  return DmsFileServices;
});

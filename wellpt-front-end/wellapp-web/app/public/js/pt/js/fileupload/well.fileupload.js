/**
 * 文件信息类
 */
var FileInfo = function () {
  this.fileID = null;
  this.fileName = null;
  this.contentType = null;
  this.digestAlgorithm = null;
  this.digestValue = null;
  this.certificate = null;
  this.signatureValue = null;
  this.fileSize = 0;
  this.isNew = true; // 表示如果值为false表示该文件已隶属于某个文件夹，true表示该文件还没和任何文件夹产生关系
};
/**
 * 列表式附件(附件在上传之后以列表的形式展示出来)
 */
$(function () {
  var defaultOptions = {
    uploadButtonLable: '上传'
  };

  /***************************************************************************
   * 该上传控件使用的样式为下拉列表样式. 如果没有其他个性化需求。其他需要文件上传的地方统一使用该控件
   */
  /**
   * @author Administrator
   *
   */
  // WellFileUpload = function(ctlID/* 控件唯一ID */) {
  // 	this.ctlID = ctlID;// 文件上传input元素的元素ID
  // 	this.$containerElement = null;
  // 	this.signature = false;// 是否签名
  // 	this.readOnly = false;
  // 	this.multiple = false;
  // 	this.$fileContainer = null;// 用户保存文件列表的html元素
  // 	this.files = null;// 用于存在当前控件的文件
  // 	this.clientServer = SystemParams.getValue("sys.context.path");
  // 	this.hostServer = SystemParams.getValue("document.preview.path");
  // 	this.readerExts = SystemParams.getValue("document.preview.readerExts");
  // };

  WellFileUpload = function (ctlID /* 控件唯一ID */ , options) {
    this.ctlID = ctlID; // 文件上传input元素的元素ID
    this.$containerElement = null;
    this.$fileContainer = null; // 用户保存文件列表的html元素
    this.signature = false; // 是否签名
    this.readOnly = false;
    this.multiple = false;
    this.files = null; // 用于存在当前控件的文件
    $.extend(defaultOptions, options);
    this.uploadButtonLable = defaultOptions.uploadButtonLable;
    this.events = defaultOptions.events || $.noop; //自定义外部事件
    this.localFileSourceIcon = ''; // 本地上传来源图标

    this.clientServer = SystemParams.getValue('sys.context.path');
    this.hostServer = SystemParams.getValue('document.preview.path');
    this.readerExts = SystemParams.getValue('document.preview.readerExts');

    if (window.opener && window.opener.WebApp && window.opener.WebApp.fileUploadConfig) {
      var fileUploadConfigCache = window.opener.WebApp.fileUploadConfig;
      WellFileUpload.configCache.isAnonymousUser = fileUploadConfigCache.isAnonymousUser;
      WellFileUpload.configCache.allSourceConfigList = fileUploadConfigCache.allSourceConfigList;
      WellFileUpload.configCache.allButtonConfigList = fileUploadConfigCache.allButtonConfigList;
    } else {
      if (WellFileUpload.configCache.isAnonymousUser == null) {
        $.ajax({
          url: ctx + '/fileListConfig/list',
          type: 'GET',
          dataType: 'json',
          async: false,
          success: function (result) {
            WellFileUpload.configCache.isAnonymousUser = result.isAnonymousUser;
            WellFileUpload.configCache.allSourceConfigList = result.allSourceConfigList;
            WellFileUpload.configCache.allButtonConfigList = result.allButtonConfigList;
            if (window.opener && window.opener.WebApp) {
              window.opener.WebApp.fileUploadConfig = result;
            }

          },
          error: function (jqXHR) {}
        });
      }
    }
  };

  WellFileUpload.CONSTANT = {
    UPLOAD_PROGRESS: 'upload_progress',
    REUPLOAD_DIV: 'reUpload_Div',
    CANCEL_UPLOAD_DIV: 'cancelUpload_Div',
    DOWNFILE_DIV: 'downFile_Div',
    REMOVE_FILE_DIV: 'removeFile_Div'
  };
  // 列表式附件配置缓存
  WellFileUpload.configCache = {
    isAnonymousUser: null,
    allSourceConfigList: null,
    allButtonConfigList: null
  };

  // 该变量用于存储最终要存放到后台的文件ID, 以ctlID为key.value为类FileInfo对象的数组
  WellFileUpload.files = [];

  // 暂时没用
  WellFileUpload.bodyFiles = [];

  // 用于保存所有的控件实例
  WellFileUpload.fileUploadObj = [];

  WellFileUpload.file2swf = false; // 是否生成swf副本,这个要页面初始化时会被初始化

  function getCookie(name) {
    var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
    if (arr) {
      return urldecode(arr[2]);
    } else {
      return null;
    }
  }

  //切片每次上传的块最大值
  var __size = (function (name) {
    var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
    return arr ? urldecode(arr[2]) : null;
  })('fileupload.maxChunkSize');

  WellFileUpload.maxChunkSize = __size ? parseInt(__size) : undefined; //1 * 1024 * 1024; // 256 * 1024; // 1 * 1024 *1024 = 1MB

  //上传文件在后端的快存储情况
  WellFileUpload.prototype.md5ChunkIndexObject = {};

  /**
   * 通过控件ID获取指定的控件实例,若该控件ID所指定的控件实例不存在则返回undefined
   *
   * @param ctlID
   *            控件实例的控件ID
   * @returns
   */
  WellFileUpload.get = function (ctlID) {
    return WellFileUpload.fileUploadObj[ctlID];
  };

  /**
   * 根据folderID从数据库里面加载文件-
   */
  WellFileUpload.loadFilesFormDb = function (folderID, purpose) {
    var dbFiles = [];
    JDS.call({
      service: 'mongoFileService.getNonioFilesFromFolder',
      data: [folderID, purpose],
      version: '',
      async: false,
      success: function (result) {
        var files = result.data;
        if (files != null) {
          $.each(files, function (i, file) {
            var file2 = new FileInfo();
            file2.fileID = file.fileID;
            file2.fileName = file.fileName;
            file2.contentType = file.contentType;
            file2.digestAlgorithm = file.digestAlgorithm;
            file2.digestValue = file.digestValue;
            file2.certificate = file.certificate;
            file2.isNew = false;
            dbFiles.push(file2);
          });
        }
      },
      error: function (jqXHR) {}
    });
    return dbFiles;
  };

  //获取文件
  WellFileUpload.loadFileFromDb = function (fileID) {
    var f = null;
    JDS.call({
      service: 'mongoFileService.getLogicFileInfo',
      data: [fileID],
      version: '',
      async: false,
      success: function (result) {
        f = result.data;
        if (f) {
          f.isNew = false;
        }
      },
      error: function (jqXHR) {}
    });
    return f;
  };

  /**
   * 签名
   *
   * @param fileID
   * @param digestValue
   * @param digestAlgorithm
   * @param certificate
   */
  WellFileUpload.sign = function (fileID, digestValue, digestAlgorithm, certificate) {
    var b = fjcaWs.OpenFJCAUSBKey();
    fjcaWs.ReadCertFromKey();
    var cert = fjcaWs.GetCertData();
    fjcaWs.SignDataWithKey(digestValue);
    var signData = fjcaWs.GetSignData();
    var signaturex = {};

    fjcaWs.CloseUSBKey();
    signaturex.digestValue = digestValue;
    signaturex.certificate = cert;
    signaturex.signatureValue = signData;
    signaturex.digestAlgorithm = digestAlgorithm;

    var url = ctx + fileServiceURL.updateSignature;
    // console.log("签名地址:" + url);
    // console.log("签名信息 ::" + JSON.stringify(signaturex));
    var updateSignatureOK = false;
    $.ajax({
      type: 'post',
      url: url,
      data: {
        signatureData: JSON.stringify(signaturex),
        fileID: fileID
      },
      timeout: 1000,
      async: false,
      success: function (data) {
        updateSignatureOK = true;
      },
      error: function (data) {
        // $('<span/>').text("文件签名更新失败");
        updateSignatureOK = false;
        // console.log("签名错误 :" + JSON.stringify(data));
      }
    });
    return updateSignatureOK;
  };

  /**
   * 初始化控件实例,在控件预先有文件信息，但文件信息存在于数据库中时使用该初始化方法，调用方需要传入文件信息对应的文件夹及文件用途，再由控件将文件信息加载出来。
   */
  WellFileUpload.prototype.initWithLoadFilesFromFileSystem = function (
    readOnly /* 是否只读,例如只给一些用户只读的权限 */ ,
    $containerElement /* 存放该附件的容器 */ ,
    signature /* 是否签名 */ ,
    multiple,
    /*
     * 多文件为true,
     * 单文件为false
     */
    folderID /* 文件夹ID */ ,
    purpose /* 文件用途 */
  ) {
    if (folderID == undefined || folderID == null || folderID == '') {
      throw new Error('请设置正确的文件夹ID');
    }

    if (purpose == undefined || purpose == null || purpose == '') {
      throw new Error('请设置正确的文件用途');
    }

    var dbFiles = WellFileUpload.loadFilesFormDb(folderID, purpose);

    // console.log("[" + folderID + "][" + purpose + "]上传控件从文件系统 中获取到的文件数为:"
    // + dbFiles.length );

    this.init(readOnly, $containerElement, signature, multiple, dbFiles);
  };

  /**
   * 初始化控件实例,在控件预先没有文件信息，或者有文件信息但已知的情况下使用该初始化方法
   *
   * @param readOnly
   *            是否只读
   * @param $containerElement,
   *            附件容器:jquery对象
   * @param signature
   *            是否签名
   * @param multiple
   *            多文件或者单文件
   * @param dbFiles
   *            文件信息数组，其成员为FileInfo类的对象,若没有文件信息，则dbFiles=[]
   */
  WellFileUpload.prototype.init = function (
    readOnly /* 是否只读,例如只给一些用户只读的权限 */ ,
    $containerElement /* 存放该附件的容器 */ ,
    signature /* 是否签名 */ ,
    multiple, // 多文件为true,
    // 单文件为false
    dbFiles
  ) {
    if ($containerElement == undefined || $containerElement == null || $containerElement == '') {
      throw new Error('请设置正确的控件容器');
    }

    this.$containerElement = $containerElement;
    this.signature = signature == undefined || signature == null || signature == '' ? false : true;
    this.readOnly = readOnly == undefined || readOnly == null || readOnly == '' ? false : true;
    if (typeof this.allowDownload == 'undefined') {
      this.allowDownload = true;
    }
    if (typeof this.allowDelete == 'undefined') {
      this.allowDelete = true;
    }

    this.multiple = multiple == undefined || multiple == null || multiple == '' ? false : true;
    WellFileUpload.fileUploadObj[this.ctlID] = this;

    this.$fileContainer = null; // 用户保存文件列表的html元素

    this.files = null;
    /**
     * 存储上传文件jqueryFileUpload data(用于取消上传/重新上传等)
     * {fileId:'',frontUuid:'',data:''}
     */
    this.fileDatas = [];

    // 获取保存文件信息的容器
    if (WellFileUpload.files[this.ctlID] == undefined) {
      // 容器不存在，新建
      WellFileUpload.files[this.ctlID] = [];
    }
    this.files = WellFileUpload.files[this.ctlID];

    // 初始化附件按钮
    if (this.flowSecDevBtnIdStr) {
      this.secDevBtnIdStr = this.flowSecDevBtnIdStr;
    }

    var secDevBtnIds = this.secDevBtnIdStr ? this.secDevBtnIdStr.split(';') : [];

    var flowSecDevBtnIds = this.flowSecDevBtnIdStr ? this.flowSecDevBtnIdStr.split(';') : [];

    var _this = this;
    var secDevBtns = [];
    this.editBtns = [];
    $.each(WellFileUpload.configCache.allButtonConfigList, function (index, buttonConfig) {
      if (flowSecDevBtnIds.length > 0) {
        // 流程有设置，以流程设置为准
        if (flowSecDevBtnIds.indexOf(buttonConfig.uuid) > -1) {
          secDevBtns.push(buttonConfig);
        }
      } else if (secDevBtnIds.indexOf(buttonConfig.uuid) > -1) {
        secDevBtns.push(buttonConfig);
      } else if (
        !_this.isDyformCtr &&
        (buttonConfig.code === 'preview_btn' || buttonConfig.code === 'download_btn' || buttonConfig.code === 'delete_btn' || buttonConfig.code === 're_upload_btn' || buttonConfig.code === 'cancel_upload_btn')
      ) {
        secDevBtns.push(buttonConfig);
      }
      if (
        (buttonConfig.btnShowType == 'show' || buttonConfig.btnShowType == '显示类操作') &&
        secDevBtnIds.indexOf(buttonConfig.uuid) > -1
      ) {
        _this.editBtns.push(buttonConfig.uuid);
      }
    });
    _this.secDevBtns = secDevBtns;

    this.enableUploadFunction(); // 定义上传功能
    //|| !this.allowUpload
    if (this.readOnly) {
      //只读 或 没有上传权限 隐藏上传按钮
      this.disableUploadFunction();
    } else if (!this.multiple) {
      // 单文件,默认为多文件
      this.$uploadInputElement.removeAttr('multiple');
    }

    this.clean();

    // 将从数据库中获取到的文件列表组装成html
    if (dbFiles != null && dbFiles.length > 0) {
      var size = dbFiles.length;
      for (var index = 0; index < size; index++) {
        var dbFile = dbFiles[index];
        var fileID = dbFile.fileID;
        var fileName = dbFile.fileName;
        var digestValue = dbFile.digestValue;
        var digestAlgorithm = dbFile.digestAlgorithm;
        var certificate = dbFile.certificate;
        var contentType = dbFile.contentType;
        var isNew = dbFile.isNew;
        isNew = isNew == undefined ? true : isNew;
        this.addFile(fileName, fileID, contentType, digestValue, digestAlgorithm, certificate, isNew /* 非新增的文件 */ , dbFile);
      }
    }
  };

  WellFileUpload.prototype.createFileContainer = function () {
    var fileListId = 'filelist' + this.ctlID;
    var attach_list = 'attach-list' + this.ctlID + ' file-list';
    var fileHtml = '<div id="' + fileListId + '"><ul class="' + attach_list + '" style="list-style-type:none;padding:0;" /></div>'; // 文件列表容器
    this.$fileContainer = $(fileHtml);

    this.$containerElement.append(this.$fileContainer);

    // this.$fileContainer.append();
  };

  /**
   * 添加上传功能
   */
  WellFileUpload.prototype.enableUploadFunction = function () {
    var _this = this;
    // 如果有上传的权限,则将添加附件的功能添加进去
    if (
      this.$uploadElement != undefined &&
      this.$uploadElement != null &&
      (this.secDevBtnIdStr == '' || this.secDevBtnIdStr.indexOf('00b13afb-8afc-4a9e-b1e2-28f321f48924') > -1)
    ) {
      // show会读取div的之前的display或者默认display属性。
      // return this.$uploadElement.show();
      return this.$uploadElement.css({
        display: ''
      });
    }
    // this.$uploadElement = $('<div></div>');

    var fileHtml = '';

    var fileSourceIds = _this.fileSourceIdStr ? _this.fileSourceIdStr.split(';') : [];

    var fileSources = [];
    $.each(WellFileUpload.configCache.allSourceConfigList, function (index, sourceConfig) {
      if (fileSourceIds.indexOf(sourceConfig.uuid) > -1) {
        fileSources.push(sourceConfig);
      }
    });

    // 上传按钮
    var uploadSecDevBtn = this.secDevBtns.find(function (btn) {
      return btn.code === 'upload_btn';
    });
    var defaultBtnObj = {
      btnColor: 'w-btn-primary',
      btnInfo: {
        class: 'w-line-btn'
      },
      iconInfo: {
        filePaths: 'iconfont icon-ptkj-shangchuan'
      }
    };
    var uploadSecDevBtnLibObj = uploadSecDevBtn ? JSON.parse(uploadSecDevBtn.btnLib) : defaultBtnObj;
    var _uploadSecDevBtnClass = [uploadSecDevBtnLibObj.btnColor, uploadSecDevBtnLibObj.btnInfo.class, uploadSecDevBtnLibObj.size].join(' ');

    var _uploadSecDevBtnIconClass =
      uploadSecDevBtnLibObj.iconInfo && uploadSecDevBtnLibObj.iconInfo.filePaths ?
      uploadSecDevBtnLibObj.iconInfo.filePaths :
      'icon iconfont icon-ptkj-shangchuan';

    if (fileSources.length === 0) {
      fileHtml += '<div class="upload_div" id="upload_div" code="local_upload">';
      fileHtml += '  <label class="well-btn ' + _uploadSecDevBtnClass + '">';
      fileHtml += '    <i class="' + _uploadSecDevBtnIconClass + '"></i>';
      fileHtml += '    <span class="add_icon">' + _this.uploadButtonLable + '</span>';
      fileHtml += '    <input ';
      fileHtml += '        type="file"';
      fileHtml += '        class="fileupload_css"';
      fileHtml += '        multiple';
      fileHtml += '        id="' + _this.ctlID + '"';
      fileHtml += '        name="' + _this.ctlID + '"';
      fileHtml += '    >';
      fileHtml += '  </label>';
      fileHtml += '</div>';

      _this.$uploadElement = $(fileHtml);
      _this.$containerElement.prepend(_this.$uploadElement);
      _this.$uploadInputElement = _this.$uploadElement.find('#' + _this.ctlID);
      _this.defineUploadEvent(); // 定义上传事件
    } else if (fileSources.length === 1) {
      var fileSourceItem = fileSources[0];
      if (fileSourceItem.code === 'local_upload') {
        _this.localFileSourceIcon = fileSourceItem.icon;

        fileHtml += '<div class="upload_div" id="upload_div" code="local_upload">';
        fileHtml += '  <label  class="well-btn ' + _uploadSecDevBtnClass + '">';
        fileHtml += '    <i class="' + _uploadSecDevBtnIconClass + '"></i>';
        fileHtml += '    <span class="add_icon">' + fileSourceItem.sourceName + '</span>';
        fileHtml += '    <input ';
        fileHtml += '        type="file"';
        fileHtml += '        class="fileupload_css"';
        fileHtml += '        multiple';
        fileHtml += '        id="' + _this.ctlID + '"';
        fileHtml += '        name="' + _this.ctlID + '"';
        fileHtml += '    >';
        fileHtml += '  </label>';
        fileHtml += '</div>';

        _this.$uploadElement = $(fileHtml);
        _this.$containerElement.prepend(_this.$uploadElement);
        _this.$uploadInputElement = _this.$uploadElement.find('#' + _this.ctlID);
        _this.defineUploadEvent(); // 定义上传事件
      } else {
        fileHtml += '<div class="upload_div" id="upload_div" code="' + fileSourceItem.code + '">';
        fileHtml += '  <label class="well-btn ' + _uploadSecDevBtnClass + '">';
        fileHtml += '    <i class="' + _uploadSecDevBtnIconClass + '"></i>';
        fileHtml += '    <span class="add_icon">' + fileSourceItem.sourceName + '</span>';
        fileHtml += '  </label>';
        fileHtml += '</div>';

        _this.$uploadElement = $(fileHtml);

        var jsModule;
        try {
          jsModule = JSON.parse(fileSourceItem.jsModule).jsModule;
        } catch (e) {
          jsModule = fileSourceItem.jsModule;
        }
        appContext.require([jsModule], function (FileSourceDevelopment) {
          var fileSourceDevelopment = new FileSourceDevelopment(_this);
          fileSourceDevelopment._setInitData(fileSourceItem);
          fileSourceDevelopment.init();
          fileSourceDevelopment._setInitData({
            fileSource: fileSourceItem
          });
          _this.$containerElement.prepend(_this.$uploadElement);
          _this.$uploadElement.click(fileSourceDevelopment._clickEvent());
        });
      }
    } else {
      var uploadDropdownMenuId = new Date().getTime();

      var $ul = $(
        ' <ul id="' +
        uploadDropdownMenuId +
        '" class="dropdown-menu editable-container" role="menu" style="position: absolute;display: none;"></ul>'
      );

      $.each(fileSources, function (index, fileSource) {
        var $li;

        var sourceIconHtml = '';
        if (_this.isShowFileSourceIcon) {
          sourceIconHtml = '<i class="icon ' + fileSource.icon + '"></i>';
        }

        if (fileSource.code === 'local_upload') {
          _this.localFileSourceIcon = fileSource.icon;

          var buttonHtml =
            '<label class="fileinput-button2">' +
            '' +
            '<span class="add_icon">' +
            fileSource.sourceName +
            '</span>' +
            '<input id="' +
            _this.ctlID +
            '" type="file" name="' +
            _this.ctlID +
            '" multiple class="file_source_css" style="cursor: pointer;height: 28px;left: 0;top: 0;margin: 0;opacity: 0;position: absolute;width: 100%;" ></label>';
          $li = $('<li code="' + fileSource.code + '" style="position: relative"><a href="#">' + sourceIconHtml + buttonHtml + '</a></li>');
        } else {
          $li = $('<li code="' + fileSource.code + '"><a href="#">' + sourceIconHtml + fileSource.sourceName + '</a></li>');
        }
        $li.appendTo($ul);
      });

      fileHtml += '<div  id="upload_div" class="btn-group" dropdownMenuId="' + uploadDropdownMenuId + '">';
      fileHtml += '  <button type="button" class="well-btn dropdown-toggle ' + _uploadSecDevBtnClass + '" data-toggle="dropdown">';
      fileHtml += '    <i class="' + _uploadSecDevBtnIconClass + '"></i>';
      fileHtml += '    <span class="add_icon">上传</span>';
      fileHtml += '    <span class="glyphicon glyphicon-menu-down" style="margin-left: 3px;font-size: 12px;"></span>';
      fileHtml += '  </button>';
      fileHtml += '  <ul class="dropdown-menu editable-container" role="menu" style="display: none"></ul>';
      fileHtml += '</div>';

      var $fileHtml = $(fileHtml);
      var $uploadBtn = $fileHtml.find('[type="button"]');

      $('body').append($ul);
      _this.$uploadElement = $fileHtml;
      _this.$containerElement.prepend(_this.$uploadElement);

      $fileHtml.on('hide.bs.dropdown', function () {
        $ul.css({
          display: 'none'
        });
      });
      $uploadBtn.click(function () {
        if ($ul.css('display') === 'block') {
          $ul.css({
            display: 'none'
          });
        } else {
          var position = _this.buttons.getPosition($uploadBtn);
          var calculatedOffset = _this.buttons.getCalculatedOffset('bottom', position, $(this).width() + 26, 0);
          $ul.css({
            top: calculatedOffset.top,
            left: calculatedOffset.left,
            display: 'block'
          });
        }
      });
      var removeUlFun = function (e) {
        if ($('[dropdownMenuId="' + uploadDropdownMenuId + '"]').length <= 0) {
          $ul.remove();
          $(document).unbind('click', removeUlFun);
        }
      };
      $(document).bind('click', removeUlFun);

      $.each(fileSources, function (index, fileSource) {
        if (fileSource.code === 'local_upload') {
          _this.$uploadInputElement = $ul.find('#' + _this.ctlID);
          //点击input，同时点击li，收回下拉项
          _this.$uploadInputElement.click(function () {
            // $ul.find('li[code="' + fileSource.code + '"]').click();
            $uploadBtn.click();
          });
          _this.defineUploadEvent(); // 定义上传事件
        } else {
          var jsModule;
          try {
            jsModule = JSON.parse(fileSource.jsModule).jsModule;
          } catch (e) {
            jsModule = fileSource.jsModule;
          }
          appContext.require([jsModule], function (FileSourceDevelopment) {
            var fileSourceDevelopment = new FileSourceDevelopment(_this);
            fileSourceDevelopment._setInitData(fileSource);
            fileSourceDevelopment.init();
            $ul.find('li[code="' + fileSource.code + '"]').click(fileSourceDevelopment._clickEvent());
          });
        }
      });
    }

    if (_this.secDevBtnIdStr) {
      _this.$containerElement
        .find('#upload_div')[_this.secDevBtnIdStr.indexOf('00b13afb-8afc-4a9e-b1e2-28f321f48924') > -1 ? 'show' : 'hide']();
    }
  };

  WellFileUpload.prototype.showFileSourceByCode = function (code) {
    var uploadElementCode = this.$uploadElement.attr('code');
    if (uploadElementCode) {
      //来源 只有一个
      if (uploadElementCode === code) {
        this.$uploadElement.show();
      }
    } else {
      //多个 来源
      this.$uploadElement
        .find('ul[class="dropdown-menu"]')
        .find('li[code="' + code + '"]')
        .show();
    }
  };

  WellFileUpload.prototype.hideFileSourceByCode = function (code) {
    var uploadElementCode = this.$uploadElement.attr('code');
    if (uploadElementCode) {
      //来源 只有一个
      if (uploadElementCode === code) {
        this.$uploadElement.hide();
      }
    } else {
      //多个 来源
      this.$uploadElement
        .find('ul[class="dropdown-menu"]')
        .find('li[code="' + code + '"]')
        .hide();
    }
  };

  /**
   * 删除上传功能
   */
  WellFileUpload.prototype.disableUploadFunction = function () {
    var _this = this;
    if (this.$uploadElement == undefined || this.$uploadElement == null) {
      return;
    }
    this.$uploadElement.hide();
    // this.$uploadElement = null;
  };

  /**
   * 设置是否可上传 下载 删除
   */
  WellFileUpload.prototype.initAllowUploadDeleteDownload = function (allowUpload, allowDelete, allowDownload) {
    this.allowUpload = allowUpload;
    this.allowDelete = allowDelete;
    this.allowDownload = allowDownload;
  };
  /**
   * 自定义上传文件类型 ['doc', 'docx']
   */
  WellFileUpload.prototype.setAccept = function (acceptType) {
    if ($.isArray(acceptType)) {
      var accept = '';
      $.each(acceptType, function (index, item) {
        accept += '.' + item + ',';
      });
      accept = accept.lastIndexOf(',') == accept.length - 1 ? accept.substr(0, accept.length - 1) : accept;
      $('input#' + this.ctlID).attr('accept', accept);
    } else if (acceptType != '') {
      appModal.error('格式必须数组形式！');
    }
  };

  /**
   * 单击附件名触发按钮事件二开入口
   * arg1:表单有编辑权限的参数
   * arg2:表单无编辑权限的参数
   */
  WellFileUpload.prototype.clickFileNameButtonEvent = function (arg1, arg2) {
    if (this.readOnly && arg2) {
      this.clickBtnCode = arg2.code;
      this.iconReadOnly = arg2.readOnly;
      // this.iconType = arg2.type;
    } else if (!this.readOnly && arg1) {
      this.clickBtnCode = arg1.code;
      this.iconReadOnly = arg1.readOnly;
      // this.iconType = arg1.type;
    }
  };

  /**
   * 已上传附件按钮显示方式
   */
  WellFileUpload.prototype.setBtnShowType = function (btnShowType) {
    this.btnShowType = btnShowType;
  };

  /**
   * 设置来源/二开按钮配置
   */
  WellFileUpload.prototype.initFileUploadExtraParam = function (
    isShowFileFormatIcon,
    isShowFileSourceIcon,
    secDevBtnIdStr,
    fileSourceIdStr,
    flowSecDevBtnIdStr,
    downloadAllType,
    displayName,
    btnShowType,
    isDyformCtr
  ) {
    this.isShowFileFormatIcon = isShowFileFormatIcon;
    this.isShowFileSourceIcon = isShowFileSourceIcon;
    this.secDevBtnIdStr = secDevBtnIdStr;
    this.fileSourceIdStr = fileSourceIdStr;
    this.flowSecDevBtnIdStr = flowSecDevBtnIdStr;
    this.downloadAllType = downloadAllType || '1';
    this.btnShowType = btnShowType || '1';
    this.displayName = displayName;
    this.isDyformCtr = isDyformCtr;
  };

  /**
   * add by wujx 20160606 设置是否允许上传重复名称的文件
   */
  WellFileUpload.prototype.initAllowFileNameRepeat = function (allowFileNameRepeat) {
    this.allowFileNameRepeat = allowFileNameRepeat;
  };

  /**
   * 调整样式适应，页面宽度变化
   */
  WellFileUpload.prototype.resize = function () {
    // var _this = this;
    // $.each(this.files, function (index, file) {
    // _this.buttons.setFileElementAdaptationStyle(_this, file);
    // });
  };

  WellFileUpload.prototype._ajaxGetFileChunkInfo = function (md5, fileName) {
    if (WellFileUpload.maxChunkSize == undefined) {
      return;
    }
    var _self = this;
    var getFileChunkInfoUrl = ctx + fileServiceURL.getFileChunkInfoAndSave; // 上传文件的地址
    var _auth = getCookie('_auth');
    var data = {
      md5: md5,
      chunkSize: WellFileUpload.maxChunkSize,
      fileName: fileName,
      fileSourceIcon: _self.localFileSourceIcon
    };
    (data[_auth] = getCookie(_auth)),
    $.ajax({
      type: 'get',
      url: getFileChunkInfoUrl,
      data: data,
      timeout: 3000,
      async: false,
      success: function (result) {
        _self.md5ChunkIndexObject[md5] = JSON.parse(result).data;
      },
      error: function (data) {}
    });
  };

  WellFileUpload.prototype._setDataUploadedBytes = function (md5, data) {
    var _this = this;
    var maxChunkSize = WellFileUpload.maxChunkSize;
    var md5ChunkIndexObjectElement = _this.md5ChunkIndexObject[md5];
    if (md5ChunkIndexObjectElement) {
      if (md5ChunkIndexObjectElement.hasMd5FileFlag) {
        // 后端有相同md5的文件 -> 直接上传最后一个字节的文件块
        data.uploadedBytes = data.files[0].size - 2;
      } else {
        var md5ChunkIndexArr = md5ChunkIndexObjectElement.chunkIndexList;
        // 空 -> 0;  0 -> 1;  012 -> 3; 012467 -> 3;
        var resultChunkIndex = 0;
        var indexIncrement = 0;
        $.each(md5ChunkIndexArr.concat(['']), function ( /*index, value*/ ) {
          if (md5ChunkIndexArr.indexOf(indexIncrement) < 0) {
            resultChunkIndex = indexIncrement;
          } else {
            indexIncrement++;
          }
        });
        var chunkIndexTotal = parseInt((parseInt(data.files[0].size) + maxChunkSize) / maxChunkSize);

        if (resultChunkIndex >= chunkIndexTotal) {
          // 大于等于总块数 -> 设置为最后一块
          resultChunkIndex = chunkIndexTotal - 1;
        }

        data.uploadedBytes = resultChunkIndex * maxChunkSize;
      }
    }
  };

  /**
   * 解析文件为md5
   */
  WellFileUpload.prototype.doIncrementalFileContentToMd5 = function (file, callback) {
    //这里假设直接将文件选择框的dom引用传入
    var _self = this;
    var SparkMD5 = require('spark-md5');
    //这里需要用到File的slice( )方法，以下是兼容写法
    var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
      // file = input.files[0],
      chunkSize = 2097152, // 以每片2MB大小来逐次读取
      chunks = Math.ceil(file.size / chunkSize),
      currentChunk = 0,
      spark = new SparkMD5.ArrayBuffer(), //创建SparkMD5的实例
      // time,
      fileReader = new FileReader();

    fileReader.onload = function (e) {
      // console.log("Read chunk number (currentChunk + 1) of  chunks ");

      spark.append(e.target.result); // Append array buffer
      currentChunk++;
      // this.progress = currentChunk / chunks;

      if (currentChunk < chunks) {
        loadNext();
      } else {
        // running = false;
        // console.log("Finished loading!");
        var md5 = spark.end(); // 完成计算，返回结果
        _self._ajaxGetFileChunkInfo(md5, file.name);
        callback(md5);
      }
    };

    fileReader.onerror = function () {
      // running = false;
      console.log('something went wrong');
    };

    function loadNext() {
      var start = currentChunk * chunkSize;
      var end = start + chunkSize >= file.size ? file.size : start + chunkSize;
      fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    }

    // running = true;
    loadNext();
  };

  WellFileUpload.prototype.defineUploadEvent = function () {
    var _this = this;
    _this.checkErrors = [];

    // this.$uploadInputElement.click(function () {
    //     appContext.require(["LocalFileSourceDevelopment"], function (LocalFileSourceDevelopment) {
    //         console.log(LocalFileSourceDevelopment);
    //         var app1 = new LocalFileSourceDevelopment(_this);
    //         app1.init();
    //     });
    // });
    //
    //
    // if (true) {
    //     return;
    // }

    var url = ctx + fileServiceURL.savefilesChunk; // 上传文件的地址

    var iframe = false;
    if ($.browser.msie && $.browser.version < 10) {
      iframe = true;
    }

    function getChunkInfoFromData(data) {
      // data.contentRange "bytes 2000000-2999999/6131632"
      var contentRange = data.contentRange;
      var chunkSize = data.chunkSize;
      var contentRangeSplit = contentRange.substring('bytes '.length).split('/');
      var split = contentRangeSplit[0].split('-');
      var chunkIndex = parseInt(parseInt(split[0]) / chunkSize);
      var total = contentRangeSplit[1];
      var chunkIndexTotal = parseInt((parseInt(total) + chunkSize) / chunkSize);
      var percentage = parseInt(((parseInt(split[1]) + 1) / parseInt(total)) * 100);

      return {
        chunkSize: chunkSize,
        chunkIndexTotal: chunkIndexTotal,
        chunkIndex: chunkIndex,
        total: total,
        percentage: percentage
      };
    }

    var _this = this;
    this.$uploadInputElement
      .fileupload({
        url: url,
        // forceIframeTransport: forceIframeTransport,
        iframe: iframe,
        dataType: 'json',
        // datatype: dataType,
        autoUpload: false,
        // sequentialUploads : true,
        formData: {
          signUploadFile: _this.signature
        },
        maxChunkSize: WellFileUpload.maxChunkSize,
        maxFileSize: 2000000000, // 2000 MB
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true,
        // pasteZone: _this.$uploadInputElement,
        dropZone: _this.$uploadInputElement
      })
      .on('fileuploadadd', function (e, data) {
        var frontUUID = require('commons').UUID.createUUID();
        var fileName = data.files[0].name;
        var contentType = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (data.files[0].size === 0) {
          _this.checkErrors.push('附件大小为0，上传失败');
        } else if (data.files[0].size > _this.fileMaxSize) {
          var fileMaxSize = '附件大小不能超过' + (_this.fileMaxSize > 0 ? formatSize(_this.fileMaxSize) : '2000M');
          if (_this.checkErrors.indexOf(fileMaxSize) == -1) {
            _this.checkErrors.push(fileMaxSize);
          }
        }

        var fileArr = data.files[0].name.split('.');
        var fileType = '.' + fileArr[fileArr.length - 1];
        var accept = this.getAttribute('accept');
        if (accept) {
          var acceptArr = accept.split(',');
          if (acceptArr.indexOf(fileType) < 0) {
            var fileType = '只能上传' + accept + '格式附件';
            if (_this.checkErrors.indexOf(fileType) == -1) {
              _this.checkErrors.push(fileType);
            }
          }
        }
        var hasUploadFilesLen = 0; //已上传文件数
        $.each(_this.files, function (index, file) {
          if (file.fileID) {
            //有fileID，说明上传成功
            hasUploadFilesLen++;
          }
        });
        if (_this.filesLength) {
          var surplusFilesLen = _this.filesLength - hasUploadFilesLen;
          var oLen = data.originalFiles.length;
          if (oLen > surplusFilesLen) {
            var fileNum = '附件数量最多不能超过' + _this.filesLength + '个';
            if (_this.checkErrors.indexOf(fileNum) == -1) {
              _this.checkErrors.push(fileNum);
            }
          }
        }

        if (_this.checkErrors.length > 0) {
          appModal.error(_this.checkErrors.join('，') + '，请检查后重新上传');
          _this.checkErrors = [];
          return false;
        }
        /*
         * console.log(_this.getFilePath(data));
         * console.log(_this.isSecuritySettingAsBid(false));
         * if(_this.isSecuritySettingAsBid(true)参数如果为true,表示需要提示用户去设置安全级别,false不提示){//返回true表示已被设置为禁用,false表示解禁
         * return false; }else{ var filePath = _this.getFilePath(data);
         * //TODO return true; }
         */

        if (_this.signature && !_this.validSignatureUSB()) {
          return false;
        }
        // add by wujx 20160606 begin
        if (_this.allowFileNameRepeat == false) {
          var isForbid = false;
          if (_this.files.length > 0) {
            for (var i = 0; i < data.files.length; i++) {
              var curFile = data.files[i];
              for (var j = 0; j < _this.files.length; j++) {
                var hadUploadFile = _this.files[j];
                if (hadUploadFile.fileName == curFile.name) {
                  appModal.error('已上传过文件名称为【' + curFile.name + '】的文件，不允许重复上传！');
                  isForbid = true;
                }
              }
            }
          }
          if (isForbid) {
            // if (file) {
            //     _this.deleteFileItem(file, _this);
            // }
            return false;
          }
        }

        _this.addFileToShow(fileName, '', contentType, '', '', '', true, {
          fileSourceIcon: _this.localFileSourceIcon
        }, frontUUID, data);

        _this.doIncrementalFileContentToMd5(data.files[0], function (md5) {
          data.formData = {
            frontUUID: frontUUID,
            chunkSize: WellFileUpload.maxChunkSize,
            md5: md5,
            localFileSourceIcon: _this.localFileSourceIcon
          };
          // var _auth = getCookie('_auth');
          // data.formData[_auth]=getCookie(_auth);

          if (
            _this.md5ChunkIndexObject &&
            _this.md5ChunkIndexObject[md5] &&
            _this.md5ChunkIndexObject[md5].hasMd5FileFlag &&
            _this.md5ChunkIndexObject[md5].uploadFiles &&
            _this.md5ChunkIndexObject[md5].uploadFiles.length &&
            _this.md5ChunkIndexObject[md5].uploadFiles.length > 0
          ) {
            $.each(_this.md5ChunkIndexObject[md5].uploadFiles, function ( /*index*/ ) {
              _this.uploadFileSuccess(
                this.filename,
                this.fileID,
                this.contentType,
                this.digestValue,
                this.digestAlgorithm,
                this.certificate,
                true,
                this,
                data.formData.frontUUID
              );
            });
          } else {
            _this._setDataUploadedBytes(md5, data);
            _this.buttons.setPercentage(_this, frontUUID, 0);
            if (_this.getFileByFrontUUID(frontUUID)) {
              data.submit();
            }
          }
        });
      })
      .on('fileuploadsubmit', function (e, data) {
        var file = _this.getFileByFrontUUID(data.formData.frontUUID);

        // add by wujx 20160606 end
        var files = data.files[0];
        // return false;
        data.formData.size = files.size;
        if (_this.beforeFileUpload(e, data) == false) {
          if (file) {
            _this.deleteFileItem(file, _this);
          }
          return false;
        }
        return true;
      })
      .on('fileuploaddone', function (e, data) {
        if (typeof data.result == 'undefined') {
          appModal.alert('请检查是否已登录!');
        } else {
          $.each(data.result.data, function (index) {
            // console.log("new file " + this.fileID);
            _this.uploadFileSuccess(
              this.filename,
              this.fileID,
              this.contentType,
              this.digestValue,
              this.digestAlgorithm,
              this.certificate,
              true,
              this,
              data.formData.frontUUID
            );
          });
          // _this.triggerFileChangeEvent();
        }
      })
      .on('fileuploadprocessalways', function (e, data) {
        var index = data.index,
          file = data.files[index];
        // node = $(data.context.children()[index]);
        if (file.preview) {
          // node
          // .prepend('<br>')
          // .prepend(file.preview);
        }
        if (file.error == 'File is too large') {
          // alert(file.error);
          // alert("附件最大支持" + (data.maxFileSize > 0 ? formatSize(data.maxFileSize) : "2000M"));
          // var fileItem = _this.getFileByFrontUUID(data.formData.frontUUID);
          // if (fileItem) {
          //     _this.deleteFileItem(fileItem, _this);
          // }
          // node
          // .append('<br>')
          // .append($('<span class="text-danger"/>').text(file.error));
        }
      })
      .on('fileuploadfail', function (e, data) {
        if (data.errorThrown === 'abort') {
          //取消上传
        } else {
          var file = _this.getFileByFrontUUID(data.formData.frontUUID);
          _this.buttons.setPercentageFail(_this, data.formData.frontUUID);
          if (file) {
            _this.buttons.addReUploadFileBtn(_this, file);
            _this.buttons.addCopyNameBtn(_this, file);
          }
          if (typeof data.result == 'undefined') {
            appModal.error('上传失败，请重新上传');
          } else {
            $.each(data.result.files, function (index, file) {
              var error = $('<span/>').text(file.error);
              $(data.context.children()[index]).append('<br>').append(error);
            });
          }
        }
      })
      // .on('fileuploadadd', function (e, data) {/* ... */console.log();})
      // .on('fileuploadsubmit', function (e, data) {/* ... */})
      // .on('fileuploadsend', function (e, data) {/* ... */})
      // .on('fileuploaddone', function (e, data) {/* ... */})
      // .on('fileuploadfail', function (e, data) {/* ... */})
      // .on('fileuploadalways', function (e, data) {/* ... */})
      // .on('fileuploadprogress', function (e, data) {/* ... */})
      // .on('fileuploadprogressall', function (e, data) {/* ... */})
      // .on('fileuploadstart', function (e) {/* ... */})
      // .on('fileuploadstop', function (e) {/* ... */})
      // .on('fileuploadchange', function (e, data) {/* ... */})
      // .on('fileuploadpaste', function (e, data) {/* ... */})
      // .on('fileuploaddrop', function(e, data) {  })
      // .on('fileuploaddragover', function(e) { })
      .on('fileuploadchunkbeforesend', function (e, data) {
        /* ... */
        // console.log('fileuploadchunkbeforesend')
      })
      .on('fileuploadchunksend', function (e, data) {
        // var frontUUID = data.formData.frontUUID;
        // var md5 = data.formData.md5;
        //
        // var chunkInfo = getChunkInfoFromData(data);
        // var md5ChunkIndexArr = _this.md5ChunkIndexObject[md5].chunkIndexList;
        // var hasMd5FileFlag = _this.md5ChunkIndexObject[md5].hasMd5FileFlag;
        // if ((chunkInfo.chunkIndexTotal - 1) === chunkInfo.chunkIndex) {
        //     //最后一个切块上传 -> 上传到后端处理
        // } else if (md5ChunkIndexArr.indexOf(chunkInfo.chunkIndex) > -1 //服务器已存在该分块
        //     || hasMd5FileFlag //该文件在服务器已存在
        // ) {
        //     // e.preventDefault();
        // }
        // console.log('fileuploadchunksend');
      })
      .on('fileuploadchunkdone', function (e, data) {
        var chunkInfo = getChunkInfoFromData(data);
        var percentage = chunkInfo.percentage;
        var frontUUID = data.formData.frontUUID;

        _this.buttons.setPercentage(_this, frontUUID, percentage);
        // console.log('fileuploadchunkdone');
      })
      .on('fileuploadchunkfail', function (e, data) {
        console.log('fileuploadchunkfail');
      })
      .on('fileuploadchunkalways', function (e, data) {
        // console.log('fileuploadchunkalways');
      })
      .on(this.events);
  };

  WellFileUpload.createReplicaOfSWF = function (fileID) {
    var okFlag = true; // 标识是否成功生成副本
    if (this.file2swf) {
      JDS.call({
        // 该接口不支持重载，如果一个方法有多个重载一定要注意，要被调用的方法必须放在最前面
        service: 'mongoFileService.createReplicaOfSWF',
        data: fileID,
        async: false,
        success: function (result) {},
        error: function (jqXHR) {
          okFlag = false; // throw new
          // Error("文件生成副本的过程中出现错误,请重新提交，若多次重试请联系技术人员");
        }
      });
    }
    return okFlag;
  };

  /**
   * 为控件添加单个附件 .
   *
   * @param filename
   * @param fileID
   * @param digestValue
   * @param digestAlgorithm
   * @param certificate
   * @param isNew
   *            如果为true,表示该文件还没与文件夹产生关系,false则反之
   */
  WellFileUpload.prototype.addFile = function (
    filename,
    fileID,
    contentType,
    digestValue,
    digestAlgorithm,
    certificate,
    isNew,
    /*
     * 新增的文件为true,
     * 否则为false
     */
    dbFile
  ) {
    var frontUUID = require('commons').UUID.createUUID();
    this.addFileToShow(filename, fileID, contentType, digestValue, digestAlgorithm, certificate, isNew, dbFile, frontUUID);
    this.uploadFileSuccess(filename, fileID, contentType, digestValue, digestAlgorithm, certificate, isNew, dbFile, frontUUID);
  };

  WellFileUpload.prototype.addFileToShow = function (
    filename,
    fileID,
    contentType,
    digestValue,
    digestAlgorithm,
    certificate,
    isNew,
    /*
     */
    dbFile,
    frontUUID,
    data
  ) {
    var _this = this;

    if (typeof isNew == 'undefined') {
      isNew = false;
    }
    var fileSize = 0,
      fileCreator = null,
      fileSourceIcon = '';
    if (dbFile) {
      if (dbFile.fileSize) {
        fileSize = dbFile.fileSize; // 第8个参数为文件大小
      }
      fileSourceIcon = dbFile.sourceIcon;
    }

    if (!fileSourceIcon) {
      fileSourceIcon = _this.localFileSourceIcon;
    }

    // if (this.signature && isNew) { // 更新文件签名
    // 	var updateSignatureOK = WellFileUpload.sign(fileID, digestValue, digestAlgorithm, certificate);
    //
    // 	if (!updateSignatureOK) {
    // 		appModal.alert("文件签名上传失败,请重新上传");
    // 		return;
    // 	}
    // }
    //
    // if (isNew && WellFileUpload.file2swf) {
    //
    // 	var ok = WellFileUpload.createReplicaOfSWF(fileID);//
    // 	if (!ok) {
    // 		appModal.alert("副本生成失败,请重试");
    // 		return;
    // 	}
    // }

    if (!this.$fileContainer) {
      this.createFileContainer();
    }

    var $ul = _this.$ul || (_this.$ul = _this.$fileContainer.find('ul'));

    if (!this.multiple) {
      // 单文件上传

      this.files.length = 0;
      // this.files.push({fileID:fileID, ctlID: this.ctlID, fileName:
      // filename, isNew: isNew});

      if ($ul.find('._delete_input').last().length) {
        $ul.find('._delete_input').last().trigger('click'); // 删除最后一个文件
      } else {
        $ul.empty();
      }
    }

    var fileType = 'tongyongfujian',
      color = '#488CEE';

    var fileArr = filename.split('.');
    var suffix = fileArr[fileArr.length - 1];

    if (suffix == 'jpg' || suffix == 'png' || suffix == 'gif') {
      fileType = 'tupian';
      color = '#D19122';
    } else if (suffix == 'doc' || suffix == 'docx') {
      fileType = 'word';
      color = '#2A5699';
    } else if (suffix == 'xls' || suffix == 'xlsx') {
      fileType = 'excel';
      color = '#207245';
    } else if (suffix == 'ppt' || suffix == 'pptx') {
      fileType = 'ppt';
      color = '#D24625';
    } else if (suffix == 'pdf') {
      fileType = 'pdf';
      color = '#F06966';
    } else if (suffix == 'txt') {
      // fileType = "txt"
    } else if (suffix == 'rar' || suffix == 'zip') {
      // fileType = "zip"
    }

    var fileNameWidthSub = 0;
    var uploadProgressStyle = ' position: relative;top: 2px;';
    if (isNew) {
      fileNameWidthSub += 25;
    } else {
      uploadProgressStyle += 'display:none;';
    }

    var _html = "<li filename='" + filename + "' class='clearfix' fileID='" + fileID + "'  frontUUID='" + frontUUID + "' >";
    var iconHtmlStr = '';

    if (_this.isShowFileSourceIcon) {
      fileNameWidthSub += 25;
      iconHtmlStr += '<span class="file_icon"><i class="icon ' + fileSourceIcon + '"></i></span>';
    }
    if (_this.isShowFileFormatIcon) {
      fileNameWidthSub += 25;
      iconHtmlStr += '<span class="file_icon"><i class="icon iconfont icon-ptkj-' + fileType + '" style="color:' + color + '"></i></span>';
    }

    var fileCreateTime = new Date(dbFile.createTime).format('yyyy-MM-dd HH:mm');
    fileCreator = SpringSecurityUtils.getUserDetails(dbFile.creator).userName; 
    var btnShowStyle = _this.btnShowType == '2' ? '' : 'display: none;';
    var btnShowClass = _this.btnShowType == '2' ? 'maxWidth55' : '';
    _html +=
      '<div class="files_div" style="background:none;">' +
      iconHtmlStr +
      '<p class="filename ' +
      btnShowClass +
      '" style="width:auto;">' +
      filename +
      '</p>' +
      '<span class="list-file-info">' +
        '<span>(&nbsp;' + formatSize(fileSize) + '</span>' +
        '<span>' + fileCreateTime + '</span>' +
        '<span>' + fileCreator + '</span>' +
        '<span>&nbsp;)</span>' +
      '</span>' +
      '<span class="file_upload_item_buttons">\n' +
      '    <span class="upload_progress" style="' +
      uploadProgressStyle +
      '"></span>\n' +
      '    <span class="sec_dev_Div" style="' +
      btnShowStyle +
      '">' +
      '       <span class="sec_dev_span"></span>' +
      '       <span class="reUpload_Div"></span>\n' +
      '       <span class="cancelUpload_Div"></span>\n' +
      '       <span class="copyName_Div"></span>\n' +
      /*            '       <span class="preview_Div"></span>\n' +
                        '       <span class="downFile_Div"></span>\n' +
                        '       <span class="removeFile_Div"></span>\n' +*/
      '    </span>\n' +
      '</span></div>';
    _html += '</li>';

    var $fileItemElement = $(_html);

    $fileItemElement.appendTo($ul);
    // 单击文件名预览
    $fileItemElement.find('p.filename').click(
      _.debounce(
        function (e) {
          if (_this.clickBtnCode) {
            var dropdownmenuid = $fileItemElement.find(".more_file_btn_div").attr("dropdownmenuid");
            if (dropdownmenuid && $("#" + dropdownmenuid).find("li[data-code='" + _this.clickBtnCode + "']").length > 0) {
              $("#" + dropdownmenuid).find("li[data-code='" + _this.clickBtnCode + "']").trigger('click');
            } else {
              $fileItemElement.find('.sec_dev_span').find("div[data-code='" + _this.clickBtnCode + "']").trigger('click');
            }
          } else {
            var clickType = SystemParams.getValue('pt.filename.click.type');
            if (clickType == 'download') {
              $fileItemElement.find('.sec_dev_span').find("div[data-code='download_btn']").trigger('click');
            } else {
              $fileItemElement.find('.sec_dev_span').find("div[data-code='preview_btn']").trigger('click');
            }
          }
        },
        500, {
          leading: true,
          trailing: false
        }
      )
    );

    ////        var width = this.$fileContainer.parents("td").width();
    ////        width = width - 200 >= 0 ? width : 200;
    ////        $fileItemElement.find(".files_div").width(width);
    ////        $fileItemElement.find(".filename").width(width - 40);

    // $fileItemElement.find(".files_div").popover({
    // 	html : true,
    // 	placement : "top",
    // 	container : "body",
    // 	trigger : "hover",
    // 	template : '<div class="popover tip-file" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    // 	content : function(){
    // 		var content = "<span class=\"tip-filename\">"+filename+"</span><br>";
    // 		if(fileSize){
    // 			content += "<span class=\"tip-s tip-filesize\">"+formatSize(fileSize)+"</span>";
    // 		}
    // 		if(dbFile && dbFile.createTime){
    // 			content += "<span class=\"tip-s tip-createTime\">"+new Date(dbFile.createTime).format('yyyy-MM-dd HH:mm')+"</span>";
    // 		}
    // 		if(dbFile && dbFile.creator) {
    // 			if(!fileCreator) {
    // 				fileCreator = SpringSecurityUtils.getUserDetails(dbFile.creator).userName;
    // 			}
    // 			content += "<span class=\"tip-s tip-creator\">"+fileCreator+"</span>";
    // 		}
    // 		return content;
    // 	}
    // });

    // 取消点击文件下载功能(剑武提的)
    // $fileItemElement.children().first().click(function(e) {// 下载功能
    // 	window.open(ctx + fileServiceURL.downFile + fileID);
    // });

    // $ul.find("li").last().html(_html);

    // var file = this.getFile(fileID);

    // if (file == null) {
    var file = new FileInfo();
    file.fileID = fileID;
    file.frontUUID = frontUUID;
    file.fileName = filename;
    file.digestAlgorithm = digestAlgorithm == undefined ? '' : digestAlgorithm;
    file.digestValue = digestValue == undefined ? '' : digestValue;
    file.certificate = certificate == undefined ? '' : certificate;
    file.signatureValue = '';
    file.contentType = contentType == undefined ? '' : contentType;
    file.isNew = isNew;
    file.$fileItemElement = $fileItemElement;
    file.fileSize = fileSize;
    this.files.push(file);
    // this.triggerFileChangeEvent();
    this.fileDatas.push({
      fileID: fileID,
      frontUUID: frontUUID,
      data: data,
      fileNameWidthSub: fileNameWidthSub
    });

    // }

    if (_this.btnShowType != '2') {
      _this.buttons.addHoverEvent(_this, file);
    }
    if ($.trim(fileID).length <= 0) {
      // ajax新加附件
      _this.buttons.setPercentageMd5(_this, frontUUID);
      _this.buttons.addCancelUploadFileBtn(_this, file);
    }
    // $fileItemElement.trigger("resetGridWidth.editable");
    // if (!this.readOnly) {
    // 	this.enableDeleteFunction4OneItem(file);// 定义删除功能
    // }

    // // 判断文件类型，根据类型决定是否可以预览
    // var regFileType = /\.(png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp)(\?.*)?/
    // if(regFileType.test(file.fileName)){
    // 	this.previewFile(file);
    // }

    var fileInfo = {};
    $.extend(fileInfo, file);
    fileInfo.filename = fileInfo.fileName;

    // this.uploadOk(fileInfo);// 上传成功

    // // 设置是否可下载
    // if (typeof this.allowDownload == "undefined" || true == this.allowDownload) {
    // 	this.enableDownLoadFunction();
    // } else {
    // 	this.disableDownLoadFunction();
    // }
    // // 设置是否可删除
    // if (!this.readOnly&&(typeof this.allowDelete == "undefined" || true == this.allowDelete)) {
    // 	this.enableDeleteFunction();
    // } else {
    // 	this.disableDeleteFunction();
    // }

    _this.buttons.setFileElementAdaptationStyle(_this, file);
  };

  WellFileUpload.prototype.uploadFileSuccess = function (
    filename,
    fileID,
    contentType,
    digestValue,
    digestAlgorithm,
    certificate,
    isNew,
    /*
     * 新增的文件为true,
     * 否则为false
     */
    dbFile,
    frontUUID
  ) {
    if (typeof isNew == 'undefined') {
      isNew = false;
    }
    var fileSize = 0,
      fileCreator = null;
    if (dbFile) {
      fileSize = dbFile.fileSize || dbFile; // 第8个参数为文件大小
    }

    var _this = this;

    if (this.signature && isNew) {
      // 更新文件签名
      var updateSignatureOK = WellFileUpload.sign(fileID, digestValue, digestAlgorithm, certificate);

      if (!updateSignatureOK) {
        appModal.alert('文件签名上传失败,请重新上传');
        return;
      }
    }

    if (isNew && WellFileUpload.file2swf) {
      var ok = WellFileUpload.createReplicaOfSWF(fileID); //
      if (!ok) {
        appModal.alert('副本生成失败,请重试');
        return;
      }
    }

    if (!this.$fileContainer) {
      this.createFileContainer();
    }

    // var $ul = this.$fileContainer.find("ul");

    // if (!this.multiple) {// 单文件上传
    //
    // 	this.files.length = 0;
    // 	// this.files.push({fileID:fileID, ctlID: this.ctlID, fileName:
    // 	// filename, isNew: isNew});
    //
    // 	$ul.find("._delete_input").last().trigger("click");// 删除最后一个文件
    // }

    // this.$fileContainer.find("ul").append('<li
    // filename="'+filename+'"></li>');

    // var fileType = "fujian", color = "#488CEE";
    // if(filename.indexOf(".jpg") != -1 || filename.indexOf(".png") != -1 || filename.indexOf(".gif") != -1){
    // 	fileType = "tupian";
    // 	color = "#D19122";
    // }
    // if(filename.indexOf(".doc") != -1 || filename.indexOf(".docx") != -1){
    // 	fileType = "word";
    // 	color = "#2A5699";
    // }
    // if(filename.indexOf(".xls") != -1 || filename.indexOf(".xlsx") != -1){
    // 	fileType = "excel";
    // 	color = "#207245";
    // }
    // if(filename.indexOf(".ppt") != -1 || filename.indexOf(".pptx") != -1){
    // 	fileType = "ppt";
    // 	color = "#D24625";
    // }
    // if(filename.indexOf(".pdf") != -1){
    // 	fileType = "pdf";
    // 	color = "#F06966";
    // }
    // if(filename.indexOf(".txt") != -1){
    // 	// fileType = "txt"
    // }
    // if(filename.indexOf(".rar") != -1 || filename.indexOf(".zip") != -1){
    // 	// fileType = "zip"
    // }

    var $fileItemElement = this.$fileContainer.find('ul').find('li[frontUUID="' + frontUUID + '"]');
    $fileItemElement.attr('fileID', fileID);

    // var _html = "<li filename='" + filename + "' class='clearfix' fileID='" + fileID + "'  frontUUID='"+frontUUID+"' >";
    // _html += '<div class="files_div" style="background:none;"><span class="file_icon"><i class="icon iconfont icon-ptkj-'+fileType+'" style="color:'+color+'"></i></span><p class="filename">' + filename + '</p></div>';
    // _html += "</li>";

    // var $fileItemElement = $(_html);
    //
    // $fileItemElement.appendTo(this.$fileContainer.find("ul"));

    ////        var width = this.$fileContainer.parents("td").width();
    ////        width = width - 200 >= 0 ? width : 200;
    ////        $fileItemElement.find(".files_div").width(width);
    ////        $fileItemElement.find(".filename").width(width - 40);

    $fileItemElement.find('.files_div').popover({
      html: true,
      placement: 'top',
      container: 'body',
      trigger: 'hover',
      template: '<div class="popover tip-file" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
      content: function () {
        var content = "<span class=\"tip-fileid\" style='display: none;' fileId='" + fileID + "'>" + fileID + '</span>';
        content += '<span class="tip-filename">' + filename + '</span><br>';
        if (fileSize) {
          content += '<span class="tip-s tip-filesize">' + formatSize(fileSize) + '</span>';
        }
        if (dbFile && dbFile.createTime) {
          content += '<span class="tip-s tip-createTime">' + new Date(dbFile.createTime).format('yyyy-MM-dd HH:mm') + '</span>';
        }
        if (dbFile && dbFile.creator) {
          if (!fileCreator) {
            var userDetails = SpringSecurityUtils.getUserDetails(dbFile.creator);
            fileCreator = userDetails ? userDetails.userName : '';
          }
          content += '<span class="tip-s tip-creator">' + (fileCreator || '') + '</span>';
        }
        return content;
      }
    });

    var file = this.getFileByFrontUUID(frontUUID);

    // if (file == null) {
    // var file = new FileInfo();
    file.fileID = fileID;
    file.fileName = filename;
    file.digestAlgorithm = digestAlgorithm == undefined ? '' : digestAlgorithm;
    file.digestValue = digestValue == undefined ? '' : digestValue;
    file.certificate = certificate == undefined ? '' : certificate;
    file.signatureValue = '';
    file.contentType = contentType == undefined ? '' : contentType;
    file.isNew = isNew;
    file.$fileItemElement = $fileItemElement;
    file.fileSize = fileSize;
    this.triggerFileChangeEvent();
    isNew && this.triggerFileOperates(_this.bReupload ? 'file-reupload' : 'file-upload', null, file);
    delete _this.bReupload;
    // this.files.push(file);
    // }

    this.buttons.hideUploadBtnDiv(this, file);
    this.buttons.addSecDevFileBtn(this, file);

    // if (!this.readOnly) {
    //     this.buttons.addDeleteFunction4OneItem(this, file);// 定义删除功能
    // }

    // 判断文件类型，根据类型决定是否可以预览
    // var regFileType = /\.(png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp)(\?.*)?/
    // if (regFileType.test(file.fileName)) {
    //     this.buttons.addPreviewFileBtn(this, file);
    // }

    this.buttons.removeCancelUploadFileBtn(this, file);
    this.buttons.setPercentageSuccess(this, file.frontUUID);

    var fileInfo = {};
    $.extend(fileInfo, file);
    fileInfo.filename = fileInfo.fileName;

    this.uploadOk(fileInfo); // 上传成功

    // 设置是否可下载
    if (typeof this.allowDownload == 'undefined' || true == this.allowDownload) {
      this.enableDownLoadFunction();
    } else {
      this.disableDownLoadFunction();
    }
    // 设置是否可删除
    if (!this.readOnly && (typeof this.allowDelete == 'undefined' || true == this.allowDelete)) {
      this.enableDeleteFunction();
    } else {
      this.disableDeleteFunction();
    }
  };

  WellFileUpload.prototype.getFile = function (fileId) {
    var file = null;
    for (var index = 0; index < this.files.length; index++) {
      if (this.files[index].fileID == fileId) {
        file = this.files[index];
      }
    }
    return file;
  };

  WellFileUpload.prototype.getFileByFrontUUID = function (frontUUID) {
    var file = null;
    for (var index = 0; index < this.files.length; index++) {
      if (this.files[index].frontUUID == frontUUID) {
        file = this.files[index];
      }
    }
    return file;
  };

  WellFileUpload.prototype.getFileJqueryDataByFrontUUID = function (frontUUID) {
    var fileData = null;
    for (var index = 0; index < this.fileDatas.length; index++) {
      if (this.fileDatas[index].frontUUID == frontUUID) {
        fileData = this.fileDatas[index];
      }
    }
    return fileData;
  };

  WellFileUpload.prototype.deleteFileItem = function (file, _this) {
    if (typeof _this == 'undefined') {
      _this = this;
    }
    var $fileItemElement = file.$fileItemElement;

    // modify by wujx 20161012 begin BUG #4493
    if ($fileItemElement) {
      $fileItemElement.remove();
    }
    // modify by wujx 20161012 begin
    for (var index = 0; index < _this.files.length; index++) {
      if (_this.files[index].fileID == file.fileID) {
        _this.files.splice(index, 1);
      }
    }

    // 删除文件提示信息框popover
    var $parent = $('span[fileId="' + file.fileID + '"]')
      .parent()
      .parent();
    if ($parent.hasClass('popover')) {
      $parent.remove();
    }

    var fileInfo = {};
    $.extend(fileInfo, file);
    fileInfo.filename = file.fileName;
    _this.deleteFileOk(fileInfo); // 删除成功
  };

  WellFileUpload.prototype.deleteAllFileItem = function () {
    var files = this.files.concat();
    for (var index = 0; index < files.length; index++) {
      this.deleteFileItem(files[index]);
    }
  };

  // 定义单个文件的删除功能
  WellFileUpload.prototype.disableDeleteFunction4OneItem = function (file) {
    var $fileItemElement = file.$fileItemElement;
    if (!$fileItemElement) {
      return;
    }
    $fileItemElement.find('.delete_span').remove();
  };

  // 定义单个文件的删除功能
  WellFileUpload.prototype.disableDeleteFunction = function () {
    for (var index = 0; index < this.files.length; index++) {
      var file = this.files[index];
      this.buttons.removeDeleteFunction4OneItem(file);
    }
  };

  WellFileUpload.prototype.enableDeleteFunction = function () {
    for (var index = 0; index < this.files.length; index++) {
      var file = this.files[index];
      this.buttons.addDeleteFunction4OneItem(this, file);
    }
  };

  WellFileUpload.prototype.enableDownLoadFunction = function () {
    // 文件点击下载
    for (var index = 0; index < this.files.length; index++) {
      var file = this.files[index];
      this.enableDownLoadFunction4OneItem(file);
    }
    // 全部下载按钮
    if (this.$downloadallbtn) {
      this.$downloadallbtn.show();
    }
  };

  WellFileUpload.prototype.enableDownLoadFunction4OneItem = function (file) {
    // file.find('downFile_Div');
    // var $fileItemElement = file.$fileItemElement;
    // 取消点击文件下载功能(剑武提的)
    // $fileItemElement.children().first().bind('click', function() {
    // 	window.open(ctx + fileServiceURL.downFile + $fileItemElement.attr("fileID"));
    // });
    // 添加下载按钮，点击按钮下载
    //         var _this = this;
    //         var $fileItemElement = file.$fileItemElement;
    //         if (!_this.hostServer || $fileItemElement.find(".preview_Div").size() > 0) {
    //             // 未配置预览服务器
    //             return;
    //         }
    //         var _html = "<div class='preview_Div' style='float:right;'><i id='preview' class='_preview_input icon iconfont icon-ptkj-xiazai' filename='"
    //             + file.fileName + "' title='下载'></i>下载</div>";
    //         $(_html).appendTo($fileItemElement);
    //
    // //        var width = $fileItemElement.find(".files_div").width();
    // //        $fileItemElement.find(".files_div").width(width - 25);
    // //        $fileItemElement.find(".filename").width($fileItemElement.find(".files_div").width() - 40);
    //
    //         $('._preview_input', $fileItemElement).click(function() {
    //             var wopiComponent = _this.clientServer+ '/wopi/files/'+file.fileID+'?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb&_requestCode=1564653762894';
    //             var url = _this.hostServer + "/document/online/viewer?WOPISrc=";
    //             var winOpts = url + encodeURIComponent(wopiComponent);
    //             window.open(winOpts,"_blank")
    //         });
  };

  WellFileUpload.prototype.disableDownLoadFunction = function () {
    // 文件点击下载
    for (var index = 0; index < this.files.length; index++) {
      var file = this.files[index];
      this.disableDownLoadFunction4OneItem(file);
    }
    // 全部下载按钮
    if (this.$downloadallbtn) {
      this.$downloadallbtn.hide();
    }
  };

  WellFileUpload.prototype.disableDownLoadFunction4OneItem = function (file) {
    var $fileItemElement = file.$fileItemElement;
    $fileItemElement.find('.downFile_Div').hide();
    // $fileItemElement.children().first().unbind('click');
  };

  WellFileUpload.prototype.copyFileNameFunc = function (file) {
    // 复制文件名称
    $('body').append("<textarea id='fileNameContent' style='opacity: 0;'>");
    var copyContent = file.fileName;
    var inputElement = document.getElementById('fileNameContent');
    inputElement.value = copyContent;
    inputElement.select();
    document.execCommand('Copy');
    appModal.success('复制成功！');
    $('#fileNameContent').remove();
  };
  // 上传成功
  WellFileUpload.prototype.uploadOk = function (fileInfo) {
    // 从验证框架中删除该控件的所对应的非法条目
    if (this.$uploadElement != undefined && this.$uploadElement != null) {
      var editableElement = this.$uploadElement.find("input[name='" + this.ctlID + "']");
      if (typeof Theme != 'undefined' && editableElement.length > 0) {
        Theme.validationRules.unhighlight(editableElement);
        Theme.validationRules.success(null, editableElement);
      }
    }
    this.addLoadAllElement();
    // this.addFileIcon();
    if (this.uploadOkCallback) {
      // 上传成功回调
      this.uploadOkCallback(fileInfo);
    }
  };

  // 上传前的回调事件,返回true表示要上传返回false表示不上传
  WellFileUpload.prototype.beforeFileUpload = function (e, data) {
    if (this.beforeFileUploadCallback) {
      // 上传前的回调事件
      return this.beforeFileUploadCallback(e, data);
    }
    return true;
  };

  // 删除成功
  WellFileUpload.prototype.deleteFileOk = function (fileInfo) {
    if (this.deleteOkCallback) {
      // 删除成功回调
      this.deleteOkCallback(fileInfo);
    }
    this.deleteLoadAllElement();
    this.triggerFileChangeEvent();
  };

  // 触发文件变更事件
  WellFileUpload.prototype.triggerFileChangeEvent = function () {
    var self = this;
    self.$containerElement && self.$containerElement.trigger('filechange');
    self.$containerElement && self.$containerElement.next('input').trigger('changeFiles');
    $.isFunction(self.afterChangeFiles) && self.afterChangeFiles();
  };

  WellFileUpload.prototype.setAfterChangeFilesCallBack = function (afterChangeFilesFunc) {
    if ($.isFunction(afterChangeFilesFunc)) {
      this.afterChangeFiles = afterChangeFilesFunc;
    }
  };

  // add the element of load all file
  WellFileUpload.prototype.addLoadAllElement = function () {
    var _this = this;
    this.$downLoadFileElem = this.$containerElement.find('div.downloadAll');
    if (this.$downLoadFileElem.length <= 0) {
      var downloadAllHtml =
        "<div class='clear-both'></div><div class='downloadAll'>共<span class='totalFileCount'>1</span>个附件&nbsp;&nbsp;&nbsp;";
      if (!_this.isDyformCtr) {
        downloadAllHtml += "<a class='downloadallbtn'><i class='iconfont icon-ptkj-xiazai'></i>全部下载</a>";
      } else {
        for (var i = 0; i < _this.secDevBtns.length; i++) {
          if (_this.secDevBtns[i].code == 'download_all_btn') {
            downloadAllHtml += "<a class='downloadallbtn'><i class='iconfont icon-ptkj-xiazai'></i>全部下载</a>";
            break;
          }
        }
      }

      downloadAllHtml += '</div>';
      this.$downLoadFileElem = $(downloadAllHtml);
      // var totalCountHtml = "";
      // this.$downLoadFileElem.append(totalCountHtml);
      this.$totalFileCount = this.$downLoadFileElem.find('.totalFileCount');
      this.$downloadallbtn = this.$downLoadFileElem.find('.downloadallbtn');
      // this.$downloadallbtn.appendTo(this.$downLoadFileElem);
      this.$downloadallbtn.off().on('click', function () {
        var files = [];
        for (var i = 0; i < _this.files.length; i++) {
          if (_this.downloadAllType == '2') {
            var url = ctx + fileServiceURL.downFile + _this.files[i].fileID;
            _this.downloadURL(url);
          } else {
            files.push({
              fileID: _this.files[i].fileID
            });
          }
        }
        if (_this.downloadAllType == '2') {
          // setTimeout(function () {
          //   $("iframe[id^='hiddenDownloader']").remove();
          // }, 1000);
        } else {
          if (_this.displayName) {
            var $dyform = DyformFacade.get$dyform();
            var title = _this.displayName + '-';
            $dyform.getDyformTitle(
              function () {
                title += arguments[0];
              },
              function () {}
            );
            var reg = /\\|\/|\:|\*|\?|\<|\>|"|\|/g;
            title = title.replace(reg, ' ');
          } else {
            var title = new Date().format('yyyyMMddHHmmss');
          }
          var url = ctx + fileServiceURL.downAllFiles + urlencode(JSON.stringify(files)) + '&includeFolder=false';
          url += '&fileName=' + title; // 下载文件取时间格式字符（后续可以优化配置增加用户自定义下载文件命名）
          // window.location.href = url;
          _this.downloadURL(url);
        }
      });
      // $("").appendTo(this.$containerElement);
      this.$downLoadFileElem.appendTo(this.$containerElement);
    } else {
      this.$downLoadFileElem.show();
      this.updateLoadAllElement();
    }
  };

  WellFileUpload.prototype.downloadURL = function (url) {
    var hiddenIFrameID = 'hiddenDownloader' + Math.floor(Math.random() * 100000000000000000 + 1);
    var iframe = document.createElement('iframe');
    iframe.id = hiddenIFrameID;
    iframe.style.display = 'none';
    document.body.appendChild(iframe);
    iframe.src = url;
    var _this = this;
    var cnt = 0;
    var timer = setInterval(function () {
      if (cnt++ == 100) {
        clearInterval(timer);
        iframe.remove();
        return;
      }
      var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
      if (iframeDoc.readyState == 'complete' || iframeDoc.readyState == 'interactive') {
        var _text = $(iframeDoc.body).text();
        if (_text && _text.indexOf('No such file') != -1) {
          //需要等待后端响应无文件的异常
          clearInterval(timer);
          iframe.remove();
          if (_text.indexOf('try later') != -1) {
            setTimeout(function () {
              _this.downloadURL(url); //重新下载
            }, 2000);
          }
        }
        return;
      }
    }, 1000);
  };

  // delete the element of load all file
  WellFileUpload.prototype.deleteLoadAllElement = function (fileInfo) {
    // console.log(this.size());
    if (this.size() > 0) {
      this.updateLoadAllElement();
    } else {
      // console.log(this.$downLoadFileElem.size() + "-----");
      // alert(this.$containerElement.find(".downloadAll").size());
      // this.$downLoadFileElem.prev().remove();
      // this.$downLoadFileElem.remove();
      // alert(this.$containerElement.find(".downloadAll").size());
      this.$containerElement.find('.downloadAll').prev().hide();
      this.$containerElement.find('.downloadAll').hide();
    }
  };

  // update the element of load all file
  WellFileUpload.prototype.updateLoadAllElement = function () {
    // modify by wujx 20161012 begin BUG #4493
    if (this.$totalFileCount) {
      this.$totalFileCount.html(this.size());
    }
    // modify by wujx 20161012 begin
  };

  WellFileUpload.prototype.size = function () {
    return this.files == null ? 0 : this.files.length;
  };

  // 删除所有的文件
  WellFileUpload.prototype.clear = function () {
    this.deleteAllFileItem();
    if (this.$downLoadFileElem && this.$downLoadFileElem.length > 0) {
      this.$downLoadFileElem.hide();
    }
    /*
     * var $ul = this.$fileContainer.find("ul"); if(this.readOnly){
     * this.enableDeleteFunction(); }
     *
     *
     *
     * var $_delete_input = $ul.find("._delete_input");
     *
     *
     * if($_delete_input.size() > 0){ debugger;
     * $_delete_input.trigger("click"); }
     *
     *
     *
     * this.files.length = 0; if(this.readOnly){
     * this.disableDeleteFunction(); }
     */
  };

  WellFileUpload.prototype.clean = function () {
    this.clear();
  };

  /**
   * 为控件添加一组文件
   *
   * @param files
   *            文件信息数组，数组中的成员的成员为类FileInfo对象
   * @param isNew
   *            如果为true，同时初始化时的signature参数也为true,那么将会为该附件生成签名信息。
   * @param isAppend
   *            如果为true,
   *            表示不清空原来夹下的文件，在原来的夹下面继续append参数files里面的文件到文件夹下，如果为false则表示将原来夹下面的文件清空，再把参数files里面的文件添加到文件夹下
   */
  WellFileUpload.prototype.addFiles = function (files, isAppend) {
    // console.log(JSON.stringify(files));
    var _this = this;
    // window.setTimeout(function(){//跟初始化时保持秒数一致.
    if (null == files) {
      return;
    } else if (false === $.isArray(files)) {
      var fileArray = [];
      fileArray.push(files);
      files = fileArray;
    }

    if (isAppend == false) {
      // 清空文件夹即该控件下面的文件
      // _this.files.length = 0;
      _this.clear();
    }

    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      _this.addFile(
        file.fileName || file.filename,
        file.fileID,
        file.contentType,
        file.digestValue,
        file.digestAlgorithm,
        file.certificate,
        file.isNew,
        file
      );
    }
  };

  WellFileUpload.prototype.addFilesByFiles = function (files, fileSourceIcon, isCover) {
    var _this = this;
    var errorStr = '';

    $.each(files, function (index, file) {
      //非空校验
      if (file.fileId) {} else {
        errorStr = '文件编码不能为空';
        return false;
      }
      if (file.fileName) {} else {
        errorStr = '文件名称不能为空';
        return false;
      }
      if (file.fileSize) {} else {
        errorStr = '文件名称不能为空';
        return false;
      }

      //文件格式校验
      if (_this.fileExt && $.isArray(_this.fileExt)) {
        var fileArr = file.fileName.split('.');
        if (fileArr.length <= 1) {
          errorStr = '文件格式不正确,只能上传' + _this.fileExt.join(',') + '格式文件';
          return false;
        } else {
          var fileType = fileArr[fileArr.length - 1];
          if (_this.fileExt.indexOf(fileType) < 0) {
            errorStr = '文件格式不正确,只能上传' + _this.fileExt.join(',') + '格式文件';
            return false;
          }
        }
      }

      //文件大小校验
      var fileMaxSize = 2000 * 1024 * 1024;
      if (_this.fileMaxSize && _this.fileMaxSize > 0) {
        fileMaxSize = _this.fileMaxSize;
      }
      if (file.fileSize > fileMaxSize) {
        errorStr = '附件最大支持' + (fileMaxSize > 0 ? formatSize(fileMaxSize) : '2000M');
        return false;
      }
    });
    if (errorStr) {
      return errorStr;
    }

    //重名校验
    if (_this.allowFileNameRepeat == false) {
      var fileNameArr = [];
      var repeatNameArr = [];
      $.each(files, function (index, file) {
        if (fileNameArr.indexOf(file.fileName) > -1) {
          repeatNameArr.push(file.fileName);
        }
        fileNameArr.push(file.fileName);
      });

      $.each(_this.files, function (index, file) {
        if (fileNameArr.indexOf(file.fileName) > -1) {
          repeatNameArr.push(file.fileName);
        }
        fileNameArr.push(file.fileName);
      });

      if (repeatNameArr.length > 0) {
        return '已上传过文件名称为【' + repeatNameArr.join(',') + '】的文件，不允许重复上传！';
      }
    }

    var hasUploadFilesLen = _this.size();
    if (_this.filesLength) {
      var surplusFilesLen = _this.filesLength - hasUploadFilesLen;
      var oLen = files.length;
      if (oLen > surplusFilesLen && surplusFilesLen !== 0) {
        return '最多还能上传' + surplusFilesLen + '个文件';
      } else if (surplusFilesLen === 0) {
        return '最多上传' + _this.filesLength + '个文件';
      }
    }

    appModal.showMask();

    if (isCover) {
      _this.clean();
    }

    var fileIds = [];
    $.each(files, function (index, file) {
      fileIds.push(file.fileId);
    });
    $.ajax({
      type: 'post',
      url: ctx + fileServiceURL.saveFilesByFileIds, // 上传文件的地址
      data: {
        fileIds: fileIds,
        fileSourceIcon: fileSourceIcon
      },
      success: function (data) {
        appModal.hideMask();
        var dataFiles = JSON.parse(data);
        if (dataFiles.success) {
          var files = dataFiles.data;
          for (var i = 0; i < files.length; i++) {
            var fileElement = files[i];
            fileElement.sourceIcon = fileSourceIcon;
            fileElement.isNew = true;
          }

          //格式 大小 数量

          _this.addFiles(files, true);
        } else {
          appModal.error(dataFiles.msg);
        }
      },
      error: function (data) {
        appModal.hideMask();
        appModal.error(data);
      }
    });
  };

  /**
   * 验证签名USB
   */
  WellFileUpload.prototype.validSignatureUSB = function () {
    // var enableSignUploadFile = false;
    /*
     * try { enableSignUploadFile = fjcaWs.OpenFJCAUSBKey(); } catch (e) {
     * try { fjcaWs.CloseUSBKey(); } catch (e) {} }
     *
     * if(!enableSignUploadFile) { //this.$containerElement.html("");
     * //附件容器中的内容清空 if(Browser.isIE()){
     * //appModal.alert("无法对表单数据进行签名，请插入USBKey数字证书!");
     * //this.$containerElement.html("无法对表单数据进行签名，请插入USBKey数字证书,再重新进入该页面!");
     * }else{ appModal.alert("当前浏览器无法对表单数据进行签名，请使用IE浏览器编辑表单!");
     * //this.$containerElement.html("当前浏览器无法对表单数据进行签名，请使用IE浏览器编辑表单!"); } }
     * else{ fjcaWs.CloseUSBKey(); }
     */

    return checkCAKey();
  };

  /**
   * 重新设置附件按钮
   */
  WellFileUpload.prototype.resetFilesSecDevFileBtn = function () {
    for (var index = 0; index < this.files.length; index++) {
      var file = this.files[index];
      this.buttons.resetSecDevFileBtn(this, file);
    }
  };

  /**
   * 调用接口添加附件按钮
   */
  WellFileUpload.prototype.addSecDevFileBtnByCode = function (code) {
    var _self = this;
    if (!_self.extraSecDevFileBtnCodes) {
      _self.extraSecDevFileBtnCodes = [];
    }

    $.each(this.secDevBtns, function (index, secDevBtn) {
      if (secDevBtn.code === code) {
        _self.extraSecDevFileBtnCodes.push(secDevBtn.code);
      }
    });

    _self.resetFilesSecDevFileBtn();
  };

  /*
   * 按钮栏相关操作
   **/
  WellFileUpload.prototype.buttons = {
    /**
     * 设置文件自适应样式
     * @param _this
     * @param file
     * @param count
     */
    setFileElementAdaptationStyle: function (_this, file, count) {
      var $fileItemElement = file.$fileItemElement;

      var fileJqueryData = _this.getFileJqueryDataByFrontUUID(file.frontUUID);
      if (null == fileJqueryData) {
        return;
      }

      var $filename = $fileItemElement.find('.filename');
      //文件名称未显示在页面上，开启定时器，等待文件名称显示再计算
      if (!$filename.is(':visible')) {
        if (!count) {
          count = 0;
        }

        if (count < 30) {
          setTimeout(function () {
            _this.buttons.setFileElementAdaptationStyle(_this, file, ++count);
          }, 100);
        }
        return;
      }

      // $filename.css({ 'max-width': 'calc(100% - 50px)', width: 'auto' });
      var filenameWidth = $filename.width();
      // fileJqueryData.filenameWidth = filenameWidth;

      var $fileDiv = $fileItemElement.find('.files_div');
      var fileContentWidth = $fileDiv.width();

      if (filenameWidth + fileJqueryData.fileNameWidthSub + _this.buttons.buttonsWidth(_this, file) > fileContentWidth) {
        fileJqueryData.showButtonsType = 2; //文件行各元素（展示按钮时）长度 大于 父级元素宽度
      } else {
        fileJqueryData.showButtonsType = 1; //文件行各元素（展示按钮时）长度 小于 父级元素宽度
      }

      if (filenameWidth + fileJqueryData.fileNameWidthSub + 5 > fileContentWidth) {
        fileJqueryData.hiddenButtonsType = 2; //文件行各元素（除按钮/隐藏按钮时）长度 大于 父级元素宽度
      } else {
        fileJqueryData.hiddenButtonsType = 1; //文件行各元素（除按钮/隐藏按钮时）长度 小于 父级元素宽度
      }

      _this.buttons._hideButtonStyle(_this, file); //默认展示隐藏按钮的样式
    },

    /**
     * 显示按钮时样式调整
     **/
    _showButtonStyle: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      var fileJqueryData = _this.getFileJqueryDataByFrontUUID(file.frontUUID);

      if (fileJqueryData.showButtonsType === 2) {
        $fileItemElement.find('.filename').css('width', 'calc(100% - ' + fileJqueryData.fileNameWidthSub + 'px)');
        $fileItemElement.find('.file_upload_item_buttons').css({
          position: 'absolute',
          right: 0
        });
      }

      if (fileJqueryData.showButtonsType === 1) {
        // $fileItemElement.find('.filename').css({ 'max-width': 'calc(100% - 50px)', width: 'auto' });
        $fileItemElement.find('.file_upload_item_buttons').css({
          position: 'absolute',
          right: ''
        });
      }
    },

    /**
     * 隐藏按钮时样式调整
     **/
    _hideButtonStyle: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      var fileJqueryData = _this.getFileJqueryDataByFrontUUID(file.frontUUID);
      // $fileItemElement.find('.file_upload_item_buttons').css('background-color', '#eee');

      if (fileJqueryData.showButtonsType === 2 && fileJqueryData.hiddenButtonsType === 2) {
        $fileItemElement.find('.filename').css('width', 'calc(100% - ' + fileJqueryData.fileNameWidthSub + 'px)');
        $fileItemElement.find('.file_upload_item_buttons').css({
          position: 'absolute',
          right: 0,
          zIndex: 10
        });
      }

      if (fileJqueryData.showButtonsType === 1 || (fileJqueryData.showButtonsType === 2 && fileJqueryData.hiddenButtonsType === 1)) {
        // $fileItemElement.find('.filename').css({ 'max-width': 'calc(100% - 50px)', width: 'auto' });
        $fileItemElement.find('.file_upload_item_buttons').css({
          position: 'absolute',
          right: '',
          zIndex: 10
        });
      }
    },

    /**
     * 计算按钮长度
     * @param _this
     * @param file
     * @param otherBtn 是否计算重新上传和取消上传按钮长度
     * @returns {number}
     */
    buttonsWidth: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;

      var $secDevDiv = $fileItemElement.find('.sec_dev_Div');
      $secDevDiv.css('opacity', 0);
      $secDevDiv.css('display', 'inline-block');

      var $secDevSpan = $fileItemElement.find('.sec_dev_span');
      var widthTotal = 0;
      $.each($secDevSpan.find('[type="button"]'), function (index, button) {
        var buttonWidth = $(button).width() + 22 + 2 + 4; //content padding border margin
        widthTotal += buttonWidth;
      });

      var $reUploadDiv = $fileItemElement.find('.reUpload_Div [type="button"]');
      if ($reUploadDiv.length > 0) {
        widthTotal += $reUploadDiv.width() + 22 + 2 + 4; //content padding border margin
      }

      var $cancelUploadDiv = $fileItemElement.find('.cancelUpload_Div [type="button"]');
      if ($cancelUploadDiv.length > 0) {
        widthTotal += $cancelUploadDiv.width() + 22 + 2 + 4; //content padding border margin
      }
      var $copyNameDiv = $fileItemElement.find('.copyName_Div [type="button"]');
      if ($copyNameDiv.length > 0) {
        widthTotal += $copyNameDiv.width() + 22 + 2 + 4; //content padding border margin
      }

      if (_this.btnShowType != '2') {
        $secDevDiv.css('display', 'none');
      }
      $secDevDiv.css('opacity', 1);

      return widthTotal;
    },

    /**
     * 显示按钮栏
     */
    showButtons: function (_this, file) {
      _this.showBtns = this;
      _this.buttons.setFileElementAdaptationStyle(_this, file);
      var $fileItemElement = file.$fileItemElement;
      _this.buttons._showButtonStyle(_this, file);
      $fileItemElement.find('.file_upload_item_buttons .sec_dev_Div').show(); //.trigger("resetGridWidth.editable");
    },

    /**
     * 隐藏按钮栏
     */
    hideButtons: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      _this.buttons._hideButtonStyle(_this, file);
      if (_this.btnShowType != '2') {
        $fileItemElement.find('.file_upload_item_buttons .sec_dev_Div').hide(); //.trigger("resetGridWidth.editable");
      }
      _this.showBtns = false;
      var _$moreFileBtnHtml = $fileItemElement.find('.file_upload_item_buttons .more_file_btn_div');
      if (_$moreFileBtnHtml.length > 0) {
        var $dropdownMenu = $('#' + _$moreFileBtnHtml.attr('dropdownMenuId'));
        setTimeout(function () {
          if (!_this.showBtns) {
            $dropdownMenu.hide();
            _$moreFileBtnHtml.dropdown('toggle');
          }
        }, 100);
      }
    },

    /**
     * 添加移入移出事件
     */
    addHoverEvent: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      $fileItemElement.unbind('mouseover').bind('mouseover', function () {
        _this.buttons.showButtons(_this, file);
      });

      $fileItemElement.unbind('mouseout').bind('mouseout', function () {
        _this.buttons.hideButtons(_this, file);
      });
    },

    /**
     * 更新上传进度
     */
    setPercentage: function (_this, frontUUID, percentage) {
      if (_this.$fileContainer) {
        // add html
        var $fileItemElement = _this.$fileContainer.find('li[frontUUID="' + frontUUID + '"]');

        if ($fileItemElement.find('.upload_progress').find('.circle').length <= 0) {
          //不存在 添加
          $fileItemElement.find('.upload_progress').html('');
          $fileItemElement
            .find('.upload_progress')
            .append(
              $(
                '<span class="circle">\n' +
                '    <span class="pie_left">\n' +
                '        <span class="left"></span>\n' +
                '    </span>\n' +
                '    <span class="pie_right">\n' +
                '        <span class="right"></span>\n' +
                '    </span>\n' +
                '    <span class="mask">\n' +
                '    </span>\n' +
                '</span>'
              )
            );
        }

        // set percentage
        if (percentage || percentage !== 0) {
          $fileItemElement.each(function () {
            var num = percentage * 3.6;
            $(this).find('.mask').html('');
            if (num <= 180) {
              $(this)
                .find('.right')
                .css({
                  transform: 'rotate(' + (num - 180) + 'deg)',
                  transition: 'transform 0.5s ease'
                });
            } else {
              $(this).find('.right').css({
                transform: 'rotate(0deg)',
                transition: 'transform 0.5s ease-in',
                background: '#488CEE'
              });
              $(this)
                .find('.left')
                .css({
                  transform: 'rotate(' + (num - 360) + 'deg)',
                  transition: 'transform 0.2s 0.5s ease-in',
                  background: '#488CEE'
                });
            }
          });
        }
      }
    },

    /**
     * 更新上传进度_md5转换中
     */
    setPercentageMd5: function (_this, frontUUID) {
      if (_this.$fileContainer) {
        var $fileItemElement = _this.$fileContainer.find('li[frontUUID="' + frontUUID + '"]');
        $fileItemElement.find('.upload_progress').html('<span class="tran_md5" ></span>');
      }
    },

    /**
     * 更新上传进度_上传成功
     */
    setPercentageSuccess: function (_this, frontUUID) {
      if (_this.$fileContainer) {
        var $fileItemElement = _this.$fileContainer.find('li[frontUUID="' + frontUUID + '"]');
        $fileItemElement
          .find('.upload_progress')
          .html('<span class="iconfont icon-ptkj-zhengquechenggongtishi" style="color: #4BB633;font-size: 20px;"></span>');
      }
    },

    /**
     * 更新上传进度_上传失败
     */
    setPercentageFail: function (_this, frontUUID) {
      if (_this.$fileContainer) {
        var $fileItemElement = _this.$fileContainer.find('li[frontUUID="' + frontUUID + '"]');
        $fileItemElement
          .find('.upload_progress')
          .html('<span class="iconfont icon-ptkj-weixianjinggaotishiyuqi" style="color: #E33033;font-size: 20px;"></span>');
      }
    },

    /**
     * 隐藏上传操作按钮
     */
    hideUploadBtnDiv: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      //取消上传按钮
      var $cancelUploadDiv = $fileItemElement.find('.cancelUpload_Div');
      $cancelUploadDiv.html('');
      $cancelUploadDiv.css('display', 'none');
      //重新上传按钮
      var $reUploadDiv = $fileItemElement.find('.reUpload_Div');
      $reUploadDiv.html('');
      //复制名称按钮
      var $copyNameDiv = $fileItemElement.find('.copyName_Div');
      $copyNameDiv.html('');
      $copyNameDiv.css('display', 'none');
    },
    /**
     *  添加取消上传按钮
     */
    addCancelUploadFileBtn: function (_this, file) {
      var cancelUploadSecDevBtn;
      $.each(_this.secDevBtns, function (index, secDevBtn) {
        if (secDevBtn.code === 'cancel_upload_btn') {
          //取消上传按钮
          cancelUploadSecDevBtn = secDevBtn;
        }
      });

      if (cancelUploadSecDevBtn) {
        var $fileItemElement = file.$fileItemElement;
        if ($fileItemElement.find('.cancelUpload_Div div').size() > 0) {
          return;
        }

        var data = _this.getFileJqueryDataByFrontUUID(file.frontUUID);
        if (!data) {
          return;
        }

        var btnLibObject = JSON.parse(cancelUploadSecDevBtn.btnLib);
        // var eventMangerObject = JSON.parse(cancelUploadSecDevBtn.eventManger);

        // 渲染按钮样式
        var _iconHtml = '';
        if (btnLibObject.iconInfo && btnLibObject.iconInfo.filePaths) {
          _iconHtml = '<i class="' + btnLibObject.iconInfo.filePaths + '"></i>';
        }

        //_cancelUploadHtml
        var _cancelUploadHtml =
          '<div type="button" uuid="' +
          cancelUploadSecDevBtn.uuid +
          '"  class="well-btn ' +
          btnLibObject.btnColor +
          ' ' +
          btnLibObject.btnInfo.class +
          ' sec_dev_btn ">' +
          _iconHtml +
          cancelUploadSecDevBtn.buttonName +
          '</div>';

        $(_cancelUploadHtml)
          .appendTo($fileItemElement.find('.cancelUpload_Div'))
          .click(function () {
            data.data.abort();
            _this.deleteFileItem(file, _this);
          });
      }
    },

    /**
     *  移除取消上传按钮
     */
    removeCancelUploadFileBtn: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      $fileItemElement.find('.cancelUpload_Div').html('');
    },

    /**
     *  添加重新上传按钮
     */
    addReUploadFileBtn: function (_this, file) {
      var reUploadSecDevBtn;
      $.each(_this.secDevBtns, function (index, secDevBtn) {
        if (secDevBtn.code === 're_upload_btn') {
          //取消上传按钮
          reUploadSecDevBtn = secDevBtn;
        }
      });

      if (reUploadSecDevBtn) {
        var $fileItemElement = file.$fileItemElement;
        if ($fileItemElement.find('.reUpload_Div div').size() > 0) {
          return;
        }

        var fileJqueryData = _this.getFileJqueryDataByFrontUUID(file.frontUUID);
        if (!fileJqueryData) {
          return;
        }

        var btnLibObject = JSON.parse(reUploadSecDevBtn.btnLib);
        // var eventMangerObject = JSON.parse(reUploadSecDevBtn.eventManger);
        // 渲染按钮样式
        var _iconHtml = '';
        if (btnLibObject.iconInfo && btnLibObject.iconInfo.filePaths) {
          _iconHtml = '<i class="' + btnLibObject.iconInfo.filePaths + '"></i>';
        }

        //_reUploadHtml
        var _reUploadHtml =
          '<div type="button" uuid="' +
          reUploadSecDevBtn.uuid +
          '" class="well-btn ' +
          btnLibObject.btnColor +
          ' ' +
          btnLibObject.btnInfo.class +
          ' sec_dev_btn ">' +
          _iconHtml +
          reUploadSecDevBtn.buttonName +
          '</div>';

        $(_reUploadHtml)
          .appendTo($fileItemElement.find('.reUpload_Div'))
          .click(function (event) {
            _this.bReupload = file;
            var md5 = fileJqueryData.data.formData.md5;
            if (md5 && WellFileUpload.maxChunkSize != undefined) {
              _this._ajaxGetFileChunkInfo(md5, fileJqueryData.data.files[0].name);
              if (
                _this.md5ChunkIndexObject[md5].hasMd5FileFlag &&
                _this.md5ChunkIndexObject[md5].uploadFiles &&
                _this.md5ChunkIndexObject[md5].uploadFiles.length &&
                _this.md5ChunkIndexObject[md5].uploadFiles.length > 0
              ) {
                $.each(_this.md5ChunkIndexObject[md5].uploadFiles, function ( /*index*/ ) {
                  _this.uploadFileSuccess(
                    this.filename,
                    this.fileID,
                    this.contentType,
                    this.digestValue,
                    this.digestAlgorithm,
                    this.certificate,
                    true,
                    this,
                    fileJqueryData.data.formData.frontUUID
                  );
                });
              } else {
                _this._setDataUploadedBytes(md5, fileJqueryData.data);
                fileJqueryData.data.submit();
              }
            } else {
              fileJqueryData.data.submit();
            }

            _this.buttons.setPercentage(_this, file.frontUUID, 0);
            $(this).remove();
          });
      }

      // _this.buttons.setFileElementAdaptationStyle(_this, file);
    },

    /**
     *  移除重新上传按钮
     */
    removeReUploadFileBtn: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      $fileItemElement.find('.reUpload_Div').html('');
    },
    /**
     *  添加复制名称按钮
     */
    addCopyNameBtn: function (_this, file) {
      var copyNameSecDevBtn;
      $.each(_this.secDevBtns, function (index, secDevBtn) {
        if (secDevBtn.code === 'copy_name_btn') {
          //取消上传按钮
          copyNameSecDevBtn = secDevBtn;
        }
      });

      if (copyNameSecDevBtn) {
        var $fileItemElement = file.$fileItemElement;
        if ($fileItemElement.find('.copyName_Div div').size() > 0) {
          return;
        }

        var fileJqueryData = _this.getFileJqueryDataByFrontUUID(file.frontUUID);
        if (!fileJqueryData) {
          return;
        }

        var btnLibObject = JSON.parse(copyNameSecDevBtn.btnLib);
        // var eventMangerObject = JSON.parse(reUploadSecDevBtn.eventManger);
        // 渲染按钮样式
        var _iconHtml = '';
        if (btnLibObject.iconInfo && btnLibObject.iconInfo.filePaths) {
          _iconHtml = '<i class="' + btnLibObject.iconInfo.filePaths + '"></i>';
        }

        //_copyNameHtml
        var _copyNameHtml =
          '<div type="button" uuid="' +
          copyNameSecDevBtn.uuid +
          '" class="well-btn ' +
          btnLibObject.btnColor +
          ' ' +
          btnLibObject.btnInfo.class +
          ' sec_dev_btn ">' +
          _iconHtml +
          copyNameSecDevBtn.buttonName +
          '</div>';

        $(_copyNameHtml)
          .appendTo($fileItemElement.find('.copyName_Div'))
          .click(function () {
            _this.copyFileNameFunc(file);
          });
      }

      // _this.buttons.setFileElementAdaptationStyle(_this, file);
    },

    /**
     *  移除重新上传按钮
     */
    removeCopyNameBtn: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      $fileItemElement.find('.copyName_Div').html('');
    },

    /**
     *  添加删除按钮
     */
    addDeleteFunction4OneItem: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      if ($fileItemElement.find('.delete_span').length > 0) {
        return;
      }
      var _html =
        "<div type='button' class='delete_span well-btn w-btn-primary w-line-btn sec_dev_btn' style='display: inline;'>删除</div>";
      $(_html)
        .appendTo($fileItemElement.find('.removeFile_Div'))
        .click(function () {
          _this.deleteFileItem(file);
        });
    },

    removeDeleteFunction4OneItem: function (file) {
      var _this = this;
      var $fileItemElement = file.$fileItemElement;
      if (!$fileItemElement) {
        return;
      }
      $fileItemElement.find('.delete_span').remove();
    },

    //         /**
    //          * 预览按钮
    //          */
    //         addPreviewFileBtn: function (_this, file) {
    //             var $fileItemElement = file.$fileItemElement;
    //             if (!_this.hostServer || $fileItemElement.find(".preview_span").size() > 0) {
    //                 // 未配置预览服务器
    //                 return;
    //             }
    //             var _html = "<div type=\"button\" class='preview_span well-btn w-btn-primary w-line-btn sec_dev_btn' style='display: inline;margin: 0 0 0 0;'>预览</div>";
    //             $(_html).appendTo($fileItemElement.find('.preview_Div'));
    //
    // //        var width = $fileItemElement.find(".files_div").width();
    // //        $fileItemElement.find(".files_div").width(width - 25);
    // //        $fileItemElement.find(".filename").width($fileItemElement.find(".files_div").width() - 40);
    //
    //             $('.preview_span', $fileItemElement).click(function () {
    //                 var wopiComponent = _this.clientServer + '/wopi/files/' + file.fileID + '?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb&_requestCode=1564653762894';
    //                 var url = _this.hostServer + "/document/online/viewer?WOPISrc=";
    //                 var winOpts = url + encodeURIComponent(wopiComponent);
    //                 window.open(winOpts, "_blank")
    //             });
    //         },

    resetSecDevFileBtn: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      $fileItemElement.find('.sec_dev_span').html('');
      this.addSecDevFileBtn(_this, file);
    },

    /**
     * 添加附件按钮（预览、下载、删除等）
     * @param _this
     * @param file
     */
    addSecDevFileBtn: function (_this, file) {
      var $fileItemElement = file.$fileItemElement;
      if (false === $fileItemElement.find('.sec_dev_span').is(':empty')) {
        // 已添加二开按钮
        return;
      }

      var $moreFileBtnUl = null;
      var uploadDropdownMenuId = new Date().getTime();
      for (var index = 0; index < _this.secDevBtns.length; index++) {
        var secDevBtn = _this.secDevBtns[index];
        if ((_this.readOnly && _this.editBtns.indexOf(secDevBtn.uuid) == -1) || secDevBtn.code == 'download_all_btn') {
          continue;
        }
        var fileExtensionFilter = false;
        var fileExtensions = secDevBtn.fileExtensions;
        if (fileExtensions) {
          var fileExtensionsSplit = fileExtensions.split(';');
          var fileExtensionsSplitStr = fileExtensionsSplit.join('|');
          var regSecDevBtnFileType = eval('/\\.(' + fileExtensionsSplitStr + ')(\\?.*)?$/');
          ///\.(png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp)(\?.*)?/;
          if (regSecDevBtnFileType.test(file.fileName)) {
            fileExtensionFilter = true;
          }
        } else {
          fileExtensionFilter = true;
        }

        var btnShow = true;
        if (
          secDevBtn.code === 'upload_btn' || //上传按钮不显示
          secDevBtn.code === 'cancel_upload_btn' || //取消上传按钮
          secDevBtn.code === 're_upload_btn' || //重新上传按钮
          (secDevBtn.code === 'delete_btn' && _this.readOnly) //readOnly && delete_btn
        ) {
          btnShow = false;
        }
        var flowSecDevBtnIds = _this.flowSecDevBtnIdStr ? _this.flowSecDevBtnIdStr.split(';') : [];
        if (_this.readOnly && flowSecDevBtnIds.length <= 0) {
          // 如果flowSecDevBtnIds有值，按flowSecDevBtnIds设置的值显示
          if (secDevBtn.code !== 'preview_btn' && secDevBtn.code !== 'download_btn' && secDevBtn.code !== 'copy_name_btn') {
            //只读，只显示预览和下载
            btnShow = false;
          }
        }
        // 调用接口添加的接口
        if (_this.extraSecDevFileBtnCodes && _this.extraSecDevFileBtnCodes.indexOf(secDevBtn.code) > -1) {
          btnShow = true;
        }

        var previewBtnFilter = true;
        if (secDevBtn.code === 'preview_btn') {
          // 判断文件类型，根据类型决定是否可以预览
          var regFileType = /\.(wps|png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp|ofd|wmv|ods|odt|odp|pot|potx|ppsm|ods|xls|xlsb|xlsm|xlsx|doc|docm|docx|dot|dotm|dotx|odt|odp|pot|potm|potx|pps|ppsm|ppsx|ppt|pptm|pptx)(\?.*)?/i;
          if (_this.hostServer && regFileType.test(file.fileName)) {} else {
            previewBtnFilter = false;
          }
        }

        // 判断文件类型，根据类型决定是否显示二开按钮
        var showSecDevBtn = fileExtensionFilter && previewBtnFilter && btnShow;

        if (showSecDevBtn) {
          var btnLibObject = JSON.parse(secDevBtn.btnLib);
          var eventMangerObject = JSON.parse(secDevBtn.eventManger);

          // 渲染按钮样式
          var _iconHtml = '';

          //_html
          var _html =
            '<div type="button" uuid="' +
            secDevBtn.uuid +
            '" data-code="' +
            secDevBtn.code +
            '" class="well-btn ' +
            btnLibObject.btnColor +
            ' ' +
            btnLibObject.btnInfo.class +
            ' sec_dev_btn ">' +
            _iconHtml +
            secDevBtn.buttonName +
            '</div>';

          if ($fileItemElement.find('.sec_dev_span>[data-code]').length === 3 && null == $moreFileBtnUl) {
            //更多按钮
            var _$moreFileBtnHtml = $(
              '<div class="btn-group more_file_btn_div"  dropdownMenuId="' +
              uploadDropdownMenuId +
              '">' +
              '    <div type="button" class="well-btn w-btn-primary w-line-btn sec_dev_btn dropdown-toggle" data-toggle="dropdown" aria-expanded="false" style="height: 26px;line-height: 26px;">\n' +
              '        <span class="add_icon">更多</span><span\n' +
              '            class="glyphicon glyphicon-menu-down" style="margin-left: 3px;font-size: 12px;"></span>\n' +
              '    </div>\n' +
              '   <div style="height: 2px;background-color: transparent;"></div>' +
              '    <ul class="dropdown-menu editable-container" role="menu" style="display: none;">\n' +
              '    </ul>\n' +
              '</div>\n'
            );

            $moreFileBtnUl = $(
              '<ul id="' + uploadDropdownMenuId + '" class="dropdown-menu" role="menu" style="min-width: 0;margin: 0;display: none;"></ul>'
            );
            _$moreFileBtnHtml.appendTo($fileItemElement.find('.sec_dev_span'));
            $moreFileBtnUl.appendTo($('body'));

            //点击下拉菜单展示
            $fileItemElement.find('.sec_dev_span').on('hide.bs.dropdown', function () {
              $moreFileBtnUl.css({
                display: 'none'
              });
            });
            _$moreFileBtnHtml.click(function () {
              if ($moreFileBtnUl.css('display') === 'block') {
                $moreFileBtnUl.css({
                  display: 'none'
                });
              } else {
                var position = _this.buttons.getPosition(_$moreFileBtnHtml);
                var calculatedOffset = _this.buttons.getCalculatedOffset('bottom', position, $(this).width() + 0, 0);
                $moreFileBtnUl.css({
                  top: calculatedOffset.top,
                  left: calculatedOffset.left,
                  display: 'block'
                });
              }
            });
            var removeUlFun = function (e) {
              if ($('[dropdownMenuId="' + uploadDropdownMenuId + '"]').length <= 0) {
                $moreFileBtnUl.remove();
                $(document).unbind('click', removeUlFun);
              }
            };
            $(document).bind('click', removeUlFun);
            $moreFileBtnUl.unbind('mouseover').bind('mouseover', function () {
              _this.buttons.showButtons(_this, file);
            });
            $moreFileBtnUl.unbind('mouseout').bind('mouseout', function () {
              _this.buttons.hideButtons(_this, file);
            });
            // 下拉菜单悬浮改背景色
            $moreFileBtnUl
              .find('li')
              .unbind('mouseover')
              .bind('mouseover', function () {
                $(this).css('background-color', '#e4eefc');
                _this.buttons.showButtons(_this, file);
              });
            $moreFileBtnUl
              .find('li')
              .unbind('mouseout')
              .bind('mouseout', function () {
                $(this).css('background-color', 'transparent');
                _this.buttons.hideButtons(_this, file);
              });
          }
          if ($moreFileBtnUl) {
            var _liBtnHtml = _iconHtml + secDevBtn.buttonName;
            $(
              '<li  uuid="' +
              secDevBtn.uuid +
              '" data-code="' +
              secDevBtn.code +
              '" style="line-height: 28px;padding:0 15px;cursor:pointer;">' +
              _liBtnHtml +
              '</li>'
            ).appendTo($moreFileBtnUl);
          } else {
            $(_html).appendTo($fileItemElement.find('.sec_dev_span'));
          }

          var buttonSelector = '[uuid="' + secDevBtn.uuid + '"]';
          //---------------------------------------ofd
          if (secDevBtn.code === 'look_up_btn') {

            var openOfdBtn = function (event) {
              require('layoutDocumentUtils').openFile(file.fileID, file.fileName);
              // window.open(location.origin + `/ofdOnlineEditView?fileName=${file.fileName}&fileId=${file.fileID}&fieldName=${_this.fieldName}`);
            };
            $fileItemElement.off('click', buttonSelector).on('click', buttonSelector, openOfdBtn);
            $moreFileBtnUl && $moreFileBtnUl.off('click', buttonSelector).on('click', buttonSelector, openOfdBtn);
          } else
          if (secDevBtn.code === 'seal_file_btn') {
            var sealOfdBtn = function (event) {
              require('layoutDocumentUtils').sealFile(file.fileID, file.fileName);
              // window.open(location.origin + `/ofdOnlineEditView?fileName=${file.fileName}&fileId=${file.fileID}&fieldName=${_this.fieldName}&isSave=true`);
            };
            $fileItemElement.off('click', buttonSelector).on('click', buttonSelector, sealOfdBtn);
            $moreFileBtnUl && $moreFileBtnUl.off('click', buttonSelector).on('click', buttonSelector, sealOfdBtn);
          } else
            //---------------------------------------
            if (secDevBtn.code === 'preview_btn') {
              var previewFileFunc = function (event) {
                var wopiComponent =
                  _this.clientServer +
                  '/wopi/files/' +
                  file.fileID +
                  '?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb&_requestCode=' +
                  new Date().getTime();
                require('filePreviewApi').preview(wopiComponent);
                // var url = _this.hostServer + '/document/online/viewer?WOPISrc=';
                // var winOpts = url + encodeURIComponent(wopiComponent);
                // window.open(winOpts, '_blank');
              };
              $fileItemElement.off('click', buttonSelector).on('click', buttonSelector, previewFileFunc);
              $moreFileBtnUl && $moreFileBtnUl.off('click', buttonSelector).on('click', buttonSelector, previewFileFunc);
            } else if (secDevBtn.code === 'download_btn') {
            var downloadFileFunc = function (event) {
              // location.href = ctx + fileServiceURL.downFile + file.fileID;
              _this.downloadURL(ctx + fileServiceURL.downFile + file.fileID);
            };
            $fileItemElement.off('click', buttonSelector).on('click', buttonSelector, downloadFileFunc);
            $moreFileBtnUl && $moreFileBtnUl.off('click', buttonSelector).on('click', buttonSelector, downloadFileFunc);
          } else if (secDevBtn.code === 'delete_btn') {
            var deleteFileFunc = function (event) {
              _this.triggerFileOperates('file-delete', file, null);
              _this.deleteFileItem(file);
            };
            $fileItemElement.on('click', buttonSelector, deleteFileFunc);
            $moreFileBtnUl && $moreFileBtnUl.on('click', buttonSelector, deleteFileFunc);
          } else if (secDevBtn.code === 'copy_name_btn') {
            var copyFileNameFunc = function (event) {
              _this.copyFileNameFunc(file);
            };
            $fileItemElement.off('click', buttonSelector).on('click', buttonSelector, copyFileNameFunc);
            $moreFileBtnUl && $moreFileBtnUl.off('click', buttonSelector).on('click', buttonSelector, copyFileNameFunc);
          } else {
            //绑定按钮事件
            if (!$.isEmptyObject(eventMangerObject.eventHandler)) {
              var eventHandler = eventMangerObject.eventHandler.eventHandler || eventMangerObject.eventHandler;
              var target = eventMangerObject.target || {};
              var opt = {
                target: target.position,
                targetWidgetId: target.widgetId,
                refreshIfExists: target.refreshIfExists,
                params: {
                  file: file
                },
                appType: eventHandler.type,
                appPath: eventHandler.path,
                view: _this.$fileContainer,
                viewOptions: defaultOptions
              };
              var fileSecDevFunc = function (event) {
                var _opt = opt;
                _opt.appContext = appContext;
                _opt.event = event;
                appContext.startApp(_opt);
              };
              $fileItemElement.on('click', buttonSelector, fileSecDevFunc);
              $moreFileBtnUl && $moreFileBtnUl.on('click', buttonSelector, fileSecDevFunc);
            }
          }
        }
      }
    },
    getPosition: function ($element) {
      $element = $element || this.$element;

      var el = $element[0];
      var isBody = el.tagName == 'BODY';

      var elRect = el.getBoundingClientRect();
      if (elRect.width == null) {
        // width and height are missing in IE8, so compute them manually; see https://github.com/twbs/bootstrap/issues/14093
        elRect = $.extend({}, elRect, {
          width: elRect.right - elRect.left,
          height: elRect.bottom - elRect.top
        });
      }
      var isSvg = window.SVGElement && el instanceof window.SVGElement;
      // Avoid using $.offset() on SVGs since it gives incorrect results in jQuery 3.
      // See https://github.com/twbs/bootstrap/issues/20280
      var elOffset = isBody ? {
        top: 0,
        left: 0
      } : isSvg ? null : $element.offset();
      var scroll = {
        scroll: isBody ? document.documentElement.scrollTop || document.body.scrollTop : $element.scrollTop()
      };
      var outerDims = isBody ? {
        width: $(window).width(),
        height: $(window).height()
      } : null;

      return $.extend({}, elRect, scroll, outerDims, elOffset);
    },
    getCalculatedOffset: function (placement, pos, actualWidth, actualHeight) {
      return placement == 'bottom' ? {
          top: pos.top + pos.height,
          left: pos.left + pos.width / 2 - actualWidth / 2
        } :
        placement == 'top' ? {
          top: pos.top - actualHeight,
          left: pos.left + pos.width / 2 - actualWidth / 2
        } :
        placement == 'left' ? {
          top: pos.top + pos.height / 2 - actualHeight / 2,
          left: pos.left - actualWidth
        } :
        /* placement == 'right' */
        {
          top: pos.top + pos.height / 2 - actualHeight / 2,
          left: pos.left + pos.width
        };
    }
  };

  /**
   * 获取指定的控件ID的文件列表信息数组,数组成员为FileInfo类对象 若指定的ctlID不存在则返回undefined
   *
   * @param ctlID
   * @returns 返回的文件信息列表，是一个由FileInfo类对象组成的数组
   */
  WellFileUpload.getFiles = function (ctlID) {
    var files = WellFileUpload.files[ctlID];
    return files;
  };

  /**
   * 产生文件夹ID
   */
  WellFileUpload.createFolderID = function () {
    var folderID = null;
    JDS.call({
      service: 'mongoFileService.createFolderID',
      data: [],
      async: false,
      success: function (result) {
        folderID = result.data;
      },
      error: function (jqXHR) {
        folderID = new UUID().id.toLowerCase();
      }
    });
    return folderID;
  };

  /**
   * 该方法仅提供给动态表单使用 通过表名，字段名，行索引组合成每个控件的id。
   *
   * @param tblName
   * @param fieldName
   * @param rowIndex
   *            行索引, 主表就直接使用0即可,从表则可以每行的行ID,该参数也可以是直接用dataUUID
   * @returns 返回控件ID
   */
  WellFileUpload.getCtlID4Dytable = function (tblName, fieldName, rowIndex) {
    var controlID = tblName + '___' + fieldName + '____' + rowIndex;

    return controlID;
  };

  /**
   * 判断value1和value2是否相等,相等返回true,不等返回false
   */
  WellFileUpload.isEqual = function (value1, value2) {
    if (WellFileUpload.isFileSetInAnotherFileSet(value1, value2) && WellFileUpload.isFileSetInAnotherFileSet(value2, value1)) {
      return true;
    } else {
      return false;
    }
  };

  /**
   * 判断value1的值集是否在value2的值集中
   */
  WellFileUpload.isFileSetInAnotherFileSet = function (value1, value2) {
    var noEqual = false;
    $.each(value1, function (i, file) {
      var fileID = file.fileID;
      var yes = false;
      $.each(value2, function (j, file2) {
        var fileId2 = file2.fileID;
        if (fileID == fileId2) {
          yes = true;
          return false;
        }
      });
      if (!yes) {
        noEqual = true;
        return false;
      }
    });

    if (noEqual) {
      return false;
    } else {
      return true;
    }
  };

  /**
   * 承办情况的全部下载按钮
   * @param url
   */
  WellFileUpload.downloadURL = function (url) {
    var hiddenIFrameID = 'hiddenDownloader' + Math.floor(Math.random() * 100000000000000000 + 1);
    var iframe = document.createElement('iframe');
    iframe.id = hiddenIFrameID;
    iframe.style.display = 'none';
    document.body.appendChild(iframe);
    iframe.src = url;
    var _this = this;
    var cnt = 0;
    var timer = setInterval(function () {
      if (cnt++ == 100) {
        clearInterval(timer);
        iframe.remove();
        return;
      }
      var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
      if (iframeDoc.readyState == 'complete' || iframeDoc.readyState == 'interactive') {
        var _text = $(iframeDoc.body).text();
        if (_text && _text.indexOf('No such file') != -1) {
          //需要等待后端响应无文件的异常
          clearInterval(timer);
          iframe.remove();
          if (_text.indexOf('try later') != -1) {
            setTimeout(function () {
              _this.downloadURL(url); //重新下载
            }, 2000);
          }
        }
        return;
      }
    }, 1000);
  };

  /**
   * 获取文件路径
   */
  WellFileUpload.prototype.getFilePath = function (data) {
    var fileInputs = data.fileInput;
    var paths = [];
    $.each(fileInputs, function (i, fileInput) {
      if (fileInput && fileInput.value) {
        paths.push(fileInput.value);
      }
    });
    return paths;
  };

  /**
   * 上传附件时是否包含本地路径,如果安全设置的值为禁用则返回true,否则还回false
   */
  WellFileUpload.prototype.isSecuritySettingAsBid = function (hint) {
    var navigatorType = '';
    if (navigator.userAgent.indexOf('MSIE') != -1 || navigator.userAgent.indexOf('.NET CLR') != -1) navigatorType = 'ie';
    else if (navigator.userAgent.indexOf('Firefox') != -1 || navigator.userAgent.indexOf('Mozilla') != -1) navigatorType = 'firefox';
    else navigatorType = 'other';

    if (navigatorType == 'ie') {
      try {
        var fso = new ActiveXObject('Scripting.FileSystemObject');
      } catch (e) {
        if (hint) {
          if (e.number == -2146827859) {
            alert(
              '由于浏览器的安全设置造成无法获取到文件的路径信息,请通过设置"工具->Internet选项->自定义级别",\n找到两个选项:\n"其他->将文件上载到服务器时包含本地目录路径"\n"ActiveX控件和插件->对未标记为可安全执行脚本的ActiveX控件初始化并执行脚本"\n将这两个选项设置为"启用"'
            );
          } else if (e.number == -2146828218) {
            alert('Unable to access local file  because of file permissions. Make sure the file and/or parent directories are readable.');
          }
        }
        return true;
      }
    } else {
      try {
        netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
      } catch (e) {
        if (hint) {
          alert(
            'Unable to access local files due to browser security settings. To overcome this, follow these steps: (1) Enter "about:config" in the URL field; (2) Right click and select New->Boolean; (3) Enter "signed.applets.codebase_principal_support" (without the quotes) as a new preference name; (4) Click OK and try loading the file again.'
          );
        }
        return true;
      }
    }

    return false;
  };

  /**
   * 上传附件成功后，添加附件图标
   *
   */
  // WellFileUpload.prototype.addFileIcon = function (fileId, iconUrl) {
  //     if (this.files.length == 0) {
  //         alert("请先上传附件！");
  //         return;
  //     }
  //
  //     var fileList = this.files;
  //
  //     for (var i = 0; i < fileList.length; i++) {
  //         var fileID = fileList[i].fileID;
  //         if (fileID == fileId) {
  //             var imgHtml = "<img src=" + iconUrl + "/>";
  //             fileList[i].$fileItemElement.find(".file_icon").html(imgHtml);
  //         }
  //     }
  // };

  WellFileUpload.prototype.setFileExt = function (fileExt) {
    if (typeof fileExt === 'string') {
      fileExt = fileExt.replace(/"/g, "'");
    }

    if (fileExt && this.$uploadInputElement) {
      try {
        this.fileExt = eval(fileExt);
        var accept = '';
        $.each(this.fileExt, function (idx, ext) {
          accept += '.' + ext + ',';
        });
        accept = accept.lastIndexOf(',') == accept.length - 1 ? accept.substr(0, accept.length - 1) : accept;
        this.$uploadInputElement.attr('accept', accept);
      } catch (ex) {}
    }
  };
  WellFileUpload.prototype.setFileMaxSize = function (fileMaxSize) {
    this.fileMaxSize = parseInt(parseFloat(fileMaxSize) * 1024 * 1024);
    if ($.isNumeric(this.fileMaxSize) && this.$uploadInputElement) {
      // this.$uploadInputElement.fileupload("option", "maxFileSize");
      this.$uploadInputElement.fileupload('option', {
        maxFileSize: this.fileMaxSize
      });
    }
  };

  WellFileUpload.prototype.setMaxFilesLength = function (length) {
    this.filesLength = parseInt(length);
    if ($.isNumeric(this.filesLength) && this.$uploadInputElement) {
      this.$uploadInputElement.fileupload('option', {
        filesLength: this.filesLength
      });
    }
  };
  WellFileUpload.prototype.parseDate = function (fDate) {
    var fullDate = fDate.split(' ')[0].split('-');
    var fullTime = fDate.split(' ')[1].split(':');
    return new Date(
      fullDate[0],
      fullDate[1] - 1,
      fullDate[2],
      fullTime[0] != null ? fullTime[0] : 0,
      fullTime[1] != null ? fullTime[1] : 0,
      fullTime[2] != null ? fullTime[2] : 0
    );
  };
  WellFileUpload.prototype.triggerFileOperates = function (operation, bValue, aValue) {
    var self = this;
    var data = {
      bValue: bValue,
      aValue: aValue,
      operation: operation
    };
    self.$containerElement.trigger('file-events', data);
    self.$containerElement.trigger(operation, data);
  };
});

$(function () {
  WellFileUpload4Icon = function (
    ctlID, // 控件唯一ID
    style, // 风格,参考对象iconFileControlStyle
    fileExt,
    fileMaxSize,
    fileMaxNum,
    defaultView,
    operateBtns,
    createHistory,
    downloadAllType,
    displayName
  ) {
    this.ctlID = ctlID; // 文件上传input元素的元素ID
    // this.bodyFiles = [];
    this.style = style == undefined ? iconFileControlStyle.IconAndBody : style; // 该控件只显示图标附件，还是只显示正文，还是图标正文都要,默认都显示

    this.filePaths = []; // 保存文件的路径
    this.splitor = '|';
    this.fileMaxSize = fileMaxSize;
    this.filesLength = fileMaxNum;
    this.fileExt = fileExt;
    this.defaultView = defaultView;
    this.operateBtns = operateBtns || ['1', '4', '6', '15', '14', '7', '2', '8', '9', '3', '10', '11', '12', '13']; // 兼容旧数据
    // this.operateBtns = operateBtns || ["1", "6", "14", "7", "2", "8", "9", "3", "10", "11", "12", "13"]; // 兼容旧数据
    this.createHistory = createHistory == 0 ? false : true;
    this.downloadAllType = downloadAllType || '1';
    this.displayName = displayName;
    var tip = [];
    if (fileExt) {
      var _fileExt = eval(fileExt).join(',');
      tip.push('支持' + _fileExt + '格式附件');
    }
    if (fileMaxSize) {
      if (fileMaxSize >= 1) {
        tip.push('附件大小不超过' + fileMaxSize + 'MB');
      } else {
        tip.push('附件大小不超过' + formatSize(fileMaxSize * 1024, 2, ['KB']));
      }
    }
    if (fileMaxNum) {
      tip.push('最多上传' + fileMaxNum + '个');
    }
    this.tip = tip;
  };
  WellFileUpload4Icon.btnCodes = {
    view: '7',
    download: '2',
    edit: '8',
    copyName: '13',
    rename: '9',
    delete: '3',
    moveup: '10',
    movedown: '11',
    history: '12',
    replace: '21',
    openLayoutDoc: '22',
    sealLayoutDoc: '23',
    openFileWithSoft: '24'
  };
  WellFileUpload4Icon.prototype = new WellFileUpload('formWellFileUpload4Icon', 'icon');
  /**
   * 组装html, 将从文件系统中获取到的文件列表组装成html。
   */
  WellFileUpload4Icon.prototype.initWithLoadFilesFromFileSystem = function (
    uploadable /* 是否则有上传的权限 */ ,
    downable /* 是否具有下载的权限 */ ,
    $containerElement /* 存放该附件的容器 */ ,
    signature /* 是否签名 */ ,
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
    var readOnly = false;
    if (!uploadable) {
      readOnly = true;
    }

    this.init(uploadable, downable, $containerElement, signature, dbFiles);
  };
  WellFileUpload4Icon.prototype.createToolbarContainer = function () {
    var _this = this;
    var toolbarId = 'toolbar_' + _this.ctlID;
    var fileHtml = '<div id="' + toolbarId + '" class="fileUpdateload">'; // 文件列表容器
    fileHtml +=
      '<span class="left-counter"><span class="file_icon" style="display: block;float: left;margin-right: 8px;"></span>附件<span style="color:#999999;margin-left: 4px;">(<span class="counter">0</span>)</span><span class="fileTip" style="display:none;margin:0;"><i class="iconfont icon-ptkj-tishishuoming" style="color:#999;vertical-align:middle;"></i></span></span>' +
      '<input name="' +
      _this.ctlID +
      '" style="display: none" type="file">';
    fileHtml += '<span style="float:right;line-height: 32px;" class="fileUpload-btn">';
    fileHtml +=
      '<a class="allSelect"  style="cursor:pointer;"><label style="height: 32px;"><input type="checkbox" class="checkAll" id="checkAll"/>全选</label></a>';
    // fileHtml += '<a class="downloadaszip" style="cursor:pointer;"><i
    // class="icon iconfont icon-ptkj-xiazai"></i>批量下载</a>';
    fileHtml +=
      '<a class="upload"  style="cursor:pointer;"><label style="padding: 0px;margin: 0px;border-width: 0px;line-height: normal;"><input type="file" style="width: 0px;height: 0px;position: absolute;opacity: 0;z-index: -1;"/><i class="icon iconfont icon-ptkj-shangchuan"></i>添加附件</label></a>';
    var bodyApi = window.getBodyApi && window.getBodyApi();
    if (bodyApi && bodyApi.pasteFile) {
      fileHtml += '<a class="paste" style="cursor:pointer;"><i class="icon iconfont icon-ptkj-niantie"></i>粘贴附件</a>';
    }
    fileHtml += '<a class="download"  style="cursor:pointer;"><i class="icon iconfont icon-ptkj-xiazai"></i>批量下载</a>';
    // fileHtml += '<a class="clean" style="cursor:pointer;"><i class="icon
    // iconfont icon-ptkj-shanchu"></i>全部删除</a>';
    fileHtml += '<a class="delete" style="cursor:pointer;"><i class="icon iconfont icon-ptkj-shanchu"></i>批量删除</a>';
    var wellOfficeAssistant = window.wellOfficeAssistant;
    if ((wellOfficeAssistant && wellOfficeAssistant.saveAs) || (bodyApi && bodyApi.saveAsFile)) {
      fileHtml += '<a class="downloadSaveAs" style="cursor:pointer;"><i class="icon iconfont icon-ptkj-xiazai"></i>另存为</a>';
    }

    // fileHtml += '<a class="openLayoutDoc" style="cursor:pointer;"><i class="icon iconfont icon-xmch-xiangmuchaxun"></i>查阅</a>';
    // fileHtml += '<a class="sealLayoutDoc" style="cursor:pointer;"><i class="icon iconfont icon-szgy-gaizhang"></i>盖章</a>';

    // fileHtml += '<a class="local-file-history" style="cursor:pointer;"><i class="icon iconfont icon-ptkj-lishi"></i>历史记录 </a>';
    fileHtml +=
      '<a class="switchbtn" style="cursor:pointer;"><span class="defaultViewBtn"></span><i class="icon iconfont icon-ptkj-xianmiaojiantou-xia"></i></a>';
    fileHtml += '</span>';
    fileHtml += '</div>';
    _this.$toolbarContainer = $(fileHtml);
    _this.$containerElement.append(_this.$toolbarContainer);
    _this.$toolbarContainer.find('.upload')[_this.operateBtns.indexOf('1') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.download')[_this.operateBtns.indexOf('6') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.downloadSaveAs')[_this.operateBtns.indexOf('15') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.delete')[_this.operateBtns.indexOf('14') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.paste')[_this.operateBtns.indexOf('4') > -1 ? 'show' : 'hide']();

    // _this.$toolbarContainer.find('.openLayoutDoc')[_this.operateBtns.indexOf('22') > -1 ? 'show' : 'hide']();
    // _this.$toolbarContainer.find('.sealLayoutDoc')[(_this.operateBtns.indexOf('23') > -1 && !_this.readOnly) ? 'show' : 'hide']();

    _this.$toolbarContainer
      .find('.allSelect')[
        _this.operateBtns.indexOf('6') > -1 || (_this.operateBtns.indexOf('14') > -1 && !_this.readOnly) || _this.operateBtns.indexOf('15') > -1 ?
        'show' :
        'hide'
      ]();
    _this.$attachCounter = _this.$toolbarContainer.find('.counter'); // 附件计数器
    _this.$toolbarContainer.find('.allSelect .checkAll').change(function () {
      if (this.checked) {
        _this.$fileContainer.find('.itemChecked').attr('checked', true);
      } else {
        _this.$fileContainer.find('.itemChecked').attr('checked', false);
      }
    });
    if ($.isArray(_this.tip) && _this.tip.length) {
      _this.$toolbarContainer.find('.fileTip').show();
      _this.$toolbarContainer.find('.fileTip').popover({
        html: true,
        placement: 'top',
        container: 'body',
        trigger: 'hover',
        offSet: ['15px', '15px'],
        template: '<div class="popover tip-file" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
        content: function () {
          return '<div style="font-size: 12px;color: #999">' + _this.tip.join('; ') + '</div>';
        }
      });
    }
    var url = ctx + fileServiceURL.saveFiles; // 上传文件的地址
    var $fileElement = _this.$toolbarContainer.find('.upload input[type=file]');
    try {
      _this.fileExt = eval(_this.fileExt);
      var accept = '';
      $.each(_this.fileExt, function (idx, ext) {
        accept += '.' + ext + ',';
      });
      accept = accept.lastIndexOf(',') == accept.length - 1 ? accept.substr(0, accept.length - 1) : accept;
      $fileElement.attr('accept', accept);
    } catch (ex) {}
    _this.checkErrors = [];
    $fileElement
      .fileupload({
        url: url,
        iframe: $.browser.msie && $.browser.version < 10,
        dataType: 'json',
        autoUpload: true,
        formData: {},
        maxFileSize: 2000000000, // 2000 MB
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true,
        // pasteZone: _this.$containerElement,
        dropZone: _this.$containerElement
      })
      .on('fileuploadadd', function (e, data) {
        appModal.showMask();
        var toReplaceFile = _this.toReplaceFile;
        data.formData = $.extend(data.formData, {
          origUuid: toReplaceFile && toReplaceFile.fileID,
          source: toReplaceFile ? '替换' : '添加附件',
          bsMode: _this.isBsEditMode()
        });
      })
      .on('fileuploadsubmit', function (e, data) {
        var maxSize = parseInt(parseFloat(_this.fileMaxSize) * 1024 * 1024);
        if (data.files[0].size > maxSize) {
          var fileMaxSize = '附件大小不能超过' + (maxSize > 0 ? formatSize(maxSize) : '2000M');
          if (_this.checkErrors.indexOf(fileMaxSize) == -1) {
            _this.checkErrors.push(fileMaxSize);
          }
        }
        var fileArr = data.files[0].name.split('.');
        var fileType = '.' + fileArr[fileArr.length - 1];
        var accept = this.getAttribute('accept');
        var oLen = data.originalFiles.length;
        if (accept) {
          var acceptArr = accept.split(',');
          if (acceptArr.indexOf(fileType) < 0) {
            if (_this.checkErrors.indexOf('只能上传' + accept + '格式附件') == -1) {
              _this.checkErrors.push('只能上传' + accept + '格式附件');
            }
          }
        }
        var hasUploadFilesLen = _this.size();
        if (_this.toReplaceFile) {
          if (_this.toReplaceFile.fileName === data.files[0].name || _this.toReplaceFile.filename === data.files[0].name) {
            // 同名替换
          } else if (_this.isFileNameExist1(data.files[0].name, data.files[0])) {
            appModal.hideMask();
            return false;
          }
        } else if (_this.filesLength) {
          var surplusFilesLen = _this.filesLength - hasUploadFilesLen;
          if (oLen > surplusFilesLen) {
            if (_this.checkErrors.indexOf('附件数量最多不能超过' + _this.filesLength + '个') == -1) {
              _this.checkErrors.push('附件数量最多不能超过' + _this.filesLength + '个');
            }
          }
        }
        if (_this.checkErrors.length > 0) {
          if (data.onceValidate || data.originalFiles[oLen - 1].name === data.files[0].name) {
            appModal.error(_this.checkErrors.join('，') + '，请检查后重新上传');
            _this.checkErrors = [];
          }
          appModal.hideMask();
          return false;
        }

        if (_this.beforeFileUpload && _this.beforeFileUpload(e, data) === false) {
          appModal.hideMask();
          return false;
        }
        return true;
      })
      .on('fileuploaddone', function (e, data) {
        appModal.hideMask();
        $.each(data.result.data, function (index, file) {
          file.fileName = file.filename;
          var toReplaceFile = _this.toReplaceFile;
          if (toReplaceFile && toReplaceFile.fileID) {
            _this.toReplaceFile = null;
            //if (!_this.isFileNameExist1(file.fileName, file)) {
            _this.del([toReplaceFile], false); // 删除不确认
            _this.addFile(file);
            _this.triggerFileOperates('file-replace', toReplaceFile, file);
            //}
          } else {
            _this.addFile(file);
            _this.triggerFileOperates('file-add', null, file);
          }
        });
        // _this.triggerFileChangeEvent();
      })
      .on('fileuploadprocessalways', function (e, data) {
        var index = data.index,
          file = data.files[index];
        if (file.error == 'File is too large') {
          appModal.error('附件大小不能超过' + (data.maxFileSize > 0 ? formatSize(data.maxFileSize) : '2000M'));
          appModal.hideMask();
        }
      })
      .on('fileuploadfail', function (e, data) {
        appModal.hideMask();
        var jqXHR = data.jqXHR;
        if (jqXHR && jqXHR.responseJSON && jqXHR.responseJSON.msg) {
          return appModal.alert(jqXHR.responseJSON.msg);
        }
        appModal.error('可能您上传的文件格式不被支持!!!');
      });
    $fileElement[_this.multiple ? 'attr' : 'removeAttr']('multiple', 'multiple');

    _this.$toolbarContainer.find('.clean').click(function () {
      // 清空
      _this.clean();
    });

    _this.$toolbarContainer.find('.paste').click(function () {
      // 粘贴
      _this.pasteFile(false);
    });

    _this.$toolbarContainer.find('.delete').click(function () {
      // 删除
      var files = _this.getCheckedFiles();
      _this.del(files);
      $.each(files, function (idx, file) {
        _this.triggerFileOperates('file-delete-batch', file, null);
      });
    });

    _this.$toolbarContainer.find('.download').click(function () {
      // 下载
      _this.download(null, 'all');
    });

    _this.$toolbarContainer.find('.downloadSaveAs').click(function () {
      // 另存为
      _this.downloadSaveAs();
    });

    _this.$toolbarContainer.find('.downloadaszip').click(function () {
      // 打包下载
      _this.downloadAsZIP();
    });

    _this.$toolbarContainer.find('.openLayoutDoc').click(function () {
      // 打包下载
      _this.openLayoutDocumentFile();
    });

    _this.$toolbarContainer.find('.sealLayoutDoc').click(function () {
      // 打包下载
      _this.sealLayoutDocumentFile();
    });

    var viewData = {
      list: {
        icon: 'icon-wsbs-liebiaoshitu',
        text: '列表视图'
      },
      table: {
        icon: 'icon-ptkj-biaogeshitu',
        text: '表格视图'
      },
      image: {
        icon: 'icon-ptkj-tubiaoshitu',
        text: '图标视图'
      }
    };
    var $switchbtn = _this.$toolbarContainer.find('.switchbtn');
    var menuHtml = '<ul class="file-view-menu dropdown-menu">';
    menuHtml += '<li><a href="#" data-view="list"><i class="iconfont icon-wsbs-liebiaoshitu"></i>列表视图</a></li>';
    menuHtml += '<li><a href="#" data-view="table"><i class="iconfont icon-ptkj-biaogeshitu"></i>表格视图</a></li>';
    menuHtml += '<li><a href="#" data-view="image"><i class="iconfont icon-ptkj-tubiaoshitu"></i>图标视图</a></li>';
    menuHtml += '</ul>';
    var $menu = $(menuHtml);
    var uiStyles = 'ui-list ui-table ui-image';
    $menu.on('click', 'a[data-view]', function (event) {
      event.preventDefault();
      event.stopPropagation();
      $switchbtn.trigger('click');
      var $this = $(this);
      $this.parent().siblings().show();
      $this.parent().hide();
      var dataView = $(this).attr('data-view');
      $('.defaultViewBtn', _this.$toolbarContainer)
        .html('<i class="iconfont ' + viewData[dataView].icon + '"></i>' + viewData[dataView].text)
        .attr('data-views', dataView);
      _this.$containerElement.removeClass(uiStyles);
      _this.$containerElement.addClass('ui-' + $(this).attr('data-view'));
      _this.changeTimeTitle(dataView);
      _this.triggerFileViewChangeEvent();
    });
    // 默认图标视图
    if (_this.defaultView) {
      var newView = _this.defaultView.substring(3);
      _this.$containerElement.addClass(_this.defaultView);
      $('.defaultViewBtn', _this.$toolbarContainer)
        .html('<i class="iconfont ' + viewData[newView].icon + '"></i>' + viewData[newView].text)
        .attr('data-views', newView);
    } else {
      var newView = 'image';
      _this.$containerElement.addClass('ui-image');
      $('.defaultViewBtn', _this.$toolbarContainer)
        .html('<i class="iconfont icon-ptkj-tubiaoshitu"></i>图标视图')
        .attr('data-views', 'image');
    }
    $menu
      .find("a[data-view='" + newView + "']")
      .parent()
      .hide();

    $switchbtn.click(function () {
      // 切换视图
      var $this = $(this);
      var offset = $this.offset();
      $menu.css({
        left: offset.left,
        top: offset.top + 32
      });
      if ($menu.is(':visible')) {
        $menu.slideUp(100, function (t) {
          $menu.detach();
        });
        $this.find('i.icon').removeClass('icon-ptkj-xianmiaojiantou-shang').addClass('icon-ptkj-xianmiaojiantou-xia');
      } else {
        $menu.appendTo(document.body).slideDown(100);
        $this.find('i.icon').removeClass('icon-ptkj-xianmiaojiantou-xia').addClass('icon-ptkj-xianmiaojiantou-shang');
      }
    });

    _this.$toolbarContainer.find('.local-file-history').click(function () {
      // 查看本地历史文件
      _this.showLocalFileHistory();
    });
  };

  WellFileUpload4Icon.prototype.changeTimeTitle = function (dataView) {
    var $lis = this.$containerElement.find('li');
    $.each($lis, function (indx, item) {
      var fileCreatetime = $(item).find('.fileCreatetime');
      if (dataView == 'table') {
        fileCreatetime.attr('title', fileCreatetime.text());
      } else {
        fileCreatetime.removeAttr('title');
      }
    });
  };

  WellFileUpload4Icon.prototype.createUuid = function () {
    return new UUID().id.toLowerCase();
  };

  WellFileUpload4Icon.prototype.open = function (fileID) {
    var fileInfo = this.getFile(fileID);
    // fileInfo.$fileItemElement.trigger("dblclick");
    this.openLocalFile(fileInfo);
  };

  // 触发文件变更事件
  WellFileUpload4Icon.prototype.triggerFileChangeEvent = function () {
    var self = this;
    self.$containerElement && self.$containerElement.trigger('filechange');
  };

  // 触发文件视图变更事件
  WellFileUpload4Icon.prototype.triggerFileViewChangeEvent = function () {
    var self = this;
    self.$containerElement && self.$containerElement.trigger('fileViewChange');
  };

  /**
   * 设置是否可以下载
   */
  (WellFileUpload4Icon.prototype.setAllowDownload = function (allowDownload) {
    if (allowDownload) {
      this.enableDownLoadFunction();
    } else {
      this.disableDownLoadFunction();
    }
    this.allowDownload = true == allowDownload;
  }),
  /**
   * 设置是否可以删除
   */
  (WellFileUpload4Icon.prototype.setAllowDelete = function (allowDelete) {
    if (allowDelete) {
      this.enableDeleteFunction();
    } else {
      this.disableDeleteFunction();
    }
    this.allowDelete = true == allowDelete;
  }),
  /**
   * 设置是否可以上传
   */
  (WellFileUpload4Icon.prototype.setAllowUpload = function (allowUpload) {
    if (allowUpload) {
      this.enableUploadFunction();
    } else {
      this.disableUploadFunction();
    }
    this.allowUpload = true == allowUpload;
  }),
  (WellFileUpload4Icon.prototype.setReadOnly = function () {
    if (this.fileLength == 0) {
      this.$fileContainer.hide();
      this.$toolbarContainer.hide();
    }
    this.disableUploadFunction();
    // this.disableDeleteFunction();
    this.$toolbarContainer.find('.delete').hide();
    this.$toolbarContainer.find('.paste').hide();
    this.$toolbarContainer.find('.sealLayoutDoc').hide();
    this.readOnly = true;
    this.disableShowBtnFunction();
    this.$toolbarContainer.find('.allSelect')[this.operateBtns.indexOf('6') > -1 || this.operateBtns.indexOf('15') > -1 ? 'show' : 'hide']();
    this.$fileContainer.find(".icoItem").find(".itemChecked").next().css({
      visibility: this.operateBtns.indexOf('6') > -1 || this.operateBtns.indexOf('15') > -1 ?
        'visible' : 'hidden'
    });
  });

  WellFileUpload4Icon.prototype.setEditable = function () {
    if (this.operateBtns.indexOf('1') > -1) {
      this.enableUploadFunction();
    }
    if (this.operateBtns.indexOf('3') > -1) {
      this.enableDeleteFunction();
    }
    if (this.operateBtns.indexOf('23') > -1) {
      this.$toolbarContainer.find('.sealLayoutDoc').show();
    }

    this.readOnly = false;
  };

  WellFileUpload4Icon.prototype.enableDownLoadFunction = function () {
    // this.$toolbarContainer.find(".downloadSaveAs").show();
    this.$toolbarContainer.find('.btn-download').show();
  };

  WellFileUpload4Icon.prototype.disableDownLoadFunction = function () {
    // this.$toolbarContainer.find(".downloadSaveAs").hide();
    this.$toolbarContainer.find('.btn-download').hide();
  };

  WellFileUpload4Icon.prototype.enableDeleteFunction = function () {
    this.$toolbarContainer.find('.clean').show();
    this.$toolbarContainer.find('.btn-delete').show();
  };

  WellFileUpload4Icon.prototype.disableDeleteFunction = function () {
    this.$toolbarContainer.find('.clean').hide();
    this.$toolbarContainer.find('.btn-delete').hide();
  };

  WellFileUpload4Icon.prototype.enableUploadFunction = function () {
    var self = this;
    if (this.operateBtns.indexOf('1') > -1) {
      self.$fileContainer && self.$fileContainer.show();
      if (self.$toolbarContainer) {
        self.$toolbarContainer.show();
        self.$toolbarContainer.find('.upload').show();
        if (self.$toolbarContainer.find('.upload').length > 0 || self.$toolbarContainer.find('.paste').length > 0) {}
      }
    }
  };

  WellFileUpload4Icon.prototype.disableUploadFunction = function () {
    this.$toolbarContainer.find('.upload').hide();
  };

  WellFileUpload4Icon.prototype.disableShowBtnFunction = function () {
    var _this = this;
    $.each(this.$fileContainer.find('.icoItem'), function (index, item) {
      _this.renderButtons($(item), _this.files[index], false);
    });
  };

  WellFileUpload4Icon.prototype.preview = function (files) {
    var _this = this;
    var file = files[0];
    var fileName = file.fileName || file.filename;

    function onlinePriview() {
      var wopiComponent = _this.clientServer + '/wopi/files/' + file.fileID + '?access_token=1564653762894';
      require('filePreviewApi').preview(wopiComponent);
      // var url = _this.hostServer + '/document/online/viewer?WOPISrc=';
      // var winOpts = url + encodeURIComponent(wopiComponent);
      // window.open(winOpts, '_blank');
    }
    var fileExt = _this.getFileExt(fileName);
    if (_this.readerExts && fileExt && _this.readerExts.indexOf(fileExt) > -1) {
      getBodyApi()
        .checkWPSProtocol()
        .then(function () {
          var openOptions = {
            newFileName: fileName,
            docId: file.fileID || _this.createUuid(),
            fileName: location.origin + ctx + '/office/wps/download?fileID=' + file.fileID,
            buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
            disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
            userName: SpringSecurityUtils.getCurrentUserName(),
            openType: {
              protectType: 3
            }
          };
          if (_this.isOfdOrPfd(fileName)) {
            getBodyApi().openOfd(openOptions);
          } else if (_this.isWord(fileName)) {
            getBodyApi().openDoc(openOptions);
          } else if (_this.isExcel(fileName)) {
            getBodyApi().openET(openOptions);
          } else if (_this.isPowerPoint(fileName)) {
            getBodyApi().openWpp(openOptions);
          } else {
            onlinePriview(file);
          }
        })
        .fail(onlinePriview);
    } else {
      onlinePriview(file);
    }
  };

  WellFileUpload4Icon.prototype.getFileExt = function (fileName) {
    if (fileName && fileName.lastIndexOf('.') > 0) {
      return fileName.substr(fileName.lastIndexOf('.') + 1);
    }
  };

  WellFileUpload4Icon.prototype.isWord = function (fileName) {
    var wpsSuffix = ['doc', 'docx', 'wps', 'wpsx'];
    return $.inArray(this.getFileExt(fileName), wpsSuffix) > -1;
  };
  WellFileUpload4Icon.prototype.isExcel = function (fileName) {
    var etSuffix = ['xls', 'xlsx', 'et', 'etx'];
    return $.inArray(this.getFileExt(fileName), etSuffix) > -1;
  };
  WellFileUpload4Icon.prototype.isPowerPoint = function (fileName) {
    var wppSuffix = ['ppt', 'pptx', 'dps', 'dpsx'];
    return $.inArray(this.getFileExt(fileName), wppSuffix) > -1;
  };
  WellFileUpload4Icon.prototype.isOfdOrPfd = function (fileName) {
    var fSuffix = ['ofd', 'pdf'];
    return $.inArray(this.getFileExt(fileName), fSuffix) > -1;
  };

  WellFileUpload4Icon.prototype.clean = function (isHint) {
    if (typeof isHint == 'undefined') {
      isHint = true;
    }
    if (isHint && this.files.length && !confirm('确定全部删除吗?')) {
      return;
    }
    for (var i = 0; i < this.files.length; i++) {
      var file = this.files[i];
      file.$fileItemElement.remove();
    }
    this.filePaths.length = 0;
    this.files.length = 0;

    this.updateCounter();
    this.triggerFileChangeEvent();
  };

  WellFileUpload4Icon.prototype.clear = function (isHint) {
    this.clean(isHint);
  };

  WellFileUpload4Icon.prototype.del = function (files, needConfirm) {
    var _this = this;
    files = files || _this.getCheckedFiles();
    if (files.length == 0) {
      // alert("请选择附件");
      appModal.error({
        message: '请选择附件'
      });
      return;
    }
    var indexs = [];
    for (var j = 0; j < files.length; j++) {
      for (var i = 0; i < this.files.length; i++) {
        var file = this.files[i];
        if (files[j] === file || files[j].fileID === file.fileID) {
          indexs.push(i);
          break;
        }
      }
    }
    if (indexs.length > 0) {
      if (needConfirm === false) {} else if (!confirm('确定删除吗?')) {
        return;
      }
    }
    var j = 0;
    for (var i = 0; i < indexs.length; i++) {
      this.files[indexs[i] - j].$fileItemElement.remove();
      this.files.splice(indexs[i] - j, 1);
      var t = this.filePaths.splice(indexs[i] - j, 1);
      j++;
    }

    this.updateCounter();
    this.triggerFileChangeEvent();
  };
  WellFileUpload4Icon.prototype.download = function (files, type) {
    var _this = this;
    // 标记不卸载窗口内容
    window.unloadContent = false;
    files = files || this.getCheckedFiles();
    if (files.length == 0) {
      // alert("请选择附件");
      return appModal.error({
        message: '请选择附件'
      });
    } else {
      if (type && type == 'all') {
        var downFiles = [];
        for (var i = 0; i < files.length; i++) {
          if (_this.downloadAllType == '2') {
            var url = ctx + fileServiceURL.downFile + files[i].fileID;
            _this.downloadURL(url);
          } else {
            downFiles.push({
              fileID: files[i].fileID
            });
          }
        }

        if (_this.downloadAllType == '2') {
          setTimeout(function () {
            $("iframe[id^='hiddenDownloader']").remove();
          }, 1000);
        } else {
          if (_this.displayName != undefined) {
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

          var downAllFilesUrl =
            ctx + fileServiceURL.downAllFiles + urlencode(JSON.stringify(downFiles)) + '&includeFolder=false&fileName=' + title;
          _this.downloadURL(downAllFilesUrl);
        }
      } else {
        _this.downloadURL(ctx + fileServiceURL.downFile + files[0].fileID);
      }
    }
  };

  WellFileUpload4Icon.prototype.pasteFile = function (needConfirm) {
    var _this = this;
    if (window.getBodyApi && getBodyApi().pasteFile) {
      appModal.showMask('上传中...', 'body', 86400 * 1000);
      try {
        getBodyApi().pasteFile({
          needConfirm: needConfirm,
          validate: function (localFiles) {
            var event = $.Event('fileuploadsubmit');
            _this.$toolbarContainer.find('a.upload input[type=file]:first').trigger(event, {
              onceValidate: true, // 一次性校验
              files: localFiles,
              originalFiles: localFiles
            });
            if (event.isDefaultPrevented()) {
              return false;
            }
          },
          success: function (result) {
            $.each(result.data, function (idx, file) {
              file.creator = file.creator || SpringSecurityUtils.getCurrentUserId();
              file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
              file.uploadTime = file.uploadTime || new Date().format('yyyy-MM-dd HH:mm:ss');
              file.isNew = false;
              file.fileName = file.filename;
              _this.addFile(file);
              _this.triggerFileOperates('file-paste', null, file);
            });
          },
          error: function (result) {
            console.log('downloadSaveAs error:' + result);
          },
          complete: function (result) {
            console.log('downloadSaveAs complete:' + result);
          }
        });
      } finally {
        appModal.hideMask();
      }
    }
  };

  WellFileUpload4Icon.prototype.downloadSaveAs = function (files, needConfirm) {
    var _this = this;
    files = files || this.getCheckedFiles();
    if (files.length == 0) {
      // alert("请选择附件");
      return appModal.alert({
        message: '请选择附件'
      });
    } else if (files.length == 1) {
      var downOptions = {
        filename: files[0].fileName || files[0].filename,
        url: window.location.origin + ctx + fileServiceURL.downFile + files[0].fileID,
        success: function (result) {
          console.log('downloadSaveAs success:' + result);
        },
        error: function (result) {
          console.log('downloadSaveAs error:' + result);
        },
        complete: function (result) {
          console.log('downloadSaveAs complete:' + result);
        }
      };
      if (window.getBodyApi && getBodyApi().saveAsFile) {
        downOptions.url = window.location.origin + ctx + fileServiceURL.downloadOcx + files[0].fileID;
        return getBodyApi().saveAsFile(downOptions);
      }
      window.wellOfficeAssistant.saveAs(downOptions);
    } else {
      return appModal.alert({
        message: '该操作只针对单一附件'
      });
      /*
            var downFiles = [];
            for (var i = 0; i < files.length; i++) {
                downFiles.push({
                    fileID: files[i].fileID
                });
            };
            var downOptions = {
                filename: (new Date()).format("yyyyMMddHHmm") + ".zip",
                url: window.location.origin + ctx + fileServiceURL.downAllFiles + urlencode(JSON.stringify(downFiles)),
                success: function(result) {
                    console.log("downloadSaveAs success:" + result);
                },
                error: function(result) {
                    console.log("downloadSaveAs error:" + result);
                },
                complete: function(result) {
                    console.log("downloadSaveAs complete:" + result);
                }
            }
            if (window.getBodyApi && getBodyApi().saveAsFile) {
                downOptions.url = window.location.origin + ctx + fileServiceURL.downAllFiles4ocx + urlencode(JSON.stringify(downFiles));
                return getBodyApi().saveAsFile(downOptions);
            }
            window.wellOfficeAssistant.saveAs(downOptions);*/
    }
  };

  // 参数示例，对象：{field:"name",title:"名称",width:"100"}
  // 参数示例，数组：[{field:"name",title:"名称",width:"100"}]
  WellFileUpload4Icon.prototype.addHistoryColumns = function (columns) {
    if ($.isArray(columns)) {
      this.historyColumns = columns;
    } else if (columns instanceof Object) {
      this.historyColumns = [columns];
    } else {
      this.historyColumns = [];
      console.log('参数不符合规范，请传数组或对象！');
    }
  };

  WellFileUpload4Icon.prototype.showLocalFileHistory = function (files) {
    var _this = this;
    files = files || this.getCheckedFiles();
    if (!files || files.length == 0) {
      return appModal.alert({
        message: '请选择附件'
      });
    } else if (files.length > 1) {
      return appModal.alert({
        message: '只能选择1个附件'
      });
    }

    var hisFiles = [];
    var loaclHisFiles = [];
    $.ajax({
      type: 'get',
      async: false,
      dataType: 'json',
      url: ctx + '/repository/file/mongo/queryFileHistory?fileID=' + files[0].fileID,
      success: function (result) {
        hisFiles = hisFiles.concat(result.data);
        var $historyDialog = appModal.dialog({
          title: '历史记录',
          message: _this.getHistoryContent(),
          size: 'middle',
          height: 600,
          width: 900,
          zIndex: 10000,
          shown: function () {
            if (window.getBodyApi && getBodyApi().queryLocalFileHistory) {
              getBodyApi().queryLocalFileHistory({
                docId: files[0].origUuid || files[0].fileID,
                success: function (result) {
                  loaclHisFiles = loaclHisFiles.concat(result);
                  if (loaclHisFiles.length > 0) {
                    $('.fileLocalHistoryTip', $historyDialog).show();
                  } else {
                    $('.fileLocalHistoryTip', $historyDialog).hide();
                  }
                },
                error: function (error) {
                  $('.fileLocalHistoryTip', $historyDialog).hide();
                }
              });
            } else {
              $('.fileLocalHistoryTip', $historyDialog).hide();
            }

            var columns = [{
                field: 'timestamp',
                title: '创建时间',
                width: '180'
              },
              {
                field: 'creator',
                title: '创建人',
                width: '120'
              },
              {
                field: 'source',
                title: '操作类型',
                width: '120'
              },
              {
                field: 'name',
                title: '文件名'
              },
              {
                field: 'roleCode',
                title: '操作',
                width: '120',
                formatter: function (value, row, index) {
                  return (
                    "<button type='button' class='openHistoryFile well-btn w-btn-primary w-line-btn well-btn-sm' data-file='" +
                    JSON.stringify(row) +
                    "'>打开</button>"
                  );
                }
              }
            ];

            if (_this.historyColumns && _this.historyColumns.length > 0) {
              $.each(_this.historyColumns, function (hIndex, hItem) {
                if (hItem.index != undefined) {
                  columns.splice(hItem.index, 0, hItem);
                } else {
                  columns.push(hItem);
                }
              });
            }

            var $fileHistoryTable = $('#fileHistoryList', $historyDialog).bootstrapTable({
              data: hisFiles,
              idField: 'id',
              striped: false,
              showColumns: false,
              undefinedText: '',
              formatNoMatches: function () {
                return '暂无历史记录！';
              },
              columns: columns
            });
            $fileHistoryTable.parents('.fixed-table-body').css({
              overflow: 'hidden'
            });
            $fileHistoryTable.find('tr>td').each(function () {
              var $cell = $(this);
              if ($cell.find('.openLocalHistoryFile').length > 0) {
                //如果是操作按钮组，不需要设置title
                return true;
              }

              $cell.attr('title', $cell.text());
            });

            $('.fileLocalHistoryTip', $historyDialog)
              .off()
              .on('click', function () {
                _this.queryLocalFileHistory(loaclHisFiles);
              });

            $('.openHistoryFile', $historyDialog)
              .off()
              .on('click', function (e) {
                // 只读打开
                var currFile = $(e.target).data('file'),
                  oldReadOnly = _this.readOnly;
                try {
                  _this.readOnly = true;
                  _this.openLocalFile(currFile, null, currFile.name, null);
                } finally {
                  _this.readOnly = oldReadOnly;
                }
              });
          },
          buttons: {}
        });
      },
      error: function (result) {
        console.log(result);
      },
      complete: function () {}
    });
  };

  WellFileUpload4Icon.prototype.queryLocalFileHistory = function (loaclHisFiles) {
    var _this = this;
    var $localHistoryDialog = appModal.dialog({
      title: '历史记录-本机历史文件',
      message: _this.getLocalHistoryContent(),
      size: 'middle',
      zIndex: 10001,
      height: 600,
      width: 900,
      buttons: {},
      shown: function () {
        var $fileLocalHistoryTable = $('#fileLocalHistoryList', $localHistoryDialog)
          .bootstrapTable('destroy')
          .bootstrapTable({
            data: loaclHisFiles,
            idField: 'id',
            striped: false,
            showColumns: false,
            undefinedText: '',
            // horizontalScroll: true,
            columns: [{
                field: 'timestamp',
                title: '创建时间'
              },
              {
                field: 'name',
                title: '文件名'
              },
              {
                field: 'url',
                title: '本机路径'
              },
              {
                field: 'roleCode',
                title: '操作',
                formatter: function (value, row, index) {
                  var html =
                    "<button type='button' class='openLocalHistoryFile well-btn w-btn-primary w-line-btn well-btn-sm' data-file='" +
                    JSON.stringify(row) +
                    "'>打开</button>" +
                    "<button type='button' class='copyRoute well-btn w-btn-primary w-line-btn well-btn-sm' data-file='" +
                    JSON.stringify(row) +
                    "'>复制路径</button>";
                  return html;
                }
              }
            ]
          });
        // $fileLocalHistoryTable.css({
        //     "table-layout": "initial"
        // })
        $fileLocalHistoryTable.find('tr>td').each(function () {
          var $cell = $(this);
          if ($cell.find('.openLocalHistoryFile').length > 0) {
            //如果是操作按钮组，不需要设置title
            return true;
          }

          $cell.attr('title', $cell.text());
        });

        $('.openLocalHistoryFile', $localHistoryDialog)
          .off()
          .on('click', function (e) {
            var currLocalFile = $(e.target).data('file');
            var openOptions = {
              docId: currLocalFile.fileID,
              fileName: currLocalFile.name,
              localPath: currLocalFile.url,
              openType: {
                protectType: 3
              },
              success: function (result) {},
              error: function (result) {
                appModal.error(result.data);
              }
            };
            getBodyApi().openDoc(openOptions);
          });

        $('.copyRoute', $localHistoryDialog)
          .off()
          .on('click', function (e) {
            $localHistoryDialog.append("<textarea id='localFileRoute' style='opacity: 0;'>");
            var currLocalFile = $(e.target).data('file');
            var inputElement = document.getElementById('localFileRoute');
            inputElement.value = currLocalFile.url;
            inputElement.select();
            document.execCommand('Copy');
            appModal.success('复制成功！');
            $('#localFileRoute', $localHistoryDialog).remove();
          });
      }
    });
  };

  WellFileUpload4Icon.prototype.getLocalHistoryContent = function (files) {
    var html = '';
    html +=
      "<div class='fileHistory'>" +
      "<div class='fileLocalHistoryTip'><i class='iconfont icon-ptkj-xinxiwenxintishi'></i>本机历史文件保存在您的本机，非本机上无法查看！</div>" +
      "<table class='fileHistoryList' id='fileLocalHistoryList'></table>" +
      '</div>';
    return html;
  };
  WellFileUpload4Icon.prototype.getHistoryContent = function (files) {
    var html = '';
    html +=
      "<div class='fileHistory'>" +
      "<div class='fileLocalHistoryTip' style='display:none;'><i class='iconfont icon-ptkj-xinxiwenxintishi'></i>发现您的本机上存在其他历史文件！<span>查阅</span></div>" +
      "<table class='fileHistoryList' id='fileHistoryList'></table>" +
      '</div>';
    return html;
  };

  WellFileUpload4Icon.prototype.rename = function (files) {
    var _this = this;
    var html =
      "<form class='form-horizontal' id='fileNameContainer'>" +
      "<div class='form-group'>" +
      "<label for='' class='col-sm-3 control-label'>文件名</label>" +
      "<div class='col-sm-8'><input type='text' name='newFileName' class='form-control'></div>" +
      '</div>' +
      '</form>';
    var val = files[0].fileName;
    var newVal = val.substring(0, val.lastIndexOf('.'));
    var type = val.substring(val.lastIndexOf('.'));
    var $renameDialog = appModal.dialog({
      message: html,
      title: '重命名',
      size: 'middle',
      shown: function () {
        $("input[name='newFileName']", $renameDialog).val(newVal);
      },
      buttons: {
        ok: {
          label: '确定',
          className: 'well-btn w-btn-primary',
          callback: function () {
            var newName = $("input[name='newFileName']", $renameDialog).val();
            var index = 0;
            for (var i = 0; i < _this.files.length; i++) {
              var file = _this.files[i];
              if (files[0] === file || files[0].fileID === file.fileID) {
                index = i;
              }
              var newFileName = file.fileName.substring(0, file.fileName.lastIndexOf('.'));
              if (newFileName == newName) {
                appModal.error('文件' + newName + '已经存在，请重新输入！');
                return false;
              }
            }
            var name = newName + type;

            function success(result) {
              var file = _this.files[index];
              var bFile = $.extend({}, file);
              file.filename = file.fileName = name;
              _this.$fileContainer
                .find('.icoItem:eq(' + index + ')')
                .attr('filename', name)
                .find('.fileName')
                .attr('title', name)
                .text(name);
              _this.triggerFileOperates('file-rename', bFile, file);
            }
            if (_this.isBsEditMode()) {
              success({}); // 保存表单后生效
            } else {
              $.ajax({
                url: ctx + '/api/repository/file/mongo/' + _this.files[index].fileID + '/fileName?newFileName=' + name,
                type: 'PUT',
                dataType: 'json',
                contentType: 'json',
                success: success
              });
            }
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default'
        }
      }
    });
  };
  WellFileUpload4Icon.prototype.replaceFile = function (files) {
    var _this = this;
    files = files || this.getCheckedFiles();
    if (!files || files.length == 0) {
      return appModal.alert({
        message: '请选择附件'
      });
    } else if (files.length > 1) {
      return appModal.alert({
        message: '只能选择1个附件'
      });
    }
    _this.toReplaceFile = files[0];
    var $input = _this.$toolbarContainer.find('a.upload input[type=file]:first');
    var multiple = $input.prop('multiple');
    $input.removeAttr('multiple'); // 单选
    $input.click();
    multiple &&
      setTimeout(function (t) {
        $input.attr('multiple', 'multiple');
      }, 1000);
  };

  WellFileUpload4Icon.prototype.openLayoutDocumentFile = function (files) {

    if (!require('layoutDocumentUtils').dialogNoConfigError()) {
      return false;
    }

    var _this = this;
    files = files || this.getCheckedFiles();
    if (!files || files.length == 0) {
      return appModal.alert({
        message: '请选择附件'
      });
    } else if (files.length > 1) {
      return appModal.alert({
        message: '只能选择1个附件'
      });
    }
    var file = files[0];
    if (!require('layoutDocumentUtils').checkFileType(file.fileName)) {
      return appModal.alert({
        message: '不允许打开'
      });
    }

    require('layoutDocumentUtils').openFile(file.fileID, file.fileName);

    // let url = location.origin + `/ofdOnlineEditView?fileName=${file.fileName}&fileId=${file.fileID}&fieldName=${_this.fieldName}`;
    // if (isSave) {
    //   url += `&isSave=${isSave}`;
    // }
    // window.open(url);

  };

  WellFileUpload4Icon.prototype.sealLayoutDocumentFile = function (files) {

    if (!require('layoutDocumentUtils').dialogNoConfigError()) {
      return false;
    }

    var _this = this;
    files = files || this.getCheckedFiles();
    if (!files || files.length == 0) {
      return appModal.alert({
        message: '请选择附件'
      });
    } else if (files.length > 1) {
      return appModal.alert({
        message: '只能选择1个附件'
      });
    }
    var file = files[0];
    if (!require('layoutDocumentUtils').checkFileType(file.fileName)) {
      return appModal.alert({
        message: '不允许打开'
      });
    }

    require('layoutDocumentUtils').sealFile(file.fileID, file.fileName);
    // let url = location.origin + `/ofdOnlineEditView?fileName=${file.fileName}&fileId=${file.fileID}&fieldName=${_this.fieldName}`;
    // if (isSave) {
    //   url += `&isSave=${isSave}`;
    // }
    // window.open(url);

  };

  WellFileUpload4Icon.prototype.openFileWithSoft = function (fileInfo, temp, filename, isOfd, iconReadOnly) {
    var _this = this;
    var localFileName = filename || fileInfo.fileName || fileInfo.filename;

    var bRevision = false;

    var openOptions = {
      bsMode: _this.isBsEditMode(),
      newFileName: localFileName,
      origId: fileInfo.origUuid, // 用于welloffice的本地历史功能
      docId: fileInfo.fileID || _this.createUuid(),
      fileName: location.origin +
        ctx +
        '/office/wps/download?fileID=' +
        fileInfo.fileID +
        (temp ? '&once=true' : '') +
        (filename ? '&filename=' + encodeURIComponent(filename) : '') +
        (isOfd === true ? '&isOfd=true' : ''),
      uploadPath: _this.readOnly === true ? '' : location.origin + ctx + '/office/wps/savefiles?fileID=' + fileInfo.fileID + '&source=编辑', // 保存文档接口
      buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
      disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
      userName: SpringSecurityUtils.getCurrentUserName(),
      revisionCtrl: $.extend({
          // 痕迹控制 ，不传正常打开
          bOpenRevision: bRevision, // true(打开)false(关闭)修订
          bShowRevision: bRevision, // true(显示)/false(关闭)痕迹。会影响showRevisionsMode，true时，showRevisionsMode的1、3有效；false时，showRevisionsMode的2、4有效
          bEnabledRevision: !bRevision, // true(有权限接受、取消保护)/false(无权限接受，受保护)痕迹
          bAcceptRevision: false, // 接受所有状态
          bRejectRevision: false, // 拒绝所有状态
          showRevisionsMode: 1 // (显示标记的最终状态)/2(最终状态)/3(显示标记的原始状态)/4(原始状态)。受bShowRevision控制。对JSAPI（WPS）有效，对welloffice无效，因为MS office会奔溃
        },
        _this.revisionCtrl
      ),
      openType: {
        protectType: (_this.readOnly === true || iconReadOnly) ? 3 : -1
      },
      success: function (result) {
        _this.del([fileInfo], false); // 删除不确认
        $.each(result.data, function (idx, file) {
          var bFile = $.extend({}, fileInfo);
          fileInfo.fileID = file.fileID; // 支持多次保存
          file.creator = file.creator || SpringSecurityUtils.getCurrentUserId();
          file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.uploadTime = file.uploadTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.isNew = false;
          file.fileName = file.filename;
          _this.addFile(file);
          if (fileInfo.operate === 'imp') {
            delete fileInfo.operate; // 导入附件
            _this.triggerFileOperates('file-imp', null, file);
          } else {
            _this.triggerFileOperates('file-edit', bFile, file);
          }
        });
      },
      error: function (result) {
        alert(result.data);
      }
    };

    if (require('layoutDocumentUtils').checkFileType(localFileName)) {
      // 版式文档处理
      require('layoutDocumentUtils').openFile(fileInfo.fileID, fileInfo.name);
    } else
    if (_this.isOfdOrPfd(localFileName) || isOfd === true) {
      getBodyApi().openOfd(openOptions);
    } else if (_this.isWord(localFileName)) {
      getBodyApi().openDoc(openOptions);
    } else if (_this.isExcel(localFileName)) {
      getBodyApi().openET(openOptions);
    } else if (_this.isPowerPoint(localFileName)) {
      getBodyApi().openWpp(openOptions);
    } else {
      appModal.error('附件类型不支持查阅');
    }

  };

  WellFileUpload4Icon.prototype.getCheckedFiles = function () {
    var files = [];
    for (var i = 0; i < this.files.length; i++) {
      var file = this.files[i];
      if (file.$fileItemElement.find('.itemChecked').attr('checked') == 'checked') {
        //
        files.push(file);
      }
    }

    return files;
  };

  var FileInfo = function () {
    this.fileID = null;
    this.fileName = null;
    this.fileSize = 0;
    this.contentType = null;
    this.digestAlgorithm = null;
    this.digestValue = null;
    this.certificate = null;
    this.signatureValue = null;
    this.localPath = null;
    this.isLocal = false;
    this.isNew = true; // 表示如果值为false表示该文件已隶属于某个文件夹，true表示该文件还没和任何文件夹产生关系
  };

  WellFileUpload4Icon.prototype.addFile = function (fileInfo, allowDuplicates) {
    var _this = this;
    var oldName = fileInfo.fileName; // 名称可能会替换
    // 判断文件是否已经存在 解决保存后再添加会重复的问题
    if (_this.isFileNameExist1(fileInfo.fileName, fileInfo, allowDuplicates)) {
      return false;
    }
    // // 判断文件格式
    // var fileExts = $.isArray(_this.fileExt) ? _this.fileExt : _this.fileExt.split("|");
    // var isTrueType = false;
    // for (var index = 0; index < fileExts.length; index++) {
    // 	if (fileInfo.fileName.lastIndexOf(fileExts[index]) > 0) {
    // 		isTrueType = true;
    // 		break;
    // 	}
    // }
    // if (!isTrueType) {
    // 	// alert("文件:" + fileInfo.fileName + "格式不对 限制为" + this.fileExt +
    // 	// "格式");
    // 	appModal.alert({
    // 		message : "文件:" + fileInfo.fileName + "格式不对 限制为" + this.fileExt + "格式"
    // 	})
    // 	return false;
    // }
    // 判断文件大小
    if (_this.fileMaxSize) {
      if (fileInfo.fileSize > 1024 * 1024 * this.fileMaxSize) {
        // alert("文件:" + fileInfo.fileName + "超过" + this.fileMaxSize +
        // "M");
        appModal.error({
          message: '附件大小不能超过' + formatSize(this.fileMaxSize) + 'M，请检查后重新上传'
        });
        return false;
      }
    }

    if (!_this.$toolbarContainer) {
      _this.createToolbarContainer();
    }

    _this.createFileContainer();
    _this.files.push(fileInfo);
    _this.triggerFileChangeEvent();
    _this.createIcon(fileInfo);
    _this.updateCounter();

    _this.addToExitFile1(oldName);

    if (!fileInfo.fileID) {}

    _this.uploadOk(fileInfo); // 上传成功
  };

  // 上传成功
  WellFileUpload4Icon.prototype.uploadOk = function (fileInfo) {
    if (this.uploadOkCallback) {
      // 上传成功回调
      this.uploadOkCallback(fileInfo);
    }
    // 从验证框架中删除该控件的所对应的非法条目
    if (this.$containerElement != undefined && this.$containerElement != null) {
      var editableElement = this.$containerElement.find("input[name='" + this.ctlID + "']");
      if (typeof Theme != 'undefined' && editableElement.length > 0) {
        Theme.validationRules.unhighlight(editableElement);
        Theme.validationRules.success(null, editableElement);
      }
    }
  };

  WellFileUpload4Icon.prototype.updateCounter = function () {
    if (this.files.length > 0) {
      this.$noData.hide();
    } else {
      this.$noData.show();
    }
    this.$attachCounter.html(this.files.length);
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
  WellFileUpload4Icon.prototype.addFiles = function (files, isAppend) {
    var _this = this;
    this.fileLength = files.length;
    if (this.readOnly && files.length == 0) {
      this.$toolbarContainer.hide();
      this.$fileContainer.hide();
    }
    if (files == undefined || !(files instanceof Array || $.isArray(files))) {
      // files中没有元素，无需再进一步处理
      return;
    }
    if (isAppend == false) {
      // 清空文件夹即该控件下面的文件
      // _this.files.length = 0;
      _this.clear(false);
    }
    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      var fileObj = {};
      $.extend(fileObj, file);
      _this.addFile(fileObj, true);
    }
  };

  // 判定附件是否已存在
  WellFileUpload4Icon.prototype.isExist = function (path) {
    for (var i = 0; i < this.files.length; i++) {
      if (this.files[i].localPath == path) {
        // 已存在
        return true;
      }
    }
    return false;
  };

  /**
   * 检验文件名的合法性
   *
   * @returns 文件名若合法则返回true, 若非法则返回false
   */
  WellFileUpload4Icon.prototype.checkFileName = function (fileName) {
    if (fileName.length > 80) {
      // appModal.alert("附件文件名称超长(不得超过80个字)[" + fileName + "]");
      appModal.alert({
        message: '附件文件名称超长(不得超过80个字)[' + fileName + ']'
      });
      return false;
    }
    return !/[\/\\:\*\?"%<>\|\n]/gi.test(fileName);
  };

  /**
   *
   */
  WellFileUpload4Icon.prototype.showFileNameInvalidMsg = function (fileName) {
    // appModal.alert("文件名不得含有字符:\"/ \\ : * ? \" % < > |\"及\"换行符\",
    // 或者文件名长度超过80个字符");
    appModal.alert({
      message: '文件名不得含有字符:"/ \\ : * ? " % < > |"及"换行符", 或者文件名长度超过80个字符'
    });
  };

  WellFileUpload4Icon.prototype.createIcon = function (fileInfo) {
    var _this = this;
    if (!this.$fileUl) {
      this.$fileUl = $("<ul class='clearfix'></ul>");
      this.$fileContainer.append(this.$fileUl);
    }
    var fileName = fileInfo.fileName;
    var $ul = this.$fileUl;

    var $li = $('<li class="icoItem" filename="' + fileName + '"></li>');
    $ul.append($li);

    var filename2 = fileName.toLowerCase();

    var itemContent = '<input type="checkbox" class="itemChecked w-checkbox" /><label></label>';

    // modifiy by xujm 2015-05-11 修改 附件大小单位不对
    var fileInfoHtml = '<span class="kh kh1">(</span>';
    if (fileInfo.fileSize < 1024) {
      fileInfoHtml += '<span class="file-inline-info fileSize" title="' + fileInfo.fileSize + 'B">' + fileInfo.fileSize + 'B';
    } else if (fileInfo.fileSize < 1024 * 1024) {
      fileInfoHtml += '<span class="file-inline-info fileSize" title="' + (fileInfo.fileSize / 1024).toFixed(2) + 'KB">' + (fileInfo.fileSize / 1024).toFixed(2) + 'KB';
    } else {
      fileInfoHtml += '<span class="file-inline-info fileSize" title="' + (fileInfo.fileSize / 1024 / 1024).toFixed(2) + 'M">' + (fileInfo.fileSize / 1024 / 1024).toFixed(2) + 'M';
    }
    fileInfoHtml += '</span>';
    if (fileInfo.createTime) {
      var createTime = null;
      if (_this.parseDate && fileInfo.createTime.length == 19) {
        createTime = _this.parseDate(fileInfo.createTime);
      } else {
        createTime = new Date(fileInfo.createTime);
      }
      if (_this.$toolbarContainer.find('.defaultViewBtn').attr('data-views') == 'table') {
        fileInfoHtml +=
          '<span class="file-inline-info fileCreatetime" title="' +
          createTime.format('yyyy-MM-dd HH:mm:ss') +
          '">' +
          createTime.format('yyyy-MM-dd HH:mm:ss') +
          '</span>';
      } else {
        fileInfoHtml += '<span class="file-inline-info fileCreatetime" title="' + createTime.format('yyyy-MM-dd HH:mm:ss') + '">' + createTime.format('yyyy-MM-dd HH:mm:ss') + '</span>';
      }
    }
    fileInfoHtml += '<span class="file-inline-info user-name"></span><span class="kh kh2">)</span>';

    var fileArr = filename2.split('.');
    var suffix = fileArr[fileArr.length - 1];

    if (suffix == 'pdf') {
      itemContent +=
        '<i class="icon iconfont icon-ptkj-pdf" style="color:#F06966" ></i><span title="' +
        fileName +
        '" class="fileName">' +
        fileName +
        '</span>';
    } else if (suffix == 'doc' || suffix == 'docx') {
      itemContent +=
        '<i class="icon iconfont icon-ptkj-word" style="color:#2A5699" ></i><span title="' +
        fileName +
        '" class="fileName">' +
        fileName +
        '</span>';
    } else if (suffix == 'xls' || suffix == 'xls') {
      itemContent +=
        '<i class="icon iconfont icon-ptkj-excel" style="color:#207245" ></i><span title="' +
        fileName +
        '" class="fileName">' +
        fileName +
        '</span>';
    } else if (suffix == 'ppt' || suffix == 'pptx') {
      itemContent +=
        '<i class="icon iconfont icon-ptkj-ppt" style="color:#D24625" ></i><span title="' +
        fileName +
        '" class="fileName">' +
        fileName +
        '</span>';
    } else if (suffix == 'jpg' || suffix == 'png' || suffix == 'gif') {
      itemContent +=
        '<i class="icon iconfont icon-ptkj-tupian" style="color:#D19122;" ></i><span title="' +
        fileName +
        '" class="fileName">' +
        fileName +
        '</span>';
    } else {
      itemContent +=
        '<i class="icon iconfont icon-ptkj-fujian" style="color:#488CEE;"></i><span title="' +
        fileName +
        '" class="fileName">' +
        fileName +
        '</span>';
    }
    itemContent += fileInfoHtml;
    $li.html(itemContent);
    $li.find('.itemChecked').next().css({
      visibility: _this.operateBtns.indexOf('6') > -1 || (_this.operateBtns.indexOf('14') > -1 && !_this.readOnly) || _this.operateBtns.indexOf('15') > -1 ?
        'visible' : 'hidden'
    });
    $li.off().on(
      'click',
      _.debounce(
        function (e) {
          if ($(e.target).is('input, label')) {
            return;
          } else if (_this.$toolbarContainer.find(".defaultViewBtn[data-views='image']").length > 0) {
            if (_this.clickBtnCode) { // 是否二开传入按钮编码
              if (_this.clickBtnCode == "btn-edit") { // 是否是编辑
                if (_this.operateBtns.indexOf('8') > -1 && !_this.readOnly) { // 文档为word且有权限时走正常编辑
                  $li.find(".btn[common-id='btn-edit']").trigger("click");
                } else if ((_this.readOnly || _this.operateBtns.indexOf('8') == -1) && _this.iconReadOnly) { // 文档为word且没有权限时走编辑但是只读
                  _this.openLocalFile(fileInfo, null, fileInfo.fileName || fileInfo.filename, null, _this.iconReadOnly);
                }
              } else {
                $li.find(".btn[common-id='" + _this.clickBtnCode + "']").trigger("click");
              }
            } else {
              var clickType = SystemParams.getValue('pt.filename.click.type');
              if (clickType == 'download' && _this.operateBtns.indexOf('2') > -1) {
                _this.download([fileInfo]);
              } else if (clickType != 'download' && _this.operateBtns.indexOf('7') > -1 && _this.hostServer) {
                _this.preview([fileInfo]);
              }
            }
          }
        },
        500, {
          leading: true,
          trailing: false
        }
      )
    );
    $li.off('click', '.fileName').on(
      'click',
      '.fileName',
      _.debounce(
        function (e) {
          e.stopPropagation();
          if (_this.clickBtnCode) { // 是否二开传入按钮编码
            if (_this.clickBtnCode == "btn-edit") { // 是否是编辑
              if (_this.operateBtns.indexOf('8') > -1) { // 文档为word且有权限时走正常编辑
                $li.find(".btn[common-id='btn-edit']").trigger("click");
              } else if (_this.operateBtns.indexOf('8') == -1 && _this.iconReadOnly) { // 文档为word且没有权限时走编辑但是只读
                _this.openLocalFile(fileInfo, null, fileInfo.fileName || fileInfo.filename, null, _this.iconReadOnly);
              }
            } else {
              $li.find(".btn[common-id='" + _this.clickBtnCode + "']").trigger("click");
            }
          } else { // 未二开传参走系统参数配置的单击事件
            var clickType = SystemParams.getValue('pt.filename.click.type');
            if (clickType == 'download' && _this.operateBtns.indexOf('2') > -1) {
              _this.download([fileInfo]);
            } else if (clickType != 'download' && _this.operateBtns.indexOf('7') > -1 && _this.hostServer) {
              _this.preview([fileInfo]);
            }
          }
        },
        500, {
          leading: true,
          trailing: false
        }
      )
    );
    $li.off('click', '.w-checkbox + input').on('click', '.w-checkbox + input', function (e) {
      e.stopPropagation();
      $(this).prev().click();
    });

    // var $btnView = $("<button type=\"button\" class=\"btn btn-view icon iconfont icon-ptkj-yulan\">预览</button>");
    // var $btnDownload = $("<button type=\"button\" class=\"btn btn-download icon iconfont icon-ptkj-xiazai\">下载</button>");
    // var $btnEdit = $("<button type=\"button\" class=\"btn btn-edit icon iconfont icon-ptkj-bianji\">编辑</button>");
    // var $btnCopyName = $("<button type=\"button\" class=\"btn btn-copyName icon iconfont icon-ptkj-fuzhi\">复制名称</button>");
    // var $btnRename = $("<button type=\"button\" class=\"btn btn-rename icon iconfont icon-ptkj-zhongmingming\">重命名</button>");
    // var $btnDelete = $("<button type=\"button\" class=\"btn btn-delete icon iconfont icon-ptkj-shanchu\">删除</button>");
    // var $btnMoveup = $("<button type=\"button\" class=\"btn btn-moveup icon iconfont icon-ptkj-shangyi\">上移</button>");
    // var $btnMovedown = $("<button type=\"button\" class=\"btn btn-movedown icon iconfont icon-ptkj-xiayi\">下移</button>");
    // $btnView.click(function(event) {
    // 	_this.preview([fileInfo]);
    // });
    // $btnDownload.click(function() {
    // 	_this.download([fileInfo]);
    // })
    //
    // $btnEdit.click(function(event) {
    // 	_this.openLocalFile(fileInfo, null, null, null);
    // });
    //
    // $btnCopyName.click(function() {
    // 	_this.copyFileNameFunc(fileInfo);
    // })
    // $btnRename.click(function() {
    // 	_this.download([ fileInfo ]);
    // })
    //		var $btnEditAsOfd = $("<button type=\"button\" class=\"btn btn-editAsOfd icon iconfont icon-ptkj-bianji\">按OFD编辑</button>");
    //		$btnEditAsOfd.click(function(event) {
    //			_this.openAsOfd(fileInfo, true);
    //		});

    //		$btnEditAsOfd[(_this.allowDownload && _this.hostServer) ? "show" : "hide"]();
    // $btnDelete.click(function(event) {
    // 	// 调用删除后，$li的popover实例会被摧毁，所以先调用
    // 	$li.popover("hide");
    // 	_this.del([fileInfo]);
    // });
    // $btnMoveup.click(function() {
    // 	var $li = $(this).closest("li");
    // 	var $prev = $li.prev("li");
    // 	if ($prev.length) {
    // 		$prev.before($li);
    // 	}
    // });
    //
    // $btnMovedown.click(function() {
    // 	var $li = $(this).closest("li");
    // 	var $next = $li.next("li");
    // 	if ($next.length) {
    // 		$next.after($li);
    // 	}
    // });
    var $btnGroup = $('<span class="c-group"></span>');

    $btnGroup.click(function (event) {
      event.preventDefault();
      event.stopPropagation();
    });
    $li.append($btnGroup);
    _this.renderButtons($li, fileInfo, _this.readOnly ? false : true);
    var hidePopover = true;
    $li.popover({
      html: true,
      placement: 'top',
      container: 'body',
      trigger: 'manual',
      template: '<div class="popover tip-file" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
      content: function () {
        var content = '<span class="tip-filename">' + fileName + '</span><br>';
        content += '<span class="tip-s tip-filesize">' + $li.find('span.fileSize').text() + '</span>';
        content += '<span class="tip-s tip-createTime">' + $li.find('span.fileCreatetime').text() + '</span>';
        content += '<span class="tip-s tip-creator">' + $li.find('span.user-name').text() + '</span>';
        content += '<hr />';
        var $cBtnGroup = $btnGroup.clone(true);
        var $pBtnGroup = $('<div>');
        $cBtnGroup.find('.btn-moveup').remove();
        $cBtnGroup.find('.btn-movedown').remove();
        $.each($cBtnGroup.find('button.btn'), function (index, item) {
          $pBtnGroup.append($(item));
        });
        $cBtnGroup.html($pBtnGroup.html()).show();
        return $('<div class="image-tips-wrapper">')
          .append(content)
          .append($cBtnGroup)
          .mouseenter(function (event) {
            hidePopover = false;
          })
          .mouseleave(function (event) {
            hidePopover = true;
            $li.popover('hide');
          });
      }
    });
    $li
      .mouseenter(function (event) {
        var $this = $(this);
        var bViewImage = $this.closest('.ui-image').length > 0;
        if (bViewImage) {
          return setTimeout(function () {
            $this.popover('show');
            var text = $this.find('.fileName').text();
            $('.image-tips-wrapper').find('.tip-filename').text(text);
          }, 50);
        }
        // 更新按钮状态 end
        $btnGroup.show().css({
          display: 'inline-block'
        });
      })
      .mouseleave(function (event) {
        var $this = $(this);
        var bViewImage = $this.closest('.ui-image').length > 0;
        if (bViewImage) {
          return setTimeout(function (t) {
            if (hidePopover) {
              $this.popover('hide');
            }
          }, 100);
        }
        $btnGroup.find('.btn-group').removeClass('open');
        $btnGroup.hide();
      });

    fileInfo.$fileItemElement = $li;
    _this.addUploadUser($li, fileInfo.creator, fileInfo);
    _this.itemEvent(fileInfo);
  };

  WellFileUpload4Icon.prototype.renderButtons = function ($li, fileInfo, isEdit) {
    var btns = [{
        value: '7',
        html: $('<button type="button" common-id=\'btn-view\'  class="btn btn-view icon iconfont icon-ptkj-yulan">预览</button>')
      },
      {
        value: '2',
        html: $('<button type="button" common-id=\'btn-download\' class="btn btn-download icon iconfont icon-ptkj-xiazai">下载</button>')
      },
      {
        value: '8',
        html: $('<button type="button" common-id=\'btn-edit\' class="btn btn-edit icon iconfont icon-ptkj-bianji">编辑</button>')
      },
      {
        value: '21',
        html: $('<button type="button" common-id=\'btn-replace\' class="btn btn-replace icon iconfont icon-ptkj-tihuan">替换</button>')
      },
      {
        value: '13',
        html: $('<button type="button" common-id=\'btn-copyName\' class="btn btn-copyName icon iconfont icon-ptkj-fuzhi">复制名称</button>')
      },
      {
        value: '9',
        html: $(
          '<button type="button" common-id=\'btn-rename\' class="btn btn-rename icon iconfont icon-ptkj-zhongmingming">重命名</button>'
        )
      },
      {
        value: '3',
        html: $('<button type="button" common-id=\'btn-delete\' class="btn btn-delete icon iconfont icon-ptkj-shanchu">删除</button>')
      },
      {
        value: '10',
        html: $('<button type="button" common-id=\'btn-moveup\' class="btn btn-moveup icon iconfont icon-ptkj-shangyi">上移</button>')
      },
      {
        value: '11',
        html: $('<button type="button" common-id=\'btn-movedown\' class="btn btn-movedown icon iconfont icon-ptkj-xiayi">下移</button>')
      },
      {
        value: '12',
        html: $('<button type="button" common-id=\'btn-history\' class="btn btn-history icon iconfont icon-ptkj-lishi">历史记录</button>')
      },
      {
        value: '22',
        html: $('<button type="button" common-id=\'btn-open-layout-doc\' class="btn btn-open-layout-doc icon iconfont icon-xmch-xiangmuchaxun">查阅</button>')
      },
      {
        value: '23',
        html: $('<button type="button" common-id=\'btn-seal-layout-doc\' class="btn btn-seal-layout-doc icon iconfont icon-szgy-gaizhang">盖章</button>')
      },
      {
        value: '24',
        html: $('<button type="button" common-id=\'btn-open-doc\' class="btn btn-open-layout-doc icon iconfont icon-xmch-xiangmuchaxun">查阅正文</button>')
      }
    ];
    var _this = this;
    var operateBtns = this.operateBtns;
    var i = 0;
    var $span = $('<span>');
    $.each(btns, function (index, item) {

      // 版式文档处理 文件格式过滤
      if (item.value === '22' || item.value === '23') {
        if (require('layoutDocumentUtils').checkFileType(fileInfo.fileName)) {} else {
          return true;
        }
      }

      if (i < 4) {
        if (item.value == '7') {
          if (operateBtns.indexOf(item.value) > -1 && _this.hostServer) {
            $span.append(item.html);
            i++;
          }
        } else if (
          item.value == '3' ||
          item.value == '9' ||
          item.value == '8' ||
          item.value == '10' ||
          item.value == '11' ||
          item.value == '21' ||
          item.value == '23'
        ) {
          if (isEdit && operateBtns.indexOf(item.value) > -1) {
            $span.append(item.html);
            i++;
          }
        } else if (operateBtns.indexOf(item.value) > -1) {
          $span.append(item.html);
          i++;
        }
      }
      if (i == 4) {
        var $dBtn = $(
          "<div class='btn-group' style='vertical-align: initial;'>" +
          "<button type='button' class='well-btn w-btn-primary w-noLine-btn well-btn-sm dropdown-toggle' data-toggle='dropdown'>" +
          "<span>更多</span><i class='iconfont icon-ptkj-xianmiaojiantou-xia'></i>" +
          '</button>' +
          "<ul class='dropdown-menu w-btn-dropMenu' role='menu'></ul>" +
          '</div>'
        );
        $span.append($dBtn);
      }
      if (i >= 4) {
        if (item.value == '7') {
          if (operateBtns.indexOf(item.value) > -1 && _this.hostServer) {
            $span.find('.dropdown-menu').append(item.html);
            i++;
          }
        } else if (
          item.value == '3' ||
          item.value == '9' ||
          item.value == '8' ||
          item.value == '10' ||
          item.value == '11' ||
          item.value == '21' ||
          item.value == '23'
        ) {
          if (isEdit && operateBtns.indexOf(item.value) > -1) {
            $span.find('.dropdown-menu').append(item.html);
            i++;
          }
        } else if (operateBtns.indexOf(item.value) > -1) {
          $span.find('.dropdown-menu').append(item.html);
          i++;
        }
      }
    });
    $li
      .find('.c-group')
      .html($span.html())
      .off()
      .on('click', '.btn[common-id]', function (e) {
        e.stopPropagation();
        var commonId = $(this).attr('common-id');
        if (commonId == 'btn-view') {
          _this.preview([fileInfo]);
        } else if (commonId == 'btn-download') {
          _this.download([fileInfo]);
        } else if (commonId == 'btn-edit') {
          _this.openLocalFile(fileInfo, null, fileInfo.fileName || fileInfo.filename, null);
        } else if (commonId == 'btn-copyName') {
          _this.copyFileNameFunc(fileInfo);
        } else if (commonId == 'btn-rename') {
          _this.rename([fileInfo]);
        } else if (commonId == 'btn-delete') {
          if (_this.$toolbarContainer.find('.defaultViewBtn').attr('data-views') == 'image') {
            $(this).closest('.popover').popover('hide');
          }
          _this.del([fileInfo]);
          _this.triggerFileOperates('file-delete', fileInfo, null);
        } else if (commonId == 'btn-moveup') {
          var $li = $(this).closest('li');
          var $prev = $li.prev('li');

          if ($prev.length) {
            var i = $li.index();
            var data = _this.files[i];
            _this.files.splice(i, 1);
            _this.files.splice(i - 1, 0, data);

            $prev.before($li);
          }
        } else if (commonId == 'btn-movedown') {
          var $li = $(this).closest('li');
          var $next = $li.next('li');

          if ($next.length) {
            var i = $li.index();
            var data = _this.files[i];
            _this.files.splice(i, 1);
            _this.files.splice(i + 1, 0, data);
            $next.after($li);
          }
        } else if (commonId == 'btn-history') {
          _this.showLocalFileHistory([fileInfo]);
        } else if (commonId == 'btn-replace') {
          _this.replaceFile([fileInfo]);
        } else if (commonId == 'btn-open-layout-doc') {
          _this.openLayoutDocumentFile([fileInfo]);
        } else if (commonId == 'btn-seal-layout-doc') {
          _this.sealLayoutDocumentFile([fileInfo]);
        } else if (commonId == 'btn-open-doc') {
          // 使用本地word打开文档
          _this.openFileWithSoft(fileInfo, null, fileInfo.fileName || fileInfo.filename, null, true);
        }
      });
    $li.off('click', '.btn-group').on('click', '.btn-group', function (e) {
      e.stopPropagation();
      if ($(this).hasClass('open')) {
        $(this).removeClass('open');
      } else {
        $(this).addClass('open');
        var windowInnerHeight = window.innerHeight;
        var _scrollTop = $(document).scrollTop();
        var _mountOffset = $(this).offset();
        var _mountOuterHeight = $(this).outerHeight();
        var $dropdown = $(this).find('.w-btn-dropMenu');
        var $dropdownOuterHeight = $dropdown.outerHeight();
        if (windowInnerHeight + _scrollTop - $dropdownOuterHeight - _mountOuterHeight - _mountOffset.top >= 0) {
          $dropdown.css({
            bottom: 'initial',
            top: '100%',
            marginTop: '0'
          });
        } else {
          $dropdown.css({
            bottom: '100%',
            top: 'initial',
            marginTop: '0'
          });
        }
      }
    });
  };

  // 为每项添加附件上传着
  WellFileUpload4Icon.prototype.addUploadUser = function ($li, userId, fileInfo) {
    window.setTimeout(function () {
      if (StringUtils.isNotBlank(userId) && userId != 'anonymousUser') {
        try {
          var userName = OrgUtils.getUserBeanById(userId).userName + '  ';
          $li.find('.user-name').html(userName).attr("title", userName);
        } catch (e) {
          console.log('error when load username of ' + userId);
        }
      }
    }, 100);
  };

  // 为每项附件添加点击事件
  WellFileUpload4Icon.prototype.itemEvent = function (item) {
    // 点中文件
    var _this = this;
    // item.$fileItemElement.bind("dblclick", function() {
    // 	_this.openLocalFile(item);
    // });
    item.$fileItemElement.bind('click', function () {
      if ($(this).hasClass('bar')) {
        $(this).find('input[type="checkbox"]').prop('checked', false);
      } else {
        $(this).find('input[type="checkbox"]').prop('checked', true);
      }
      $(this).toggleClass('bar');
    });
  };
  WellFileUpload4Icon.prototype.getFileOpenMethod = function (fileName) {
    // 指定文件的打开方式
    var openMethod = '-1'; // 使用系统默认软件
    if (typeof WellFileUpload4Body != 'undefined' && this instanceof WellFileUpload4Body) {
      // 正文控件的打开方式由参数参数决定(由参数doc.file.open.method决定)
      // -1表示默认软件打开，0表示MS打开，1表示WPS打开，2表示YOZO打开,
      // 不指定时值为-1
      openMethod = SystemParams.getValue('doc.file.open.method', openMethod);
    }
    return openMethod;
  };

  WellFileUpload4Icon.prototype.openAsOfd = function (fileInfo, force) {
    var self = this;
    window.appModal && appModal.showMask('正在转换...');
    $.ajax({
      dataType: 'json',
      url: ctx + '/repository/file/mongo/saveAsOfd?fileID=' + fileInfo.fileID + '&force=' + (force ? 'true' : 'false'),
      success: function (result) {
        var file = result.data[0];
        self.openLocalFile(file);
      },
      error: function (result) {
        appModal.error('文件转换错误' + (result.msg || ''));
      },
      complete: function () {
        window.appModal && appModal.hideMask();
      }
    });
  };

  WellFileUpload4Icon.prototype.isBsEditMode = function () {
    return true; //this.createHistory;// BS编辑模式，现对于CS编辑
  };

  WellFileUpload4Icon.prototype.openLocalFile = function (fileInfo, temp, filename, isOfd, iconReadOnly) {
    // isOfd = true;
    // var fileCtrl = this;
    // 套红测试：选中正文fileInfo套红，再选择后头文件
    // return fileCtrl.insertRedHeadDocFromWeb(fileInfo, "http://192.168.40.13:8080/repository/file/mongo/download?fileID=c8bd7ad1c8d000017a6410bd11f013c2", "Content");
    // 模板测试：选中模板fileInfo填充，再组织dataFromWeb
    // return fileCtrl.fillTemplate(fileInfo, [ {"name": "FirstTitle", "text": "web"}, {"name": "TopTitle1","text": "军参谋-web"} ]);

    var _this = this;
    var localFileName = filename || fileInfo.fileName || fileInfo.filename;

    var bRevision = _this.readOnly != true && _this.pbRecord;

    // var saveAsbsMode = this.createHistory;
    // var saveAsbsMode = true;
    var openOptions = {
      bsMode: _this.isBsEditMode(),
      newFileName: localFileName,
      origId: fileInfo.origUuid, // 用于welloffice的本地历史功能
      docId: fileInfo.fileID || _this.createUuid(),
      fileName: location.origin +
        ctx +
        '/office/wps/download?fileID=' +
        fileInfo.fileID +
        (temp ? '&once=true' : '') +
        (filename ? '&filename=' + encodeURIComponent(filename) : '') +
        (isOfd === true ? '&isOfd=true' : ''),
      uploadPath: _this.readOnly === true ? '' : location.origin + ctx + '/office/wps/savefiles?fileID=' + fileInfo.fileID + '&source=编辑', // 保存文档接口
      buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
      disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
      userName: SpringSecurityUtils.getCurrentUserName(),
      revisionCtrl: $.extend({
          // 痕迹控制 ，不传正常打开
          bOpenRevision: bRevision, // true(打开)false(关闭)修订
          bShowRevision: bRevision, // true(显示)/false(关闭)痕迹。会影响showRevisionsMode，true时，showRevisionsMode的1、3有效；false时，showRevisionsMode的2、4有效
          bEnabledRevision: bRevision, // true(有权限接受、取消保护)/false(无权限接受，受保护)痕迹
          bAcceptRevision: false, // 接受所有状态
          bRejectRevision: false, // 拒绝所有状态
          showRevisionsMode: 1 // (显示标记的最终状态)/2(最终状态)/3(显示标记的原始状态)/4(原始状态)。受bShowRevision控制。对JSAPI（WPS）有效，对welloffice无效，因为MS office会奔溃
        },
        _this.revisionCtrl
      ),
      openType: {
        protectType: (_this.readOnly === true || iconReadOnly) ? 3 : -1
      },
      success: function (result) {
        _this.del([fileInfo], false); // 删除不确认
        $.each(result.data, function (idx, file) {
          var bFile = $.extend({}, fileInfo);
          fileInfo.fileID = file.fileID; // 支持多次保存
          file.creator = file.creator || SpringSecurityUtils.getCurrentUserId();
          file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.uploadTime = file.uploadTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.isNew = false;
          file.fileName = file.filename;
          _this.addFile(file);
          if (fileInfo.operate === 'imp') {
            delete fileInfo.operate; // 导入附件
            _this.triggerFileOperates('file-imp', null, file);
          } else {
            _this.triggerFileOperates('file-edit', bFile, file);
          }
        });
      },
      error: function (result) {
        alert(result.data);
      }
    };

    if (require('layoutDocumentUtils').checkFileType(localFileName)) {
      // 版式文档处理
      require('layoutDocumentUtils').openFile(fileInfo.fileID, fileInfo.name);
    } else
    if (_this.isOfdOrPfd(localFileName) || isOfd === true) {
      getBodyApi().openOfd(openOptions);
    } else if (_this.isWord(localFileName)) {
      getBodyApi().openDoc(openOptions);
    } else if (_this.isExcel(localFileName)) {
      getBodyApi().openET(openOptions);
    } else if (_this.isPowerPoint(localFileName)) {
      getBodyApi().openWpp(openOptions);
    } else {
      appModal.error('附件类型不支持编辑');
    }

  };
  /**
   * 填充模板
   *
   * @apiParam {object} fileInfo 需要套红的文件
   * @apiParam {string} templateURL 红头文件的获取路径
   * @apiParam {string} replaceBookMark 正文你换的书签名
   */
  WellFileUpload4Icon.prototype.insertRedHeadDocFromWeb = function (fileInfo, templateURL, replaceBookMark) {
    var _this = this;
    var localFileName = fileInfo.fileName || fileInfo.fileName;
    var openOptions = {
      newFileName: localFileName,
      docId: fileInfo.fileID || _this.createUuid(),
      fileName: location.origin + ctx + '/office/wps/download?fileID=' + fileInfo.fileID,
      uploadPath: location.origin + ctx + '/office/wps/savefiles?fileID=' + fileInfo.fileID, // 保存文档接口
      buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
      disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
      userName: SpringSecurityUtils.getCurrentUserName(),
      success: function (result) {
        _this.del([fileInfo], false); // 删除不确认
        $.each(result.data, function (idx, file) {
          file.creator = file.creator || SpringSecurityUtils.getCurrentUserId();
          file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.uploadTime = file.uploadTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.isNew = false;
          file.fileName = file.filename;
          _this.addFile(file);
        });
      },
      error: function (result) {
        alert(result.data);
      }
    };
    if (_this.isWord(localFileName)) {
      getBodyApi().insertRedHeadDocFromWeb(openOptions, templateURL, replaceBookMark);
    } else {
      appModal.alert('附件类型不支持套红');
    }
  };

  /**
   * 填充模板
   *
   * @apiParam {object} fileInfo 需要填充的模板文件
   * @apiParam {Array} dataFromWeb 红头文件的获取路径 [ {"name": "FirstTitle", "text":
   *           "web"}, {"name": "TopTitle1","text": "军参谋-web"} ]
   */
  WellFileUpload4Icon.prototype.fillTemplate = function (fileInfo, dataFromWeb) {
    var _this = this;
    var localFileName = fileInfo.fileName || fileInfo.fileName;
    var openOptions = {
      newFileName: localFileName,
      docId: fileInfo.fileID || _this.createUuid(),
      fileName: location.origin + ctx + '/office/wps/download?fileID=' + fileInfo.fileID,
      uploadPath: location.origin + ctx + '/office/wps/savefiles?fileID=' + fileInfo.fileID, // 保存文档接口
      buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
      disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
      userName: SpringSecurityUtils.getCurrentUserName(),
      success: function (result) {
        _this.del([fileInfo], false); // 删除不确认
        $.each(result.data, function (idx, file) {
          file.creator = file.creator || SpringSecurityUtils.getCurrentUserId();
          file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.uploadTime = file.uploadTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.isNew = false;
          file.fileName = file.filename;
          _this.addFile(file);
        });
      },
      error: function (result) {
        alert(result.data);
      }
    };
    if (_this.isWord(localFileName)) {
      getBodyApi().openDoc(openOptions, [{
        fillTemplate: {
          dataFromWeb: dataFromWeb
        }
      }]);
    } else {
      appModal.alert('附件类型不支持填充模板');
    }
  };

  /**
   * 以"_"替换文件名中的"."
   */
  function replaceDotInFileNameWithUnderLine(fileName) {
    return fileName.replaceAll('\\.', '_');
  }

  WellFileUpload4Icon.prototype.getFileNameInPath = function (path) {
    return path.substr(path.lastIndexOf('\\') + 1);
  };

  WellFileUpload4Icon.prototype.deleteIcon = function (path) {};

  WellFileUpload4Icon.prototype.createFileContainer = function () {
    if (this.$fileContainer == null || typeof this.$fileContainer === 'undefined') {
      var fileListId = 'filelist' + this.ctlID;
      var fileHtml = '<div id="' + fileListId + '" style="" class="fileContainer"></div>'; // 文件列表容器
      this.$fileContainer = $(fileHtml);
      var tHeader = '<div class="file-table-header">';
      tHeader += '<span class="td1">文件名</span>';
      tHeader += '<span class="td2">大小</span>';
      tHeader += '<span class="td3">上传时间</span>';
      tHeader += '<span class="td4">上传人</span>';
      tHeader += '<span class="td5">操作</span>';
      tHeader += '</div>';

      var color = getUserTheme().color;

      var noData =
        "<div class='file-nodata'><img src='/static/js/pt/theme/default/images/file-empty." +
        color +
        ".png'/><span class=''>未上传附件</span></div>";
      this.$tHeader = $(tHeader);
      this.$noData = $(noData);
      this.$fileContainer.append(this.$tHeader);
      this.$fileContainer.append(this.$noData);
      this.$containerElement.append(this.$fileContainer);
    }
  };

  WellFileUpload4Icon.prototype.getUserName = function () {
    var userInfo = SpringSecurityUtils.getUserDetails();
    return userInfo.userName;
  };

  /**
   * 组装html, 将从数据库中获取到的文件列表组装成html。
   */
  WellFileUpload4Icon.prototype.init = function (
    uploadable /* 是否则有上传的权限 */ ,
    downable /* 是否具有下载的权限 */ ,
    $containerElement /* 存放该附件的容器 */ ,
    signature /* 是否签名 */ ,
    dbFiles /* 是不是需要从数据库获取属于该ctlID及purpose的文件,true为需要，false为不需要 */
  ) {
    var _this = this;

    if ($containerElement == undefined || $containerElement == null || $containerElement == '') {
      throw new Error('请设置正确的控件容器');
    }
    var readOnly = false;
    if (!uploadable) {
      readOnly = true;
    }
    this.$containerElement = $containerElement;
    this.signature = signature == undefined || signature == null || signature == '' ? false : true;
    this.readOnly = readOnly == undefined || readOnly == null || readOnly == '' ? false : true;

    this.multiple = true;
    WellFileUpload.fileUploadObj[this.ctlID] = this;

    this.$fileContainer = null; // 用户保存文件列表的html元素

    this.files = null;

    // 获取保存文件信息的容器
    if (WellFileUpload.files[this.ctlID] == undefined) {
      // 容器不存在，新建
      WellFileUpload.files[this.ctlID] = [];
    }
    this.files = WellFileUpload.files[this.ctlID];

    if (!this.$toolbarContainer) {
      this.createToolbarContainer();
      // 按钮权限处理
      if (this.readOnly) {
        this.setReadOnly();
      } else {
        this.setEditable();
      }
      if (downable) {
        this.enableDownLoadFunction();
      } else {
        this.disableDownLoadFunction();
      }
    }

    this.createFileContainer();

    this.clean(false);
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
        dbFile.isNew = isNew;
        var fileObj = {};
        $.extend(fileObj, dbFile);
        this.addFile(fileObj);
      }
    }
  };

  var latestPluginVersion = ['1.0.1.6', '1,0,1,6'];

  // 判定文件是否已存在
  WellFileUpload4Icon.prototype.isFileNameExist1 = function (fileName, fileInfo, allowDuplicates) {
    var isExitFile = false;
    for (var i = 0; i < this.filePaths.length; i++) {
      if (fileName == this.filePaths[i] && !allowDuplicates) {
        isExitFile = true;
        break;
      } else if (fileName == this.filePaths[i] && allowDuplicates) {
        var name = fileName.substring(0, fileName.lastIndexOf('.'));
        var type = fileName.substring(fileName.lastIndexOf('.'));
        var count = this.getSameFileCount(this.filePaths, fileName);
        fileInfo.fileName = name + '(' + count + ')' + type;
        break;
      }
    }
    if (isExitFile) {
      alert('文件:[' + fileName + ']已经存在');
    }
    return isExitFile;
  };
  //获取相同文件的个数
  WellFileUpload4Icon.prototype.getSameFileCount = function (fileNameList, name) {
    var i = 0;
    $.each(fileNameList, function (index, item) {
      if (item == name) {
        i++;
      }
    });
    return i;
  };
  // 加入filePaths数组中
  WellFileUpload4Icon.prototype.addToExitFile1 = function (fileName) {
    var fileSize = this.filePaths.length;
    this.filePaths[fileSize] = fileName;
  };

  /**
   * 获取指定的控件ID的文件列表信息数组,数组成员为FileInfo类对象 若指定的ctlID不存在则返回undefined
   *
   * @param ctlID
   * @returns 返回的文件信息列表，是一个由FileInfo类对象组成的数组
   */
  WellFileUpload4Icon.prototype.getFiles = function () {
    return this.files;
  };

  WellFileUpload4Icon.prototype.enableBtn = function (code) {
    var fileuploadobj = this;
    var operateBtns = (fileuploadobj.operateBtns = fileuploadobj.operateBtns || []);
    operateBtns.push(code);
  };

  WellFileUpload4Icon.prototype.disableBtn = function (code) {
    var fileuploadobj = this;
    var operateBtns = (fileuploadobj.operateBtns = fileuploadobj.operateBtns || []);
    for (var i = 0; i < operateBtns.length; i++) {
      if (operateBtns[i] === code) {
        operateBtns.splice(i, 1);
      }
    }
  };
});

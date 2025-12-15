$(function () {
  WellFileUpload4Body = function (
    ctlID, // 控件唯一ID
    style, // 风格,参考对象iconFileControlStyle
    fileExt,
    fileMaxSize,
    fileMaxNum,
    defaultView,
    operateBtns,
    createHistory
  ) {
    this.ctlID = ctlID; // 文件上传input元素的元素ID
    // this.bodyFiles = [];
    this.style = style == undefined ? iconFileControlStyle.IconAndBody : style; // 该控件只显示图标附件，还是只显示正文，还是图标正文都要,默认都显示

    this.filePaths = []; // 保存文件的路径
    this.fileExt = fileExt;
    this.splitor = '|';
    if (appContext && appContext.getDocument) {
      //流程拟稿环节不保留痕迹
      this.pbRecord = appContext.getDocument().isDraft() == true ? false : true;
    } else {
      this.pbRecord = true; // 布尔型，true编辑要求保留痕迹；false编辑不保留痕迹add-by-wujx-20161024
    }
    this.fileMaxSize = fileMaxSize;
    this.filesLength = fileMaxNum;
    this.defaultView = defaultView;
    this.operateBtns = operateBtns || ['1', '4', '5', '15', '14', '7', '2', '8', '9', '3', '10', '11', '12', '13']; // 兼容旧数据
    // this.operateBtns = operateBtns || ["1", "5", "14", "7", "2", "8", "9", "3", "10", "11", "13"]; // 兼容旧数据
    this.createHistory = createHistory;
    var tip = [];
    if (fileExt) {
      var _fileExt = eval(fileExt).join(',');
      tip.push('支持' + _fileExt + '格式附件');
    }

    if (fileMaxNum) {
      tip.push('最多上传' + fileMaxNum + '个');
    }
    this.tip = tip;
  };

  WellFileUpload4Body.prototype = new WellFileUpload4Icon();

  WellFileUpload4Body.prototype.createToolbarContainer = function () {
    var _this = this;
    var toolbarId = 'toolbar_' + _this.ctlID;

    var fileHtml = '<div id="' + toolbarId + '" class="fileUpdateload">'; // 文件列表容器
    fileHtml +=
      '<span class="left-counter">    <span class="file_icon" style="display: block;float: left;margin-right: 8px;"></span>附件<span style="color:#999999;margin-left: 4px;">(<span class="counter">0</span>)</span><span class="fileTip" style="display:none;margin:0;"><i class="iconfont icon-ptkj-tishishuoming" style="color:#999;vertical-align:middle;"></i></span></span>' +
      '<input name="' +
      _this.ctlID +
      '" style="display: none">';
    fileHtml += '<span style="float:right;line-height: 32px;" class="fileUpload-btn">';
    fileHtml +=
      '<a class="allSelect"  style="cursor:pointer;"><label style="height:32px;"><input type="checkbox" class="checkAll" id="checkAll"/>全选</label></a>';
    // fileHtml += '<a class="downloadaszip" style="cursor:pointer;"><i
    // class="icon iconfont icon-ptkj-xiazai"></i>批量下载</a>';
    // fileHtml += '<a class="download" style="cursor:pointer;"><i
    // class="icon iconfont icon-ptkj-xiazai"></i>批量下载</a>';
    fileHtml += '<a class="newWordBody"  style="cursor:pointer;"><i class="icon iconfont icon-ptkj-bianji"></i>新建正文 </a>';
    var bodyApi = window.getBodyApi && window.getBodyApi();
    if (bodyApi && bodyApi.pasteFile) {
      fileHtml += '<a class="paste pasteWordBody" style="cursor:pointer;"><i class="icon iconfont icon-ptkj-niantie"></i>粘贴附件</a>';
    }
    fileHtml +=
      '<a class="imposeWordBody upload"  style="cursor:pointer;"><label style="padding: 0px;margin: 0px;border-width: 0px;line-height: normal;"><input type="file" style="width: 0px;height: 0px;position: absolute;opacity: 0;z-index: -1;" accept=".doc,.docx,.wps,.wpsx"/><i class="icon iconfont icon-ptkj-daoru"></i>导入正文</label></a>';
    var wellOfficeAssistant = window.wellOfficeAssistant;
    if ((wellOfficeAssistant && wellOfficeAssistant.saveAs) || (bodyApi && bodyApi.saveAsFile)) {
      fileHtml += '<a class="downloadSaveAs" style="width:105px;"><i class="icon iconfont icon-ptkj-xiazai"></i>另存为</a>';
    }
    fileHtml += '<a class="clean" style="cursor:pointer;display:none;"><i class="icon iconfont icon-ptkj-shanchu"></i>全部删除</a>';
    fileHtml += '<a class="delete" style="cursor:pointer;"><i class="icon iconfont icon-ptkj-shanchu"></i>批量删除</a>';
    var bodyBarHtml = '<a class="new">';
    bodyBarHtml += '<span class="newBody">新建正文<font class="down" style="font-size:9px">▼</font></span>';
    bodyBarHtml += '<div class="newContent ToolBtnOutset" style="display:none;position:absolute;width:100%;height:30px;">';
    bodyBarHtml += '<span class="newWordBody"  style="cursor:pointer;">文章正文</span><br/>';
    bodyBarHtml += '<span class="newExcelBody"  style="cursor:pointer;display:none;">表格正文</span></div>';
    bodyBarHtml += ' </a>';
    // fileHtml += bodyBarHtml;
    bodyBarHtml = '<a class="impose">';
    bodyBarHtml +=
      '<span class="imposeBody">导入正文<font class="down" style="font-size:9px">▼</font></span><div class="imposeContent ToolBtnOutset" style="display:none;position:absolute;width:100%;">';
    bodyBarHtml += '<span class="imposeWordBody"  style="cursor:pointer;" >文章正文</span><br/>';
    bodyBarHtml += '<span class="imposeExcelBody"  style="cursor:pointer;display:none;">表格正文</span></div>';
    bodyBarHtml += '</a>';
    // fileHtml += bodyBarHtml;

    // fileHtml += '<a class="imposeWordBody" style="cursor:pointer;"><i
    // class="icon iconfont icon-ptkj-daoru"></i>导入正文</a>';
    // fileHtml += '<a class="local-file-history" style="cursor:pointer;"><i
    // class="icon iconfont icon-ptkj-lishi"></i>历史记录 </a>';
    fileHtml +=
      '<a class="switchbtn" style="cursor:pointer;"><span class="defaultViewBtn"></span><i class="icon iconfont icon-ptkj-xianmiaojiantou-xia"></i></a>';
    fileHtml += '</span></div>';
    _this.$toolbarContainer = $(fileHtml);
    var $containerElement = _this.$containerElement.append(_this.$toolbarContainer);
    _this.$toolbarContainer.find('.newWordBody')[_this.operateBtns.indexOf('1') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.imposeWordBody')[_this.operateBtns.indexOf('5') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.downloadSaveAs')[_this.operateBtns.indexOf('15') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.delete')[_this.operateBtns.indexOf('14') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer.find('.paste')[_this.operateBtns.indexOf('4') > -1 ? 'show' : 'hide']();
    _this.$toolbarContainer
      .find('.allSelect')[(_this.operateBtns.indexOf('14') > -1 && !_this.readOnly) || _this.operateBtns.indexOf('15') > -1 ? 'show' : 'hide']();
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
    _this.$attachCounter = _this.$toolbarContainer.find('.counter'); // 附件计数器
    _this.$toolbarContainer.find('.newBody').click(function () {
      if ('none' != $('.imposeBody', $containerElement).next().css('display')) {
        $('.imposeBody', $containerElement).next().hide();
      }

      if ('none' == $('.newBody', $containerElement).next().css('display')) {
        $('.newBody', $containerElement).next().show();
      } else {
        $('.newBody', $containerElement).next().hide();
      }
    });

    _this.$toolbarContainer.find('.imposeBody').click(function () {
      if ('none' != $('.newBody', $containerElement).next().css('display')) {
        $('.newBody', $containerElement).next().hide();
      }

      if ('none' == $('.imposeBody', $containerElement).next().css('display')) {
        $('.imposeBody', $containerElement).next().show();
      } else {
        $('.imposeBody', $containerElement).next().hide();
      }
    });

    _this.checkErrors = [];

    _this.$toolbarContainer.find('.newWordBody').click(function () {
      var hasUploadFilesLen = _this.size();
      if (_this.filesLength) {
        var surplusFilesLen = _this.filesLength - hasUploadFilesLen;
        if (surplusFilesLen - 0 <= 0) {
          _this.checkErrors.push('附件数量最多不能超过' + _this.filesLength + '个');
        }
      }
      if (_this.checkErrors.length > 0) {
        appModal.error(_this.checkErrors.join(',') + '，请检查后重新上传');
        _this.checkErrors = [];
        return;
      } else {
        _this.newBody(WellFileUpload4Body.type.doc);
      }
      // $(this).parent().hide();
    });

    _this.$toolbarContainer.find('.newExcelBody').click(function () {
      _this.newBody(WellFileUpload4Body.type.excel);
      // $(this).parent().hide();
    });
    var url = ctx + fileServiceURL.saveFiles; // 上传文件的地址
    var $fileElement = _this.$toolbarContainer.find('.imposeWordBody input[type=file]');
    try {
      _this.fileExt = eval(_this.fileExt);
      var accept = '';
      $.each(_this.fileExt, function (idx, ext) {
        accept += '.' + ext + ',';
      });
      accept = accept.lastIndexOf(',') == accept.length - 1 ? accept.substr(0, accept.length - 1) : accept;
      $fileElement.attr('accept', accept);
    } catch (ex) {}

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
          source: toReplaceFile ? '替换' : '导入正文',
          bsMode: _this.isBsEditMode()
        });
      })
      .on('fileuploadsubmit', function (e, data) {
        var fileArr = data.files[0].name.split('.');
        var fileType = '.' + fileArr[fileArr.length - 1];
        var acceptArr = ['.doc', '.docx'];
        if (acceptArr.indexOf(fileType) < 0) {
          if (_this.checkErrors.indexOf('只能上传' + acceptArr.join(',') + '格式附件') == -1) {
            _this.checkErrors.push('只能上传' + acceptArr.join(',') + '格式附件');
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
          var oLen = data.originalFiles.length;
          if (oLen > surplusFilesLen) {
            _this.checkErrors.push('附件数量最多不能超过' + _this.filesLength + '个');
          }
        }
        if (_this.checkErrors.length > 0) {
          appModal.error(_this.checkErrors.join(',') + '，请检查后重新上传');
          _this.checkErrors = [];
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
            // if(!_this.isFileNameExist1(file.fileName, file)){
            _this.del([toReplaceFile], false); // 删除不确认
            _this.addFile(file);
            _this.triggerFileOperates('file-replace', toReplaceFile, file);
            // }
          } else {
            var fileInfo = data.result.data[0];
            fileInfo.operate = 'imp';
            _this.addFile(fileInfo);
            appModal.confirm('导入成功,是否打开编辑?', function (ret) {
              ret && _this.openLocalFile(fileInfo, true, fileInfo.filename);
            });
          }
          return false;
        });
      })
      .on('fileuploadprocessalways', function (e, data) {})
      .on('fileuploadfail', function (e, data) {
        appModal.hideMask();
        appModal.error('导入正文失败');
      });
    _this.$toolbarContainer.find('.imposeExcelBody').click(function () {
      _this.imposeBody(WellFileUpload4Body.type.excel);
    });

    _this.$toolbarContainer.find('.allSelect').click(function () {
      _this.$fileContainer.find('.itemChecked').attr('checked', this.checked);
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
    // 默认列表视图
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
        $menu.hide().detach();
        $this.find('i.icon').removeClass('icon-ptkj-xianmiaojiantou-shang').addClass('icon-ptkj-xianmiaojiantou-xia');
      } else {
        $menu.appendTo('body').show();
        $this.find('i.icon').removeClass('icon-ptkj-xianmiaojiantou-xia').addClass('icon-ptkj-xianmiaojiantou-shang');
      }
    });

    _this.$toolbarContainer.find('.delete').click(function () {
      // 清空
      var files = _this.getCheckedFiles();
      _this.del(files);
      $.each(files, function (idx, file) {
        _this.triggerFileOperates('file-delete-batch', file, null);
      });
    });

    _this.$toolbarContainer.find('.paste').click(function () {
      // 粘贴
      _this.pasteFile(false);
    });

    _this.$toolbarContainer.find('.download').click(function () {
      // 下载
      _this.download();
    });

    _this.$toolbarContainer.find('.downloadSaveAs').click(function () {
      // 另存为
      _this.downloadSaveAs();
    });

    _this.$toolbarContainer.find('.downloadaszip').click(function () {
      // 清空
      _this.downloadAsZIP();
    });

    _this.$toolbarContainer.find('.local-file-history').click(function () {
      // 查看本地历史文件
      _this.showLocalFileHistory();
    });
  };

  WellFileUpload4Body.prototype.setReadOnly = function () {
    if (this.fileLength == 0) {
      this.$fileContainer.hide();
      this.$toolbarContainer.hide();
    }
    this.disableUploadFunction();
    // this.disableDeleteFunction();
    this.$toolbarContainer.find('.delete').hide();
    this.$toolbarContainer.find('.paste').hide();
    this.$toolbarContainer.find('.imposeWordBody').hide();
    this.readOnly = true;
    this.disableShowBtnFunction();
  };

  WellFileUpload4Body.prototype.setEditable = function () {
    this.enableUploadFunction();
    this.enableDeleteFunction();
  };

  WellFileUpload4Body.prototype.enableDeleteFunction = function () {
    this.$toolbarContainer.find('.btn-delete').show();
  };

  WellFileUpload4Body.prototype.disableDeleteFunction = function () {
    this.$toolbarContainer.find('.btn-delete').hide();
  };

  WellFileUpload4Body.prototype.enableUploadFunction = function () {
    var self = this;
    self.$fileContainer && self.$fileContainer.show();
    if (self.$toolbarContainer) {
      self.$toolbarContainer.show();
      if (self.operateBtns.indexOf('1') > -1) {
        self.$toolbarContainer.find('.new,.newWordBody').show();
      }
      if (self.$toolbarContainer.find('.new,.newWordBody').length > 0 || self.$toolbarContainer.find('.paste').length > 0) {}
    }
  };

  WellFileUpload4Body.prototype.disableUploadFunction = function () {
    this.$toolbarContainer.find('.new,.newWordBody').hide();
  };

  WellFileUpload4Body.type = {
    doc: 1, // word正文
    xls: 2
    // excel正文
  };

  WellFileUpload4Body.prototype.newBody = function (type) {
    var _this = this,
      fnType;
    var fileType,
      fileName = prompt('请输入正文文件名', '正文');
    if (fileName == null) {
      return;
    }
    if ($.trim(fileName).length == 0 || $.trim(fileName) == 'undefined') {
      alert('输入文件名');
      return _this.newBody(type);
    }
    // 检查文件名的合法性
    if (!_this.checkFileName(fileName)) {
      _this.showFileNameInvalidMsg(fileName);
      return _this.newBody(type);
    }
    if (type === WellFileUpload4Body.type.xls) {
      // word正文
      fnType = 'openET';
      fileType = '.xls';
    } else {
      // excel正文
      fnType = 'openDoc';
      fileType = '.doc';
    }
    fileName += fileType;
    if (_this.isFileNameExist1(fileName)) {
      return _this.newBody(type);
    }
    var fileID = _this.createUuid();
    var bRevision = _this.readOnly != true && _this.pbRecord;
    var openOptions = {
      bsMode: _this.isBsEditMode(),
      docId: fileID,
      newFileName: fileName,
      uploadPath: location.origin + ctx + '/office/wps/savefiles?source=新建正文', // 保存文档接口
      buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
      disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
      suffix: fileType,
      userName: SpringSecurityUtils.getCurrentUserName(),
      "revisionCtrl": { // 痕迹控制 ，不传正常打开
        "bOpenRevision": bRevision, // true(打开)false(关闭)修订
        "bShowRevision": bRevision, // true(显示)/false(关闭)痕迹。会影响showRevisionsMode，true时，showRevisionsMode的1、3有效；false时，showRevisionsMode的2、4有效
        "bEnabledRevision": !bRevision, // true(有权限接受、取消保护)/false(无权限接受，受保护)痕迹
        // true(显示)/false(关闭)痕迹
      },
      openType: {
        protectType: _this.readOnly === true ? 3 : -1
      },
      success: function (result) {
        _this.del(
          [{
            fileID: fileID
          }],
          false
        ); // 删除不确认,可以多次保存
        $.each(result.data, function (idx, file) {
          var bFile = $.extend({}, file, {
            fileID: fileID
          });
          fileID = file.fileID; // 支持多次保存
          file.creator = file.creator || SpringSecurityUtils.getCurrentUserId();
          file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.uploadTime = file.uploadTime || new Date().format('yyyy-MM-dd HH:mm:ss');
          file.isNew = false;
          file.fileName = file.filename;
          _this.addFile(file);
          if ($.trim(type).length > 0) {
            type = null; // 新建正文标记,后面都是编辑
            _this.triggerFileOperates('file-new', null, file);
          } else {
            _this.triggerFileOperates('file-edit', bFile, file);
          }
        });
      },
      error: function (result) {
        alert(result.data);
      }
    };
    getBodyApi()[fnType](openOptions);
  };

  // 判定附件是否已存在
  WellFileUpload4Icon.prototype.isFileNameExist = function (fileName) {
    for (var i = 0; i < this.files.length; i++) {
      if (this.files[i].fileName == fileName) {
        // 已存在
        return true;
      }
    }
    return false;
  };
});

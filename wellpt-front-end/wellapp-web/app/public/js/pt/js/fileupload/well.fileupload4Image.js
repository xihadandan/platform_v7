$(function () {
  var deg = 0,
    scale = 1;
  function rotateImg($img, rotateDeg) {
    $img.css({
      transform: rotateDeg,
      '-o-transform': rotateDeg,
      /* Opera */
      '-ms-transform': rotateDeg,
      /* IE 9 */
      '-moz-transform': rotateDeg,
      /* Firefox */
      '-webkit-transform': rotateDeg
      /* Safari and Chrome */
    });
  }
  WellFileUpload4Image = function (
    ctlID // 控件唯一ID
  ) {
    this.ctlID = ctlID; // 文件上传input元素的元素ID
  };
  WellFileUpload4Image.prototype = new WellFileUpload('formWellFileUpload4Image', 'image');

  /**
   * 初始化控件实例,在控件预先有文件信息，但文件信息存在于数据库中时使用该初始化方法，调用方需要传入文件信息对应的文件夹及文件用途，再由控件将文件信息加载出来。
   * @param {boolean} readOnly  是否只读,例如只给一些用户只读的权限
   * @param {JQuery} $containerElement 存放该附件的容器
   * @param {boolean} signature 是否签名
   * @param {boolean} multiple  是否多文件
   * @param {string[]} folderID 文件夹ID
   * @param {string} purpose 文件用途
   * @param {number} imgWidth 图片宽度
   * @param {number} imgHeight 图片高度
   */
  WellFileUpload4Image.prototype.initWithLoadFilesFromFileSystem = function (
    readOnly,
    $containerElement,
    signature,
    multiple,
    folderID,
    purpose,
    imgWidth,
    imgHeight
  ) {
    if (folderID == undefined || folderID == null || folderID == '') {
      throw new Error('请设置正确的文件夹ID');
    }

    if (purpose == undefined || purpose == null || purpose == '') {
      throw new Error('请设置正确的文件用途');
    }
    var dbFiles = WellFileUpload.loadFilesFormDb(folderID, purpose);

    this.init(readOnly, $containerElement, signature, multiple, dbFiles, imgWidth, imgHeight);
  };

  /**
   *
   * @param {boolean} readOnly  是否只读,例如只给一些用户只读的权限
   * @param {JQuery} $containerElement 存放该附件的容器
   * @param {boolean} signature 是否签名
   * @param {boolean} multiple  是否多文件
   * @param {*} dbFiles
   * @param {number} imgWidth 图片宽度
   * @param {number} imgHeight 图片高度
   * @param {{pixelCheck: boolean; imgWidth: number; imgHeight: number;} | null} pixelObj 像素校验
   * @param {FileInfo[]} uploadedFiles 使用图片库弹窗的本地上传时，额外校验已经上传的图片数量和文件崇明
   * @param {number | null | false} fileMaxNum 图片最大数量
   */
  WellFileUpload4Image.prototype.init = function (
    readOnly,
    $containerElement,
    signature,
    multiple,
    dbFiles,
    imgWidth,
    imgHeight,
    pixelObj,
    uploadedFiles,
    fileMaxNum
  ) {
    if ($containerElement == undefined || $containerElement == null || $containerElement == '') {
      throw new Error('请设置正确的控件容器');
    }

    this.$containerElement = $containerElement;
    this.signature = signature == undefined || signature == null || signature == '' ? false : true;
    this.readOnly = readOnly == undefined || readOnly == null || readOnly == '' ? false : true;
    this.multiple = multiple == undefined || multiple == null || multiple == '' ? false : true;
    this.imgHeight = imgHeight == undefined || imgHeight == null || imgHeight == '' ? '110' : imgHeight;
    this.imgWidth = imgWidth == undefined || imgWidth == null || imgWidth == '' ? '110' : imgWidth;
    this.pixelObj = pixelObj;
    this.uploadedFiles = uploadedFiles;
    this.fileMaxNum = fileMaxNum;

    WellFileUpload.fileUploadObj[this.ctlID] = this;

    this.$fileContainer = null; // 用户保存文件列表的html元素

    this.files = null;

    // 获取保存文件信息的容器
    if (WellFileUpload.files[this.ctlID] == undefined) {
      // 容器不存在，新建
      WellFileUpload.files[this.ctlID] = [];
    }
    this.files = WellFileUpload.files[this.ctlID];

    var fileHtml = '';
    var fileListId = 'filelist' + this.ctlID;

    var btnHtml =
      '<div class="upload_div" id="upload_div" style="display: ' +
      (this.readOnly ? 'none' : '') +
      ';">' +
      '<label class="btn btn-primary fileinput-button2">' +
      '<span class="icon iconfont icon-ptkj-jiahao" style="display: block;"></span>' +
      '<span class="add_icon">点此添加</span>' +
      '<input id=' +
      this.ctlID +
      ' type="file" name="' +
      this.ctlID +
      '" ' +
      (this.multiple ? 'multiple' : '') +
      ' class="fileupload_css" accept=".png,.jpg,.jpeg,.gif,.bmp">' +
      '</label></div>';

    // fileHtml += '<div id=img_container_' + this.ctlID + ' ></div>';// 文件图片容器
    fileHtml += '<div id="' + fileListId + '" class="ui-image-container"></div>'; // 文件列表容器

    // $('#img_container_' + this.ctlID).html('<img src="" alt="显示图片"></img>');
    this.$containerElement.html(fileHtml);

    // if (!this.multiple) {
    //   // 单文件,默认为多文件
    //   $('#' + this.ctlID).removeAttr('multiple');
    // }

    var _this = this;

    _this.$fileContainer = $('#' + fileListId);

    var attach_list = 'attach-list' + _this.ctlID;
    var $attacheList = (_this.$attacheList = $('<ul class="' + attach_list + '" style="list-style-type:none;padding: 0;" />'));
    var $btnUploader = (_this.$btnUploader = $('<li class="btn-image-uploader">').append(
      $(btnHtml).height(this.imgHeight).width(this.imgWidth)
    ));
    $attacheList.append($btnUploader);
    if (!!_this.allowPreview) {
      $attacheList.addClass('allow-preview');
    }

    _this.$uploadInputElement = $btnUploader.find('input[type=file]');
    _this.$fileContainer.append($attacheList);
    $btnUploader[_this.allowUpload && !this.readOnly ? 'show' : 'hide']();

    var modalHTML =
      '<div class="modal fade pic-previewer">' +
      // 图片预览头部
      '  <div class="pic-previewer-header" style="user-select: none;">' +
      '    <span class="pic-previewer-header-title">图片名称</span>' +
      '    <span class="pic-previewer-header-middle-operations">' +
      '      <span class="turn-left"><i class="iconfont icon-ptkj-nishizhenxuanzhuan"></i></span>' +
      '      <span class="zoom-down"><i class="iconfont icon-ptkj-jianhao"></i></span>' +
      '      <span class="zoom-up"><i class="iconfont icon-ptkj-jiahao"></i></span>' +
      '      <span class="turn-right"><i class="iconfont icon-ptkj-shunshizhenxuanzhuan"></i></span>' +
      '    </span>' +
      '    <span class="pic-previewer-header-right-operations">' +
      '      <span class="pic-preview-close-btn"><i class="iconfont icon-ptkj-dacha-xiao"></i></span>' +
      '    </span>' +
      '  </div>' +
      // 图片预览头部
      '  <div class="pic-previewer-body">' +
      '    <img class="pic-previewer-img"></img>' +
      '    <button type="button" class="previous-pic-btn"><i class="iconfont icon-ptkj-xianmiaojiantou-zuo"></i></button>' +
      '    <button type="button" class="next-pic-btn"><i class="iconfont icon-ptkj-xianmiaojiantou-you"></i></button>' +
      '  </div>' +
      '</div>';

    var $modal = $(modalHTML);
    this.$modal = $modal;

    var $img = $modal.find('img'),
      img = $img[0],
      $zoomDown = $modal.find('i.icon-ptkj-jianhao');
    $modal.find('.pic-previewer-header .turn-left').on('click', function () {
      deg = (deg - 90) % 360;
      img.style.transform = 'rotate(' + deg + 'deg) scale(' + scale + ')';
    });
    $modal.find('.pic-previewer-header .turn-right').on('click', function () {
      deg = (deg + 90) % 360;
      img.style.transform = 'rotate(' + deg + 'deg) scale(' + scale + ')';
    });
    $modal.find('.pic-previewer-header .zoom-down').on('click', function () {
      if (scale < 0.2) {
        $zoomDown.css({
          cursor: 'not-allowed'
        });
        return;
      }
      scale = scale - 0.1;
      img.style.transform = 'rotate(' + deg + 'deg) scale(' + scale + ')';
    });
    $modal.find('.pic-previewer-header .zoom-up').on('click', function () {
      $zoomDown.css({
        cursor: 'pointer'
      });
      scale = scale + 0.1;
      img.style.transform = 'rotate(' + deg + 'deg) scale(' + scale + ')';
    });

    $modal.find('.pic-previewer-header .pic-preview-close-btn').on('click', function () {
      $modal.trigger('hidden.bs.modal');
    });

    $(window.top.document)
      .off('keydown')
      .on('keydown', function (e) {
        // ESC
        if (e.keyCode === 27) {
          $modal.trigger('hidden.bs.modal');
        }

        // Right
        if (e.keyCode === 39) {
          $modal.find('.pic-previewer-body .next-pic-btn').trigger('click');
        }

        // Left
        if (e.keyCode === 37) {
          $modal.find('.pic-previewer-body .previous-pic-btn').trigger('click');
        }
      });

    $modal.find('.pic-previewer-body .previous-pic-btn').on('click', function () {
      if ($(this).hasClass('disabled')) {
        return;
      }
      _this.switchPreviewImg(_this.$modal.data().currentIdx - 1);
    });

    $modal.find('.pic-previewer-body .next-pic-btn').on('click', function () {
      if ($(this).hasClass('disabled')) {
        return;
      }
      _this.switchPreviewImg(_this.$modal.data().currentIdx + 1);
    });

    $modal
      .on('hidden.bs.modal', function (event) {
        $modal.detach();
        $('.modal-backdrop').remove();
      })
      .modal({ show: false });

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
        _this.addFile(fileName, fileID, contentType, digestValue, digestAlgorithm, certificate, isNew /* 非新增的文件 */);
      }
    }
    // if(!_this.readOnly){
    _this.defineUploadEvent(); // 定义上传事件
    // }
  };

  /**
   * 删除上传功能
   */
  WellFileUpload4Image.prototype.disableUploadFunction = function () {
    $('#' + this.ctlID + ' .upload_div').hide();
    $('#' + this.ctlID + ' .btn-image-uploader').hide();
  };

  // 定义单个文件的删除功能
  WellFileUpload4Image.prototype.disableDeleteFunction = function (file) {
    $('#' + this.ctlID + ' ._delete_input')
      .attr('disabled', 'disabled')
      .hide();
  };

  /**
   * 显示上传功能
   */
  WellFileUpload4Image.prototype.enableUploadFunction = function () {
    $('#' + this.ctlID + ' .upload_div').show();
    $('#' + this.ctlID + ' .btn-image-uploader').show();
  };

  /**
   * 隐藏删除
   */
  WellFileUpload4Image.prototype.enableDeleteFunction = function (file) {
    $('#' + this.ctlID + ' ._delete_input').removeAttr('disabled');
  };

  /**
   * 可下载
   */
  WellFileUpload4Image.prototype.disableDownLoadFunction = function (file) {
    // $("#" + this.ctlID + " #imgherf_"+this.ctlID).attr('href', '#');
    // this.$fileContainer.find("li").each(function() {
    //     $(this).find(".filename").unbind('click');
    // });
    // this.$fileContainer.find("img[src]").unbind('click').bind('click', function(event) {
    //     event.preventDefault();
    //     event.stopPropagation();
    // });
  };

  /**
   * 不可下载
   */
  WellFileUpload4Image.prototype.enableDownLoadFunction = function (file) {
    // $("#" + this.ctlID + " #imgherf_"+this.ctlID).each(function(){
    // $(this).attr('href', $(this).attr('linkAddr'));
    // });
    // var self = this;
    // self.$fileContainer.find("li").each(function() {
    //     $ul = $(this);
    //     $ul.find(".filename").unbind('click').bind('click', function(event) {
    //         event.preventDefault();
    //         event.stopPropagation();
    //         // window.open(ctx + fileServiceURL.downFile + $(this).closest("li[fileID]").attr('fileID'));
    //         // self.previewImg(ctx + fileServiceURL.downFile + $(this).closest("li[fileID]").attr('fileID'));
    //     });
    // });
    // self.$fileContainer.find("img[src]").unbind('click');
  };

  WellFileUpload4Image.prototype.previewImg = function (files, currentIdx) {
    var self = this;
    self.$modal.data({ files: files, currentIdx: currentIdx, totalImages: files.length });

    if (files.length > 1) {
      self.$modal.find('.previous-pic-btn, .next-pic-btn').show();
    } else {
      self.$modal.find('.previous-pic-btn, .next-pic-btn').hide();
    }
    deg = 0;
    scale = 1;
    self.switchPreviewImg(currentIdx || 0);
    self.$modal.appendTo(window.top.document.body).modal('show');
  };

  WellFileUpload4Image.prototype.switchPreviewImg = function (idx) {
    var self = this;
    var files = self.$modal.data().files;
    var img = files[idx];
    var fileID = img.fileID;
    self.$modal.data({ currentIdx: idx });
    var url = '/repository/file/mongo/download?fileID=' + fileID;
    self.$modal.find('.pic-previewer-header .pic-previewer-header-title').text(img.fileName);
    var $img = self.$modal.find('img').attr('src', url);
    rotateImg($img, 'rotate(0deg)');

    self.$modal.find('.pic-previewer-body .previous-pic-btn').removeClass('disabled');
    self.$modal.find('.pic-previewer-body .next-pic-btn').removeClass('disabled');
    if (idx === 0) {
      self.$modal.find('.pic-previewer-body .previous-pic-btn').addClass('disabled');
    }
    if (idx === self.$modal.data().totalImages - 1 || self.$modal.data().totalImages === 1) {
      self.$modal.find('.pic-previewer-body .next-pic-btn').addClass('disabled');
    }
  };

  // 触发文件变更事件
  WellFileUpload4Image.prototype.triggerFileChangeEvent = function () {
    var self = this;

    self.$containerElement && self.$containerElement.trigger('filechange');
  };

  /**
   * 隐藏文件名列表 add by wujx 20160809
   */
  WellFileUpload4Image.prototype.hideFileNameList = function () {
    $('#filelist' + this.ctlID).hide();
  };

  /**
   * 显示文件名列表 add by wujx 20160809
   */
  WellFileUpload4Image.prototype.showFileNameList = function () {
    $('#filelist' + this.ctlID).show();
  };

  /**
   * 设置是否可上传 下载 删除
   */
  WellFileUpload4Image.prototype.initAllowUploadDeleteDownload = function (allowUpload, allowDelete, allowDownload) {
    this.allowUpload = allowUpload;
    this.allowDelete = allowDelete;
    this.allowDownload = allowDownload;
  };

  /**
   * 设置是否可以预览
   */
  WellFileUpload4Image.prototype.initAllowPreview = function (allowPreview) {
    this.allowPreview = allowPreview;
  };

  // 上传前的回调事件,返回true表示要上传返回false表示不上传
  WellFileUpload.prototype.beforeFileUpload = function (e, data) {
    if (this.beforeFileUploadCallback) {
      // 上传前的回调事件
      return this.beforeFileUploadCallback(e, data);
    }
    return true;
  };

  WellFileUpload4Image.prototype.defineUploadEvent = function () {
    var _this = this;

    var url = ctx + fileServiceURL.saveFiles; // 上传文件的地址

    var iframe = false;
    if ($.browser.msie && $.browser.version < 10) {
      iframe = true;
    }
    _this.checkErrors = [];
    _this.dataList = [];

    // $('input[type=file]#' + _this.ctlID).off().on("change", function(e){
    //   var files = $(this)[0].files;
    //   if(_this.pixelObj && _this.pixelObj.pixelCheck == "1"){
    //     var reader = new FileReader();
    //     reader.readAsDataURL(files[0]);
    //     reader.onload = function (e) {
    //       var src = this.result;
    //       var img = new Image();
    //       img.src = src;

    //       img.onload = function () {
    //         if(img.height - parseInt(_this.pixelObj.imgHeight) > 0 || img.width - parseInt(_this.pixelObj.imgWidth) > 0){
    //           _this.checkErrors.push("像素必须满足"+_this.pixelObj.imgWidth+"*"+_this.pixelObj.imgHeight);
    //         }
    //       };
    //     };
    //   }
    // })
    var imgLoadStatus = false;
    $('input[type=file]#' + _this.ctlID)
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
        maxFileSize: 5000000, // 5 MB
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true,
        // pasteZone: $('input[type=file]#' + _this.ctlID),
        dropZone: $('input[type=file]#' + _this.ctlID)
      })
      .on('fileuploadadd', function (e, data) {
        appModal.showMask();
        _this.dataList.push(data);
        if (data.files[0].size > _this.fileMaxSize) {
          var fileMaxSize = '附件大小不能超过' + (_this.fileMaxSize > 0 ? formatSize(_this.fileMaxSize) : '2000M');
          if (_this.checkErrors.indexOf(fileMaxSize) == -1) {
            _this.checkErrors.push(fileMaxSize);
          }
        }
        var originalFiles = data.originalFiles;
        var oLen = originalFiles.length;
        var accept = this.getAttribute('accept');
        var acceptArr = accept.split(',');

        for (var i = 0; i < originalFiles.length; i++) {
          var fileArr = originalFiles[i].name.split('.');
          var fileType = '.' + fileArr[fileArr.length - 1];
          if (acceptArr.indexOf(fileType) < 0) {
            if (_this.checkErrors.indexOf('只支持' + accept + '格式附件') == -1) {
              _this.checkErrors.push('只支持' + accept + '格式附件');
              break;
            }
          }
        }
        var hasUploadFilesLen = _this.size();
        if (_this.filesLength || _this.fileMaxNum) {
          var surplusFilesLen = 0;
          if (_this.fileMaxNum) {
            surplusFilesLen = _this.fileMaxNum - hasUploadFilesLen - _this.uploadedFiles.length;
          } else {
            surplusFilesLen = _this.filesLength - hasUploadFilesLen;
          }

          var oLen = data.originalFiles.length;
          if (oLen > surplusFilesLen) {
            if (_this.checkErrors.indexOf('附件数量不能超过' + (_this.filesLength || _this.fileMaxNum) + '个') == -1) {
              _this.checkErrors.push('附件数量不能超过' + (_this.filesLength || _this.fileMaxNum) + '个');
            }
          }
        }

        if (_this.pixelObj && _this.pixelObj.pixelCheck == '1') {
          var reader = new FileReader();
          reader.readAsDataURL(data.files[0]);
          reader.onload = function (e) {
            var src = this.result;
            var img = new Image();
            img.src = src;
            img.onload = function () {
              if (img.height - parseInt(_this.pixelObj.imgHeight) != 0 || img.width - parseInt(_this.pixelObj.imgWidth) != 0) {
                if (_this.checkErrors.indexOf('像素必须满足' + _this.pixelObj.imgWidth + 'px*' + _this.pixelObj.imgHeight + 'px') <= -1) {
                  _this.checkErrors.push('像素必须满足' + _this.pixelObj.imgWidth + 'px*' + _this.pixelObj.imgHeight + 'px');
                }
              }
              if (data.originalFiles[oLen - 1].name === data.files[0].name) {
                imgLoadStatus = true;
              }
            };
          };
        } else {
          imgLoadStatus = true;
        }

        var imgInterval = setInterval(function () {
          if (imgLoadStatus) {
            clearInterval(imgInterval);
            _this.isPixelObj = false;
            if (_this.checkErrors.length > 0) {
              if (data.originalFiles[oLen - 1].name === data.files[0].name) {
                appModal.error(_this.checkErrors.join('; ') + '，请检查后重新操作');
                _this.checkErrors = [];
                _this.dataList = [];
                appModal.hideMask();
                return false;
              }
            } else {
              if (data.originalFiles[oLen - 1].name === data.files[0].name) {
                $.each(_this.dataList, function (index, item) {
                  item.submit();
                });
                _this.dataList = [];
              }
            }
          }
        }, 100);
      })
      .on('fileuploadsubmit', function (e, data) {
        if (_this.signature && !_this.validSignatureUSB()) {
          appModal.hideMask();
          return false;
        }

        if (_this.allowFileNameRepeat == false) {
          var isForbid = false;
          var uploadedNamesMap = {};
          for (var i = 0; i < _this.files.length; i++) {
            var img = _this.files[i];
            uploadedNamesMap[img.fileName] = true;
          }
          if (_this.uploadedFiles) {
            for (var i = 0; i < _this.uploadedFiles.length; i++) {
              var img = _this.uploadedFiles[i];
              uploadedNamesMap[img.fileName] = true;
            }
          }
          for (var i = 0; i < data.files.length; i++) {
            var curFile = data.files[i];
            if (uploadedNamesMap[curFile.name]) {
              appModal.error('已上传过文件名称为【' + curFile.name + '】的文件，不允许重复上传！');
              isForbid = true;
            }
          }

          // if (_this.files.length > 0) {
          //   for (var i = 0; i < data.files.length; i++) {
          //     var curFile = data.files[i];
          //     for (var j = 0; j < _this.files.length; j++) {
          //       var hadUploadFile = _this.files[j];
          //       if (hadUploadFile.fileName == curFile.name) {
          //         appModal.error('已上传过文件名称为【' + curFile.name + '】的文件，不允许重复上传！');
          //         isForbid = true;
          //       }
          //     }
          //   }
          // }
          appModal.hideMask();
          if (isForbid) {
            return false;
          }
        }
        if (_this.beforeFileUpload(e, data) == false) {
          appModal.hideMask();
          return false;
        }

        return true;
      })
      .on('fileuploaddone', function (e, data) {
        appModal.hideMask();
        if (typeof data.result == 'undefined') {
          appModal.error('不支持上传该格式的文件');
        } else {
          var checkpass = true;
          $.each(data.result.data, function (index) {
            var fileNametmp = this.filename;
            if (fileNametmp != null) {
              fileNametmp = fileNametmp.toLowerCase();
              if (
                fileNametmp.indexOf('.jpg') == -1 &&
                fileNametmp.indexOf('.jpeg') == -1 &&
                fileNametmp.indexOf('.gif') == -1 &&
                fileNametmp.indexOf('.png') == -1 &&
                fileNametmp.indexOf('.bmp') == -1 &&
                fileNametmp.indexOf('.tiff') == -1 &&
                fileNametmp.indexOf('.pcx') == -1 &&
                fileNametmp.indexOf('.tga') == -1 &&
                fileNametmp.indexOf('.exif') == -1 &&
                fileNametmp.indexOf('.fpx') == -1 &&
                fileNametmp.indexOf('.svg') == -1 &&
                fileNametmp.indexOf('.psd') == -1 &&
                fileNametmp.indexOf('.cdr') == -1 &&
                fileNametmp.indexOf('.pcd') == -1 &&
                fileNametmp.indexOf('.dxf') == -1 &&
                fileNametmp.indexOf('.ufo') == -1 &&
                fileNametmp.indexOf('.eps') == -1 &&
                fileNametmp.indexOf('.ai') == -1 &&
                fileNametmp.indexOf('.raw') == -1
                // bmp,jpg,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw
              ) {
                appModal.error('不支持上传该格式的文件');
                checkpass = false;
              }
            }
          });

          if (!checkpass) {
            return;
          }

          $.each(data.result.data, function (index) {
            _this.addFile(this.filename, this.fileID, this.contentType, this.digestValue, this.digestAlgorithm, this.certificate, true);
            _this.triggerFileOperates('file-addimage', null, this);
          });
          // _this.triggerFileChangeEvent();
        }
      })
      .on('fileuploadprocessalways', function (e, data) {
        // var index = data.index, file = data.files[index];
        // if (file.error == "File is too large") {
        // 	appModal.error("附件大小不能超过"+(data.maxFileSize > 0 ? formatSize(data.maxFileSize) : "2000M") + "，请检查后重新上传");
        // 	appModal.hideMask();
        // }
      })
      .on('fileuploadfail', function (e, data) {
        appModal.hideMask();
        if (typeof data.result == 'undefined') {
          appModal.alert('请检查是否已登录或者不支持上传该格式的文件');
        } else {
          $.each(data.result.files, function (index, file) {
            var error = $('<span/>').text(file.error);
            $(data.context.children()[index]).append('<br>').append(error);
          });
        }
      });
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

  WellFileUpload4Image.prototype.addFile = function (
    filename,
    fileID,
    contentType,
    digestValue,
    digestAlgorithm,
    certificate,
    isNew
    /*
     * 新增的文件为true,
     * 否则为false
     */
  ) {
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

    var $ul = this.$attacheList;

    if (!this.multiple) {
      // 单文件上传
      // this.files.length = 0;
      $ul.find('._delete_input').last().trigger('click'); // 删除最后一个文件
    }

    $ul.attr('fileID', fileID);

    var $item = $('<li filename="' + filename + '"></li>');
    $item.attr('fileID', fileID);
    this.$btnUploader.before($item);
    // $ul.append('<li filename="'+filename+'"');
    var _html = '<button type="button" class="filename icon iconfont icon-ptkj-fangda" filename="' + filename + '" title="预览"></button>';
    if (this.allowDownload) {
      _html += '<button type="button" class="download_input icon iconfont icon-ptkj-xiazai" title="下载"></button>';
    }

    if (this.readOnly) {
      //如果只读，则不提供删除 的功能
      // _html += "</div>";
    } else if (this.allowDelete) {
      _html +=
        "<button type='button' id='delete' class='_delete_input icon iconfont icon-ptkj-shanchu' filename='" +
        filename +
        "' title='删除'></button>";
    }
    _html += '<div class="img-preview"></div>';

    // $item.html(_html);
    $item
      .mouseenter(function (event) {
        $item.addClass('active');
      })
      .mouseleave(function (event) {
        $item.removeClass('active');
      });
    try {
      var $divimg = $('<a></a>').attr({
        id: 'imgherf_' + this.ctlID,
        name: 'imgherf_' + this.ctlID,
        href: ctx + fileServiceURL.downFile + fileID,
        linkAddr: ctx + fileServiceURL.downFile + fileID
      });

      $divimg.append(_html);
      $item.append($divimg);
      var imgelement = document.createElement('img');
      imgelement.src = ctx + fileServiceURL.downFile + fileID;
      imgelement.setAttribute('id', 'img_' + fileID);
      imgelement.setAttribute('name', 'img_' + fileID);
      imgelement.setAttribute('style', 'width:' + this.imgWidth + 'px;height:' + this.imgHeight + 'px;');
      $divimg.append(imgelement);
      $('#' + this.ctlID + ' #imgherf_' + self.ctlID).show();
    } catch (e) {
      console.error(e);
    }

    var isExist = false;
    // 图片库需求，图片允许重复
    // for (var index = 0; index < this.files.length; index++) {
    //   if (_this.files[index].fileID == fileID) {
    //     isExist = true;
    //   }
    // }

    if (!isExist) {
      var file = new FileInfo();
      file.fileID = fileID;
      file.fileName = filename;
      file.digestAlgorithm = digestAlgorithm == undefined ? '' : digestAlgorithm;
      file.digestValue = digestValue == undefined ? '' : digestValue;
      file.certificate = certificate == undefined ? '' : certificate;
      file.signatureValue = '';
      file.contentType = contentType == undefined ? '' : contentType;
      file.isNew = isNew;
      file.$fileItemElement = $item;
      this.files.push(file);
      this.triggerFileChangeEvent();
      var editableElement = this.$containerElement.find("input[name='" + this.ctlID + "']");
      if (typeof Theme != 'undefined' && editableElement.length > 0) {
        Theme.validationRules.unhighlight(editableElement);
        Theme.validationRules.success(null, editableElement);
      }
    }

    //阻止a链接默认事件下载文件
    $item.find('a').click(function (event) {
      event.preventDefault();
      event.stopPropagation();

      if (_this.allowPreview !== false) {
        var $this = $(this);
        var idx = $this.closest('li').index();
        _this.previewImg(_this.files, idx);
      }
    });

    // 下载文件
    $item.find('button.download_input').click(function (event) {
      event.preventDefault();
      event.stopPropagation();
      window.open(ctx + fileServiceURL.downFile + fileID, '_self');
    });

    // 预览文件
    // $item.find('img').click(function (event) {
    //   event.preventDefault();
    //   event.stopPropagation();
    //   _this.previewImg(ctx + fileServiceURL.downFile + $(this).closest('li[fileID]').attr('fileID'));
    // });

    // 删除文件
    $item.find('button._delete_input').click(function (event) {
      event.preventDefault();
      event.stopPropagation();
      $(this).closest('li[filename]').remove();
      var bFile = null;
      for (var index = 0; index < _this.files.length; index++) {
        if (_this.files[index].fileID == fileID) {
          bFile = _this.files[index];
          _this.files.splice(index, 1);
        }
      }

      _this.triggerFileChangeEvent();
      bFile && _this.triggerFileOperates('file-delete', bFile, null);
    });

    // 设置是否可上传
    if (true == this.allowDownload) {
      this.enableDownLoadFunction();
    } else {
      this.disableDownLoadFunction();
    }
    // 设置是否可下载
    if (true == this.allowDelete) {
      this.enableDeleteFunction();
    } else {
      this.disableDeleteFunction();
    }
  };
});

(function ($) {
  var columnProperty = {
    // 控件字段属性
    applyTo: null, // 应用于
    columnName: null, // 字段定义 fieldname
    displayName: null, // 描述名称 descname
    dbDataType: '', // 字段类型 datatype type
    indexed: null, // 是否索引
    showed: null, // 是否界面表格显示
    sorted: null, // 是否排序
    sysType: null, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, // 长度
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null // 只读状态下设置跳转的url,
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  /*
   * FILEUPLOAD4IMAGE CLASS DEFINITION ======================
   */
  var FileUpload4Image = function (element, options) {
    this.$element = $(element);
    this.options = $.extend({}, $.fn['wfileUpload4Image'].defaults, options, this.$element.data());
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = this.$element;
  };

  FileUpload4Image.prototype = {
    constructor: FileUpload4Image,
    init: function () {
      var self = this;
      $.wControlInterface.initControlEvents.call(this);
      this.invoke('beforeInit', this.options);
      if (this.initReadOnly()) {
        this.options.allowUpload = this.options.allowDelete = false;
      }

      // 参数初始化
      $.ControlUtil.setCommonCtrAttr(this.$element, this.options);
      this.$element.attr('id', this.$element.attr('name'));

      if (this.options.flowSecDevBtnIdStr) {
        var flowSecDevBtnIdStr = this.options.flowSecDevBtnIdStr ? this.options.flowSecDevBtnIdStr.split(';') : [];
        this.options.allowDownload = flowSecDevBtnIdStr.indexOf('allowDownload') > -1 ? true : false;
        this.options.allowUpload = flowSecDevBtnIdStr.indexOf('allowUpload') > -1 ? true : false;
        this.options.allowDelete = flowSecDevBtnIdStr.indexOf('allowDelete') > -1 ? true : false;
        this.options.allowPreview = flowSecDevBtnIdStr.indexOf('allowPreview') > -1 ? true : false;
      }

      var mutiselect = this.options.mutiselect || false;

      // var setReadOnly=false;
      // setReadOnly=!this.options.allowUpload;;
      this.$element.hide();
      var id = this.$element.attr('id'); // 字段名称

      // var uploaddivhtml="<div id='_fileupload"+id+"'></div>";

      // this.$element.after(uploaddivhtml);
      var $attachContainer = this.$element;
      this.$element.show();
      // 创建上传控件
      // var elementID =
      // WellFileUpload.getCtlID4Dytable(this.options.mainTableName, id,
      // 0);
			this.fileuploadobj = new WellFileUpload4Image(id);
			self.fileuploadobj.formUuid = self.getFormDefinition().uuid;
			self.fileuploadobj.dataUuid = self.getDataUuid() || self.getCurrentForm().getDataUuid();
			self.fileuploadobj.fieldName = self.getFieldName();
      this.fileuploadobj.initAllowUploadDeleteDownload(this.options.allowUpload, this.options.allowDelete, this.options.allowDownload);
      this.fileuploadobj.initAllowPreview(this.options.allowPreview);
      // add by wujx 20160606 begin
      this.fileuploadobj.initAllowFileNameRepeat(this.options.allowFileNameRepeat);
      // add by wujx 20160606 end

      var ctlwidth = this.options.commonProperty.ctlWidth; // 宽度
      var ctlhight = this.options.commonProperty.ctlHight;
      var pixelObj = {};
      pixelObj.pixelCheck = this.options.pixelCheck;
      pixelObj.imgWidth = this.options.imgWidth;
      pixelObj.imgHeight = this.options.imgHeight;

      var files = [];
      if (this.options.columnProperty.data) {
        files = this.options.columnProperty.data;
      }

      this.fileuploadobj.init(
        this.initReadOnly(),
        $attachContainer,
        this.options.enableSignature,
        mutiselect,
        files,
        ctlwidth,
        ctlhight,
        pixelObj
      );
      this.addMustMark();
      // add by wujx 20160614 begin
      this.displayByShowType();
      // add by wujx 20160614 end
      var tip = [];
      if (this.options.fileExt) {
        tip.push('支持附件格式：' + this.options.fileExt);
        this.fileuploadobj.setFileExt(this.options.fileExt);
      }
      if (this.options.fileMaxSize) {
        tip.push('附件大小：不超过' + this.options.fileMaxSize + 'M');
        this.fileuploadobj.setFileMaxSize(this.options.fileMaxSize);
      }
      if (this.options.fileMaxNum) {
        tip.push('附件最多' + this.options.fileMaxNum + '个');
        this.fileuploadobj.setMaxFilesLength(this.options.fileMaxNum);
      }
      if (pixelObj.imgWidth && pixelObj.imgWidth != '' && pixelObj.imgHeight && pixelObj.imgHeight != '') {
        tip.push('尺寸：' + pixelObj.imgWidth + 'px*' + pixelObj.imgHeight + 'px');
      } else if (this.options.fileTips && this.options.fileTips != '') {
        var re = /^[0-9]\d*$/;
        var curPiexl = this.options.fileTips.split('*');
        var width = curPiexl[0];
        var height = curPiexl[1] ? curPiexl[1] : '';
        width = width != '' && width.indexOf('px') == width.length - 2 ? width.split('px')[0] : '';
        height = height != '' && height.indexOf('px') == height.length - 2 ? height.split('px')[0] : '';
        if (width != '' && re.test(width) && height != '' && re.test(height)) {
          tip.push('尺寸：' + this.options.fileTips);
        }
      }

      if (tip.length > 0 && this.fileuploadobj.$fileContainer) {
        this.tip = tip;
        this.fileuploadobj.$fileContainer.append('<span class="tip">' + tip.join(';  ') + '</span>');
      }
      if (!this.initReadOnly()) {
        this.initInputEvents();
      }
      $(self.$element).on('filechange', function () {
        self.invoke('afterSetValue', true);
      });

      this.invoke('afterInit', true);
      this.interceptUploadBtn();
      this.bindEvent();
    },

    interceptUploadBtn: function () {
      var _this = this;
      this.$element
        .find('#upload_div')
        .off()
        .on('click', function (e) {
          if (_this.options.operationMode === '1') {
            return;
          }

          e.preventDefault();
          _this.openDialog();
        });
    },

    _getAvailableCategories: function () {
      var _this = this;
      var selectedCategories = [];
      // 排除已经被删除的分类
      $.ajax({
        type: 'GET',
        url: '/basicdata/img/category/queryAllCategory',
        dataType: 'json',
        async: false,
        success: function (result) {
          var selectedCategoriesUuidMap = {};

          result.data.sort(function (a, b) {
            return a.code.localeCompare(b.code);
          });

          for (var i = 0; i < _this.options.selectedCategories.length; i++) {
            var uuid = _this.options.selectedCategories[i];
            selectedCategoriesUuidMap[uuid] = true;
          }
          for (var i = 0; i < result.data.length; i++) {
            var item = result.data[i];
            if (selectedCategoriesUuidMap[item.uuid]) {
              item.id = item.uuid;
              item.text = item.name;
              b;
              selectedCategories.push(item);
            }
          }
        }
      });
      selectedCategories.sort(function (a, b) {
        return a.code.localeCompare(b.code);
      });
      return selectedCategories;
    },

    _bindDialogEvents: function () {
      var _this = this;

      // 导航栏点击事件
      $('.modal-content .panel-body .nav-tabs .panel-tab')
        .off('click')
        .on('click', function (e) {
          e.preventDefault();
          var $this = $(this);
          var anchor = $this.find('a').attr('href');
          $this.closest('.nav-tabs').children('.panel-tab').removeClass('active');
          $this.addClass('active');
          $('.modal-content .tab-content .tab-pane').removeClass('active');
          $('.modal-content .tab-content .tab-pane' + anchor).addClass('active');

          if ($this.find('a[href=#pic-lib-panel]').length) {
            $('.modal-footer').show();
            $('.modal-body').css('height', '494px');
            _this._resetCategoryImagesCounts();
            var $activeCategory = $('.modal-content .tab-content .pic-category-col-list .category-item.active');
            var categoryUuid = $activeCategory.attr('uuid');
            _this._getPicturesByCategoryUuid(categoryUuid);
          } else {
            $('.modal-footer').hide();
            $('.modal-body').css('height', '600px');

            $('.modal-content #pic-upload-panel .form-horizontal').css('height', '475px');
          }
        });

      // 图片上传 - 保存到图片库 开关点击事件
      $('.modal-content #saveToPicLib')
        .off('change')
        .on('change', function () {
          var $this = $(this);
          var isSaveToPictureLib = $this.prop('checked');
          if (isSaveToPictureLib) {
            $('.modal-content #pic-lib-categories-row').show();
            // $('.modal-content #pic-upload-panel .form-horizontal').css('height', '430px');
            // $('.modal-content #pic-uploader').css('height', '240px');
          } else {
            $('.modal-content #pic-lib-categories-row').hide();
            // $('.modal-content #pic-upload-panel .form-horizontal').css('height', '480px');
            // $('.modal-content #pic-uploader').removeProp('disabled').css('height', '290px');
          }
        })
        .trigger('change');

      // 重设图片库分类 `title`
      $('.modal-content .pic-category-col-list')
        .off('hover')
        .on('hover', '.category-item:not(".title-reset")', function () {
          var $category = $(this).addClass('title-reset');
          var item = $category.data();
          var $label = $category.find('.category-name');
          var labelEl = $label.get(0);
          var clientWidth = labelEl.clientWidth;
          var scrollWidth = labelEl.scrollWidth;

          var title = '';
          if (scrollWidth > clientWidth) {
            title = item.name;
          }

          if (item.description) {
            title += (title ? '\r' : '') + '分类描述：' + item.description;
          }

          $category.attr('title', title || '');
        });

      // 图片上传事件
      $('.modal-content #pic-upload-panel .upload-btn-wrapper button')
        .off('click')
        .on('click', function () {
          if (!_this.dialogPicUploadObj.files.length) {
            // 关闭弹窗
            _this.$dialog.modal('hide');
            return;
          }

          // 保存图片到分类s
          var isSaveToCategory = $('.modal-content #pic-upload-panel #saveToPicLib').prop('checked');
          if (isSaveToCategory) {
            if ((categoryUuids = $('.modal-content #pic-lib-categories-control').val().length)) {
              _this._savePictureToCategory();
            } else {
              appModal.error('请先选择分类！');
              return;
            }
          }

          // 保存图片
          _this.getValue(function (originalValue) {
            $.merge(originalValue, _this.dialogPicUploadObj.files);
            if (!_this.fileuploadobj.multiple) {
              _this.fileuploadobj.$attacheList.find('._delete_input').last().trigger('click'); // 删除最后一个文件
            }
            _this.setValue(originalValue);
            $.each(_this.dialogPicUploadObj.files, function (index) {
              _this.addLog('file-addimage', null, this);
            });

            // 关闭弹窗
            _this.$dialog.modal('hide');
          });
        });

      // 图片库分类点击事件
      $('.modal-content .pic-category-col-list .category-item')
        .off('click')
        .on('click', function (e) {
          var $this = $(this);
          $this.closest('.pic-category-col-list').children('.category-item').removeClass('active');
          $this.addClass('active');
          var data = $this.data();
          _this._getPicturesByCategoryUuid(data.uuid);
        });
      $('.modal-content .pic-category-col-list .category-item:first').trigger('click');

      // 图片库删除按钮点击事件
      $('.modal-content #pic-lib-panel .pic-delete-btn')
        .off('click')
        .on('click', function () {
          var fileIDs = _this._getActiveCategorySelectedImages();

          if (!fileIDs.length) {
            appModal.error('请先选择要删除的记录！');
            return;
          }
          var $activeCategory = $('.modal-content #pic-lib-panel .pic-category-col-list .category-item.active');
          var categoryUuid = $activeCategory.data().uuid;
          appModal.confirm('确定要删除吗？<br>删除后，即在图片库的当前分类中，<br>删除图片，无法恢复，请谨慎操作！', function (result) {
            if (result) {
              $.ajax({
                url: '/basicdata/img/category/delImgs/' + categoryUuid,
                dataType: 'json',
                method: 'post',
                data: {
                  fileIDs: JSON.stringify(fileIDs)
                },
                success: function (result) {
                  $('.modal-content #pic-lib-panel .pic-selection-cards-row .pic-card.selected').remove();
                  _this._resetActiveCategoryPictureSelectionData();
                }
              });
            }
          });
        });

      // 图片库类别筛选器变更事件
      $('.modal-content #pic-lib-panel #pic-file-type-selector')
        .off('change')
        .on('change', function () {
          _this._filterPictureByLibFileExtType();
        });

      // 图片点击事件
      $('.modal-content #pic-lib-panel .pic-selection-cards-row')
        .off('click', '.pic-card')
        .on('click', '.pic-card', function (e) {
          var $this = $(this);
          if ($(e.target).is('img')) {
            var files = $('.tab-content .pic-category-col-list .category-item.active').data('images');
            var idx = $this.closest('.pic-card').index();
            _this.dialogPicUploadObj.previewImg(files, idx);
            return;
          }

          $this.toggleClass('selected');
          _this._resetActiveCategoryPictureSelectionData();
        });
    },

    _getActiveCategorySelectedImages: function () {
      var selectedFilesIDS = [];
      var $selectedCards = $('.modal-content #pic-lib-panel .pic-selection-cards-row .pic-card.selected');
      $.each($selectedCards, function (idx, card) {
        var $card = $(card);
        selectedFilesIDS.push($card.data().fileObj.fileID);
      });
      return selectedFilesIDS;
    },

    _resetCategoryImagesCounts: function () {
      $.ajax({
        type: 'GET',
        url: '/basicdata/img/category/queryAllCategory',
        dataType: 'json',
        async: false,
        success: function (result) {
          for (var i = 0; i < result.data.length; i++) {
            var category = result.data[i];
            var $category = $('.modal-content #pic-lib-panel .pic-category-col-list .category-item[uuid=' + category.uuid + ']');
            $category.find('.category-count .total').text(category.fileIDs.length || 0);
          }
        }
      });
    },

    _resetActiveCategoryPictureSelectionData: function () {
      var $activeCategory = $('.modal-content #pic-lib-panel .pic-category-col-list .category-item.active');
      var $selectedCards = $('.modal-content #pic-lib-panel .pic-selection-cards-row .pic-card.selected');
      var $allCards = $('.modal-content #pic-lib-panel .pic-selection-cards-row .pic-card');
      $activeCategory.find('.category-count .selected').text($selectedCards.length);
      $activeCategory.find('.category-count .total').text($allCards.length);

      var selectedFilesIDS = [];
      $.each($selectedCards, function (idx, card) {
        var $card = $(card);
        selectedFilesIDS.push($card.attr('fileid'));
      });
      $activeCategory.data('selectedFilesIDS', selectedFilesIDS);
    },

    _getAllSelectedPictures: function () {
      var files = [];
      var $categories = $('.modal-content #pic-lib-panel .pic-category-col-list .category-item');

      $.each($categories, function (idx, item) {
        var $category = $(item);
        var selectedFileIDs = $category.data('selectedFilesIDS') || [];
        var images = $category.data('images');
        for (let i = 0; i < selectedFileIDs.length; i++) {
          var image = images.find(function (img) {
            return img.fileID === selectedFileIDs[i];
          });
          files.push(image);
        }
      });

      return files;
    },

    _filterPictureByLibFileExtType: function () {
      var _this = this;
      var fileType = $('.modal-content #pic-file-type-selector').val();
      if (fileType === 'customFiles' && _this.options.fileExt) {
        var fileExts = JSON.parse(_this.options.fileExt);
        $.each($('.modal-content .pic-selection-cards-row .pic-card'), function (idx, card) {
          var $card = $(card);
          var ext = $card.attr('ext');
          var match = fileExts.some(function (fileExt) {
            return fileExt.toLowerCase().trim() === ext.toLowerCase().trim();
          });
          if (match) {
            $card.show();
          } else {
            $card.hide();
          }
        });
      } else {
        $('.modal-content .pic-selection-cards-row .pic-card').show();
      }
    },

    _getPicturesByCategoryUuid: function (uuid) {
      var _this = this;
      appModal.showMask();
      $.ajax({
        url: '/basicdata/img/category/queryImgs/' + uuid,
        dataType: 'json',
        method: 'GET',
        success: function (result) {
          _this._renderCategoryPictures(uuid, result.data);
          appModal.hideMask();
        }
      });
    },

    _renderCategoryPictures: function (categoryUuid, images) {
      var $category = $('.modal-content .pic-category-col-list .category-item[uuid=' + categoryUuid + ']');
      $category.data('images', images);

      var $cards = $('.modal-content .pic-selection-cards-row').empty();
      var selectedFilesIDS = $('.modal-content .pic-category-col-list .category-item.active').data('selectedFilesIDS') || [];

      if (!images) {
        return;
      }

      for (var i = 0; i < images.length; i++) {
        var img = images[i];
        var cardContent =
          '<div class="pic-card">' +
          '  <div class="pic-card-img allow-preview">' +
          '    <img></img>' +
          '  </div>' +
          '  <div class="pic-card-body">' +
          '    <p class="pic-card-img-name ellipsis"></p>' +
          '    <p class="pic-card-img-info"></p>' +
          '    <p class="pic-card-img-size"></p>' +
          '  </div>' +
          '</div>';

        var $card = $(cardContent).data({
          fileObj: img
        });
        var ext = img.fileName.split('.')[img.fileName.split('.').length - 1].toUpperCase();
        var size = formatSize(img.fileSize);
        $card.attr({
          ext: ext,
          fileID: img.fileID
        });
        $card.find('.pic-card-body .pic-card-img-name').text(img.fileName).attr('title', img.fileName);
        $card.find('.pic-card-img img').attr({
          src: '/repository/file/mongo/download?fileID=' + img.fileID,
          name: img.fileName
        });

        $card.find('.pic-card-body .pic-card-img-info').text(ext + '/' + size);
        $card.find('.pic-card-body .pic-card-img-size').text(img.width + '×' + img.height + 'px');
        $card.appendTo($cards);

        if ($.inArray(img.fileID, selectedFilesIDS) > -1) {
          $card.addClass('selected');
        }
      }
      this._filterPictureByLibFileExtType();
    },

    _savePictureToCategory: function () {
      var categoryUuids = $('.modal-content #pic-lib-categories-control').val().split(';');
      var fileIDs = [];

      $.each($('.modal-content #pic-uploader .attach-listpic-uploader li'), function (idx, img) {
        var fileID = $(img).attr('fileid');
        if (fileID) {
          fileIDs.push(fileID);
        }
      });

      if (!fileIDs.length) {
        return;
      }

      for (var i = 0; i < categoryUuids.length; i++) {
        var categoryUuid = categoryUuids[i];
        $.ajax({
          url: '/basicdata/img/category/addImgs/' + categoryUuid,
          dataType: 'json',
          method: 'POST',
          data: {
            fileIDs: JSON.stringify(fileIDs)
          },
          success: function (result) {
            console.log(result);
          }
        });
      }
    },

    _renderCategories: function (selectedCategories) {
      var $list = $('.modal-content #pic-lib-panel .pic-category-col-list');
      for (var i = 0; i < selectedCategories.length; i++) {
        var category = selectedCategories[i];
        var $li = $('<li class="category-item pl-sm pr-sm"><span class="category-name ellipsis">' + category.name + '</span></li>').attr({
          title: category.name,
          uuid: category.uuid
        });

        if (category.icon) {
          var $icon = $('<span class="mr-sm msgIconShow"></span>')
            .addClass(category.icon)
            .css('background', category.color || '')
            .prependTo($li.children('.category-name'));
        } else {
          $li.children('.category-name').css({
            'padding-left': '33px'
          });
        }
        var totalImages = category.fileIDs.length;

        var $count = $('<span class="category-count pull-right ml-sm"></span>').append(
          '(<span class="selected">0</span>/<span class="total">' + totalImages + '</span>)'
        );
        $li.append($count).appendTo($list).data(category);
      }
    },

    _renderDialogUploader: function (uploadedFiles) {
      var _this = this;
      var allowDownload = _this.options.allowDownload;
      var allowDelete = _this.options.allowDelete;
      var multiSelect = _this.options.mutiselect || false;
      var ctlWidth = _this.options.commonProperty.ctlWidth; // 宽度
      var ctlHeight = _this.options.commonProperty.ctlHight;
      var fileMaxNum = _this.options.fileMaxNum ? +_this.options.fileMaxNum : false;
      var pixelObj = {};
      pixelObj.pixelCheck = _this.options.pixelCheck;
      pixelObj.imgWidth = _this.options.imgWidth;
      pixelObj.imgHeight = _this.options.imgHeight;

      if (WellFileUpload.fileUploadObj['pic-uploader']) {
        delete WellFileUpload.fileUploadObj['pic-uploader'];
        delete WellFileUpload.files['pic-uploader'];
      }

      var picUploadObj = new WellFileUpload4Image('pic-uploader');
      picUploadObj.initAllowUploadDeleteDownload(true, true, false);
      picUploadObj.initAllowFileNameRepeat(!!_this.options.allowFileNameRepeat);
      picUploadObj.initAllowPreview(true);
      // if (_this.options.fileExt) {
      //   picUploadObj.setFileExt(_this.options.fileExt);
      // }
      if (_this.options.fileMaxSize) {
        picUploadObj.setFileMaxSize(_this.options.fileMaxSize);
      }
      // if (this.options.fileMaxNum) {
      //   picUploadObj.setMaxFilesLength(this.options.fileMaxNum);
      // }

      picUploadObj.init(false, $('#pic-uploader'), false, multiSelect, [], ctlWidth, ctlHeight, pixelObj, uploadedFiles, fileMaxNum);
      if (_this.options.fileExt) {
        var exts = JSON.parse(_this.options.fileExt).map(function (ext) {
          return '.' + ext;
        });

        $('input#pic-uploader').attr('accept', exts.join(','));
      }

      if (_this.tip && _this.tip.length) {
        $('.modal-content .upload-tip').text(_this.tip.join('; '));
      }
      _this.dialogPicUploadObj = picUploadObj;
    },

    openDialog: function () {
      var _this = this;
      var content = _this.getDialogHtml();
      if (_this.options.fileExt) {
        _this.options.fileExt = _this.options.fileExt.replace(/'/g, '"');
      }
      _this.$dialog = appModal.dialog({
        title: '添加图片',
        message: content,
        height: '600px',
        size: 'large',
        shown: function () {
          $('.modal-body').css('padding', '0px');

          if (_this.options.operationMode === '2') {
            $('.modal-body .panel-body').addClass('hide-uploader');
          }

          if (_this.options.fileExt) {
            $('.modal-body .panel-body #pic-file-type-selector').val('customFiles').parent('span').show();
          }

          var selectedCategories = _this._getAvailableCategories();
          _this._renderCategories(selectedCategories);

          _this.getValue(function (uploadedFiles) {
            _this._renderDialogUploader(uploadedFiles);
            $('#pic-lib-categories-select').wSelect2({
              data: selectedCategories,
              defaultBlank: true,
              valueField: 'pic-lib-categories-control',
              multiple: true,
              height: 300
            });
            _this._bindDialogEvents();
          });
        },
        buttons: {
          save: {
            label: '确定选择图片',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var newFiles = _this._getAllSelectedPictures();

              if (newFiles.length === 0) {
                return;
              }

              _this.getValue(function (uploadedFiles) {
                // 大小、规格、格式校验
                var checkErrors = _this.checkUploadedFiles(newFiles, uploadedFiles);

                // 数量校验
                if (_this.options.mutiselect) {
                  if (_this.options.fileMaxNum && +_this.options.fileMaxNum < newFiles.length + uploadedFiles.length) {
                    checkErrors.push('附件数量不能超过' + _this.options.fileMaxNum + '个');
                  }
                } else {
                  if (newFiles.length > 1) {
                    checkErrors.push('只能选择1张图片，操作成功后将自动覆盖原有图片');
                  } else {
                    uploadedFiles = [];
                  }
                }

                // 重名校验
                if (!_this.options.allowFileNameRepeat) {
                  var namesMap = {};
                  for (var i = 0; i < uploadedFiles.length; i++) {
                    var img = uploadedFiles[i];
                    namesMap[img.fileName] = true;
                  }
                  for (var i = 0; i < newFiles.length; i++) {
                    var img = newFiles[i];
                    if (namesMap[img.fileName]) {
                      if (checkErrors.indexOf('已存在文件名称为【' + img.fileName + '】的文件，不允许重复') <= -1) {
                        checkErrors.push('已存在文件名称为【' + img.fileName + '】的文件，不允许重复');
                      }
                    } else {
                      namesMap[img.fileName] = true;
                    }
                  }
                }

                if (checkErrors.length) {
                  appModal.error(checkErrors.join('; ') + '，请检查后重新操作');
                } else {
                  $.merge(uploadedFiles, newFiles);
                  if (!_this.fileuploadobj.multiple) {
                    _this.fileuploadobj.$attacheList.find('._delete_input').last().trigger('click'); // 删除最后一个文件
                  }
                  // 校验图片
                  _this.setValue(uploadedFiles);
                  $.each(newFiles, function (index) {
                    _this.addLog('file-addimage', null, this);
                  });

                  // 关闭弹窗
                  _this.$dialog.modal('hide');
                }
              });

              // 延迟关闭弹窗
              return false;
            }
          }
        }
      });
    },

    checkUploadedFiles: function (newFiles, uploadedFiles) {
      var _this = this;
      var options = this.options;

      checkErrors = [];
      dataList = [];

      // 大小校验
      if (options.fileMaxSize) {
        var maxSize = +options.fileMaxSize * 1024 * 1024;
        for (var i = 0; i < newFiles.length; i++) {
          var img = newFiles[i];
          if (img.fileSize > maxSize) {
            checkErrors.push('附件大小不能超过' + formatSize(maxSize));
            break;
          }
        }
      }

      // 规格校验
      if (options.pixelCheck) {
        var allowHeight = +options.imgHeight;
        var allowWidth = +options.imgWidth;
        for (var i = 0; i < newFiles.length; i++) {
          var img = newFiles[i];
          if (img.width !== allowWidth || img.height !== allowHeight) {
            checkErrors.push('像素必须满足' + allowWidth + 'px*' + allowHeight + 'px');
            break;
          }
        }
      }

      // 格式校验
      if (options.fileExt) {
        var allowExts = JSON.parse(options.fileExt);
        for (var i = 0; i < newFiles.length; i++) {
          var img = newFiles[i];
          var fileType = img.fileName.split('.')[img.fileName.split('.').length - 1];
          if (allowExts.indexOf(fileType) < 0) {
            checkErrors.push('只支持' + allowExts.join(', ') + '格式附件');
            break;
          }
        }
      }

      return checkErrors;
    },

    getDialogHtml: function () {
      return (
        '<div class="panel-body" style="padding: 0;">' +
        // 导航条
        '   <div class="panel-tab-header">' +
        '     <ul class="nav nav-tabs pull-left">' +
        '       <li class="panel-tab active"><a href="#pic-lib-panel">图片库</a></li>' +
        '       <li class="panel-tab"><a href="#pic-upload-panel">本地上传</a></li>' +
        '     </ul>' +
        '   </div>' +
        // 内容区域
        '   <div class="tab-content">' +
        // 图片库面板
        '     <div class="panel-tab-content tab-pane pic-lib-panel active" id="pic-lib-panel">' +
        // - 左侧分类选择器
        '       <div class="col-md-3 pic-category-col">' +
        '         <p class="pic-category-col-title mb-sm">图片库分类</p>' +
        '         <ul class="pic-category-col-list scrollbar"></ul>' +
        '       </div>' +
        // - 右侧图片选择器
        '       <div class="col-md-9 pic-selection-col">' +
        '         <div class="pic-selection-operation-row">' +
        '           <button type="button" class="well-btn w-btn-primary w-btn-minor pic-delete-btn"><i class="iconfont icon-ptkj-shanchu"></i>删除</button>' +
        '           <span class="pull-right" style="display: none;">' +
        '             <select class="inline" id="pic-file-type-selector">' +
        '               <option value ="allFiles">所有文件</option>' +
        '               <option value ="customFiles">自定义文件</option>' +
        '             </select>' +
        '             <i' +
        '               class="iconfont icon-ptkj-xinxiwenxintishi ml-md"' +
        '               style="color:#488cee; font-size: 18px"' +
        '               title="自定义文件：显示符合支持附件格式的文件\r所有文件：显示所有文件">' +
        '             </i>' +
        '           </span>' +
        '         </div>' +
        '         <div class="pic-selection-cards-row mt-lg scrollbar"></div>' +
        '       </div>' +
        '     </div>' +
        // 上传面板
        '     <div class="panel-tab-content tab-pane pic-upload-panel" id="pic-upload-panel">' +
        '       <div class="well-form form-horizontal">' +
        '         <div class="form-group">' +
        '           <label class="well-form-label control-label">保存图片库</label>' +
        '           <div class="well-form-control">' +
        '             <input type="checkbox" name="saveToPicLib" id="saveToPicLib" class="switch" checked />' +
        '             <label for="saveToPicLib" style="top: 4px;"></label>' +
        '             <span class="ml-md">开启时，图片会先保存至图片库，再上传</span>' +
        '           </div>' +
        '         </div>' +
        '         <div class="form-group" id="pic-lib-categories-row">' +
        '           <label class="well-form-label control-label required">选择图片库分类</label>' +
        '           <div class="well-form-control">' +
        '             <select id="pic-lib-categories-select"></select>' +
        '             <input type="hidden" name="pic-lib-categories" id="pic-lib-categories-control"></select>' +
        '           </div>' +
        '         </div>' +
        '         <div class="form-group">' +
        '           <label class="well-form-label control-label">上传图片</label>' +
        '           <div class="well-form-control">' +
        '             <p class="upload-tip" style="font-weight: 400; color: #999999; padding-top: 7px;"></p>' +
        '             <div id="pic-uploader" class="scrollbar"></div>' +
        '           </div>' +
        '         </div>' +
        '       </div>' +
        '       <div class="upload-btn-wrapper">' +
        '         <button type="button" class="well-btn w-btn-primary">确定上传</button>' +
        '       </div>' +
        '     </div>' +
        '   </div>' +
        '</div>'
      );
    }
  };

  $.FileUpload4Image = {
    // 设置可编辑 add by wujx 20160809
    setEditable: function () {
      $.wFileUploadMethod.setEditable.apply(this);

      // 显示文件列表
      //this.fileuploadobj.showFileNameList();
    },
    // 设置只读 add by wujx 20160809
    setReadOnly: function () {
      $.wFileUploadMethod.setReadOnly.apply(this);

      // 隐藏文件列表
      //this.fileuploadobj.hideFileNameList();
    }
  };
  /*
   * FILEUPLOAD4IMAGE PLUGIN DEFINITION =========================
   */
  $.fn.wfileUpload4Image = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this);
        var data = $this.data('wfileUpload4Image');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wfileUpload4Image'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new FileUpload4Image(this, options);

      var data1 = $.extend(data, $.wFileUploadMethod);
      $.extend(data1, $.FileUpload4Image);
      data1.init();
      $this.data('wfileUpload4Image', data1);
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wfileUpload4Image.Constructor = FileUpload4Image;

  $.fn.wfileUpload4Image.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    isHide: false, // 是否隐藏
    mainTableName: '',
    formSign: '',
    enableSignature: '',
    allowUpload: true, // 允许上传
    allowDownload: true, // 允许下载
    allowDelete: true, // 允许删除
    allowPreview: true, // 允许删除
    mutiselect: true, // 是否多选
    isAppend: false
    // true 追加值，false 不追加，采用覆盖 add by wujx 20160615
  };
})(jQuery);

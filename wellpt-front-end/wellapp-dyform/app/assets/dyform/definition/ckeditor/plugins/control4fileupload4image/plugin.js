var controlConfig = {};
$.extend(controlConfig, $.ControlConfigUtil);
$.extend(controlConfig, {
  initProperty: function (field) {
    var _self = this;

    if (field == null || typeof field == 'undefined') {
      field = new WFileUpload4IconClass();
      field.inputMode = '6';
      field.valueCreateMethod = '1';
    } else {
    }

    // 兼容旧数据
    if (!field.operationMode) {
      field.operationMode = '1';
    }

    if (!field.selectedCategories) {
      field.selectedCategories = [];
    }

    if (!field.hasOwnProperty('allowPreview')) {
      field.allowPreview = true;
    }

    _self.field = field;

    // 获得所有图片库可选分类数据, 并排除无效分类库
    _self.queryAllCategories(true);
    // $.ajax({
    //   type: 'GET',
    //   url: '/basicdata/img/category/queryAllCategory',
    //   dataType: 'json',
    //   async: true,
    //   success: function (result) {
    //     // 排除已经被删除的分类
    //     var selectableUuidMap = {};
    //     var selectedCategories = [];
    //     for (var i = 0; i < result.data.length; i++) {
    //       var item = result.data[i];
    //       selectableUuidMap[item.uuid] = true;
    //     }
    //     for (var i = 0; i < field.selectedCategories.length; i++) {
    //       var uuid = field.selectedCategories[i];
    //       if (selectableUuidMap[uuid]) {
    //         selectedCategories.push(uuid);
    //       }
    //     }
    //     field.selectedCategories = selectedCategories;

    //     // 设置数据
    //     _self.$('#selectCategories').data({ selectableCategories: result.data, selectedCategories: field.selectedCategories });
    //     _self.$('#selectedPicLibCategories').text(field.selectedCategories.length);
    //   }
    // });

    //公共属性
    this.ctlPropertyComInitSet(field);
    //私有属性设置
    var allowUpload = field.allowUpload;
    var allowDownload = field.allowDownload;
    var allowDelete = field.allowDelete;
    var allowPreview = field.allowPreview;
    // add by wujx 20160606 begin -->
    var allowFileNameRepeat = field.allowFileNameRepeat;
    // add by wujx 20160606 end -->
    if (allowUpload) {
      $("input[name='oppower'][value='1']").attr('checked', true);
    }
    if (allowDownload) {
      $("input[name='oppower'][value='2']").attr('checked', true);
    }
    if (allowDelete) {
      $("input[name='oppower'][value='3']").attr('checked', true);
    }
    if (allowPreview) {
      $("input[name='oppower'][value='4']").attr('checked', true);
    }

    this.$('#allowFileNameRepeat')
      .off()
      .on('click', function () {
        var status = $(this).find('.switch-radio').data('status');
        $(this)[status == '1' ? 'removeClass' : 'addClass']('active');
        $(this)
          .find('.switch-radio')
          .data('status', status == '1' ? '0' : '1');
      });

    if (allowFileNameRepeat == false) {
      this.$('#allowFileNameRepeat').trigger('click');
    }

    this.$('#fileExt').val(field.fileExt);
    this.$('#fileMaxSize').val(field.fileMaxSize);
    // this.$('#fileTips').val(field.fileTips);
    var re = /^[0-9]\d*$/;
    if (field.fileTips && field.fileTips != '') {
      var curPiexl = field.fileTips.split('*');
      var width = curPiexl[0];
      var height = curPiexl[1] ? curPiexl[1] : '';
      width = width != '' && width.indexOf('px') == width.length - 2 ? width.split('px')[0] : '';
      height = height != '' && height.indexOf('px') == height.length - 2 ? height.split('px')[0] : '';
      if (width != '' && re.test(width) && height != '' && re.test(height)) {
        this.$('#imgWidth').val(width);
        this.$('#imgHeight').val(height);
      }
    } else {
      this.$('#imgWidth').val(field.imgWidth || '');
      this.$('#imgHeight').val(field.imgHeight || '');
    }
    var mutiselect = field.mutiselect;

    this.$('#allowMulti')
      .off()
      .on('click', function () {
        var status = $(this).find('.switch-radio').data('status');
        $(this)[status == '1' ? 'removeClass' : 'addClass']('active');
        _self.$('.fileMaxNum')[status == '1' ? 'hide' : 'show']();

        $(this)
          .find('.switch-radio')
          .data('status', status == '1' ? '0' : '1');
      });

    if (mutiselect == false) {
      this.$('#allowMulti').trigger('click');
    }

    this.$("input[name='operationMode'][value='" + field.operationMode + "']").prop('checked', 'checked');

    this.$('#fileMaxNum')
      .val(field.fileMaxNum)
      .on('keyup', function () {
        var $this = $(this);
        var val = $this.val();
        if (val && !re.test(val)) {
          $this.val(val.substr(0, val.length - 1));
          appModal.error('允许上传附件数量为正整数');
        }
      });

    this.$('input[name=operationMode]')
      .off()
      .on('change', function () {
        var selectedMode = _self.$('input[name=operationMode]:checked').val();
        if (selectedMode === '1') {
          _self.$('#tr_picLibCategory').hide();
        } else {
          _self.$('#tr_picLibCategory').show();
        }
      })
      .trigger('change');

    this.$('#selectCategories')
      .off()
      .on('click', function () {
        _self.queryAllCategories(false);

        var selectableCategories = _self.$('#selectCategories').data('selectableCategories');
        var selectedCategories = _self.$('#selectCategories').data('selectedCategories');

        _self.openCategoriesDialog(selectableCategories, selectedCategories, function (uuids) {
          if (uuids === false) {
            return;
          }
          _self.field.selectedCategories = uuids;
          _self.$('#selectCategories').data('selectedCategories', uuids);
          _self.$('#selectedPicLibCategories').text(uuids.length);
        });
      });

    this.$('#pixelCheck')
      .off()
      .on('click', function () {
        var status = $(this).find('.switch-radio').data('status');
        $(this)[status == '1' ? 'removeClass' : 'addClass']('active');
        $(this)
          .find('.switch-radio')
          .data('status', status == '1' ? '0' : '1');
        _self
          .$('.tr_pixel')
          .find('.Label font')
          .css({
            display: status == '1' ? 'none' : 'inline'
          });
      });

    if (field.pixelCheck == '1') {
      this.$('#pixelCheck').trigger('click');
    }

    this.$('.show-type-help-popup').hide();
    this.$("input[name='showType']").each(function () {
      if ($(this).val() != '5') {
        $(this).hide();
        $(this).next().hide();
      } else {
        /*$(this).click(function(){

		 		console.log($(this).attr("checked"));
		 		if($(this).attr("checked") == "checked"){
		 			$(this).attr("checked", "") ;
		 		}else{
		 			$(this).attr("checked","checked")   ;
		 		}
		 	})*/
      }
    });
    var self = this;
    this.hasChacked = this.$("input[name='showType'][value='5']").prop('checked');
    this.$("input[name='showType'][value='5']").click(function () {
      self.hasChacked = !self.hasChacked;
      $(this).prop('checked', self.hasChacked);
    });

    this.setPictureLibLink();
  },

  setPictureLibLink: function () {
    var userId = this.getCookie('cookie.current.userId');
    var $link = this.$('#picLibLink');
    if (userId === 'U0000000000') {
      // 超管
      $link.attr(
        'href',
        '/web/app/pt-mgr.html?pageUuid=2f852b9e-4564-4f5b-bc7e-bb57f639cbe3#/wLeftSidebar_C8AB2993170000013026134FCB501A1B/C9390A3FC470000127BA28F5141D1CC9'
      );
    } else {
      // 单位管理员
      $link.attr(
        'href',
        '/web/app/pt-mgr.html?pageUuid=ac525dcd-50b7-42e9-95b7-658b117ac19b#/wLeftSidebar_97FEB03AFCBC4315A504847D9626E0F1/C93BACC754F00001305F9C101BB0E030'
      );
    }
  },

  getCookie: function (name) {
    var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
    var arr = document.cookie.match(reg);
    if (arr) {
      return unescape(arr[2]);
    }
    return null;
  },

  queryAllCategories: function (async) {
    var _self = this;
    $.ajax({
      type: 'GET',
      url: '/basicdata/img/category/queryAllCategory',
      dataType: 'json',
      async: async,
      success: function (result) {
        result.data.sort(function (a, b) {
          return a.code.localeCompare(b.code);
        });

        // 排除已经被删除的分类
        var selectableUuidMap = {};
        var selectedCategories = [];
        for (var i = 0; i < result.data.length; i++) {
          var item = result.data[i];
          selectableUuidMap[item.uuid] = true;
        }
        for (var i = 0; i < _self.field.selectedCategories.length; i++) {
          var uuid = _self.field.selectedCategories[i];
          if (selectableUuidMap[uuid]) {
            selectedCategories.push(uuid);
          }
        }
        _self.field.selectedCategories = selectedCategories;

        // 设置数据
        _self.$('#selectCategories').data({ selectableCategories: result.data, selectedCategories: _self.field.selectedCategories });
        _self.$('#selectedPicLibCategories').text(_self.field.selectedCategories.length);
      }
    });
  },

  openCategoriesDialog: function (selectableData, selectedData, onClosed) {
    var _self = this;

    var dialogContent = _self.getCategoriesDialog();

    var $dialog = appModal.dialog({
      title: '图片库分类',
      message: dialogContent,
      height: '600px',
      size: 'large',
      shown: function () {
        // 填充列表
        var $selectableUl = $('#transfer-selectable-area');
        var $selectedUl = $('#transfer-selected-area');

        for (var i = 0; i < selectableData.length; i++) {
          var item = selectableData[i];
          var isItemSelected = selectedData.some(function (uuid) {
            return item.uuid === uuid;
          });

          var title = item.name + (item.description ? '\r' + item.description : '');
          $selectableLi = $('<li></li>').attr('title', title).attr('uuid', item.uuid).data(item);

          if (item.icon) {
            var $spanIcon = $('<span class="msgIconShow inline-block ml-sm mr-sm"></span>');
          } else {
            var $spanIcon = $('<span class="msgIconShow inline-block ml-sm mr-sm">&nbsp;</span>');
          }
          var $icon = $spanIcon.addClass(item.icon).css({ background: item.color, display: 'inline-block' });

          $('<label class="item-label"></label>')
            .append('<input type="checkbox" class="selectable-checkbox"' + ' id="' + item.uuid + '"/>')
            .append('<label for="' + item.uuid + '">' + '</label>')
            .append($icon)
            .append('<span>' + item.name + '</span>')
            .appendTo($selectableLi);

          $selectableUl.append($selectableLi);

          if (isItemSelected) {
            $selectableLi.find('.selectable-checkbox').prop('checked', true);
            var $selectedLi = $('<li></li>').attr('title', title).attr('uuid', item.uuid);

            $selectedLi
              .append('<span class="item-label">' + $icon.prop('outerHTML') + item.name + '</span>')
              .append('<span class="delete-btn">X</span>')
              .data(item);
            $selectedUl.append($selectedLi);
          }
        }

        // 绑定事件
        // - 搜索框 `Enter` 键
        $('#transfer-search-input')
          .off('keyup')
          .on('keyup', function (e) {
            if (e.keyCode === 13) {
              $('#transfer-search-btn').trigger('click');
            }
          });

        // - 搜索框按钮点击事件
        $('#transfer-search-btn')
          .off('click')
          .on('click', function () {
            var keyword = $('#transfer-search-input').val().toLowerCase().trim();
            var visibleItemsCount = 0;
            if (keyword === '') {
              $selectableUl.find('li').show();
              visibleItemsCount = $selectableUl.find('li').length;
            } else {
              $.each($selectableUl.find('li'), function (idx, li) {
                var $li = $(li);
                var item = $li.data();
                if (item.name.includes(keyword) || (item.description || '').includes(keyword)) {
                  $li.show();
                  visibleItemsCount++;
                } else {
                  $li.hide();
                }
              });
            }

            if (visibleItemsCount) {
              $('#transfer-selectable-area-placeholder').hide();
            } else {
              $('#transfer-selectable-area-placeholder').show();
            }
          })
          .trigger('click');

        // 重设 `title`
        $selectableUl
          .add($selectedUl)
          .off('hover')
          .on('hover', 'li:not(".title-reset")', function () {
            var $li = $(this).addClass('title-reset');
            var item = $li.data();
            var $label = $li.find('.item-label');

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

            $li.attr('title', title || '');
          });

        // - 可选面板内的复选框点击事件
        $selectableUl.off('click').on('click', 'li .selectable-checkbox', function (e) {
          var $this = $(this);
          var checked = $this.attr('checked');
          var item = $this.closest('li').data();

          if (checked) {
            if ($selectedUl.find('li[uuid="' + item.uuid + '"]').length) {
              return;
            }

            var title = item.name + (item.description ? '\r' + item.description : '');
            var $selectedLi = $('<li></li>').attr('title', title).attr('uuid', item.uuid);
            var $icon = $('<span class="msgIconShow inline-block ml-sm mr-sm">&nbsp;</span>')
              .addClass(item.icon)
              .css({ background: item.color, display: 'inline-block' });
            $selectedLi
              .append('<span class="item-label">' + $icon.prop('outerHTML') + item.name + '</span>')
              .append('<span class="delete-btn">X</span>')
              .data(item);
            $selectedUl.append($selectedLi);
          } else {
            $('li[uuid="' + item.uuid + '"]', $selectedUl).remove();
          }
        });

        // - 已选面板内的移除按钮点击事件
        $selectedUl.off('click').on('click', 'li .delete-btn', function (e) {
          var $this = $(this);
          var uuid = $this.closest('li').attr('uuid');
          $('li[uuid="' + uuid + '"] input', $selectableUl).removeAttr('checked');
          $('li[uuid="' + uuid + '"]', $selectedUl).remove();
        });
      },
      buttons: {
        save: {
          label: '确定',
          className: 'well-btn w-btn-primary',
          callback: function () {
            var uuids = [];
            $.each($('#transfer-selected-area li'), function (i, li) {
              uuids.push($(li).attr('uuid'));
            });

            onClosed(uuids);
          }
        },
        close: {
          label: '关闭',
          className: 'btn-default',
          callback: function () {
            onClosed(false);
          }
        }
      }
    });
  },

  getCategoriesDialog: function () {
    return (
      '<div class="w-transfer" >' +
      '   <div class="col-md-6" style="border-right: 1px solid #E8E8E8">' +
      '       <p>选择分类：</p>' +
      '       <p>' +
      '           <input id="transfer-search-input" type="text" placeholder="关键字" style="width: 200px;">' +
      '           <button id="transfer-search-btn" type="button" class="well-btn w-btn-primary">查询</button>' +
      '       </p>' +
      '       <ul id="transfer-selectable-area">' +
      '           <div id="transfer-selectable-area-placeholder" style="display: none;">' +
      '               <div class="well-table-no-data"></div>' +
      '               <div style="text-align: center">没有找到匹配的记录</div>' +
      '           </div>' +
      '       </ul>' +
      '   </div>' +
      '   <div class="col-md-6" >' +
      '       <p>已选分类：</p>' +
      '       <ul id="transfer-selected-area"></ul>' +
      '   </div>' +
      '</div>'
    );
  },

  collectFormAndFillCkeditor: function () {
    var field = new WFileUpload4IconClass();
    //added by linxr
    field.noNullValidateReminder = $('#noNullValidateReminder').val();
    //控件公共属性收集
    var checkpass = this.collectFormCtlComProperty(field);
    if (!checkpass) {
      return false;
    }
    //特殊属性收集
    field.inputMode = dyFormInputMode.accessoryImg;
    // field.fieldCheckRules.push({ value: field.contentFormat, label: $('#contentFormat').find('option:selected').text() });
    $("input[name='oppower']:checked").each(function () {
      if (this.checked) {
        if ($(this).val() == '1') {
          field.allowUpload = true;
        } else if ($(this).val() == '2') {
          field.allowDownload = true;
        } else if ($(this).val() == '3') {
          field.allowDelete = true;
        } else if ($(this).val() == '4') {
          field.allowPreview = true;
        }
      }
    });

    $("input[name='oppower']:not(:checked)").each(function () {
      if ($(this).val() == '1') {
        field.allowUpload = false;
      } else if ($(this).val() == '2') {
        field.allowDownload = false;
      } else if ($(this).val() == '3') {
        field.allowDelete = false;
      } else if ($(this).val() == '4') {
        field.allowPreview = false;
      }
    });

    field.allowFileNameRepeat = $('#allowFileNameRepeat').find('.switch-radio').data('status') == '0' ? false : true;
    field.operationMode = this.$('input[name=operationMode]:checked').val();
    field.selectedCategories = this.$('#selectCategories').data('selectedCategories');
    if (field.operationMode === '2' || field.operationMode === '3') {
      if (!field.selectedCategories || field.selectedCategories.length === 0) {
        appModal.error('图片库分类不能为空，请选择！');
        return false;
      }
    }

    field.fileExt = this.$('#fileExt').val();
    field.fileMaxSize = this.$('#fileMaxSize').val();
    // field.fileTips = this.$('#fileTips').val();
    field.mutiselect = this.$('#allowMulti').find('.switch-radio').data('status') == '1' ? ' true' : false;
    if (field.mutiselect) {
      field.fileMaxNum = this.$('#fileMaxNum').val();
    } else {
      delete field.fileMaxNum;
    }
    field.pixelCheck = this.$('#pixelCheck').find('.switch-radio').data('status');
    field.imgWidth = this.$('#imgWidth').val();
    field.imgHeight = this.$('#imgHeight').val();

    var res = /^[0-9]+$/;
    if (field.imgWidth == '' || field.imgHeight == '') {
      if (field.pixelCheck == '1') {
        appModal.error('像素要求必填！');
        return false;
      }
    } else if (!res.test(field.imgWidth) || !res.test(field.imgHeight) || field.imgWidth == 0 || field.imgHeight == 0) {
      appModal.error('像素要求只能输入正整数！');
      return false;
    }

    //创建控件占位符
    this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

    formDefinition.addField(field.name, field);

    return true;
  }
});

controlConfig.pluginName = CkPlugin.FILEUPLOAD4IMAGECTL;
addPlugin(controlConfig.pluginName, '图片附件控件', '图片附件控件属性设置', controlConfig);

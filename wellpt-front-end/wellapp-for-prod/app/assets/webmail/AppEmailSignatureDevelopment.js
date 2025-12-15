define(['jquery', 'commons', 'constant', 'server', 'ViewDevelopmentBase', 'appModal', 'ckeditor'], function (
  $,
  commons,
  constant,
  server,
  ViewDevelopmentBase,
  appModal,
  ckeditor
) {
  var AppEmailSignatureDevelopment = function () {
    ViewDevelopmentBase.apply(this, arguments);
  };

  commons.inherit(AppEmailSignatureDevelopment, ViewDevelopmentBase, {
    init: function () {
      var _self = this;
      _self.ckeditorFixed();
    },

    /*ckeditor在bootstrap模态窗口上时候输入框等失效问题解决*/
    ckeditorFixed: function (focus) {
      $.fn.modal.Constructor.prototype.enforceFocus = function () {};
    },

    afterRender: function (options, configuration) {
      var _self = this;
      _self.loadCssLink();
      _self.bindEvent();
    },

    loadCssLink: function () {
      // $("head").append(
      //     $("<link>", { "href": staticPrefix + "/js/pt/css/webmail/wm_webmail_v2.css", "rel": "stylesheet" })
      // );
    },

    bindEvent: function () {
      var _self = this;
      var $tableElement = _self.widget.$tableElement;
      //表格渲染数据完毕后的事件处理
      $tableElement.on('post-body.bs.table', function (event, data) {
        var datas = _self.getData();
        if (datas && datas.length > 0) {
          //1.第一行如果是默认的签名，则“设为默认”按钮名称要变为“取消默认”
          if (datas[0].IS_DEFAULT) {
            _self.widget.$tableElement.find('tr[data-index=0] .btn_class_setThisAsDefault').text('取消默认');
            _self.widget.$tableElement
              .find('tr[data-index=0] td:eq(1)')
              .append($('<span>', { class: 'glyphicon glyphicon-ok-sign', style: 'color:#0fbb0f;', title: '默认签名' }));
          }

          //2.渲染签名内容为html
          for (var i = 0, len = datas.length; i < len; i++) {
            _self.widget.$tableElement
              .find('tr[data-index=' + i + '] td:eq(3)')
              .html('<div class="sign_content_div">' + datas[i].SIGNATURE_CONTENT + '</div>');
          }
        }
      });
    },

    /**
     * 新建文件夹
     */
    addSignature: function () {
      var _self = this;
      _self.popDialog();
    },

    /**
     * 弹窗展示
     */
    popDialog: function (signature, dialogOpt, successCallback) {
      var _self = this;
      var isAdd = !signature;
      var getSignatureData = function (signature) {
        var signName = $.trim($('#signatureName').val());
        if (!signature && signName.length == 0) {
          //新建时候需要判断
          appModal.alert('请输入标题');
          return;
        }
        if (signName.length > 30) {
          appModal.alert('标题不要超过30个中文字符');
          return;
        }
        if (!signature) {
          signature = {};
        }
        signature.signatureName = signName;
        signature.signatureContent = CKEDITOR.instances.signatureContent.getData();
        if ($.trim(signature.signatureContent).length == 0) {
          appModal.alert('无法保存空内容的签名');
          return;
        }
        signature.isDefault = $('#isDefault').prop('checked') ? 1 : 0;

        return signature;
      };
      var options = {
        title: dialogOpt && dialogOpt.title ? dialogOpt.title : !isAdd ? '编辑签名' : '新建文本签名',
        message: _self.signDialogHtml(signature ? signature : null),
        buttons: {
          confirm: {
            label: '保存',
            className: 'btn-primary jsBtnSaveSign',
            callback: function (result) {
              var commitResult = false;
              var signData = getSignatureData(signature);
              if (!signData) {
                return false;
              }
              var success = _self.saveSignature(signData);
              if (success) {
                if ($.isFunction(successCallback)) {
                  successCallback(result.data);
                } else {
                  appModal.success(!isAdd ? '编辑成功' : '新建成功');
                  _self.refresh();
                }
              }
              return success;
            }
          },

          saveAndDefault: {
            label: '保存并设为默认',
            className: 'btn-default btn_width_auto',
            callback: function (result) {
              $('#isDefault').prop('checked', true);
              $('.jsBtnSaveSign').trigger('click');
              return false;
            }
          },

          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        size: 'middle'
      };
      appModal.dialog(options);
      _self.initCkeditorPlugin(signature ? signature.signatureContent : null);
    },

    initCkeditorPlugin: function (content) {
      CKEDITOR.replace('signatureContent', {
        allowedContent: true,
        enterMode: CKEDITOR.ENTER_P,

        //工具栏
        toolbar: [
          [
            'Bold',
            'Italic',
            'Underline',
            'Font',
            'FontSize',
            'TextColor',
            'BGColor',
            'JustifyLeft',
            'JustifyCenter',
            'JustifyRight',
            'NumberedList',
            'BulletedList',
            'Link',
            'Image',
            'Maximize',
            'Preview'
          ]
        ],

        on: {
          paste: function (evt) {
            handleCkeditorPaste(evt);
          },
          instanceReady: function (ev) {
            this.dataProcessor.writer.setRules('p', {
              indent: true,
              breakBeforeOpen: false,
              breakAfterOpen: false,
              breakBeforeClose: false,
              breakAfterClose: false
            });
          },
          loaded: function (ev) {}
        }
      });
    },

    saveSignature: function (commitData) {
      var commitResult = false;
      var _self = this;
      server.JDS.call({
        service: 'wmMailSignatureFacadeService.' + (commitData.uuid ? 'updateSignature' : 'addSinagure'),
        data: [commitData],
        success: function (result) {
          commitResult = true;
        },
        error: function (jqXHR) {
          var response = JSON.parse(jqXHR.responseText);
          _self.showExceptionMsg({ msg: response.msg, signatureName: commitData.signatureName });
        },
        async: false
      });
      return commitResult;
    },

    /*编辑签名*/
    editSignature: function (event, options, rowData) {
      var _self = this;
      _self.popDialog({
        signatureName: rowData.SIGNATURE_NAME,
        uuid: rowData.UUID,
        signatureContent: rowData.SIGNATURE_CONTENT,
        isDefault: rowData.IS_DEFAULT == 1
      });
    },

    /**
     * 删除签名
     */
    deleteSignature: function (event, options, rowData) {
      var _self = this;
      appModal.confirm('是否确定删除签名', function (result) {
        if (result) {
          _self.deleteByUuids([rowData.UUID]);
        }
      });
    },

    /*批量删除*/
    batchDeleteSignature: function () {
      var _self = this;
      var ids = _self.getSelectedRowIds();
      if (ids.length == 0) {
        appModal.alert('请勾选需要删除的签名');
        return;
      }
      _self.deleteByUuids(ids);
    },

    /*设置为默认签名*/
    setThisAsDefault: function (event, options, rowData) {
      var _self = this;
      server.JDS.call({
        service: 'wmMailSignatureFacadeService.updateSignatureDefault',
        data: [rowData.UUID, !rowData.IS_DEFAULT],
        success: function (result) {
          appModal.success(!rowData.IS_DEFAULT ? '默认签名设置成功' : '取消默认签名成功');
          _self.refresh();
        },
        error: function (jqXHR) {}
      });
    },

    deleteByUuids: function (ids) {
      var _self = this;
      server.JDS.call({
        service: 'wmMailSignatureFacadeService.deleteSignatures',
        data: [ids],
        success: function (result) {
          appModal.success('删除成功');
          _self.refresh();
        },
        error: function (jqXHR) {}
      });
    },

    signDialogHtml: function (sign) {
      var $table = $('<table>', {
        class: 'table table-hover table-striped'
      });
      var $titleTr = $('<tr>').append(
        $('<td>', { style: 'width:60px;' }).html('标题'),
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'signatureName',
            style: 'width:100%;',
            maxlength: 30,
            placeholder: sign ? sign.signatureName : ''
          })
        )
      );
      var $contentTr = $('<tr>').append(
        $('<td>'),
        $('<td>').append(
          $('<textarea>', { id: 'signatureContent', name: 'signatureContent', style: 'width:100%;min-height:300px' }).text(
            sign ? sign.signatureContent : ''
          )
        )
      );
      var $hideTr = $('<tr>', { class: 'hide' }).append(
        $('<td>').append($('<input>', { type: 'checkbox', id: 'isDefault', checked: sign ? sign.isDefault : false }))
      );
      $table.append($titleTr, $contentTr, $hideTr);
      return $table[0].outerHTML;
    },

    showExceptionMsg: function (args) {
      if (args.msg.indexOf('SYS_UNIQ00171224') != -1) {
        //名称唯一性（用户数据内）
        args.msg = '当前用户已经存在签名[' + args.signatureName + ']';
      }
      appModal.error(args.msg);
    },

    //获取选中的行数据uuid
    getSelectedRowIds: function () {
      var selections = this.getSelections();
      var mailboxUuids = [];
      if (selections != null && selections.length > 0) {
        for (var i = 0; i < selections.length; i++) {
          mailboxUuids.push(selections[i]['UUID']);
        }
      }
      return mailboxUuids;
    }
  });

  return AppEmailSignatureDevelopment;
});

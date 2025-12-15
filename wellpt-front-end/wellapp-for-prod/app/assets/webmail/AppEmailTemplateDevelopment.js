define(['jquery', 'commons', 'constant', 'server', 'ListViewWidgetDevelopment', 'appModal', 'ckeditor'], function (
  $,
  commons,
  constant,
  server,
  ListViewWidgetDevelopment,
  appModal,
  ckeditor
) {
  var AppEmailTemplateDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppEmailTemplateDevelopment, ListViewWidgetDevelopment, {
    init: function () {
      var _self = this;
      _self.ckeditorFixed();
    },

    /*ckeditor在bootstrap模态窗口上时候输入框等失效问题解决*/
    ckeditorFixed: function () {
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
          //1.第一行如果是默认的，则“设为默认”按钮名称要变为“取消默认”
          if (datas[0].IS_DEFAULT) {
            _self.widget.$tableElement.find('tr[data-index=0] .btn_class_setThisAsDefault').text('取消默认');
            _self.widget.$tableElement
              .find('tr[data-index=0] td:eq(1)')
              .append($('<span>', { class: 'glyphicon glyphicon-ok-sign', style: 'color:#0fbb0f;', title: '默认模板' }));
          }

          //2.渲染内容为html
          for (var i = 0, len = datas.length; i < len; i++) {
            //_self.widget.$tableElement.find('tr[data-index='+i+'] td:eq(3)').html('<div class="sign_content_div">'+datas[i].SIGNATURE_CONTENT+'</div>');
          }
        }
      });
    },

    /**
     * 新建模板
     */
    addMailTemplate: function () {
      var _self = this;
      _self.popDialog();
    },

    /**
     * 弹窗展示
     */
    popDialog: function (template, dialogOpt, successCallback) {
      var _self = this;
      var isAdd = !template;
      var getTemplateData = function (templateData) {
        var templateName = $.trim($('#templateName').val());
        if (!templateData && templateName.length == 0) {
          //新建时候需要判断
          appModal.alert('请输入标题');
          return;
        }
        if (templateName.length > 30) {
          appModal.alert('标题不要超过30个中文字符');
          return;
        }
        if (!template) {
          template = {};
        }
        template.templateName = templateName;
        template.templateContent = CKEDITOR.instances.mailTemplateContent.getData();
        if ($.trim(template.templateContent).length == 0) {
          appModal.alert('无法保存空内容的模板');
          return;
        }
        template.isDefault = $('#isDefault').prop('checked') ? 1 : 0;

        return template;
      };
      var options = {
        title: dialogOpt && dialogOpt.title ? dialogOpt.title : !isAdd ? '编辑写信模板' : '新建写信模板',
        message: _self.templateDialogHtml(template ? template : null),
        buttons: {
          confirm: {
            label: '保存',
            className: 'btn-primary jsBtnSaveTemplate',
            callback: function (result) {
              var commitResult = false;
              var templateData = getTemplateData(template);
              if (!templateData) {
                $('#isDefault').prop('checked', false);
                return false;
              }
              var success = _self.saveMailTemplate(templateData);
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
              $('.jsBtnSaveTemplate').trigger('click');
              return false;
            }
          },

          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        size: 'large'
      };
      appModal.dialog(options);
      _self.initCkeditorPlugin(template ? template.templateContent : null);
    },

    initCkeditorPlugin: function (content) {
      CKEDITOR.replace('mailTemplateContent', {
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

    saveMailTemplate: function (commitData) {
      var commitResult = false;
      var _self = this;
      server.JDS.call({
        service: 'wmMailTemplateFacadeService.' + (commitData.uuid ? 'updateMailTemplate' : 'addMailTemplate'),
        data: [commitData],
        success: function (result) {
          commitResult = true;
        },
        error: function (jqXHR) {
          var response = JSON.parse(jqXHR.responseText);
          _self.showExceptionMsg({ msg: response.msg, templateName: commitData.templateName });
        },
        async: false
      });
      return commitResult;
    },

    /*编辑写信模板*/
    enditMailTemplate: function (event, options, rowData) {
      var _self = this;
      _self.popDialog({
        templateName: rowData.TEMPLATE_NAME,
        uuid: rowData.UUID,
        templateContent: rowData.TEMPLATE_CONTENT,
        isDefault: rowData.IS_DEFAULT == 1
      });
    },

    /**
     * 删除签名
     */
    deleteMailTemplate: function (event, options, rowData) {
      var _self = this;
      appModal.confirm('是否确定删除写信模板', function (result) {
        if (result) {
          _self.deleteByUuids([rowData.UUID]);
        }
      });
    },

    /*批量删除*/
    batchDeleteTemplate: function () {
      var _self = this;
      var ids = _self.getSelectedRowIds();
      if (ids.length == 0) {
        appModal.alert('请勾选需要删除的写信模板');
        return;
      }
      _self.deleteByUuids(ids);
    },

    /*设置为默认签名*/
    setThisAsDefault: function (event, options, rowData) {
      var _self = this;
      server.JDS.call({
        service: 'wmMailTemplateFacadeService.updateMailTemplateDefaultStatus',
        data: [!rowData.IS_DEFAULT, rowData.UUID],
        success: function (result) {
          appModal.success(!rowData.IS_DEFAULT ? '默认写信模板设置成功' : '取消默认写信模板成功');
          _self.refresh();
        },
        error: function (jqXHR) {}
      });
    },

    deleteByUuids: function (ids) {
      var _self = this;
      server.JDS.call({
        service: 'wmMailTemplateFacadeService.deleteMailTemplatesByUuids',
        data: [ids],
        success: function (result) {
          appModal.success('删除成功');
          _self.refresh();
        },
        error: function (jqXHR) {}
      });
    },

    templateDialogHtml: function (template) {
      var $table = $('<table>', {
        class: 'table table-hover table-striped template-table'
      });
      var $titleTr = $('<tr>').append(
        $('<td>', { style: 'width:120px;' }).html('标题'),
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'templateName',
            style: 'width:100%;',
            maxlength: 30,
            placeholder: template ? template.templateName : ''
          })
        )
      );
      var $contentTr = $('<tr>').append(
        $('<td>'),
        $('<td>').append(
          $('<textarea>', { id: 'mailTemplateContent', name: 'mailTemplateContent', style: 'width:100%;min-height:300px' }).text(
            template ? template.templateContent : ''
          )
        )
      );
      var $hideTr = $('<tr>', { class: 'hide' }).append(
        $('<td>').append($('<input>', { type: 'checkbox', id: 'isDefault', checked: template ? template.isDefault : false }))
      );
      var $describeTr = $('<tr>').append(
        $('<td>').text('设计模板说明'),
        $('<td>').text(
          '${发件人}或${FROM}，${内容}或${BODY}，${部门}或${DEPT}，${岗位}或${DUTY}，${年}或${YEAR}，${月}或${MONTH}，${日}或${DAY}，${时}或${HOUR}，${分}或${MINUTE}，${秒}或${SECOND}，${个人签名.签名标题}或${SIGN.TITLE}'
        )
      );
      $table.append($titleTr, $contentTr, $describeTr, $hideTr);
      return $table[0].outerHTML;
    },

    showExceptionMsg: function (args) {
      if (args.msg.indexOf('SYS_UNIQ00171402') != -1) {
        //名称唯一性（用户数据内）
        args.msg = '当前用户已经存在签名[' + args.templateName + ']';
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

  return AppEmailTemplateDevelopment;
});

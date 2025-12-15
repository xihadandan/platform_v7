define(['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal', 'DmsDyformActionBase', 'wFileUpload'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal,
  DmsDyformActionBase,
  wFileUpload
) {
  var DmsDocExchangerReceiverOperateAction = function () {
    DmsDyformActionBase.apply(this, arguments);
  };

  var NOTIFY_WAY_NAME = {
    SMS: '短信',
    IM: '在线消息',
    MAIL: '邮件'
  };

  commons.inherit(DmsDocExchangerReceiverOperateAction, DmsDyformActionBase, {
    /**
     * 退回
     * @param options
     */
    btn_return_doc_exchanger: function (options) {
      var _self = this;
      var ui = options.ui;

      // 选中数据检验
      var selection = ui.getSelections();
      var actionFunction = options.appFunction;
      var promptMsg = actionFunction.promptMsg;
      if (selection.length === 0 && !promptMsg) {
        appModal.error(promptMsg);
        return;
      } else if (selection.length === 0) {
        ui.refresh(false);
        return;
      }
      options.data = [];

      //this.dmsDataServices.performed(options);
    },

    /**
     * 反馈意见
     * @param options
     */
    btn_feedback_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_feedback_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      var docExchangeConfig = options.data.extras.docExchangeConfiguration;
      var fileupload = new WellFileUpload('feedbackFile');
      //弹窗初始化
      var $dialog = this.popDialog(
        this.createFeedbackHtml(docExchangeConfig, docExchangeRecord),
        '反馈',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              if (!$('#feedbackContent')) {
                appModal.info('请输入反馈内容');
                return false;
              }
              options.data.docExcRecordUuid = docExchangeRecord.uuid;
              options.data.feedbackData = {
                content: $('#feedbackContent').val(),
                fileUuids: '',
                fileNames: ''
              };
              for (var i = 0, len = fileupload.files.length; i < len; i++) {
                options.data.feedbackData.fileUuids += fileupload.files[i].fileID;
                options.data.feedbackData.fileNames += fileupload.files[i].fileName;
                if (i != len - 1) {
                  options.data.feedbackData.fileUuids += '/';
                  options.data.feedbackData.fileNames += '/';
                }
              }
              options.success = function (result) {
                if (result.success) {
                  $dialog.find('.bootbox-close-button').trigger('click');
                }
                _self.dmsDataServices.onPerformedResult(result, options);
              };
              options.failure = function () {
                appModal.hideMask($dialog);
              };
              appModal.showMask('反馈处理中，请稍后...', $dialog);
              _self.dmsDataServices.performed(options);
              return false;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        },
        function () {
          fileupload.init(false, $('#fileUploadDiv'), false, true, []);
        }
      );
    },

    /**
     * 转发
     * @param options
     */
    btn_forward_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_forward_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      var docExchangeConfig = options.ui.configDto;
      var fileupload = new WellFileUpload('forwardFile');
      //弹窗初始化
      var $dialog = this.popDialog(
        this.createForwardHtml(docExchangeConfig, docExchangeRecord),
        '转发',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              if (!$('#forwardUserIds')) {
                appModal.info('请选择转发人员');
                return false;
              }
              var files = fileupload.files;
              options.data.docExcRecordUuid = docExchangeRecord.uuid;
              options.data.forwardData = {
                content: $('#forwardContent').val(),
                fileUuids: '',
                fileNames: '',
                toUserData: [
                  {
                    toUserId: $('#forwardUserIds').val(),
                    toUserName: $('#forwardUserNames').val(),
                    notifyWays: []
                  }
                ]
              };
              for (var i = 0, len = fileupload.files.length; i < len; i++) {
                options.data.forwardData.fileUuids += fileupload.files[i].fileID;
                options.data.forwardData.fileNames += fileupload.files[i].fileName;
                if (i != len - 1) {
                  options.data.forwardData.fileUuids += '/';
                  options.data.forwardData.fileNames += '/';
                }
              }

              $("input[name='forwardNw']:checked").each(function (i) {
                options.data.forwardData.toUserData[0].notifyWays.push($(this).val());
              });
              if ($('#forwardSignTimeLimit').val()) {
                options.data.forwardData.toUserData[0].signTimeLimit = $('#forwardSignTimeLimit').val() + ':00';
              }
              if ($('#forwardFeedbackTimeLimit').val()) {
                options.data.forwardData.toUserData[0].feedbackTimeLimit = $('#forwardFeedbackTimeLimit').val() + ':00';
              }
              options.success = function (result) {
                if (result.success) {
                  $dialog.find('.bootbox-close-button').trigger('click');
                }
                _self.dmsDataServices.onPerformedResult(result, options);
              };
              options.failure = function () {
                appModal.hideMask($dialog);
              };
              appModal.showMask('转发处理中，请稍后...', $dialog);
              _self.dmsDataServices.performed(options);
              return false;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        },
        function (docExchangeConfig) {
          fileupload.init(false, $('#fileUploadDiv'), false, true, []);
          $('#forwardSignTimeLimit,#forwardFeedbackTimeLimit').datetimepicker({
            showClose: true,
            showClear: true,
            showTodayButton: true,
            format: 'YYYY-MM-DD HH:mm', // 日期格式
            locale: 'zh-cn' // 本地化
          });

          $('#forwardUserNames').on('click', function () {
            if (docExchangeConfig.businessCategoryUuid) {
              $.unit2.open({
                targetWindow: window,
                valueField: 'forwardUserIds',
                labelField: 'forwardUserNames',
                title: '选择用户',
                type: 'MyUnit;MyDept;MyLeader;MyUnderling;MyCompany;BusinessBook',
                defaultType: 'BusinessBook',
                multiple: true,
                selectTypes: 'O;B;D;J;U;G;DU;E',
                valueFormat: 'justId',
                otherParams: {
                  categoryId: docExchangeConfig.businessCategoryUuid,
                  showOrgUser: false
                }
              });
            } else {
              $.unit2.open({
                targetWindow: window,
                valueField: 'forwardUserIds',
                labelField: 'forwardUserNames',
                title: '选择用户',
                type: 'MyUnit;MyDept;MyLeader;MyUnderling;MyCompany;BusinessBook',
                defaultType: 'MyUnit',
                multiple: true,
                selectTypes: 'O;B;D;J;U;G;DU;E',
                valueFormat: 'justId',
                otherParams: {
                  categoryId: docExchangeConfig.businessCategoryUuid,
                  showOrgUser: false
                }
              });
            }
          });
        }
      );

      return false;
    },

    //生成转发的HTML
    createForwardHtml: function (configuration, data) {
      var $form = $('<form>', {
        id: 'forwardContainer',
        class: 'dyform'
      });
      var $table = $('<table>', {
        id: 'forwardTable',
        class: 'table'
      });

      $table.append(
        $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).text('转发人员'),
          $('<td>', {
            colspan: '3'
          }).append(
            $('<input>', {
              class: 'editableClass',
              id: 'forwardUserNames',
              type: 'text',
              readonly: ''
            }),
            $('<input>', {
              id: 'forwardUserIds',
              type: 'hidden'
            })
          )
        ),
        $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).text('附件'),
          $('<td>', {
            colspan: '3'
          }).append(
            $('<div>', {
              id: 'fileUploadDiv'
            }),
            $('<input>', {
              id: 'fileUuid',
              type: 'hidden'
            })
          )
        ),
        $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).text('转发意见'),
          $('<td>', {
            colspan: '3'
          }).append(
            $('<textarea>', {
              id: 'forwardContent',
              style: 'resize:vertical;height:100px;width:100%;',
              maxlength: 150,
              class: 'editableClass',
              placeholder: '请输入转发意见'
            })
          )
        )
      );
      var $tds = [];
      if (data && data.isNeedSign) {
        $tds.push([
          $('<td>', {
            class: 'label-td'
          }).text('签收时限'),
          $('<td>').append(
            $('<input>', {
              type: 'text',
              id: 'forwardSignTimeLimit'
            })
          )
        ]);
      }
      if (data && data.isNeedFeedback) {
        $tds.push([
          $('<td>', {
            class: 'label-td'
          }).text('反馈时限'),
          $('<td>').append(
            $('<input>', {
              type: 'text',
              id: 'forwardFeedbackTimeLimit'
            })
          )
        ]);
      }
      var notifyTypes = [];
      var types = configuration.notifyTypes.split(';');
      for (var n = 0; n < types.length; n++) {
        notifyTypes.push(
          $('<input>', {
            type: 'checkbox',
            name: 'forwardNw',
            value: types[n],
            id: 'forwardnw' + n
          })
        );
        notifyTypes.push(
          $('<label>', {
            for: 'forwardnw' + n
          }).text(NOTIFY_WAY_NAME[types[n]])
        );
      }
      $tds.push([
        $('<td>', {
          class: 'label-td'
        }).text('提醒方式'),
        $('<td>').append(
          $('<span>', {
            class: 'editableClass forwardNotifyWays'
          }).append(notifyTypes)
        )
      ]);

      var $tr;
      for (var i = 0; i < $tds.length; i++) {
        if (i % 2 == 0) {
          $tr = $('<tr>');
          $table.append($tr);
        }
        $tr.append($tds[i]);
      }

      if ($tds.length % 2 != 0) {
        $tds[$tds.length - 1][1].attr('colspan', 3);
      }

      return $form.append($table)[0].outerHTML;
    },

    //生成反馈的HTML
    createFeedbackHtml: function (configuration, data) {
      var $form = $('<form>', {
        id: 'feedbackContainer',
        class: 'dyform'
      });
      var $table = $('<table>', {
        id: 'feedbackTable',
        class: 'table'
      });

      $table.append(
        $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).text('反馈内容'),
          $('<td>').append(
            $('<textarea>', {
              id: 'feedbackContent',
              style: 'resize:vertical;height:100px;width:100%;',
              maxlength: 150,
              class: 'editableClass',
              placeholder: '请输入反馈内容'
            })
          )
        ),
        $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).text('附件'),
          $('<td>').append(
            $('<div>', {
              id: 'fileUploadDiv'
            }),
            $('<input>', {
              id: 'fileUuid',
              type: 'hidden'
            })
          )
        )
      );

      return $form.append($table)[0].outerHTML;
    },

    /**
     * 弹窗
     * @param html  弹窗内容html
     * @param title  标题
     * @param buttons 按钮配置
     * @param shownCallback 弹窗展示后的回调函数
     */
    popDialog: function (html, title, buttons, shownCallback) {
      var configDto = this.options.ui.configDto;
      return appModal.dialog({
        title: title,
        message: html,
        size: 'large',
        buttons: buttons,
        zIndex: 1000,
        shown: function () {
          if ($.isFunction(shownCallback)) {
            shownCallback(configDto);
          }
        }
      });
    },

    /**
     * 退回-操作
     * @param options
     */
    btn_return_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_return_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      _self.confirmReturnDoc(docExchangeRecord.uuid);
    },

    /**
     * 签收-操作
     * @param options
     */
    signOrReturn: function (options) {
      var _self = this;
      var _dialog = appModal.dialog({
        title: '提示',
        message: '请选择签收或者退回',
        size: 'small',
        zIndex: 1000,
        shown: function () {
          _dialog.find('.modal-body').attr('style', 'min-height:40px!important;text-align:center;');
          _dialog.find('.bootbox-body').attr('style', 'min-height:40px!important;text-align:center;');
        },
        buttons: {
          confirm: {
            label: '签收',
            className: 'btn-primary',
            callback: function () {
              server.JDS.call({
                service: 'docExchangerFacadeService.signDocExchangeRecord',
                data: [options.docExchangeRecordUuid, false, null],
                success: function (res) {
                  if (res.data) {
                    options.signCallback();
                  }
                },
                error: function (jqXHR) {
                  appModal.dialog({
                    message: '您没有收件权限！',
                    title: '无权限',
                    zIndex: 1001,
                    buttons: {
                      confirm: {
                        label: '确定',
                        className: 'well-btn w-btn-primary',
                        callback: function () {
                          window.close();
                        }
                      }
                    }
                  });
                },
                async: false
              });
            }
          },
          cancel: {
            label: '退回',
            className: 'btn-default',
            callback: function () {
              _self.confirmReturnDoc(options.docExchangeRecordUuid, options);
            }
          }
        }
      });
    },

    processViewReminder: function (options) {
      var message = '';
      var buttons = {
        confirm: {
          label: '知道了',
          className: 'btn-primary',
          callback: options.callback
        }
      };

      if (options.configDto.refuseToView) {
        // 收件单位可拒绝查看
        message += '<div>发件单位可查阅当前文档的相关办理过程，您可以选择保持现状或关闭查阅。</div>';
        message += '<div class="text-light">（可以在文档页面中开关查阅权限）</div>';

        buttons.refuseToView = {
          label: '关闭查阅',
          className: 'btn-primary btn-line',
          callback: function () {
            updateRefuseToView();
          }
        };
      } else {
        // 收件单位不可拒绝查看
        message += '<div>发件单位可查阅当前文档的相关办理过程。</div>';
      }

      function updateRefuseToView() {
        server.JDS.call({
          async: false,
          service: 'dmsDocExchangeRecordService.updateRefuseToView',
          data: [options.docExchangeRecordUuid, 1],
          success: function (res) {
            appModal.success('已拒绝发件单位查阅相关文档');
            // 用于设置“发件单位可查看相关文档”按钮的状态
            window.refuseToView = 1;
            options.callback();
          },
          error: function (jqXHR) {}
        });
      }

      function updateNotRemind(checked) {
        server.JDS.call({
          async: true,
          service: 'dmsDocExchangeRecordService.updateNoReminders',
          data: [options.docExchangeRecordUuid, checked ? 1 : 0],
          success: function (res) {}
        });
      }

      var _dialog = appModal.dialog({
        title: '温馨提示',
        message: message,
        className: 'alignCenter',
        size: 'small',
        buttons: buttons,
        onEscape: options.callback,
        zIndex: 1001,
        shown: function () {
          var notReminder = '';
          notReminder += '<label style="position: absolute; left: 16px; color: #666;">';
          notReminder += '  <input id="not-remind" type="checkbox">';
          notReminder += '  不再提醒';
          notReminder += '</label>';

          $('.modal-footer', _dialog).prepend(notReminder);

          $('#not-remind', _dialog).click(function () {
            updateNotRemind($(this).attr('checked'));
          });
        }
      });
    },

    confirmReturnDoc: function (uuid, options) {
      var _self = this;
      appModal.dialog({
        title: '退回原因',
        message: _self.createReturnHtml(),
        size: 'middle',
        buttons: {
          confirm: {
            label: '确认',
            className: 'btn-primary',
            callback: function () {
              server.JDS.call({
                service: 'docExchangerFacadeService.signDocExchangeRecord',
                data: [uuid, true, $('#returnReason').val()],
                success: function (res) {
                  if (res.data) {
                    appContext.getWindowManager().refreshParent();
                    appContext.getWindowManager().close();
                  }
                },
                error: function (jqXHR) {},
                async: false
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              if (options) {
                _self.signOrReturn(options);
              }
            }
          }
        }
      });
    },

    createReturnHtml: function () {
      var $form = $('<form>', {
        id: 'returnContainer',
        class: 'dyform'
      });
      var $tableDiv = $('<div>', {
        id: 'retTableContainer'
      });
      $form.append($tableDiv);
      $form.append(
        $('<label>', {
          style: 'margin-bottom:8px;color:#333;'
        }).text('退回原因：'),
        $('<br>'),
        $('<textarea>', {
          id: 'returnReason',
          style: 'resize:vertical;height:100px;width:100%;',
          maxlength: 150,
          class: 'editableClass',
          placeholder: '请输入退回原因'
        })
      );
      return $form[0].outerHTML;
    }
  });

  return DmsDocExchangerReceiverOperateAction;
});

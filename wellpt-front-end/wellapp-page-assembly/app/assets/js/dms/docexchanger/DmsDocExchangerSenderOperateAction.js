define(['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal', 'DmsDyformActionBase', 'formBuilder', 'lodash'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal,
  DmsDyformActionBase,
  formBuilder,
  lodash
) {
  var NOTIFY_WAY_NAME = {
    SMS: '短信',
    IM: '在线消息',
    MAIL: '邮件'
  };

  var StringUtils = commons.StringUtils;

  var DmsDocExchangerSenderOperateAction = function () {
    DmsDyformActionBase.apply(this, arguments);
  };

  commons.inherit(DmsDocExchangerSenderOperateAction, DmsDyformActionBase, {
    /**
     * 补充发送-操作
     * @param options
     * @returns {boolean}
     */
    btn_extra_send_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_extra_send_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      var docExchangeConfig = options.data.extras.docExchangeConfiguration;
      var $dialog = this.popDialog(
        this.createExtraSendHtml(docExchangeConfig, docExchangeRecord),
        '补充发送',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              if (!$('#extraSendUserIds').val()) {
                appModal.info('请添加补发人员');
                return false;
              }
              options.data.docExcRecordUuid = docExchangeRecord.uuid;
              options.data.extraSendData = {
                toUserData: [
                  {
                    toUserId: $('#extraSendUserIds').val(),
                    toUserName: $('#extraSendUserNames').val(),
                    notifyWays: []
                  }
                ]
              };
              if ($('#extraSendSignTimeLimit').val()) {
                options.data.extraSendData.toUserData[0].signTimeLimit = $('#extraSendSignTimeLimit').val() + ':00';
              }
              if ($('#extraSendFeedbackTimeLimit').val()) {
                options.data.extraSendData.toUserData[0].feedbackTimeLimit = $('#extraSendFeedbackTimeLimit').val() + ':00';
              }
              $('.extraSendNotifyWays :checked').each(function (i) {
                options.data.extraSendData.toUserData[0].notifyWays.push($(this).val());
              });

              options.success = function (result) {
                if (result.success) {
                  $dialog.find('.bootbox-close-button').trigger('click');
                }
                _self.dmsDataServices.onPerformedResult(result, options);
              };
              options.failure = function () {
                appModal.hideMask($dialog);
              };
              appModal.showMask('补充发送处理中，请稍后...', $dialog);
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
        function (configDto) {
          $('#extraSendSignTimeLimit,#extraSendFeedbackTimeLimit').datetimepicker({
            showClose: true,
            showClear: true,
            showTodayButton: true,
            format: 'YYYY-MM-DD HH:mm', // 日期格式
            locale: 'zh-cn' // 本地化
          });

          $('#extraSendUserNames').on('click', function () {
            if (configDto.businessCategoryUuid) {
              $.unit2.open({
                targetWindow: window,
                valueField: 'extraSendUserIds',
                labelField: 'extraSendUserNames',
                title: '选择用户',
                type: 'MyUnit;MyDept;MyLeader;MyUnderling;MyCompany;BusinessBook',
                defaultType: 'BusinessBook',
                multiple: true,
                selectTypes: 'O;B;D;J;U;G;DU;E',
                valueFormat: 'justId',
                otherParams: {
                  categoryId: configDto.businessCategoryUuid,
                  showOrgUser: false
                }
              });
            } else {
              $.unit2.open({
                targetWindow: window,
                valueField: 'extraSendUserIds',
                labelField: 'extraSendUserNames',
                title: '选择用户',
                type: 'MyUnit;MyDept;MyLeader;MyUnderling;MyCompany;BusinessBook',
                defaultType: 'MyUnit',
                multiple: true,
                selectTypes: 'O;B;D;J;U;G;DU;E',
                valueFormat: 'justId',
                otherParams: {
                  categoryId: configDto.businessCategoryUuid,
                  showOrgUser: false
                }
              });
            }
          });
        }
      );

      return false;
    },

    /**
     * 撤回-操作
     * @param options
     */
    btn_revoke_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_revoke_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      var docExchangeConfig = options.data.extras.docExchangeConfiguration;
      //弹窗初始化
      var $dialog = this.popDialog(
        this.createRevokeHtml(docExchangeConfig, docExchangeRecord),
        '撤回',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var selections = $('#table_docExcRevokeTable_info').bootstrapTable('getSelections');
              if (selections.length == 0 && !$('#allRevokeAndDraft').prop('checked')) {
                appModal.info('请选择需要撤回的收文人员');
                return false;
              }
              if ($.trim($('#revokeReason').val()).length == 0) {
                appModal.info('请填写撤回原因');
                return false;
              }
              appModal.confirm('确认撤回？', function (result) {
                if (result) {
                  options.data.docExcRecordUuid = docExchangeRecord.uuid;
                  var toUserData = [];
                  for (var i = 0, len = selections.length; i < len; i++) {
                    toUserData.push({
                      receiverUuid: selections[i].uuid,
                      toUserId: selections[i].toUserId,
                      toUserName: selections[i].toUserName
                    });
                  }
                  options.data.revokeData = {
                    content: $('#revokeReason').val(),
                    toUserData: toUserData,
                    operationCode: ''
                  };
                  if ($('#cascadeRevokeCk').prop('checked')) {
                    options.data.revokeData.operationCode = 'REOVKE_CASCADE'; //级联撤回转发的
                  }
                  if ($('#allRevokeAndDraft').prop('checked')) {
                    options.data.revokeData.operationCode += ',DRAFT'; //全部撤回并生成草稿
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
                  appModal.showMask('撤回处理中，请稍后...', $dialog);
                  _self.dmsDataServices.performed(options);
                }
              });

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
          //bootstrap表格初始化
          formBuilder.bootstrapTable.build({
            container: $('#revokeTableContainer'),
            name: 'docExcRevokeTable',
            toolbar: false,
            table: {
              classes: 'table table-hover dmsDocExchangeTable',
              columns: [
                {
                  checkbox: true
                },
                {
                  field: 'toUserName',
                  title: '收文人'
                },
                {
                  field: 'signStatusName',
                  title: '签收情况',
                  formatter: function (v, row, i) {
                    if (row.type == 1) {
                      return v + '  ' + '[补充发送]';
                    }
                    return v;
                  }
                },
                {
                  field: 'uuid',
                  title: 'UUID',
                  visible: false
                }
              ],
              data: _.filter(_self.loadReceiverData(docExchangeRecord.uuid), function (n) {
                return !n.isRevoked;
              }),
              onClickRow: function (row, $element) {
                if ($(event.target).is('input') && $(event.target).closest('.bs-checkbox').length === 0) {
                  return true;
                }
                $('#table_docExcRevokeTable_info').bootstrapTable(
                  $element.find(':checkbox').prop('checked') ? 'uncheck' : 'check',
                  $element[0].rowIndex - 1
                );
              }
            }
          });
          $('#revokeTableContainer .fixed-table-body').css('overflow-x', 'hidden');
        }
      );
    },

    /**
     * 催办-操作
     * @param options
     */
    btn_urge_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_urge_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      var docExchangeConfig = options.data.extras.docExchangeConfiguration;
      //弹窗初始化
      var $dialog = this.popDialog(
        this.createUrgeHtml(docExchangeConfig, docExchangeRecord),
        '催办',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var selections = $('#table_docExcUrgeTable_info').bootstrapTable('getSelections');
              if (selections.length == 0) {
                appModal.info('请选择需要催办的收文人员');
                return false;
              }
              if ($.trim($('#urgeReason').val()).length == 0) {
                appModal.info('请填写催办意见');
                return false;
              }
              options.data.docExcRecordUuid = docExchangeRecord.uuid;
              var toUserData = [];
              var haveNoNotifyWayUserNames = [];
              for (var i = 0, len = selections.length; i < len; i++) {
                var notifyWays = [];
                $("input[name='ck_" + selections[i].uuid + "']:checked").each(function (i) {
                  notifyWays.push($(this).val());
                });
                if (notifyWays.length == 0) {
                  haveNoNotifyWayUserNames.push(selections[i].toUserName);
                  //                                    appModal.info('催办收文人[ ' + selections[i].toUserName + ' ]的方式至少要选一项');
                  //                                    throw new Error('没有选择催办方式');
                } else {
                  toUserData.push({
                    receiverUuid: selections[i].uuid,
                    toUserId: selections[i].toUserId,
                    toUserName: selections[i].toUserName,
                    notifyWays: notifyWays
                  });
                }
              }
              if (haveNoNotifyWayUserNames.length > 0) {
                appModal.info('催办收文人[ ' + haveNoNotifyWayUserNames.join(',') + ' ]的催办方式至少要选一项');
                throw new Error('没有选择催办方式');
              }
              options.data.urgeData = {
                content: $('#urgeReason').val(),
                toUserData: toUserData
              };
              options.success = function (result) {
                if (result.success) {
                  $dialog.find('.bootbox-close-button').trigger('click');
                }
                _self.dmsDataServices.onPerformedResult(result, options);
              };
              options.failure = function () {
                appModal.hideMask($dialog);
              };
              appModal.showMask('催办处理中，请稍后...', $dialog);
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
          var columns = [
            {
              checkbox: true
            },
            {
              field: 'toUserName',
              title: '收文人'
            },
            {
              field: 'signStatusName',
              title: '签收情况',
              formatter: function (v, row, i) {
                if (row.type == 1) {
                  return v + '  ' + '[补充发送]';
                }
                return v;
              }
            },
            {
              field: 'isFeedback',
              title: '反馈情况',
              formatter: function (v, row, i) {
                return v ? '已反馈' : '待反馈';
              }
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false
            }
          ];

          var cnt = 0;
          for (var n = 0, wlen = options.data.docExchangeRecord.notifyWays.length; n < wlen; n++) {
            var nt = options.data.docExchangeRecord.notifyWays[n];

            var title = StringUtils.format(
              '<label><input type="checkbox" value="${notifyType}" class="notifyCkAll ${notifyType}" id="${id}"><label for="${labelId}">${notifyName}</label></label>',
              {
                notifyType: nt,
                notifyName: NOTIFY_WAY_NAME[nt],
                id: nt + n,
                labelId: nt + n
              }
            );

            columns.push({
              field: 'isSmsNotify',
              title: title,
              formatter: function (v, r, i) {
                var id = r.uuid + '_ck_' + options.data.docExchangeRecord.notifyWays[cnt % wlen];
                var val = options.data.docExchangeRecord.notifyWays[cnt++ % wlen];

                var html = StringUtils.format(
                  '<label><input type="checkbox" class="${class}" value="${value}" id="${id}" name="${name}"><label for="${labelFor}"></label></label>',
                  {
                    id: id,
                    class: 'notifyCk_' + val,
                    value: val,
                    name: 'ck_' + r.uuid,
                    labelFor: id
                  }
                );

                return html;
              }
            });
          }
          //bootstrap表格初始化
          formBuilder.bootstrapTable.build({
            container: $('#urgeTableContainer'),
            name: 'docExcUrgeTable',
            toolbar: false,
            table: {
              classes: 'table table-hover dmsDocExchangeTable',

              columns: columns,
              data: _.filter(_self.loadReceiverData(docExchangeRecord.uuid), function (n) {
                return !n.isRevoked && n.signStatus != 'RETURNED'; //过滤出非撤回与非退回的数据
              }),
              onClickRow: function (row, $element) {
                if ($(event.target).is('input') || $(event.target).is('label')) {
                  return true;
                }
                if (!$(window.event.target).is("[class^='notifyCk_']"))
                  $('#table_docExcUrgeTable_info').bootstrapTable(
                    $element.find(':checkbox').prop('checked') ? 'uncheck' : 'check',
                    $element[0].rowIndex - 1
                  );
              }
            }
          });
          $('#urgeTableContainer .fixed-table-body').css('overflow-x', 'hidden');
          $('.notifyCkAll').on('click', function () {
            $('.notifyCk_' + $(this).val()).prop('checked', $(this).prop('checked'));
          });
        }
      );
    },

    /**
     * 办结-操作
     * @param options
     * @returns {boolean}
     */
    btn_finish_doc_exchanger: function (options) {
      var _self = this;
      console.log('btn_finish_doc_exchanger invoked! options:', options);
      var docExchangeRecord = options.data.docExchangeRecord;
      var docExchangeConfig = options.data.extras.docExchangeConfiguration;
      var $dialog = this.popDialog(
        this.createFinishHtml(docExchangeConfig, docExchangeRecord),
        '办结',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              options.data.docExcRecordUuid = docExchangeRecord.uuid;
              options.success = function (result) {
                if (result.success) {
                  $dialog.find('.bootbox-close-button').trigger('click');
                }
                _self.dmsDataServices.onPerformedResult(result, options);
              };
              options.failure = function () {
                appModal.hideMask($dialog);
              };
              appModal.showMask('办结处理中，请稍后...', $dialog);
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
          //bootstrap表格初始化
          formBuilder.bootstrapTable.build({
            container: $('#finishTableContainer'),
            name: 'docExcFinishTable',
            toolbar: false,
            table: {
              classes: 'table table-hover dmsDocExchangeTable',
              columns: [
                {
                  field: 'toUserName',
                  title: '收文人'
                },
                {
                  field: 'signStatusName',
                  title: '签收情况',
                  formatter: function (v, row, i) {
                    if (row.type == 1) {
                      return v + '  ' + '[补充发送]';
                    }
                    return v;
                  }
                },
                {
                  field: 'isFeedback',
                  title: '反馈情况',
                  formatter: function (v, row, i) {
                    return v ? '已反馈' : '待反馈';
                  }
                },
                {
                  field: 'uuid',
                  title: 'UUID',
                  visible: false
                }
              ],
              data: _self.loadReceiverData(docExchangeRecord.uuid)
            }
          });
          $('#finishTableContainer .fixed-table-body').css('overflow-x', 'hidden');
        }
      );
      return false;
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
        shown: function () {
          if ($.isFunction(shownCallback)) {
            shownCallback(configDto);
          }
        }
      });
    },

    //生成补充发送的HTML
    createExtraSendHtml: function (configuration, data) {
      var $table = $('<table>', {
        id: 'extraSendTable',
        class: 'table label-height-table'
      });
      var $userTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).text('补发人员'),
        $('<td>', {
          colspan: '3'
        }).append(
          $('<input>', {
            type: 'hidden',
            id: 'extraSendUserIds'
          }),
          $('<input>', {
            type: 'text',
            id: 'extraSendUserNames',
            readonly: ''
          })
        )
      );
      $table.append($userTr);

      var $tds = [];
      if (data && data.isNeedSign) {
        $tds.push([
          $('<td>', {
            class: 'label-td'
          }).text('签收时限'),
          $('<td>').append(
            $('<input>', {
              type: 'text',
              id: 'extraSendSignTimeLimit'
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
              id: 'extraSendFeedbackTimeLimit'
            })
          )
        ]);
      }
      var notifyTypes = [];

      var notifyTypesData = configuration.notifyTypes.length == 0 ? data.notifyWays : configuration.notifyTypes;

      // 调整顺序 => 在线消息 / 短信 / 邮件
      notifyTypes.sort(function (a, b) {
        var idx1 = $.inArray(a, ['IM', 'SMS', 'MAIL']);
        var idx2 = $.inArray(b, ['IM', 'SMS', 'MAIL']);
        return idx2 - idx1;
      });

      for (var n = 0; n < notifyTypesData.length; n++) {
        notifyTypes.push(
          $('<input>', {
            type: 'checkbox',
            name: 'extraSendNw',
            value: notifyTypesData[n],
            id: 'extranw' + n
          })
        );
        notifyTypes.push(
          $('<label>', {
            for: 'extranw' + n
          }).text(NOTIFY_WAY_NAME[notifyTypesData[n]])
        );
      }
      $tds.push([
        $('<td>', {
          class: 'label-td'
        }).text('提醒方式'),
        $('<td>').append(
          $('<span>', {
            class: 'editableClass extraSendNotifyWays'
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

      return $('<form>', {
        class: 'dyform'
      }).append($table)[0].outerHTML;
    },

    //获取接收者数据
    loadReceiverData: function (docExcUuid) {
      var data = [];
      server.JDS.call({
        service: 'docExchangerFacadeService.listDocExchangeReceiverDetail',
        data: [docExcUuid],
        success: function (res) {
          data = res.data;
        },
        error: function (jqXHR) {},
        async: false
      });
      return data;
    },

    //生成撤回的HTML
    createRevokeHtml: function (configuration, data) {
      var $form = $('<form>', {
        id: 'revokeContainer',
        class: 'dyform'
      });
      var $tableDiv = $('<div>', {
        id: 'revokeTableContainer'
      });
      $form.append($tableDiv);
      var $div = $('<div>', {
        class: 'div-bootstraptable-formbuilder'
      });
      $div.append(
        $('<font>', {
          color: 'red',
          size: 2
        }).text('*'),
        $('<label>').text('撤回原因：').css({
          'margin-bottom': '10px'
        }),
        $('<br>'),
        $('<textarea>', {
          id: 'revokeReason',
          style: 'resize:vertical;height:100px;width:100%;',
          maxlength: 150,
          class: 'editableClass',
          placeholder: '请输入撤回原因'
        }),
        $('<br>'),
        $('<input>', {
          type: 'checkbox',
          id: 'cascadeRevokeCk'
        }).addClass('docExchangeCheckbox'),
        $('<label>', {
          for: 'cascadeRevokeCk'
        }).text('同时撤回收文人的转发'),
        $('<input>', {
          type: 'checkbox',
          id: 'allRevokeAndDraft',
          style: 'margin-left:10px;'
        }).addClass('docExchangeCheckbox'),
        $('<label>', {
          for: 'allRevokeAndDraft'
        }).text('全部撤回收文并生成草稿')
      );
      $form.append($div);

      return $form[0].outerHTML;
    },

    //生成催办的HTML
    createUrgeHtml: function (configuration, data) {
      var $form = $('<form>', {
        id: 'urgeContainer',
        class: 'dyform'
      });
      var $tableDiv = $('<div>', {
        id: 'urgeTableContainer'
      });
      $form.append($tableDiv);
      var $div = $('<div>', {
        class: 'div-bootstraptable-formbuilder'
      });
      $div.append(
        $('<p>', {
          style: 'color:red;'
        }).text('注:已撤回与已退回的用户无法进行催办'),
        $('<font>', {
          color: 'red',
          size: 2
        }).text('*'),
        $('<label>').text('催办意见：').css({
          'margin-bottom': '10px'
        }),
        $('<br>'),
        $('<textarea>', {
          id: 'urgeReason',
          style: 'resize:vertical;height:100px;width:100%;',
          maxlength: 150,
          class: 'editableClass',
          placeholder: '请输入催办意见'
        })
      );
      $form.append($div);
      return $form[0].outerHTML;
    },

    //生成办结的HTML
    createFinishHtml: function (configuration, data) {
      var $form = $('<form>', {
        id: 'finishContainer',
        class: 'dyform'
      });
      var $tableDiv = $('<div>', {
        id: 'finishTableContainer'
      });
      $form.append($tableDiv);
      $form.append(
        $('<p>', {
          style: 'color:red;'
        }).text('注:确认后将该发文将标记为已办结')
      );
      return $form[0].outerHTML;
    }
  });

  return DmsDocExchangerSenderOperateAction;
});

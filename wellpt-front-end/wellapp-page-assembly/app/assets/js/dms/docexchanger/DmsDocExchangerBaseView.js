define(['jquery', 'commons', 'constant', 'server', 'ViewDevelopmentBase', 'appModal', 'layDate', 'appContext'], function (
  $,
  commons,
  constant,
  server,
  ViewDevelopmentBase,
  appModal,
  laydate,
  appContext
) {
  var DmsDocExchangerBaseView = function () {
    ViewDevelopmentBase.apply(this, arguments);
  };

  var NOTIFY_TYPES = {
    SMS: '短信',
    MAIL: '邮件',
    IM: '在线消息'
  };

  var URGE_LEVEL = {
    NORMAL: '一般',
    URGE: '急件',
    EXTRA_URGE: '特急'
  };

  var ENCRYPT_LEVEL = {
    NO_ENCRYPT: '非密',
    SECRET: '秘密',
    CONFIDENTIAL: '机密'
  };

  commons.inherit(DmsDocExchangerBaseView, ViewDevelopmentBase, {
    $container: null,
    options: null,
    configuration: {},
    data: null,

    beforeRender: function (options) {
      this.$container = options.$container;
      this.options = options;
      this.configDto = options.configDto;
    },

    afterRender: function () {
      if (this.options.configDto.dmsDocExchangeDyformUuid == '' || this.options.configDto.dmsDocExchangeDyformUuid == null) {
        this.bindEvent();
      }

      if (this.options.displayAsLabel) {
        $(':input', this.$container).trigger('toLabel');
      }
    },

    init: function (options) {
      this.beforeRender(options);
      this.loadDocExchangerData();

      if (options.configDto.dmsDocExchangeDyformUuid == '' || options.configDto.dmsDocExchangeDyformUuid == null) {
        this.createTableLayout(options.configDto);
      }

      this.afterRender();

      if (options.configDto.dmsDocExchangeDyformUuid == '' || options.configDto.dmsDocExchangeDyformUuid == null) {
        this.initData();
      }
    },

    loadDocExchangerData: function () {
      var _self = this;
      if (this.options.docExchangeRecordUuid || (this.options.formUuid && this.options.dataUuid)) {
        server.JDS.call({
          service: 'docExchangerFacadeService.getDocExchangeRecord',
          data: [this.options.docExchangeRecordUuid, this.options.formUuid, this.options.dataUuid],
          success: function (res) {
            _self.data = res.data;
            if (_self.data.configurationJson) {
              _self.configuration = JSON.parse(_self.data.configurationJson);
            }
          },
          error: function (jqXHR) {
          },
          async: false
        });
      }
    },

    initData: function () {
      var data = this.data;
      if (data) {
        $('#selectUserIds').val(data.toUserIds);
        $('#selectUserNames').val(data.toUserNames);
        $('#ex_doc_signTimeLimit').val(data.signTimeLimit);
        $('#ex_doc_feedbackTimeLimit').val(data.feedbackTimeLimit);
        $('#ex_doc_toUserIds').val(data.toUserIds);
        $('#ex_doc_fromUnitId').val(data.fromUnitId);
        $('#doc_sendTime').text(data.sendTime);
        if (data.toUserIds != '' && data.toUserIds != null) {
          var userName = data.toUserNames.split(';');
          var userId = data.toUserIds.split(';');
          var oHtml = '';
          $.each(userId, function (oIndex, oItem) {
            var currClass = oItem.substr(0, 1);
            oHtml += "<li class='org-entity " + currClass + "'><span class='org-label'>" + userName[oIndex] + '</span></li>';
          });
          $('#label_ex_doc_toUserIds .org-select').html(oHtml);
        }
        $('#docExchangeRecordUuid').val(data.uuid);
        if (data.docEncryptionLevel != null) {
          $('#ex_doc_docEncryptionLevel').wellSelect('val', data.docEncryptionLevel.toString());
        }
        if (data.docUrgeLevel != null) {
          $('input[name="ex_doc_docUrgeLevel"][value="' + data.docUrgeLevel + '"]').prop('checked', true);
        }

        $('input[name="ex_doc_isNeedSign"]').prop('checked', data.isNeedSign);
        $('input[name="ex_doc_isNeedFeedback"]').prop('checked', data.isNeedFeedback);

        if (data.notifyWays.length > 0) {
          $('input[name=ex_doc_notifyTypes]').prop('checked', false);
          for (var i in data.notifyWays) {
            var $ckInput = $('input[name=ex_doc_notifyTypes][value=' + data.notifyWays[i] + ']');
            $ckInput.prop('checked', true);
          }
        }
        if (data.forwardDto) {
          this.createForwardDetailTableLayout(data.forwardDto);
        }
      }
    },

    bindEvent: function () {
      var _self = this;

      if ($('#ex_doc_signTimeLimit,#ex_doc_feedbackTimeLimit').length > 0) {
        laydate.render({
          elem: '#ex_doc_signTimeLimit',
          format: 'yyyy-MM-dd HH:mm',
          trigger: 'click',
          min: Date.now(),
          type: 'datetime'
        });

        laydate.render({
          elem: '#ex_doc_feedbackTimeLimit',
          format: 'yyyy-MM-dd HH:mm',
          trigger: 'click',
          min: Date.now(),
          type: 'datetime'
        });
      }

      $('#label_ex_doc_toUserIds')
        .off()
        .on('click', function () {
          if ($(this).attr('readonly')) {
            return;
          }
          $.unit2.open({
            targetWindow: window,
            valueField: 'selectUserIds',
            labelField: 'selectUserNames',
            title: '选择用户',
            type: 'MyUnit;MyDept;MyLeader;MyUnderling;BusinessBook',
            multiple: true,
            selectTypes: 'O;B;D;J;U;G;DU;E',
            valueFormat: 'justId',
            orgStyle: 'org-style3',
            otherParams: {
              categoryId: _self.configDto.businessCategoryUuid,
              showOrgUser: true
            },
            callback: function (values, labels) {
              $('#ex_doc_toUserIds').val(values);
              var oHtml = '';
              $.each(values, function (oIndex, oItem) {
                var currClass = oItem.substr(0, 1);
                oHtml += "<li class='org-entity " + currClass + "'><span class='org-label'>" + labels[oIndex] + '</span></li>';
              });
              $('#label_ex_doc_toUserIds .org-select').html(oHtml);
            }
          });
        });
      $('#selectUserNames').prop('readonly', true);
      var encryptionLevelData = [
        {
          id: '1',
          text: '非密'
        },
        {
          id: '2',
          text: '秘密'
        },
        {
          id: '3',
          text: '机密'
        },
        {
          id: '4',
          text: '绝密'
        }
      ];
      $('#ex_doc_docEncryptionLevel').wellSelect({
        data: encryptionLevelData,
        valueField: 'ex_doc_docEncryptionLevel'
      }).wellSelect('val', _self.configDto.defaultEncryptionLevel).trigger('change');
      if (_self.configDto.defaultUrgeLevel) {
        $('input[name="ex_doc_docUrgeLevel"][value="' + _self.configDto.defaultUrgeLevel + '"]').attr("checked", true);
      }

      if (_self.configDto.businessCategoryUuid) {
        $.ajax({
          url: ctx + '/api/dms/doc/exc/config/queryBusinessCategorOrgs',
          type: 'get',
          data: {
            categoryUuid: _self.configDto.businessCategoryUuid
          },
          success: function (res) {
            if (res.code == 0) {
              $('#ex_doc_fromUnitId').wellSelect({
                data: res.data,
                valueField: 'ex_doc_fromUnitId'
              });
            }
          }
        });
      } else {
        $('#ex_doc_fromUnitId').val(SpringSecurityUtils.getCurrentUserUnitName()).attr('disabled', true);
      }

      if (_self.configDto.defaultSign == 1) {
        $("input[name='ex_doc_isNeedSign']").prop('checked', true);
      }

      if (_self.configDto.defaultFeedback == 1) {
        $("input[name='ex_doc_isNeedFeedback']").prop('checked', true);
      }

      if (_self.configDto.defaultNotifyTypes != '') {
        var dNotifyTypes = _self.configDto.defaultNotifyTypes && _self.configDto.defaultNotifyTypes.split(';');
        $.each(dNotifyTypes, function (dIndex, dItem) {
          $("input[name='ex_doc_notifyTypes'][value='" + dItem + "']").prop('checked', true);
        });
      }
    },

    createForwardDetailTableLayout: function (forwardData) {
      var $table = $('<table>');
      this.$container.append($table);

      var $forwardSenderInfoTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).text('转发单位'),
        $('<td>').append($('<span>').text(forwardData.fromUserUnitName)),
        $('<td>', {
          class: 'label-td'
        }).text('转发人'),
        $('<td>').append($('<span>').text(forwardData.fromUserName))
      );
      var $forwardTimeTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).text('转发时间'),
        $('<td>').append($('<span>').text(forwardData.createTime)),
        $('<td>', {
          class: 'label-td'
        }).text('转发对象'),
        $('<td>').append($('<span>').text(forwardData.toUserNames))
      );

      var fileHtml = '';
      if (forwardData.fileNames) {
        var fileNameArr = forwardData.fileNames.split('/');
        var fileUuidArr = forwardData.fileUuids.split('/');
        for (var j = 0; j < fileNameArr.length; j++) {
          var $a = $('<a>', {
            href: ctx + fileServiceURL.downFile + fileUuidArr[j],
            style: 'margin-right:10px;'
          }).text(fileNameArr[j]);
          fileHtml += $a[0].outerHTML;
          if (j != fileNameArr.length - 1) {
            fileHtml += '<br>';
          }
        }
      }

      var $forwardFileTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).text('转发附件'),
        $('<td>', {
          colspan: '3'
        }).html(fileHtml)
      );

      var $remarkTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).text('转发意见'),
        $('<td>', {
          colspan: '3'
        }).text(forwardData.remark || '')
      );

      $table.append($forwardSenderInfoTr, $forwardTimeTr, $forwardFileTr, $remarkTr);

      var $tds = [];
      if (this.options.configuration.isSign) {
        $tds.push([
          $('<td>', {
            class: 'label-td'
          }).text('签收时限'),
          $('<td>').text(forwardData.signTimeLimit)
        ]);
      }
      if (this.options.configuration.isFeedback) {
        $tds.push([
          $('<td>', {
            class: 'label-td'
          }).text('反馈时限'),
          $('<td>').text(forwardData.feedbackTimeLimit)
        ]);
      }

      if (this.options.configuration.isNotify) {
        $tds.push([
          $('<td>', {
            class: 'label-td'
          }).text('提醒方式'),
          $('<td>').text(
            (forwardData.isSmsNotify ? '短信 ' : '') +
            (forwardData.isMailNotify ? '邮件 ' : '') +
            (forwardData.isImNotify ? '在线消息' : '')
          )
        ]);
      }

      var $tr;
      for (var i = 0, len = $tds.length; i < len; i++) {
        if (i == 0 || i % 2 == 0) {
          $tr = $('<tr>');
          $table.append($tr);
        }
        if (i == len - 1 && $tr.children().length == 0) {
          $tds[i][1].attr('colspan', 3);
        }
        $tr.append($tds[i]);
      }
    },

    createTableLayout: function (configDto) {
      console.log(configDto);
      var $table = $('<table>');
      this.$container.append($table);

      if (configDto.businessCategoryUuid != '') {
        var $senderInfoTr = $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).text('发件单位'),
          $('<td>').append(
            $('<input>', {
              id: 'ex_doc_fromUnitId',
              class: 'labelclass',
              type: 'text'
            })
          ),
          $('<td>', {
            class: 'label-td'
          }).text('发件人'),
          $('<td>').append(
            $('<span>', {
              id: 'ex_doc_fromUserId',
              class: 'labelclass'
            }).text(SpringSecurityUtils.getCurrentUserName())
          )
        );

        var $receiverTr = $('<tr>').append(
          $('<td>', {
            class: 'label-td required'
          }).text('收件单位'),
          $('<td>', {
            colspan: 3
          }).append(
            $('<input>', {
              id: 'ex_doc_toUserIds',
              class: 'labelclass',
              type: 'text',
              style: 'display:none'
            }),
            $('<div>', {
              class: 'org-select-container editableClass input-people org-style3',
              id: 'label_ex_doc_toUserIds'
            }).append(
              $('<ul>', {
                class: 'org-select'
              }),
              $('<i>', {
                class: 'icon iconfont icon-ptkj-zuzhixuanze'
              })
            )
          )
        );

        $table.append($senderInfoTr, $receiverTr);
      }

      var $tds1 = [];

      if (configDto.docEncryptionLevel == 1) {
        $tds1.push([
          $('<td>', {
            class: 'label-td'
          }).text('文档密级'),
          $('<td>').append(
            $('<input>', {
              type: 'text',
              id: 'ex_doc_docEncryptionLevel'
            })
          )
        ]);
      }

      if (configDto.docUrgeLevel == 1) {
        var docUrgeLevelData = [
          {
            name: '一般',
            value: '0'
          },
          {
            name: '急件',
            value: '1'
          },
          {
            name: '特级',
            value: '2'
          }
        ];

        $tds1.push([
          $('<td>', {
            class: 'label-td'
          }).text('文档缓急程度'),
          $('<td>').append($(getDomHtml('ex_doc_docUrgeLevel', 'radio', docUrgeLevelData)))
        ]);
      }

      mergeHtml($table, $tds1);

      var $tds2 = [];

      if (configDto.isNeedSign == 1 && configDto.docSign == 1) {
        var isNeedSignData = [
          {
            name: '需签收',
            value: '1'
          }
        ];
        $tds2.push([
          $('<td>', {
            class: 'label-td'
          }).text('是否需要签收'),
          $('<td>').append($(getDomHtml('ex_doc_isNeedSign', 'checkbox', isNeedSignData)))
        ]);
      }

      if (configDto.signTimeLimit == 1) {
        $tds2.push([
          $('<td>', {
            class: 'label-td'
          }).text('签收时限'),
          $('<td>').append(
            $('<input>', {
              type: 'text',
              id: 'ex_doc_signTimeLimit',
              class: 'editableClass Wdate',
              name: 'ex_doc_signTimeLimit'
            })
          )
        ]);
      }

      mergeHtml($table, $tds2);

      var $tds3 = [];

      if (configDto.isNeedFeedback == 1 && configDto.docFeedback == 1) {
        var isNeedFeedbackData = [
          {
            name: '需反馈',
            value: '1'
          }
        ];
        $tds3.push([
          $('<td>', {
            class: 'label-td'
          }).text('是否需要反馈'),
          $('<td>').append($(getDomHtml('ex_doc_isNeedFeedback', 'checkbox', isNeedFeedbackData)))
        ]);
      }

      if (configDto.feedbackTimeLimit == 1) {
        $tds3.push([
          $('<td>', {
            class: 'label-td'
          }).text('反馈时限'),
          $('<td>').append(
            $('<input>', {
              type: 'text',
              id: 'ex_doc_feedbackTimeLimit',
              class: 'editableClass Wdate laydate-ex_doc_feedbackTimeLimit',
              name: 'ex_doc_feedbackTimeLimit'
            })
          )
        ]);
      }

      mergeHtml($table, $tds3);

      if (configDto.notifyTypes != '') {
        var nArr = ['IM|在线消息', 'SMS|短信', 'MAIL|邮件'];
        var notifyTypesData = [];

        if (configDto.notifyTypes != null) {
          var currNotifyTypes = configDto.notifyTypes.split(';');
          $.each(nArr, function (nIdex, nItem) {
            var oneType = nItem.split('|');
            if (currNotifyTypes.indexOf(oneType[0]) > -1) {
              notifyTypesData.push({
                name: oneType[1],
                value: oneType[0]
              });
            }
          });
        }

        var $notifyTypes = $('<tr>').append([
          $('<td>', {
            class: 'label-td'
          }).text('提醒方式'),
          $('<td>', {
            colspan: 3
          }).append($(getDomHtml('ex_doc_notifyTypes', 'checkbox', notifyTypesData)))
        ]);
        $table.append($notifyTypes);
      }

      var $sendTimeTr = $('<tr>').append([
        $('<td>', {
          class: 'label-td'
        }).text('发件时间'),
        $('<td>', {
          colspan: 3
        }).append($('<span id="doc_sendTime">'))
      ]);
      $table.append($sendTimeTr);

      function getDomHtml(field, type, data) {
        var docUrgeLevelHtml =
          "<span class='editableClass' name='" + field + "' fieldname='" + field + "'><div class='input-box'><div class='row text-left'>";

        $.each(data, function (lIndex, lItem) {
          docUrgeLevelHtml +=
            "<div class='fix-margin'>" +
            "<input type='" +
            type +
            "' id='" +
            field +
            lItem.value +
            "' name='" +
            field +
            "' value='" +
            lItem.value +
            "'>" +
            "<label class='iconfont' for='" +
            field +
            lItem.value +
            "'>" +
            lItem.name +
            '</label>' +
            '</div>';
        });
        docUrgeLevelHtml += '</div></div></span>';

        return docUrgeLevelHtml;
      }

      function mergeHtml($table, $tds) {
        var $tr;
        for (var i = 0, len = $tds.length; i < len; i++) {
          if (i == 0 || i % 2 == 0) {
            $tr = $('<tr>');
            $table.append($tr);
          }
          if (i == len - 1 && $tr.children().length == 0) {
            $tds[i][1].attr('colspan', 3);
          }
          $tr.append($tds[i]);
        }
      }

      var $hiddenHtml =
        "<div style='display:none;'>" +
        "<input type='hidden' name='selectUserIds' id='selectUserIds'/>" +
        "<input type='hidden' name='selectUserNames' id='selectUserNames'/>" +
        '</div>';

      this.$container.append($hiddenHtml);
    }
  });

  return DmsDocExchangerBaseView;
});

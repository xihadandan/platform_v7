define('DmsDocExchangerSaveAction', ['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal', 'DmsDyformActionBase'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal,
  DmsDyformActionBase
) {
  var DmsDocExchangerSaveAction = function () {
    DmsDyformActionBase.apply(this, arguments);
  };

  commons.inherit(DmsDocExchangerSaveAction, DmsDyformActionBase, {
    collectionDocExchangeConfigInfo: function (options) {
      var configDto = JSON.parse($('#dms_configDto').val());
      var flowUuid = options.data.extras.docExchangeConfiguration.flowUuid || configDto.flowUuid;
      var isNeedSign = '';
      var isNeedFeedback = '';
      if (configDto.isNeedSign == 1 && configDto.docSign == 1) {
        isNeedSign = $('#ex_doc_isNeedSign1').prop('checked') ? '1' : '0';
      } else if (configDto.isNeedSign == 1) {
        isNeedSign = 1;
      }
      if (configDto.isNeedFeedback == 1 && configDto.docFeedback == 1) {
        isNeedFeedback = $('#ex_doc_isNeedFeedback1').prop('checked') ? '1' : '0';
      } else if (configDto.isNeedFeedback == 1) {
        isNeedFeedback = 1;
      }
      var $notifyTypes = $('input[name="ex_doc_notifyTypes"]');
      var notifyTypes = [];
      $.each($notifyTypes, function (index, item) {
        if ($(item).prop('checked')) {
          notifyTypes.push($(item).val());
        }
      });
      // debugger
      // var isNeedSign = configDto.isNeedSign == 1 && configDto.docSign == 1?
      var docExchangeConfig = {
        selectUserIds: $('#selectUserIds').val() == null ? '' : $('#selectUserIds').val(),
        selectUserNames: $('#selectUserNames').val(),
        signTimeLimit: $('#ex_doc_signTimeLimit').val(),
        feedbackTimeLimit: $('#ex_doc_signTimeLimit').val(),
        docEncryptionLevel: $('#ex_doc_docEncryptionLevel').val(),
        docUrgeLevel: $('input[name="ex_doc_docUrgeLevel"]:checked').val(),
        isNeedSign: isNeedSign, //需要签收动作
        isNeedFeedback: isNeedFeedback,
        documentTitle: $('.widget-title').text(),
        dataType: configDto.exchangeType,
        flowUuid: flowUuid || '',
        configurationJson: JSON.stringify(options.data.extras.docExchangeConfiguration),
        notifyTypes: notifyTypes,
        fromUnitId: $('#ex_doc_fromUnitId').val(),
        fromUserId: $('#ex_doc_fromUserId').text(),
        toUserIds: $('#selectUserIds').val() == null ? '' : $('#selectUserIds').val(),
        sendTime: ''
      };
      if (options.data.docExchangeRecord) {
        docExchangeConfig.uuid = options.data.docExchangeRecord.uuid;
      }
      $("input[name='notifyWay']:checked").each(function (i) {
        docExchangeConfig.notifyTypes.push($(this).val());
      });
      var newNotifyTypes = [];
      $.each(docExchangeConfig.notifyTypes, function (nIndex, nItem) {
        var newItem = nItem == 'IM' ? '2' : nItem == 'SMS' ? '0' : '2';
        newNotifyTypes.push(newItem);
      });
      docExchangeConfig.notifyTypes = newNotifyTypes.join(';');
      return docExchangeConfig;
    },
    //保存
    btn_save_doc_exchanger: function (options) {
      options = this.getExtraDatas(options);
      this.dmsDataServices.performed(options);
      return true;
    },
    //保存并校验
    btn_save_and_validate_doc_exchanger: function (options) {
      options = this.getExtraDatas(options);
      if (!options.data.extras.docExchangeConfig.selectUserIds) {
        appModal.error('请选择收件单位');
        return false;
      }
      this.dmsDataServices.performed(options);
      return true;
    },
    getExtraDatas: function (options) {
      // options.data.extras.docExchangeConfig = this.collectionDocExchangeConfigInfo(options); //发文选项
      var dms_configDto = JSON.parse($('#dms_configDto').val());
      var formDatas = options.data.dyFormData.formDatas;
      var fields = {};
      if (
        dms_configDto.dmsDocExchangeDyformUuid &&
        dms_configDto.dmsDocExchangeDyformUuid != '' &&
        dms_configDto.dmsDocExchangeDyformUuid != null
      ) {
        for (var i in formDatas) {
          var formData = formDatas[i][0];
          for (var j in formData) {
            if (j.indexOf('ex_doc_') > -1) {
              var field = j.split('ex_doc_')[1];
              if (field == 'notifyTypes') {
                fields[field] = formData[j].split(';');
                fields.isMailNotify = fields[field].indexOf('1') > -1 ? true : false;
                fields.isSmsNotify = fields[field].indexOf('0') > -1 ? true : false;
                fields.isImNotify = fields[field].indexOf('2') > -1 ? true : false;
              } else {
                fields[field] = formData[j];
              }

              delete formData[j];
            }
          }
        }
        options.data.extras.docExchangeConfig = _.assign(this.collectionDocExchangeConfigInfo(options), fields);
        options.data.extras.docExchangeConfig.configUuid = options.data.extras.docExchangeConfiguration.configUuid;
        options.data.extras.docExchangeConfig.selectUserIds = options.data.extras.docExchangeConfig.toUserIds;
        var $selectUserNames = $("input[name='ex_doc_toUserIds']").next().find('.org-label');
        var names = [];
        $.each($selectUserNames, function (index, item) {
          names.push($(item).text());
        });
        options.data.extras.docExchangeConfig.selectUserNames = names.join(';');
      } else {
        options.data.extras.docExchangeConfig = _.assign(this.collectionDocExchangeConfigInfo(options));
        options.data.extras.docExchangeConfig.configUuid = options.data.extras.docExchangeConfiguration.configUuid;
        options.data.extras.docExchangeConfig.notifyTypes =
          options.data.extras.docExchangeConfig.notifyTypes == '' ? [] : options.data.extras.docExchangeConfig.notifyTypes.split(';');
        options.data.extras.docExchangeConfig.isMailNotify =
          options.data.extras.docExchangeConfig.notifyTypes.indexOf('1') > -1 ? true : false;
        options.data.extras.docExchangeConfig.isSmsNotify =
          options.data.extras.docExchangeConfig.notifyTypes.indexOf('0') > -1 ? true : false;
        options.data.extras.docExchangeConfig.isImNotify =
          options.data.extras.docExchangeConfig.notifyTypes.indexOf('2') > -1 ? true : false;
      }
      options.data.dyFormData.formDatas = formDatas;

      return options;
    },
    //发送
    btn_send_doc_exchanger: function (options) {
      this.btn_save_and_validate_doc_exchanger(options);
    }
  });

  return DmsDocExchangerSaveAction;
});

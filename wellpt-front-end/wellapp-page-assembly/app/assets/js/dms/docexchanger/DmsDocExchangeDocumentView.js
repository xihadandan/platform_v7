define([
  'jquery',
  'commons',
  'appContext',
  'appModal',
  'DmsDataServices',
  'DmsDocumentViewDevelopment',
  'DmsActionDispatcher',
  'DyformExplain',
  'server'
], function ($, commons, appContext, appModal, DmsDataServices, DmsDocumentViewDevelopment, DmsActionDispatcher, DyformExplain, server) {
  var StringBuilder = commons.StringBuilder;
  var dmsDataServices = new DmsDataServices();
  // 获取表单数据操作ID
  var action_get_id = 'btn_get_doc_exchanger';
  // 数据管理表单单据开发
  var DmsDocExchangeDocumentView = function () {
    DmsDocumentViewDevelopment.apply(this, arguments);
  };
  var StringUtils = commons.StringUtils;

  function _getButtonPresettingById(id) {
    var presettingMap = {
      btn_send_doc_exchanger: {
        type: 'primary',
        iconfont: 'icon-ptkj-tijiaofabufasong'
      },
      btn_save_doc_exchanger: {
        type: 'primary',
        iconfont: 'icon-ptkj-baocun'
      },
      btn_urge_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-ptkj-cuibanjian'
      },
      btn_finish_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-xmch-yibangongzuo'
      },
      btn_revoke_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-oa-chehui'
      },
      btn_extra_send_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-ptkj-buchongfasong'
      },
      btn_forward_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-oa-zhuanban'
      },
      btn_return_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-oa-zhijietuihui'
      },
      btn_feedback_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-ptkj-wentifankui'
      },
      btn_save_and_validate_doc_exchanger: {
        type: 'line',
        iconfont: 'icon-ptkj-baocunbingyanzheng'
      }
    };

    return presettingMap[id];
  }

  function _createdBtnLibObj(id) {
    var presetting = _getButtonPresettingById(id);
    var type = presetting ? presetting.type : 'line';
    var iconfont = presetting ? presetting.iconfont : '';

    return {
      btnSize: '',
      btnColor: 'w-btn-primary',
      iconInfo: {
        fileType: 3,
        fileIDs: 'iconfont ' + iconfont
      },
      btnInfo: {
        type: 'line',
        class: type === 'line' ? 'w-line-btn' : '',
        status: [
          {
            class: '',
            text: '普通状态'
          },
          {
            class: 'hover',
            text: '鼠标移入状态'
          },
          {
            class: 'active',
            text: '点击状态'
          },
          {
            class: 'w-disable-btn',
            text: '禁用状态'
          }
        ]
      }
    };
  }

  commons.inherit(DmsDocExchangeDocumentView, DmsDocumentViewDevelopment, {
    // 初始化
    init: function (options) {
      var _self = this;
      _self.options = options;
      _self.dmsId = options.dmsId; //文档交换器CMS配置ID
      _self.formUuid = options.formUuid; //表单定义UUID
      _self.dataUuid = options.dataUuid; //表单数据UUID
      _self.dataType = options.dataType; //数据类型
      _self.moduleId = options.extraParams.ep_moduleId; //归属业务模块ID
      _self.docExcRecordUuid = options.docExchangeRecordUuid; //文档交换记录UUID
      _self.dyformSelector = options.dyformSelector; //表单前端选择元素
      _self.configDto = options.configDto ? JSON.parse(options.configDto) : {}; //表单前端选择元素
      _self.dyformDto = options.dyformDto == '' ? '' : JSON.parse(options.dyformDto); //表单前端选择元素
    },
    // 获取加载数据的操作ID
    getLoadDataActionId: function () {
      var _self = this;
      // 如果传入获取数据的action，则使用该action
      var extraParams = _self.options.extraParams || {};
      return extraParams['ep_ac_get'] || action_get_id;
    },
    // 加载数据
    load: function () {
      var _self = this;
      var success = function () {
        _self.onLoadSuccess.apply(_self, arguments);
        _self.initDocumentLoadedData.apply(_self, arguments);
      };
      var failure = function () {
        _self.onLoadFailure.apply(_self, arguments);
      };
      var loadActionId = _self.getLoadDataActionId();
      var extras = _self.getExtras();
      var urlParams = {
        dms_id: _self.dmsId,
        ac_id: loadActionId
      };
      var data = {
        extras: extras,
        formUuid: _self.formUuid,
        dataUuid: _self.dataUuid,
        docExcRecordUuid: _self.docExcRecordUuid
      };

      var options = {
        urlParams: urlParams,
        data: data,
        success: success,
        failure: failure,
        ui: _self
      };
      // 准备加载数据
      _self.prepareLoad(options);
      // 单据操作派发处理
      appContext.executeJsModule(DmsActionDispatcher, options);
    },
    // 准备加载数据
    prepareLoad: function (loadOptions) {},
    // 初始化文档加载的数据
    initDocumentLoadedData: function (result) {
      var _self = this;
      _self.documentData = result;
      _self.actions = _self._wrapperActions(result.actions);
      _self.dyFormData = result.dyFormData;

      if (_self.dyformDto != '') {
        _self.dyFormData.formDefinition = _self.compareFields(result.dyFormData, _self.dyformDto, _self.configDto);
        _self.dyFormData.formDefinition.uuid = result.dyFormData.formUuid;
      }

      if (result.docExchangeRecord) {
        _self.dyFormData.formDatas = _.cloneDeep(this.getFormDatas(result, _self.dyFormData.formDefinition.fields));
      }

      if (_self.dyFormData.formUuid && _self.configDto.exchangeType == '0') {
        _self.initDyform();
      } else {
        _self.initFileUploadForm(function () {
          _self.createActionButtons();
        });
      }
    },

    compareFields: function (dyFormData) {
      var _self = this;
      var definitionJson = JSON.parse(_self.dyformDto.definitionJson);
      var formDefinition = JSON.parse(dyFormData.formDefinition);
      var fields = definitionJson.fields;
      console.log(_self.configDto);
      if (formDefinition.defaultFormData) {
        definitionJson.defaultFormData = formDefinition.defaultFormData;
      }
      for (var i in fields) {
        if (i.indexOf('ex_doc_') == -1) {
          fields[i] = formDefinition.fields[i];
          continue;
        }
        var field = i.split('ex_doc_')[1];
        var valueCheckbox = ['docEncryptionLevel', 'docUrgeLevel', 'isNeedSign', 'signTimeLimit', 'isNeedFeedback', 'feedbackTimeLimit']; // 复选框，选中值为1的放一类
        if (_self.configDto.businessCategoryUuid != '' && field == 'toUserIds') {
          fields[i].filterCondition = 'otherParams://{categoryId:"' + _self.configDto.businessCategoryUuid + '",showOrgUser:false}';
        }

        if (field == 'fromUnitId') {
          $.ajax({
            url: ctx + '/api/dms/doc/exc/config/queryBusinessCategorOrgs',
            type: 'get',
            data: {
              categoryUuid: _self.configDto.businessCategoryUuid
            },
            async: false,
            success: function (res) {
              if (res.code == 0) {
                var optionSet = {};
                $.each(res.data, function (pIndex, pItem) {
                  optionSet[pItem.id] = pItem.text;
                });
                fields[i].optionSet = optionSet;
              }
            }
          });
        }

        if (field == 'fromUserId') {
          fields[i].defaultValue = SpringSecurityUtils.getCurrentUserName();
        }
        if (_self.configDto.businessCategoryUuid == '' && (field == 'fromUnitId' || field == 'fromUserId' || field == 'toUserIds')) {
          fields[i].showType = '5';
        } else if (_self.configDto[field] != '1' && valueCheckbox.indexOf(field) > -1) {
          fields[i].showType = '5';
        } else if (field == 'isNeedSign' || field == 'isNeedFeedback') {
          var fieldName = field == 'isNeedSign' ? 'docSign' : 'docFeedback';
          var fieldName2 = field == 'isNeedSign' ? 'defaultSign' : 'defaultFeedback';
          if (_self.configDto[field] != '1') {
            fields[i].showType = '5';
          } else if (_self.configDto[field] == '1' && _self.configDto[fieldName] != '1') {
            fields[i].showType = '5';
            fields[i].defaultValue = '1';
          } else if (_self.configDto[field] == '1' && _self.configDto[fieldName] == '1') {
            fields[i].showType = '1';
            if (_self.configDto[fieldName2] == '1') {
              fields[i].defaultValue = String(_self.configDto[fieldName2]);
            } else {
              fields[i].defaultValue = '';
            }
          }
        } else if (field == 'notifyTypes') {
          if (!_self.configDto[field] || _self.configDto[field].length == 0) {
            fields[i].showType = '5';
          } else {
            var obj = {
              IM: '在线消息|2',
              SMS: '短信|0',
              MAIL: '邮件|1'
            };

            var optionSet = _self.configDto[field].split(';');
            fields[i].optionSet = [];
            $.each(optionSet, function (oIndex, oItem) {
              var nArr = obj[oItem].split('|');
              fields[i].optionSet.push({
                name: nArr[0],
                value: nArr[1]
              });
            });

            var tDefaultValue = [];
            var defaultNotifyTypes = _self.configDto.defaultNotifyTypes ? _self.configDto.defaultNotifyTypes.split(';') : [];
            $.each(defaultNotifyTypes, function (index, item) {
              var newItem = obj[item].split('|');
              tDefaultValue.push(newItem[1]);
            });
            fields[i].defaultValue = tDefaultValue.join(';');
          }
        } else if (field == 'docEncryptionLevel') {
          var defaultEncrypttionLevel = _self.configDto.defaultEncryptionLevel ? _self.configDto.defaultEncryptionLevel.toString() : '';
          fields[i].defaultValue = defaultEncrypttionLevel;
        } else if (field == 'docUrgeLevel') {
          var defaultUrgeLevel = _self.configDto.defaultUrgeLevel ? _self.configDto.defaultUrgeLevel.toString() : '';
          fields[i].defaultValue = defaultUrgeLevel;
        }
      }
      return definitionJson;
    },

    getFormDatas: function (result, fields) {
      var datas = {};
      var obj = {
        IM: '2',
        SMS: '0',
        MAIL: '1'
      };

      for (var i in fields) {
        if (i.indexOf('ex_doc_') > -1) {
          var field = i.split('ex_doc_')[1];
          if (field == 'notifyTypes') {
            var notifyWays = result.docExchangeRecord['notifyWays'];
            var notifyTypes = [];
            $.each(notifyWays, function (index, item) {
              notifyTypes.push(obj[item]);
            });
            datas[i] = notifyTypes.join(';');
          } else if (field == 'fromUserId') {
            datas[i] = result.docExchangeRecord['userName'];
          } else {
            datas[i] =
              result.docExchangeRecord[field] == undefined || result.docExchangeRecord[field] == null
                ? ''
                : result.docExchangeRecord[field];
          }
        }
      }

      var formDatas = result.dyFormData.formDatas;
      for (var j in formDatas) {
        formDatas[j][0] = _.assign(formDatas[j][0], datas);
      }
      return formDatas;
    },

    initFileUploadForm: function (successCallback) {
      var $table = $('<table>').append(
        $('<tr>').append(
          $('<td>', {
            class: 'label-td'
          }).append('附件'),
          $('<td>').append(
            $('<div>', {
              id: 'docExchangeFileUploadContainer'
            })
          )
        )
      );
      $(this.dyformSelector).append($table);
      this.fileupload = new WellFileUpload('docExchangeFileUploadContainer');
      var dbFiles = [];
      if (this.documentData.docExchangeRecord && this.documentData.docExchangeRecord.fileUuids) {
        var fileUuidArr = this.documentData.docExchangeRecord.fileUuids.split('/');
        var fileNameArr = this.documentData.docExchangeRecord.fileNames.split('/');
        for (var i = 0; i < fileUuidArr.length; i++) {
          dbFiles.push({
            fileID: fileUuidArr[i],
            fileName: fileNameArr[i]
          });
        }
      }
      var readOnly = this.documentData.docExchangeRecord && this.documentData.docExchangeRecord.recordStatus != 0;
      if (readOnly) {
        this.fileupload.initAllowUploadDeleteDownload(false, false, true);
      }
      this.fileupload.init(readOnly, $('#docExchangeFileUploadContainer'), false, true, dbFiles);
      if ($.isFunction(successCallback)) {
        successCallback();
      }
    },

    // 获取文档数据
    getDocumentData: function () {
      return this.documentData || {};
    },
    // 获取文档额外数据
    getExtras: function () {
      var _self = this;
      var docData = _self.getDocumentData();
      var extras = docData.extras;
      if (extras == null) {
        extras = {};
        var extraParams = _self.options.extraParams;
        for (var p in extraParams) {
          extras[p] = extraParams[p];
        }
        docData.extras = extras;
      }
      return extras;
    },
    // 获取文档额外数据
    getExtra: function (key) {
      return this.getExtras[key];
    },
    // 设置文档额外数据
    setExtra: function (key, value) {
      this.getExtras()[key] = value;
    },
    // 包装操作对象，将properties属性的key、value合并到action中
    _wrapperActions: function (actions) {
      if (!actions) {
        return actions;
      }

      $.each(actions, function (i, action) {
        var properties = action.properties;
        for (var p in properties) {
          // 名称不复制
          if (p === 'name') {
            continue;
          }
          action[p] = properties[p];
        }
      });
      return actions;
    },
    // 初始化表单数据
    initDyform: function () {
      var _self = this;
      var dyFormData = _self.dyFormData;
      var acId = _self.options.acId;
      var displayAsLabel = _self.documentData.displayAsLabel;

      // 已发送状态表单设置只读
      if (_self.options.recordDto && JSON.parse(_self.options.recordDto).recordStatus !== 0) {
        displayAsLabel = true;

        if (_self.options.configDto.dmsDocExchangeDyformUuid == '' || _self.options.configDto.dmsDocExchangeDyformUuid == null) {
          $('.dyform.doc_exchange_container').find('input').attr('disabled', 'disabled');
          $('.dyform.doc_exchange_container').find('#ex_doc_docEncryptionLevel').wellSelect('disable', true);
          $('.dyform.doc_exchange_container').find('#label_ex_doc_toUserIds').attr('readonly', true);
        }
      }

      try {
        var dyformOptions = {
          renderTo: _self.dyformSelector,
          formData: dyFormData,
          displayAsLabel: displayAsLabel,
          success: function () {
            _self.dyform = this;
            _self.createActionButtons();
            _self.onDyformInitSuccess.apply(_self, arguments);
            // 已过时
            _self.onInitDyformSuccess.apply(_self, arguments);

            // 交换业务配置业务通讯录时，发件表单的“发件单位”字段为必填，否则以文本显示当前发件人的单位文本
            var $formUuidCtl = $.ControlManager.getCtl('ex_doc_fromUnitId');
            if ($formUuidCtl) {
              $formUuidCtl.setRequired(true);
              if (0 == $('#wellSelect_ex_doc_fromUnitId').find('.well-select-dropdown-list').find('li').length) {
                $formUuidCtl.setDisplayAsLabel();
                var unitName = SpringSecurityUtils.getCurrentUserUnitName();
                $formUuidCtl.setValue(unitName || '');
              }
              if (1 == $('#wellSelect_ex_doc_fromUnitId').find('.well-select-dropdown-list').find('li').length) {
                $('#wellSelect_ex_doc_fromUnitId').find('.well-select-dropdown-list').find('li').eq(0).trigger('click');
                $formUuidCtl.setDisplayAsLabel();
              }
              // 发件表单的“收件单位”字段 默认类型为业务通讯录
              if (_self.configDto.businessCategoryUuid) {
                var $toUserUuidCtl = $.ControlManager.getCtl('ex_doc_toUserIds');
                $toUserUuidCtl.getOptions().defaultType = 'BusinessBook';
              }
            }
          },
          error: function () {
            _self.dyform = this;
            _self.onDyformInitFailure.apply(_self, arguments);
            // 已过时
            _self.onInitDyformFailure.apply(_self, arguments);
          }
        };
        // 准备初始化表单
        _self.prepareInitDyform(dyformOptions);
        _self.dyform = new DyformExplain($(_self.dyformSelector), dyformOptions);
        //如果是复制操作事件，则需要清掉dyFormData的UUID, 以及对应的从表的dataUuid
        if ('btn_list_view_copy' == acId) {
          $(_self.dyformSelector).dyform('reFillFormData', true);
        }
      } catch (e) {
        appModal.error('表单解析失败： ' + e);
        throw e;
      }
    },
    // 表单初始化成功回调
    onDyformInitSuccess: function () {
      var _self = this;
      // 输入的动态表单值
      var extras = _self.getExtras();
      for (var key in extras) {
        if (key.length > 8 && key.substring(0, 8) == 'ep_dyfs_') {
          var fieldName = key.substring(8);
          var fieldVal = extras[key];
          _self.dyform.setFieldValue(fieldName, fieldVal);
        }
      }

      if (_self.options.isReceiver) {
        _self.initRelationDocTable();
      }

      // 填充默认值
      var defaultValue = $('#dms_defaultValue').val();
      if (defaultValue) {
        defaultValue = JSON.parse(defaultValue);

        for (var ctl in defaultValue) {
          var val = defaultValue[ctl];
          $.ControlManager[ctl] && $.ControlManager[ctl].control.setValue(val);
        }
      }
    },

    initRelationDocTable: function () {
      var _self = this;

      function createRelationDocTable(detailResult, options) {
        var content = '';
        // 标题
        content += '<h3 class="doc-detail-dialog-header">';
        content += '  相关文档';
        content += '  <div class="w-tooltip">';
        content += '    <i class="iconfont icon-ptkj-tishishuoming"></i>';
        content += '    <div class="w-tooltip-content">';
        content += '      对本文档进行处理或流转的相关文档';
        content += '    </div>';
        content += '  </div>';

        // “发件单位可查看相关文档” 按钮
        var configDto = JSON.parse(options.configDto);
        if (configDto.processView && configDto.refuseToView) {
          var recordDto = JSON.parse(options.recordDto);
          var refuseToView = recordDto.refuseToView || window.refuseToView;
          delete window.refuseToView;

          content += '  <div class="inline-block float-right mr-lg">';
          content += '    <span class="text-light mr-sm">发件单位可查看相关文档</span>';
          content += '    <input type="hidden" id="refuseToView" name="refuseToViewSwitch" value="' + refuseToView + '">';
          content += '    <div id="refuseToViewSwitch" class="switch-wrap ' + (refuseToView ? '' : 'active') + '">';
          content += '      <span class="switch-text switch-open">开启</span>';
          content += '      <span class="switch-radio"></span>';
          content += '      <span class="switch-text switch-close">关闭</span>';
          content += '    </div>';
          content += '  </div>';
        }

        content += '</h3>';

        // 表格
        content += '<table class="doc-detail-dialog-table">';
        content += '  <tbody>';

        for (var i = 0; i < detailResult.length; i++) {
          var item = detailResult[i];
          content += '    <tr>';
          content += '      <td>' + item.processingMethod + '</td>';
          content += '      <td>';
          content += StringUtils.format('<a target="_blank" href="${href}">${docTitle}</a>', {
            href: item.docLink,
            docTitle: item.docTitle
          });
          content += '        <span class="text-light">(' + item.createTime + ')</span>';
          content += '        </a>';
          content += '      </td>';
          content += '    </tr>';
        }

        content += '  </tbody>';
        content += '</table>';

        $('#tab_base_info').append(content);
        bindRefuseToViewEvent(options);
      }

      function bindRefuseToViewEvent(options) {
        $('#refuseToViewSwitch', '#tab_base_info').click(function () {
          var $this = $(this);
          var refuseToView = $this.hasClass('active') ? 1 : 0;

          server.JDS.call({
            async: false,
            service: 'dmsDocExchangeRecordService.updateRefuseToView',
            data: [options.docExchangeRecordUuid, refuseToView],
            success: function (res) {
              $this.toggleClass('active');
              if (refuseToView) {
                appModal.success('已拒绝发件单位查阅相关文档');
              } else {
                appModal.success('已同意发件单位查阅相关文档');
              }
            }
          });
        });
      }

      server.JDS.call({
        service: 'docExchangerFacadeService.listRelatedDocByRecordUuid',
        async: false,
        data: [_self.options.docExchangeRecordUuid],
        success: function (res) {
          if (res.code === 0 && res.data.length) {
            createRelationDocTable(res.data, _self.options);
          }
        }
      });
    },

    // 表单初始化失败回调
    onDyformInitFailure: function () {},
    // 创建操作按钮
    createActionButtons: function () {
      var _self = this;
      var actions = _self.getActions();
      _self.actionMap = {};
      // 生成按钮
      // var button = '<button btnId="{0}" name="{1}" class="btn btn-primary btn-minier smaller {3}">{2}</button>';
      // 操作按钮占位符
      var toolbarPlaceholder = _self.options.toolbarPlaceholder;
      var buttonHtml = new StringBuilder();

      $.each(actions, function (i, action) {
        _self.actionMap[action.id] = action;
        var btnId = action.id;
        var name = action.id;
        var text = action.name;
        var cssClass = action.cssClass;

        // sb.appendFormat(button, btnId, name, text, cssClass);
        if (!action.btnLib) {
          action.btnLib = _createdBtnLibObj(action.id);
        }

        if (action.btnLib) {
          if ($.inArray(action.btnLib.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
            if (action.btnLib.iconInfo) {
              buttonHtml.appendFormat(
                '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}" id="{6}"><i class="{4}"></i>{5}</button>',
                action.btnLib.btnColor,
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.code,
                action.btnLib.iconInfo.fileIDs,
                text,
                btnId
              );
            } else {
              buttonHtml.appendFormat(
                '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}" id="{5}">{4}</button>',
                action.btnLib.btnColor,
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.code,
                text,
                btnId
              );
            }
          } else {
            if (action.btnLib.btnInfo.icon) {
              buttonHtml.appendFormat(
                '<button type="button" class="well-btn {0} {1} btn_class_{2}" id="{5}"><i class="{3}"></i>{4}</button>',
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.code,
                action.btnLib.btnInfo.icon,
                action.btnLib.btnInfo.text,
                btnId
              );
            } else {
              buttonHtml.appendFormat(
                '<button type="button" class="well-btn {0} {1} btn_class_{2}" id="{4}">{3}</button>',
                action.btnLib.btnInfo['class'],
                action.btnLib.btnSize,
                action.code,
                action.btnLib.btnInfo.text,
                btnId
              );
            }
          }
        }
      });
      $(toolbarPlaceholder, _self.$element).append(buttonHtml.toString());

      // 绑定事件
      $(toolbarPlaceholder, _self.$element)
        .off()
        .on('click', 'button', function (e) {
          var btnId = $(this).attr('id');
          var action = _self.actionMap[btnId];
          if (action == null) {
            console.error('button of id[' + btnId + '] is not found');
            return;
          }
          _self.currentEvent = e;
          _self.performed(action);
          _self.currentEvent = null;
        });
    },
    // 准备初始化表单
    prepareInitDyform: function (dyformOptions) {},

    // 获取表单选择器
    getDyformSelector: function () {
      return this.dyformSelector;
    },
    // 获取表单对象
    getDyform: function () {
      return this.dyform;
    },
    // 获取表单数据
    getDyformData: function (fnCallback) {
      this.dyform.collectFormData(fnCallback, function (errorInfo) {
        appModal.error('表单数据收集失败[ + ' + JSON.stringify(errorInfo) + ']，无法提交数据！');
        throw e;
      });
    },
    // 验证表单数据
    validateDyformData: function () {
      return this.dyform.validateForm();
    },
    // 执行验证
    validate: function () {
      var _self = this;
      var result = false;
      try {
        if (_self.dyFormData.formUuid) {
          result = _self.validateDyformData();
        } else {
          if (_self.fileupload.files.length == 0) {
            appModal.error('请上传附件！');
          } else {
            result = true;
          }
        }
      } catch (e) {
        console.error(e);
        appModal.error('表单数据验证异常！');
      }
      return result;
    },
    // 执行操作
    performed: function (action) {
      var _self = this;
      var extras = _self.getExtras();

      var commit = function (dyFormData) {
        var urlParams = {
          dms_id: _self.dmsId,
          ac_id: action.id
        };
        // 验证处理
        if (action.validate === true && !_self.validate(action)) {
          return;
        }
        var data = {
          action: action,
          extras: extras,
          formUuid: _self.formUuid,
          dataUuid: _self.dataUuid
        };

        if (_self.dataType == 'FILE') {
          data.extras.files = [];
          for (var i = 0, len = _self.fileupload.files.length; i < len; i++) {
            data.extras.files.push({
              fileID: _self.fileupload.files[i].fileID,
              fileName: _self.fileupload.files[i].fileName
            });
          }
        }

        if (dyFormData) {
          data.dyFormData = dyFormData;
        }
        data.docExchangeRecord = _self.documentData.docExchangeRecord;

        var options = {
          urlParams: urlParams,
          data: data,
          appType: action.appType,
          appPath: action.appPath,
          appFunction: action,
          action: action.id,
          ui: _self,
          event: _self.currentEvent,
          appContext: appContext
        };
        // 准备执行操作
        _self.preparePerformed(options);
        // 默认派发器派发处理
        if (StringUtils.isNotBlank(action.appPath)) {
          appContext.getDispatcher().dispatch(options);
        } else {
          // 单据操作派发处理
          appContext.executeJsModule(DmsActionDispatcher, options);
        }
      };

      if (_self.dyFormData.formUuid) {
        _self.getDyformData(function (dyFormData) {
          commit(dyFormData);
        });
      } else {
        commit();
      }
    },
    // 准备执行操作
    preparePerformed: function (performedOptions) {}
  });
  return DmsDocExchangeDocumentView;
});

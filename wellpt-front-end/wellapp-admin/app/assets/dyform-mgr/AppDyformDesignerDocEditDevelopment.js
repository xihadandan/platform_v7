define([
  'jquery',
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'docFields',
  'AppDyformDesignerDocUtils',
  'HtmlWidgetDevelopment',
  'AppDyformDesignerDocEditExtendDevelopment'
], function ($, constant, commons, server, appContext, appModal, docFields, DesignerUtils, HtmlWidgetDevelopment) {
  var JDS = server.JDS;

  var AppDyformDesignerDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 平台管理_公共资源_表单管理编辑组件二开
  commons.inherit(AppDyformDesignerDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      var uuid = Browser.getQueryString('uuid');
      var formUuid = Browser.getQueryString('formUuid');
      var docUuid = Browser.getQueryString('docUuid');
      getFormDefinition(formUuid, docUuid, uuid);
      var formType = 'C';

      // P:存储单据, V:展现单据, M: 手机单据 MST: 子单据(模板单据)
      _self.widget.element.find('.dyform-' + formType).removeClass('hide');

      function getFormDefinition(formUuid, docUuid, uuid) {
        if (!docUuid) {
          var obj = {};
          obj.blocks = {
            sendInfo: {
              blockTitle: '发件信息',
              blockCode: 'sendInfo',
              hide: false,
              blockAnchor: false
            }
          };

          obj.formTree = [
            {
              nodeType: 'block',
              icon: '/static/js/pt/img/ptkj-block.png',
              children: [
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '199',
                  name: '发件单位',
                  fieldName: 'ex_doc_fromUnitId'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '1',
                  name: '发件人',
                  fieldName: 'ex_doc_fromUserId'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '43',
                  name: '收件单位',
                  fieldName: 'ex_doc_toUserIds'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '199',
                  name: '文档密级',
                  fieldName: 'ex_doc_docEncryptionLevel'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '17',
                  name: '紧急程度',
                  fieldName: 'ex_doc_docUrgeLevel'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '18',
                  name: '是否需要签收',
                  fieldName: 'ex_doc_isNeedSign'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '30',
                  name: '签收时限',
                  fieldName: 'ex_doc_signTimeLimit'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '18',
                  name: '是否需要反馈',
                  fieldName: 'ex_doc_isNeedFeedback'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '30',
                  name: '反馈时限',
                  fieldName: 'ex_doc_feedbackTimeLimit'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '18',
                  name: '发件提醒',
                  fieldName: 'ex_doc_notifyTypes'
                },
                {
                  nodeType: 'field',
                  children: [],
                  inputMode: '1',
                  name: '发件时间',
                  fieldName: 'ex_doc_sendTime'
                }
              ],
              isLayout: true,
              isContainer: true,
              blockCode: 'sendInfo',
              symbol: 'sendInfo',
              name: '发件信息'
            }
          ];
          getConfigData(formUuid, docUuid, obj);
        } else {
          $.ajax({
            url: ctx + '/api/dms/doc/exc/dyform/getOne',
            type: 'get',
            data: {
              uuid: docUuid
            },
            success: function (res) {
              if (res.code == 0) {
                if (formUuid && res.data.dyformUuid != formUuid) {
                  getFormDefinition(formUuid);
                } else {
                  getConfigData(formUuid, docUuid, JSON.parse(res.data.definitionJson));
                }
              }
            }
          });
        }
      }

      function getConfigData(formUuid, docUuid, obj) {
        var defaultFields = getFields();
        var placeHolders = {
          199: {
            placeHolder: 'select',
            title: 'select'
          },
          1: {
            placeHolder: 'text',
            title: 'textInput'
          },
          43: {
            placeHolder: 'multiOrg',
            title: 'unit2'
          },
          17: {
            placeHolder: 'radio',
            title: 'radio'
          },
          18: {
            placeHolder: 'checkbox',
            title: 'checkBox'
          },
          30: {
            placeHolder: 'date',
            title: 'datePicker'
          }
        };

        var fields = {};
        $.ajax({
          url: ctx + '/api/dms/doc/exc/config/getOne',
          type: 'get',
          dataType: 'json',
          data: {
            uuid: uuid
          },
          success: function (res) {
            if (res.code == 0) {
              var data = res.data;
              var $table = '<table><tbody><tr><td blockcode="sendInfo" class="title" colspan="12">发件信息</td></tr>';

              // 发件单位、发件人、收件单位
              fields['ex_doc_fromUnitId'] = defaultFields['ex_doc_fromUnitId'];
              fields['ex_doc_fromUserId'] = defaultFields['ex_doc_fromUserId'];
              fields['ex_doc_toUserIds'] = defaultFields['ex_doc_toUserIds'];

              $table += getFieldTable(['ex_doc_fromUnitId', 'ex_doc_fromUserId', 'ex_doc_toUserIds'], fields, placeHolders);

              var arr1 = [];
              if (data.docEncryptionLevel == 1) {
                // 文档密级
                fields['ex_doc_docEncryptionLevel'] = defaultFields['ex_doc_docEncryptionLevel'];
                arr1.push('ex_doc_docEncryptionLevel');
              }
              if (data.docUrgeLevel == 1) {
                // 文档紧急程度
                fields['ex_doc_docUrgeLevel'] = defaultFields['ex_doc_docUrgeLevel'];
                arr1.push('ex_doc_docUrgeLevel');
              }
              $table += getFieldTable(arr1, fields, placeHolders);
              var arr2 = [];
              if (data.isNeedSign == 1) {
                // 是否需要签收
                fields['ex_doc_isNeedSign'] = defaultFields['ex_doc_isNeedSign'];
                arr2.push('ex_doc_isNeedSign');
              }
              if (data.signTimeLimit == 1) {
                // 签收时限
                fields['ex_doc_signTimeLimit'] = defaultFields['ex_doc_signTimeLimit'];
                arr2.push('ex_doc_signTimeLimit');
              }
              $table += getFieldTable(arr2, fields, placeHolders);
              var arr3 = [];
              if (data.isNeedFeedback == 1) {
                // 是否需要反馈
                fields['ex_doc_isNeedFeedback'] = defaultFields['ex_doc_isNeedFeedback'];
                arr3.push('ex_doc_isNeedFeedback');
              }
              if (data.feedbackTimeLimit == 1) {
                // 反馈时限
                fields['ex_doc_feedbackTimeLimit'] = defaultFields['ex_doc_feedbackTimeLimit'];
                arr3.push('ex_doc_feedbackTimeLimit');
              }
              $table += getFieldTable(arr3, fields, placeHolders);
              if (data.notifyTypes && data.notifyTypes.split(';').length > 0) {
                // 发件提醒
                fields['ex_doc_notifyTypes'] = defaultFields['ex_doc_notifyTypes'];

                fields['ex_doc_notifyTypes'].optionSet = getNotifyTypes(data, 'optionSet');
                // fields['ex_doc_notifyTypes'].defaultValue = getNotifyTypes(data, 'defaultValue');
                $table += getFieldTable(['ex_doc_notifyTypes'], fields, placeHolders);
              }

              fields['ex_doc_sendTime'] = defaultFields['ex_doc_sendTime'];
              $table += getFieldTable(['ex_doc_sendTime'], fields, placeHolders);

              $table += '</tbody></table>';

              if (docUuid) {
                var oldField = _.cloneDeep(obj.fields);
                for (var i in obj.fields) {
                  if (i.indexOf('ex_doc_') > -1) {
                    delete obj.fields[i];
                  }
                }
                obj.fields = _.assign(obj.fields, fields);
                addDyformTypeScript(formUuid, docUuid, obj, oldField);
              } else {
                obj.fields = fields;
                obj.html = $table;
                addDyformTypeScript(formUuid, docUuid, obj);
              }
            }
          }
        });
      }

      function getNotifyTypes(data, types) {
        var obj = {
          IM: '在线消息|2',
          SMS: '短信|0',
          MAIL: '邮件|1'
        };

        var notifyTypes = data.notifyTypes ? data.notifyTypes.split(';') : [];
        var optionSet = [];
        $.each(notifyTypes, function (index, item) {
          var newItem = obj[item].split('|');
          optionSet.push({
            name: newItem[0],
            value: newItem[1]
          });
        });
        return optionSet;
      }

      function getFieldTable(list, fields, placeHolders) {
        var $table = '';
        for (var i = 0; i < list.length; i++) {
          var item = list[i];
          var newField = fields[item];
          if (i % 2 == 0) {
            $table += '<tr class="field">';
          }
          var colspan = '';
          if (i == list.length - 1 && i % 2 == 0) {
            colspan = 3;
          }

          $table +=
            '<td class="label-td">' +
            newField.displayName +
            '</td>' +
            '<td colspan="' +
            colspan +
            '" style="width:40%;">' +
            '<img class="value" inputmode="' +
            newField.inputMode +
            '" name="' +
            newField.name +
            '" src="/resources/pt/js/dyform/definition/ckeditor/plugins/control4' +
            placeHolders[newField.inputMode].placeHolder +
            '/images/placeHolder.jpg" title="' +
            placeHolders[newField.inputMode].title +
            '">' +
            '</td>';
          if (i % 2 == 1 || (i == list.length - 1 && i % 2 == 0)) {
            $table += '</tr>';
          }
        }
        return $table;
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDyformDesignerDevelopment;
});

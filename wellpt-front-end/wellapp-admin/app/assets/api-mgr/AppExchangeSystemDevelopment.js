define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'ztree',
  'multiOrg',
  'comboTree',
  'wSelect2',
  'HtmlWidgetDevelopment'
], function (constant, commons, server, appContext, appModal, ztree, multiOrg, comboTree, wSelect2, HtmlWidgetDevelopment) {
  var JDS = server.JDS;

  var AppExchangeSystemDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppExchangeSystemDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var self = this;
      var bean = {
        uuid: null,
        name: null,
        id: null,
        code: null,
        serverIp: null,
        unitId: null,
        unitName: null,
        sendCallbackUrl: null,
        routeCallbackUrl: null,
        receiveUrl: null,
        replyMsgUrl: null,
        ftpServerUrl: null,
        ftpUserName: null,
        ftpUserPassword: null,
        ftpFilePath: null,
        remark: null,
        typeId: null,
        typeId1: null,
        webServiceUrl: null,
        subjectDN: null,
        isCallBack: null,
        exchangeType: null
      };
      var setting = {
        async: {
          otherParam: {
            serviceName: 'exchangeDataTypeService',
            methodName: 'getViewAsTreeAsync',
            data: [-1, 'null']
          }
        }
      };
      var data1 = [
        { id: 'XZSP', text: '数据交换标准' },
        { id: 'CHANGXIANG', text: '畅享业务协同平台' },
        { id: 'SPXXGXPT', text: '审批信息共享平台平台' }
      ];
      var data2 = [
        { id: 'in', text: '内网' },
        { id: 'out', text: '外网' }
      ];
      $('#exchange_exchangeType').wSelect2({
        data: data1,
        valueField: 'exchange_exchangeType',
        remoteSearch: false
      });
      $('#exchange_sendPosition').wSelect2({
        data: data2,
        valueField: 'exchange_sendPosition',
        remoteSearch: false
      });
      $('#exchange_module_form').json2form(bean);
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        getModuleById(uuid);
      } else {
        self.getComboTree(setting);
      }

      function getModuleById(uuid) {
        JDS.call({
          service: 'exchangeDataConfigService.getBeanByUuid',
          data: [uuid],
          version: '',
          success: function (result) {
            bean = result.data;
            if (bean.unitId) {
              self.getUnitName([bean.unitId]);
            }
            if (bean.typeId != '') {
              init('typeId', 'typeName');
            }
            if (bean.typeId1 != '') {
              init('typeId1', 'typeName1');
            }

            var isEnableCa = bean.isEnableCa;
            if (isEnableCa == undefined || isEnableCa == null) {
              bean.isEnableCa = true;
            }
            $('#exchange_module_form').json2form(bean);
            self.getComboTree(setting);
            $('#exchange_exchangeType').trigger('change');
            $('#exchange_sendPosition').trigger('change');
          }
        });
      }

      $('#exchange_btn_save')
        .off()
        .on('click', function () {
          $('#exchange_module_form').form2json(bean);
          JDS.call({
            service: 'exchangeDataConfigService.saveBean',
            data: [bean],
            async: false,
            validate: true,
            version: '',
            success: function (result) {
              if (result.success) {
                appModal.success('保存成功！');
                appContext.getNavTabWidget().closeTab();
              }
            }
          });
        });

      $('#exchange_unitName')
        .off()
        .on('click', function () {
          $.unit2.open({
            title: '选择单位',
            labelField: 'exchange_unitName',
            valueField: 'exchange_unitId',
            type: 'MyUnit',
            selectTypes: 'all',
            valueFormat: 'justId',
            nameFormat: 'justName',
            multiple: false
          });
        });

      function init(id, name) {
        var temp = bean[id].split(','); //1;11
        var tids = '';
        var tnames = '';
        for (var i = 0; i < temp.length; i++) {
          var idname = temp[i];
          var temp1 = idname.split(';')[0];
          var temp2 = idname.split(';')[1];
          tids += temp1 + ';';
          tnames += temp2 + ';';
        }
        bean[id] = tids.substring(0, tids.lastIndexOf(';'));
        console.log(bean[id]);
        $('#' + name).val(tnames.substring(0, tnames.lastIndexOf(';')));
      }
    },
    getUnitName: function (ids) {
      $.ajax({
        type: 'POST',
        url: ctx + '/api/org/facade/getNameByOrgEleIds',
        dataType: 'json',
        data: {
          orgIds: ids
        },
        async: false,
        success: function (result) {
          if (result.code === 0) {
            var texts = [];
            $.each(result.data, function (k, v) {
              texts.push(v);
            });
            $('#exchange_unitName').val(texts.join(';'));
          } else {
            appModal.error(result.msg);
          }
        }
      });
    },
    getComboTree: function (setting) {
      $('#typeName').comboTree({
        labelField: 'typeName',
        valueField: 'typeId',
        treeSetting: setting,
        width: 220,
        height: 220,
        autoInitValue: false,
        autoCheckByValue: true
      });
      $('#typeName1').comboTree({
        labelField: 'typeName1',
        valueField: 'typeId1',
        treeSetting: setting,
        width: 220,
        height: 220,
        autoInitValue: false,
        autoCheckByValue: true
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppExchangeSystemDevelopment;
});

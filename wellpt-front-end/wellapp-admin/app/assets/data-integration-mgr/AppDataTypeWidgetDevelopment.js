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

  var AppDataTypeWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDataTypeWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        uuid: null,
        name: null,
        id: null,
        code: null,
        formId: null,
        unitId: null,
        retain: null,
        toSys: null,
        tableName: null,
        text: null,
        businessTypeId: null,
        synchronous: null,
        merge: null,
        showToUnit: null,
        businessId: null,
        reportLimit: null,
        receiveLimit: null,
        unitSysSourceName: null,
        unitSysSourceId: null
      };
      $('#type_module_form').json2form(bean);
      JDS.call({
        service: 'exchangeDataTypeService.getBusinessTypeList',
        async: false,
        version: '',
        success: function (result) {
          var businessTypeIdData = [];
          $.each(result.data, function (i) {
            businessTypeIdData.push({
              id: result.data[i].id,
              text: result.data[i].name
            });
          });
          $('#businessTypeId').wSelect2({
            data: businessTypeIdData,
            valueField: 'businessTypeId',
            remoteSearch: false
          });
        }
      });
      var self = this;
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        getModuleById(uuid);
      } else {
        $('#type_attach').hide();
        self.getComboTree();
      }

      function getModuleById(uuid) {
        JDS.call({
          service: 'exchangeDataTypeService.getBeanByUuid',
          data: [uuid],
          version: '',
          success: function (result) {
            bean = result.data;
            $('#formId').val(bean.formId);
            if (bean.name != '') {
              $('.fileDownLoad').text(bean.name + '.xml');
              $('#attach').show();
            }
            $('#type_module_form').json2form(bean);
            $('#businessTypeId').trigger('change');
            $('#type_unitName').val(bean.unitId);
            if (bean.businessId && bean.businessId.indexOf(':')) {
              $('#businessId').val(bean.businessId.split(':')[0]);
              $('#businessName').val(bean.businessId.split(':')[1]);
            }

            self.getComboTree();
            if (bean.unitId != '') {
              self.getUnitName(bean.unitId.split(';'));
            }
          }
        });
      }

      $('#type_btn_save')
        .off()
        .on('click', function () {
          if ($('#type_id').val() == '') {
            appModal.error('id不能为空');
            return false;
          }
          if (!uuid) {
            JDS.call({
              service: 'commonValidateService.checkExists',
              data: ['exchangeDataType', 'id', $('#type_id').val()],
              sync: false,
              version: '',
              success: function (result) {
                if (result.data) {
                  appModal.error('id已存在，请重新输入');
                  return false;
                } else {
                  saveData();
                }
              }
            });
          } else {
            saveData();
          }
        });

      function saveData() {
        if ($('#type_reportLimit').val() != '' && !$('#type_reportLimit').val().match('^[0-9]*[1-9][0-9]*$')) {
          appModal.error('上报时限请输入合法的数字');
          return false;
        }

        if ($('#type_receiveLimit').val() != '' && !$('#type_receiveLimit').val().match('^[0-9]*[1-9][0-9]*$')) {
          appModal.error('接收时限请输入合法的数字');
          return false;
        }
        var business = $('#businessId').val() + ':' + $('#businessName').val();
        bean.businessId = business;
        $('#type_module_form').form2json(bean);
        JDS.call({
          service: 'exchangeDataTypeService.saveBean',
          data: [bean],
          validate: true,
          version: '',
          success: function (result) {
            if (result.success) {
              appModal.success('保存成功!', function () {
                appContext.getNavTabWidget().closeTab();
              });
            }
          }
        });
      }

      $('.fileDownLoad')
        .off()
        .on('click', function () {
          if ($('#type_uuid').val() == '') {
            appModal.error('请先保存数据');
          } else {
            location.href = ctx + '/exchangedata/dataconfig/downLoadXml?uuid=' + $('#type_uuid').val();
          }
        });

      $('#type_unitName')
        .off()
        .on('click', function () {
          $.unit2.open({
            title: '选择目的单位',
            labelField: 'type_unitName',
            valueField: 'type_unitId',
            type: 'MyUnit',
            selectType: 'all',
            close: function (e) {}
          });
        });
    },
    getComboTree: function () {
      var otherParam1 = {
        serviceName: 'getViewDataService',
        methodName: 'getForms',
        data: [-1, '4']
      };
      var otherParam2 = {
        serviceName: 'exchangeDataTypeService',
        methodName: 'getBusinessHandleList'
      };
      var otherParam3 = {
        serviceName: 'exchangeDataTypeService',
        methodName: 'getUnitSystemSourceList'
      };
      this.initOptions(otherParam1, 'formId', 'formName');
      this.initOptions(otherParam2, 'businessId', 'businessName');
      this.initOptions(otherParam3, 'unitSysSourceId', 'unitSysSourceName');
    },
    initOptions: function (otherParam, id, name) {
      var setting = {
        async: {
          otherParam: otherParam
        },
        check: {
          enable: true,
          chkStyle: 'radio'
        }
      };
      $('#' + name).comboTree({
        labelField: name,
        valueField: id,
        treeSetting: setting,
        width: 220,
        height: 220,
        autoInitValue: false,
        autoCheckByValue: true
      });
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
            $('#type_unitName').val(texts.join(';'));
          } else {
            appModal.error(result.msg);
          }
        }
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDataTypeWidgetDevelopment;
});

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
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  var AppRouteWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppRouteWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var reg = new RegExp('^[0-9]*$');
      var self = this;
      var bean = {
        uuid: null,
        name: null,
        id: null,
        code: null,
        fromTypeId: null,
        toType: null,
        toId: null,
        toField: null,
        transformId: null,
        retransmissionNum: null,
        interval: null,
        restrain: null
      };
      $('#route_module_form').json2form(bean);

      JDS.call({
        service: 'exchangeDataTypeService.getExDataTypeList',
        async: false,
        version: '',
        success: function (result) {
          var formTypeData = [];
          $.each(result.data, function (i) {
            formTypeData.push({
              id: result.data[i].id,
              text: result.data[i].name
            });
          });
          $('#fromTypeId')
            .wellSelect({
              data: formTypeData,
              valueField: 'fromTypeId',
              remoteSearch: false
            })
            .trigger('change');
        }
      });

      var setting = {
        async: {
          otherParam: {
            serviceName: 'exchangeDataTypeService',
            methodName: 'getViewAsTreeAsync',
            data: [getValueById('fromTypeId')]
          }
        }
      };

      var uuid = GetRequestParam().uuid;
      if (uuid) {
        getModuleById(uuid);
      } else {
        $('.toIdClass').hide();
        $('.toFieldClass').hide();
        self.getComboTree(setting);
      }

      function getModuleById(uuid) {
        JDS.call({
          service: 'exchangeRouteService.getBeanByUuid',
          data: [uuid],
          version: '',
          success: function (result) {
            bean = result.data;
            $('#route_module_form').json2form(bean);
            if (bean.toId != null && bean.toId != undefined && bean.toId != '') {
              self.getUnitName(bean.toId.split(';'));
            }

            if (bean.transformId == '') {
              $('#transformId').val('');
              $('#transformName').val('');
            } else {
              var tran = init(bean.transformId, 'transformName');
              if (tran != '-1') bean.transformId = tran.substring(1, tran.length);
            }

            $('#fromTypeId').wellSelect('val', bean.fromTypeId).trigger('change');

            if (bean.toType) {
              getTypeStatus(bean.toType);
              $("input[name='toType'][value='" + bean.toType + "']").attr('checked', true);
            }
            getFieldData(bean.fromTypeId);

            $('#toField').val(bean.toField).trigger('change');

            if (StringUtils.isNotBlank(getValueById('fromTypeId'))) {
              setting.async.otherParam.data.splice(0, 1, -1, getValueById('fromTypeId'));
            }
            self.getComboTree(setting);
          }
        });
      }

      $('.toType')
        .off()
        .on('click', function () {
          getTypeStatus($(this).val());
        });

      $('#fromTypeId')
        .off()
        .on('change', function () {
          getFieldData($('#fromTypeId').val());
        });
      $('#retransmissionNum')
        .off()
        .on('blur', function () {
          if (!reg.test($(this).val())) {
            appModal.error('重发次数只能输入数字');
            return false;
          }
        });
      $('#interval')
        .off()
        .on('blur', function () {
          if (!reg.test($(this).val())) {
            appModal.error('间隔秒数只能输入数字');
            return false;
          }
        });
      // 保存
      $('#route_btn_save')
        .off()
        .on('click', function () {
          $('#route_module_form').form2json(bean);
          if ($('#transformName').val() == '') {
            bean.transformId = '';
          }
          JDS.call({
            service: 'exchangeRouteService.saveBean',
            data: [bean],
            async: false,
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
        });

      $('#toName')
        .off()
        .on('click', function () {
          $.unit2.open({
            title: '选择目的单位',
            labelField: 'toName',
            valueField: 'toId',
            type: 'MyUnit',
            selectTypes: 'all',
            close: function (e) {}
          });
        });

      function getFieldData(val) {
        JDS.call({
          service: 'exchangeRouteService.getToFieldsOptionList',
          data: [val],
          version: '',
          success: function (result) {
            var toFieldData = [];
            if (result.data && result.data.length > 0) {
              $.each(result.data, function (i) {
                var newData = result.data[i];
                var id = (newData.isSubDy ? '' : 'main') + ':' + newData.dyName + ':' + newData.fieldName;
                toFieldData.push({
                  id: id,
                  text: result.data[i].fieldShow
                });
              });
              $('#toField').wSelect2({
                data: toFieldData,
                valueField: 'toField',
                remoteSearch: false
              });
            }
          }
        });
      }

      function init(value, name) {
        if (value.indexOf(';') == -1) {
          return '-1';
        }
        var temp = value.split(','); //1;11
        var tids = '';
        var tnames = '';
        for (var i = 0; i < temp.length; i++) {
          var idname = temp[i];
          var temp1 = idname.split(';')[0];
          var temp2 = idname.split(';')[1];
          tids += ';' + temp1;
          tnames += ';' + temp2;
        }
        $('#' + name).val(tnames.substring(1, tnames.length));
        return tids;
      }

      function getValueById(id) {
        return $('#' + id).val();
      }

      function getTypeStatus(val) {
        if (val == 'type1') {
          $('.toIdClass').show();
          $('.toFieldClass').hide();
        } else if (val == 'type2') {
          $('.toIdClass').hide();
          $('.toFieldClass').show();
        } else if (val == 'type3') {
          $('.toIdClass').hide();
          $('.toFieldClass').hide();
        }
      }
    },
    getComboTree: function (setting) {
      $('#transformName').comboTree({
        labelField: 'transformName',
        valueField: 'transformId',
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
            $('#toName').val(texts.join(';'));
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
  return AppRouteWidgetDevelopment;
});

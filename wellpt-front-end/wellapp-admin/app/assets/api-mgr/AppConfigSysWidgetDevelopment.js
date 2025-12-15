define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppConfigSysWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppConfigSysWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        uuid: '',
        systemCode: '',
        systemName: '',
        serviceConfigs: [],
        authorizeItems: []
      };
      var uuid = GetRequestParam().uuid;
      $('#system_config_form').json2form(bean);
      initServiceTable();
      initAuthorizeTable('#authroze_list');
      initAuthorizeTable('#unauthroze_list');

      if (uuid) {
        loadSystemConfigInfo(uuid);
        $('#btn_generate_token').show();
      } else {
        $('#btn_generate_token').hide();
      }
      function loadSystemConfigInfo(uuid) {
        JDS.call({
          service: 'apiOutSystemFacadeService.getByUuid',
          data: [uuid, true],
          version: '',
          success: function (result) {
            var bean = result.data;
            $('#system_config_form').json2form(bean);
            var serviceConfigs = bean['serviceConfigs'];
            for (var index = 0; index < serviceConfigs.length; index++) {
              var rowData = serviceConfigs[index];
              addTableRow($('#service_list'), rowData);
            }
            var authorizeItems = bean['authorizeItems'];
            for (var index = 0; index < authorizeItems.length; index++) {
              var rowData = authorizeItems[index];
              var $targetTable = rowData.isAuthorized ? $('#authroze_list') : $('#unauthroze_list');
              addTableRow($targetTable, rowData);
            }
          }
        });
      }

      function initServiceTable() {
        // 初始化服务表
        $('#service_list')
          .bootstrapTable('destroy')
          .bootstrapTable({
            data: [],
            idField: 'uuid',
            pagination: true,
            striped: false,
            showColumns: false,
            uniqueId: 'uuid',
            undefinedText: '',
            clickToSelect: true,
            columns: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                visible: false
              },
              {
                field: 'serviceName',
                title: '服务名称',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'serviceCode',
                title: '服务编码',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'serviceUrl',
                title: '服务地址',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'overtimeLimit',
                title: '超时时限',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'serviceAdapter',
                title: '服务适配器',
                editable: {
                  onblur: 'submit',
                  type: 'text',
                  mode: 'inline',
                  showbuttons: false
                }
              }
            ],
            onClickCell: function (field, value, row, $element) {
              if (field == 'serviceAdapter') {
                var div =
                  '<div class="form-group">' +
                  '<label for="systemName" class="col-sm-3 control-label">系统名称</label>' +
                  '<div class="col-sm-9">' +
                  '<input type="text" id="adapterName" name="adapterName" value="' +
                  value +
                  '">' +
                  '<input type="hidden" id="adapterId" name="adapterId">' +
                  '</div>' +
                  '</div>';
                appModal.dialog({
                  title: '适配器选择',
                  size: 'middle',
                  message: $(div),
                  shown: function () {
                    $('#adapterId').val(value);
                    $('#adapterName')
                      .wSelect2({
                        serviceName: 'apiOutSystemFacadeService',
                        queryMethod: 'queryApiAdapterClass',
                        valueField: 'adapterId',
                        labelField: 'adapterName',
                        width: '100%',
                        remoteSearch: false,
                        defaultBlank: true
                      })
                      .trigger('change')
                      .on('change', function () {
                        row.serviceAdapter = $('#adapterId').val();
                        $element.text($('#adapterId').val());
                      });
                  }
                });
              }
            }
          });
      }
      function initAuthorizeTable(id) {
        // 初始化 黑白名单表
        $(id)
          .bootstrapTable('destroy')
          .bootstrapTable({
            data: [],
            idField: 'uuid',
            striped: false,
            showColumns: false,
            uniqueId: 'uuid',
            undefinedText: '',
            clickToSelect: true,
            columns: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                visible: false
              },
              {
                field: 'pattern',
                title: '匹配地址',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'isAuthorized',
                title: '授权',
                visible: false
              }
            ]
          });
      }

      function addTableRow(ele, data) {
        // 插入数据
        ele.bootstrapTable('insertRow', { index: 0, row: data });
      }

      function delTableRow(ele, fields) {
        var rowids = ele.bootstrapTable('getSelections');
        if (rowids.length == 0) {
          appModal.error('请选择记录！');
          return;
        }
        appModal.confirm('确定删除记录？', function (result) {
          if (result) {
            var fieldsVal = [];
            for (var i = 0; i < rowids.length; i++) {
              fieldsVal.push(rowids[i][fields]);
            }
            ele.bootstrapTable('remove', {
              field: fields,
              values: fieldsVal
            });
          }
        });
      }

      $('#service_btn_add')
        .off()
        .on('click', function () {
          // 新增 -- 服务
          var data = {
            uuid: '',
            serviceName: '',
            serviceCode: '',
            serviceUrl: '',
            overtimeLimit: 60,
            serviceAdapter: ''
          };
          addTableRow($('#service_list'), data);
        });

      $('#service_btn_del')
        .off()
        .on('click', function () {
          delTableRow($('#service_list'), 'serviceName');
        });

      $('.authroze_table_add_btn')
        .off()
        .on('click', function () {
          // 新增 -- 黑/白名单
          var isAuthorized = $(this).attr('id').indexOf('unauthroze_') != 0;
          var $targetTable = !isAuthorized ? $('#unauthroze_list') : $('#authroze_list');
          var data = { pattern: '', isAuthorized: isAuthorized };
          addTableRow($targetTable, data);
        });

      $('.authroze_table_del_btn')
        .off()
        .on('click', function () {
          var $targetTable = $(this).attr('id').indexOf('unauthroze') != -1 ? $('#unauthroze_list') : $('#authroze_list');

          delTableRow($targetTable, 'pattern');
        });
      //token 生成
      $('#btn_generate_token')
        .off()
        .on('click', function () {
          JDS.call({
            service: 'apiOutSystemFacadeService.generateToken',
            data: [$('#uuid').val()],
            version: '',
            success: function (result) {
              if (result.data) {
                appModal.alert(result.data);
              }
            }
          });
        });

      $('#conf_sys_btn_save')
        .off()
        .on('click', function () {
          // 清空JSON
          $.common.json.clearJson(bean);
          // 收集表单数据
          $('#system_config_form').form2json(bean);
          var serviceData = $('#service_list').bootstrapTable('getData');
          bean.serviceConfigs = serviceData;
          var authItems = [];
          var authrozeData = $('#authroze_list').bootstrapTable('getData');
          var unauthrozeData = $('#unauthroze_list').bootstrapTable('getData');
          authItems = authItems.concat(authrozeData, unauthrozeData);
          bean.authorizeItems = authItems;
          JDS.call({
            service: 'apiOutSystemFacadeService.addSystemConfig',
            data: [bean],
            validate: true,
            version: '',
            success: function (result) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            }
          });
        });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppConfigSysWidgetDevelopment;
});

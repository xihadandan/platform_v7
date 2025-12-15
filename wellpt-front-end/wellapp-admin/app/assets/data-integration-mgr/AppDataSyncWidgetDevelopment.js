define(['constant', 'commons', 'server', 'appContext', 'appModal', 'comboTree', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  comboTree,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppDataSyncWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppDataSyncWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var obj_ = new Object();
      var _self = this;
      var bean = {
        uuid: null,
        name: null,
        id: null,
        code: null,
        type: null,
        tableCnName: null,
        tableEnName: null,
        definitionUuid: null,
        joinTable: null,
        whereStr: null,
        method: null,
        isUserUse: null,
        tenant: null,
        isEnable: null,
        isRelationTable: null,
        relationTable: null,
        notOut: null,
        notIn: null,
        jobId: null,
        jobIdIn: null,
        synchronousSourceFields: []
      };
      $('#data_form').json2form(bean);
      this.getJobDetails();

      var setting = {
        async: {
          otherParam: {
            serviceName: 'exchangeDataSynchronousService',
            methodName: 'getSynchronousSourceTablesByType',
            data: [-1, $('#type').val()]
          }
        },
        check: {
          enable: true
        },
        callback: {
          onClick: treeNodeOnClick()
        }
      };
      this.initComboTree(setting);

      $('#isRelationTable')
        .off()
        .on('click', function () {
          if ($(this).attr('checked') == 'checked') {
            $('#column_list').bootstrapTable('showColumn', 'foreignKeyTable');
          } else {
            $('#column_list').bootstrapTable('hideColumn', 'foreignKeyTable');
          }
        });

      //select框值变化时候调用
      $('#type')
        .wSelect2({
          data: [
            { id: 'table', text: '动态表单' },
            { id: 'entity', text: '实体表' },
            { id: 'other', text: '其他' }
          ],
          valueField: 'type',
          remoteSearch: false
        })
        .change(function (e) {
          var val = $(this).val();
          if (val == 'other') {
            $('.tableEnNameClass').show();
            _self.initTable(bean['synchronousSourceFields'], 'input', []);
          } else {
            $('.tableEnNameClass').hide();

            var setting = {
              async: {
                otherParam: {
                  data: [-1, val]
                }
              }
            };
            $('#tableCnName').comboTree('setParams', { treeSetting: setting });
          }
        });

      var uuid = GetRequestParam().uuid;
      if (uuid) {
        // 渲染表单
        _self.getDataById(uuid, bean, setting);
      } else {
        _self.initTable(bean.synchronousSourceFields, 'input', []);
      }

      function treeNodeOnClick(event, treeId, treeNode) {
        if (treeNode) {
          $('#tableCnName').val(treeNode.name);
          $('#definitionUuid').val(treeNode.id);
          $('#tableEnName').val(treeNode.data);
        }
        var temp = _self.getColumnsData($('#type').val(), $('#definitionUuid').val());
        if (temp && temp != '') {
          _self.initTable([], 'select', temp);
          var data_ = temp.split(';');
          for (var i = 0; i < data_.length; i++) {
            var fieldCnName = data_[i].split(':')[1];
            var fieldEnName = data_[i].split(':')[0];
            var dataType = obj_[data_[i].split(':')[0]];

            $('#column_list').bootstrapTable('insertRow', {
              index: 0,
              row: {
                uuid: i,
                fieldCnName: fieldCnName,
                fieldEnName: fieldEnName,
                dataType: dataType,
                isUserUse: 'true',
                foreignKeyTable: ''
              }
            });
          }
        }
      }

      // 新增列
      $('#btn_add_column')
        .off()
        .on('click', function () {
          $('#column_list').bootstrapTable('insertRow', {
            index: 0,
            row: {
              fieldCnName: '',
              fieldEnName: '',
              dataType: '',
              isUserUse: 'false',
              foreignKeyTable: ''
            }
          });
        });

      //删除列
      $('#btn_del_column')
        .off()
        .on('click', function () {
          var rowids = $('#column_list').bootstrapTable('getSelections');
          if (rowids.length == 0) {
            appModal.error('请选择记录!');
            return;
          } else {
            appModal.confirm('确定删除记录?', function (res) {
              if (res) {
                var fieldsVal = [];
                for (var i = 0; i < rowids.length; i++) {
                  fieldsVal.push(rowids[i].fieldCnName);
                }
                $('#column_list').bootstrapTable('remove', {
                  field: 'fieldCnName',
                  values: fieldsVal
                });
              }
            });
          }
        });

      // 保存
      $('#sync_btn_save')
        .off()
        .on('click', function () {
          $('#data_form').form2json(bean);
          if (bean.type == 'entity' && bean.definitionUuid == '') {
            appModal.error('请选择表中文名');
            return false;
          }
          _self.collectColumn(bean);
          JDS.call({
            service: 'exchangeDataSynchronousService.saveBean',
            data: [bean],
            async: false,
            validate: true,
            version: '',
            success: function (result) {
              // 保存成功刷新列表
              appModal.success('保存成功!', function () {
                appContext.getNavTabWidget().closeTab();
              });
            }
          });
        });
    },
    getColumnsData: function (val1, val2) {
      var str = [];
      JDS.call({
        service: 'exchangeDataSynchronousService.getColumnsData',
        data: [val1, val2],
        async: false,
        version: '',
        success: function (result) {
          var data = result.data;
          for (var i = 0; i < data.length; i++) {
            var ename = data[i]['fieldEnName'];
            var cname = data[i]['fieldCnName'];
            if (cname == undefined) {
              str.push({
                value: ename,
                text: ename
              });
            } else {
              str.push({
                value: ename,
                text: cname
              });
            }
          }
        }
      });
      return str;
    },
    initComboTree: function (setting) {
      $('#tableCnName').comboTree({
        labelField: 'tableCnName',
        valueField: 'definitionUuid',
        treeSetting: setting,
        width: 220,
        height: 220,
        autoInitValue: false,
        autoCheckByValue: true
      });
    },
    initColumn: function (selectType, data) {
      if (selectType == 'select') {
        var columns = [
          {
            checkbox: true
          },
          {
            field: 'fieldCnName',
            title: '字段名',
            editable: {
              type: 'select',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline',
              source: data
            }
          },
          {
            field: 'fieldEnName',
            title: '列字段',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'dataType',
            title: '数据类型',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'isUserUse',
            title: '用户使用',
            editable: {
              type: 'select',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline',
              source: [
                {
                  value: 'true',
                  text: '是'
                },
                {
                  value: 'false',
                  text: '否'
                }
              ]
            }
          },
          {
            field: 'foreignKeyTable',
            title: '对应外键表',
            visible: false,
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          }
        ];
      } else {
        var columns = [
          {
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'fieldCnName',
            title: '字段名',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'fieldEnName',
            title: '列字段',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'dataType',
            title: '数据类型',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'isUserUse',
            title: '用户使用',
            editable: {
              type: 'select',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline',
              source: [
                {
                  value: 'true',
                  text: '是'
                },
                {
                  value: 'false',
                  text: '否'
                }
              ]
            }
          },
          {
            field: 'foreignKeyTable',
            title: '对应外键表',
            visible: false,
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          }
        ];
      }
      return columns;
    },
    initTable: function (data, type, selectData) {
      var newData = [];
      for (var i = 0; i < data.length; i++) {
        newData.push({
          dataType: data[i].dataType,
          fieldCnName: data[i].fieldCnName,
          fieldEnName: data[i].fieldEnName,
          foreignKeyTable: data[i].foreignKeyTable,
          isUserUse: data[i].isUserUse,
          uuid: i
        });
      }
      var columns = this.initColumn(type, selectData);
      $('#column_list').bootstrapTable({
        data: newData,
        idField: 'uuid',
        pagination: true,
        striped: false,
        showColumns: false,
        uniqueId: 'uuid',
        width: 500,
        undefinedText: '',
        sortName: 'fieldEnName',
        sortable: true,
        sortOrder: 'asc',
        columns: columns
      });
    },
    collectColumn: function (bean) {
      var change1 = $('#column_list').bootstrapTable('getData');
      bean['synchronousSourceFields'] = change1;
    },
    getDataById: function (uuid, bean, setting) {
      var self = this;
      JDS.call({
        service: 'exchangeDataSynchronousService.getBeanByUuid',
        data: [uuid],
        version: '',
        success: function (result) {
          bean = result.data;
          $('#data_form').json2form(bean);
          $('#type').trigger('change');
          $('#jobId').trigger('change');
          $('#jobIdIn').trigger('change');
          if (bean['type'] == 'other') {
            $('.tableEnNameClass').show();
            self.initTable(bean.synchronousSourceFields, 'input', []);
          } else {
            $('.tableEnNameClass').hide();
            var datas = self.getColumnsData($('#type').val(), bean['definitionUuid']);
            var tableType = datas.length == 0 ? 'input' : 'select';
            self.initTable(bean.synchronousSourceFields, tableType, datas);
          }

          if (bean.isRelationTable == true) {
            $('#column_list').bootstrapTable('showColumn', 'foreignKeyTable');
          } else {
            $('#column_list').bootstrapTable('hideColumn', 'foreignKeyTable');
          }

          self.initComboTree(setting);
        }
      });
    },
    getJobDetails: function () {
      JDS.call({
        async: false,
        service: 'exchangeDataSynchronousService.getJobDetails',
        version: '',
        success: function (result) {
          var data = result.data;
          var jobData = [];
          for (var i = 0; i < data.length; i++) {
            jobData.push({
              id: data[i].id,
              text: data[i].name
            });
          }
          var filedList = ['jobId', 'jobIdIn'];
          for (var i = 0; i < filedList.length; i++) {
            $('#' + filedList[i]).wSelect2({
              data: jobData,
              valueField: filedList[i],
              remoteSearch: false
            });
          }
        }
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDataSyncWidgetDevelopment;
});

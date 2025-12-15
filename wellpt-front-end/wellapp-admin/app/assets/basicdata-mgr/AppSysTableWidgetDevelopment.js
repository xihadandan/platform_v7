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

  var AppSysTableWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppSysTableWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        uuid: null,
        tableName: null,
        fullEntityName: null,
        chineseName: null,
        code: null,
        remark: null,
        moduleName: null,
        moduleId: null,
        attributes: [],
        relationships: [],
        changedAttributes: [],
        changedRelationships: [],
        deletedRelationships: []
      };
      var validator = $.common.validation.validate('#system_table_form', 'systemTable');
      var uuid = GetRequestParam().uuid;

      if (uuid) {
        getSystemTableById(uuid);
      }
      $('#moduleId').wSelect2({
        valueField: 'moduleId',
        labelField: 'moduleName',
        remoteSearch: false,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData',
        params: {
          systemUnitId: SpringSecurityUtils.getCurrentUserUnitId()
        }
      });

      $('.system-table-list').each(function () {
        var id = $(this).attr('id');
        if (id == 'child_systemtable_list') {
          var columns = [
            {
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'uuid',
              visible: false
            },
            {
              field: 'attributeName',
              title: '列名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'fieldName',
              title: '字段名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'chineseName',
              title: '中文名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'remark',
              title: '备注',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'columnType',
              title: '数据类型',
              editable: {
                // 1:INTEGER;2:LONG;3:DOUBLE;4:DATE;5:STRING;6:BOOLEAN;7:CLOB
                type: 'select',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline',
                source: [
                  {
                    value: '1',
                    text: 'INTEGER'
                  },
                  {
                    value: '2',
                    text: 'LONG'
                  },
                  {
                    value: '3',
                    text: 'DOUBLE'
                  },
                  {
                    value: '4',
                    text: 'DATE'
                  },
                  {
                    value: '5',
                    text: 'STRING'
                  },
                  {
                    value: '6',
                    text: 'BOOLEAN'
                  },
                  {
                    value: '7',
                    text: 'CLOB'
                  }
                ]
              }
            },
            {
              field: 'columnAliases',
              title: '列别名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'isSynchronization',
              title: '是否同步',
              editable: {
                type: 'select',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline',
                source: [
                  {
                    value: 'true',
                    text: 'true'
                  },
                  {
                    value: 'false',
                    text: 'false'
                  }
                ]
              }
            },
            {
              field: 'dataDictionary',
              title: '数据字典',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'constant',
              title: '常量',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'isOrganizeSelectionBox',
              title: '组织选择框',
              editable: {
                type: 'select',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline',
                source: [
                  {
                    value: 'true',
                    text: 'true'
                  },
                  {
                    value: 'false',
                    text: 'false'
                  }
                ]
              }
            },
            {
              field: 'entityName',
              title: '所属类',
              visible: false
            }
          ];
        } else {
          var columns = [
            {
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'uuid',
              visible: false
            },
            {
              field: 'mainTableName',
              title: '主表名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'secondaryTableName',
              title: '从表名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'tableRelationship',
              title: '主从表关系',
              editable: {
                type: 'select',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline', // 1:一对一;2:一对多;3:多对一;4:多对多
                source: [
                  {
                    value: '1',
                    text: '一对一'
                  },
                  {
                    value: '2',
                    text: '一对多'
                  },
                  {
                    value: '2',
                    text: '多对一'
                  },
                  {
                    value: '2',
                    text: '多对多'
                  }
                ]
              }
            },
            {
              field: 'associatedAttributes',
              title: '关联属性',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            }
          ];
        }
        initTable($('#' + id), columns);
      });

      $('#sys_table_btn_save').on('click', function () {
        if (!validator.form()) {
          return false;
        }
        $.common.json.clearJson(bean);
        $('#system_table_form').form2json(bean);

        collectAttributesList(bean);
        collectRelationshipsList(bean);

        JDS.call({
          service: 'systemTableService.saveBean',
          data: [bean],
          async: false,
          validate: true,
          version: '',
          success: function (result) {
            if (result.data) {
              appModal.success('保存成功!', function () {
                appContext.getNavTabWidget().closeTab();
              });
            } else {
              appModal.error('该类不存在');
            }
          }
        });
      });

      $('#relationship_btn_add').click(function () {
        var data = {
          uuid: null,
          mainTableName: null,
          secondaryTableName: null,
          tableRelationship: null,
          associatedAttributes: null
        };
        addTableRow($('#main_secondary_table_relationship_list'), data);
      });

      $('#relationship_btn_del').click(function () {
        var rowids = $('#main_secondary_table_relationship_list').bootstrapTable('getSelections');
        if (rowids.length == 0) {
          appModal.error('请选择记录!');
          return;
        }
        appModal.confirm('确定删除记录？', function () {
          var fieldsVal = [];
          for (var i = 0; i < rowids.length; i++) {
            fieldsVal.push(rowids[i].uuid);
          }
          $('#main_secondary_table_relationship_list').bootstrapTable('remove', {
            field: 'uuid',
            values: fieldsVal
          });
        });
      });

      function getSystemTableById(uuid) {
        JDS.call({
          service: 'systemTableService.getBeanById',
          data: [uuid],
          version: '',
          success: function (result) {
            bean = result.data;
            $('#system_table_form').json2form(bean);
            validator.form();
            toChildSystemTableList(bean);
            toChildRelationshipsList(bean);

            $('#moduleId').prop('readonly', bean.moduleId != null);
            $('#moduleId').trigger('change');
          }
        });
      }
      function collectAttributesList(bean) {
        var changes = $('#child_systemtable_list').bootstrapTable('getData');
        bean['attributes'] = [];
        bean['changedAttributes'] = changes;
      }
      function collectRelationshipsList(bean) {
        var changes = $('#main_secondary_table_relationship_list').bootstrapTable('getData');
        bean['relationships'] = [];
        bean['changedRelationships'] = changes;
      }
      function toChildRelationshipsList(bean) {
        var $relationshipsList = $('#main_secondary_table_relationship_list');
        var relationships = bean['relationships'];
        for (var index = 0; index < relationships.length; index++) {
          addTableRow($relationshipsList, relationships[index]);
        }
      }
      function toChildSystemTableList(bean) {
        var $attributesList = $('#child_systemtable_list');
        var attributes = bean['attributes'];
        for (var index = 0; index < attributes.length; index++) {
          addTableRow($attributesList, attributes[index]);
        }
      }
      function addTableRow(ele, data) {
        // 插入行数据
        ele.bootstrapTable('insertRow', { index: 0, row: data });
      }
      function initTable(ele, columns) {
        ele.bootstrapTable({
          data: [],
          striped: false,
          showColumns: false,
          width: 500,
          sortable: true,
          sortName: 'name',
          sortOrder: 'asc',
          pageNumber: '20',
          undefinedText: '',
          clickToSelect: true,
          columns: columns
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppSysTableWidgetDevelopment;
});

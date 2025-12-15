define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  //  平台应用_公共资源_脚本定义编辑二开
  var AppCdScriptDefinitionSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppCdScriptDefinitionSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid;
      if (GetRequestParam().uuid) {
        _uuid = GetRequestParam().uuid;
      }
      // 表单选择器
      var form_selector = '#cd_script_definiton_form';

      // CdScriptDefinition的VO类
      var formBean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        code: null, // 编号
        name: null, // 名称
        id: null, // ID
        type: null, // 类型
        content: null, // 内容
        remark: null, // 备注
        systemUnitId: null // 归属系统单位ID
      };

      var scriptTypes = SelectiveDatas.getItems('BASIC_DATA_SCRIPT_TYPE');
      var validator = $.common.validation.validate(form_selector, 'cdScriptDefinitionEntity', function (options) {
        options.ignore = '';
      });

      $(form_selector).json2form(formBean);

      var data = $.map(scriptTypes, function (item) {
        return {
          aceId: item.aceId,
          id: item.value,
          text: item.label
        };
      });

      $('#cd_script_definiton_type', form_selector).wSelect2({
        valueField: 'cd_script_definiton_type',
        searchable: true,
        data: data
      });

      function getScriptDefinition() {
        JDS.call({
          service: 'cdScriptDefinitionFacadeService.get',
          data: _uuid,
          success: function (result) {
            var bean = result.data;
            $(form_selector).json2form(bean);
            // 设置变量定义
            var variablesDefinition = JSON.parse(bean.variablesDefinition || '[]');
            initScriptVariableTable(variablesDefinition);
            // ID只读
            $('#cd_script_definiton_id', form_selector).prop('readonly', 'readonly');
            $('#cd_script_definiton_type', form_selector).wSelect2('val', bean.type);
            validator.form();
          }
        });
      }

      var $script_variable_table = $('#script_variable_table', _self.element);

      function initScriptVariableTable(bean) {
        var _data = bean || [];
        var _bean = {
          checked: false,
          uuid: '',
          text: '', //变量名称
          name: '', //变量名
          value: '', //变量值
          valueText: '', //变量值
          valueType: '', //值类型
          valueDataStoreId: '', //值信息的数据仓库ID
          valueValueColumn: '', //值信息的值字段
          valueTextColumn: '' //值信息的展示字段
        };
        // 定义添加，删除，上移，下移4按钮事件
        formBuilder.bootstrapTable.initTableTopButtonToolbar('script_variable_table', 'script_variable', _self.element, _bean);
        $script_variable_table.bootstrapTable('destroy').bootstrapTable({
          data: _data,
          idField: 'uuid',
          striped: true,
          toolbar: $('#div_script_variable_toolbar', _self.element),
          onEditableHidden: function (field, row, $el, reason) {
            $el.closest('table').bootstrapTable('resetView');
          },
          onEditableSave: function (field, row, oldValue, $el) {},
          onClickCell: function (field, value, rowData, $el) {
            if (field === 'valueText') {
              var container = $('<div>').html(_self.dlg_script_variable_value_definitionHtml());
              appModal.dialog({
                title: '变量值设置',
                size: 'middle',
                message: container,
                shown: function () {
                  $('input[name="valueType"]', container).on('change', function () {
                    var _val = $(this).val();
                    $('.form-group[data-type="' + _val + '"]').show();
                    if (_val === '1') {
                      $('.form-group[data-type="2"]').hide();
                    } else {
                      $('.form-group[data-type="1"]').hide();
                    }
                  });
                  if (rowData.valueType === '1') {
                    $('.valueTypeConst', container).prop('checked', true).trigger('change');
                    $("textarea[name='value1']", container).val(rowData.value);
                  } else {
                    $('.valueTypeDataStore', container).prop('checked', true).trigger('change');
                    $("input[name='value2']", container).val(rowData.value);
                    $("input[name='value2Text']", container).val(rowData.valueText);
                    $("input[name='dataStoreId']", container).val(rowData.valueDataStoreId);
                    $("input[name='valueColumn']", container).val(rowData.valueValueColumn);
                    $("input[name='textColumn']", container).val(rowData.valueTextColumn);
                  }

                  $('#cd_script_definiton_dataStoreName', container).wSelect2({
                    labelField: 'cd_script_definiton_dataStoreName',
                    valueField: 'cd_script_definiton_dataStoreId',
                    serviceName: 'viewComponentService',
                    queryMethod: 'loadSelectData',
                    remoteSearch: false
                  });

                  var columnSelect2Options = {
                    serviceName: 'viewComponentService',
                    queryMethod: 'loadColumnsSelectData',
                    params: function () {
                      return {
                        dataStoreId: $('#cd_script_definiton_dataStoreId', container).val()
                      };
                    },
                    remoteSearch: false
                  };

                  // 值字段
                  $('#cd_script_definiton_valueColumn', container).wSelect2(columnSelect2Options);
                  // 展示字段
                  $('#cd_script_definiton_textColumn', container).wSelect2(columnSelect2Options);

                  $('#cd_script_definiton_dataStoreName', container).on('change', function () {
                    // 值字段
                    $('#cd_script_definiton_valueColumn', container).wSelect2(columnSelect2Options);
                    // 展示字段
                    $('#cd_script_definiton_textColumn', container).wSelect2(columnSelect2Options);
                  });

                  $('#cd_script_definiton_valueColumn, #cd_script_definiton_textColumn', container).on('change', function () {
                    // 选择变量值
                    var valueSelect2Options = {
                      labelField: 'cd_script_definiton_value2Text',
                      valueField: 'cd_script_definiton_value2',
                      serviceName: 'select2DataStoreQueryService',
                      queryMethod: 'loadSelectData',
                      // selectionMethod : "loadSelectDataByIds",
                      params: {
                        dataStoreId: $('#cd_script_definiton_dataStoreId', container).val(),
                        idColumnIndex: $('#cd_script_definiton_valueColumn', container).val(),
                        textColumnIndex: $('#cd_script_definiton_textColumn', container).val()
                      },
                      remoteSearch: true,
                      defaultBlank: true
                    };
                    $('#cd_script_definiton_value2Text', container).wSelect2(valueSelect2Options);
                  });
                  // 选择变量值
                  $('#cd_script_definiton_value2Text', container).wSelect2({
                    labelField: 'cd_script_definiton_value2Text',
                    valueField: 'cd_script_definiton_value2',
                    serviceName: 'select2DataStoreQueryService',
                    queryMethod: 'loadSelectData',
                    // selectionMethod : "loadSelectDataByIds",
                    params: {
                      dataStoreId: $('#cd_script_definiton_dataStoreId', container).val(),
                      idColumnIndex: $('#cd_script_definiton_valueColumn', container).val(),
                      textColumnIndex: $('#cd_script_definiton_textColumn', container).val()
                    },
                    remoteSearch: true,
                    defaultBlank: true
                  });
                },
                callback: function () {
                  var valueType = $("input[name='valueType']:checked", container).val();
                  var newRowData = {
                    valueType: valueType
                  };
                  if (valueType === '1') {
                    newRowData.value = newRowData.valueText = $("textarea[name='value1']", container).val();
                    newRowData.valueDataStoreId = newRowData.valueValueColumn = newRowData.valueTextColumn = '';
                  } else {
                    newRowData.value = $("input[name='value2']", container).val();
                    newRowData.valueText = $("input[name='value2Text']", container).val();
                    newRowData.valueDataStoreId = $("input[name='dataStoreId']", container).val();
                    newRowData.valueValueColumn = $("input[name='valueColumn']", container).val();
                    newRowData.valueTextColumn = $("input[name='textColumn']", container).val();
                  }
                  var allData = $script_variable_table.bootstrapTable('getData');
                  $.each(allData, function (i, item) {
                    if (item.uuid === rowData.uuid) {
                      $script_variable_table.bootstrapTable('updateRow', i, $.extend(rowData, newRowData));
                      return false;
                    }
                  });
                }
              });
            }
          },
          columns: [
            {
              field: 'checked',
              checkbox: true,
              formatter: function (value) {
                if (value) {
                  return true;
                }
                return false;
              }
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false
            },
            {
              field: 'text',
              title: '变量名称',
              width: '33%',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline',
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入变量名称!';
                  }
                }
              }
            },
            {
              field: 'name',
              title: '变量名',
              width: '33%',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline',
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入变量名!';
                  }
                }
              }
            },
            {
              field: 'value',
              title: '变量值',
              visible: false
            },
            {
              field: 'valueText',
              title: '变量值',
              editable: {
                type: 'text',
                mode: 'inline',
                showbuttons: false,
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入变量值!';
                  }
                }
              },
              formatter: function (value, rowData) {
                if (value) {
                  return rowData.valueText + '(' + rowData.value + ')';
                } else {
                  return '';
                }
              }
            },
            {
              field: 'valueType',
              title: '值类型',
              visible: false
            },
            {
              field: 'valueDataStoreId',
              title: '值信息的数据仓库ID',
              visible: false
            },
            {
              field: 'valueValueColumn',
              title: '值信息的值字段',
              visible: false
            },
            {
              field: 'valueTextColumn',
              title: '值信息的展示字段',
              visible: false
            }
          ]
        });
      }
      if (_uuid) {
        getScriptDefinition();
      } else {
        // 生成ID
        $.common.idGenerator.generate('#cd_script_definiton_id', 'sc_');
        initScriptVariableTable();
      }

      // 保存脚本定义信息
      $('#btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $(form_selector).form2json(formBean);
        // 收集变量定义
        var variables = $script_variable_table.bootstrapTable('getData');
        console.log(variables);
        formBean.variablesDefinition = JSON.stringify(variables);
        JDS.call({
          service: 'cdScriptDefinitionFacadeService.save',
          data: formBean,
          success: function (result) {
            appModal.success('保存成功!', function () {
              appContext.getNavTabWidget().closeTab();
            });
          }
        });
      });
    },

    dlg_script_variable_value_definitionHtml: function () {
      return (
        '<div class="well-form well-dialog-form form-horizontal">' +
        '        <div class="form-group">' +
        '            <label class="well-form-label control-label">变量值来源</label>' +
        '            <div class="well-form-control">' +
        '                <label class="radio-inline">' +
        '                    <input type="radio" class="valueTypeConst" name="valueType" value="1" id="valueType1"> ' +
        '                     <label for="valueType1">常量' +
        '                     </label>' +
        '                </label>' +
        '                <label class="radio-inline">' +
        '                    <input type="radio" class="valueTypeDataStore" name="valueType" value="2" id="valueType2"> ' +
        '                    <label for="valueType2">数据仓库值' +
        '                    </label>' +
        '                </label>' +
        '            </div>' +
        '        </div>' +
        '        <div class="form-group" data-type="1">' +
        '            <label class="well-form-label control-label">变量值</label>' +
        '            <div class="well-form-control">' +
        '                <textarea type="text" class="form-control" name="value1"></textarea>' +
        '            </div>' +
        '        </div>' +
        '        <div class="form-group" data-type="2">' +
        '            <label for="cd_script_definiton_dataStoreName" class="well-form-label control-label">数据仓库</label>' +
        '            <div class="well-form-control">' +
        '                <input type="text" class="form-control" id="cd_script_definiton_dataStoreName" name="dataStoreName">' +
        '                <input type="hidden" class="form-control" id="cd_script_definiton_dataStoreId" name="dataStoreId">' +
        '            </div>' +
        '        </div>' +
        '        <div class="form-group" data-type="2">' +
        '            <label for="cd_script_definiton_valueColumn" class="well-form-label control-label">值字段</label>' +
        '            <div class="well-form-control">' +
        '                <input type="text" class="form-control" id="cd_script_definiton_valueColumn" name="valueColumn">' +
        '            </div>' +
        '        </div>' +
        '        <div class="form-group" data-type="2">' +
        '            <label for="cd_script_definiton_textColumn" class="well-form-label control-label">展示字段</label>' +
        '            <div class="well-form-control">' +
        '                <input type="text" class="form-control" id="cd_script_definiton_textColumn" name="textColumn">' +
        '            </div>' +
        '        </div>' +
        '        <div class="form-group" data-type="2">' +
        '            <label for="cd_script_definiton_value2Text" class="well-form-label control-label">选择变量值</label>' +
        '            <div class="well-form-control">' +
        '                <input type="text" class="form-control" id="cd_script_definiton_value2Text" name="value2Text">' +
        '                <input type="hidden" class="form-control" id="cd_script_definiton_value2" name="value2">' +
        '            </div>' +
        '        </div>' +
        '    </div>'
      );
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppCdScriptDefinitionSetDevelopment;
});

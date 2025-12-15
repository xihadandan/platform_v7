define([
  'appContext',
  'design_commons',
  'constant',
  'commons',
  'server',
  'bootbox',
  'formBuilder',
  'appModal',
  'select2',
  'wSelect2',
  'ace_code_editor',
  'ace_ext_language_tools',
  'ace_ext_zoom_win',
  'ace_ext_code_var_snippets'
], function (appContext, designCommons, constant, commons, server, bootbox, formBuilder, appModal) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var collectClass = 'w-configurer-option';
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };
  var checkRequire = function (propertyNames, options, $container) {
    for (var i = 0; i < propertyNames.length; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        if ('exportDataType' == propertyName || 'fileName' == propertyName || 'exportTypes' == propertyName) {
          appModal.error('导出配置的' + '' + title.replace('*', '') + '不允许为空！');
        } else {
          appModal.error('基础信息的' + '' + title.replace('*', '') + '不允许为空！');
        }
        return false;
      }
    }
    return true;
  };
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };
  var columnRowBean = {
    checked: false,
    uuid: '',
    header: '',
    name: '',
    dataType: '',
    renderer: {},
    width: '',
    hidden: '1',
    sortable: '0',
    keywordQuery: '0',
    editable: '0'
  };
  var roleRowBean = {
    checked: false,
    uuid: null,
    roleName: null,
    roleUuid: null,
    orgNames: null,
    orgIds: null,
    denyName: null,
    deny: null,
    permit: 'Y',
    permitName: '是'
  };
  var defaultButtons = [
    {
      checked: false,
      uuid: 'C8D5471903600001A09DF9C01A4E1387',
      code: 'btn_add',
      text: '新增',
      position: ['1'],
      group: '',
      cssStr: 'btn-bg-color',
      cssClass: 'btn-default ',
      eventManger: {},
      btnLib: {
        btnSize: '',
        iconInfo: null,
        btnInfo: {
          type: 'primary',
          type_name: '主操作按钮',
          class: '',
          status: [
            { class: '', text: '普通状态' },
            { class: 'hover', text: '鼠标移入状态' },
            { class: 'active', text: '点击状态' },
            { class: 'w-disable-btn', text: '禁用状态' }
          ]
        },
        btnColor: 'w-btn-primary'
      }
    },
    {
      checked: false,
      uuid: 'C8D5471925100001151EA6803270AA10',
      code: 'btn_add_children',
      text: '新增子夹',
      position: ['1'],
      group: '',
      cssStr: 'btn-bg-color',
      cssClass: 'btn-default ',
      eventManger: {},
      btnLib: {
        btnSize: '',
        iconInfo: null,
        btnInfo: {
          type: 'primary',
          type_name: '主操作按钮',
          class: '',
          status: [
            { class: '', text: '普通状态' },
            { class: 'hover', text: '鼠标移入状态' },
            { class: 'active', text: '点击状态' },
            { class: 'w-disable-btn', text: '禁用状态' }
          ]
        },
        btnColor: 'w-btn-primary'
      }
    },
    {
      checked: false,
      uuid: 'C8D54719D1F000018F1A1C4B6DE013FC',
      code: 'btn_check',
      text: '查看',
      position: ['3'],
      group: '',
      cssStr: 'btn-bg-color',
      cssClass: 'btn-default ',
      eventManger: {},
      btnLib: {
        btnSize: '',
        iconInfo: null,
        btnInfo: {
          type: 'line',
          type_name: '线框按钮',
          class: 'w-line-btn',
          status: [
            { class: '', text: '普通状态' },
            { class: 'hover', text: '鼠标移入状态' },
            { class: 'active', text: '点击状态' },
            { class: 'w-disable-btn', text: '禁用状态' }
          ]
        },
        btnColor: 'w-btn-primary'
      }
    },
    {
      checked: false,
      uuid: 'C8D54719DFB0000117791B883FC4BF60',
      code: 'btn_delete',
      text: '删除',
      position: ['1', '3'],
      group: '',
      cssStr: 'btn-bg-color',
      cssClass: 'btn-default ',
      eventManger: {},
      btnLib: {
        btnSize: '',
        iconInfo: null,
        btnInfo: {
          type: 'line',
          type_name: '线框按钮',
          class: 'w-line-btn',
          status: [
            { class: '', text: '普通状态' },
            { class: 'hover', text: '鼠标移入状态' },
            { class: 'active', text: '点击状态' },
            { class: 'w-disable-btn', text: '禁用状态' }
          ]
        },
        btnColor: 'w-btn-primary'
      }
    }
  ];

  var fieldsList = [
    { text: '名称', field: 'name', isChecked: false },
    { text: '编号', field: 'code', isChecked: false },
    { text: '类型', field: 'contentType', isChecked: false },
    { text: '备注', field: 'remark', isChecked: false },
    { text: '数据类型', field: 'dataType', isChecked: false },
    { text: '动态表单', field: 'formName', isChecked: false },
    { text: '文档标题字段', field: 'fileNameFieldName', isChecked: false },
    { text: '文档状态字段', field: 'fileStatusFieldName', isChecked: false },
    { text: '阅读人员字段', field: 'readFileFieldName', isChecked: false },
    { text: '编辑人员字段', field: 'editFileFieldName', isChecked: false },
    { text: '置顶状态', field: 'stick', isChecked: false },
    { text: '置顶状态字段', field: 'stickStatusFieldName', isChecked: false },
    { text: '置顶时间字段', field: 'stickTimeFieldName', isChecked: false },
    { text: '启用阅读记录', field: 'readRecord', isChecked: false },
    { text: '阅读记录人员字段', field: 'readRecordFieldName', isChecked: false },
    { text: '显示表单', field: 'displayFormName', isChecked: false },
    { text: '展示视图', field: 'listViewName', isChecked: false }
  ];

  var clearInputValue = function ($container) {
    $container.find('.w-configurer-option').each(function () {
      var $element = $(this);
      var type = $element.attr('type');
      if (type == 'text' || type == 'hidden') {
        $element.val('');
      } else if (type == 'checkbox' || type == 'radio') {
        $element.prop('checked', false);
      }
      $element.trigger('change');
    });
  };

  var loadColumnNames = function (alwaysLoad) {
    var dataStoreId = $('#dataStoreId').val();
    var dataStoreIdKey = 'data-' + dataStoreId;
    var nameSource = $('#dataStoreId').data(dataStoreIdKey);
    if (nameSource == null || typeof nameSource === 'undefined') {
      server.JDS.call({
        service: 'viewComponentService.getColumnsById',
        data: [dataStoreId],
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            nameSource = $.map(result.data, function (data) {
              return {
                value: data.columnIndex,
                text: data.columnIndex,
                dataType: data.dataType,
                title: data.title,
                id: data.columnIndex
              };
            });
          }
        }
      });
      $('#dataStoreId').data(dataStoreIdKey, nameSource);
    }
    // 必须套一个空数组来合并，不然每次会被 wselect2 初始化的时候给添加一个空字符值尽量
    return [].concat(nameSource);
  };

  var configurer = $.ui.component.BaseComponentConfigurer();
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    var _self = this;
    designCommons.setElementValue(configuration, $container, ['query', 'exportDataType']);
    // 数据源
    $('#dataStoreId', $container).wSelect2({
      serviceName: 'viewComponentService',
      queryMethod: 'loadSelectData',
      labelField: 'dataStoreName',
      valueField: 'dataStoreId',
      remoteSearch: false,
      params: {
        piUuid: _self.component.pageDesigner.getPiUuid()
      }
    });

    $('#dataStoreId', $container).change(function () {
      // 数据仓库变更时，相关配置项重置开关
      if ($('#resetConfigurationOnDataStoreChange', $container).attr('checked') != 'checked') {
        return;
      }
      var newColumnNames = loadColumnNames(true);
      var columns = $.map(newColumnNames, function (column) {
        var bean = $.extend({}, columnRowBean);
        bean.uuid = commons.UUID.createUUID();
        bean.header = column.title;
        bean.name = column.id;
        bean.dataType = column.dataType;
        return bean;
      });
      var $columnInfoTable = $('#table_column_info', $container);
      $columnInfoTable.bootstrapTable('removeAll').bootstrapTable('load', columns);

      $('#readMarkerField').val('').wSelect2({
        data: newColumnNames
      });

      $('#treeField').val('').wSelect2({
        data: newColumnNames
      });
      $('#parentIdField').val('').wSelect2({
        data: newColumnNames
      });
    });
    // 加载的JS模块
    if ($('#jsModule').val() == '') {
      $('#jsModule').val('AppFileLibraryListDevelopment').trigger('change');
    }
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'ListViewWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });
  };
  configurer.prototype.initTreeGrid = function (treeGrid, $container) {
    designCommons.setElementValue(treeGrid, $container);
    // 启用树形列表
    var $treeDefinition = $('.div_treegrid_definition', $container);
    $('#treeView', $container).prop('checked', true);
    $('#enableTreegrid', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $treeDefinition.show();
        } else {
          $treeDefinition.hide();
          clearInputValue($treeDefinition);
        }
      })
      .trigger('change');

    //树形未初始化配置的默认处理
    if (treeGrid == undefined || !treeGrid.enableTreegrid) {
      $('#expandIcon', $container).val('iconfont icon-ptkj-shixinjiantou-xia'); //默认展开图标
      $('#collapseIcon', $container).val('iconfont icon-ptkj-shixinjiantou-you'); //默认折叠图标
      $('#initTreeShowStyle', $container).val('COLLAPSE_ALL');
      $('#treeView', $container).val(true);
    }

    $('#parentIdField').wSelect2({
      data: loadColumnNames()
    });
    $('#treeNodeDisplayField').wSelect2({
      data: loadColumnNames()
    });

    $('#treeBuildType').wSelect2({
      data: (function () {
        return [
          {
            text: '父级字段构造树形列表',
            id: 'parentField'
          },
          {
            text: '分类多字段构造树形列表',
            id: 'multiField'
          }
        ];
      })()
    });
    $('#initTreeShowStyle').wSelect2({
      defaultBlank: false,
      data: (function () {
        return [
          {
            text: '默认全部展开',
            id: 'EXPAND_ALL'
          },
          {
            text: '默认全部折叠',
            id: 'COLLAPSE_ALL'
          },
          {
            text: '默认第一个节点展开',
            id: 'FIRST_EXPAND'
          }
        ];
      })()
    });

    $('#lastLevelDataParentTree').on('change', function () {
      if ($(this).is(':checked')) {
        $('.treegrid_parentField').show();
      } else {
        //父级字段选择清空
        $('#parentIdField').val('');
        $('#parentIdField').trigger('change');
        $('.treegrid_parentField').hide();
      }
    });

    $('#treeBuildType')
      .on('change', function () {
        var value = $(this).val();
        $('.treeBuildType').hide();
        if (value) {
          $('.treegrid_' + value).show();
        }

        if (value == 'multiField') {
          $('#lastLevelDataParentTree').trigger('change');
        }
      })
      .trigger('change');

    var treeNodes = treeGrid && treeGrid.treeNodes ? treeGrid.treeNodes : [];
    // 默认排序
    var $treeNodeTable = $('#table_tree_node_info', $container);
    //定义添加，删除，上移，下移4按钮事件
    var treeNodeBean = {
      name: '',
      nodeField: ''
    };
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_tree_node_info', 'tree_node', $container, treeNodeBean);

    // 列定义
    var $treeNodeTable = $('#table_tree_node_info', $container);
    $treeNodeTable.bootstrapTable('destroy').bootstrapTable({
      data: treeNodes,
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_tree_node_toolbar', $container),
      onEditableHidden: onEditHidden,
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: "名称<b style='color: red;'>*</b>",
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            savenochange: true,
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'nodeField',
          title: "分类字段<b style='color: red;'>*</b>",
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: loadColumnNames
          }
        },
        {
          field: 'nodeFieldDisplay',
          title: '分类显示字段',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: function () {
              return [{ value: '', text: '' }].concat(loadColumnNames());
            }
          }
        },
        {
          field: 'multiNodeData',
          title: '是否多分类<br>(通过<code>;</code>分割)',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        }
      ]
    });

    // 选择图标
    $('.iconSelectBtn', $container).on('click', function () {
      var _thiz = this;
      $.WCommonPictureLib.show({
        selectTypes: [3],
        confirm: function (data) {
          var fileIDs = data.fileIDs;
          $(_thiz).prev().prev().val(fileIDs);
          $(_thiz).prev().prev().trigger('iconClassChange');
        }
      });
    });

    $('.treeIconClass')
      .on('iconClassChange', function () {
        $(this).next().attr('class', $(this).val());
      })
      .trigger('iconClassChange');
  };
  configurer.prototype.initColumnData = function (columns, $container) {
    var columnsData = columns ? columns : [];
    var $columnInfoTable = $('#table_column_info', $container);
    $columnInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: columnsData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'idField' && row[field] == '1') {
          var data = $columnInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row != rowData) {
              rowData.idField = 0;
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        if (field == 'name') {
          var rowDatas = $columnInfoTable.bootstrapTable('getData');
          $.each(rowDatas, function (index, rowData) {
            if (row == rowData) {
              $.each(loadColumnNames(), function (index, val) {
                if (val.value == row.name) {
                  rowData.header = val.title;
                  rowData.dataType = val.dataType;
                }
              });
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_column_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'header',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名'
        },
        {
          field: 'dataType',
          title: '数据类型',
          visible: false
        },
        {
          field: 'width',
          title: '列宽',
          visible: false
        },
        {
          field: 'renderer',
          title: '渲染器',
          visible: false
        },
        {
          field: 'idField',
          title: '是否主键',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        },
        {
          field: 'sortable',
          title: '点击排序',
          visible: false
        },
        {
          field: 'hidden',
          title: '隐藏',
          formatter: function (value, row, index) {
            return designCommons.bootstrapTable.checkbox.formatter(value, 'hidden');
          },
          events: designCommons.bootstrapTable.checkbox.events
        },
        {
          field: 'keywordQuery',
          title: '参与关键字查询',
          visible: false
        }
      ]
    });
  };
  configurer.prototype.initColumnInfo = function (columns, $container) {
    // 列定义
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_column_info', 'column', $container, columnRowBean);
    this.initColumnData(columns, $container);
  };
  configurer.prototype.initRoleInfo = function (configuration, roles, $container) {
    var roleEditOptionValues = [];
    JDS.call({
      service: 'dmsRoleMgr.getAll',
      async: false,
      version: '',
      success: function (reuslt) {
        var data = reuslt.data;
        $.each(data, function (i, dmsRole) {
          roleEditOptionValues.push({
            value: dmsRole.uuid,
            text: dmsRole.name
          });
        });
      }
    });
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_role_info', 'role', $container, roleRowBean);
    var rolesData = roles ? roles : [];
    var $columnInfoTable = $('#table_role_info', $container);
    $columnInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: rolesData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'idField' && row[field] == '1') {
          var data = $columnInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row != rowData) {
              rowData.idField = 0;
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_role_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'uuid',
          visible: false
        },
        {
          field: 'roleUuid',
          title: '文件夹目录操作权限',
          editable: {
            type: 'select',
            showbuttons: true,
            onblur: 'submit',
            mode: 'inline',
            source: roleEditOptionValues
          }
        },
        {
          field: 'roleName',
          title: '文件夹目录操作权限UUID',
          visible: false
        },
        {
          field: 'orgNames',
          title: '人员',
          visible: false
        },
        {
          field: 'orgIds',
          title: '人员ID',
          visible: false
        },
        {
          field: 'permit',
          title: '允许',
          visible: false
        },
        {
          field: 'permitName',
          title: '允许真实值',
          visible: false
        },
        {
          field: 'deny',
          title: '拒绝',
          visible: false
        },
        {
          field: 'denyName',
          title: '拒绝真实值',
          visible: false
        }
      ],
      onClickCell: function (field, value, row, $element) {
        if (field == 'roleUuid') {
          $('.input-sm', $element).on('change', function () {
            var val = $(this).val();
            row['roleName'] = $(this)
              .find("option[value='" + val + "']")
              .text();
          });
        }
      }
    });
  };
  configurer.prototype.initButtonInfo = function (configuration, $container) {
    var buttonData = configuration.buttons ? configuration.buttons : defaultButtons;
    console.log(1, buttonData, JSON.stringify(buttonData));
    var piUuid = this.component.pageDesigner.getPiUuid();
    var system = appContext.getCurrentUserAppData().getSystem();
    var productUuid = system.productUuid;
    if (StringUtils.isNotBlank(system.piUuid)) {
      piUuid = system.piUuid;
    }
    // 按钮定义
    var $buttonInfoTable = $('#table_button_info', $container);

    var buttonRowBean = {
      checked: false,
      uuid: '',
      code: '',
      text: '',
      position: ['1'],
      group: '',
      cssStr: 'btn-bg-color',
      cssClass: 'btn-default'
    };
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_button_info', 'button', $container, buttonRowBean);

    //兼容旧的事件处理配置
    for (var i = 0, len = buttonData.length; i < len; i++) {
      if (!buttonData[i].eventManger) {
        buttonData[i].eventManger = {
          eventHandler: buttonData[i].eventHandler
        };
        if (buttonData[i].eventParams) {
          $.extend(buttonData[i].eventManger, buttonData[i].eventParams);
        }
      }
    }

    $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: buttonData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_button_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'text',
          title: '名称',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'code',
          title: '编码',
          width: 80,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            savenochange: true,
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入编码!';
              }
            }
          }
        },
        {
          field: 'position',
          title: '展示位置',
          width: 120,
          editable: {
            onblur: 'save',
            type: 'wCustomForm',
            renderParams: {
              columnTableData: this.getListViewTableColumnData()
            },
            placement: 'bottom',
            savenochange: true,
            source: [
              {
                value: '1',
                text: '表格头部'
              },
              {
                value: '2',
                text: '表格底部'
              },
              {
                value: '3',
                text: '行末'
              },
              {
                value: '4',
                text: '悬浮行上'
              }
            ],
            validate: function (value) {
              if (!value.length) {
                return '请选择按钮位置!';
              } else {
                var tip = '';
                $.each(value, function (i, item) {
                  if (item.split('-')[0] === '4' && !item.split('-')[1]) {
                    tip = '请选择按钮浮动列!';
                  }
                });
                if (tip) {
                  return tip;
                }
              }
            },
            value2input: designCommons.bootstrapTable.btnPosition.value2input,
            input2value: designCommons.bootstrapTable.btnPosition.input2value,
            value2display: designCommons.bootstrapTable.btnPosition.value2display,
            value2html: designCommons.bootstrapTable.btnPosition.value2html
          }
        },
        {
          field: 'group',
          title: '组别',
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit'
          }
        },
        {
          field: 'btnLib',
          title: '按钮库',
          width: 100,
          editable: {
            onblur: 'save',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.btnLib.value2input,
            input2value: designCommons.bootstrapTable.btnLib.input2value,
            value2display: designCommons.bootstrapTable.btnLib.value2display,
            value2html: designCommons.bootstrapTable.btnLib.value2html
          }
        },
        {
          field: 'resource',
          title: '资源',
          width: 100,
          editable: {
            mode: 'modal',
            type: 'wCommonComboTree',
            placement: 'bottom',
            showbuttons: false,
            onblur: 'submit',
            wCommonComboTree: {
              inlineView: true,
              service: 'appProductIntegrationMgr.getTreeNodeByUuid',
              serviceParams: [piUuid, [], ['BUTTON']],
              multiSelect: false
            }
          }
        },
        {
          field: 'target',
          title: '目标位置',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.targePosition.value2input,
            value2display: designCommons.bootstrapTable.targePosition.value2display,
            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
          }
        },
        {
          field: 'eventManger',
          title: '事件管理',
          editable: {
            mode: 'modal',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.eventManager.value2input,
            input2value: designCommons.bootstrapTable.eventManager.input2value,
            validate: designCommons.bootstrapTable.eventManager.validate,
            value2display: designCommons.bootstrapTable.eventManager.value2display
          }
        }
      ]
    });
  };
  configurer.prototype.initFieldInfo = function (configuration) {
    var fieldsInfo = configuration.fieldsInfo ? configuration.fieldsInfo : fieldsList;
    var html = '';
    console.log(fieldsInfo);
    for (var i = 0; i < fieldsInfo.length; i++) {
      html += "<div style='margin-bottom: 5px;' class='fields-group'>";
      if (fieldsInfo[i].isChecked) {
        html +=
          "<input type='checkbox' name='" +
          fieldsInfo[i].field +
          "' checked='" +
          fieldsInfo[i].isChecked +
          "' id='" +
          fieldsInfo[i].field +
          '_' +
          i +
          "'>";
      } else {
        html += "<input type='checkbox' name='" + fieldsInfo[i].field + "' id='" + fieldsInfo[i].field + '_' + i + "'>";
      }

      html +=
        "<label class='field-info-text' style='display: inline-block;width: 200px;margin-left: 20px;outline:none;cursor: pointer' for='" +
        fieldsInfo[i].field +
        '_' +
        i +
        "'>" +
        fieldsInfo[i].text +
        '</label>' +
        "<span style='display: inline-block;width: 200px;'>" +
        fieldsInfo[i].field +
        '</span>' +
        "<div class='fields-info-btns' style='display: inline-block;'>" +
        "<i class='glyphicon glyphicon-arrow-up btn-field-up' style='margin-right: 15px;'></i>" +
        "<i class='glyphicon glyphicon-arrow-down btn-field-down'></i>" +
        '</div>' +
        '</div>';
    }
    $('#widget_fields_list').append(html);

    // 上移常用意见
    $('.fields-info-btns', '#widget_fields_list').on('click', '.btn-field-up', function () {
      var $item = $(this).parents('.fields-group');
      $item.after($item.prev());
    });
    // 下移常用意见
    $('.fields-info-btns', '#widget_fields_list').on('click', '.btn-field-down', function () {
      var $item = $(this).parents('.fields-group');
      $item.before($item.next());
    });
    // 名称修改
    $('.fields-group', '#widget_fields_list').on('click', '.field-info-text', function () {
      $(this).attr('contenteditable', true).focus();
    });
    $('.fields-group', '#widget_fields_list').on('blur', '.field-info-text', function () {
      $(this).attr('contenteditable', false);
    });
  };
  configurer.prototype.getListViewTableColumnData = function () {
    var colDatas = [];
    var $columnInfoTable = $('#table_column_info');
    if ($columnInfoTable) {
      var columnTableData = $columnInfoTable.bootstrapTable('getData');
      var j = 1;
      for (var i = 0; i < columnTableData.length; i++) {
        if (columnTableData[i].hidden == 0) {
          colDatas.push({
            value: j + '',
            id: j + '',
            text: '[第' + j++ + '列] ' + columnTableData[i].header
          });
        }
      }
    }
    return colDatas;
  };
  configurer.prototype.onLoad = function ($container, options) {
    // 初始化页签项
    $('#widget_bootstrap_table_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
      var panelId = $(this).attr('href');
      $(panelId + ' .definition_info').bootstrapTable('resetView');
    });
    console.log('初始化数据');
    console.log(options.configuration);
    var configuration = $.extend(true, {}, options.configuration);

    this.initBaseInfo(configuration, $container);
    this.initColumnInfo(configuration.columns, $container);
    this.initFieldInfo(configuration);
    this.initRoleInfo(configuration, configuration.rolesField, $container);
    this.initButtonInfo(configuration, $container);
    this.initTreeGrid(configuration.treeGrid, $container);
  };
  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }
    var $columnInfoTable = $('#table_column_info', $container);
    var opt = designCommons.collectConfigurerData($('#widget_file_library_tabs_base_info', $container), collectClass);
    opt.columnDnd = Boolean(opt.columnDnd);
    opt.readMarker = Boolean(opt.readMarker);
    opt.hideTdBorder = Boolean(opt.hideTdBorder);
    opt.hideChecked = true;

    var requeryFields = ['name', 'dataStoreId'];
    if (opt.readMarker) {
      requeryFields.push('readMarkerField');
    }
    if (!checkRequire(requeryFields, opt, $container)) {
      return false;
    }

    if (StringUtils.isNotBlank(opt.width)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.width)) {
        appModal.error('宽度必须为正浮点数!');
        return false;
      }
    }
    if (StringUtils.isNotBlank(opt.height)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.height)) {
        appModal.error('高度必须为正浮点数!');
        return false;
      }
    }

    var columns = $columnInfoTable.bootstrapTable('getData');
    columns = $.map(columns, clearChecked);
    if (columns.length === 0) {
      appModal.error('请配置数据列！');
      return false;
    }

    // 弹窗字段
    var fieldsInfo = [];
    var fieldsRows = $('#widget_fields_list').find('.fields-group');
    $.each(fieldsRows, function (index, item) {
      fieldsInfo.push({
        text: $(item).find('.field-info-text').text(),
        field: $(item).find('input').attr('name'),
        isChecked: $(item).find('input').attr('checked') ? true : false
      });
    });

    var checkedFields = $('#widget_fields_list').find('input:checked').size();
    if (checkedFields == 0) {
      appModal.error('弹窗字段不能为空！');
      return false;
    }
    var $rolesFieldTable = $('#table_role_info', $container);
    var rolesField = $rolesFieldTable.bootstrapTable('getData');

    // 按钮定义
    var $tableButtonInfo = $('#table_button_info', $container);
    var buttons = $tableButtonInfo.bootstrapTable('getData');

    buttons = $.map(buttons, clearChecked);
    for (var i = 0; i < buttons.length; i++) {
      var button = buttons[i];
      if (StringUtils.isBlank(button.text)) {
        appModal.error('按钮的名称不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(button.code)) {
        appModal.error('按钮的编码不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(button.position)) {
        appModal.error('按钮的位置不允许为空！');
        return false;
      }
      //事件管理的各参数值提到上一层，兼容老代码
      if (!$.isEmptyObject(button.eventManger)) {
        for (var ek in button.eventManger) {
          button[ek] = button.eventManger[ek];
        }
      }
    }

    // 树节点配置
    requeryFields = [];
    var treeGrid = designCommons.collectConfigurerData($('#widget_file_library_tabs_tree_info', $container), collectClass);
    treeGrid.enableTreegrid = Boolean(treeGrid.enableTreegrid);
    treeGrid.allPageDataTreeFormate = Boolean(treeGrid.allPageDataTreeFormate);
    treeGrid.treeCollapseAll = true;
    treeGrid.treeView = true;
    treeGrid.treeCascadeCheck = Boolean(treeGrid.treeCascadeCheck);
    treeGrid.showFolderBtn = Boolean(treeGrid.showFolderBtn);
    treeGrid.lastLevelDataParentTree = Boolean(treeGrid.lastLevelDataParentTree);
    var $treeNodeTable = $('#table_tree_node_info', $container);
    var treeNodes = $treeNodeTable.bootstrapTable('getData');
    treeNodes = $.map(treeNodes, clearChecked);
    for (var i = 0; i < treeNodes.length; i++) {
      var node = treeNodes[i];
      if (StringUtils.isBlank(node.name)) {
        appModal.error('名称不允许为空！');
        return false;
      }
    }
    treeGrid.treeNodes = treeNodes;
    if (!checkRequire(requeryFields, treeGrid, $container)) {
      return false;
    }

    opt.columns = columns;
    opt.buttons = buttons;
    opt.treeGrid = treeGrid;
    opt.fieldsInfo = fieldsInfo;
    opt.rolesField = rolesField;
    opt.defineEventJs = {};

    this.component.options.configuration = $.extend({}, opt);
  };
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };
  component.prototype.create = function () {
    $(this.element).find('.widget-body').html(this.options.content);
  };
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };

  return component;
});
